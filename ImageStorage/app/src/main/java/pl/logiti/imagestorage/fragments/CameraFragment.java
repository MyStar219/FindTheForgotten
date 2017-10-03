package pl.logiti.imagestorage.fragments;

import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.ImageFormat;
import android.hardware.Camera;
import android.os.Bundle;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;
import pl.logiti.imagestorage.R;
import pl.logiti.imagestorage.interfaces.FragmentCommunication;
import pl.logiti.imagestorage.utils.Const;
import pl.logiti.imagestorage.utils.Utils;
import tyrantgit.explosionfield.ExplosionField;


public class CameraFragment extends Fragment implements
        SurfaceHolder.Callback,
        Camera.ShutterCallback,
        Camera.PictureCallback {

    private boolean PORTRAIT_MODE;

    private static final String ARG_FRAGMENT_NAME = "fragment_name";
    private String FRAGMENT_NAME;

    private AlertDialog alertDialog;

    private ImageView buttonTakePhoto;
    private ImageView buttonOrientation;

    private boolean previewIsRunning;
    private Camera camera;
    private SurfaceView preview;

    private FragmentCommunication fragmentCommunication;

    public CameraFragment() {
        PORTRAIT_MODE = true;
    }


    public static CameraFragment newInstance(String name) {
        CameraFragment fragment = new CameraFragment();
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
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_camera, container, false);
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

        buttonTakePhoto = (ImageView) view.findViewById(R.id.buttonTakePhoto);
        buttonOrientation = (ImageView) view.findViewById(R.id.buttonOrientation);

        buttonTakePhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(camera != null) {
                    onSnapClick();
                }
            }
        });

        buttonOrientation.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if(camera != null) changeCameraOrientation();
            }
        });

        try {
            preview = (SurfaceView) view.findViewById(R.id.preview);
            preview.getHolder().addCallback(this);
            preview.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);

            previewIsRunning = false;
            camera = Camera.open();

            camera.setPreviewDisplay(preview.getHolder());
            camera.setDisplayOrientation(90);
        } catch (IOException e) {
            Utils.showToastOnUIThread(getContext(), e.getMessage());
        }
    }

    @Override
    public void onResume() {
        myStartPreview();  // restart preview after awake from phone sleeping
        super.onResume();
    }
    @Override
    public void onPause() {
        myStopPreview();  // stop preview in case phone is going to sleep
        super.onPause();
    }

    public void onSnapClick() {
        camera.takePicture(this, null, null, this);

        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                ExplosionField explosionField = ExplosionField.attach2Window(getActivity());
                explosionField.explode(buttonTakePhoto);
            }
        };
        handler.postDelayed(r, 50);
    }

    @Override
    public void onShutter() {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View dialogView = inflater.inflate(R.layout.dialog_progress, null);
        builder.setView(dialogView);
        builder.setCancelable(false);
        alertDialog = builder.create();
        alertDialog.show();
    }


    @Override
    public void onPictureTaken(byte[] data, Camera camera) {
//        //Here, we chose internal storage
        try {
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length);//, options);
//            BitmapFactory.Options opt;
//
//            opt = new BitmapFactory.Options();
//            opt.inTempStorage = new byte[16 * 1024];
//            Camera.Parameters parameters = camera.getParameters();
//            Camera.Size size = parameters.getPictureSize();
//
//            int height11 = size.height;
//            int width11 = size.width;
//            float mb = (width11 * height11) / 1024000;
//
//            if (mb > 4f)
//                opt.inSampleSize = 4;
//            else if (mb > 3f)
//                opt.inSampleSize = 2;
//
//            //preview from camera
//            Bitmap bitmap = BitmapFactory.decodeByteArray(data, 0, data.length,opt);
//
//            if (PORTRAIT_MODE) bitmap = Utils.rotateBitmap(bitmap, 0);
//            else bitmap = Utils.rotateBitmap(bitmap, 270);

            saveBitmap(data, "tmp.jpg");
        }catch (OutOfMemoryError e){
            e.printStackTrace();
            System.gc();
        }
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
        myStartPreview();
