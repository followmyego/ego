package net.egobeta.ego;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.Toast;

import com.facebook.login.LoginManager;

import net.amazonaws.mobile.AWSMobileClient;
import net.amazonaws.mobile.user.IdentityManager;

/**
 * Created by Lucas on 28/06/2016.
 */
public class SettingsActivity extends AppCompatActivity implements View.OnClickListener {

    Context context;
    private static AlertDialog.Builder builder;
    Button logoutButton;
    IdentityManager identityManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Obtain a reference to the mobile client. It is created in the Application class.
        final AWSMobileClient awsMobileClient = AWSMobileClient.defaultMobileClient();

        // Obtain a reference to the identity manager.
        identityManager = awsMobileClient.getIdentityManager();

        setContentView(R.layout.activity_settings);
        context = this;
        logoutButton = (Button) findViewById(R.id.logout_button);
        logoutButton.setOnClickListener(this);
    }


    //Method to Logout the user. Clear user data and set loggedIn = to false on the LocalDataBase
    public void logout(View v) {
        Toast.makeText(SettingsActivity.this, "logout user", Toast.LENGTH_SHORT).show();
    }


    //Method to Logout the user. Clear user data and set loggedIn = to false on the LocalDataBase
    public void disconnectInstagramImages(View v) {
        Toast.makeText(SettingsActivity.this, "disconnect instagram images", Toast.LENGTH_SHORT).show();
//        Fragment_Main.clickConnectButton(SettingsActivity.this, context);

//        builder = new AlertDialog.Builder(context);
//        builder.setMessage("Disconnect from Instagram?")
//                .setCancelable(false)
//                .setPositiveButton("Yes",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                //TODO find out why the last execute argument is being passed through via mLocalDataBase
//                                new UpdateUserInstagramInfo(context, "no").execute(user.facebookId, mLocalDataBase.getInstagramID(), mLocalDataBase.getInstagramUsername());
//                                Fragment_Main.disconnectInstagramImages();
//                                finishActivity();
//                            }
//                        })
//                .setNegativeButton("No",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                dialog.cancel();
//                            }
//                        });
//        final AlertDialog alert = builder.create();
//        alert.show();
    }


    public void finishActivity(){
        this.finish();
    }

    @Override
    public void onClick(View view) {
        if(view == logoutButton){
            identityManager.signOut();
            startActivity(new Intent(this, SignInActivity.class));
            finish();
        }
    }
}
