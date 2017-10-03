package pl.logiti.imagestorage.models;

import com.j256.ormlite.field.DatabaseField;

/**
 * Created by logiti.pl on 2016-05-04.
 */
public class Category {

    @DatabaseField(generatedId = true)
    private int id;

    @DatabaseField(columnName = "name")
    private String name;

    public Category() {
    }

    public Category(String name) {
        this.name = name;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
