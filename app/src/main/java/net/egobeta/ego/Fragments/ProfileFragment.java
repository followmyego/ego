package net.egobeta.ego.Fragments;

import android.app.AlertDialog;

import android.app.ProgressDialog;
import android.content.Context;

import android.content.DialogInterface;
import android.graphics.Typeface;

import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.AsyncTask;
import android.os.Bundle;

import android.os.Handler;
import android.os.Message;
import android.support.v17.leanback.widget.HorizontalGridView;
import android.support.v4.app.Fragment;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import android.util.Log;
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
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;
import com.jess.ui.TwoWayGridView;

import net.egobeta.ego.ImportedClasses.NonScrollableGridView;
import net.egobeta.ego.InstagramClasses.ApplicationData;
import net.egobeta.ego.InstagramClasses.ImageViewAdapterInstagram;
import net.egobeta.ego.InstagramClasses.InstagramApp;
import net.egobeta.ego.R;
import net.egobeta.ego.Table_Classes.User_Profile;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.lucasr.twowayview.TwoWayView;
import org.w3c.dom.Text;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;

import javax.net.ssl.HttpsURLConnection;

public class ProfileFragment extends ScrollTabHolderFragment implements AbsListView.OnScrollListener{

	private static final String ARG_POSITION = "position";

	//User profile info variables
	private String facebookId;

	//Other variables
	String[] web = new String[20];
	public static ListView mListView;
	private static final String TAG = "DEBUGGING MESSAGE";
	public ScrollView scrollView;

	public static int mPosition;
	static Context context;
	private Typeface typeface;

	//Instagram GridView view Variables
	private static AlertDialog.Builder builder;
	private static TextView connectButtonText;
	private static ImageButton connectButton;
	private static TwoWayGridView  gridView;

	public static View v;
	private static Toolbar toolbar;
	static User_Profile userProfile;
	private int scrollHeight;
	static String instagramPicsConnected;
	boolean instagramIsConnected = false;
	static DynamoDBMapper mapper = null;
	static int streamPosition;


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
	private Handler handler = new Handler(new Handler.Callback() {

		@Override
		public boolean handleMessage(Message msg) {
			if (msg.what == InstagramApp.WHAT_FINALIZE) {

				//Store user instagram info from instagram login on userProfile item
				instagramUserInfoHashmap = instagramApp.getUserInfo();
				userProfile.setInstagram_username(instagramApp.getUserName());
				userProfile.setInstagram_id(instagramUserInfoHashmap.get("id"));

				//Assign the class instagram variables thier values
				instagramId = userProfile.getInstagram_id();
				instagramUsername = userProfile.getInstagram_username();

				//If logged in user
				if(streamPosition == 2){
					//Save userProfile item on server to reflect new instagram variable changes
					userProfile.setInstagram_photos_connected("yes");
					/** Run method to save userProfile item to the database **/
					new SaveUserProfile().execute();
				}


				//Get logged in users instagram image links
				if(isNetworkAvailable()){
					getAllMediaImages(getContext());
				} else {
					Toast.makeText(getActivity(), "No internet connection detected", Toast.LENGTH_SHORT).show();
				}


			} else if (msg.what == InstagramApp.WHAT_ERROR) {
				Log.d(TAG, "Check your network.");
			}
			return false;
		}
	});



