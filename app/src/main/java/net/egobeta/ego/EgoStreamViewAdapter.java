package net.egobeta.ego;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Lucas on 30/06/2016.
 */
public class EgoStreamViewAdapter extends ArrayAdapter<String> {

    private final Activity context;
    private final String[] web;
    private ArrayList<String> arrList;
    FacebookPictureViewRound userProfilePic;

    public EgoStreamViewAdapter(Activity context, String[] web, ArrayList<String> arrList) {
        super(context, R.layout.stream_gridviewitem, web);
        this.context = context;
        this.web = web;
        this.arrList = arrList; /**This should hold be the facebook id's*/
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.stream_gridviewitem, null, true);


        userProfilePic = (FacebookPictureViewRound) rowView.findViewById(R.id.img);
        userProfilePic.setPresetSize(FacebookPictureViewRound.CUSTOM);
        userProfilePic.setProfileId("699211431");


        /**Code to load pic from facebook id into facebookImageView**/
        return rowView;
    }

}
