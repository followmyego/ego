package net.egobeta.ego.Adapters;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.InputType;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import net.egobeta.ego.Table_Classes.User_Profile;

/**
 * Created by Lucas on 16/09/2016.
 */
public class SocialMediaGridClickHandler {


    private DynamoDBMapper mapper;

    private static final String CONNECT_SOCIAL_PROFILE = "1";
    private static final String DISCONNECT_SOCIAL_PROFILE = "0";

    private Context mContext;

    //Profile Variables
    private static String facebookId;
    private static String email;
    private static String firstName;
    private static String lastName;
    private static String gender;
    private static String birthday;
    private static String user_status;
    private static String usingFacebookPic;

    //Facebook variables
    private static String facebookProfileLinked;

    //Instagram variables
    private static String instagramId;
    private static String instagramUsername;
    private static String instagramProfileLinked;

    //Twitter variables
    private static String twitterId;
    private static String twitterUsername;
    private static String twitterProfileLinked;

    //Google+ variables
    private static String googlePlusId;
    private static String googlePlusProfileLinked;

    //Snapchat variables
    private static String snapchatUsername;
    private static String snapchatProfileLinked;

    //LinkedIn variables
    private static String linkedInId;
    private static String linkedInProfileLinked;

    //User Profile
    User_Profile userProfile;


    //Constructor to initialize the user social media variables in this class
    public SocialMediaGridClickHandler(Context context, User_Profile userProfile, DynamoDBMapper mapper){
        mContext = context;
        this.userProfile = userProfile;
        this.mapper = mapper;

        //User variables
        facebookId = userProfile.getFacebookId();

        //Facebook variables
        facebookProfileLinked = userProfile.getFacebookConnected();

        //Instagram variables
        instagramId = userProfile.getInstagram_id();
        instagramUsername = userProfile.getInstagram_username();
        instagramProfileLinked = userProfile.getInstagramConnected();

        //Twitter variables
        twitterId = userProfile.getTwitter_id();
        twitterUsername = userProfile.getTwitter_username();
        twitterProfileLinked = userProfile.getTwitterConnected();

        //Google+ variables
        googlePlusId = userProfile.getGoogle_plus_id();
        googlePlusProfileLinked = userProfile.getGoogle_plusConnected();

        //Snapchat variables
        snapchatUsername = userProfile.getSnapchat_username();
        snapchatProfileLinked = userProfile.getSnapchatConnected();

        //LinkedIn variables
        linkedInId = userProfile.getLinkedIn_id();
        linkedInProfileLinked = userProfile.getLinkedInConnected();
    }


