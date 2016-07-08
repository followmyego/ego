package net.egobeta.ego.InstagramClasses;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;


import com.squareup.picasso.Picasso;

import net.egobeta.ego.R;

import java.util.ArrayList;

/**
 * Created by Lucas on 13/03/2016.
 */

public class ImageViewAdapterInstagram extends ArrayAdapter<String>{

    private final Activity context;
    private ArrayList<String> arrList;


    public ImageViewAdapterInstagram(Activity context, String[] web, Integer[] imageId, ArrayList<String> arrList) {
        super(context, R.layout.instagram_gridviewitem, web);
        this.context = context;
        this.arrList = arrList;
    }


    @Override
    public View getView(int position, View view, ViewGroup parent) {
        LayoutInflater inflater = context.getLayoutInflater();
        View rowView= inflater.inflate(R.layout.instagram_gridviewitem, null, true);


        ImageView imageView = (ImageView) rowView.findViewById(R.id.img);
        Picasso.with(getContext()).load(arrList.get(position)).into(imageView);
        //imageView.setImageResource(imageId[position]);
        return rowView;
    }


}