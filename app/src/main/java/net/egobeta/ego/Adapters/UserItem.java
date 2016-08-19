package net.egobeta.ego.Adapters;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;

import net.egobeta.ego.RoundedImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

/**
 * Created by Lucas on 11/08/2016.
 */
public class UserItem {
    Context context;
    Drawable profilePicture = null;
    String facebookId;
    boolean isFriend;
    RoundedImageView roundedImageView;


    public UserItem(Context context, String facebookId) {
        this.context = context;
        this.facebookId = facebookId;
        setUserProfilePicture();
    }

    private void setUserProfilePicture(){
        if(profilePicture == null){
            new LoadUserImageAsyncTask().execute(facebookId);
        }

    }

    public Drawable getProfilePicture() {
        return profilePicture;
    }

    public void setProfilePicture(Drawable profilePicture) {
        this.profilePicture = profilePicture;
    }

    public String getFacebookId() {
        return facebookId;
    }

    public void setFacebookId(String facebookId) {
        this.facebookId = facebookId;
    }

    public void setViewItem(RoundedImageView roundedImageView, boolean isFriend) {
        this.roundedImageView = roundedImageView;
        new LoadUserImageAsyncTask().execute(facebookId);
        this.isFriend = isFriend;
    }




    public class LoadUserImageAsyncTask extends AsyncTask<String, Void, String> {

        String userImageUrl;
        Bitmap userImage;


        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            //Code to Load Bitmap
            userImageUrl = "https://graph.facebook.com/" + params[0] + "/picture?width=190&height=190";

            try {
                final InputStream is = new URL(userImageUrl).openStream();
                userImage = BitmapFactory.decodeStream(is);
                is.close();
            } catch (IOException e) {
                // clear user image
                userImage = null;
            }

            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            if(userImage != null){
                System.out.println("UserItem: ONE");
                profilePicture = new BitmapDrawable(context.getResources(), userImage);
            } else {
                System.out.println("UserItem: TWO");
                System.out.println("User Image eqauls null");
            }

            if(roundedImageView != null){
                System.out.println("UserItem: THREE");
                if(profilePicture != null){
                    System.out.println("UserItem: FOUR");
                    roundedImageView.setImageDrawable(profilePicture);
                }
            }
        }


    }
}
