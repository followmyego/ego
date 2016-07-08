package net.egobeta.ego;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import java.util.ArrayList;

/**
 * Created by Lucas on 30/06/2016.
 */
public class EgoStreamViewAdapter2 extends ArrayAdapter<String> {

    private final Activity context;
    private ArrayList<String> arrList;
    private String[] facebookIds;

    public EgoStreamViewAdapter2(Activity context, String[] facebookIds, ArrayList<String> arrList) {
        super(context, R.layout.stream_gridviewitem, facebookIds);
        this.context = context;
        this.arrList = arrList;
        this.facebookIds = facebookIds;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.stream_gridviewitem, null, true);


        FacebookPictureViewRound userProfilePic = (FacebookPictureViewRound) rowView.findViewById(R.id.img);
//        userProfilePic.setPresetSize(FacebookPictureViewRound.NORMAL);
        userProfilePic.setProfileId(facebookIds[position]);
        return rowView;
    }

}
