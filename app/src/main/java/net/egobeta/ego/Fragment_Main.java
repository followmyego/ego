package net.egobeta.ego;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
import android.databinding.DataBindingUtil;
import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.widget.Toolbar;

import android.support.v8.renderscript.ScriptGroup;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayout;
import com.orangegangsters.github.swipyrefreshlayout.library.SwipyRefreshLayoutDirection;


//import net.egobeta.ego.databinding.FragmentListBinding;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.TreeSet;


/**
 * Created by Lucas on 29/06/2016.
 */
public class Fragment_Main extends ScrollTabHolderFragment implements SwipyRefreshLayout.OnRefreshListener, AbsListView.OnScrollListener {


    private static final String ARG_POSITION = "position";
    static int egoStreamPosition = 0;
    static int egoFriendsPosition = 1;
    public static final int DISMISS_TIMEOUT = 2000;

    static SwipyRefreshLayout swipeRefreshLayout;
    private EgoStreamTabAdapter myListAdapter;
    //User profile info variables
    private String facebookId;
    private int count = 0;
    //Other variables
    static ArrayList<String> facebook_Ids = new ArrayList<String>();
//    SwipyRefreshLayout swipyRefreshLayout;
//    private ActivityMainBinding mBinding;

    private ListView mListView;
    private static TextView loadingMessage;
    private ArrayList<String> mListItems;
    private static final String TAG = "DEBUGGING MESSAGE";
    private String interestSuggestionsUrlAddress = "http://www.myegotest.com/android_user_api/searcher.php";
    private String getUserInterestsUrlAddress =  "http://www.myegotest.com/android_user_api/getUserInterests.php";
    public ScrollView scrollView;

    private static int mPosition;
    public static Context context;
    static Activity activity;
    private Typeface typeface;

    //Instagram GridView view Variables
    private static NonScrollableGridView gridView;

    public static View v;
    private static Toolbar toolbar;

    private int scrollHeight;
//    EgoStreamViewAdapter adapter;
//    private static FragmentListBinding mBinding;

    static EgoStreamViewAdapter adapter;


    SlidingMenu slidingMenu;

    //Instagram stuffs
    private static String instagramProfileLinked;
    private static String instagramId;
    private static String instagramUsername;
    private static ArrayList<String> facebookProfileIds = new ArrayList<String>();
    private int WHAT_FINALIZE = 0;
    private static int WHAT_ERROR = 1;
    private ProgressDialog pd;
    public static final String TAG_DATA = "data";
    public static final String TAG_IMAGES = "images";
    public static final String TAG_THUMBNAIL = "standard_resolution";
    public static final String TAG_URL = "url";
    public JSONObject jObj = null;
    Integer[] imageId = new Integer[10];
    private Button btnConnect;
    private HashMap<String, String> instagramUserInfoHashmap = new HashMap<String, String>();


    public static Fragment newInstance(Activity activtiy, Context context1, int position, Toolbar toolbar1) {
        activity = activtiy;
        Fragment_Main f = new Fragment_Main();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        context = context1;
        toolbar = toolbar1;
        f.setArguments(b);
//        mBinding = mBindingg;
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(ARG_POSITION);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");
//		/**Not Currently using this data set**/
//		mListItems = new ArrayList<String>();
//		for (int i = 1; i <= 15; i++) {
//			mListItems.add(i + ". item - currnet page: " + (mPosition + 1));
//		}
        /**Add 30 strings to the ArrayList<Strings> to load 30 fake profiles to the stream for testing **/
        for(int i = 0; i < 10; i++){
            facebookProfileIds.add("facebookProfileId " + i);
        }



//        getUsersAroundUs();
        //Create adapter for instagram images and horizontal image sliding view
        adapter = new EgoStreamViewAdapter(context, facebook_Ids);

    }


    //This function send our location and pull the users that are around us from the database
    public void getUsersAroundUs(){
        /**Fake array of facebook ids that are around us to mimic loading users from the database
        //based on location **/
        String[] user_facebookIds = {
                "699211431",
                "531370423",
                "100008170621778"

        };

            int lengthOfUsers = user_facebookIds.length;


            for (int i = 0; i < lengthOfUsers; i++) {
                facebook_Ids.add(user_facebookIds[i]);
            }
    }

    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        v = inflater.inflate(R.layout.fragment_list, null);
        mListView = (ListView) v.findViewById(R.id.listView);
        View placeHolderView = inflater.inflate(R.layout.view_header_placeholder, mListView, false);
        mListView.addHeaderView(placeHolderView);
        mListView.setDivider(null);
        mListView.setDividerHeight(0);

        loadingMessage = (TextView) v.findViewById(R.id.loading_message);

