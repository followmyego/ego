package net.amazonaws.mobile.user;
//
// Copyright 2016 Amazon.com, Inc. or its affiliates (Amazon). All Rights Reserved.
//
// Code generated by AWS Mobile Hub. Amazon gives unlimited permission to 
// copy, distribute and modify it.
//
// Source code generated from template: aws-my-sample-app-android v0.7
//
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.Log;

import com.amazonaws.ClientConfiguration;
import com.amazonaws.SDKGlobalConfiguration;
import com.amazonaws.auth.AWSBasicCognitoIdentityProvider;
import com.amazonaws.auth.CognitoCachingCredentialsProvider;
import net.amazonaws.mobile.AWSConfiguration;
import net.amazonaws.mobile.util.ThreadUtils;
import com.amazonaws.regions.Region;
import com.amazonaws.regions.Regions;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Date;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

/**
 * The identity manager keeps track of the current sign-in provider and is responsible
 * for caching credentials.
 */
public class IdentityManager {

    /**
     * Allows the application to get asynchronous response with user's
     * unique identifier.
     */
    public interface IdentityHandler {
        /**
         * Handles the user's unique identifier.
         * @param identityId Amazon Cognito Identity ID which uniquely identifies
         *                   the user.
         */
        public void handleIdentityID(final String identityId);

        /**
         * Handles any error that might have occurred while getting the user's
         * unique identifier from Amazon Cognito.
         * @param exception exception
         */
        public void handleError(final Exception exception);
    }

    /** Log tag. */
    private static final String LOG_TAG = IdentityManager.class.getSimpleName();

    /** Cognito caching credentials provider. */
    private CognitoCachingCredentialsProvider credentialsProvider;

    /** Executor service for obtaining credentials in a background thread. */
    private final ExecutorService executorService = Executors.newFixedThreadPool(2);

    /** Current provider being used to obtain a Cognito access token. */
    private IdentityProvider currentIdentityProvider = null;

    /** Results adapter for adapting results that came from logging in with a provider. */
    private SignInResultsAdapter resultsAdapter;

    /** Keep tract of the currently registered SignInStateChangeListiners. */
    private final HashSet<SignInStateChangeListener> signInStateChangeListeners = new HashSet<>();


    /**
     * Custom Cognito Identity Provider to handle refreshing the individual provider's tokens.
     */
    public class AWSRefreshingCognitoIdentityProvider extends AWSBasicCognitoIdentityProvider {

        /** Log tag. */
        private final String LOG_TAG = AWSRefreshingCognitoIdentityProvider.class.getSimpleName();

        public AWSRefreshingCognitoIdentityProvider(final String accountId,
                                                    final String identityPoolId,
                                                    final ClientConfiguration clientConfiguration,
                                                    final Regions regions) {
            super(accountId, identityPoolId, clientConfiguration);
            // Force refreshing ID provider to use same region as caching credentials provider
            this.cib.setRegion(Region.getRegion(regions));
        }

        @Override
        public String refresh() {

            Log.d(LOG_TAG, "Refreshing token...");

            if (currentIdentityProvider != null) {
                final String newToken = currentIdentityProvider.refreshToken();

                getLogins().put(currentIdentityProvider.getCognitoLoginKey(), newToken);
            }
            return super.refresh();
        }
    }

    /**
     * Constructor. Initializes the cognito credentials provider.
     * @param appContext the application context.
     * @param clientConfiguration the client configuration options such as retries and timeouts.
     */
    public IdentityManager(final Context appContext, final ClientConfiguration clientConfiguration) {
        Log.d(LOG_TAG, "IdentityManager init");
        initializeCognito(appContext, clientConfiguration);
    }

    private void initializeCognito(final Context context, final ClientConfiguration clientConfiguration) {
        AWSRefreshingCognitoIdentityProvider cognitoIdentityProvider = new AWSRefreshingCognitoIdentityProvider(
            null, AWSConfiguration.AMAZON_COGNITO_IDENTITY_POOL_ID, clientConfiguration, AWSConfiguration.AMAZON_COGNITO_REGION);

        credentialsProvider =
            new CognitoCachingCredentialsProvider(context,
                cognitoIdentityProvider,
                AWSConfiguration.AMAZON_COGNITO_REGION,
                clientConfiguration
            );

    }

    /**
     * @return true if the cached Cognito credentials are expired, otherwise false.
     */
    public boolean areCredentialsExpired() {

        final Date credentialsExpirationDate =
            credentialsProvider.getSessionCredentitalsExpiration();

        if (credentialsExpirationDate == null) {
            Log.d(LOG_TAG, "Credentials are EXPIRED.");
            return true;
        }

        long currentTime = System.currentTimeMillis() -
            (long)(SDKGlobalConfiguration.getGlobalTimeOffset() * 1000);

        final boolean credsAreExpired =
            (credentialsExpirationDate.getTime() - currentTime) < 0;

        Log.d(LOG_TAG, "Credentials are " + (credsAreExpired ? "EXPIRED." : "OK"));

        return credsAreExpired;
    }

