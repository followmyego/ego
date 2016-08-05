package net.egobeta.ego.OnBoarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.ImageButton;

import com.facebook.AccessToken;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;

import net.egobeta.ego.MainActivity;
import net.egobeta.ego.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;

public class LoadFacebookPermissions extends AppCompatActivity implements View.OnClickListener {

    private ArrayList<String> privacyPreferences;

    //View items
    ImageButton finishButton;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.content_load_facebook_permissions);
        Intent intent = getIntent();
        privacyPreferences = intent.getStringArrayListExtra("privacy_preferences");

        initializeViewItems();

        String final_badge_selection = "";
        for (String item : privacyPreferences) {
            final_badge_selection = final_badge_selection + item + "\n";
        }
//        Toast.makeText(LoadFacebookPermissions.this, final_badge_selection, Toast.LENGTH_LONG).show();





    }

    private void getFacebookInfo() {
        String facebook_id;

        final Bundle parameters = new Bundle();
        parameters.putString("fields", "name,picture.type(large),user_birthday,user_location");
        final GraphRequest graphRequest = new GraphRequest(AccessToken.getCurrentAccessToken(), "me");
        graphRequest.setParameters(parameters);
        GraphResponse response = graphRequest.executeAndWait();

        JSONObject json = response.getJSONObject();
        try {
            facebook_id = json.getString("id");
//            userName = json.getString("name");
//            userImageUrl = json.getJSONObject("picture")
//                    .getJSONObject("data")
//                    .getString("url");
            System.out.println("FACEBOOK RESULT:  " + json.toString());

        } catch (final JSONException jsonException) {
            Log.e("LOGTAG",
                    "Unable to get Facebook user info. " + jsonException.getMessage() + "\n" + response,
                    jsonException);
            // Nothing much we can do here.
        }
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
}
