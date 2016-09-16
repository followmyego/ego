package net.egobeta.ego;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.amazonaws.mobileconnectors.cognito.Dataset;

import net.amazonaws.mobile.AWSMobileClient;
import net.amazonaws.mobile.user.IdentityManager;

/**
 * Simple class for user settings.
 */
public class UserPinned {
    private static final String LOG_TAG = UserPinned.class.getSimpleName();

    // dataset name to store user permissions
    private static final String USER_PINNED_DATASET_NAME = "user_pinned";

    // Intent action used in local broadcast
    public static final String ACTION_PINNED_CHANGED = "user-pinned-changed";

    // key names in dataset
    private static final String KEY_PINNED_USER_1 = "pinned_user_1";
    private static final String KEY_PINNED_USER_2 = "pinned_user_2";
    private static final String KEY_PINNED_USER_3 = "pinned_user_3";
    private static final String KEY_PINNED_USER_4 = "pinned_user_4";
    private static final String KEY_PINNED_USER_5 = "pinned_user_5";

    private static UserPinned instance;

    // default values
    private static String DEFAULT_VALUE = "empty"; // white

    //Class Variables
    private String pinnedUser1 = DEFAULT_VALUE;
    private String pinnedUser2 = DEFAULT_VALUE;
    private String pinnedUser3 = DEFAULT_VALUE;
    private String pinnedUser4 = DEFAULT_VALUE;
    private String pinnedUser5 = DEFAULT_VALUE;




    String[] keys = {
            KEY_PINNED_USER_1,
            KEY_PINNED_USER_2,
            KEY_PINNED_USER_3,
            KEY_PINNED_USER_4,
            KEY_PINNED_USER_5

    };


    public String getPinnedUser1() {
        return pinnedUser1;
    }

    public void setPinnedUser1(String pinnedUser1) {
        this.pinnedUser1 = pinnedUser1;
    }

    public String getPinnedUser2() {
        return pinnedUser2;
    }

    public void setPinnedUser2(String pinnedUser2) {
        this.pinnedUser2 = pinnedUser2;
    }

    public String getPinnedUser3() {
        return pinnedUser3;
    }

    public void setPinnedUser3(String pinnedUser3) {
        this.pinnedUser3 = pinnedUser3;
    }

    public String getPinnedUser4() {
        return pinnedUser4;
    }

    public void setPinnedUser4(String pinnedUser4) {
        this.pinnedUser4 = pinnedUser4;
    }

    public String getPinnedUser5() {
        return pinnedUser5;
    }

    public void setPinnedUser5(String pinnedUser5) {
        this.pinnedUser5 = pinnedUser5;
    }

    public static UserPinned getInstance() {
        return instance;
    }

    public static void setInstance(UserPinned instance) {
        UserPinned.instance = instance;
    }

    /**
     * Loads user settings from local dataset into memory.
     */
    public void loadFromDataset() {
        Dataset dataset = getDataset();

        final String pinnedUser1_loaded = dataset.get(KEY_PINNED_USER_1);
        if (pinnedUser1_loaded != null) {
            pinnedUser1 = pinnedUser1_loaded;
        }

        final String pinnedUser2_loaded = dataset.get(KEY_PINNED_USER_2);
        if (pinnedUser2_loaded != null) {
            pinnedUser2 = pinnedUser2_loaded;
        }

        final String pinnedUser3_loaded = dataset.get(KEY_PINNED_USER_3);
        if (pinnedUser3_loaded != null) {
            pinnedUser3 = pinnedUser3_loaded;
        }

        final String pinnedUser4_loaded = dataset.get(KEY_PINNED_USER_4);
        if (pinnedUser4_loaded != null) {
            pinnedUser4 = pinnedUser4_loaded;
        }

        final String pinnedUser5_loaded = dataset.get(KEY_PINNED_USER_5);
        if (pinnedUser5_loaded != null) {
            pinnedUser5 = pinnedUser5_loaded;
        }

    }

    /**
     * Saves in memory user settings to local dataset.
     */
    public void saveToDataset() {
        String[] variables = {
                pinnedUser1,
                pinnedUser2,
                pinnedUser3,
                pinnedUser4,
                pinnedUser5
        };
        Dataset dataset = getDataset();

        //Save item booleans
        for(int i = 0; i < variables.length; i++){
            dataset.put(keys[i], variables[i]);
            Log.d(LOG_TAG, "onSuccess - dataset updated: " + keys[i] + " " + variables[i]);
        }
    }

    /**
     * Gets the Cognito dataset that stores user settings.
     *
     * @return Cognito dataset
     */
    public Dataset getDataset() {
        return AWSMobileClient.defaultMobileClient()
                .getSyncManager()
                .openOrCreateDataset(USER_PINNED_DATASET_NAME);
    }

    /**
     * Gets a singleton of user settings
     *
     * @return user settings
     */
    public static UserPinned getInstance(final Context context) {
        if (instance != null) {
            return instance;
        }
        instance = new UserPinned();
        final IdentityManager identityManager = AWSMobileClient.defaultMobileClient()
                .getIdentityManager();
        identityManager.addSignInStateChangeListener(
                new IdentityManager.SignInStateChangeListener() {
                    @Override
                    public void onUserSignedIn() {
                        Log.d(LOG_TAG, "load from dataset on user sign in");
                        instance.loadFromDataset();
                    }

                    @Override
                    public void onUserSignedOut() {
                        Log.d(LOG_TAG, "wipe user data after sign out");
                        AWSMobileClient.defaultMobileClient().getSyncManager().wipeData();

                        //Set the default newUser variable for this instance if user signs out
                        instance.setPinnedUser1(DEFAULT_VALUE);
                        instance.setPinnedUser2(DEFAULT_VALUE);
                        instance.setPinnedUser3(DEFAULT_VALUE);
                        instance.setPinnedUser4(DEFAULT_VALUE);
                        instance.setPinnedUser5(DEFAULT_VALUE);


                        instance.saveToDataset();
                        final Intent intent = new Intent(ACTION_PINNED_CHANGED);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
        return instance;
    }
}
