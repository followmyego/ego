package net.egobeta.ego.Fragments;

import android.app.Activity;
import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.Intent;
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

import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.Button;

import android.widget.ImageButton;
import android.widget.ListView;
import android.widget.ScrollView;
import android.widget.TextView;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import net.egobeta.ego.Adapters.EgoStreamViewAdapter;
import net.egobeta.ego.Adapters.UserItem;
import net.egobeta.ego.ImportedClasses.NonScrollableGridView;
import net.egobeta.ego.ProfileActivity;
import net.egobeta.ego.R;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.TreeSet;


/**
 * Created by Lucas on 29/06/2016.
 */
public class Fragment_Main_Friends extends ScrollTabHolderFragment implements AbsListView.OnScrollListener {


    //View Item Variables
    private ListView mListView;
    public ScrollView scrollView;
    private NonScrollableGridView gridView;
    public View v;
    public static Toolbar toolbar;

    //Other Variables
    private static final String ARG_POSITION = "position";
    private static ArrayList<String> friends_Ids = new ArrayList<String>();
    private static ArrayList<String> badges = new ArrayList<String>();
    private List<UserItem> userList = new ArrayList<UserItem>();
    public EgoStreamViewAdapter adapter;
    private static int mPosition;
    Context context;
    Activity activity;
    public Typeface typeface;
    public int scrollHeight;




    public static Fragment newInstance(int position, Toolbar toolbar1, ArrayList<String> friendsIds) {
        Fragment_Main_Friends f = new Fragment_Main_Friends();
        Bundle b = new Bundle();
        b.putInt(ARG_POSITION, position);
        toolbar = toolbar1;
        f.setArguments(b);
        friends_Ids = friendsIds;
        return f;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        context = getContext();
        activity = getActivity();

        mPosition = getArguments().getInt(ARG_POSITION);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");

        /** Initialize UserItems from ArrayList of friends facebook ids **/
        for (int i = 0; i < friends_Ids.size(); i++) {
            UserItem userItem = new UserItem(context, friends_Ids.get(i), 0);
            userList.add(userItem);
        }

        /** Create adapter for the gridView containing the users friends profiles **/
        adapter = new EgoStreamViewAdapter(userList, context, friends_Ids, getActivity());
    }


    //This function send our location and pull the users that are around us from the database
    public void getUsersAroundUs(){
        /**Fake array of facebook ids that are around us to mimic loading users from the database
         //based on location **/

        String[] user_friendsFacebookIds = {
                "699211431",
                "574273123",
                "100004583984873",
                "531370423",
                "1765792246"
        };

        int lengthOfUsers = user_friendsFacebookIds.length;
        for (int i = 0; i < lengthOfUsers; i++) {
            friends_Ids.add(user_friendsFacebookIds[i]);
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


        EgoStreamTabAdapter myCustomAdapter = new EgoStreamTabAdapter();
        myCustomAdapter.addSeparatorItem("separator " + 1);
        mListView.setAdapter(myCustomAdapter);
        mListView.setOnScrollListener(this);

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
                bundle.putString("facebook_id", friends_Ids.get(position));
                Intent intent = new Intent(getActivity(), ProfileActivity.class);
                intent.putExtras(bundle);
                startActivity(intent);
            }
        });

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
                gridViewInitializer(convertView);


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
