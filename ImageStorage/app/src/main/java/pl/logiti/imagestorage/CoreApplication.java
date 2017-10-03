package pl.logiti.imagestorage;

import android.content.Context;
import org.acra.ACRA;
import org.acra.annotation.ReportsCrashes;
import android.app.Application;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.Signature;
import android.util.Base64;
import android.util.Log;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

import pl.logiti.imagestorage.utils.Const;


@ReportsCrashes(formUri = Const.reportURL)
public class CoreApplication extends Application {
//    private static Context context;

    @Override
    protected void attachBaseContext (Context base){
        super.attachBaseContext(base);

        // The following line triggers the initialization of ACRA
        ACRA.init(this);
    }

//    public static Context getContext() {
//        return CoreApplication.context;
//    }
}