    /**
     * @return the Cognito credentials provider.
     */
    public CognitoCachingCredentialsProvider getCredentialsProvider() {
        return this.credentialsProvider;
    }

    /**
     * Gets the cached unique identifier for the user.
     * @return the cached unique identifier for the user.
     */
    public String getCachedUserID() {
        return getCredentialsProvider().getCachedIdentityId();
    }

    /**
     * Gets the user's unique identifier. This method can be called from
     * any thread.
     * @param handler handles the unique identifier for the user
     */
    public void getUserID(final IdentityHandler handler) {

        new Thread(new Runnable() {
            Exception exception = null;

            @Override
            public void run() {
                String identityId = null;

                try {
                    // Retrieve the user identity on the background thread.
                    identityId = getCredentialsProvider().getIdentityId();
                } catch (final Exception exception) {
                    this.exception = exception;
                    Log.e(LOG_TAG, exception.getMessage(), exception);
                } finally {

                    final String result = identityId;

                    ThreadUtils.runOnUiThread(new Runnable() {
                        @Override
                        public void run() {

                            if (exception != null) {
                                handler.handleError(exception);
                                return;
                            }

                            handler.handleIdentityID(result);
                        }
                    });
                }
            }
        }).start();
    }

    /**
     *  Implement this interface to get callbacks for the results to a sign-in operation.
     */
    public interface SignInResultsHandler {

        /**
         * Sign-in was successful.
         * @param provider sign-in identity provider
         */
        void onSuccess(IdentityProvider provider);

        /**
         * Sign-in was cancelled by the user.
         * @param provider sign-in identity provider
         */
        void onCancel(IdentityProvider provider);

        /**
         * Sign-in failed.
         * @param provider sign-in identity provider
         * @param ex exception that occurred
         */
        void onError(IdentityProvider provider, Exception ex);
    }


    /**
     * Implement this interface to receive callbacks when the user's sign-in state changes
     * from signed-in to not signed-in or vice versa.
     */
    public interface SignInStateChangeListener {

        /**
         * Invoked when the user completes sign-in.
         */
        void onUserSignedIn();

        /**
         * Invoked when the user signs out.
         */
        void onUserSignedOut();
    }

    /**
     * The adapter to handle results that come back from Cognito as well as handle the result from
     * any login providers.
     */
    private class SignInResultsAdapter implements SignInResultsHandler {
        final private SignInResultsHandler handler;

        public SignInResultsAdapter(final SignInResultsHandler handler) {
            this.handler = handler;
        }

        public void onSuccess(final IdentityProvider provider) {
            Log.d(LOG_TAG,
                String.format("SignInResultsAdapter.onSuccess(): %s provider sign-in succeeded.",
                    provider.getDisplayName()));
            // Update cognito login with the token.
            loginWithProvider(provider);
        }

        private void onCognitoSuccess() {
            Log.d(LOG_TAG, "SignInResultsAdapter.onCognitoSuccess()");
            handler.onSuccess(currentIdentityProvider);
        }

        private void onCognitoError(final Exception ex) {
            Log.d(LOG_TAG, "SignInResultsAdapter.onCognitoError()", ex);
            final IdentityProvider provider = currentIdentityProvider;
            // Sign out of parent provider. This clears the currentIdentityProvider.
            IdentityManager.this.signOut();
            handler.onError(provider, ex);

        }

        public void onCancel(final IdentityProvider provider) {
            Log.d(LOG_TAG,
                String.format("SignInResultsAdapter.onCancel(): %s provider sign-in canceled.",
                    provider.getDisplayName()));
            handler.onCancel(provider);
        }

        public void onError(final IdentityProvider provider, final Exception ex) {
            Log.e(LOG_TAG,
                String.format("SignInResultsAdapter.onError(): %s provider error. %s",
                    provider.getDisplayName(), ex.getMessage()), ex);
            handler.onError(provider, ex);
        }
    }

    /**
     * Add a listener to receive callbacks when sign-in or sign-out occur.  The listener
     * methods will always be called on a background thread.
     * @param listener the sign-in state change listener.
     */
    public void addSignInStateChangeListener(final SignInStateChangeListener listener) {
        synchronized (signInStateChangeListeners) {
            signInStateChangeListeners.add(listener);
        }
    }

    /**
     * Remove a listener from receiving callbacks when sign-in or sign-out occur.
     * @param listener the sign-in state change listener.
     */
    public void removeSignInStateChangeListener(final SignInStateChangeListener listener) {
        synchronized (signInStateChangeListeners) {
            signInStateChangeListeners.remove(listener);
        }
    }

