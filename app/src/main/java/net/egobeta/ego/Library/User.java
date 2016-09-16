package net.egobeta.ego.Library;


public class User {

    //Login Information
    public String facebookId;

    //Ego Personal Information
    public String mProfilePicture;
    public String mEmail;
    public String mFirstName;
    public String mLastName;
    public String mBirthday;
    public String mUserStatus;
    public String mPageViews;


    //Facebook Information
    public String facebookProfileLinked;

    //Instagram info
    public String instagramId;
    public String instagramUsername;
    public String instagramProfileLinked;
    public String instagramImagesLinked;

    //Twitter info
    public String twitterId;
    public String twitterUsername;
    public String twitterProfileLinked;

    //GooglePlus info
    public String googlePlusId;
    public String googlePlusProfileLinked;

    //LinkedIn info
    public String linkedInId;
    public String linkedInProfileLinked;

    //Snapchat info
    public String snapchatUsername;
    public String snapchatProfileLinked;






    /**Constructor for LocalDataBase.storeUsersInfo()*/
    public User (String facebookID, String email, String firstName, String lastName,
                 String birthday, String user_status, String pageViews){
        facebookId = facebookID;
        mEmail = email;
        mFirstName = firstName;
        mLastName = lastName;
        mBirthday = birthday;
        mUserStatus = user_status;
        mPageViews = pageViews;

    }

    /**Constructor for getting User info with profile pic*/
    public User (String facebookID, String email, String firstName, String lastName,
                 String birthday, String user_status, String pageViews, String profilePicture){
        facebookId = facebookID;
        mEmail = email;
        mFirstName = firstName;
        mLastName = lastName;
        mBirthday = birthday;
        mUserStatus = user_status;
        mPageViews = pageViews;
        mProfilePicture = profilePicture;
    }



    /**Create user from facebook*/
    public User (String facebookId, String email, String firstName, String lastName, String birthday){
        this.facebookId = facebookId;
        this.mEmail = email;
        this.mFirstName = firstName;
        this.mLastName = lastName;
        this.mBirthday = birthday;
    }

    /**Constructor for LocalDataBase.storeUsersSocialNetworkInfo()*/
    public User (String facebookId, String facebookProfileLinked, String instagramId, String instagram_username,
                 String instagramProfileLinked, String instagramImagesLinked, String twitterId, String twitterUsername,
                 String twitterProfileLinked, String googlePlusId, String googlePlusProfileLinked, String linkedInId,
                 String linkedInProfileLinked, String snapchatUsername, String snapchatProfileLinked ){

        this.facebookId = facebookId;
        this.facebookProfileLinked = facebookProfileLinked;
        this.instagramId = instagramId;
        this.instagramUsername = instagram_username;
        this.instagramProfileLinked = instagramProfileLinked;
        this.instagramImagesLinked = instagramImagesLinked;
        this.twitterId = twitterId;
        this.twitterUsername = twitterUsername;
        this.twitterProfileLinked = twitterProfileLinked;
        this.googlePlusId = googlePlusId;
        this.googlePlusProfileLinked = googlePlusProfileLinked;
        this.linkedInId = linkedInId;
        this.linkedInProfileLinked = linkedInProfileLinked;
        this.snapchatUsername = snapchatUsername;
        this.snapchatProfileLinked = snapchatProfileLinked;
    }


    /**Constructor for storing Instagram id*/
    public User(String instagramTAG_ID){
        instagramId = instagramTAG_ID;
    }

}
