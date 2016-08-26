package net.egobeta.ego.Fragments;

import android.app.Activity;

import android.content.Context;

import android.content.Intent;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;

import android.widget.AdapterView;

import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.dobmob.doblist.DobList;
import com.dobmob.doblist.events.OnLoadMoreListener;
import com.dobmob.doblist.exceptions.EmptyViewNotAttachedException;
import com.dobmob.doblist.exceptions.ListViewNotAttachedException;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;


//import net.egobeta.ego.databinding.FragmentListBinding;

import net.amazonaws.mobile.user.IdentityManager;
import net.egobeta.ego.Adapters.BadgeItem;
import net.egobeta.ego.Adapters.EgoStreamViewAdapter;
import net.egobeta.ego.Adapters.GenericAdapter;
import net.egobeta.ego.Adapters.UserItem;
import net.egobeta.ego.EgoMap;
import net.egobeta.ego.ImportedClasses.NonScrollableGridView;
import net.egobeta.ego.MainActivity;
import net.egobeta.ego.ProfileActivity;
import net.egobeta.ego.R;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;


/**
 * Created by Lucas on 29/06/2016.
 */
public class Fragment_Main extends ScrollTabHolderFragment implements SwipyRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {

    static DobList dobList = new DobList();
    private static final String ARG_POSITION = "position";
    static int egoStreamPosition = 0;
    static int egoFriendsPosition = 1;
    public static final int DISMISS_TIMEOUT = 2000;

    static SwipyRefreshLayout swipeRefreshLayout;
    //User profile info variables

    private static int count = 0;
    //Other variables
    static ArrayList<String> facebook_Ids = new ArrayList<String>();
    SwipyRefreshLayout swipyRefreshLayout;
//    private ActivityMainBinding mBinding;
    IdentityManager identityManager = null;
    private static ListView mListView;
    private ArrayList<String> mListItems;
    private static final String TAG = "DEBUGGING MESSAGE";
    private String interestSuggestionsUrlAddress = "http://www.myegotest.com/android_user_api/searcher.php";
    private String getUserInterestsUrlAddress =  "http://www.myegotest.com/android_user_api/getUserInterests.php";
    public ScrollView scrollView;
    static int deleteCounter = 0;
    private static int mPosition;
    private Typeface typeface;
    public static String facebookId;

    //Instagram GridView view Variables
    private static NonScrollableGridView gridView;

    public static View v;
    private static Toolbar toolbar;

    private int scrollHeight;
//    EgoStreamViewAdapter adapter;
//    private static FragmentListBinding mBinding;

    static EgoStreamViewAdapter adapter_Grid;

    private static ItemAdapter adapter;
    SlidingMenu slidingMenu;
    private static ArrayList<String> friends_Ids;
    private static List<UserItem> userList = new ArrayList<UserItem>();
    private static EgoMap egoMap;

    public static Fragment newInstance(EgoMap egomap, IdentityManager identityManager,
                                       int position, Toolbar toolbar1, ArrayList<String> friendsIds) {
        Fragment_Main f = new Fragment_Main();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        toolbar = toolbar1;
        f.setArguments(b);
        friends_Ids = friendsIds;
        facebookId = identityManager.getUserFacebookId();
        egoMap = egomap;
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(ARG_POSITION);
        typeface = Typeface.createFromAsset(getContext().getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");

        //Initialize the adapter for the listView that holds the gridView
        adapter = new ItemAdapter();

        //Initialize the adapter for gridView with empty variables. We will populate variables later.
//        UserItem userItem = new UserItem(context, facebookId);
//        userList.add(userItem);

    }



    /** check if the network is available **/
    private boolean isNetworkAvailable(){
        ConnectivityManager connectivityManager = (ConnectivityManager) getContext().getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_list, container, false);
        mListView = (ListView) v.findViewById(R.id.listView);
        View placeHolderView = inflater.inflate(R.layout.view_header_placeholder, mListView, false);
        mListView.addHeaderView(placeHolderView);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);

