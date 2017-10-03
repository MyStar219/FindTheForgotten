package pl.logiti.imagestorage.fragments;

import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Point;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.view.Display;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.ImageLoadingListener;

import org.w3c.dom.Text;

import java.io.File;
import pl.logiti.imagestorage.R;
import pl.logiti.imagestorage.database.DatabaseOperations;
import pl.logiti.imagestorage.interfaces.FragmentCommunication;
import pl.logiti.imagestorage.models.Category;
import pl.logiti.imagestorage.models.Item;
import pl.logiti.imagestorage.utils.Utils;


public class ShowFragment extends Fragment {

    private static final String ARG_FRAGMENT_NAME = "fragment_name";
    private static final String ARG_CATEGORY_ID = "category_id";
    private static final String ARG_ITEM_ID = "item_id";
    private String FRAGMENT_NAME;
    private int CATEGORY_ID;
    private int ITEM_ID;

    private ImageView imageViewPicture;
    private TextView textViewDescription;
    private TextView textViewCategory;
    private Button buttonKeep;
    private Button buttonDelete;

    private Item item;

    private FragmentCommunication fragmentCommunication;

    public ShowFragment() {
        // Required empty public constructor
    }


    public static ShowFragment newInstance(String name, int itemId, int categoryId) {
        ShowFragment fragment = new ShowFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_NAME, name);
        args.putInt(ARG_ITEM_ID, itemId);
        args.putInt(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FRAGMENT_NAME = getArguments().getString(ARG_FRAGMENT_NAME);
            ITEM_ID = getArguments().getInt(ARG_ITEM_ID);
            CATEGORY_ID = getArguments().getInt(ARG_CATEGORY_ID);
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
        View view = inflater.inflate(R.layout.fragment_show, container, false);
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
        fragmentCommunication.clearSearchView();

        buttonKeep = (Button) view.findViewById(R.id.buttonKeep);
        buttonDelete = (Button) view.findViewById(R.id.buttonDelete);
        textViewDescription = (TextView) view.findViewById(R.id.textViewDescription);
        textViewCategory = (TextView) view.findViewById(R.id.textViewCategory);
        imageViewPicture = (ImageView) view.findViewById(R.id.imageViewPicture);

        item = DatabaseOperations.getItem(getActivity(), ITEM_ID);

        if(item != null) {
            textViewDescription.setText(item.getDescription());
            Category category = DatabaseOperations.getCategory(getActivity(), item.getCategoryId());
            textViewCategory.setText(category.getName());

            /************************/

            String decodedImgUri = Uri.fromFile(new File(item.getPath())).toString();
            decodedImgUri = decodedImgUri.replaceAll("%20", " ").replaceAll("%26", "&");
            Utils.LogInfo(decodedImgUri, "URI:");
            fragmentCommunication.getImageLoader().setDefaultLoadingListener(new ImageLoadingListener() {
                AlertDialog alertDialog;

                @Override
                public void onLoadingStarted(String imageUri, View view) {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    View dialogView = inflater.inflate(R.layout.dialog_progress, null);
                    builder.setView(dialogView);
                    builder.setCancelable(false);
                    alertDialog = builder.create();
                    alertDialog.show();
                }

                @Override
                public void onLoadingFailed(String imageUri, View view, FailReason failReason) {
                    alertDialog.dismiss();
                }

                @Override
                public void onLoadingComplete(String imageUri, View view, Bitmap loadedImage) {
                    alertDialog.dismiss();
                }

                @Override
                public void onLoadingCancelled(String imageUri, View view) {
                    alertDialog.dismiss();
                }
            });
            fragmentCommunication.getImageLoader().displayImage(decodedImgUri, imageViewPicture);

            /************************/

            buttonKeep.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    fragmentCommunication.replaceFragment(
                            ListFragment.newInstance("LIST_FRAGMENT", CATEGORY_ID), R.id.contentFrame, "ListFragment");
                }
            });

            buttonDelete.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    LayoutInflater inflater = getActivity().getLayoutInflater();
                    final View dialogView = inflater.inflate(R.layout.dialog_remove, null);
                    builder.setView(dialogView);
                    builder.setCancelable(false);
                    final AlertDialog alertDialog = builder.create();

                    Button buttonPositive = (Button) dialogView.findViewById(R.id.buttonPositive);
                    Button buttonNegative = (Button) dialogView.findViewById(R.id.buttonNegative);
                    buttonPositive.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            File dir = new File(item.getPath());
                            dir.delete();
                            File thumbDir = new File(item.getPathThumb());
                            thumbDir.delete();
                            DatabaseOperations.removeItem(getActivity(), item);
                            Utils.showToastOnUIThread(getActivity(), "Item deleted");
                            alertDialog.dismiss();
                            fragmentCommunication.replaceFragment(
                                    ListFragment.newInstance("LIST_FRAGMENT", CATEGORY_ID), R.id.contentFrame, "ListFragment");
                        }
                    });

                    buttonNegative.setOnClickListener(new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            alertDialog.dismiss();
                        }
                    });

                    alertDialog.show();
                }
            });
        } else {
            buttonDelete.setEnabled(false);
            buttonKeep.setEnabled(false);
        }
    }

    public int getCategoryId() {
        if(item != null) {
            return item.getCategoryId();
        } else return -1;
    }
}
