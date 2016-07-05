package net.egobeta.ego;

import android.app.Activity;
import android.content.Context;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import net.amazonaws.mobile.util.ThreadUtils;
import net.egobeta.ego.demo.nosql.DemoNoSQLOperationListItem;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by Lucas on 30/06/2016.
 */
public class EgoStreamViewAdapter extends BaseAdapter {

    private final Activity context;
    private final String[] web;
    private ArrayList<String> arrList;
    private LayoutInflater mInflater;

    public EgoStreamViewAdapter(Activity context, String[] web, ArrayList<String> arrList) {
        mInflater = (LayoutInflater)context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        this.context = context;
        this.web = web;
        this.arrList = arrList; /**This should hold be the facebook id's*/
    }

    @Override
    public int getCount() {
        return arrList.size();
    }

    @Override
    public Object getItem(int position) {
        return arrList.get(position);
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
            convertView = mInflater.inflate(R.layout.stream_gridviewitem, null);
            viewHolder.userProfilePic = (FacebookPictureViewRound) convertView.findViewById(R.id.img);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder)convertView.getTag();
        }
            try{
                viewHolder.userProfilePic.setPresetSize(FacebookPictureViewRound.NORMAL);
                viewHolder.userProfilePic.setProfileId("699211431");
            } catch(OutOfMemoryError e) {
                throw e;
    }

        return convertView;
    }


    public class ViewHolder{
        FacebookPictureViewRound userProfilePic;
    }
}