        myListAdapter = new EgoStreamTabAdapter();
        myListAdapter.addSeparatorItem("separator " + 1);
        mListView.setAdapter(myListAdapter);
        mListView.setOnScrollListener(this);
        //        mListView.setOnScrollListener(new EndlessScrollerListener() {
//            @Override
//            public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
//                if (mScrollTabHolder != null)
//                    mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
//            }
//
//            @Override
//            public void onScrollStateChanged(AbsListView view, int scrollState) {
//                // nothing
//            }
//
//            @Override
//            public boolean onLoadMore(int page, int totalItemsCount) {
//                // Triggered only when new data needs to be appended to the list
//                // Add whatever code is needed to append new items to your AdapterView
//                MainActivity.getNearbyUsers(count);
//                count += 1;
//                Toast.makeText(getActivity(), "LoadMore is triggered", Toast.LENGTH_SHORT).show();
//                // or customLoadMoreDataFromApi(totalItemsCount);
//                return true; // ONLY if more data is actually being loaded; false otherwise.
//            }
//        });

        swipeRefreshLayout = (SwipyRefreshLayout) v.findViewById(R.id.swipyrefreshlayout);
//        mBinding = DataBindingUtil.setContentView(this, R.layout.activity_main);
//        mBinding = DataBindingUtil.setContentView(activity, R.layout.fragment_list);
        swipeRefreshLayout.setOnRefreshListener(this);
        return v;
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
//        setUsersForFriendsTab();
        //Initialize the gridview
        gridView = (NonScrollableGridView) convertView.findViewById(R.id.myGridView);
        gridView.setVisibility(View.VISIBLE);
        gridView.setAdapter(adapter);

        gridView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Bundle bundle = new Bundle();
                bundle.putString("facebook_id", facebook_Ids.get(position));
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
//                Toast.makeText(getActivity(), "item number: " + position, Toast.LENGTH_SHORT).show();
            }
        });

    }

    //Method to set height of the interests ListView
    public static void setGridViewHeightBasedOnChildren(NonScrollableGridView gridView) {
        EgoStreamViewAdapter2 listAdapter = (EgoStreamViewAdapter2) gridView.getAdapter();
        if (listAdapter == null) {
            // pre-condition
            return;
        }

        int totalHeight = 0;
        for (int i = 0; i < listAdapter.getCount(); i++) {
            View listItem = listAdapter.getView(i, null, gridView);
            listItem.measure(0, 0);
            totalHeight += listItem.getMeasuredHeight();
        }

        ViewGroup.LayoutParams params = gridView.getLayoutParams();
        params.height = totalHeight + ((listAdapter.getCount() - 1));
        gridView.setLayoutParams(params);
        gridView.requestLayout();
    }

    public static void notifiyAdapterHasChanged(String[] facebook_Ids1){

        facebook_Ids = new ArrayList<>(Arrays.asList(facebook_Ids1));
//        adapter = new EgoStreamViewAdapter(context, facebook_Ids);
        adapter.setItems(facebook_Ids);
//        gridView.setAdapter(adapter);
//        facebook_Ids = facebook_Ids1;
//        setGridViewHeightBasedOnChildren(gridView);
//        adapter.setUsers(facebook_Ids1);
//        adapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    public static void addNewItems(String[] facebook_Ids1) {
        ArrayList<String> newList = new ArrayList<>(Arrays.asList(facebook_Ids1));
        for (int i = 0; i < 16; i++) {
            facebook_Ids.add(newList.get(i));
//            adapter.addItem(newList.get(i));
        }

        adapter.notifyDataSetChanged();
        gridView.invalidateViews();
    }

    public static void stopRefreshing(){

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
        } else if(direction == SwipyRefreshLayoutDirection.BOTTOM ){
            count += 1;
        }
        MainActivity.getNearbyUsers(count);

//        new Handler().postDelayed(new Runnable() {
//            @Override
//            public void run() {
//                //Hide the refresh after 2sec
//                getActivity().runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        swipeRefreshLayout.setRefreshing(false);
//                    }
//                });
//            }
//        }, DISMISS_TIMEOUT);
    }



    /** ADAPTER INSTAGRAM TAB **/
    public class EgoStreamTabAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private ArrayList<String> mData = new ArrayList<String>();
        private LayoutInflater mInflater;

        private TreeSet mSeparatorsSet = new TreeSet();

        public EgoStreamTabAdapter(){
            mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item){
            mData.add(item);
            notifyDataSetChanged();
        }

        public void addSeparatorItem(final String item){

            mData.add(item);
            // save separator position
            mSeparatorsSet.add(mData.size() - 1);
            notifyDataSetChanged();
        }

        @Override
        public int getItemViewType(int position) {
//			return mSeparatorsSet.contains(position) ? TYPE_SEPARATOR : TYPE_ITEM;
            if(mSeparatorsSet.contains(position)){
                return TYPE_SEPARATOR;
            } else {
                return TYPE_ITEM;
            }
        }

        @Override
        public int getViewTypeCount() {
            return TYPE_MAX_COUNT;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return mData.get(position);
        }

        @Override
        public long getItemId(int position) {
            return position;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            System.out.println("getView" + position + " " + convertView);

            ViewHolder viewHolder = null;

            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_stream, null);

                //Initialize the gridview
                gridViewInitializer(convertView);
                viewHolder.loadingMessage = (TextView) convertView.findViewById(R.id.loading_message);
                viewHolder.loadingMessage.setText("Pull up to load more egos");
                viewHolder.loadingMessage.setTypeface(typeface);
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();

            }
            return convertView;
        }

        public class ViewHolder{
            NonScrollableGridView gridView;
            TextView loadingMessage;
        }
    }

}



