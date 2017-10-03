package pl.logiti.imagestorage.utils;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Matrix;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.Handler;
import android.preference.PreferenceManager;
import android.view.Display;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.FileChannel;
import java.util.Date;
import java.util.List;

import pl.logiti.imagestorage.models.Category;

/**
 * Created by logiti.pl on 2016-04-04.
 */
public class Utils {

    public static void LogInfo(String msg, String...tag){
        String tagstr = null;
        if(tag!=null && tag.length > 0)
            tagstr = Const.appTAG + tag[0];
        else
            tagstr = Const.appTAG;
        android.util.Log.i(tagstr, msg);
    }
    public static void LogDebug(String msg, String...tag){
        String tagstr = null;
        if(tag!=null && tag.length > 0)
            tagstr = Const.appTAG + tag[0];
        else
            tagstr = Const.appTAG;
        android.util.Log.d(tagstr, msg);
    }
    public static void LogError(String msg, Throwable e, String...tag){
        String tagstr = null;
        if(tag!=null && tag.length > 0)
            tagstr = Const.appTAG + tag[0];
        else
            tagstr = Const.appTAG;
        android.util.Log.e(tagstr, msg, e);
    }

    public static void showToastOnUIThread(final Context context, final String toast) {
        Handler handler = new Handler(context.getMainLooper());
        handler.post(new Runnable() {
            public void run() {
                Toast.makeText(context, toast, Toast.LENGTH_SHORT).show();
            }
        });
    }

    public static void createFoldersIfNotExist(Context context, List<Category> categories) {
        for (Category category: categories) {
            if(!category.getName().equalsIgnoreCase("all")) {
                File dir = new File(Const.appFolder(context) + "/" + category.getName());
                if (!dir.exists()) dir.mkdirs();
            }
        }

        //  NO MEDIA
        File nomediaFile = new File(Const.appFolder(context) + "/.nomedia");
        if(!nomediaFile.exists()) {
            try {
                nomediaFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean copyFileToCategory(Context context, String destinationImagePath, String destinationThumbImagePath) {
        try {
            File sd = Environment.getExternalStorageDirectory();
//            File data = Environment.getDataDirectory();
            if (sd.canWrite()) {
                String sourceImagePath= Const.appFolder(context) + "/tmp.jpg";
//                String destinationImagePath= Const.appFolder +"/" + category + "/tmp.jpg";
                File source= new File(sourceImagePath);
                File destination= new File(destinationImagePath);
                if (source.exists()) {
                    FileChannel src = new FileInputStream(source).getChannel();
                    FileChannel dst = new FileOutputStream(destination).getChannel();
                    dst.transferFrom(src, 0, src.size());
                    src.close();
                    dst.close();

                    saveThumbnail(destination, destinationThumbImagePath);
                } else {
                    Utils.showToastOnUIThread(context, "File not exists: "+sourceImagePath);
                    return false;
                }
            }
            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        }

    }

    public static void saveThumbnail(File mainFile, String destinationThumbImagePath) {
//        byte[] imageData = null;
//        Bitmap thumbnail = null;
//
//        BitmapFactory.Options options = new BitmapFactory.Options();
//        options.inJustDecodeBounds = true;
//        BitmapFactory.decodeFile(mainFile.getAbsolutePath(), options);
//        int imageHeight = options.outHeight;
//        int imageWidth = options.outWidth;

        try {
//            final int THUMBNAIL_WIDTH = 150;
//            int ratio = (THUMBNAIL_WIDTH * 100) / imageWidth;
//            final int THUMBNAIL_HEIGHT = (ratio * imageHeight)  / 100;
//
//            FileInputStream fis = new FileInputStream(mainFile);
//            Bitmap imageBitmap = BitmapFactory.decodeStream(fis);
//
//            imageBitmap = Bitmap.createScaledBitmap(imageBitmap, THUMBNAIL_WIDTH, THUMBNAIL_HEIGHT, false);
//            thumbnail = imageBitmap;

            saveBitmapToFile(decodeSampledBitmapFromFile(mainFile.getAbsolutePath(), 70, 70), destinationThumbImagePath);
        } catch(Exception ex) {
            ex.printStackTrace();
        }
    }

//    public static Bitmap getBitmapFromPicture(Activity activity, String path) {
//        Display display = activity.getWindowManager().getDefaultDisplay();
//        Point size = new Point();
//        display.getSize(size);
//        int width = size.x;
//        int height = size.y;
//
//        return decodeSampledBitmapFromFile(path, width, height);
//    }

    public static Bitmap decodeSampledBitmapFromFile(String path, int reqWidth, int reqHeight) {

        // First decode with inJustDecodeBounds=true to check dimensions
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(path, options);

        // Calculate inSampleSize
        options.inSampleSize = calculateInSampleSize(options, reqWidth, reqHeight);

        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        return BitmapFactory.decodeFile(path, options);
    }

    public static int calculateInSampleSize(
            BitmapFactory.Options options, int reqWidth, int reqHeight) {
        // Raw height and width of image
        final int height = options.outHeight;
        final int width = options.outWidth;
        int inSampleSize = 1;

        if (height > reqHeight || width > reqWidth) {

            final int halfHeight = height / 2;
            final int halfWidth = width / 2;

            // Calculate the largest inSampleSize value that is a power of 2 and keeps both
            // height and width larger than the requested height and width.
            while ((halfHeight / inSampleSize) > reqHeight
                    && (halfWidth / inSampleSize) > reqWidth) {
                inSampleSize *= 2;
            }
        }

        return inSampleSize;
    }

    public static void saveBitmapToFile(Bitmap bitmap, String destinationThumbImagePath) {
//        String newThumbnileFilePath = Const.appFolder + "/" + category.getName() + "/thumb_" + sdf.format(new Date()) + ".jpg";
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(destinationThumbImagePath);
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, out); // bmp is your Bitmap instance
            // PNG is a lossless format, the compression factor (100) is ignored
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }


    public static Bitmap rotateBitmap(Bitmap source, float angle) {
        Matrix matrix = new Matrix();
        matrix.postRotate(angle);
        return Bitmap.createBitmap(source, 0, 0, source.getWidth(), source.getHeight(), matrix, true);
    }

    public static void closeKeyboard(Activity activity) {
        View view = activity.getCurrentFocus();
        if (view != null) {
            InputMethodManager imm = (InputMethodManager)activity.getSystemService(Context.INPUT_METHOD_SERVICE);
            if(imm != null) imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
        }
    }

    public static void saveStorageType(Context context, boolean isExternal) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        prefs.edit().putBoolean("EXTERNAL_STORAGE", isExternal).apply();
    }

    public static boolean isExternalStorage(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getBoolean("EXTERNAL_STORAGE", false);
    }

    public static boolean isSharedPreferencesExists(Context context) {
        SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.contains("EXTERNAL_STORAGE");
    }
}
