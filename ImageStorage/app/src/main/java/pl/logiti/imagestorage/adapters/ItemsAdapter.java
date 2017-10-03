package pl.logiti.imagestorage.adapters;

import android.app.ProgressDialog;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.support.v4.app.FragmentActivity;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import java.util.List;
import pl.logiti.imagestorage.R;
import pl.logiti.imagestorage.fragments.ShowFragment;
import pl.logiti.imagestorage.interfaces.FragmentCommunication;
import pl.logiti.imagestorage.models.Item;
import pl.logiti.imagestorage.utils.Utils;


/**
 * Created by logiti.pl on 2016-04-07.
 */
public class ItemsAdapter extends RecyclerView.Adapter<ItemsAdapter.ViewHolder> {

    private FragmentActivity context;
    private FragmentCommunication fragmentCommunication;
    private List<Item> items;
    private int categoryId;

    public void setModel(List<Item> model, int categoryId) {
        this.items = model;
        this.categoryId = categoryId;
        this.notifyDataSetChanged();
    }


    public static class ViewHolder extends RecyclerView.ViewHolder {
        public ImageView imageViewItem;

        public ViewHolder(View itemView) {
            super(itemView);

            imageViewItem = (ImageView) itemView.findViewById(R.id.imageViewItem);
        }
    }

    public ItemsAdapter(FragmentActivity context, FragmentCommunication fragmentCommunication,
                        List<Item> items, int categoryId) {
        this.fragmentCommunication = fragmentCommunication;
        this.context = context;
        this.items = items;
        this.categoryId = categoryId;
    }

    @Override
    public ItemsAdapter.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.image_item, parent, false);
        ViewHolder viewHolder = new ViewHolder(view);

        viewHolder.imageViewItem = (ImageView)view.findViewById(R.id.imageViewItem);

        return viewHolder;
    }



    @Override
    public void onBindViewHolder(ViewHolder holder, final int position) {
        final Item item = items.get(position);

        Bitmap bitmap = BitmapFactory.decodeFile(item.getPathThumb());
        holder.imageViewItem.setImageBitmap(bitmap);

        holder.imageViewItem.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                fragmentCommunication.replaceFragment(
                        ShowFragment.newInstance("SHOW_FRAGMENT", item.getId(), categoryId), R.id.contentFrame, "ShowFragment");
            }
        });

        holder.itemView.setTag(item);
    }

    @Override
    public int getItemCount() {
        return items.size();
    }


}



