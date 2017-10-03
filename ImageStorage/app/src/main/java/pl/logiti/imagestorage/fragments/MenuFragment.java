package pl.logiti.imagestorage.fragments;

import android.content.Context;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import pl.logiti.imagestorage.R;
import pl.logiti.imagestorage.interfaces.FragmentCommunication;


public class MenuFragment extends Fragment {

    private static final String ARG_FRAGMENT_NAME = "fragment_name";
    private String FRAGMENT_NAME;

    private Button buttonPlace;
    private Button buttonFind;

    private FragmentCommunication fragmentCommunication;

    public MenuFragment() {
        // Required empty public constructor
    }


    public static MenuFragment newInstance(String name) {
        MenuFragment fragment = new MenuFragment();
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
        View view = inflater.inflate(R.layout.fragment_menu, container, false);
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

        buttonPlace = (Button) view.findViewById(R.id.buttonPlace);
        buttonFind = (Button) view.findViewById(R.id.buttonFind);

        buttonPlace.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentCommunication.replaceFragment(
                        CameraFragment.newInstance("CAMERA_FRAGMENT"), R.id.contentFrame, "CameraFragment");
            }
        });

        buttonFind.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentCommunication.replaceFragment(
                        ListFragment.newInstance("LIST_FRAGMENT"), R.id.contentFrame, "ListFragment");
            }
        });
    }

}
