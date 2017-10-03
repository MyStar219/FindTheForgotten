package pl.logiti.imagestorage.database;

import com.j256.ormlite.android.apptools.OrmLiteConfigUtil;

import java.io.File;
import java.io.IOException;
import java.sql.SQLException;

import pl.logiti.imagestorage.models.Category;
import pl.logiti.imagestorage.models.Item;

public class DatabaseConfigUtil extends OrmLiteConfigUtil {
    /**
     * classes represents the models to use for generating the ormlite_config.txt file
     */
    private static final Class<?>[] classes = new Class[] {
            Category.class, Item.class
    };
    /**
     * Given that this is a separate program from the android app, we have to use
     * a static main java method to create the configuration file.
     * @param args
     * @throws IOException
     * @throws SQLException
     */
    public static void main(String[] args) throws IOException, SQLException {
        writeConfigFile(new File("C:\\Users\\lukas_000\\StudioProjects\\ImageStorage\\app\\src\\main\\res\\raw\\ormlite_config.txt"), classes);
//        writeConfigFile("ormlite_config.txt", classes);
    }
}