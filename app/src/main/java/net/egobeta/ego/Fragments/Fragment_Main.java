package net.egobeta.ego.Fragments;

import android.app.Activity;

import android.content.Context;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;

import android.widget.AdapterView;

import android.widget.GridView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

//import com.dobmob.doblist.DobList;
//import com.dobmob.doblist.events.OnLoadMoreListener;
//import com.dobmob.doblist.exceptions.EmptyViewNotAttachedException;
//import com.dobmob.doblist.exceptions.ListViewNotAttachedException;
//import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
//import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
//import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;


//import net.egobeta.ego.databinding.FragmentListBinding;

import net.amazonaws.mobile.user.IdentityManager;
import net.egobeta.ego.Adapters.BadgeItem;
import net.egobeta.ego.Adapters.EgoStreamViewAdapter;
import net.egobeta.ego.Adapters.GenericAdapter;
import net.egobeta.ego.Adapters.UserItem;
import net.egobeta.ego.EgoMap;
import net.egobeta.ego.MainActivity;
import net.egobeta.ego.ProfileActivity;
import net.egobeta.ego.R;

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
import java.nio.charset.MalformedInputException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Lucas on 29/06/2016.
 */
public class Fragment_Main extends ScrollTabHolderFragment  {


    //Other variables
//    static DobList dobList = new DobList();
    private static final String ARG_POSITION = "position";
    static ArrayList<String> facebook_Ids = new ArrayList<String>();
    private static final String TAG = "DEBUGGING MESSAGE";
    public ScrollView scrollView;
    private static int mPosition;
    static Typeface typeface;
    public static String facebookId;
    private static GridView gridView;
    public static View v;
    private static EgoStreamViewAdapter adapter;
    private static ArrayList<String> friends_Ids;
    private static ArrayList<UserItem> userList = new ArrayList<UserItem>();
    private static EgoMap egoMap;
    private static String serverURL = "http://ebjavasampleapp-env.us-east-1.elasticbeanstalk.com/dynamodb-geo";
    static ProgressBar progressBar;


    //New Variables

    // FLAG FOR CURRENT PAGE
    public static int current_page = 0;

    // BOOLEAN TO CHECK IF NEW FEEDS ARE LOADING
    static Boolean loadingMore = true;

    Boolean stopLoadingData = false;

    Boolean noMoreUsersToLoad = false;
    private static Context context;
    private static Activity activity;


    public static Fragment newInstance(EgoMap egomap, IdentityManager identityManager,
                                       int position, ArrayList<String> friendsIds) {
        Fragment_Main f = new Fragment_Main();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        friends_Ids = friendsIds;
        facebookId = identityManager.getUserFacebookId();
        egoMap = egomap;
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
        mPosition = getArguments().getInt(ARG_POSITION);
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");

        //Initialize the adapter for the listView that holds the gridView
        userList.add(new UserItem(getContext(), null, 0)); //These two act as my invisible header
        userList.add(new UserItem(getContext(), null, 0)); //These two act as my invisible header
        userList.add(new UserItem(getContext(), facebookId, 0)); //These two act as my invisible header
        adapter = new EgoStreamViewAdapter(userList, getContext(), friends_Ids, getActivity(), facebookId, typeface);




    }



