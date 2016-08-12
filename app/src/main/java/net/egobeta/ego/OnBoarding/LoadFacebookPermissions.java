package net.egobeta.ego.OnBoarding;

import android.animation.Animator;
import android.animation.ObjectAnimator;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.cognito.Dataset;
import com.amazonaws.mobileconnectors.cognito.Record;
import com.amazonaws.mobileconnectors.cognito.SyncConflict;
import com.amazonaws.mobileconnectors.cognito.exceptions.DataStorageException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import net.amazonaws.mobile.AWSMobileClient;
import net.egobeta.ego.Fragments.OnBoarding_Fragment4;
import net.egobeta.ego.MainActivity;
import net.egobeta.ego.R;
import net.egobeta.ego.Table_Classes.User_Badges;
import net.egobeta.ego.Table_Classes.User_Profile;
import net.egobeta.ego.UserPermissions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class LoadFacebookPermissions extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> privacyPreferences;
    private final static String LOG_TAG = "LFP";
    Context context;
    Activity activity;
    Typeface typeface;
    User_Badges userBadges;
    User_Profile userProfile;

    private String facebookId;
    static DynamoDBMapper mapper = null;
    GraphResponse response;

    //View items
    ImageButton finishButton;
    ImageView loadingItem1;
    ImageView loadingItem2;
    ImageView loadingItem3;
    TextView textView;
    ProgressBar spinner;


    //User privacy booleans
    private boolean friends_isConnected = false;
    private boolean friendsOfFriends_isConnected = false;
    private boolean following_isConnected = false;
    private boolean followers_isConnected = false;
    private boolean location_isConnected = false;
    private boolean hometown_isConnected = false;
    private boolean birthday_isConnected = false;
    private boolean likes_isConnected = false;
    private boolean work_isConnected = false;
    private boolean school_isConnected = false;
    private boolean music_isConnected = false;
    private boolean movies_isConnected = false;
    private boolean books_isConnected = false;
    private boolean professionalSkills_isConnected = true;

    //Basic Profile Info
    private String status;
    private int views;
    private String firstName;
    private String lastName;
    private String age;
    private String email;

    //Badge Variables
    private String friend;
    private String friendsOfFriends;
    private String instagramFollower;
    private String instagramFollowing;
    private String location;
    private String hometown;
    private String birthday;
    private String likes_json;
    private String workplace_json;
    private String school_json;
    private String music_json;
    private String movies_json;
    private String books_json;
    private String professionalSkills_json;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_load_facebook_permissions);
        context = getApplicationContext();
        activity = LoadFacebookPermissions.this;
        Intent intent = getIntent();
        privacyPreferences = intent.getStringArrayListExtra("privacy_preferences");
        typeface = Typeface.createFromAsset(getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");

        userBadges = new User_Badges();
        userProfile = new User_Profile();

        /** Initialize the mapper for DynamoDB **/
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();

        /** Initialize the view items **/
        initializeViewItems();

        /** Set booleans for user badges **/
        setBooleansForUserBadges();

        /** Sync the user privacy settings **/
        syncPrivacySettings();

        String final_badge_selection = "";
        for (String item : privacyPreferences) {
            final_badge_selection = final_badge_selection + item + "\n";
        }
//        Toast.makeText(LoadFacebookPermissions.this, final_badge_selection, Toast.LENGTH_LONG).show();

    }


    /** Method to sync the user's privacy settings with Cognito Sync **/
    private void syncPrivacySettings() {
        final UserPermissions userPermissions = UserPermissions.getInstance(context);

        if(friends_isConnected){
            userPermissions.setFriends(1);
        } else {
            Toast.makeText(LoadFacebookPermissions.this, "Does not contain " + OnBoarding_Fragment4.KEY_PERMISSION_FRIENDS, Toast.LENGTH_SHORT).show();
        }

        if(friendsOfFriends_isConnected){
            userPermissions.setFriendsOfFriends(1);
        }

        if(followers_isConnected){
            userPermissions.setInstagramFollowers(1);
        }

        if(following_isConnected){
            userPermissions.setInstagramFollowing(1);
        }

        if(location_isConnected){
            userPermissions.setLocation(1);
        }

        if(hometown_isConnected){
            userPermissions.setHometown(1);
        }

        if(likes_isConnected){
            userPermissions.setCommonLikes(1);
        }

        if(birthday_isConnected){
            userPermissions.setBirthday(1);
        }

        if(work_isConnected){
            userPermissions.setWorkplace(1);
        }

        if(school_isConnected){
            userPermissions.setSchool(1);
        }

        if(music_isConnected){
            userPermissions.setMusic(1);
        }

        if(movies_isConnected){
            userPermissions.setMovies(1);
        }

        if(books_isConnected){
            userPermissions.setBooks(1);
        }

        //Set First time user boolean to be false.
        userPermissions.setNewUser(1);

        new AsyncTask<Void, Void, Void>() {
            @Override
            protected Void doInBackground(final Void... params) {
                userPermissions.saveToDataset();
                return null;
            }

            @Override
            protected void onPostExecute(final Void aVoid) {
                // save user settings to remote on background thread
                userPermissions.getDataset().synchronize(new Dataset.SyncCallback() {
                    @Override
                    public void onSuccess(Dataset dataset, List<Record> updatedRecords) {
                        Log.d(LOG_TAG, "onSuccess - dataset updated");

                        //Get the info from facebook based on the privacy settings
                        getFacebookInfo();
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
                animateItem(loadingItem1);

            }
        }.execute();
    }

    /** Get the info from the facebook api**/
    private void getFacebookInfo() {

        //This is to start the animation for getting the Facebook info
        runOnUiThread(new Runnable() {
            @Override
            public void run() {
//                Toast.makeText(LoadFacebookPermissions.this, "Getting facebook info", Toast.LENGTH_SHORT).show();
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
                        response = graphRequest.executeAndWait();
                        getBadgeVariablesFromResponse(response);
                    }
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        };

        thread.start();

    }

    /** Gets the variables set by the user's privacy preferences needed for the badge class **/
    public void getBadgeVariablesFromResponse(GraphResponse response){

        //Get json object from response
        JSONObject json = response.getJSONObject();

        //Print json response for debugging purposes
        printJsonResponse(json);

        //Parse json response into the corresponding variables
        try {
            facebookId = json.getString("id");
            userBadges.setFacebookId(facebookId);

            if(friends_isConnected){
                friend = "1";
                userBadges.setFriend(friend);
            }

            if(friendsOfFriends_isConnected){
                friendsOfFriends = "1";
                userBadges.setFriendsOfFriends(friendsOfFriends);
            }

            if(followers_isConnected){
                instagramFollower = "1";
                userBadges.setInstagram_follower(instagramFollower);
            }

            if(following_isConnected){
                instagramFollowing = "1";
                userBadges.setInstagram_following(instagramFollowing);
            }

            if(location_isConnected){
                JSONObject locationObject = json.getJSONObject("location");
                location = locationObject.getString("name");
                userBadges.setLocation(location);
                System.out.println("JSONPARSING: " + location);
            }

            if(hometown_isConnected){
                JSONObject hometownObject = json.getJSONObject("hometown");
                hometown = hometownObject.getString("name");
                userBadges.setHometown(hometown);
                System.out.println("JSONPARSING: " + hometown);
            }

            if(likes_isConnected){
                //Create JSON array to store the likes
                JSONArray likesArray = new JSONArray();

                //Get the items from the response JSONArray and add it to our custom json array
                JSONObject contextObject = json.getJSONObject("context");
                JSONObject mutualLikes = contextObject.getJSONObject("mutual_likes");
                JSONArray data = mutualLikes.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonobject = data.getJSONObject(i);
                    String name = jsonobject.getString("name");

                    likesArray.put(name);
                }

                JSONObject likesObject = new JSONObject();
                likesObject.put("likes", likesArray);

                likes_json = likesObject.toString();

                userBadges.setLikes_json(likes_json);
                System.out.println("JSONPARSING: " + likes_json);
            }

            if(birthday_isConnected){
                birthday = json.getString("birthday");
                userBadges.setBirthday(birthday);
                System.out.println("JSONPARSING: " + birthday);
            }

            if(work_isConnected){
                //Create JSON array to store the work items
                JSONArray workArray = new JSONArray();

                //Get the work array from the json response
                JSONArray data = json.getJSONArray("work");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject workObject = data.getJSONObject(i);

                    //Get work position name
                    JSONObject positionObject = workObject.getJSONObject("position");
                    String position = positionObject.getString("name");

                    //Get work employer name
                    JSONObject employerObject = workObject.getJSONObject("employer");
                    String employer = employerObject.getString("name");

                    //Get work location name
                    JSONObject locationObject = workObject.getJSONObject("location");
                    String location = locationObject.getString("name");

                    JSONObject workItem = new JSONObject();
                    workItem.put("position", position);
                    workItem.put("employer", employer);
                    workItem.put("location", location);
                    workArray.put(workItem);
                }

                JSONObject workObject = new JSONObject();
                workObject.put("work", workArray);

                workplace_json = workObject.toString();

                userBadges.setWorkplace_json(workplace_json);
                System.out.println("JSONPARSING: " + workplace_json);
            }

            if(school_isConnected){
                //Create JSON array to store the school items
                JSONArray educationArray = new JSONArray();

                //Get the education array from the json response
                JSONArray data = json.getJSONArray("education");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject educationObject = data.getJSONObject(i);

                    //Get school name
                    JSONObject schoolObject = educationObject.getJSONObject("school");
                    String school = schoolObject.getString("name");

                    educationArray.put(school);
                }

                JSONObject schoolsObject = new JSONObject();
                schoolsObject.put("schools", educationArray);

                school_json = schoolsObject.toString();

                userBadges.setSchool_json(school_json);
                System.out.println("JSONPARSING: " + school_json);
            }

            if(professionalSkills_isConnected){
                //Create JSON array to store the skill items
                JSONArray skillsArray = new JSONArray();

                //Get the education array from the json response
                JSONArray data = json.getJSONArray("education");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject educationObject = data.getJSONObject(i);

                    //Get skill name
                    JSONArray concentration = educationObject.getJSONArray("concentration");
                    for(int ii = 0; ii < concentration.length(); ii++){
                        JSONObject skillObject = concentration.getJSONObject(ii);
                        String skill = skillObject.getString("name");

                        skillsArray.put(skill);
                    }
                }

                JSONObject skillObject = new JSONObject();
                skillObject.put("skills", skillsArray);

                professionalSkills_json = skillObject.toString();

                userBadges.setProfessionalSkills_json(professionalSkills_json);
                System.out.println("JSONPARSING: " + professionalSkills_json);
            }

            if(music_isConnected){
                //Create JSON array to store the music items
                JSONArray musicArray = new JSONArray();

                //Get the items from the response JSONArray and add it to our custom json array
                JSONObject musicObject = json.getJSONObject("music");
                JSONArray data = musicObject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonobject = data.getJSONObject(i);
                    String name = jsonobject.getString("name");

                    musicArray.put(name);
                }

                musicObject = new JSONObject();
                musicObject.put("music", musicArray);

                music_json = musicObject.toString();

                userBadges.setMusic_json(music_json);
                System.out.println("JSONPARSING: " + music_json);
            }

            if(movies_isConnected){
                //Create JSON array to store the movie items
                JSONArray moviesArray = new JSONArray();

                //Get the items from the response JSONArray and add it to our custom json array
                JSONObject movieObject = json.getJSONObject("movies");
                JSONArray data = movieObject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonobject = data.getJSONObject(i);
                    String name = jsonobject.getString("name");

                    moviesArray.put(name);
                }

                movieObject = new JSONObject();
                movieObject.put("movies", moviesArray);

                movies_json = movieObject.toString();

                userBadges.setMovies_json(movies_json);
                System.out.println("JSONPARSING: " + movies_json);
            }

            if(books_isConnected){
                //Create JSON array to store the book items
                JSONArray booksArray = new JSONArray();

                //Get the items from the response JSONArray and add it to our custom json array
                JSONObject bookObject = json.getJSONObject("books");
                JSONArray data = bookObject.getJSONArray("data");

                for (int i = 0; i < data.length(); i++) {
                    JSONObject jsonobject = data.getJSONObject(i);
                    String name = jsonobject.getString("name");

                    booksArray.put(name);
                }

                bookObject = new JSONObject();
                bookObject.put("books", booksArray);

                books_json = bookObject.toString();

                userBadges.setBooks_json(books_json);
                System.out.println("JSONPARSING: " + books_json);
            }
