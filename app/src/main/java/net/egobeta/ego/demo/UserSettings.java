package net.egobeta.ego.demo;

import android.content.Context;
import android.content.Intent;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import net.amazonaws.mobile.AWSMobileClient;
import net.amazonaws.mobile.user.IdentityManager;
import com.amazonaws.mobileconnectors.cognito.Dataset;

/**
 * Simple class for user settings.
 */
public class UserSettings {
    private static final String LOG_TAG = UserSettings.class.getSimpleName();

    // dataset name to store user settings
    private static final String USER_SETTINGS_DATASET_NAME = "user_settings";

    // Intent action used in local broadcast
    public static final String ACTION_SETTINGS_CHANGED = "user-settings-changed";

    // key names in dataset
    private static final String USER_SETTINGS_KEY_TITLE_TEXT_COLOR = "title_text_color";
    private static final String USER_SETTINGS_KEY_TITLE_BAR_COLOR = "title_bar_color";
    private static final String USER_SETTINGS_KEY_BACKGROUND_COLOR = "background_color";
    private static final String USER_SETTINGS_KEY_NEW_USER = "new_user";
    private static final String USER_SETTINGS_KEY_USER_PERMISSIONS = "user_permissions";

    // default color
    private static int DEFAULT_TITLE_TEXT_COLOR = 0xFFFFFFFF; // white
    private static int DEFAULT_TITLE_BAR_COLOR = 0xFFF58535; // orange
    private static int DEFAULT_TITLE_BACKGROUND_COLOR = 0xFFFFFFFF; // white
    private static int DEFAULT_NEW_USER = 0; // white
    private static int DEFAULT_PERMISSION = 0; // white


    private int titleTextColor = DEFAULT_TITLE_TEXT_COLOR;
    private int titleBarColor = DEFAULT_TITLE_BAR_COLOR;
    private int backgroudColor = DEFAULT_TITLE_BACKGROUND_COLOR;
    private int newUser = DEFAULT_NEW_USER;
    private int[] user_permissions = {DEFAULT_PERMISSION, DEFAULT_PERMISSION, DEFAULT_PERMISSION};

    private static UserSettings instance;


    /**Sets if the user is a new user.*/
    public void setUserPermissions(final int[] user_permissions) {
        this.user_permissions = user_permissions;
    }

    /**Gets if the user is a new user or not*/
    public int[] getUserPermissions() {
        return user_permissions;
    }


    /**Sets if the user is a new user.*/
    public void setNewUser(final int newUser) {
        this.newUser = newUser;
    }

    /**Gets if the user is a new user or not*/
    public int getNewUser() {
        return newUser;
    }

    /**NOT CURRENTLY USING THIS**/
    public void setTitleTextColor(final int titleTextColor) {
        this.titleTextColor = titleTextColor;
    }

    /**NOT CURRENTLY USING THIS**/
    public void setTitleBarColor(int color) {
        this.titleBarColor = color;
    }

    /**NOT CURRENTLY USING THIS**/
    public void setBackgroundColor(int color) {
        this.backgroudColor = color;
    }

    /**NOT CURRENTLY USING THIS**/
    public int getTitleTextColor() {
        return titleTextColor;
    }

    /**NOT CURRENTLY USING THIS**/
    public int getTitleBarColor() {
        return titleBarColor;
    }

    /**NOT CURRENTLY USING THIS**/
    public int getBackgroudColor() {
        return backgroudColor;
    }


    /**
     * Loads user settings from local dataset into memory.
     */
    public void loadFromDataset() {
        Dataset dataset = getDataset();
        final String dataTextColor = dataset.get(USER_SETTINGS_KEY_TITLE_TEXT_COLOR);
        if (dataTextColor != null) {
            titleTextColor = Integer.valueOf(dataTextColor);
        }
        final String dataTitleBarColor = dataset.get(USER_SETTINGS_KEY_TITLE_BAR_COLOR);
        if (dataTitleBarColor != null) {
            titleBarColor = Integer.valueOf(dataTitleBarColor);
        }
        final String dataBackgroundColor = dataset.get(USER_SETTINGS_KEY_BACKGROUND_COLOR);
        if (dataBackgroundColor != null) {
            backgroudColor = Integer.valueOf(dataBackgroundColor);
        }








        //Load New User Boolean
        final String dataNewUser = dataset.get(USER_SETTINGS_KEY_NEW_USER);
        if (dataNewUser != null) {
            newUser = Integer.valueOf(dataNewUser);
        }
    }

    /**
     * Saves in memory user settings to local dataset.
     */
    public void saveToDataset() {
        Dataset dataset = getDataset();
        dataset.put(USER_SETTINGS_KEY_TITLE_TEXT_COLOR, String.valueOf(titleTextColor));
        dataset.put(USER_SETTINGS_KEY_TITLE_BAR_COLOR, String.valueOf(titleBarColor));
        dataset.put(USER_SETTINGS_KEY_BACKGROUND_COLOR, String.valueOf(backgroudColor));



        //Save newUser boolean
        dataset.put(USER_SETTINGS_KEY_NEW_USER, String.valueOf(newUser));
    }

    /**
     * Gets the Cognito dataset that stores user settings.
     *
     * @return Cognito dataset
     */
    public Dataset getDataset() {
        return AWSMobileClient.defaultMobileClient()
                .getSyncManager()
                .openOrCreateDataset(USER_SETTINGS_DATASET_NAME);
    }

    /**
     * Gets a singleton of user settings
     *
     * @return user settings
     */
    public static UserSettings getInstance(final Context context) {
        if (instance != null) {
            return instance;
        }
        instance = new UserSettings();
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
                        instance.setTitleTextColor(DEFAULT_TITLE_TEXT_COLOR);
                        instance.setTitleBarColor(DEFAULT_TITLE_BAR_COLOR);
                        instance.setBackgroundColor(DEFAULT_TITLE_BACKGROUND_COLOR);

                        //Set the default newUser variable for this instance if user signs out
                        instance.setNewUser(DEFAULT_NEW_USER);

                        instance.saveToDataset();
                        final Intent intent = new Intent(ACTION_SETTINGS_CHANGED);
                        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
                    }
                });
        return instance;
    }
}