    /** check if the network is available **/
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.list_item_stream, container, false);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);

        gridView = (GridView) v.findViewById(R.id.myGridView);
        gridView.setAdapter(adapter);
        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                Bundle bundle = new Bundle();
                bundle.putString("facebook_id", userList.get(position).getFacebookId());
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

        // ONSCROLLLISTENER
        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {

            @Override
            public void onScrollStateChanged(AbsListView view, int scrollState) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (mScrollTabHolder != null)
                    //mScrollTabHolder is set to the ScrollTabHolder context from the Parent Activity holding this fragment since the parent activity implements scrolltabholder
                    //Since we are extending ScrollTabHolder fragment we are able to access this variable
                    mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
                int lastInScreen = firstVisibleItem + visibleItemCount;
                if ((lastInScreen == totalItemCount) && !(loadingMore)) {

                    if (!stopLoadingData) {
                        // FETCH THE NEXT BATCH OF FEEDS
                        if(!noMoreUsersToLoad){
                            new loadMorePhotos().execute();
                        }
                    }

                }
            }
        });
        if(egoMap.getLongitude() != 0.0 && egoMap.getLatitude() != 0.0 && current_page == 0){
            new PushUserLocationToDataBase().execute();
        }

        return v;
    }

    @Override
    public void adjustScroll(int scrollHeight) {

    }


    //AsyncTask to get profile pic url string from server
    public static class PushUserLocationToDataBase extends AsyncTask<String, Void, String> {



        public PushUserLocationToDataBase(){

        }

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // SHOW THE BOTTOM PROGRESS BAR (SPINNER) WHILE LOADING MORE PHOTOS
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... params) {
            // CHANGE THE LOADING MORE STATUS TO PREVENT DUPLICATE CALLS FOR
            // MORE DATA WHILE LOADING A BATCH
            loadingMore = true;


            try {
                java.net.URL url = new URL(serverURL);
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

                JSONObject requestParams   = new JSONObject();
                JSONObject parent = new JSONObject();

                requestParams.put("lat", egoMap.getLatitude());
                requestParams.put("lng", egoMap.getLongitude());
                System.out.println("Fragment LONGITUDE: " + egoMap.getLongitude());
                System.out.println("Fragment LATITUDE: " + egoMap.getLatitude());
//                requestParams.put("lat", 49.888721);
//                requestParams.put("lng", -119.491572);
                requestParams.put("count", current_page);
                requestParams.put("accessToken", "");
                requestParams.put("radiusInMeter", "500000");
                requestParams.put("debug", "androidApp");
                requestParams.put("rangeKey", facebookId);

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

                String json_response = response.toString().trim();
                String[] facebook_Ids1 = returnArrayOfFacebookIds(json_response);
                int[] badges = returnArrayOfBadges(json_response);

                /** Set the arrList of UserItems **/
                if(returnArrayOfFacebookIds(json_response) != null && returnArrayOfBadges(json_response) != null){
                    for (int i = 0; i < returnArrayOfFacebookIds(json_response).length; i++) {

                        //Create temporary list of ids
                        ArrayList<String> listOfIds = new ArrayList<String>();
                        for(int c = 0; c < userList.size(); c++){
                            listOfIds.add(userList.get(c).getFacebookId());
                        }

                        //Compare items, check to see what index is duplicate
                        if(!facebook_Ids1[i].equals(facebookId)){
                            if(listOfIds.contains(facebook_Ids1[i])){
                                userList.remove(listOfIds.indexOf(facebook_Ids1[i]));

                                UserItem userItem = new UserItem(context, facebook_Ids1[i], badges[i]);
                                userList.add(userItem);
                            } else {
                                UserItem userItem = new UserItem(context, facebook_Ids1[i], badges[i]);
                                userList.add(userItem);
                            }
                        }


                    }
                }


                return json_response;
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

            // HIDE THE BOTTOM PROGRESS BAR (SPINNER) AFTER LOADING MORE ALBUMS
            progressBar.setVisibility(View.GONE);

            //Print server AsyncTask response
            System.out.println("Resulted Value: " + result);

            // SET THE ADAPTER TO THE GRIDVIEW
            adapter = new EgoStreamViewAdapter(userList, context, friends_Ids, activity, facebookId, typeface);
            gridView.setAdapter(adapter);


            // CHANGE THE LOADING MORE STATUS
            loadingMore = false;


//            If null Response
            if (result != null && !result.equals("")) {
                if(!egoMap.mRequestLocationUpdates){
                    egoMap.startLocationUpdates();
                    egoMap.mRequestLocationUpdates = true;
                } else {

                }
            } else {
                if(!egoMap.mRequestLocationUpdates){
                    egoMap.mRequestLocationUpdates = true;
                }
            }
        }


    }





    private class loadMorePhotos extends AsyncTask<String, Void, String> {

        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            // SHOW THE BOTTOM PROGRESS BAR (SPINNER) WHILE LOADING MORE PHOTOS
            progressBar.setVisibility(View.VISIBLE);
        }

        @Override
        protected String doInBackground(String... arg0) {

            // SET LOADING MORE "TRUE"
            loadingMore = true;

            // INCREMENT CURRENT PAGE
            current_page += 1;

            try {
                java.net.URL url = new URL(serverURL);
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

                JSONObject requestParams   = new JSONObject();
                JSONObject parent = new JSONObject();

                requestParams.put("lat", egoMap.getLatitude());
                requestParams.put("lng", egoMap.getLongitude());
                System.out.println("Fragment LONGITUDE: " + egoMap.getLongitude());
                System.out.println("Fragment LATITUDE: " + egoMap.getLatitude());
//                requestParams.put("lat", 49.888721);
//                requestParams.put("lng", -119.491572);
                requestParams.put("count", current_page);
                requestParams.put("accessToken", "");
                requestParams.put("radiusInMeter", "500000");
                requestParams.put("debug", "androidApp");
                requestParams.put("rangeKey", facebookId);

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

                String json_response = response.toString().trim();
                String[] facebook_Ids1 = returnArrayOfFacebookIds(json_response);
                int[] badges = returnArrayOfBadges(json_response);


                /** Set the arrList of UserItems **/
                if(returnArrayOfFacebookIds(json_response) != null && returnArrayOfBadges(json_response) != null){
                    for (int i = 0; i < returnArrayOfFacebookIds(json_response).length; i++) {

                        //Create temporary list of ids
                        ArrayList<String> listOfIds = new ArrayList<String>();
                        for(int c = 0; c < userList.size(); c++){
                            listOfIds.add(userList.get(c).getFacebookId());
                        }

                        //Compare items, check to see what index is duplicate
                        if(!facebook_Ids1[i].equals(facebookId)){
                            if(listOfIds.contains(facebook_Ids1[i])){
                                userList.remove(listOfIds.indexOf(facebook_Ids1[i]));

                                UserItem userItem = new UserItem(context, facebook_Ids1[i], badges[i]);
                                userList.add(userItem);
                            } else {
                                UserItem userItem = new UserItem(context, facebook_Ids1[i], badges[i]);
                                userList.add(userItem);
                            }
                        }

                    }

                }
                return json_response;
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

            // HIDE THE BOTTOM PROGRESS BAR (SPINNER) AFTER LOADING MORE ALBUMS
            progressBar.setVisibility(View.GONE);

            //Print server AsyncTask response
            System.out.println("Resulted Value: " + result);

            if(result == null){
                Toast.makeText(getActivity(), "End of the stream", Toast.LENGTH_LONG).show();
            } else {

//            // get gridView current position - used to maintain scroll position
                int currentPosition = gridView.getFirstVisiblePosition();
//
//            // APPEND NEW DATA TO THE ArrayList AND SET THE ADAPTER TO THE gridView
//                adapter = new EgoStreamViewAdapter(userList, getContext(), friends_Ids, getActivity());
                adapter.notifyDataSetChanged();
//                gridView.setAdapter(adapter);
//
//            // Setting new scroll position
                gridView.setSelection(currentPosition + 1);
//

            }
//             SET loadingMore "FALSE" AFTER ADDING NEW FEEDS TO THE EXISTING LIST
            loadingMore = false;
        }

    }

    //Method to parse json result and get the value of the key "rangeKey"
    private static String[] returnArrayOfFacebookIds(String result){
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

    //Method to parse json result and get the value of the key "badge"
    private static int[] returnArrayOfBadges(String result){
        JSONObject resultObject = null;
        JSONArray arrayOfUsers = null;
        int[] badges = null;
        try {

            resultObject = new JSONObject(result);
            arrayOfUsers = resultObject.getJSONArray("result");

            badges = new int[arrayOfUsers.length()];
            for(int i = 0; i < arrayOfUsers.length(); ++i){
                JSONObject user = arrayOfUsers.getJSONObject(i);
                int badge = user.getInt("badge");
                badges[i] = badge;
            }
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return badges;
    }
}



