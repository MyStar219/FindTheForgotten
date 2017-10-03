package pl.logiti.imagestorage.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by logiti.pl on 2016-05-04.
 */
public class Item {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "category_id")
    private int categoryId;

    @DatabaseField(columnName = "path")
    private String path;

    @DatabaseField(columnName = "path_thumb")
    private String pathThumb;

    @DatabaseField(columnName = "description")
    private String description;

    public Item() {
    }

    public Item(int categoryId, String path, String pathThumb, String description) {
        this.categoryId = categoryId;
        this.pathThumb = pathThumb;
        this.path = path;
        this.description = description;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getCategoryId() {
        return categoryId;
    }

    public void setCategoryId(int categoryId) {
        this.categoryId = categoryId;
    }

    public String getPathThumb() {
        return pathThumb;
    }

    public void setPathThumb(String pathThumb) {
        this.pathThumb = pathThumb;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
