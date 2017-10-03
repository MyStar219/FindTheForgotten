package pl.logiti.imagestorage.fragments;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import java.io.File;
import pl.logiti.imagestorage.R;
import pl.logiti.imagestorage.interfaces.FragmentCommunication;
import pl.logiti.imagestorage.utils.Const;


public class SplashFragment extends Fragment {

    private static final String ARG_FRAGMENT_NAME = "fragment_name";
    private String FRAGMENT_NAME;

    private FragmentCommunication fragmentCommunication;

    public SplashFragment() {
        // Required empty public constructor
    }


    public static SplashFragment newInstance(String name) {
        SplashFragment fragment = new SplashFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FRAGMENT_NAME = getArguments().getString(ARG_FRAGMENT_NAME);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_splash, container, false);
        loadComponents(view);

        return view;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof FragmentCommunication) {
            fragmentCommunication = (FragmentCommunication) context;
        } else {
            throw new RuntimeException(context.toString()+ " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        fragmentCommunication = null;
    }

    private void loadComponents(View view) {
        fragmentCommunication.setMenuInvisible();

        new CountDownTimer(3000, 1000) {

            public void onTick(long millisUntilFinished) {
                //here you can have your logic to set text to edittext
            }

            public void onFinish() {
                fragmentCommunication.replaceFragment(
                        MenuFragment.newInstance("MENU_FRAGMENT"), R.id.contentFrame, "MenuFragment");
            }

        }.start();
    }
}
