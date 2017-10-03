package pl.logiti.imagestorage.adapters;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.List;

import pl.logiti.imagestorage.R;
import pl.logiti.imagestorage.models.Category;
import pl.logiti.imagestorage.models.Item;

public class CategoryAdapter extends ArrayAdapter<Category>{

    // Your sent context
    private Context context;
    // Your custom values for the spinner (User)
    private Category[] categories;

    public CategoryAdapter(Context context, int textViewResourceId, Category[] categories) {
        super(context, textViewResourceId, categories);
        this.context = context;
        this.categories = categories;
    }

    public int getCount(){
        return categories.length;
    }

    public Category getItem(int position){
        return categories[position];
    }

    public long getItemId(int position){
        return position;
    }


    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View view = View.inflate(context, R.layout.category_item, null);
        TextView textView = (TextView) view.findViewById(R.id.textViewCategory);
        textView.setText(categories[position].getName());

        return view;
    }

    @Override
    public View getDropDownView(int position, View convertView,
                                ViewGroup parent) {
        View view = View.inflate(context, R.layout.category_item, null);
        TextView textView = (TextView) view.findViewById(R.id.textViewCategory);
        textView.setText(categories[position].getName());

        return view;
    }
}