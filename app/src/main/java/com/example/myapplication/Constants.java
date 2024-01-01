package com.example.myapplication;

public class Constants {
    // database or db name
    public static final String DATABASE_NAME = "CONTACT_DB";
    //database version
    public static final int DATABASE_VERSION = 20;

    // table name
    public static final String TABLE_NAME = "CONTACT_TABLE";
    public static final String TABLE_GROUPS = "GROUPS_TABLE";
    public static final String COLUMN_GROUP_ID = "group_id";
    public static final String C_GROUP_ID = "group_id";
    public static final String COLUMN_GROUP_NAME = "group_name";
    public static final String COLUMN_GROUP_DESCRIPTION = "group_description";
    public static final String COLUMN_GROUP_ADDED_TIME = "group_added_time";
    public static final String COLUMN_GROUP_UPDATED_TIME= "group_updated_time";

    // table column or field name
    public static final String C_ID = "ID";
    public static final String C_IMAGE = "IMAGE";
    public static final String C_NAME = "NAME";
    public static final String C_PHONE = "PHONE";
    public static final String C_EMAIL = "EMAIL";
    public static final String C_NOTE = "NOTE";
    public static final String C_ADDED_TIME = "ADDED_TIME";
    public static final String C_UPDATED_TIME = "UPDATED_TIME";

    // query for create table
    public static final String CREATE_TABLE = "CREATE TABLE " + TABLE_NAME + "( "
            + C_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
            + C_IMAGE + " TEXT, "
            + C_NAME + " TEXT, "
            + C_PHONE + " TEXT, "
            + C_EMAIL + " TEXT, "
            + C_NOTE + " TEXT, "
            + C_ADDED_TIME + " TEXT, "
            + C_UPDATED_TIME + " TEXT, "
            + C_GROUP_ID + " INTEGER" // Ajout de la colonne group_id
            + " );";
    public static final String CREATE_GROUPS_TABLE = "CREATE TABLE " + TABLE_GROUPS + "("
            + COLUMN_GROUP_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_GROUP_NAME + " TEXT,"
            + COLUMN_GROUP_DESCRIPTION + " TEXT,"+
            COLUMN_GROUP_ADDED_TIME + " TEXT,"+
            COLUMN_GROUP_UPDATED_TIME + " TEXT"
            + ");";


    // Create database helper class for CRUD Query And Database Creation


}
