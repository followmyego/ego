package net.egobeta.ego;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentActivity;
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

import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import net.amazonaws.mobile.AWSMobileClient;
import net.egobeta.ego.demo.nosql.Book;

import java.util.ArrayList;
import java.util.TreeSet;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link EgoStreamFragment.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link EgoStreamFragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class EgoStreamFragment extends ScrollTabHolderFragment implements AbsListView.OnScrollListener {
    private static final String LOG_TAG = MainActivity.class.getSimpleName();
    private static final String ARG_POSITION = "position";

    /** Inner classes use this value to determine how many results to retrieve per service call. */
    private static final int RESULTS_PER_RESULT_GROUP = 40;

    AWSMobileClient awsMobileClient;
    static DynamoDBMapper mapper;

    Book bookToLoad;

    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_title = "title";
    private static final String ARG_author = "author";
    private static final String ARG_price = "price";
    private static final String ARG_isbn = "isbn";
    private static final String ARG_hardcover = "hardcover";
    private static final String ARG_awesome = "awesome";

    private String mTitle;
    private String mAuthor;
    private String Price;
    private String ISBN;
    private Boolean mHardCover;
    private Boolean mAwesome;

    private TextView tvmTitle;
    private TextView tvmAuthor;
    private TextView tvPrice;
    private TextView tvISBN;
    private TextView tvmHardCover;
    private TextView tvmAwesome;
    private Button mButton;
    private String isbn;
    private SpinnerRunner spinnerRunner;
    private OnFragmentInteractionListener mListener;
    String[] web = new String[10];
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

    /** The delay that must pass before showing a spinner. */
    private static final int SPINNER_DELAY_MS = 300;


    public static EgoStreamFragment newInstance(Context context1, int position, Toolbar toolbar1) {
        EgoStreamFragment fragment = new EgoStreamFragment();
        Bundle args = new Bundle();
        args.putInt(ARG_POSITION, position);
        context = context1;
        toolbar = toolbar1;
        fragment.setArguments(args);
        return fragment;
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mPosition = getArguments().getInt(ARG_POSITION);
        typeface = Typeface.createFromAsset(context.getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");

        awsMobileClient = AWSMobileClient.defaultMobileClient();
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        v = inflater.inflate(R.layout.fragment_list, null);
        mListView = (ListView) v.findViewById(R.id.listView);
        View placeHolderView = inflater.inflate(R.layout.view_header_placeholder, mListView, false);
        mListView.addHeaderView(placeHolderView);
//        mButton = (Button)  view.findViewById(R.id.mButton);

//        mButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                GetABook(getActivity());
//            }
//        });


        if (mPosition == 0) {
            MyCustomAdapterSlide3And4 myCustomAdapter = new MyCustomAdapterSlide3And4();
            for(int i = 0; i < 10; i++){
                myCustomAdapter.addItem("item " + i);
            }
            mListView.setAdapter(myCustomAdapter);
            mListView.setOnScrollListener(this);
        } else if (mPosition == 1) {

            MyCustomAdapterSlide3And4 myCustomAdapter = new MyCustomAdapterSlide3And4();
            for(int i = 0; i < 10; i++){
                myCustomAdapter.addItem("item " + i);
            }
            mListView.setAdapter(myCustomAdapter);
            mListView.setOnScrollListener(this);

        } else if (mPosition == 2) {
            MyCustomAdapterSlide3And4 myCustomAdapter = new MyCustomAdapterSlide3And4();
            for(int i = 0; i < 10; i++){
                myCustomAdapter.addItem("item " + i);
            }
            mListView.setAdapter(myCustomAdapter);
            mListView.setOnScrollListener(this);

        } else if (mPosition == 3) {
            MyCustomAdapterSlide3And4 myCustomAdapter = new MyCustomAdapterSlide3And4();
            for(int i = 0; i < 10; i++){
                myCustomAdapter.addItem("item " + i);
            }
            mListView.setAdapter(myCustomAdapter);
            mListView.setOnScrollListener(this);

        }


        /*if (mPosition == 0) {
            EgoStreamTabAdapter myCustomAdapter = new EgoStreamTabAdapter();
            for(int i = 0; i < 10; i++){
                myCustomAdapter.addItem("item " + i);
            }
            myCustomAdapter.addSeparatorItem("separator " + 1);
//			for(int i = 0; i < 1; i++){
//				myCustomAdapter.addItem("item " + i);
//				if( i == 0){
//					myCustomAdapter.addSeparatorItem("separator " + i);
//				}
//
            mListView.setAdapter(myCustomAdapter);
            mListView.setOnScrollListener(this);
        } else if (mPosition == 1) {

            MyCustomAdapterSlide3And4 myCustomAdapter = new MyCustomAdapterSlide3And4();
            for(int i = 0; i < 10; i++){
                myCustomAdapter.addItem("item " + i);
            }
            mListView.setAdapter(myCustomAdapter);
            mListView.setOnScrollListener(this);
        }*/

        return v;
    }

    /*@Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        spinnerRunner = new SpinnerRunner();
    }*/








    private void dismissSpinner() {
        spinnerRunner.cancelOrDismiss();
    }

    private void showSpinner() {
        // Disable the operations list until query executes.
//        operationsListView.setEnabled(false);

        spinnerRunner.schedule();
    }

    private void updateViewItems() {

        tvmTitle.setText(bookToLoad.getTitle());
        tvmAuthor.setText(bookToLoad.getAuthor());
        tvPrice.setText(bookToLoad.getPrice() + "");
        tvISBN.setText(bookToLoad.getIsbn());

        if(bookToLoad.getHardCover()){
            tvmHardCover.setText("HardCover: Yes");
        } else {
            tvmHardCover.setText("HardCover: No");
        }

        if(bookToLoad.getAwesome()){
            tvmAwesome.setText("Awesome: Yes");
        } else {
            tvmAwesome.setText("Awesome: No");
        }

    }


    /************************************** IMPLEMENTED METHODS ******************************************/
    /***********************************************************************************************/

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(Uri uri);
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
        if (mScrollTabHolder != null) {
            mScrollTabHolder.onScroll(view, firstVisibleItem, visibleItemCount, totalItemCount, mPosition);
        }
    }

    @Override
    public void onScrollStateChanged(AbsListView view, int scrollState) {

    }


    /************************************** INNER CLASSES ******************************************/
    /***********************************************************************************************/

    private class SpinnerRunner implements Runnable {
        /** A Handler for showing a spinner if service call latency becomes too long. */
        private Handler spinnerHandler;
        private volatile boolean isCanceled = false;
        private volatile ProgressDialog progressDialog = null;

        private SpinnerRunner() {
            spinnerHandler = new Handler();
        }

        @Override
        public synchronized void run() {
            if (isCanceled) {
                return;
            }
            final FragmentActivity activity = getActivity();
            if (activity != null) {
                progressDialog = ProgressDialog.show(activity,
                        getString(R.string.nosql_dialog_title_pending_results_text),
                        getString(R.string.nosql_dialog_message_pending_results_text));
            }
        }

        private void schedule() {
            isCanceled = false;
            // Post delayed runnable so that the spinner will be shown if the delay
            // expires and results haven't come back.
            spinnerHandler.postDelayed(this, SPINNER_DELAY_MS);
        }

        private synchronized void cancelOrDismiss() {
            isCanceled = true;
            // Cancel showing the spinner if it hasn't been shown yet.
            spinnerHandler.removeCallbacks(this);

            if (progressDialog != null) {
                // if the spinner has been shown, dismiss it.
                progressDialog.dismiss();
                progressDialog = null;
            }
        }
    }

    public class InstagramTabAdapter extends BaseAdapter {

        private static final int TYPE_ITEM = 0;
        private static final int TYPE_SEPARATOR = 1;
        private static final int TYPE_MAX_COUNT = TYPE_SEPARATOR + 1;

        private ArrayList<String> mData = new ArrayList<String>();
        private LayoutInflater mInflater;

        private TreeSet mSeparatorsSet = new TreeSet();

        public InstagramTabAdapter(){
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
            int type = getItemViewType(position);

            if(convertView == null){
                viewHolder = new ViewHolder();
                convertView = mInflater.inflate(R.layout.list_item_stream, null);


                //Initialize the gridview
                gridView = (NonScrollableGridView) convertView.findViewById(R.id.myGridView);
//				final String[] letters = new String[] {
//						"A", "B", "C", "D", "E",
//						"F", "G", "H", "I", "J",
//						"K", "L", "M", "N", "O",
//						"P", "Q", "R", "S", "T",
//						"U", "V", "W", "X", "Y", "Z"};
//				CustomGridAdapter gridAdapter = new CustomGridAdapter(getActivity(), letters);
//				gridView.setAdapter(gridAdapter);
//						break;
//				}
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









    //    public class GetBookAsyncTask extends AsyncTask<String, Void, String> {
//        @Override
//        protected String doInBackground(String... params) {
//            getBookFromDB(params[0]);
//            return null;
//        }
//    }

    /*
     * Retrieves all of the attribute/value pairs for the specified user.
     */
//    public void getBookFromDB(String bookISBN) {
//
//        bookToLoad = null;
//
//        /** Show the spinner to let the user know the app is getting/sending data **/
//        showSpinner();
//
//        try {
//            bookToLoad = mapper.load(Book.class, bookISBN);
//        } catch (final AmazonServiceException ex) {
//            ThreadUtils.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
////                    operationsListView.setEnabled(true);
//                    Log.e(LOG_TAG,
//                            "Failed executing selected DynamoDB table", ex);
//                    DynamoDBUtils.showErrorDialogForServiceException(getActivity(),
//                            getString(R.string.nosql_dialog_title_failed_operation_text), ex);
//                }
//            });
//            return;
//        } finally {
//            dismissSpinner();
//        }
//
//        if (bookToLoad != null) {
//            /** Update ui if there were results **/
//            ThreadUtils.runOnUiThread(new Runnable() {
//                @Override
//                public void run() {
//                    updateViewItems();
//                }
//            });
//        } else {
//
//        }
//
//    }


//    public void GetABook(final Context context) {
//
//        /** Display alert dialog asking user for a ISBN number for the book they want to get **/
//        AlertDialog.Builder statusUpdateBuilder = new AlertDialog.Builder(context);
//        statusUpdateBuilder.setTitle("Enter your book ISBN and we will see if we have it!");
//
//        // Set up the input
//        final EditText input = new EditText(context);
//        // Specify the type of input expected; this, for example, sets the input as a password, and will mask the text
//        input.setInputType(InputType.TYPE_CLASS_TEXT);
//        statusUpdateBuilder.setView(input);
//
//
//
//        // Set up the buttons
//        statusUpdateBuilder.setPositiveButton("Search", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//
//
//                //Check if the isbn is null or empty
//                if (!(input.getText().toString().trim()).equals("")) {
//                    isbn = input.getText().toString().trim();
//
//                    //Query the book on the server
//                    new GetBookAsyncTask().execute(isbn);
//                } else {
//                    Toast.makeText(context, "You have not entered an ISBN yet", Toast.LENGTH_SHORT).show();
//                }
//            }
//        });
//        statusUpdateBuilder.setNegativeButton("Nevermind.", new DialogInterface.OnClickListener() {
//            @Override
//            public void onClick(DialogInterface dialog, int which) {
//                dialog.cancel();
//            }
//        });
//        statusUpdateBuilder.show();
//
//
//        /** Get the ISBN number from the alertdialog input and get book from db on Positive button click **/
//    }
}