	public static Fragment newInstance(Context context1, int position, Toolbar toolbar1,
									   String facebookId, User_Profile user_Profile, DynamoDBMapper dbMapper, int pos) {
		ProfileFragment f = new ProfileFragment();
		Bundle b = new Bundle();
		b.putInt(ARG_POSITION, position);
		b.putString("facebook_id", facebookId);
		context = context1;
		toolbar = toolbar1;
		f.setArguments(b);
		userProfile = user_Profile;
		mapper = dbMapper;
		mPosition = position;
		streamPosition = pos;

		if(user_Profile.getInstagram_photos_connected() != null){
			instagramPicsConnected = user_Profile.getInstagram_photos_connected();
		} else {
			instagramPicsConnected = "no";
		}
		return f;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
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
		mListView.setOverScrollMode(View.OVER_SCROLL_NEVER);
		View placeHolderView = inflater.inflate(R.layout.view_header_placeholder_profile, mListView, false);
		mListView.addHeaderView(placeHolderView);



		/** Check to see if we are at our profile and we have our instagram photos connected **/

//		if(instagramPicsConnected.equals("yes") && mPosition == 2){

			/** Initialize Instagram so we can get our instagram photos **/
			instagramApp = new InstagramApp(getActivity(), ApplicationData.CLIENT_ID,
					ApplicationData.CLIENT_SECRET, ApplicationData.CALLBACK_URL);
			instagramApp.setListener(new InstagramApp.OAuthAuthenticationListener() {
				@Override
				public void onSuccess() {
					// tvSummary.setText("Connected as " + instagramApp.getUserName());
//						connectButton.setText("Photos");
					instagramApp.fetchUserName(handler);
				}
				@Override
				public void onFail(String error) {
					Toast.makeText(getActivity(), error, Toast.LENGTH_SHORT).show();
//						connectButton.setText("Connect");
				}
			});
//		}

		MyCustomAdapterSlide3And4 myCustomAdapter = new MyCustomAdapterSlide3And4();
		for(int i = 0; i < 10; i++){
			myCustomAdapter.addItem("item " + i);
		}
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
		mListView.setSelectionFromTop(1, scrollHeight);


	}

