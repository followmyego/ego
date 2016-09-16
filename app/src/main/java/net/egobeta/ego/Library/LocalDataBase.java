package net.egobeta.ego.Library;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.ArrayList;

/**
 * Created by Lucas on 20/01/2016.
 */
public class LocalDataBase {

    //Declare the Shared Preference Variable
    public static SharedPreferences mLocalDatabase;

    //Name of this Shared Preference
    public final static String SP_NAME = "User Details";

    //User variables
    public static final String FACEBOOK_ID = "facebook_id";
    public static final String EMAIL = "email";
    public static final String FIRST_NAME = "first_name";
    public static final String LAST_NAME = "last_name";
    public static final String GENDER = "gender";
    public static final String BIRTHDAY = "birthday";
    public static final String USER_STATUS = "user_status";
    public static final String PAGE_VIEWS = "page_views";


    //Other variables
    public static final String LOGGED_IN = "logged_In";
    public static final String PROFILE_PIC = "profile_picture";


    //Facebook variables
    public static final String FACEBOOK_P_LINKED = "facebook_profile_linked";

    //Instagram variables
    public static final String INSTAGRAM_ID = "instagram_id";
    public static final String INSTAGRAM_USERNAME = "instagram_username";
    public static final String INSTAGRAM_P_LINKED = "instagram_profile_linked";
    public static final String INSTAGRAM_IMAGES_LINKED = "instagram_images_linked";

    //Twitter variables
    public static final String TWITTER_ID = "twitter_id";
    public static final String TWITTER_USERNAME = "twitter_username";
    public static final String TWITTER_P_LINKED = "twitter_profile_linked";

    //GooglePlus variables
    public static final String GOOGLE_PLUS_ID = "google_plus_id";
    public static final String GOOGLE_PLUS_P_LINKED = "google_plus_profile_linked";

    //LinkedIn variables
    public static final String LINKEDIN_ID = "linkedIn_id";
    public static final String LINKEDIN_P_LINKED = "linkedIn_profile_linked";

    //Snapchat variables
    public static final String SNAPCHAT_USERNAME = "snapchat_username";
    public static final String SNAPCHAT_P_LINKED = "snapchat_profile_linked";

    //Pinned Users
    public static final String PINNED_USER_1 = "pinned_user_1";
    public static final String PINNED_USER_2 = "pinned_user_2";
    public static final String PINNED_USER_3 = "pinned_user_3";
    public static final String PINNED_USER_4 = "pinned_user_4";
    public static final String PINNED_USER_5 = "pinned_user_5";



    //Call this constructor to access this local data base
    public LocalDataBase (Context context){
        mLocalDatabase = context.getSharedPreferences(SP_NAME, 0);
    }


    public void setPinnedUser1(String pinnedUser1){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(PINNED_USER_1, pinnedUser1);
        spEditor.commit();
    }

    public String getPinnedUser1(){
        String pinnedUser1 = mLocalDatabase.getString(PINNED_USER_1, "");
        return pinnedUser1;
    }

    public void setPinnedUser2(String pinnedUser2){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(PINNED_USER_2, pinnedUser2);
        spEditor.commit();
    }

    public String getPinnedUser2(){
        String pinnedUser2 = mLocalDatabase.getString(PINNED_USER_2, "");
        return pinnedUser2;
    }

    public void setPinnedUser3(String pinnedUser3){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(PINNED_USER_3, pinnedUser3);
        spEditor.commit();
    }

    public String getPinnedUser3(){
        String pinnedUser3 = mLocalDatabase.getString(PINNED_USER_3, "");
        return pinnedUser3;
    }

    public void setPinnedUser4(String pinnedUser4){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(PINNED_USER_4, pinnedUser4);
        spEditor.commit();
    }

    public String getPinnedUser4(){
        String pinnedUser4 = mLocalDatabase.getString(PINNED_USER_4, "");
        return pinnedUser4;
    }

    public void setPinnedUser5(String pinnedUser5){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(PINNED_USER_1, pinnedUser5);
        spEditor.commit();
    }

    public String getPinnedUser5(){
        String pinnedUser5 = mLocalDatabase.getString(PINNED_USER_5, "");
        return pinnedUser5;
    }






    //Method to store the user details from database table in local data base
    public void storeUsersInfo(User user){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(FACEBOOK_ID, user.facebookId);
        spEditor.putString(EMAIL, user.mEmail);
        spEditor.putString(FIRST_NAME, user.mFirstName);
        spEditor.putString(LAST_NAME, user.mLastName);
        spEditor.putString(BIRTHDAY, user.mBirthday);
        spEditor.putString(USER_STATUS, user.mUserStatus);
        spEditor.putString(PAGE_VIEWS, user.mPageViews);
        spEditor.commit();
    }

