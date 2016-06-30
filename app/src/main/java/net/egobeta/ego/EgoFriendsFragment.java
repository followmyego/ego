package net.egobeta.ego;

import android.app.Activity;
import android.app.AlertDialog;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.Toast;

import com.amazonaws.AmazonClientException;
import com.amazonaws.mobileconnectors.dynamodbv2.dynamodbmapper.DynamoDBMapper;

import net.amazonaws.mobile.AWSMobileClient;
import net.amazonaws.mobile.util.ThreadUtils;
import net.egobeta.ego.demo.nosql.Book;
import net.egobeta.ego.demo.nosql.DynamoDBUtils;


public class EgoFriendsFragment extends Fragment {

    AWSMobileClient awsMobileClient;
    static DynamoDBMapper mapper;

    private EditText tvTitle;
    private EditText tvAuthor;
    private EditText tvPrice;
    private EditText tvIsbn;
    private RadioButton rbHardcover;
    private RadioButton rbAwesome;
    private Button addBookButton;


    private String mTitle;
    private String mAuthor;
    private int mPrice = 0;
    private String mIsbn;
    private Boolean mHardcover;
    private Boolean awesome;

    private Book bookToSave;

    private OnFragmentInteractionListener mListener;

    public EgoFriendsFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        awsMobileClient = AWSMobileClient.defaultMobileClient();
        mapper = AWSMobileClient.defaultMobileClient().getDynamoDBMapper();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_ego_friends, container, false);
        tvTitle  = (EditText) view.findViewById(R.id.etTitle);
        tvAuthor = (EditText) view.findViewById(R.id.etAuthor);
        tvPrice  = (EditText) view.findViewById(R.id.etPrice);
        tvIsbn  = (EditText) view.findViewById(R.id.etIsbn);
        rbHardcover = (RadioButton) view.findViewById(R.id.rbHardcover);
        rbAwesome = (RadioButton) view.findViewById(R.id.rbAwesome);


        addBookButton = (Button) view.findViewById(R.id.addBookButton);
        addBookButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveToDB();
            }
        });


        return view;
    }

    //I created this class to test how to save an item to a db table
    /*public class SaveToDB extends AsyncTask<Void, Void, Void> {


        @Override
        protected Void doInBackground(Void... params) {
            try{
                mapper.save(bookToSave);
            } catch (AmazonClientException ex){
                ex.printStackTrace();
            }

            return null;
        }

        @Override
        protected void onPostExecute(Void aVoid) {
            super.onPostExecute(aVoid);
            Toast.makeText(getActivity(), "Successfully saved book to db", Toast.LENGTH_SHORT).show();
        }
    }*/

    private void saveToDB() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                try {
                    AmazonClientException lastException = null;

                    mTitle = "Frozen";
                    mAuthor = "Charles Dickens";
                    mPrice = 34;
                    mIsbn = "121456";
                    mHardcover = true;
                    awesome = true;

                    bookToSave = new Book();
                    bookToSave.setTitle(mTitle);
                    bookToSave.setAuthor(mAuthor);
                    bookToSave.setPrice(mPrice);
                    bookToSave.setIsbn(mIsbn);
                    bookToSave.setHardCover(mHardcover);
                    bookToSave.setAwesome(awesome);

                    try {
                        mapper.save(bookToSave);
                    } catch (final AmazonClientException ex) {
                        Log.e("AMAZON EXCEPTION", "Failed saving item : " + ex.getMessage(), ex);
                        lastException = ex;
                    }

                    if (lastException != null) {
                        // Re-throw the last exception encountered to alert the user.
                        throw lastException;
                    }

                } catch (final AmazonClientException ex) {
                    // The insertSampleData call already logs the error, so we only need to
                    // show the error dialog to the user at this point.
                    DynamoDBUtils.showErrorDialogForServiceException(getActivity(),
                            getString(R.string.nosql_dialog_title_failed_operation_text), ex);
                    return;
                }
                ThreadUtils.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        final AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(getActivity());
                        dialogBuilder.setTitle(R.string.nosql_dialog_title_added_sample_data_text);
                        dialogBuilder.setMessage(R.string.nosql_dialog_message_added_sample_data_text);
                        dialogBuilder.setNegativeButton(R.string.nosql_dialog_ok_text, null);
                        dialogBuilder.show();
                    }
                });
            }
        }).start();
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

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

}
