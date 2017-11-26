package org.thecuriousdev.demo.skeleton.db.domain;

import com.google.common.base.MoreObjects;
import org.thecuriousdev.demo.skeleton.db.CouchbaseDocument;

public class Person implements CouchbaseDocument {

    public static final String DB_TYPE = "tcd:person";

    private String id;
    private long cas;

    private String name;
    private int age;
    private String favouriteFood;
    private String company;

    private String type;


    public Person() {
        this.type = DB_TYPE;
    }

    public Person(String name, int age, String favouriteFood) {
        this.name = name;
        this.age = age;
        this.favouriteFood = favouriteFood;
        this.type = DB_TYPE;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public int getAge() {
        return age;
    }

    public void setAge(int age) {
        this.age = age;
    }

    public String getFavouriteFood() {
        return favouriteFood;
    }

    public void setFavouriteFood(String favouriteFood) {
        this.favouriteFood = favouriteFood;
    }

    public String getCompany() {
        return company;
    }

    public void setCompany(String company) {
        this.company = company;
    }

    @Override
    public String getType() {
        return type;
    }

    @Override
    public void setType(String type) {
        this.type = type;
    }

    @Override
    public String getId() {
        return id;
    }

    @Override
    public void setId(String id) {
        this.id = id;
    }

    @Override
    public long getCas() {
        return cas;
    }

    @Override
    public void setCas(long cas) {
        this.cas = cas;
    }

    @Override
    public String toString() {
        return MoreObjects.toStringHelper(this.getClass())
                .add("name", name)
                .add("age", age)
                .add("favouriteFood", favouriteFood)
                .add("company", company)
                .omitNullValues()
                .toString();
    }
}
