package net.egobeta.ego;

import android.app.Activity;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import net.amazonaws.mobile.AWSMobileClient;
import net.amazonaws.mobile.user.IdentityManager;
import net.egobeta.ego.demo.nosql.UserLocation;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.net.URLEncoder;
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.List;



public class EgoMap implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, LocationListener {


    //Location/GPS services variables
    private static final int PLAY_SERVICES_RESOLUTION_REQUEST = 1000;
    private static final String TAG = EgoMap.class.getSimpleName();
    private Location mLastLocation;
    private GoogleApiClient mGoogleApiClient;
    private boolean mRequestLocationUpdates = false;
    private LocationRequest mLocationRequest;

    //Other variables
    private List<String> usersImages;
    private List<String> usernames;
    private String serverURL = "http://www.myegotest.com/android_user_api/index.php";
    private String googleAPI = "AIzaSyAyMXHOJdJg6Jjj64SZnmyxIaY2lWvKDC0";
    private Activity context;
    private String username = "username";
    double longitude;
    double latitude;
    UserLocation userLocation;
    IdentityManager identityManager;
    DynamoDBMapper mapper;

    public EgoMap(Activity context, IdentityManager identityManager, DynamoDBMapper mapper){
        this.context = context;
        this.identityManager = identityManager;
        this.mapper = mapper;
    }

    public void saveToDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AmazonClientException lastException = null;


                    userLocation = new UserLocation();
                    userLocation.setUserId(AWSMobileClient.defaultMobileClient().getIdentityManager().getCachedUserID());
                    userLocation.setFacebookId(identityManager.getUserFacebookId());
                    userLocation.setLongitude(getLongitude() + "");
                    userLocation.setLatitude(getLatitude() + "");

                    try {
                        mapper.save(userLocation);
                    } catch (final AmazonClientException ex) {
                        Log.e("AMAZON EXCEPTION", "Failed saving item : " + ex.getMessage(), ex);
                        lastException = ex;
                    }

                    if (lastException != null) {
                        // Re-throw the last exception encountered to alert the user.
                        throw lastException;
                    }

                } catch (final AmazonClientException ex) {
                    // The insertSampleData call already logs the error, so we only need to
                    // show the error dialog to the user at this point.



                    return;
                }