        initDobList(v, mListView);
        adapter.addItem("Some item");
        mListView.setAdapter(adapter);


        swipeRefreshLayout = (SwipyRefreshLayout) v.findViewById(R.id.swipyrefreshlayout);
        swipeRefreshLayout.setOnRefreshListener(this);
        return v;
    }

    /** Method to Initialize the auto loader when scrolled to the bottom of the list **/
    private void initDobList(View rootView, ListView listView) {
        System.out.println("SOUT" + " initDobList");

        // DobList initializing
        dobList = new DobList();
        dobList.setOnScrollListener(this);
        try {

            // Register ListView
            //
            // NoListViewException will be thrown when
            // there is no ListView
            dobList.register(listView);

            // Add ProgressBar to footers of ListView
            // to be shown in loading more
            dobList.addDefaultLoadingFooterView();

            // Sets the view to show if the adapter is empty
            // see startCentralLoading() method
            View noItems = rootView.findViewById(R.id.noItems);
            dobList.setEmptyView(noItems);

            // Callback called when reaching last item in ListView
            dobList.setOnLoadMoreListener(new OnLoadMoreListener() {

                @Override
                public void onLoadMore(final int totalItemCount) {
                    Log.i(TAG, "onStart totalItemCount " + totalItemCount);
                    Toast.makeText(getActivity(), "Loading more...", Toast.LENGTH_SHORT).show();
                    // Just inserting some dummy data after
                    // period of time to simulate waiting
                    // data from server
                    System.out.println("SOUT" + " onLoadMore");
                    checkIfWeAddItemToListView(1, false);
                }
            });

        } catch (ListViewNotAttachedException e) {
            e.printStackTrace();
        }

        try {
            // Show ProgressBar at the center of ListView
            // this can be used while loading data from
            // server at the first time
            //
            // setEmptyView() must be called before
            //
            // NoEmptyViewException will be thrown when
            // there is no EmptyView
            dobList.startCentralLoading();

        } catch (EmptyViewNotAttachedException e) {
            e.printStackTrace();
        }
        // Simulate adding data at the first time
//        checkIfWeAddItemToListView(3);
    }

    /** Make sure the proper conditions are met before we add an item to the listView **/
    protected static void checkIfWeAddItemToListView(final int itemsCount, boolean bool) {
        System.out.println("SOUT" + " checkIfWeAddItemToListView");
        System.out.println("MAINACTIVITY: getNearbyUsers checkIfWeAddItemToListView");
        if(!bool){
            if(count > 0){
                System.out.println("SOUT" + " checkIfWeAddItemToListView 1");
                MainActivity.getNearbyUsers(count, egoMap);
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        // We must call finishLoading when finishing adding data
                        dobList.finishLoading(true);
                    }
                }, 1000);
            } else {
                System.out.println("SOUT" + " checkIfWeAddItemToListView 2");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        addItemsToListView(adapter.getCount(), adapter.getCount() + itemsCount);

                        // We must call finishLoading
                        // when finishing adding data
                        dobList.finishLoading(true);
                    }
                }, 1000);
            }
        } else {
            System.out.println("SOUT" + " checkIfWeAddItemToListView 3");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    addItemsToListView(adapter.getCount(), adapter.getCount() + itemsCount);

                    // We must call finishLoading
                    // when finishing adding data
                    dobList.finishLoading(true);
                }
            }, 1000);
        }

    }

    /** method to add an item to the listview adapter **/
    protected static void addItemsToListView(int from, int to) {
        System.out.println("SOUT" + " addItemsToListView");
        String strPlainItem = "Some string";
        for (int i = from; i < to; i++) {
            String strItem = String.format(strPlainItem, i);
            adapter.addItem(strItem);
        }
    }


    /** ONLY CALLED FROM EGOMAP CLASS **/
    /** Method to add items to gridView adapter if its the first time getting users **/
    public static void notifiyAdapterHasChanged(String[] facebook_Ids1, Context context, Activity activity, int[] badges){
        System.out.println("SOUT" + " notifiyAdapterHasChanged");
        facebook_Ids = new ArrayList<>(Arrays.asList(facebook_Ids1));
        for (int i = 0; i < facebook_Ids1.length; i++) {
            UserItem userItem = new UserItem(context, facebook_Ids1[i], badges[i]);
            userList.add(userItem);
        }
        adapter_Grid = new EgoStreamViewAdapter(userList, context, friends_Ids, activity);
        gridView.setAdapter(adapter_Grid);
//        gridView.invalidateViews();
        checkIfWeAddItemToListView(1, false);
        count ++;
    }

    public static void addNewItems(String[] facebook_Ids1, Context context, Activity activity, int[] badges) {
        System.out.println("SOUT" + " addNewItems");

        for (int i = 0; i < facebook_Ids1.length; i++) {
            UserItem userItem = new UserItem(context, facebook_Ids1[i], badges[i]);
            userList.add(userItem);
        }
        adapter_Grid = new EgoStreamViewAdapter(userList, context, friends_Ids, activity);
//        adapter_Grid.addAllItems(userList);
        adapter_Grid.notifyDataSetChanged();
        gridView.setAdapter(adapter_Grid);
//        gridView.invalidateViews();
        checkIfWeAddItemToListView(1, true);
        count ++;
    }


    public static void stopRefreshing(Activity activity){
        //Hide the refresh after 2sec
        activity.runOnUiThread(new Runnable() {
            @Override
            public void run() {
                swipeRefreshLayout.setRefreshing(false);
            }
        });
    }

    @Override
    public void onRefresh(SwipyRefreshLayoutDirection direction) {
        Log.d("MainActivity", "Refresh triggered at "
                + (direction == SwipyRefreshLayoutDirection.TOP ? "top" : "bottom"));
        if(direction == SwipyRefreshLayoutDirection.TOP){

            count = 0;
            facebook_Ids = new ArrayList<String>();
            userList = new ArrayList<UserItem>();
//        adapter = new EgoStreamViewAdapter(context, friends_Ids);
            adapter_Grid.setItems(userList);
            gridView.invalidateViews();
            adapter = new ItemAdapter();
            initDobList(v, mListView);
            adapter.addItem("Some item");
            mListView.setAdapter(adapter);

        }
        System.out.println("MAINACTIVITY: getNearbyUsers onRefresh");
        MainActivity.getNearbyUsers(count, egoMap);
    }



    @Override
    public void adjustScroll(int scrollHeight) {
        this.scrollHeight = scrollHeight;
        if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
            return;
        }