	@Override
	public void onScroll(AbsListView view, int firstVisibleItem, int visibleItemCount, int totalItemCount) {
		if (mScrollTabHolder != null)
			//mScrollTabHolder is set to the ScrollTabHolder context from the Parent Activity holding this fragment since the parent activity implements scrolltabholder
			//Since we are extending ScrollTabHolder fragment we are able to access this variable
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
			ViewHolder holder = null;

			/** If 1st position, return the about view as the list item **/
			if(position == 0){
				convertView = mInflater.inflate(R.layout.profile_about_section, null);
				/** Initialize View item variables **/
				TextView aboutHeader = (TextView) convertView.findViewById(R.id.about_header);
				TextView ageHeader = (TextView) convertView.findViewById(R.id.age_header);
				TextView age = (TextView) convertView.findViewById(R.id.age);
				TextView livesInHeader = (TextView) convertView.findViewById(R.id.lives_in_header);
				TextView livesIn = (TextView) convertView.findViewById(R.id.lives_in);
				TextView worksAtHeader = (TextView) convertView.findViewById(R.id.works_at_header);
				TextView worksAt = (TextView) convertView.findViewById(R.id.works_at);

				/** Set the view item fonts **/
				aboutHeader.setTypeface(typeface);
				ageHeader.setTypeface(typeface);
				age .setTypeface(typeface);
				livesInHeader.setTypeface(typeface);
				livesIn.setTypeface(typeface);
				worksAtHeader.setTypeface(typeface);
				worksAt.setTypeface(typeface);

				/** If 2nd position, return the Instagram GridView as the list item **/
			} else if(position == 1){
				if(convertView == null){
					convertView = mInflater.inflate(R.layout.profile_instagram_photos_section, null);
					holder = new ViewHolder();

					/** If we are on the user's profile **/
					if(streamPosition == 2){
						System.out.println("Profile position = " + streamPosition);
						/**Initialize and set up the connect button**/
						holder.connectButtonText = (TextView) convertView.findViewById(R.id.buttonText);
						holder.connectButtonText.setTypeface(typeface);
						holder.connectButton = (ImageButton) convertView.findViewById(R.id.btnConnect);
						holder.connectButton.setOnClickListener(new View.OnClickListener() {
							@Override
							public void onClick(View v) {
								connectOrDisconnectUser();
							}
						});
						/***************************************INSTAGRAM*******************************************/
						if (instagramApp.hasAccessToken()) {
							//btnConnect.setText("Disconnect your instagram");
							holder.connectButtonText.setText("Instagram Photos");
							holder.connectButton.setVisibility(View.GONE);
							instagramApp.fetchUserName(handler);
							Log.d("INSTAGRAM INFO", "TAG_ID - " + InstagramApp.TAG_ID
									+ "CLIENT_ID - " + ApplicationData.CLIENT_ID
							);
						} else {
							if(holder.connectButton != null){
								holder.connectButtonText.setText("Share your photos too");
								holder.connectButton.setVisibility(View.VISIBLE);
							}
						}
						final ViewHolder viewHolder = holder;

						builder = new AlertDialog.Builder(getActivity());
						builder.setMessage("Disconnect from Instagram?")
								.setCancelable(false)
								.setPositiveButton("Yes",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {
												//Save userProfile item on server to reflect new instagram variable changes
												userProfile.setInstagram_photos_connected("no");
												/** Run method to save userProfile item to the database **/
												new SaveUserProfile().execute();
												instagramApp.resetAccessToken();
												viewHolder.connectButtonText.setText("Share your photos too");
												viewHolder.connectButton.setVisibility(View.VISIBLE);
												viewHolder.gridView.setVisibility(View.GONE);
											}
										})
								.setNegativeButton("No",
										new DialogInterface.OnClickListener() {
											public void onClick(DialogInterface dialog, int id) {
												dialog.cancel();
											}
										});
						//Initialize the gridview
						holder.gridView = (TwoWayGridView) convertView.findViewById(R.id.myGridView);
						convertView.setTag(holder);

						/** If we are on someone else's profile **/
					} else {
						System.out.println("Profile position = " + streamPosition);
						/** Check if the user has their instagram connected **/
						if(instagramPicsConnected.equals("yes")){
							/** Initialize and set up the connect button **/
							holder.connectButtonText = (TextView) convertView.findViewById(R.id.buttonText);
							holder.connectButtonText.setText("Instagram Photos");
							holder.connectButtonText.setTypeface(typeface);
							holder.connectButton = (ImageButton) convertView.findViewById(R.id.btnConnect);
							holder.connectButton.setVisibility(View.GONE);

							//Get logged in users instagram image links
							if(isNetworkAvailable()){
								getAllMediaImages2(getContext());
							} else {
								Toast.makeText(getActivity(), "No internet connection detected", Toast.LENGTH_SHORT).show();
							}
							//Initialize the gridview
							holder.gridView = (TwoWayGridView) convertView.findViewById(R.id.myGridView);
							convertView.setTag(holder);

							/** Their instagram isn't connected, set all the views to gone **/
						} else {
							holder.connectButtonText = (TextView) convertView.findViewById(R.id.buttonText);
							holder.connectButton = (ImageButton) convertView.findViewById(R.id.btnConnect);
							holder.gridView = (TwoWayGridView) convertView.findViewById(R.id.myGridView);
							holder.connectButtonText.setVisibility(View.GONE);
							holder.connectButton.setVisibility(View.GONE);
							holder.gridView.setVisibility(View.GONE);
							convertView.setTag(holder);
						}
					}
				} else {
					holder = (ViewHolder)convertView.getTag();
				}
				connectButton = holder.connectButton;
				connectButtonText = holder.connectButtonText;
				gridView = holder.gridView;
			} else {
				convertView = mInflater.inflate(R.layout.list_item, null);
				TextView textView = (TextView) convertView.findViewById(R.id.text1);
			}