//                ThreadUtils.runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(context);
//                        dialogBuilder.setTitle(R.string.nosql_dialog_title_added_sample_data_text);
//                        dialogBuilder.setMessage(R.string.nosql_dialog_message_added_sample_data_text);
//                        dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
//                        dialogBuilder.show();
//                    }
//                });
            }
        }).start();
    }


    public void theOnCreateMethod(){
        if(checkPlayServices()){
            buildGoogleApiClient();
            createLocationRequest();
            displayLocation();
        }
    }

    public void theOnStartMethod(){
        if(mGoogleApiClient != null){
            mGoogleApiClient.connect();
        }
    }

    public void theOnResumeMethod() {
        checkPlayServices();
        if(mGoogleApiClient.isConnected() && mRequestLocationUpdates){
            startLocationUpdates();
        }
    }

    protected void theOnPauseMethod() {
        stopLocationUpdates();
    }

    public double getLongitude(){
        return longitude;
    }

    public double getLatitude(){
        return latitude;
    }





    private void displayLocation(){
        mLastLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(mLastLocation != null){
            longitude = mLastLocation.getLongitude();
            latitude = mLastLocation.getLatitude();
            //lblLocation.setText(latitude + " " + longitude);

            new PushUserLocationToDataBase().execute(serverURL, username, latitude + "", longitude + "");
            //new PushUserLocationToDataBase().execute(serverURL, username);
            saveToDB();
            Toast.makeText(context, "Long: " + longitude + " Lat: " + latitude, Toast.LENGTH_SHORT).show();
        } else {
            //lblLocation.setText("Couldn't get the location. Make sure location is enabled on the device");
        }
    }


    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }


    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        int UPDATE_INTERVAL = 10000;
        mLocationRequest.setInterval(UPDATE_INTERVAL);
        int FASTEST_INTERVAL = 5000;
        mLocationRequest.setFastestInterval(FASTEST_INTERVAL);
        mLocationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        //int DISPLACEMENT = 1000;
        //mLocationRequest.setSmallestDisplacement(DISPLACEMENT);
    }


    private boolean checkPlayServices(){
        int resultCode = GooglePlayServicesUtil.isGooglePlayServicesAvailable(context);
        if(resultCode != ConnectionResult.SUCCESS){
            if(GooglePlayServicesUtil.isUserRecoverableError(resultCode)){
                GooglePlayServicesUtil.getErrorDialog(resultCode, context, PLAY_SERVICES_RESOLUTION_REQUEST).show();
            } else {
                Toast.makeText(context, "This device is not supported", Toast.LENGTH_LONG).show();
            }
            return false;
        }
        return  true;
    }


    protected void startLocationUpdates(){
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
    }


    protected void stopLocationUpdates(){
        LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
    }


    @Override
    public void onConnected(Bundle bundle) {
        displayLocation();

        if(mRequestLocationUpdates){
            startLocationUpdates();
        }
    }


    @Override
    public void onConnectionSuspended(int i) {
        mGoogleApiClient.connect();
    }


    @Override
    public void onLocationChanged(Location location) {
        mLastLocation = location;

        //Toast.makeText(getApplicationContext(), "Location changed", Toast.LENGTH_SHORT).show();

        displayLocation();
    }


    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        Log.i(TAG, "Connection failed: " + connectionResult.getErrorCode());
    }


    //AsyncTask to get profile pic url string from server
    private class PushUserLocationToDataBase extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {
            try {
                URL url = new URL(params[0]);
                HttpURLConnection LucasHttpURLConnection = (HttpURLConnection)url.openConnection();
                LucasHttpURLConnection.setRequestMethod("POST");
                LucasHttpURLConnection.setDoOutput(true);
                LucasHttpURLConnection.setDoInput(true);
                LucasHttpURLConnection.setConnectTimeout(1000 * 6);
                LucasHttpURLConnection.setReadTimeout(1000 * 6);
                //OutputStream to get response
                OutputStream outputStream = LucasHttpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                String data =
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8")+"&"+
                                URLEncoder.encode("latitude", "UTF-8")+"="+URLEncoder.encode(params[2], "UTF-8")+"&"+
                                URLEncoder.encode("longitude", "UTF-8")+"="+URLEncoder.encode(params[3], "UTF-8");

                bufferedWriter.write(data);
                bufferedWriter.flush();
                bufferedWriter.close();
                outputStream.close();
                //InputStream to get response
                InputStream IS = LucasHttpURLConnection.getInputStream();
                BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(IS, "iso-8859-1"));
                StringBuilder response = new StringBuilder();
                String json;
                while( (json = bufferedReader.readLine()) != null){
                    response.append(json + "\n");
                    break;
                }
                bufferedReader.close();
                IS.close();
                LucasHttpURLConnection.disconnect();
                return response.toString().trim();
            } catch (MalformedInputException e){
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //Print server AsyncTask response
            System.out.println("Resulted Value: " + result);

            //If null Response
            if (result != null && !result.equals("")) {
                if(!mRequestLocationUpdates){
                    usernames = new ArrayList<>();
                    usersImages = new ArrayList<>();
                    //startLocationUpdates();
                    //Toast.makeText(EgoMap.this, "Location updated to server successfully", Toast.LENGTH_SHORT).show();
                    mRequestLocationUpdates = true;
//                    returnParsedJsonArray(result);
                } else {
                    usernames = new ArrayList<>();
                    usersImages = new ArrayList<>();
                    //Toast.makeText(EgoMap.this, "Location updated to server successfully", Toast.LENGTH_SHORT).show();
//                    returnParsedJsonArray(result);
                }
            } else {
                if(!mRequestLocationUpdates){
                    //startLocationUpdates();
                    //Toast.makeText(EgoMap.this, "Sorry, there was an error. Please try again", Toast.LENGTH_SHORT).show();
                    mRequestLocationUpdates = true;
                } else {
                    //Toast.makeText(EgoMap.this, "Sorry, there was an error. Please try again", Toast.LENGTH_SHORT).show();
                }
            }
        }

        //Method to parse json result and get the value of the key "image"
        private int returnParsedJsonInt(String result){
            JSONObject resultObject = null;
            int returnedResult = 0;
            try {
                resultObject = new JSONObject(result);
                returnedResult = resultObject.getInt("success");
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return returnedResult;
        }

        //Method to parse json result and get the value of the key "image"
        private void returnParsedJsonArray(String result){
            List<String> list = null;
            try {
                JSONArray arr = new JSONArray(result);

                for(int i = 0; i < arr.length(); i++){
                    usersImages.add(arr.getJSONObject(i).getString("images"));
                    usernames.add(arr.getJSONObject(i).getString("usernames"));

                    Log.d("USERNAMES", usernames.get(i));
                    Log.d("USERIMAGES", usersImages.get(i));
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }


    class Userinfo{
        String profilePiciture;
        String user_id;

        Userinfo(String pic, String id){
            profilePiciture = pic;
            user_id = id;
        }
    }


    public class ViewHolder{
        ImageView usersProfilePic;

        ViewHolder(View v){
            usersProfilePic = new ImageView(null);
        }
    }

}