    //Method to detect and handle what happens when the selected social media is pressed
    public void handleTheSocialMediaPressed(int position) {
        String whatAction = CONNECT_SOCIAL_PROFILE;

        /******************************FACEBOOK BUTTON******************************************/
        if (position == 0) {
            String socialMedia = "Facebook";
            //Check if the user has linked their facebook profile to their ego profile.
            if (!facebookProfileLinked.equals("1")) {
                //Ask them if they want to link their facebook profile to their ego profile.
                promptToLinkSocialMediaProfile(socialMedia, CONNECT_SOCIAL_PROFILE);
            } else {
                //Bring them to their facebook profile
                Intent facebookIntent = UrlIntentHandler.facebookPageUrlIntent(mContext, facebookId);
                mContext.startActivity(facebookIntent);
            }

            /******************************TWITTER BUTTON*******************************************/
        } else if (position == 1) {
            String socialMedia = "Twitter";
//            Check if the user has linked their twitter profile to their ego profile.
            if (!twitterProfileLinked.equals("1")) {
//                Ask the user if they want to link their twitter profile to their ego profile.
                promptToLinkSocialMediaProfile(socialMedia, whatAction);
            }
//            Bring them to their twitter profile
            else {
//                Make sure that we have the twitter username and id just in case
                if (!twitterId.equals("") || !twitterId.equals(null) || !twitterUsername.equals("") || !twitterUsername.equals(null)) {
                    /*******Bring them to their profile page*********/
                    Intent twitterIntent = UrlIntentHandler.twitterPageUrlIntent(mContext, twitterId, twitterUsername);
                    mContext.startActivity(twitterIntent);
                } else {
                    /******Code to login with twitter to get their twitter user info*****/
//                    MainActivity.clickTwitterButton(socialMedia, key, whatAction);
                    /** CODE HERE TO LOGIN TO TWITTER **/
                    Toast.makeText(mContext, "Connecting Twitter is not ready yet", Toast.LENGTH_SHORT).show();
                }
            }
            /******************************GOOGLE PLUS BUTTON***************************************/
        } else if (position == 2) {
            String socialMedia = "Google+";
//            Check if the user has linked their GooglePlus profile to their ego profile.
            if (!googlePlusProfileLinked.equals("1")) {
//                Ask the user if they want to link their GooglePlus profile to their ego profile.
                promptToLinkSocialMediaProfile(socialMedia, whatAction);
//                Bring them to their GooglePlus profile
            } else {
//                Make sure that we have the GooglePlus username and if just in case
                if (!googlePlusId.equals("") || !googlePlusId.equals(null)) {
                    /*******Bring them to their GooglePlus profile page*********/
                    UrlIntentHandler.googlePlusPageUrlIntent(mContext, googlePlusId);
                } else {
                    /******Code to login with GooglePlus to get their Google user info*****/
//                    MainActivity.googleSignIn(socialMedia, key, whatAction);
                    /** CODE HERE TO LOGIN TO GOOGLE PLUS **/
                    Toast.makeText(mContext, "Connecting Google Plus is not ready yet", Toast.LENGTH_SHORT).show();
                }
            }
            /******************************INSTAGRAM BUTTON*****************************************/
        } else if (position == 3) {
            String socialMedia = "Instagram";
//            Check if the user has linked their instagram profile to their ego profile.
            if (!instagramProfileLinked.equals("1")) {
//                Ask the user if they want to link their instagram profile to their ego profile.
                promptToLinkSocialMediaProfile(socialMedia, CONNECT_SOCIAL_PROFILE);
            } else {
//                Bring them to their instagram profile
                Intent instagramIntent = UrlIntentHandler.instagramPageUrlIntent(mContext, instagramUsername);
                mContext.startActivity(instagramIntent);
            }
            /******************************LINKEDIN BUTTON*****************************************/
        } else if (position == 4) {
            String socialMedia = "LinkedIn";
            if(!linkedInProfileLinked.equals("1")){
//                Ask the user if they want to link their LinkedIn profile to their ego profile.
                promptToLinkSocialMediaProfile(socialMedia, whatAction);
            } else {
//                Bring them to their LinkedIn profile
                Intent intent = UrlIntentHandler.linkedInPageUrlIntent(mContext, linkedInId);
                mContext.startActivity(intent);
            }
            /******************************SNAPCHAT BUTTON*****************************************/
        } else if (position == 5){
            String socialMedia = "Snapchat";
            if(!snapchatProfileLinked.equals("1")){
//                Ask the user if they want to link their Snapchat profile to their ego profile.
                promptToLinkSocialMediaProfile(socialMedia, whatAction);
            } else {
//                Open up Snapchat App
                Toast.makeText(mContext, snapchatUsername, Toast.LENGTH_SHORT).show();
                Intent intent = UrlIntentHandler.snapchatPageUrlIntent(mContext);
                mContext.startActivity(Intent.createChooser(intent, "Open Snapchat"));
            }
        } else {
            Toast.makeText(mContext, "item: " + position, Toast.LENGTH_SHORT).show();
        }
    }