//        camera.startPreview();
    }


    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        try {
            camera.setPreviewDisplay(preview.getHolder());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {
        myStopPreview();
        camera.release();
        camera = null;
    }


    private void saveBitmap(final byte[] data, final String filename) {
        Handler handler = new Handler();
        Runnable r = new Runnable() {
            public void run() {
                File dir = new File(Const.appFolder(getActivity()));
                if(!dir.exists()) dir.mkdirs();

                File file = new File(dir, filename);

                FileOutputStream outStream = null;
                try {
                    outStream = new FileOutputStream(file);
                    outStream.write(data);
                    outStream.close();
//                    Log.d("Log", "onPictureTaken - wrote bytes: " + data.length);
                    fragmentCommunication.replaceFragment(
                            DecideFragment.newInstance("DECIDE_FRAGMENT", !PORTRAIT_MODE), R.id.contentFrame, "DecideFragment");
                } catch (FileNotFoundException e) {
                    e.printStackTrace();

                } catch (IOException e) {
                    e.printStackTrace();
                } finally {

                }

//                File dir = new File(Const.appFolder);
//                if(!dir.exists()) dir.mkdirs();
//
//                File file = new File(dir, filename);
//                FileOutputStream fOut = null;
//                try {
//                    fOut = new FileOutputStream(file);
//                } catch (FileNotFoundException e) {
//                    e.printStackTrace();
//                }
//
//                bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fOut);
//
//                try {
//                    fOut.flush();
//                    fOut.close();
//
//                    fragmentCommunication.replaceFragment(
//                            DecideFragment.newInstance("DECIDE_FRAGMENT"), R.id.contentFrame, "DecideFragment");
//                } catch (IOException e) {
//                    e.printStackTrace();
//                }

                alertDialog.dismiss();
            }
        };
        handler.postDelayed(r, 50);
    }

    private void myStartPreview() {
        if (!previewIsRunning && (camera != null)) {
            Camera.Parameters params = camera.getParameters();
            List<Camera.Size> previewSizes = params.getSupportedPreviewSizes();
            Camera.Size selected = previewSizes.get(0);

            for(int i=0;i<previewSizes.size();i++) {
                if(previewSizes.get(i).width > selected.width)
                    selected = previewSizes.get(i);
            }

            params.setPreviewSize(selected.width,selected.height);
            params.set("orientation", "portrait");
            params.set("rotation", 90);

            List<Camera.Size> pictureSizes = params.getSupportedPictureSizes();
            Camera.Size selectedPicture = pictureSizes.get(0);

            for(int i=0;i<pictureSizes.size();i++) {
                if(pictureSizes.get(i).width > selectedPicture.width)
                    selectedPicture = pictureSizes.get(i);
            }

            params.setPictureFormat(ImageFormat.JPEG);
            params.setPictureSize(selectedPicture.width, selectedPicture.height);
//        params.setFlashMode(Camera.Parameters.FLASH_MODE_TORCH);
//            params.setFlashMode(Camera.Parameters.FLASH_MODE_ON);
            params.setFocusMode(Camera.Parameters.FOCUS_MODE_CONTINUOUS_PICTURE);

            //  BUG NA SAMSUNGU J5, Huawei 3c LTE - V.4.4.2

//            camera.autoFocus(new Camera.AutoFocusCallback() {
//                public void onAutoFocus(boolean success, Camera camera) {
////                    Utils.showToastOnUIThread(getActivity(), "FOCUSED! SHOOT PHOTO NOW!");
//                }
//            });

            camera.setParameters(params);
            camera.startPreview();
            previewIsRunning = true;
        }
    }

    // same for stopping the preview
    private void myStopPreview() {
        if (previewIsRunning && (camera != null)) {
            camera.stopPreview();
            previewIsRunning = false;
        }
    }


    private void changeCameraOrientation() {
        rotateImageView(buttonTakePhoto);
        rotateImageView(buttonOrientation);

        if(PORTRAIT_MODE) {
            PORTRAIT_MODE = false;
        } else {
            PORTRAIT_MODE = true;
        }
    }

    private void rotateImageView(ImageView imageView) {
        if(PORTRAIT_MODE) {
            final Animation animationRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_right);
            imageView.startAnimation(animationRotate);
        } else {
            final Animation animationRotate = AnimationUtils.loadAnimation(getActivity(), R.anim.rotate_left);
            imageView.startAnimation(animationRotate);
        }
    }
}
