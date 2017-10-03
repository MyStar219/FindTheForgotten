package pl.logiti.imagestorage.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;

import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import java.io.ByteArrayOutputStream;
import java.io.File;
import pl.logiti.imagestorage.R;
import pl.logiti.imagestorage.activities.MainActivity;
import pl.logiti.imagestorage.interfaces.FragmentCommunication;
import pl.logiti.imagestorage.utils.Const;
import pl.logiti.imagestorage.utils.Utils;


public class DecideFragment extends Fragment {

    private static final String ARG_FRAGMENT_NAME = "fragment_name";
    private static final String ARG_IS_ROTATE = "is_rotate";
    private String FRAGMENT_NAME;
    private boolean IS_ROTATE;

    private Button buttonRetake;
    private Button buttonSave;
    private ImageView preview;

    private FragmentCommunication fragmentCommunication;

    public DecideFragment() {
        // Required empty public constructor
    }


    public static DecideFragment newInstance(String name, boolean isRotate) {
        DecideFragment fragment = new DecideFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_NAME, name);
        args.putBoolean(ARG_IS_ROTATE, isRotate);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FRAGMENT_NAME = getArguments().getString(ARG_FRAGMENT_NAME);
            IS_ROTATE = getArguments().getBoolean(ARG_IS_ROTATE, false);
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
        View view = inflater.inflate(R.layout.fragment_decide, container, false);
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

        buttonRetake = (Button) view.findViewById(R.id.buttonRetake);
        buttonSave = (Button) view.findViewById(R.id.buttonSave);
        preview = (ImageView) view.findViewById(R.id.preview);

        buttonRetake.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentCommunication.replaceFragment(
                        CameraFragment.newInstance("CAMERA_FRAGMENT"), R.id.contentFrame, "CameraFragment");
            }
        });

        buttonSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentCommunication.replaceFragment(
                        SaveFragment.newInstance("SAVE_FRAGMENT"), R.id.contentFrame, "SaveFragment");
            }
        });

        final String file_path = Const.appFolder(getActivity()) +"/tmp.jpg";
        File dir = new File(file_path);
        if(dir.exists()){
            String decodedImgUri = Uri.fromFile(dir).toString();
            decodedImgUri = decodedImgUri.replaceAll("%20", " ").replaceAll("%26", "&");

            fragmentCommunication.getImageLoader().setDefaultLoadingListener(new ImageLoadingListener() {
                AlertDialog alertDialog;

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {

                }

                @Override
                public void onLoadingComplete(String imageUri, View view, final Bitmap loadedImage) {
                    if(IS_ROTATE) new RotateTask(loadedImage, file_path).execute();
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {

                }
            });

            fragmentCommunication.getImageLoader().displayImage(decodedImgUri, preview);
        }
    }

    private class RotateTask extends AsyncTask<Void, Void, Bitmap> {

        AlertDialog alertDialog;
        Bitmap loadedImage;
        String file_path;

        RotateTask(Bitmap loadedImage, String file_path) {
            this.loadedImage = loadedImage;
            this.file_path = file_path;

            AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
            LayoutInflater inflater = getActivity().getLayoutInflater();
            View dialogView = inflater.inflate(R.layout.dialog_rotate, null);
            builder.setView(dialogView);
            builder.setCancelable(false);
            alertDialog = builder.create();
        }

        // Runs in UI before background thread is called
        @Override
        protected void onPreExecute() {
            super.onPreExecute();

            alertDialog.show();
        }

        // This is run in a background thread
        @Override
        protected Bitmap doInBackground(Void... params) {
            Bitmap rotatedImage = null;
            if(IS_ROTATE) {
                rotatedImage = Utils.rotateBitmap(loadedImage, 270);

                Utils.saveBitmapToFile(rotatedImage, file_path);
            }

            return rotatedImage;
        }

        // This is called from background thread but runs in UI
        @Override
        protected void onProgressUpdate(Void... values) {
            super.onProgressUpdate(values);

            // Do things like update the progress bar
        }

        // This runs in UI when background thread finishes
        @Override
        protected void onPostExecute(Bitmap rotatedImage) {
            super.onPostExecute(rotatedImage);
            if(rotatedImage != null)preview.setImageBitmap(rotatedImage);
            // Do things like hide the progress bar or change a TextView
            alertDialog.dismiss();
        }
    }


}
