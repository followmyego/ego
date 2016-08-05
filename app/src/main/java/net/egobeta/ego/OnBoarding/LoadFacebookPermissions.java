package net.egobeta.ego.OnBoarding;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ImageButton;

import net.egobeta.ego.MainActivity;
import net.egobeta.ego.R;

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

    private void initializeViewItems() {
        finishButton = (ImageButton) findViewById(R.id.fpFinishButton);
        finishButton.setOnClickListener(this);
    }


    @Override
    public void onClick(View v) {
        if(v == finishButton){
            Intent intent = new Intent(LoadFacebookPermissions.this, MainActivity.class);
            startActivity(intent);
            overridePendingTransition(R.anim.activity_close_scale_r, R.anim.activity_open_translate_r);

        }
    }
}
