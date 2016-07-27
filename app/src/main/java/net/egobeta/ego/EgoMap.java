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
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBDeleteExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBQueryExpression;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.PaginatedQueryList;
import com.amazonaws.services.dynamodbv2.model.AttributeValue;
import com.amazonaws.services.dynamodbv2.model.ComparisonOperator;
import com.amazonaws.services.dynamodbv2.model.Condition;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.GooglePlayServicesUtil;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;

import net.amazonaws.mobile.AWSMobileClient;
import net.amazonaws.mobile.user.IdentityManager;
import net.egobeta.ego.demo.nosql.Book;
import net.egobeta.ego.demo.nosql.UserLocation;
import net.egobeta.ego.demo.nosql.User_Locations;

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
    int firstTime = 0;

    //Other variables
    private List<String> usersImages;
    private List<String> usernames;
    private static String serverURL = "http://ebjavasampleapp-env.us-east-1.elasticbeanstalk.com/dynamodb-geo";
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





            if(firstTime == 0){
                new PushUserLocationToDataBase(0).execute(serverURL, username);
                firstTime = 1;
            }

//            saveToDB();
            Toast.makeText(context, "Long: " + longitude + " Lat: " + latitude, Toast.LENGTH_SHORT).show();
            System.out.println("Long: " + longitude + " Lat: " + latitude + " facebookId: " + identityManager.getUserFacebookId());
        } else {
            //lblLocation.setText("Couldn't get the location. Make sure location is enabled on the device");
        }
    }

    public void PushLocation(int count){
        new PushUserLocationToDataBase(count).execute(serverURL);
    }


    protected synchronized void buildGoogleApiClient(){
        mGoogleApiClient = new GoogleApiClient.Builder(context)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(LocationServices.API).build();
    }


    protected void createLocationRequest(){
        mLocationRequest = new LocationRequest();
        int UPDATE_INTERVAL = 20000;
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

        int count;

        public PushUserLocationToDataBase(int count){
            this.count = count;
        }

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
                LucasHttpURLConnection.setRequestProperty("Content-Type", "application/json; charset=UTF-8");
                LucasHttpURLConnection.setRequestProperty("Accept", "application/json");
                //OutputStream to get response
                OutputStream outputStream = LucasHttpURLConnection.getOutputStream();
                BufferedWriter bufferedWriter = new BufferedWriter(new OutputStreamWriter(outputStream,"UTF-8"));

                /*String data =
                        URLEncoder.encode("username", "UTF-8") + "=" + URLEncoder.encode(params[1], "UTF-8")+"&"+
                                URLEncoder.encode("latitude", "UTF-8")+"="+URLEncoder.encode(params[2], "UTF-8")+"&"+
                                URLEncoder.encode("longitude", "UTF-8")+"="+URLEncoder.encode(params[3], "UTF-8");*/



                JSONObject requestParams   = new JSONObject();
                JSONObject parent = new JSONObject();

                requestParams.put("lat", getLatitude());
                requestParams.put("lng", getLongitude());
                requestParams.put("count", count);
                requestParams.put("radiusInMeter", "200");
                requestParams.put("debug", "androidApp");
                requestParams.put("rangeKey", identityManager.getUserFacebookId());


                parent.put("action", "put-point");
                parent.put("request", requestParams);

                System.out.println(parent.toString());
                bufferedWriter.write(parent.toString());
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
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //Print server AsyncTask response
            System.out.println("Resulted Value: " + result);



//            If null Response
            if (result != null && !result.equals("")) {
                if(!mRequestLocationUpdates){
                    usernames = new ArrayList<>();
                    usersImages = new ArrayList<>();
                    startLocationUpdates();
                    mRequestLocationUpdates = true;
//                    Fragment_Main.setUsers(returnArrayOfFacebookIds(result));
//                    returnParsedJsonArray(result);
//                    adapter = new EgoStreamViewAdapter2(context, returnArrayOfFacebookIds(result));
                    if(count == 0){
                        Fragment_Main.notfiyAdapterHasChanged(returnArrayOfFacebookIds(result));
                    } else {
                        Fragment_Main.addNewItems(returnArrayOfFacebookIds(result));
                    }
                    Fragment_Main.stopRefreshing();
                } else {
                    usernames = new ArrayList<>();
                    usersImages = new ArrayList<>();
//                    Fragment_Main.setUsers(returnArrayOfFacebookIds(result));
//                    returnParsedJsonArray(result);
//                    adapter.setUsers(returnArrayOfFacebookIds(result));
                    if(count == 0){
                        Fragment_Main.notfiyAdapterHasChanged(returnArrayOfFacebookIds(result));
                    } else {
                        Fragment_Main.addNewItems(returnArrayOfFacebookIds(result));
                    }

                    Fragment_Main.stopRefreshing();
                }
            } else {
                if(!mRequestLocationUpdates){
                    //startLocationUpdates();
//                    Toast.makeText(context, "Uh oh, looks like there was an error", Toast.LENGTH_LONG).show();
                    mRequestLocationUpdates = true;
                }
            }
        }

        //Method to parse json result and get the value of the key "image"
        private String[] returnArrayOfFacebookIds(String result){
            JSONObject resultObject = null;
            JSONArray arrayOfUsers = null;
            String[] facebookIds = null;
            try {

                resultObject = new JSONObject(result);
                arrayOfUsers = resultObject.getJSONArray("result");

                facebookIds = new String[arrayOfUsers.length()];
                for(int i = 0; i < arrayOfUsers.length(); ++i){
                    JSONObject user = arrayOfUsers.getJSONObject(i);
                    String id = user.getString("rangeKey");
                    facebookIds[i] = id;
                }
            } catch (JSONException e) {
                e.printStackTrace();
            }
            return facebookIds;
        }
    }









    //AsyncTask to get profile pic url string from server
    private class DeleteCurrentLocationOnDB extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
        }

        @Override
        protected String doInBackground(String... params) {


            User_Locations userToFind = new User_Locations();
            userToFind.setFacebookId(identityManager.getUserFacebookId());

            String queryString = identityManager.getUserFacebookId();

            Condition rangeKeyCondition = new Condition()
                    .withComparisonOperator(ComparisonOperator.EQ.toString())
                    .withAttributeValueList(new AttributeValue().withS(queryString.toString()));

            DynamoDBQueryExpression queryExpression = new DynamoDBQueryExpression()
                    .withRangeKeyCondition("rangeKey", rangeKeyCondition)
                    .withConsistentRead(false);

            PaginatedQueryList result = mapper.query(User_Locations.class, queryExpression);
// Do something with result.

            System.out.println("Returned GEOHASH" + result.get(0).toString());







//            User_Locations userToDelete = new User_Locations();
//            userToDelete.setFacebookId(identityManager.getUserFacebookId());
//
//            mapper.delete(userToDelete);



            return null;
        }

        @Override
        protected void onPostExecute(String result) {
            super.onPostExecute(result);

            //Print server AsyncTask response
            System.out.println("Delete method Resulted Value: " + result);

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



