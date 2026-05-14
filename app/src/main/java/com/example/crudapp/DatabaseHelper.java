package com.example.crudapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import java.util.ArrayList;
import java.util.List;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final int DATABASE_VERSION = 2; // Increment database version
    private static final String DATABASE_NAME = "items_db";
    private static final String TABLE_ITEMS = "items";
    private static final String COLUMN_ID = "id";
    private static final String COLUMN_NAME = "name";
    private static final String COLUMN_DESCRIPTION = "description";
    private static final String COLUMN_TIMESTAMP = "timestamp";
    private static final String COLUMN_CATEGORY = "category";

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        String CREATE_ITEMS_TABLE = "CREATE TABLE " + TABLE_ITEMS + "("
                + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
                + COLUMN_NAME + " TEXT,"
                + COLUMN_DESCRIPTION + " TEXT,"
                + COLUMN_TIMESTAMP + " TEXT,"
                + COLUMN_CATEGORY + " TEXT"
                + ")";
        db.execSQL(CREATE_ITEMS_TABLE);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Drop older table if existed
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_ITEMS);
        // Create tables again
        onCreate(db);
    }

    // Add new item
    public void addItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_TIMESTAMP, item.getTimestamp());
        values.put(COLUMN_CATEGORY, item.getCategory());

        db.insert(TABLE_ITEMS, null, values);
        db.close();
    }

    // Get single item
    public Item getItem(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor cursor = db.query(TABLE_ITEMS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_TIMESTAMP, COLUMN_CATEGORY},
                COLUMN_ID + "=?", new String[]{String.valueOf(id)}, null, null, null, null);
        if (cursor != null) {
            cursor.moveToFirst();
        }

        Item item = new Item(Integer.parseInt(cursor.getString(0)),
                cursor.getString(1), cursor.getString(2), cursor.getString(3), cursor.getString(4));
        cursor.close();
        return item;
    }

    // Get all items
    public List<Item> getAllItems() {
        List<Item> itemList = new ArrayList<>();
        String selectQuery = "SELECT * FROM " + TABLE_ITEMS;

        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = db.rawQuery(selectQuery, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setName(cursor.getString(1));
                item.setDescription(cursor.getString(2));
                item.setTimestamp(cursor.getString(3));
                item.setCategory(cursor.getString(4));
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }

    // Update item
    public int updateItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(COLUMN_NAME, item.getName());
        values.put(COLUMN_DESCRIPTION, item.getDescription());
        values.put(COLUMN_TIMESTAMP, item.getTimestamp());
        values.put(COLUMN_CATEGORY, item.getCategory());

        // updating row
        return db.update(TABLE_ITEMS, values, COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
    }

    // Delete item
    public void deleteItem(Item item) {
        SQLiteDatabase db = this.getWritableDatabase();
        db.delete(TABLE_ITEMS, COLUMN_ID + " = ?",
                new String[]{String.valueOf(item.getId())});
        db.close();
    }

    // Get items count
    public int getItemsCount() {
        String countQuery = "SELECT * FROM " + TABLE_ITEMS;
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery(countQuery, null);
        int count = cursor.getCount();
        cursor.close();
        return count;
    }

    // Search items by name, description or category
    public List<Item> searchItems(String query) {
        List<Item> itemList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.query(TABLE_ITEMS, new String[]{COLUMN_ID, COLUMN_NAME, COLUMN_DESCRIPTION, COLUMN_TIMESTAMP, COLUMN_CATEGORY},
                COLUMN_NAME + " LIKE ? OR " + COLUMN_DESCRIPTION + " LIKE ? OR " + COLUMN_CATEGORY + " LIKE ?",
                new String[]{"%" + query + "%", "%" + query + "%", "%" + query + "%"}, null, null, null, null);

        if (cursor.moveToFirst()) {
            do {
                Item item = new Item();
                item.setId(Integer.parseInt(cursor.getString(0)));
                item.setName(cursor.getString(1));
                item.setDescription(cursor.getString(2));
                item.setTimestamp(cursor.getString(3));
                item.setCategory(cursor.getString(4));
                itemList.add(item);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return itemList;
    }
}