    //Method to ask the user if they would like to connect the targeted social media to their ego account
    public void promptToLinkSocialMediaProfile(final String socialMedia, final String whatAction){
        AlertDialog.Builder statusUpdateBuilder = new AlertDialog.Builder(mContext);
        statusUpdateBuilder.setTitle("Would you like to connect your " + socialMedia + " to your ego account?");

//        Handle the set up of them connecting their social media platform.
        statusUpdateBuilder.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {

                if(socialMedia.equals("Facebook")){
                    /*********************************FACEBOOK**************************************/
                    /****Set facebookProfileLinked on server to be true****/
                    Toast.makeText(mContext, "We already have the facebook id", Toast.LENGTH_LONG).show();
                    userProfile.setFacebookConnected("1");
                    new SaveUserProfile().execute();


                    /********************************INSTAGRAM**************************************/
                } else if(socialMedia.equals("Instagram")){
                    if(instagramUsername.equals(null) || instagramUsername.equals("")){
                        /******Code to login with instagram to get their instagram user info*****/
//                        SocialMediasPageFragment.connectOrDisconnectUser();
//                        reloadUserVariables();
                        /** CODE HERE TO LOGIN TO Instagram **/
                        Toast.makeText(mContext, "Connecting to Instagram is not ready yet", Toast.LENGTH_SHORT).show();

                        /****Set instagramProfileLinked on server to be true****/
//                        userProfile.setInstagramConnected("1");
//                        new SaveUserProfile().execute();
                    } else {
                        /****Set instagramProfileLinked on server to be true****/
                        Toast.makeText(mContext, "We already have the instagram username", Toast.LENGTH_LONG).show();
                        userProfile.setInstagramConnected("1");
                        new SaveUserProfile().execute();
                    }
                    /*********************************TWITTER***************************************/
                } else if (socialMedia.equals("Twitter")){
                    if(twitterUsername.equals(null) || twitterUsername.equals("")){
                        /******Code to login with twitter to get their twitter user info*****/
//                    MainActivity.clickTwitterButton(socialMedia, key, whatAction);
                        /** CODE HERE TO LOGIN TO TWITTER **/
                        Toast.makeText(mContext, "Connecting Twitter is not ready yet", Toast.LENGTH_SHORT).show();
                    } else {
                        /****Set twitterProfileLinked on server to be true****/
                        userProfile.setTwitterConnected("1");
                        new SaveUserProfile().execute();
                    }
                    /********************************GOOGLE+****************************************/
                } else if (socialMedia.equals("Google+")){
                    if(googlePlusId.equals(null) || googlePlusId.equals("")){
                        /******Code to login with GooglePlus to get their GooglePlus user info*****/
//                        new MainActivity();
//                        MainActivity.googleSignIn(socialMedia, key, whatAction);
                        /** CODE HERE TO LOGIN TO GOOGLE+ **/
                        Toast.makeText(mContext, "Connecting GOOGLE+ is not ready yet", Toast.LENGTH_SHORT).show();
                    } else {
                        /****Set googlePlusProfileLinked on server to be true****/
                        userProfile.setGoogle_plusConnected("1");
                        new SaveUserProfile().execute();
                    }
                    /********************************SNAPCHAT+**************************************/
                } else if (socialMedia.equals("Snapchat")){
                    if(snapchatUsername.equals(null) || snapchatUsername.equals("")){
                        /******Code to prompt the user to enter their snapchat username*****/
                        AlertDialog.Builder snapchatUsernameUpdateBuilder = new AlertDialog.Builder(mContext);
                        snapchatUsernameUpdateBuilder.setTitle("What is your Snapchat username?");
                        // Set up the input
                        final EditText input = new EditText(mContext);
                        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
                        input.setInputType(InputType.TYPE_CLASS_TEXT);
                        snapchatUsernameUpdateBuilder.setView(input);
                        // Set up the buttons
                        snapchatUsernameUpdateBuilder.setPositiveButton("confirm", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                snapchatUsername = input.getText().toString();
                                //Send updated user status to the server
                                userProfile.setSnapchat_username(snapchatUsername);
                                userProfile.setSnapchatConnected("1");
                                new SaveUserProfile().execute();
                            }
                        });
                        snapchatUsernameUpdateBuilder.setNegativeButton("cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.cancel();
                            }
                        });
                        snapchatUsernameUpdateBuilder.show();
                    } else {
                        /****Set snapchatProfileLinked on server to be true****/
                        userProfile.setSnapchatConnected("1");
                        new SaveUserProfile().execute();
                    }
                    /********************************LINKEDIN+**************************************/
                } else if (socialMedia.equals("LinkedIn")){
                    if(linkedInId.equals(null) || linkedInId.equals("")){
                        /******Code to login with LinkedIn to get their LinkedIn user info*****/
//                        MainActivity.linkedInSignIn(socialMedia, key, whatAction);
                        /** CODE HERE TO LOGIN TO LINKEDIN **/
                        Toast.makeText(mContext, "Connecting LinkedIn is not ready yet", Toast.LENGTH_SHORT).show();
                    } else {
                        /****Set googlePlusProfileLinked on server to be true****/
                        userProfile.setLinkedInConnected("1");
                        new SaveUserProfile().execute();
                    }
                    /*******************************************************************************/
                } else {
                    //Empty
                }
            }
        });

        statusUpdateBuilder.setNegativeButton("No.", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.cancel();
            }
        });
        statusUpdateBuilder.show();
    }



    //Method to reload the user variables in this class
    public static void reloadUserVariables(){
        //User variables
//        facebookId = user.facebookId;
//
//        //Facebook variables
//        facebookProfileLinked = user_SocialNetwork.facebookProfileLinked;
//
//        //Instagram variables
//        instagramId = user_SocialNetwork.instagramId;
//        instagramUsername = user_SocialNetwork.instagramUsername;
//        instagramProfileLinked = user_SocialNetwork.instagramProfileLinked;
//
//        //Twitter variables
//        twitterId = user_SocialNetwork.twitterId;
//        twitterUsername = user_SocialNetwork.twitterUsername;
//        twitterProfileLinked = user_SocialNetwork.twitterProfileLinked;
//
//        //Google+ variables
//        googlePlusId = user_SocialNetwork.googlePlusId;
//        googlePlusProfileLinked = user_SocialNetwork.googlePlusProfileLinked;
//
//        //Snapchat variables
//        snapchatUsername = user_SocialNetwork.snapchatUsername;
//        snapchatProfileLinked = user_SocialNetwork.snapchatProfileLinked;
//
//        //LinkedIn variables
//        snapchatUsername = user_SocialNetwork.snapchatUsername;
//        snapchatProfileLinked = user_SocialNetwork.snapchatProfileLinked;
    }



    /** Method to save userProfile to Database **/
    public class SaveUserProfile extends AsyncTask<Void, Void, Void> {


        public SaveUserProfile() {

        }

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
//            Toast.makeText(MainActivity.this, "Successfully saved user's info to db", Toast.LENGTH_SHORT).show();

        }

    }

}
