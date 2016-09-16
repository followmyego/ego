package net.egobeta.ego.Fragments;

import android.graphics.Typeface;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import net.egobeta.ego.R;


public class OnBoarding_Fragment123 extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private int position;
    private View view;
    Typeface typeface;

    //View Items
    private TextView slideText;
    private ImageView slideImageView;
    private ImageView phoneContent;



    public static OnBoarding_Fragment123 newInstance(String param1) {
        OnBoarding_Fragment123 fragment = new OnBoarding_Fragment123();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public OnBoarding_Fragment123() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        if (getArguments() != null) {
            String arg = getArguments().getString(ARG_PARAM1);
            assert arg != null;
            position = Integer.parseInt(arg);
        }

        /**Initialize font*/
        typeface = Typeface.createFromAsset(getActivity().getAssets(), "fonts/ChaletNewYorkNineteenEighty.ttf");
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.onboarding_slide, container, false);

        InitializeViewItems();


        return view;
    }

    private void InitializeViewItems() {
        slideText = (TextView) view.findViewById(R.id.onBoarding_text);
        slideImageView = (ImageView) view.findViewById(R.id.onBoarding_phone);
        phoneContent = (ImageView) view.findViewById(R.id.phone_content);

        if(position == 0){
            slideText.setText("Ego shows you the profiles of the people around you.");
            phoneContent.setImageResource(R.drawable.onboarding_1);
        } else if(position == 1){
            slideText.setText("Badges show you why those people are important to you.");
            phoneContent.setImageResource(R.drawable.onboarding_2);
        } else if(position == 2){
            slideText.setText("Highlighted people are in the same room as you.");
            phoneContent.setImageResource(R.drawable.onboarding_3);
        }

        slideText.setTypeface(typeface);
    }


}
