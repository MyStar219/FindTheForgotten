package pl.logiti.imagestorage.fragments;

import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.GridLayoutManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import java.util.List;
import pl.logiti.imagestorage.R;
import pl.logiti.imagestorage.adapters.CategoryAdapter;
import pl.logiti.imagestorage.adapters.ItemsAdapter;
import pl.logiti.imagestorage.database.DatabaseOperations;
import pl.logiti.imagestorage.interfaces.FragmentCommunication;
import pl.logiti.imagestorage.models.Category;
import pl.logiti.imagestorage.models.Item;
import pl.logiti.imagestorage.utils.Const;
import pl.logiti.imagestorage.utils.Utils;


public class ListFragment extends Fragment {

    private RecyclerView recyclerViewImageList;
    private RecyclerView.Adapter recyclerViewAdapter;
    private RecyclerView.LayoutManager layoutManager;
    private Spinner spinnerCategory;

    private static final String ARG_FRAGMENT_NAME = "fragment_name";
    private static final String ARG_CATEGORY_ID = "category_id";
    private String FRAGMENT_NAME;
    private int CATEGORY_ID;

    private FragmentCommunication fragmentCommunication;

    public ListFragment() {
        // Required empty public constructor
    }


    public static ListFragment newInstance(String name) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_NAME, name);
        fragment.setArguments(args);
        return fragment;
    }

    public static ListFragment newInstance(String name, int categoryId) {
        ListFragment fragment = new ListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_FRAGMENT_NAME, name);
        args.putInt(ARG_CATEGORY_ID, categoryId);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            FRAGMENT_NAME = getArguments().getString(ARG_FRAGMENT_NAME);
            CATEGORY_ID = getArguments().getInt(ARG_CATEGORY_ID, 13);
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
        View view = inflater.inflate(R.layout.fragment_list, container, false);
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
        fragmentCommunication.setMenuVisible();

        recyclerViewImageList = (RecyclerView) view.findViewById(R.id.recyclerViewImageList);
        spinnerCategory = (Spinner) view.findViewById(R.id.spinnerCategory);

        // use this setting to improve performance if you know that changes
        // in content do not change the layout size of the RecyclerView
        recyclerViewImageList.setHasFixedSize(true);

        // use a linear layout manager
//        layoutManager = new LinearLayoutManager(getActivity());
        layoutManager = new GridLayoutManager(getActivity(), 2, GridLayoutManager.VERTICAL, false);
        recyclerViewImageList.setLayoutManager(layoutManager);

        List<Category> categories = DatabaseOperations.getAllCategories(getActivity());
        Category[] categoriesArray = new Category[categories.size()];
        categoriesArray = categories.toArray(categoriesArray);

        Utils.createFoldersIfNotExist(getActivity(), categories);

        spinnerCategory.setAdapter(new CategoryAdapter(getActivity(), R.layout.category_item, categoriesArray));

        List<Item> items = null;
        if(CATEGORY_ID == -1) {
            items = DatabaseOperations.getItemByCategory(getActivity(), 1);
        } else {
            if(CATEGORY_ID == 13) {
                items = DatabaseOperations.getAllItems(getActivity());
            } else {
                items = DatabaseOperations.getItemByCategory(getActivity(), CATEGORY_ID);
            }

            spinnerCategory.setSelection(CATEGORY_ID - 1);
        }

        // specify an adapter
        recyclerViewAdapter = new ItemsAdapter(getActivity(), fragmentCommunication, items, CATEGORY_ID);
        recyclerViewImageList.setAdapter(recyclerViewAdapter);

        spinnerCategory.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int i, long l) {
                fragmentCommunication.clearSearchView();
                Category category = (Category)spinnerCategory.getSelectedItem();
                showItemsForCategory(category.getId());
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterView) {

            }
        });

        showItemsForCategory(CATEGORY_ID);

//        layoutManager = new GridLayoutManager(this, 2, orientation, reverseLayout);
//        recyclerView.setLayoutManager(layoutManager);

//        int spanCount = 3; // 3 columns
//        int spacing = 50; // 50px
//        boolean includeEdge = false;
//        recyclerViewImageList.addItemDecoration(new GridSpacingItemDecoration(spanCount, spacing, includeEdge));
    }

    private void showItemsForCategory(int categoryId) {
        CategoryAdapter categoryAdapter = (CategoryAdapter) spinnerCategory.getAdapter();
        Category category = categoryAdapter.getItem(spinnerCategory.getSelectedItemPosition());
        if(category != null) {
            List<Item> items = null;
            if(category.getId() == 13) {    //  ALL
                items = DatabaseOperations.getAllItems(getActivity());
            } else {
                items = DatabaseOperations.getItemByCategory(getActivity(), category.getId());
            }

            if (items != null) {
                ItemsAdapter itemsAdapter = (ItemsAdapter) recyclerViewImageList.getAdapter();
                itemsAdapter.setModel(items, categoryId);
            }
        }
    }

    public void updateItems(String query) {
        Category category = (Category)spinnerCategory.getSelectedItem();
        final List<Item> filteredItems = DatabaseOperations.getFilteredItems(getActivity(), query, category.getId());
        ItemsAdapter itemsAdapter = (ItemsAdapter)recyclerViewAdapter;
        itemsAdapter.setModel(filteredItems, category.getId());
        recyclerViewImageList.scrollToPosition(0);
    }
}
