package net.egobeta.ego;

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
import android.widget.Toast;

import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;

import net.egobeta.ego.InstagramClasses.ImageViewAdapterInstagram;
import net.egobeta.ego.InstagramClasses.InstagramApp;

import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;

public class ProfileFragment extends ScrollTabHolderFragment implements AbsListView.OnScrollListener{

	private static final String ARG_POSITION = "position";

	//User profile info variables
	private String facebookId;

	//Other variables
	String[] web = new String[10];
	private ListView mListView;
	private static final String TAG = "DEBUGGING MESSAGE";
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



	//Instagram stuffs
	private static String instagramProfileLinked;
	private static String instagramId;
	private static String instagramUsername;
	private ArrayList<String> imageThumbList = new ArrayList<String>();
	private int WHAT_FINALIZE = 0;
	private static int WHAT_ERROR = 1;
	private ProgressDialog pd;
	public static final String TAG_DATA = "data";
	public static final String TAG_IMAGES = "images";
	public static final String TAG_THUMBNAIL = "standard_resolution";
	public static final String TAG_URL = "url";
	public JSONObject jObj = null;
	Integer[] imageId = new Integer[10];
	ImageViewAdapterInstagram adapter;
	private static InstagramApp instagramApp;
	private Button btnConnect;
	private HashMap<String, String> instagramUserInfoHashmap = new HashMap<String, String>();


	public static Fragment newInstance(Context context1, int position, Toolbar toolbar1, String facebookId) {
		ProfileFragment f = new ProfileFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		b.putString("facebook_id", facebookId);
		context = context1;
		toolbar = toolbar1;
		f.setArguments(b);
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		mPosition = getArguments().getInt(ARG_POSITION);
		facebookId = getArguments().getString("facebook_id");
		typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");
//		/**Not Currently using this data set**/
//		mListItems = new ArrayList<String>();
//		for (int i = 1; i <= 15; i++) {
//			mListItems.add(i + ". item - currnet page: " + (mPosition + 1));
//		}
	}


	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		v = inflater.inflate(R.layout.fragment_list, null);
		mListView = (ListView) v.findViewById(R.id.listView);
		View placeHolderView = inflater.inflate(R.layout.view_header_placeholder_profile, mListView, false);
		mListView.addHeaderView(placeHolderView);



		if(mPosition == 0){
			MyCustomAdapterSlide3And4 myCustomAdapter = new MyCustomAdapterSlide3And4();
			for(int i = 0; i < 10; i++){
				myCustomAdapter.addItem("item " + i);
			}
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

}