    //Method to get the current user thats stored in the local data base
    public User getUsersInfo(){
        String facebookId = mLocalDatabase.getString(FACEBOOK_ID, "");
        String email = mLocalDatabase.getString(EMAIL, "");
        String firstName = mLocalDatabase.getString(FIRST_NAME, "");
        String lastName = mLocalDatabase.getString(LAST_NAME, "");
        String gender = mLocalDatabase.getString(GENDER, "");
        String birthday = mLocalDatabase.getString(BIRTHDAY, "");
        String user_status = mLocalDatabase.getString(USER_STATUS, "");

        String pageViews = mLocalDatabase.getString(PAGE_VIEWS, "");
        return new User(facebookId, email, firstName, lastName, gender, birthday, user_status, pageViews);
    }

    public void storeUsersSocialNetworkInfo(User user){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(FACEBOOK_ID, user.facebookId);
        spEditor.putString(FACEBOOK_P_LINKED, user.facebookProfileLinked);
        spEditor.putString(INSTAGRAM_ID, user.instagramId);
        spEditor.putString(INSTAGRAM_USERNAME, user.instagramUsername);
        spEditor.putString(INSTAGRAM_P_LINKED, user.instagramProfileLinked);
        spEditor.putString(INSTAGRAM_IMAGES_LINKED, user.instagramImagesLinked);
        spEditor.putString(TWITTER_ID, user.twitterId);
        spEditor.putString(TWITTER_USERNAME, user.twitterUsername);
        spEditor.putString(TWITTER_P_LINKED, user.twitterProfileLinked);
        spEditor.putString(GOOGLE_PLUS_ID, user.googlePlusId);
        spEditor.putString(GOOGLE_PLUS_P_LINKED, user.googlePlusProfileLinked);
        spEditor.putString(LINKEDIN_ID, user.linkedInId);
        spEditor.putString(LINKEDIN_P_LINKED, user.linkedInProfileLinked);
        spEditor.putString(SNAPCHAT_USERNAME, user.snapchatUsername);
        spEditor.putString(SNAPCHAT_P_LINKED, user.snapchatProfileLinked);
        spEditor.commit();
    }

    public User getUsersSocialNetworkInfo(){
        String facebookId = mLocalDatabase.getString(FACEBOOK_ID, "");
        String facebookProfileLinked = mLocalDatabase.getString(FACEBOOK_P_LINKED, "");
        String instagram_id = mLocalDatabase.getString(INSTAGRAM_ID, "");
        String instagram_username = mLocalDatabase.getString(INSTAGRAM_USERNAME, "");
        String instagramProfileLinked = mLocalDatabase.getString(INSTAGRAM_P_LINKED, "");
        String instagramImagesLinked = mLocalDatabase.getString(INSTAGRAM_IMAGES_LINKED, "");
        String twitterId = mLocalDatabase.getString(TWITTER_ID, "");
        String twitterUsername = mLocalDatabase.getString(TWITTER_USERNAME, "");
        String twitterProfileLinked = mLocalDatabase.getString(TWITTER_P_LINKED, "");
        String googlePlusId = mLocalDatabase.getString(GOOGLE_PLUS_ID, "");
        String googlePlusProfileLinked = mLocalDatabase.getString(GOOGLE_PLUS_P_LINKED, "");
        String linkedInId = mLocalDatabase.getString(LINKEDIN_ID, "");
        String linkedInProfileLinked = mLocalDatabase.getString(LINKEDIN_P_LINKED, "");
        String snapchatUsername = mLocalDatabase.getString(SNAPCHAT_USERNAME, "");
        String snapchatProfileLinked = mLocalDatabase.getString(SNAPCHAT_P_LINKED, "");
        return new User(facebookId, facebookProfileLinked, instagram_id, instagram_username, instagramProfileLinked,
                instagramImagesLinked, twitterId, twitterUsername,twitterProfileLinked, googlePlusId, googlePlusProfileLinked,
                linkedInId, linkedInProfileLinked, snapchatUsername, snapchatProfileLinked);
    }

    //Clear all data in SharedPreferences
    public void clearUserData(){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.clear();
        spEditor.commit();
    }




    //Method to get the current user thats store in the local data base
    public User getInfoFromFacebook(){
        String facebookId = mLocalDatabase.getString(FACEBOOK_ID, "");
        String email = mLocalDatabase.getString(EMAIL, "");
        String firstName = mLocalDatabase.getString(FIRST_NAME, "");
        String lastName = mLocalDatabase.getString(LAST_NAME, "");
        String gender = mLocalDatabase.getString(GENDER, "");
        String birthday = mLocalDatabase.getString(BIRTHDAY, "");
        String userStatus = mLocalDatabase.getString(USER_STATUS, "");
        String pageViews = mLocalDatabase.getString(PAGE_VIEWS, "");
        return new User(facebookId, email, firstName, lastName, gender, birthday, userStatus, pageViews);
    }

