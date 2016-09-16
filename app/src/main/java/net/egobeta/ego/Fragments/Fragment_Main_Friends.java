package net.egobeta.ego.Fragments;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.Dialog;
import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.view.Window;
import android.widget.AbsListView;

import android.widget.AdapterView;


import android.widget.Button;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import net.amazonaws.mobile.user.IdentityManager;
import net.egobeta.ego.Adapters.EgoStreamViewAdapter;
import net.egobeta.ego.Adapters.UserItem;
import net.egobeta.ego.EgoMap;

import net.egobeta.ego.ProfileActivity;
import net.egobeta.ego.R;
import java.util.ArrayList;



/**
 * Created by Lucas on 29/06/2016.
 */
public class Fragment_Main_Friends extends ScrollTabHolderFragment implements SwipeRefreshLayout.OnRefreshListener {

    //Other variables
    private static final String ARG_POSITION = "position";
    public ScrollView scrollView;
    private static int mPosition;
    Typeface typeface;
    public static String facebookId;
    private static GridView gridView;
    public static View v;
    private static EgoStreamViewAdapter adapter;
    private static ArrayList<String> friends_Ids = new ArrayList<>();
    private static ArrayList<UserItem> userList = new ArrayList<UserItem>();
    private Context context;
    private Activity activity;
    private SwipeRefreshLayout swipeRefreshLayout;
    private ProgressBar progressBar;


    public static Fragment newInstance(IdentityManager identityManager,
                                       int position, ArrayList<String> friendsIds) {
        Fragment_Main_Friends f = new Fragment_Main_Friends();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        f.setArguments(b);
        friends_Ids = friendsIds;
        facebookId = identityManager.getUserFacebookId();
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();
        mPosition = getArguments().getInt(ARG_POSITION);
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");

        /** Initialize UserItems from ArrayList of friends facebook ids **/
        userList.add(new UserItem(getContext(), null, 0, false)); //These two act as my invisible header
        userList.add(new UserItem(getContext(), null, 0, false)); //These two act as my invisible header
        for (int i = 0; i < friends_Ids.size(); i++) {
            UserItem userItem = new UserItem(context, friends_Ids.get(i), 0, false);
            userList.add(userItem);
        }

        //Initialize the adapter for the listView that holds the gridView
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
        swipeRefreshLayout = (SwipeRefreshLayout) v.findViewById(R.id.swiperefresh);
        swipeRefreshLayout.setOnRefreshListener(this);
        progressBar = (ProgressBar) v.findViewById(R.id.progressBar);
        progressBar.setVisibility(View.GONE);
        ImageView addFriendsImage = (ImageView) v.findViewById(R.id.inviteFriendsButton);
        Button addFriendsButton = (Button) v.findViewById(R.id.Button);
        Button button = (Button) v.findViewById(R.id.addMoreFriendsButton);

        if(friends_Ids.size() == 0){
            RelativeLayout relativeLayout = (RelativeLayout) v.findViewById(R.id.noFriendsLayout);
            relativeLayout.setVisibility(View.VISIBLE);
            TextView noFriendsText = (TextView) v.findViewById(R.id.noFriendsText);
            noFriendsText.setTypeface(typeface);
            button.setVisibility(View.VISIBLE);

        }

        addFriendsImage.setVisibility(View.VISIBLE);
        addFriendsButton.setVisibility(View.VISIBLE);
        addFriendsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inviteFriends();
            }
        });
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                inviteFriends();
            }
        });

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

        gridView.setOnScrollListener(new AbsListView.OnScrollListener() {
            @Override
            public void onScrollStateChanged(AbsListView absListView, int i) {

            }

            @Override
            public void onScroll(AbsListView view, int firstVisibleItem,
                                 int visibleItemCount, int totalItemCount) {
                if (mScrollTabHolder != null)
                    //mScrollTabHolder is set to the ScrollTabHolder context from the Parent Activity holding this fragment since the parent activity implements scrolltabholder
                    //Since we are extending ScrollTabHolder fragment we are able to access this variable
                    mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
            }
        });

        return v;
    }

    @Override
    public void adjustScroll(int scrollHeight) {

    }

    @Override
    public void onRefresh() {
        swipeRefreshLayout.setRefreshing(false);
        // show custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_toggle_stream);


        // set the custom dialog components
        TextView title = (TextView) dialog.findViewById(R.id.title);
        Button yes_button = (Button) dialog.findViewById(R.id.yes);
        Button cancel_button = (Button) dialog.findViewById(R.id.cancel);

        //Set the text of the title
        title.setText("Invite your friends to ego?");

        /** Set the custom dialog view item fonts **/
        title.setTypeface(typeface);
        yes_button.setTypeface(typeface);
        cancel_button.setTypeface(typeface);

        // if button is clicked, close the custom dialog
        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Code to turn off the Ego Stream **/
                swipeRefreshLayout.setRefreshing(false);
                dialog.dismiss();
                Toast.makeText(getActivity(), "This feature is not ready yet", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "You can invite your friends by sending them your link you used to join", Toast.LENGTH_LONG).show();

            }
        });

        // if button is clicked, close the custom dialog
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }


    public void inviteFriends(){
        // show custom dialog
        final Dialog dialog = new Dialog(getActivity());
        dialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
        dialog.setContentView(R.layout.custom_dialog_toggle_stream);


        // set the custom dialog components
        TextView title = (TextView) dialog.findViewById(R.id.title);
        Button yes_button = (Button) dialog.findViewById(R.id.yes);
        Button cancel_button = (Button) dialog.findViewById(R.id.cancel);

        //Set the text of the title
        title.setText("Invite your friends to ego?");

        /** Set the custom dialog view item fonts **/
        title.setTypeface(typeface);
        yes_button.setTypeface(typeface);
        cancel_button.setTypeface(typeface);

        // if button is clicked, close the custom dialog
        yes_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                /** Code to turn off the Ego Stream **/
                swipeRefreshLayout.setRefreshing(false);
                dialog.dismiss();
                Toast.makeText(getActivity(), "This feature is not ready yet", Toast.LENGTH_SHORT).show();
                Toast.makeText(getActivity(), "You can invite your friends by sending them your link you used to join", Toast.LENGTH_LONG).show();

            }
        });

        // if button is clicked, close the custom dialog
        cancel_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dialog.dismiss();
            }
        });

        dialog.show();
    }
}
