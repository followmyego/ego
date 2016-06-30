package net.egobeta.ego;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Toast;

import com.facebook.login.LoginManager;

/**
 * Created by Lucas on 28/06/2016.
 */
public class SettingsActivity extends AppCompatActivity {

    Context context;
    private static AlertDialog.Builder builder;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);
        context = this;
    }


    //Method to Logout the user. Clear user data and set loggedIn = to false on the LocalDataBase
    public void logout(View v) {
        Toast.makeText(SettingsActivity.this, "logout user", Toast.LENGTH_SHORT).show();
    }


    //Method to Logout the user. Clear user data and set loggedIn = to false on the LocalDataBase
    public void disconnectInstagramImages(View v) {
        Toast.makeText(SettingsActivity.this, "disconnect instagram images", Toast.LENGTH_SHORT).show();
//        SampleListFragment.clickConnectButton(SettingsActivity.this, context);

//        builder = new AlertDialog.Builder(context);
//        builder.setMessage("Disconnect from Instagram?")
//                .setCancelable(false)
//                .setPositiveButton("Yes",
//                        new DialogInterface.OnClickListener() {
//                            public void onClick(DialogInterface dialog, int id) {
//                                //TODO find out why the last execute argument is being passed through via mLocalDataBase
//                                new UpdateUserInstagramInfo(context, "no").execute(user.facebookId, mLocalDataBase.getInstagramID(), mLocalDataBase.getInstagramUsername());
//                                SampleListFragment.disconnectInstagramImages();
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
}
