package net.egobeta.ego.Fragments;

import android.app.Activity;

import android.app.ProgressDialog;
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
import android.widget.Button;

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

import net.egobeta.ego.Adapters.EgoStreamViewAdapter;
import net.egobeta.ego.Adapters.GenericAdapter;
import net.egobeta.ego.ImportedClasses.NonScrollableGridView;
import net.egobeta.ego.MainActivity;
import net.egobeta.ego.ProfileActivity;
import net.egobeta.ego.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;


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

    private static ListView mListView;
    private ArrayList<String> mListItems;
    private static final String TAG = "DEBUGGING MESSAGE";
    private String interestSuggestionsUrlAddress = "http://www.myegotest.com/android_user_api/searcher.php";
    private String getUserInterestsUrlAddress =  "http://www.myegotest.com/android_user_api/getUserInterests.php";
    public ScrollView scrollView;
    static int deleteCounter = 0;
    private static int mPosition;
    public static Context context;
    static Activity activity;
    private Typeface typeface;
    private static String facebookId = MainActivity.identityManager.getUserFacebookId();

    //Instagram GridView view Variables
    private static NonScrollableGridView gridView;

    public static View v;
    private static Toolbar toolbar;

    private int scrollHeight;
//    EgoStreamViewAdapter adapter;
//    private static FragmentListBinding mBinding;

    static EgoStreamViewAdapter adapter2;

    private static ItemAdapter adapter;
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



        //Create adapter for instagram images and horizontal image sliding view
        adapter2 = new EgoStreamViewAdapter(context, facebook_Ids);
        adapter = new ItemAdapter();

        //Make the current user the first person on the EgoStream
        facebook_Ids.add(facebookId);

    }




    private boolean isNetworkAvailable() {
        ConnectivityManager connectivityManager
                = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
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
                    addDummyData(1, false);
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
//        addDummyData(3);
    }

    protected static void addDummyData(final int itemsCount, boolean bool) {
        System.out.println("SOUT" + " addDummyData");
        System.out.println("MAINACTIVITY: getNearbyUsers addDummyData");
        if(!bool){
            if(count > 0){
                System.out.println("SOUT" + " addDummyData 1");
                MainActivity.getNearbyUsers(count);
//            dobList.finishLoading();
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        // We must call finishLoading
                        // when finishing adding data

                        dobList.finishLoading();
                    }

                }, 3000);
            } else {
                System.out.println("SOUT" + " addDummyData 2");
                new Handler().postDelayed(new Runnable() {

                    @Override
                    public void run() {

                        addItems(adapter.getCount(), adapter.getCount() + itemsCount);

                        // We must call finishLoading
                        // when finishing adding data
                        dobList.finishLoading();
                    }
                }, 3000);
            }
        } else {
            System.out.println("SOUT" + " addDummyData 3");
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {

                    addItems(adapter.getCount(), adapter.getCount() + itemsCount);

                    // We must call finishLoading
                    // when finishing adding data
                    dobList.finishLoading();
                }
            }, 3000);
        }

    }

    protected static void addItems(int from, int to) {
        System.out.println("SOUT" + " addItems");

        String strPlainItem = "Some string";

        for (int i = from; i < to; i++) {
            String strItem = String.format(strPlainItem, i);
            adapter.addItem(strItem);
        }
    }



    public static void notifiyAdapterHasChanged(String[] facebook_Ids1){
        System.out.println("SOUT" + " notifiyAdapterHasChanged");
        facebook_Ids = new ArrayList<>(Arrays.asList(facebook_Ids1));
//        adapter = new EgoStreamViewAdapter(context, facebook_Ids);
        facebook_Ids.add(0, facebookId);
        adapter2.setItems(facebook_Ids);



        gridView.invalidateViews();
        addDummyData(1, false);

        count ++;
//        dobList.finishLoading();
    }

    public static void addNewItems(String[] facebook_Ids1) {
        System.out.println("SOUT" + " addNewItems");
        ArrayList<String> newList = new ArrayList<>(Arrays.asList(facebook_Ids1));
        if(count == 3){
            System.out.println("SOUT" + " backToTop1");
            Toast.makeText(activity, "End of your Ego Stream", Toast.LENGTH_SHORT).show();
        } else {
            if(facebook_Ids.size() >= 40 ){
                System.out.println("SOUT" + " removeAnItem");
                for (int i = 0; i < facebook_Ids1.length - 1; i++) {
                    facebook_Ids.remove(0);
                    facebook_Ids.add(newList.get(i));
                }
            } else {
                for (int i = 0; i < facebook_Ids1.length; i++) {
                    facebook_Ids.add(newList.get(i));
                }
            }
//            adapter.addItem(newList.get(i));


//        adapter.notifyDataSetChanged();
            gridView.invalidateViews();
//            dobList.finishLoading();
            addDummyData(1, true);
            count ++;
        }
    }

    public static void backToTop(){
//        System.out.println("MAINACTIVITY: getNearbyUsers backToTop");
//        count = 0;
//        addDummyData(3, true);
//        MainActivity.getNearbyUsers(count);

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
            facebook_Ids = new ArrayList<String>();
//        adapter = new EgoStreamViewAdapter(context, facebook_Ids);
            adapter2.setItems(facebook_Ids);
            gridView.invalidateViews();
            adapter = new ItemAdapter();
            initDobList(v, mListView);
            adapter.addItem("Some item");
            mListView.setAdapter(adapter);

        }
        System.out.println("MAINACTIVITY: getNearbyUsers onRefresh");
        MainActivity.getNearbyUsers(count);
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
        gridView.setAdapter(adapter2);

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






    public class ItemAdapter extends GenericAdapter<String> {

        public ItemAdapter() {
            super(context);
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
                if (convertView == null) {
                    convertView = layoutInflater.inflate(R.layout.list_item, null);

                    ViewHolder viewHolder = new ViewHolder();
                    viewHolder.title = (TextView) convertView.findViewById(R.id.text1);

                    convertView.setTag(viewHolder);
                }

                ViewHolder holder = (ViewHolder) convertView.getTag();
                String item = getItem(position);

                holder.title.setText(" ");

                return convertView;
            }



        }

        class ViewHolder {
            public TextView title;
        }

    }
}



