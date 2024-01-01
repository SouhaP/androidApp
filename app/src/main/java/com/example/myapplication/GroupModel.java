package com.example.myapplication;

public class GroupModel {
    private String id;
    private String name;
    private String description;

    private String addedTime,updatedTime;
    public GroupModel() {


    }

    public GroupModel(String id, String name, String description,String addedTime,String updatedTime) {
        this.id = id;
        this.name = name;
        this.description = description;
        this.addedTime = addedTime;
        this.updatedTime = updatedTime;

    }
    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
    public String getAddedTime() {
        return addedTime;
    }

    public void setAddedTime(String addedTime) {
        this.addedTime = addedTime;
    }

    public String getUpdatedTime() {
        return updatedTime;
    }

    public void setUpdatedTime(String updatedTime) {
        this.updatedTime = updatedTime;
    }
    @Override
    public String toString() {
        return getName();
    }
}