//        mListView.setSelectionFromTop(1, scrollHeight);
        mListView.setSelectionFromTop(1, 0);
    }


    @Override
    public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
        if (mScrollTabHolder != null)
            mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {
        // nothing
    }

    public void runthismethod(){}

    //Method to show users instagram photos
    public void gridViewInitializer(View convertView){
        System.out.println("SOUT" + " gridViewInitializer");
//        setUsersForFriendsTab();
        //Initialize the gridview
        gridView = (NonScrollableGridView) convertView.findViewById(R.id.myGridView);
        gridView.setVisibility(View.VISIBLE);


        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("facebook_id", userList.get(position).getFacebookId());
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
//                Toast.makeText(getActivity(), "item number: " + position, Toast.LENGTH_SHORT).show();
            }
        });

    }






    public class ItemAdapter extends GenericAdapter<String> {

        private List<BadgeItem> badgeList;

        public ItemAdapter() {
            super(getContext());
        }



        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            if(position == 0){
                System.out.println("SOUT" + " ItemAdapter1");

                convertView = layoutInflater.inflate(R.layout.list_item_stream, null);
                //Initialize the gridview
                gridViewInitializer(convertView);

                return convertView;
            } else {
                System.out.println("SOUT" + " ItemAdapter2");
                convertView = layoutInflater.inflate(R.layout.list_item, null);
                TextView title = (TextView) convertView.findViewById(R.id.text1);
                title.setText(" ");
                return convertView;
            }
        }

    }
}



