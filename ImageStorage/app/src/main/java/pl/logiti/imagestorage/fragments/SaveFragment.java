package pl.logiti.imagestorage.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import pl.logiti.imagestorage.R;
import pl.logiti.imagestorage.adapters.CategoryAdapter;
import pl.logiti.imagestorage.database.DatabaseOperations;
import pl.logiti.imagestorage.interfaces.FragmentCommunication;
import pl.logiti.imagestorage.models.Category;
import pl.logiti.imagestorage.models.Item;
import pl.logiti.imagestorage.utils.Const;
import pl.logiti.imagestorage.utils.Utils;


public class SaveFragment extends Fragment {

    private static final String ARG_FRAGMENT_NAME = "fragment_name";
    private String FRAGMENT_NAME;

    private EditText editTextPlace;
    private Button buttonDone;
    private Spinner spinnerCategory;

    private Item item;

    private FragmentCommunication fragmentCommunication;

    public SaveFragment() {
        // Required empty public constructor
    }


    public static SaveFragment newInstance(String name) {
        SaveFragment fragment = new SaveFragment();
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
        View view = inflater.inflate(R.layout.fragment_save, container, false);
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

        buttonDone = (Button) view.findViewById(R.id.buttonDone);
        editTextPlace = (EditText) view.findViewById(R.id.editTextPlace);
        spinnerCategory = (Spinner) view.findViewById(R.id.spinnerCategory);

        buttonDone.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String place = editTextPlace.getText().toString();
                if(place.isEmpty()) {
                    Utils.showToastOnUIThread(getActivity(), "Place is required");
                } else {
                    CategoryAdapter categoryAdapter = (CategoryAdapter) spinnerCategory.getAdapter();
                    Category category = categoryAdapter.getItem(spinnerCategory.getSelectedItemPosition());
                    if (category != null) {
                        Utils.LogInfo(category.getName(), Const.appTAG + " category:");
                        SimpleDateFormat sdf = new SimpleDateFormat("yyMMdd_HHmmss");
                        String newFilePath = Const.appFolder(getActivity()) + "/" + category.getName() + "/" + sdf.format(new Date()) + ".jpg";
                        String newThumbnileFilePath = Const.appFolder(getActivity()) + "/" + category.getName() + "/thumb_" + sdf.format(new Date()) + ".jpg";
                        item = new Item(category.getId(),
                                newFilePath,
                                newThumbnileFilePath,
                                place);
                        //                    Utils.LogInfo(Integer.toString(category.getId()), Const.appTAG +" category ID:");
                        //                    Utils.LogInfo(newFilePath, Const.appTAG +" item file path:");
                        //                    Utils.LogInfo(editTextPlace.getText().toString(), Const.appTAG +" item desc:");

                        if (DatabaseOperations.saveItem(getActivity(), item) &&
                                Utils.copyFileToCategory(getActivity(), newFilePath, newThumbnileFilePath)) {
                            Utils.closeKeyboard(getActivity());
                            Utils.showToastOnUIThread(getActivity(), "Item saved successfully");
                            fragmentCommunication.replaceFragment(
                                    MenuFragment.newInstance("MENU_FRAGMENT"), R.id.contentFrame, "MenuFragment");
                        } else {
                            Utils.showToastOnUIThread(getActivity(), "Item wasn't saved");
                        }
                    }
                }
            }
        });

        List<Category> categories = DatabaseOperations.getAllCategoriesWithoutALL(getActivity());
        Category[] categoriesArray = new Category[categories.size()];
        categoriesArray = categories.toArray(categoriesArray);

        Utils.createFoldersIfNotExist(getActivity(), categories);

        spinnerCategory.setAdapter(new CategoryAdapter(getActivity(), R.layout.category_item, categoriesArray));
        //  others
        spinnerCategory.setSelection(11);
    }

}
