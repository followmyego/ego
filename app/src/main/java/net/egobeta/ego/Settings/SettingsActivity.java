package net.egobeta.ego.Settings;

import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.amazonaws.mobile.AWSMobileClient;
import net.amazonaws.mobile.user.IdentityManager;
import net.egobeta.ego.MainActivity;
import net.egobeta.ego.R;
import net.egobeta.ego.SignInActivity;

/**
 * Created by Lucas on 28/06/2016.
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    private static AlertDialog.Builder builder;
    IdentityManager identityManager;

    //View Item Variables
    Button logoutButton;
    Button blockedButton;
    Button helpCenterButton;
    Button feedbackButton;

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.d("ACT DEBUG", "SettingsActivity: OnDestroy");
    }

    Button generalButton;


    @Override
    protected void onPause() {
        super.onPause();
        Log.d("ACT DEBUG", "SettingsActivity: OnPause");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d("ACT DEBUG", "SettingsActivity: OnCreate");
        // Obtain a reference to the mobile client. It is created in the Application class.
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();
        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();
        setContentView(R.layout.activity_settings);
        context = this;

        initializeButtons();
        setButtonClickListeners();
    }



    private void initializeButtons() {
        logoutButton = (Button) findViewById(R.id.logout_button);
        blockedButton = (Button) findViewById(R.id.blocked_button);
        helpCenterButton = (Button) findViewById(R.id.help_center_button);
        feedbackButton = (Button) findViewById(R.id.feedback_button);
        generalButton = (Button) findViewById(R.id.general_button);
    }

    private void setButtonClickListeners() {
        logoutButton.setOnClickListener(this);
        blockedButton.setOnClickListener(this);
        helpCenterButton.setOnClickListener(this);
        feedbackButton.setOnClickListener(this);
        generalButton.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        if(view == logoutButton){
            identityManager.signOut();
            Intent intent = new Intent(this, SignInActivity.class);
            intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_NEW_TASK);
//            startActivity(new Intent(this, SignInActivity.class));
//            MainActivity.activity.finish();
//            this.finish();
            startActivity(intent);
        }

        if(view == blockedButton){
            Toast.makeText(SettingsActivity.this, "Your Blocked People", Toast.LENGTH_SHORT).show();
        }

        if(view == helpCenterButton){
            Toast.makeText(SettingsActivity.this, "The Help Center", Toast.LENGTH_SHORT).show();
        }

        if(view == feedbackButton){
            Toast.makeText(SettingsActivity.this, "Leave some feedback", Toast.LENGTH_SHORT).show();
        }

        if(view == generalButton){
            startActivity(new Intent(this, GeneralSettingsActivity.class));
            finish();
        }
    }
}