			return convertView;
		}

		public class ViewHolder{
			TextView aboutHeader;
			TextView ageHeader;
			TextView age;
			TextView livesInHeader;
			TextView livesIn;
			TextView worksAtHeader;
			TextView worksAt;

			TextView connectButtonText;
			ImageButton connectButton;

			TwoWayGridView gridView;
		}

	}



	/**Method to check if user is currently logged into instagram
	 What state should the connect button be in?*/
	public void connectOrDisconnectUser() {
//		Toast.makeText(getActivity(), "Click worked :)", Toast.LENGTH_LONG).show();
		if (instagramApp.hasAccessToken()) {

			if(instagramPicsConnected.equals("yes")){
				final AlertDialog alert = builder.create();
				alert.show();
			} else {
				//If logged in user
				if(streamPosition == 2){
					//Save userProfile item on server to reflect new instagram variable changes
					userProfile.setInstagram_photos_connected("yes");
					/** Run method to save userProfile item to the database **/
					new SaveUserProfile().execute();
				}


				//Get logged in users instagram image links
				if(isNetworkAvailable()){
					getAllMediaImages(getContext());
				} else {
					Toast.makeText(getActivity(), "No internet connection detected", Toast.LENGTH_SHORT).show();
				}

			}

		} else {
			instagramApp.authorize();
		}
	}


	private boolean isNetworkAvailable() {
		ConnectivityManager connectivityManager
				= (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
		NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
		return activeNetworkInfo != null && activeNetworkInfo.isConnected();
	}

	//Gets instagram image links from url with user info and stores them in StringArray imageThumbList
	private void getAllMediaImages(Context context) {
//		pd = ProgressDialog.show(context, "", "Loading images...");


		try {

			new GetInstagramPhotosViaURL().execute("https://api.instagram.com/v1/users/" + instagramId
					+ "/media/recent/?access_token=" + instagramApp.getTOken()
					+ "&count=" + instagramUserInfoHashmap.get(InstagramApp.TAG_COUNTS));

			System.out.println("MY INSTAGRAM LINK = " + "https://api.instagram.com/v1/users/" + instagramUserInfoHashmap.get(InstagramApp.TAG_ID)
					+ "/media/recent/?access_token=" + instagramApp.getTOken()
					+ "&count=" + 6);

			JSONObject jsonObject = jObj;
		} catch (Exception exception) {
			exception.printStackTrace();

		}
		handler.sendEmptyMessage(1);
	}

	//Gets instagram image links for other user from url with user info and stores them in StringArray imageThumbList
	private void getAllMediaImages2(Context context) {
//		pd = ProgressDialog.show(context, "", "Loading images...");

		try {
			new GetInstagramPhotosViaURL2().execute("https://www.instagram.com/" + userProfile.getInstagram_username() + "/media");
		} catch (Exception exception) {
			exception.printStackTrace();
		}
		handler.sendEmptyMessage(1);
	}



	//Method to hit a REST api and get json response of user's instagram photos
	public class GetInstagramPhotosViaURL extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			BufferedReader bufferedReader = null;
			HttpsURLConnection LucasHttpURLConnection = null;
			InputStream IS;
			try {
				URL url = new URL(params[0]);
				LucasHttpURLConnection = (HttpsURLConnection) url.openConnection();
				LucasHttpURLConnection.connect();
				IS = LucasHttpURLConnection.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(IS));
				StringBuffer buffer = new StringBuffer();
				String json;
				while ((json = bufferedReader.readLine()) != null) {
					buffer.append(json);
				}

				return buffer.toString().trim();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (LucasHttpURLConnection != null) {
					LucasHttpURLConnection.disconnect();
				}
				try {

					if (bufferedReader != null)
						bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			int what1 = 1;

			//Print server AsyncTask response
			System.out.println("GetInstagramPhotosViaURL Resulted Value: " + result);

			//Check if null Response
			if (result != null && !result.equals("")) {
				try {
					jObj = new JSONObject(result);
					JSONArray data = jObj.getJSONArray("data");
					int count = 0;

					//Loop through json object populating imageThumbList String array with links from json response
					for (int data_i = 0; data_i < data.length(); data_i++) {
						JSONObject data_obj = data.getJSONObject(data_i);
						JSONObject images_obj = data_obj
								.getJSONObject("images");
						JSONObject thumbnail_obj = images_obj
								.getJSONObject("standard_resolution");
						String str_url = thumbnail_obj.getString("url");
						imageThumbList.add(str_url);
						Log.i("INSTAGRAM PHOTOS", str_url);
						if(count < 10){
							web[count] = str_url;
							imageId[count] = 1;
						}
						count++;
					}

				} catch (JSONException e) {
					e.printStackTrace();
					what1 = WHAT_ERROR;
				}
				handler.sendEmptyMessage(what1);
				displayInstagramImageList();
			} else {
				Toast.makeText(context, "Error in getting Instagram images", Toast.LENGTH_LONG).show();
//				pd.dismiss();
			}
		}
	}


	//Method to hit a REST api and get json response of user's instagram photos
	public class GetInstagramPhotosViaURL2 extends AsyncTask<String, Void, String> {

		@Override
		protected String doInBackground(String... params) {
			BufferedReader bufferedReader = null;
			HttpsURLConnection LucasHttpURLConnection = null;
			InputStream IS;
			try {
				URL url = new URL(params[0]);
				LucasHttpURLConnection = (HttpsURLConnection) url.openConnection();
				LucasHttpURLConnection.connect();
				IS = LucasHttpURLConnection.getInputStream();
				bufferedReader = new BufferedReader(new InputStreamReader(IS));
				StringBuffer buffer = new StringBuffer();
				String json;
				while ((json = bufferedReader.readLine()) != null) {
					buffer.append(json);
				}

				return buffer.toString().trim();
			} catch (IOException e) {
				e.printStackTrace();
			} finally {
				if (LucasHttpURLConnection != null) {
					LucasHttpURLConnection.disconnect();
				}
				try {

					if (bufferedReader != null)
						bufferedReader.close();
				} catch (IOException e) {
					e.printStackTrace();
				}

			}
			return null;
		}

		@Override
		protected void onPostExecute(String result) {
			super.onPostExecute(result);
			int what1 = 1;

			//Print server AsyncTask response
			System.out.println("GetInstagramPhotosViaURL Resulted Value: " + result);

			//Check if null Response
			if (result != null && !result.equals("")) {
				try {
					jObj = new JSONObject(result);
					JSONArray data = jObj.getJSONArray("items");


					//Loop through json object populating imageThumbList String array with links from json response
					for (int data_i = 0; data_i < data.length(); data_i++) {
						JSONObject data_obj = data.getJSONObject(data_i);
						JSONObject images_obj = data_obj
								.getJSONObject("images");
						JSONObject thumbnail_obj = images_obj
								.getJSONObject("standard_resolution");
						String str_url = thumbnail_obj.getString("url");
						imageThumbList.add(str_url);
						Log.i("INSTAGRAM PHOTOS", str_url);
					}

				} catch (JSONException e) {
					e.printStackTrace();
					what1 = WHAT_ERROR;
				}
				handler.sendEmptyMessage(what1);
				displayInstagramImageList();
			} else {
				Toast.makeText(context, "Error in getting Instagram images", Toast.LENGTH_LONG).show();
//				pd.dismiss();
			}
		}
	}


	//Method to show users instagram photos
	public void displayInstagramImageList(){
		//Initialize the adapter for the horizontal image sliding view
//		twoWayView = (TwoWayView) rootView.findViewById(R.id.lvItems);

		//Create adapter for instagram images and horizontal image sliding view
		adapter = new ImageViewAdapterInstagram(getActivity(), imageThumbList);

		//Set the adapter for the horizontal image sliding view
		if(gridView != null){
			gridView.setVisibility(View.VISIBLE);
			gridView.setAdapter(adapter);
		}

//		pd.dismiss();

		//If logged in user
		if(streamPosition == 2){
			//Update on images linked on local database
			userProfile.setInstagram_photos_connected("yes");
			/** Run method to save userProfile item to the database **/
			new SaveUserProfile().execute();
		}
	}





	public class SaveUserProfile extends AsyncTask<Void, Void, Void> {


		public SaveUserProfile() {

		}

		@Override
		protected Void doInBackground(Void... params) {
			try{
				mapper.save(userProfile);
			} catch (AmazonClientException ex){
				ex.printStackTrace();
			}

			return null;
		}

		@Override
		protected void onPostExecute(Void aVoid) {
			super.onPostExecute(aVoid);
//            Toast.makeText(MainActivity.this, "Successfully saved user's info to db", Toast.LENGTH_SHORT).show();

		}

	}
}