package pl.logiti.imagestorage.activities;

import android.Manifest;
import android.annotation.TargetApi;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.view.MenuItemCompat;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.SearchView;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Toast;

import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.logiti.imagestorage.CoreApplication;
import pl.logiti.imagestorage.R;
import pl.logiti.imagestorage.fragments.ListFragment;
import pl.logiti.imagestorage.fragments.MenuFragment;
import pl.logiti.imagestorage.fragments.ShowFragment;
import pl.logiti.imagestorage.fragments.SplashFragment;
import pl.logiti.imagestorage.interfaces.FragmentCommunication;
import pl.logiti.imagestorage.utils.Utils;

public class MainActivity extends AppCompatActivity implements FragmentCommunication, SearchView.OnQueryTextListener {

    private SearchView searchView;
    protected CoreApplication app;
    private final int REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS = 200;

    ImageLoader imageLoader;
    DisplayImageOptions options;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        try {
            app = (CoreApplication) getApplication();
        } catch (Exception e) {
         Utils.showToastOnUIThread(this, e.getMessage());
        }
        AdView adView=(AdView)findViewById(R.id.AdView);
        loadComponents();
    }

    //  FRAGMENT COMMUNICATION INTERFACE METODS

    @Override
    public void replaceFragment(Fragment fragment, int container, String fragmentTag) {
        FragmentManager fragmentManager = getSupportFragmentManager();
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
//        fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right);
        fragmentTransaction.setCustomAnimations(R.anim.slide_in_bottom, R.anim.slide_out_top);
        fragmentTransaction.replace(container, fragment, fragmentTag);
        fragmentTransaction.commit();
    }

    @Override
    public void showFragment(Fragment fragment) {
        FragmentTransaction fragmentTransaction = getSupportFragmentManager().beginTransaction();
        fragmentTransaction.show(fragment);
    }


    @Override
    public void onBackPressed() {
        MenuFragment fragmentMenu = (MenuFragment) getSupportFragmentManager().findFragmentByTag("MenuFragment");
        ShowFragment fragmentShow = (ShowFragment) getSupportFragmentManager().findFragmentByTag("ShowFragment");

        if (fragmentMenu != null && fragmentMenu.isVisible()) finish();
        else {
            if(fragmentShow != null && fragmentShow.isVisible()) {
                replaceFragment(ListFragment.newInstance("LIST_FRAGMENT", fragmentShow.getCategoryId()), R.id.contentFrame, "ListFragment");
            } else {
                clearSearchView();
                replaceFragment(MenuFragment.newInstance("MENU_FRAGMENT"), R.id.contentFrame, "MenuFragment");
            }
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_category, menu);
        final MenuItem item = menu.findItem(R.id.action_search);
        searchView = (SearchView) menu.findItem(R.id.action_search).getActionView();
        final SearchView searchView = (SearchView) MenuItemCompat.getActionView(item);
        searchView.setOnQueryTextListener(this);

        return true;
    }

    @Override
    public boolean onQueryTextChange(String query) {
        if(query.isEmpty()) {
            ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentByTag("ListFragment");

            if (fragment != null && fragment.isVisible()) {
                fragment.updateItems(query);
            }

            Utils.closeKeyboard(this);
        }
        return true;
    }

    @Override
    public boolean onQueryTextSubmit(String query) {
        ListFragment fragment = (ListFragment) getSupportFragmentManager().findFragmentByTag("ListFragment");

        if (fragment != null && fragment.isVisible()) {
            fragment.updateItems(query);
        }

        Utils.closeKeyboard(this);

        return true;
    }

    //  MY METHODS

    private void loadComponents() {
        getPermission();

        setMenuInvisible();
        //  starting fragment
        replaceFragment(SplashFragment.newInstance("SPLASH_FRAGMENT"), R.id.contentFrame, "SplashFragment");

        configureImageLoader();
    }

    private void configureImageLoader() {
        imageLoader = ImageLoader.getInstance();
        imageLoader.init(ImageLoaderConfiguration.createDefault(MainActivity.this));

        options = new DisplayImageOptions.Builder()
                .showStubImage(R.mipmap.ic_launcher)
                .showImageForEmptyUri(R.mipmap.ic_launcher)
                .cacheOnDisc()
                .cacheInMemory()
                .build();
    }

    @Override
    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    @Override
    public void setMenuVisible(){
//        menu.setGroupVisible(R.id.main_menu_group, true);
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.show();
    }

    @Override
    public void clearSearchView() {
        if (searchView != null) {
            searchView.onActionViewCollapsed();
        }
    }

    @Override
    public void setMenuInvisible(){
        android.support.v7.app.ActionBar actionBar = getSupportActionBar();
        if(actionBar != null) actionBar.hide();
    }

    private void showMessageOKCancel(String message, DialogInterface.OnClickListener okListener,
                                     DialogInterface.OnClickListener cancelListener) {
        new AlertDialog.Builder(MainActivity.this)
                .setMessage(message)
                .setPositiveButton("OK", okListener)
                .setNegativeButton("Cancel", cancelListener)
                .setCancelable(false)
                .create()
                .show();
    }

    @TargetApi(Build.VERSION_CODES.M)
    private void getPermission() {
        if (android.os.Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            List<String> permissionsNeeded = new ArrayList<String>();

            final List<String> permissionsList = new ArrayList<String>();
            if (!addPermission(permissionsList, Manifest.permission.CAMERA))
                permissionsNeeded.add("Camera");
            if (!addPermission(permissionsList, Manifest.permission.WRITE_EXTERNAL_STORAGE))
                permissionsNeeded.add("Write storage");
            if (!addPermission(permissionsList, Manifest.permission.INTERNET))
                permissionsNeeded.add("Internet");

            if (permissionsList.size() > 0) {
                if (permissionsNeeded.size() > 0) {
                    // Need Rationale
                    String message = "You need to grant access to " + permissionsNeeded.get(0);
                    for (int i = 1; i < permissionsNeeded.size(); i++)
                        message = message + ", " + permissionsNeeded.get(i);
                    showMessageOKCancel(message,
                            new DialogInterface.OnClickListener() {
                                @TargetApi(Build.VERSION_CODES.M)
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                                            REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                                }
                            },
                            new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(MainActivity.this, "Some Permissions are Denied", Toast.LENGTH_SHORT).show();
                                    finish();
                                }
                            });
                    return;
                }
                requestPermissions(permissionsList.toArray(new String[permissionsList.size()]),
                        REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS);
                return;
            }
        } else {
            //  do something after permission granted
        }
    }

    @TargetApi(Build.VERSION_CODES.M)
    private boolean addPermission(List<String> permissionsList, String permission) {
        if (checkSelfPermission(permission) != PackageManager.PERMISSION_GRANTED) {
            permissionsList.add(permission);
            // Check for Rationale Option
            if (!shouldShowRequestPermissionRationale(permission))
                return false;
        }
        return true;
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_CODE_ASK_MULTIPLE_PERMISSIONS:
            {
                Map<String, Integer> perms = new HashMap<String, Integer>();
                // Initial
                perms.put(Manifest.permission.CAMERA, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.WRITE_EXTERNAL_STORAGE, PackageManager.PERMISSION_GRANTED);
                perms.put(Manifest.permission.INTERNET, PackageManager.PERMISSION_GRANTED);
                // Fill with results
                for (int i = 0; i < permissions.length; i++)
                    perms.put(permissions[i], grantResults[i]);
                // Check for ACCESS_FINE_LOCATION
                if (perms.get(Manifest.permission.CAMERA) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED
                        && perms.get(Manifest.permission.INTERNET) == PackageManager.PERMISSION_GRANTED) {
                    // All Permissions Granted
                } else {
                    // Permission Denied
                    Toast.makeText(MainActivity.this, "Some Permissions are Denied", Toast.LENGTH_SHORT).show();
                    finish();
                }
            }
            break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }
}
