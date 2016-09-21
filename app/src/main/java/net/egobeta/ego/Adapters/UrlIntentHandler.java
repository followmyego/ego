package net.egobeta.ego.Adapters;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;

import java.util.List;

/**
 * Created by Lucas on 16/09/2016.
 */
public class UrlIntentHandler {



    /**FACEBOOK PROFILE**/
    public static Intent facebookPageUrlIntent(Context context, String facebookId) {

        /*Return the intent to open the official Facebook app to the user's profile. If the Facebook app is not
       installed then the Web Browser will be used.*/
        String FACEBOOK_URL = "https://www.facebook.com/" + facebookId;
        String facebookUrl;
        PackageManager packageManager = context.getPackageManager();
        try {
            int versionCode = packageManager.getPackageInfo("com.facebook.katana", 0).versionCode;
            if (versionCode >= 3002850) { //newer versions of fb app
                facebookUrl = "fb://facewebmodal/f?href=" + FACEBOOK_URL;
            } else { //older versions of fb app
                facebookUrl = "fb://page/" + facebookId;
            }
        } catch (PackageManager.NameNotFoundException e) {
            facebookUrl = FACEBOOK_URL; //normal web url
        }
        Intent facebookIntent = new Intent(Intent.ACTION_VIEW);
        facebookIntent.setData(Uri.parse(facebookUrl));
        return facebookIntent;
    }


    /**INSTAGRAM PROFILE**/
    public static Intent instagramPageUrlIntent(Context context, String instagramUsername) {

        /*Intent to open the official Instagram app to the user's profile. If the Instagram app is not
         installed then the Web Browser will be used.*/
        String url = "http://instagram.com/" + instagramUsername;
        final Intent intent = new Intent(Intent.ACTION_VIEW);
        try {
            if (context.getPackageManager().getPackageInfo("com.instagram.android", 0) != null) {
                if (url.endsWith("/")) {
                    url = url.substring(0, url.length() - 1);
                }
                final String username = url.substring(url.lastIndexOf("/") + 1);
                // http://stackoverflow.com/questions/21505941/intent-to-open-instagram-user-profile-on-android
                intent.setData(Uri.parse("http://instagram.com/_u/" + username));
                intent.setPackage("com.instagram.android");
                return intent;
            }
        } catch (PackageManager.NameNotFoundException ignored) {
        }
        intent.setData(Uri.parse(url));
        return intent;
    }

    /**TWITTER PROFILE**/
    public static Intent twitterPageUrlIntent(Context context, String twitterId, String twitterUsername) {

        /*Intent to open the official Twitter app to the user's profile. If the Twitter app is not
         installed then the Web Browser will be used.*/
        Intent intent = null;
        try {
            // get the Twitter app if possible
            context.getPackageManager().getPackageInfo("com.twitter.android", 0);
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("twitter://user?user_id=" + twitterId));
            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        } catch (PackageManager.NameNotFoundException e) {
            // no Twitter app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://twitter.com/" + twitterUsername));
        }
        return intent;
    }

    /**GOOGLE PLUS PROFILE**/
    public static void googlePlusPageUrlIntent(Context context, String googlePlusId) {
        /*Intent to open the official Google+ app to the user's profile. If the Google+ app is not
         installed then the Web Browser will be used.*/
        Intent intent;
        try {
            // get the Google+ app if possible
            intent = new Intent(Intent.ACTION_VIEW);
            intent.setClassName("com.google.android.apps.plus",
                    "com.google.android.apps.plus.phone.UrlGatewayActivity");
            intent.putExtra("customAppUri", googlePlusId);
            context.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            // no Google+ app, revert to browser
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("https://plus.google.com/" + googlePlusId + "/posts"));
            context.startActivity(intent);
        }
    }

    /**LINKEDIN PROFILE**/
    public static Intent linkedInPageUrlIntent(Context context, String linkedInId) {
        /*Intent to open the official LinkedIn app to the user's profile. If the LinkedIn app is not
         installed then the Web Browser will be used.*/
        Intent intent;
        intent = new Intent(Intent.ACTION_VIEW, Uri.parse("linkedin://" + linkedInId));
        final PackageManager packageManager = context.getPackageManager();
        final List<ResolveInfo> list = packageManager.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);
        if (list.isEmpty()) {
            intent = new Intent(Intent.ACTION_VIEW, Uri.parse("http://www.linkedin.com/in/" + linkedInId));
        }
        return intent;
    }

    /**SNAPCHAT PROFILE**/
    public static Intent snapchatPageUrlIntent(Context context) {
        /*Intent to open the official Snapchat app to the user's profile. If the Snapchat app is not
         installed then the Web Browser will be used.*/
        Intent intent;
        intent = new Intent(Intent.ACTION_SEND);
        intent.setType("*/*");
        intent.setPackage("com.snapchat.android");
        return intent;
    }

}