//            userName = json.getString("name");
//            userImageUrl = json.getJSONObject("picture")
//                    .getJSONObject("data")
//                    .getString("url");
        } catch (final JSONException jsonException) {
            Log.e("LOGTAG",
                    "Unable to get Facebook user info. " + jsonException.getMessage() + "\n" + response,
                    jsonException);
            // Nothing much we can do here.
        }

        /** Push userBadges item to server **/
        new SaveUserBadgesToDB().execute();
    }

    /** Gets the variables set by the user's privacy preferences needed for the badge class **/
    public void getBasicUserVariablesFromResponse(GraphResponse response){

        //Get json object from response
        JSONObject json = response.getJSONObject();

        try {
            //Get name
            String name = json.getString("name");
            String firstAndLastNAme[] = name.split(" ");
            firstName = firstAndLastNAme[0];
            lastName = firstAndLastNAme[1];

            //Get age
            JSONObject ageRangeObject = json.getJSONObject("age_range");
            age = ageRangeObject.getString("min");

            //Get email
            email = json.getString("email");

            //Set facebookId
            userProfile.setFacebookId(facebookId);

            //Set first name
            userProfile.setFirstName(firstName);

            //Set last name
            userProfile.setLastName(lastName);

            //Set Age
            userProfile.setAge(age);

            //Set email
            userProfile.setEmail(email);

            //Set default Status
            userProfile.setStatus("Hi everyone, I am new to ego.");

            //Set default views
            userProfile.setViews(0);
        } catch (JSONException e) {
            e.printStackTrace();
        }

        /** Push userBadges item to server **/
        new SaveBasicInfoToDB().execute();
    }

    public class SaveUserBadgesToDB extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try{
                mapper.save(userBadges);
            } catch (AmazonClientException ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Toast.makeText(LoadFacebookPermissions.this, "Successfully saved user's badges to db", Toast.LENGTH_SHORT).show();
            getBasicUserVariablesFromResponse(response);
            animateItem(loadingItem2);
        }
    }

    public class SaveBasicInfoToDB extends AsyncTask<Void, Void, Void> {

        @Override
        protected Void doInBackground(Void... params) {
            try{
                mapper.save(userProfile);
            } catch (AmazonClientException ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
//            Toast.makeText(LoadFacebookPermissions.this, "Successfully saved user's info to db", Toast.LENGTH_SHORT).show();
            animateItem(loadingItem3);
        }

    }

    /** Method to go to the Main Activity **/
    public void goToMainActivity(){

        startActivity(new Intent(LoadFacebookPermissions.this, MainActivity.class)
                .setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP));

        LoadFacebookPermissions.this.finish();
//        overridePendingTransition(R.anim.activity_close_scale_r, R.anim.activity_open_translate_r);
    }

    /** Divides json response down into 5 lines to be printed **/
    private void printJsonResponse(JSONObject json) {
        int maxLogSize = 1000;
        for(int i = 0; i <= json.toString().length() / maxLogSize; i++) {
            int start = i * maxLogSize;
            int end = (i+1) * maxLogSize;
            end = end > json.toString().length() ? json.toString().length() : end;
            Log.v("FACEBOOK RESULT2:  ", json.toString().substring(start, end));

        }
    }


    private void initializeViewItems() {
        finishButton = (ImageButton) findViewById(R.id.fpFinishButton);
        finishButton.setOnClickListener(this);
        loadingItem1 = (ImageView) findViewById(R.id.itemListing1);
        loadingItem2 = (ImageView) findViewById(R.id.itemListing2);
        loadingItem3 = (ImageView) findViewById(R.id.itemListing3);
        textView = (TextView) findViewById(R.id.onBoarding_text);
        textView.setTypeface(typeface);
        spinner = (ProgressBar) findViewById(R.id.progressBar1);
    }

    public String animateItem(View loadingBar){

        int trackStartValue = loadingBar.getLeft();
        int trackEndValue = loadingBar.getRight();

        loadingBar.setRight(trackStartValue);
        loadingBar.setVisibility(View.VISIBLE);


        if(loadingBar == loadingItem3){
            ObjectAnimator animator = ObjectAnimator.ofInt(loadingBar, "right", trackStartValue, trackEndValue).setDuration(1000);
            animator.addListener(new Animator.AnimatorListener() {
                @Override
                public void onAnimationStart(Animator animation) {

                }

                @Override
                public void onAnimationEnd(Animator animation) {
                    spinner.setVisibility(View.GONE);
                    finishButton.setVisibility(View.VISIBLE);
                }

                @Override
                public void onAnimationCancel(Animator animation) {

                }

                @Override
                public void onAnimationRepeat(Animator animation) {

                }
            });
            animator.start();
        } else {
            ObjectAnimator.ofInt(loadingBar, "right", trackStartValue, trackEndValue).setDuration(1000).start();
        }
        return null;
    }


    @Override
    public void onClick(View v) {
        if(v == finishButton){
            goToMainActivity();

        }
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

    /** Sets the booleans for the user badges, just makes the code cleaner **/
    private void setBooleansForUserBadges() {

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_FRIENDS)){
            friends_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_FRIENDS_OF_FRIENDS)){
            friendsOfFriends_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_INSTAGRAM_FOLLOWERS)){
            following_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_INSTAGRAM_FOLLOWING)){
            followers_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_LOCATION)){
            location_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_HOMETOWN)){
            hometown_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_LIKES)){
            likes_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_BIRTHDAY)){
            birthday_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_WORK)){
            work_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_SCHOOL)){
            school_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_MUSIC)){
            music_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_MOVIES)){
            movies_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_BOOKS)){
            books_isConnected = true;
        }

        if(privacyPreferences.contains(OnBoarding_Fragment4.KEY_PERMISSION_PROFFESIONAL_SKILLS)){
            professionalSkills_isConnected = true;
        }
    }
}
