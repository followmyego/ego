package net.egobeta.ego.OnBoarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import net.egobeta.ego.R;

import java.util.ArrayList;

public class LoadFacebookPermissions extends AppCompatActivity {

    private ArrayList<String> privacyPreferences;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_load_facebook_permissions);
        Intent intent = getIntent();
        privacyPreferences = intent.getStringArrayListExtra("privacy_preferences");



        String final_badge_selection = "";
        for (String item : privacyPreferences) {
            final_badge_selection = final_badge_selection + item + "\n";
        }
        Toast.makeText(LoadFacebookPermissions.this, final_badge_selection, Toast.LENGTH_LONG).show();
    }

}
