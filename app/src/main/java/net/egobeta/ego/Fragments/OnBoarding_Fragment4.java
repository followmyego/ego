package net.egobeta.ego.Fragments;

import android.content.Intent;
import android.support.v4.app.Fragment;
import android.graphics.Typeface;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import net.egobeta.ego.MainActivity;
import net.egobeta.ego.OnBoarding.Main_OnBoarding;
import net.egobeta.ego.R;


public class OnBoarding_Fragment4 extends Fragment implements View.OnClickListener {

    private static final String ARG_PARAM1 = "param1";
    private int position;
    private View view;
    Typeface typeface;

    //View Items
    private TextView headerText;
    private ImageButton doneButton;



    public static OnBoarding_Fragment4 newInstance(String param1) {
        OnBoarding_Fragment4 fragment = new OnBoarding_Fragment4();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        fragment.setArguments(args);
        return fragment;
    }

    public OnBoarding_Fragment4() {
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
        view = inflater.inflate(R.layout.onboarding_slide_4, container, false);

        InitializeViewItems();
        SetOnClickListeners();

        return view;
    }

    private void InitializeViewItems() {

        headerText = (TextView) view.findViewById(R.id.header_text);
        doneButton = (ImageButton) view.findViewById(R.id.onboarding_button);
        headerText.setTypeface(typeface);

    }

    private void SetOnClickListeners(){

//        doneButton.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//
//                /** Get user privacy preferences from the listView that the user has checked **/
//
//                /** Sync user settings with the items from the listView **/
//
//                /** Launch SettingUpAccount activity  **/
//
//                Intent intent = new Intent(getActivity(), MainActivity.class);
//                startActivity(intent);
//            }
//        });

        doneButton.setOnClickListener(this);

    }


    @Override
    public void onClick(View v) {
        if(v == doneButton){
            ((Main_OnBoarding) getActivity()).goToMainActivity();
        }
    }
}