    /**
     * Set the results handler that will be used for results when calling loginWithProvider.
     * @param signInResultsHandler the results handler.
     */
    public void setResultsHandler(final SignInResultsHandler signInResultsHandler) {
        if (signInResultsHandler == null) {
            throw new IllegalArgumentException("signInResultsHandler cannot be null.");
        }
        this.resultsAdapter = new SignInResultsAdapter(signInResultsHandler);
    }


    /**
     * Call getResultsAdapter to get the IdentityManager's handler that adapts results before
     * sending them back to the handler set by {@link #setResultsHandler(SignInResultsHandler)}
     * @return the Identity Manager's results adapter.
     */
    public SignInResultsAdapter getResultsAdapter() {
        return resultsAdapter;
    }

    /**
     * @return true if Cognito credentials have been obtained with at least one provider.
     */
    public boolean isUserSignedIn() {
        final Map<String, String> logins = credentialsProvider.getLogins();
        if (logins == null || logins.size() == 0)
            return false;
        return true;
    }

    /**
     * Sign out of the currently in use credentials provider and clear Cognito credentials.
     */
    public void signOut() {
        Log.d(LOG_TAG, "Signing out...");

        if (currentIdentityProvider != null) {
            currentIdentityProvider.signOut();
            credentialsProvider.clear();
            currentIdentityProvider = null;
            // Notify state change listeners of sign out.
            synchronized (signInStateChangeListeners) {
                for (final SignInStateChangeListener listener : signInStateChangeListeners) {
                    listener.onUserSignedOut();
                }
            }
        }
    }

    private void refreshCredentialWithLogins(final Map<String, String> loginMap) {
        credentialsProvider.clear();
        credentialsProvider.withLogins(loginMap);
        // Calling refresh is equivalent to calling getIdentityId() + getCredentials().
        Log.d(getClass().getSimpleName(), "refresh credentials");
        credentialsProvider.refresh();
        Log.d(getClass().getSimpleName(), "Cognito ID: " + credentialsProvider.getIdentityId());
        Log.d(getClass().getSimpleName(), "Cognito Credentials: " + credentialsProvider.getCredentials());
    }

    /**
     * Login with an identity provider (ie. Facebook, Twitter, etc.).
     * @param provider A sign-in provider.
     */
    public void loginWithProvider(final IdentityProvider provider) {
        Log.d(LOG_TAG, "loginWithProvider");
        final Map<String, String> loginMap = new HashMap<String, String>();
        loginMap.put(provider.getCognitoLoginKey(), provider.getToken());
        currentIdentityProvider = provider;

        executorService.submit(new Runnable() {
            @Override
            public void run() {
                try {
                    refreshCredentialWithLogins(loginMap);
                } catch (Exception ex) {
                    resultsAdapter.onCognitoError(ex);
                    return;
                }

                resultsAdapter.onCognitoSuccess();

                // Notify state change listeners of sign out.
                synchronized (signInStateChangeListeners) {
                    for (final SignInStateChangeListener listener : signInStateChangeListeners) {
                        listener.onUserSignedIn();
                    }
                }
            }
        });
    }

    /**
     * Gets the current provider.
     * @return current provider or null if not signed-in
     */
    public IdentityProvider getCurrentIdentityProvider() {
        return currentIdentityProvider;
    }

    // local cache of the user image of currentIdentityProvider.getUserImageUrl();
    private Bitmap userImage = null;

    private void loadUserImage(final String userImageUrl) {
        if (userImageUrl == null) {
            userImage = null;
            return;
        }

        try {
            final InputStream is = new URL(userImageUrl).openStream();
            userImage = BitmapFactory.decodeStream(is);
            is.close();
        } catch (IOException e) {
            Log.w(LOG_TAG, "Failed to prefetch user image: " + userImageUrl, e);
            // clear user image
            userImage = null;
        }
    }

    /**
     * Reload the user info and image in the background.
     *
     * @param provider sign-in provider
     * @param onReloadComplete Runnable to be executed on the main thread after user info
     *                         and user image is reloaded.
     */
    public void loadUserInfoAndImage(final IdentityProvider provider, final Runnable onReloadComplete) {
        executorService.submit(new Runnable() {
            @Override
            public void run() {
                provider.reloadUserInfo();
                // preload user image
                loadUserImage(provider.getUserImageUrl());
                ThreadUtils.runOnUiThread(onReloadComplete);
            }
        });
    }

    /**
     * Convenient method to get the user image of the current identity provider.
     * @return user image of the current identity provider, or null if not signed in or unavailable
     */
    public Bitmap getUserImage() {
        return userImage;
    }

    /**
     * Convenient method to get the user id from the current identity provider.
     * @return user name from the current identity provider, or null if not signed in
     */
    public String getUserFacebookId() {
        return currentIdentityProvider == null ? null : currentIdentityProvider.getUserFacebookId();
    }

    /**
     * Convenient method to get the user name from the current identity provider.
     * @return user name from the current identity provider, or null if not signed in
     */
    public String getUserName() {
        return currentIdentityProvider == null ? null : currentIdentityProvider.getUserName();
    }
}
