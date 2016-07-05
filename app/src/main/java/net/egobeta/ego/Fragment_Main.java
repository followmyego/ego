package net.egobeta.ego;

import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;

import android.graphics.Typeface;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

import android.os.Bundle;

import android.support.v4.app.Fragment;
import android.support.v7.widget.Toolbar;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import android.widget.AbsListView;

import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;


/**
 * Created by Lucas on 29/06/2016.
 */
public class Fragment_Main extends ScrollTabHolderFragment implements AbsListView.OnScrollListener {


    private static final String ARG_POSITION = "position";

    //User profile info variables
    private String facebookId;

    //Other variables
    String[] web = new String[4];
    private ListView mListView;
    private ArrayList<String> mListItems;
    private static final String TAG = "DEBUGGING MESSAGE";
    private String interestSuggestionsUrlAddress = "http://www.myegotest.com/android_user_api/searcher.php";
    private String getUserInterestsUrlAddress =  "http://www.myegotest.com/android_user_api/getUserInterests.php";
    public ScrollView scrollView;

    private int mPosition;
    static Context context;
    private Typeface typeface;

    //Instagram GridView view Variables
    private static AlertDialog.Builder builder;
    private static TextView connectButtonText;
    private static ImageButton connectButton;
    private static NonScrollableGridView gridView;

    public static View v;
    private static Toolbar toolbar;

    private int scrollHeight;
//    EgoStreamViewAdapter adapter;
    EgoStreamViewAdapter2 adapter;


    //Instagram stuffs
    private static String instagramProfileLinked;
    private static String instagramId;
    private static String instagramUsername;
    private ArrayList<String> facebookProfileIds = new ArrayList<String>();
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


    public static Fragment newInstance(Context context1, int position, Toolbar toolbar1) {
        Fragment_Main f = new Fragment_Main();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        context = context1;
        toolbar = toolbar1;
        f.setArguments(b);
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
        adapter = new EgoStreamViewAdapter2(getActivity(), web, facebookProfileIds);
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


        if (mPosition == 0) {

            EgoStreamTabAdapter myCustomAdapter = new EgoStreamTabAdapter();
            myCustomAdapter.addSeparatorItem("separator " + 1);

            mListView.setAdapter(myCustomAdapter);
            mListView.setOnScrollListener(this);

        } else if (mPosition == 1) {

            EgoStreamTabAdapter myCustomAdapter = new EgoStreamTabAdapter();
            myCustomAdapter.addSeparatorItem("separator " + 1);

            mListView.setAdapter(myCustomAdapter);
            mListView.setOnScrollListener(this);

        }

        return v;

    }



    @Override
    public void adjustScroll(int scrollHeight) {
        this.scrollHeight = scrollHeight;
        if (scrollHeight == 0 && mListView.getFirstVisiblePosition() >= 1) {
            return;
        }
        mListView.setSelectionFromTop(1, scrollHeight);
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

    //Method to capitalize 1st letter of the String that's using this method.
    private String capitalizeFirstCharacter(String textInput) {
        String input = textInput.toLowerCase();
        String output = input.substring(0, 1).toUpperCase() + input.substring(1);
        return output;
    }

    //Method to show users instagram photos
    public void displayStreamImageList(){


        //Set the adapter for the horizontal image sliding view
        if(gridView != null){
            gridView.setVisibility(View.VISIBLE);
            gridView.setAdapter(adapter);
        }
    }

    /**Adapter for the interests, social network and chat page */
    public class MyCustomAdapterSlide3And4 extends BaseAdapter {

        private ArrayList<String> mData = new ArrayList<String>();
        private LayoutInflater mInflater;

        public MyCustomAdapterSlide3And4(){
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        public void addItem(final String item){
            mData.add(item);
            notifyDataSetChanged();
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
                convertView = mInflater.inflate(R.layout.list_item, null);
                viewHolder.textView = (TextView) convertView.findViewById(R.id.text1);

                convertView.setTag(viewHolder);

            } else {
                viewHolder = (ViewHolder)convertView.getTag();

            }
            viewHolder.textView.setText(mData.get(position) + " ");
            return convertView;
        }

        public class ViewHolder{
            TextView textView;
        }
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
            mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
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
                gridView = (NonScrollableGridView) convertView.findViewById(R.id.myGridView);
                displayStreamImageList();
                convertView.setTag(viewHolder);
            } else {
                viewHolder = (ViewHolder)convertView.getTag();

            }
            return convertView;
        }

        public class ViewHolder{
            NonScrollableGridView gridView;
        }
    }


}
