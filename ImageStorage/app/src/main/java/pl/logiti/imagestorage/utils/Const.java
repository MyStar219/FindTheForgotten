package pl.logiti.imagestorage.utils;

import android.content.Context;
import android.os.Environment;

public class Const {
    public static final String reportURL = "http://android.logiti.pl/acra/image_storage/report.php";
    public static final String packageName = "pl.logiti.imagestorage";
    public static final String appTAG = "imagestorage::";

    public static final String appFolder(Context context) {
        String path = "";
        if (!Utils.isSharedPreferencesExists(context)) {
            if (android.os.Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImageStorage";
                Utils.saveStorageType(context, true);
            } else {
                path = context.getCacheDir().getAbsolutePath() + "/ImageStorage";
                Utils.saveStorageType(context, false);
            }
        } else {
            if (Utils.isExternalStorage(context)) {
                path = Environment.getExternalStorageDirectory().getAbsolutePath() + "/ImageStorage";
            } else {
                path = context.getCacheDir().getAbsolutePath() + "/ImageStorage";
            }
        }

        return path;
    }
}
