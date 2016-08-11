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
 * Created by Lucas on 04/08/2016.
 */
public class LoadUserImageAsyncTask extends AsyncTask<String, Void, String> {

    private RoundedImageView imageView;
    String userImageUrl;
    Bitmap userImage;
    Context context;

    public LoadUserImageAsyncTask(RoundedImageView imageView, Context context){
        this.imageView = imageView;
        this.context = context;
    }

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

            Drawable d = new BitmapDrawable(context.getResources(), userImage);
            imageView.setImageDrawable(d);
        }

        //Print server AsyncTask response
        System.out.println("LoadUserImageAsyncTask method Resulted Value: " + result);
    }


}