package net.egobeta.ego.OnBoarding;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.Toast;

import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.DefaultSyncCallback;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import net.amazonaws.mobile.AWSMobileClient;
import net.egobeta.ego.Fragments.OnBoarding_Fragment4;
import net.egobeta.ego.MainActivity;
import net.egobeta.ego.R;
import net.egobeta.ego.UserPermissions;
import net.egobeta.ego.demo.UserSettings;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoadFacebookPermissions extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> privacyPreferences;
    private final static String LOG_TAG = "LFP";
    Context context;
    Activity activtiy;

    //View items
    ImageButton finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_load_facebook_permissions);
        context = getApplicationContext();
        activtiy = LoadFacebookPermissions.this;
        Intent intent = getIntent();
        privacyPreferences = intent.getStringArrayListExtra("privacy_preferences");

        /** Initialize the view items **/
        initializeViewItems();

        /** Sync the user privacy settings **/
        syncPrivacySettings();

        String final_badge_selection = "";
        for (String item : privacyPreferences) {
            final_badge_selection = final_badge_selection + item + "\n";
        }
//        Toast.makeText(LoadFacebookPermissions.this, final_badge_selection, Toast.LENGTH_LONG).show();

    }

    private void syncPrivacySettings() {
        final UserPermissions userPermissions = UserPermissions.getInstance(context);

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_FRIENDS)){
            userPermissions.setFriends(1);
        } else {
            Toast.makeText(LoadFacebookPermissions.this, "Does not contain " + OnBoarding_Fragment4.KEY_PERMISSION_FRIENDS, Toast.LENGTH_SHORT).show();
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_FRIENDS_OF_FRIENDS)){
            userPermissions.setFriendsOfFriends(1);
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_INSTAGRAM_FOLLOWERS)){
            userPermissions.setInstagramFollowers(1);
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_INSTAGRAM_FOLLOWING)){
            userPermissions.setInstagramFollowing(1);
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_LOCATION)){
            userPermissions.setLocation(1);
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_HOMETOWN)){
            userPermissions.setHometown(1);
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_LIKES)){
            userPermissions.setCommonLikes(1);
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_BIRTHDAY)){
            userPermissions.setBirthday(1);
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_WORK)){
            userPermissions.setWorkplace(1);
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_SCHOOL)){
            userPermissions.setSchool(1);
        }


        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_MUSIC)){
            userPermissions.setMusic(1);
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_MOVIES)){
            userPermissions.setMovies(1);
        }


        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_BOOKS)){
            userPermissions.setBooks(1);
        }


        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                userPermissions.saveToDataset();
                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {

                // update color
//                ((MainActivity) getActivity()).updateColor();

                // save user settings to remote on background thread
                userPermissions.getDataset().synchronize(new Dataset.SyncCallback() {
                    @Override
                    public void onSuccess(Dataset dataset, List<Record> updatedRecords) {
                        Log.d(LOG_TAG, "onSuccess - dataset updated");

                    }

                    @Override
                    public boolean onConflict(Dataset dataset, List<SyncConflict> conflicts) {
                        Log.d(LOG_TAG, "onConflict - dataset conflict");
                        return false;
                    }

                    @Override
                    public boolean onDatasetDeleted(Dataset dataset, String datasetName) {
                        Log.d(LOG_TAG, "onDatasetDeleted - dataset deleted");
                        return false;
                    }

                    @Override
                    public boolean onDatasetsMerged(Dataset dataset, List<String> datasetNames) {
                        Log.d(LOG_TAG, "onDatasetsMerged - datasets merged");
                        return false;
                    }

                    @Override
                    public void onFailure(DataStorageException dse) {
                        Log.e(LOG_TAG, "onFailure - " + dse.getMessage(), dse);
                    }
                });
            }
        }.execute();
    }


    private void getFacebookInfo() {

        //This is to start the animation for getting the Facebook info
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(LoadFacebookPermissions.this, "Getting facebook info", Toast.LENGTH_SHORT).show();
            }
        });

        Thread thread = new Thread() {
            @Override
            public void run() {
                try {
                    if(true) {
                        sleep(1000);
                        final Bundle parameters = new Bundle();
                        parameters.putString("fields", "name,picture.type(large), age_range, birthday, context, " +
                            "education, email, favorite_athletes, favorite_teams, hometown, inspirational_people, is_verified, " +
                            "languages, locale, location, work, movies, music, books, friends");
                        final GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "me");
                        graphRequest.setParameters(parameters);
                        GraphResponse response = graphRequest.executeAndWait();
                        String facebook_id;
                        JSONObject json = response.getJSONObject();
                        try {
                            facebook_id = json.getString("id");
//                            userName = json.getString("name");
//                            userImageUrl = json.getJSONObject("picture")
//                                .getJSONObject("data")
//                                .getString("url");

                            int maxLogSize = 1000;
                            for(int i = 0; i <= json.toString().length() / maxLogSize; i++) {
                                int start = i * maxLogSize;
                                int end = (i+1) * maxLogSize;
                                end = end > json.toString().length() ? json.toString().length() : end;
                                Log.v("FACEBOOK RESULT2:  ", json.toString().substring(start, end));
                            }

                        } catch (final JSONException jsonException) {
                            Log.e("LOGTAG",
                                    "Unable to get Facebook user info. " + jsonException.getMessage() + "\n" + response,
                                    jsonException);
                            // Nothing much we can do here.
                        }
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }


    private void initializeViewItems() {
        finishButton = (ImageButton) findViewById(R.id.fpFinishButton);
        finishButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v == finishButton){
            Intent intent = new Intent(LoadFacebookPermissions.this, MainActivity.class);
//            startActivity(intent);
//            overridePendingTransition(R.anim.activity_close_scale_r, R.anim.activity_open_translate_r);
            getFacebookInfo();

        }
    }














    /**Created from the AWS demo app**/
    /** Sync user's preferences only if user is signed in **/
    private void syncUserSettings() {
        System.out.println("MAINACTIVITY: syncUserSettings");
        // sync only if user is signed in
        if (AWSMobileClient.defaultMobileClient().getIdentityManager().isUserSignedIn()) {
            final UserPermissions userPermissions = UserPermissions.getInstance(getApplicationContext());
            userPermissions.getDataset().synchronize(new DefaultSyncCallback() {
                @Override
                public void onSuccess(final Dataset dataset, final List<Record> updatedRecords) {
                    super.onSuccess(dataset, updatedRecords);
                    Log.d(LOG_TAG, "successfully synced user settings");

                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            loadUserSettings();

                        }
                    });
                }
            });
        }
    }




    private void loadUserSettings() {
        System.out.println("MAINACTIVITY: loadUserSettings");
        final UserPermissions userPermissions = UserPermissions.getInstance(context);
        final Dataset dataset = userPermissions.getDataset();
        final ProgressDialog dialog = ProgressDialog.show(this,
                getString(R.string.settings_fragment_dialog_title),
                getString(R.string.settings_fragment_dialog_message));
        Log.d(LOG_TAG, "Loading user settings from remote");
        dataset.synchronize(new DefaultSyncCallback() {
            @Override
            public void onSuccess(final Dataset dataset, final List<Record> updatedRecords) {
                super.onSuccess(dataset, updatedRecords);
                userPermissions.loadFromDataset();
                if (userPermissions.getNewUser() == 0) {
                    updateUI(dialog, 0);
                } else {
                    updateUI(dialog, 1);
                }
                ;
            }

            @Override
            public void onFailure(final DataStorageException dse) {
                Log.w(LOG_TAG, "Failed to load user settings from remote, using default.", dse);
                updateUI(dialog, 3);
            }

            @Override
            public boolean onDatasetsMerged(final Dataset dataset,
                                            final List<String> datasetNames) {
                // Handle dataset merge. One can selectively copy records from merged datasets
                // if needed. Here, simply discard merged datasets
                for (String name : datasetNames) {
                    Log.d(LOG_TAG, "found merged datasets: " + name);
                    AWSMobileClient.defaultMobileClient().getSyncManager().openOrCreateDataset(name).delete();
                }
                return true;
            }
        });
    }

    private void updateUI(final ProgressDialog dialog, final int isFirstTimeUSer) {
        System.out.println("MAINACTIVITY: updateUI");
        activtiy.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                if (dialog != null) {
                    dialog.dismiss();
                }
                if (isFirstTimeUSer == 1) {
                    Toast.makeText(activtiy, "not FirstTimeUser", Toast.LENGTH_SHORT).show();
                    setFirstTimeUser(isFirstTimeUSer);
                } else if (isFirstTimeUSer == 0) {
                    Toast.makeText(activtiy, "FirstTimeUser", Toast.LENGTH_SHORT).show();
                    setFirstTimeUser(isFirstTimeUSer);
//                    Intent intent = new Intent(MainActivity.this, Main_OnBoarding.class);
//                    startActivity(intent);
//                    MainActivity.this.finish();
                } else {
                    Toast.makeText(activtiy, "Failure updating", Toast.LENGTH_SHORT).show();
                    setFirstTimeUser(0);
                }

            }
        });
    }

    private void setFirstTimeUser(int firstTime) {
        System.out.println("MAINACTIVITY: setFirstTimeUser");
        final UserPermissions userPermissions = UserPermissions.getInstance(context);
        userPermissions.setNewUser(firstTime);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                userPermissions.saveToDataset();
                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {

                // update color
//                ((MainActivity) getActivity()).updateColor();

                // save user settings to remote on background thread
                userPermissions.getDataset().synchronize(new Dataset.SyncCallback() {
                    @Override
                    public void onSuccess(Dataset dataset, List<Record> updatedRecords) {
                        Log.d(LOG_TAG, "onSuccess - dataset updated");

                    }

                    @Override
                    public boolean onConflict(Dataset dataset, List<SyncConflict> conflicts) {
                        Log.d(LOG_TAG, "onConflict - dataset conflict");
                        return false;
                    }

                    @Override
                    public boolean onDatasetDeleted(Dataset dataset, String datasetName) {
                        Log.d(LOG_TAG, "onDatasetDeleted - dataset deleted");
                        return false;
                    }

                    @Override
                    public boolean onDatasetsMerged(Dataset dataset, List<String> datasetNames) {
                        Log.d(LOG_TAG, "onDatasetsMerged - datasets merged");
                        return false;
                    }

                    @Override
                    public void onFailure(DataStorageException dse) {
                        Log.e(LOG_TAG, "onFailure - " + dse.getMessage(), dse);
                    }
                });
            }
        }.execute();
    }
}