    /********************************INSTAGRAM*******************************/
    public void setInstagramID(String instagram_TAG_ID){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(INSTAGRAM_ID, instagram_TAG_ID);
        spEditor.commit();
    }

    public String getInstagramID(){
        String instagram_id = mLocalDatabase.getString(INSTAGRAM_ID, "");
        return instagram_id;
    }

    public void setInstagramUsername(String instagram_username){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(INSTAGRAM_USERNAME, instagram_username);
        spEditor.commit();
    }

    public String getInstagramUsername(){
        String instagram_username = mLocalDatabase.getString(INSTAGRAM_USERNAME, "");
        return instagram_username;
    }

    public void setInstagramPLinked(String instagramPLinked){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(INSTAGRAM_P_LINKED, instagramPLinked);
        spEditor.commit();
    }

    public String getInstagramPLinked(){
        String instagramPLinked = mLocalDatabase.getString(INSTAGRAM_P_LINKED, "");
        return instagramPLinked;
    }

    public void setInstagramImagesLinked(String instagramImagesLinked){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(INSTAGRAM_IMAGES_LINKED, instagramImagesLinked);
        spEditor.commit();
    }

    public String getInstagramImagesLinked(){
        String instagramImagesLinked = mLocalDatabase.getString(INSTAGRAM_IMAGES_LINKED, "");
        return instagramImagesLinked;
    }

    /********************************Twitter*********************************/
    public void setTwitterId(String twitterId){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(TWITTER_ID, twitterId);
        spEditor.commit();
    }

    public String getTwitterId(){
        String twitterId = mLocalDatabase.getString(TWITTER_ID, "");
        return twitterId;
    }

    public void setTwitterUsername(String twitterUsername){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(TWITTER_USERNAME, twitterUsername);
        spEditor.commit();
    }

    public String getTwitterUsername(){
        String twitterUsername = mLocalDatabase.getString(TWITTER_USERNAME, "");
        return twitterUsername;
    }

    public void setTwitterPLinked(String twitterPLinked){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(TWITTER_P_LINKED, twitterPLinked);
        spEditor.commit();
    }

    public String getTwitterPLinked(){
        String twitterPLinked = mLocalDatabase.getString(TWITTER_P_LINKED, "");
        return twitterPLinked;
    }

    /********************************GOOGLE PLUS*********************************/
    public void setGooglePlusId(String googlePlusId){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(GOOGLE_PLUS_ID, googlePlusId);
        spEditor.commit();
    }

    public String getGooglePlusId(){
        String googlePlusId = mLocalDatabase.getString(GOOGLE_PLUS_ID, "");
        return googlePlusId;
    }

    public void setGooglePlusPLinked(String googlePlusPLinked){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(GOOGLE_PLUS_P_LINKED, googlePlusPLinked);
        spEditor.commit();
    }

    public String getGooglePlusPLinked(){
        String googlePlusPLinked = mLocalDatabase.getString(GOOGLE_PLUS_P_LINKED, "");
        return googlePlusPLinked;
    }

    /********************************LINKEDIN*********************************/
    public void setLinkedinId(String linkedinId){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(LINKEDIN_ID, linkedinId);
        spEditor.commit();
    }

    public String getLinkedinId(){
        String linkedinId = mLocalDatabase.getString(LINKEDIN_ID, "");
        return linkedinId;
    }

    public void setLinkedinPLinked(String linkedinPLinked){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(LINKEDIN_P_LINKED, linkedinPLinked);
        spEditor.commit();
    }

    public String getLinkedinPLinked(){
        String linkedinPLinked = mLocalDatabase.getString(LINKEDIN_P_LINKED, "");
        return linkedinPLinked;
    }

    /********************************SNAPCHAT*********************************/
    public void setSnapchatUsername(String snapchatUsername){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(SNAPCHAT_USERNAME, snapchatUsername);
        spEditor.commit();
    }

    public String getSnapchatUsername(){
        String snapchatUsername = mLocalDatabase.getString(SNAPCHAT_USERNAME, "");
        return snapchatUsername;
    }

    public void setSnapchatPLinked(String snapchatPLinked){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(SNAPCHAT_P_LINKED, snapchatPLinked);
        spEditor.commit();
    }

    public String getSnapchatPLinked(){
        String snapchatPLinked = mLocalDatabase.getString(SNAPCHAT_P_LINKED, "");
        return snapchatPLinked;
    }

    /************************************************************************/


    public void setSocialProfileLinked(String key, String whatAction){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(key, whatAction);
        spEditor.commit();
    }

    public void updateUserStatus(String userStatus){
        SharedPreferences.Editor spEditor = mLocalDatabase.edit();
        spEditor.putString(USER_STATUS, userStatus);
        spEditor.commit();
    }

}

