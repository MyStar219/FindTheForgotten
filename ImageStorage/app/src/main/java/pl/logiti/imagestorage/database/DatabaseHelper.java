package pl.logiti.imagestorage.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import com.j256.ormlite.android.apptools.OrmLiteSqliteOpenHelper;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.support.ConnectionSource;
import com.j256.ormlite.table.TableUtils;
import java.sql.SQLException;
import pl.logiti.imagestorage.R;
import pl.logiti.imagestorage.models.Category;
import pl.logiti.imagestorage.models.Item;


public class DatabaseHelper extends OrmLiteSqliteOpenHelper {

    private static final String DATABASE_NAME = "image_storage";
    private static final int DATABASE_VERSION = 1;

    private Dao<Item, Integer> daoItem = null;
    private Dao<Category, Integer> daoCategory = null;
    private Context appContext;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION, R.raw.ormlite_config);
        appContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase database, ConnectionSource connectionSource) {
        try {
            TableUtils.createTable(connectionSource, Item.class);
            TableUtils.createTable(connectionSource, Category.class);
            createCategories();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createCategories() {
        Category category1 = new Category(appContext.getString(R.string.documents));
        Category category2 = new Category(appContext.getString(R.string.tools));
        Category category3 = new Category(appContext.getString(R.string.clothing));
        Category category4 = new Category(appContext.getString(R.string.electronics));
        Category category5 = new Category(appContext.getString(R.string.cosmetics));
        Category category6 = new Category(appContext.getString(R.string.toys));
        Category category7 = new Category(appContext.getString(R.string.crockery));
        Category category8 = new Category(appContext.getString(R.string.bags));
        Category category9 = new Category(appContext.getString(R.string.jewelry));
        Category category10 = new Category(appContext.getString(R.string.sports));
        Category category11 = new Category(appContext.getString(R.string.medicines));
        Category category12 = new Category(appContext.getString(R.string.others));
        Category category13 = new Category(appContext.getString(R.string.all));

        try {
            getDaoCategory().create(category1);
            getDaoCategory().create(category2);
            getDaoCategory().create(category3);
            getDaoCategory().create(category4);
            getDaoCategory().create(category5);
            getDaoCategory().create(category6);
            getDaoCategory().create(category7);
            getDaoCategory().create(category8);
            getDaoCategory().create(category9);
            getDaoCategory().create(category10);
            getDaoCategory().create(category11);
            getDaoCategory().create(category12);
            getDaoCategory().create(category13);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onUpgrade(SQLiteDatabase database, ConnectionSource connectionSource, int oldVersion, int newVersion) {
        try {
            TableUtils.dropTable(connectionSource, Item.class, true);
            TableUtils.dropTable(connectionSource, Category.class, true);
            onCreate(database, connectionSource);
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }


    public Dao<Item, Integer> getDaoItem() throws SQLException {
        if(daoItem == null) {
            daoItem = getDao(Item.class);
        }

        return daoItem;
    }

    public Dao<Category, Integer> getDaoCategory() throws SQLException {
        if(daoCategory == null) {
            daoCategory = getDao(Category.class);
        }

        return daoCategory;
    }
}

