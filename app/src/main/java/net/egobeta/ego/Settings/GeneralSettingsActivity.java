package net.egobeta.ego.Settings;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import net.egobeta.ego.R;

public class GeneralSettingsActivity extends AppCompatActivity implements View.OnClickListener {

    //View Item Variables
    Button privacySettingsButton;
    Button privacyTermsButton;
    Button termsAndConditionsButton;
    Button deleteAccountButton;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_general_settings);

        initializeButtons();
        setButtonClickListeners();
    }



    private void initializeButtons() {
        privacySettingsButton = (Button) findViewById(R.id.privacy_settings_button);
        privacyTermsButton = (Button) findViewById(R.id.privacy_terms_button);
        termsAndConditionsButton = (Button) findViewById(R.id.terms_and_conditions_button);
        deleteAccountButton = (Button) findViewById(R.id.delete_account_button);

    }

    private void setButtonClickListeners() {
        privacySettingsButton.setOnClickListener(this);
        privacyTermsButton.setOnClickListener(this);
        termsAndConditionsButton.setOnClickListener(this);
        deleteAccountButton.setOnClickListener(this);

    }

    @Override
    public void onClick(View view) {
        if(view == privacySettingsButton){
            Toast.makeText(GeneralSettingsActivity.this, "Change your privacy settings", Toast.LENGTH_SHORT).show();
        }

        if(view == privacyTermsButton){
            Toast.makeText(GeneralSettingsActivity.this, "Read our privacy terms", Toast.LENGTH_SHORT).show();
        }

        if(view == termsAndConditionsButton){
            Toast.makeText(GeneralSettingsActivity.this, "Read our terms and conditions", Toast.LENGTH_SHORT).show();
        }

        if(view == deleteAccountButton){
            Toast.makeText(GeneralSettingsActivity.this, "Delete your account", Toast.LENGTH_SHORT).show();
        }
    }
}
