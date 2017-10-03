package pl.logiti.imagestorage.interfaces;

import android.support.v4.app.Fragment;

import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * Created by logiti.pl on 2016-04-04.
 */
public interface FragmentCommunication {
    public void replaceFragment(Fragment fragment, int container, String fragmentTag);
    public void showFragment(Fragment fragment);

    //  searching
    public void setMenuInvisible();
    public void setMenuVisible();
    public void clearSearchView();

    public ImageLoader getImageLoader();
}
