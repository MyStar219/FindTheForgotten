package pl.logiti.imagestorage.database;

import android.content.Context;
import com.j256.ormlite.android.apptools.OpenHelperManager;
import com.j256.ormlite.dao.Dao;
import com.j256.ormlite.stmt.PreparedQuery;
import com.j256.ormlite.stmt.QueryBuilder;

import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import pl.logiti.imagestorage.models.Category;
import pl.logiti.imagestorage.models.Item;
import pl.logiti.imagestorage.utils.Utils;


public class DatabaseOperations {

    private static DatabaseHelper databaseHelper;


    public static List<Category> getAllCategories(Context context) {
        try {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<Category, Integer> daoCategory = databaseHelper.getDaoCategory();
            return daoCategory.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Category> getAllCategoriesWithoutALL(Context context) {
        try {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<Category, Integer> daoCategory = databaseHelper.getDaoCategory();
//            return daoCategory.queryForEq("id", 13);
//            return daoCategory.queryForAll();
            QueryBuilder<Category, Integer> queryBuilder = daoCategory.queryBuilder();
            queryBuilder.where().ne("id", 13);
//            like("description", "%"+ query +"%").and().eq("category_id", categoryId);
            PreparedQuery<Category> preparedQuery = queryBuilder.prepare();
            return daoCategory.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Category getCategory(Context context, int id) {
        try {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<Category, Integer> daoCategory = databaseHelper.getDaoCategory();

            return daoCategory.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List<Item> getAllItems(Context context) {
        try {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<Item, Integer> daoItem = databaseHelper.getDaoItem();
            return daoItem.queryForAll();
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static Item getItem(Context context, int id) {
        try {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<Item, Integer> daoItem = databaseHelper.getDaoItem();

            return daoItem.queryForId(id);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static List getFilteredItems(Context context, String query, int categoryId) {
        try {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<Item, Integer> daoItem = databaseHelper.getDaoItem();

            QueryBuilder<Item, Integer> queryBuilder = daoItem.queryBuilder();
            if(categoryId == 13) queryBuilder.where().like("description", "%"+ query +"%"); //   ALL
            else queryBuilder.where().like("description", "%"+ query +"%").and().eq("category_id", categoryId);

            PreparedQuery<Item> preparedQuery = queryBuilder.prepare();
            return daoItem.query(preparedQuery);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return new ArrayList<>();
    }

    public static List<Item> getItemByCategory(Context context, int categoryId) {
        try {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<Item, Integer> daoItem = databaseHelper.getDaoItem();

            return daoItem.queryForEq("category_id", categoryId);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return null;
    }

    public static boolean saveItem(Context context, Item item) {
        try {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<Item, Integer> daoItem = databaseHelper.getDaoItem();
//            Utils.showToastOnUIThread(context, "Data saved!");
            daoItem.createOrUpdate(item);
            return true;

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }

    public static boolean removeItem(Context context, Item item) {
        try {
            databaseHelper = OpenHelperManager.getHelper(context, DatabaseHelper.class);
            Dao<Item, Integer> daoItem = databaseHelper.getDaoItem();
            daoItem.delete(item);
            return true;
        } catch (SQLException e) {
            e.printStackTrace();
        }

        return false;
    }
}