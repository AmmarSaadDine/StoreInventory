package com.example.android.storeinventory.data;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

import com.example.android.storeinventory.data.ProductContract.ProductEntry;

/**
 * Created by ammar_saaddine on 05.05.18.
 */

public class ProductProvider extends ContentProvider {

    /**
     * URI matcher code for the content URI for the products table
     */
    private static final int PRODUCTS = 100;

    /**
     * URI matcher code for the content URI for a single product in the products table
     */
    private static final int PRODUCT_ID = 101;

    /**
     * UriMatcher object to match a content URI to a corresponding code.
     */
    private static final UriMatcher uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    static {
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS, PRODUCTS);
        uriMatcher.addURI(ProductContract.CONTENT_AUTHORITY, ProductContract.PATH_PRODUCTS + "/#", PRODUCT_ID);
    }

    /**
     * Database helper object
     */
    private InventoryDbHelper dbHelper;

    /**
     * Initialize the provider and the database helper object.
     */
    @Override
    public boolean onCreate() {
        dbHelper = new InventoryDbHelper(getContext());
        return true;
    }

    /**
     * Perform the query for the given URI.
     */
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs,
                        String sortOrder) {
        // Get readable database
        SQLiteDatabase database = dbHelper.getReadableDatabase();

        // This cursor will hold the result of the query
        Cursor cursor;

        // Figure out if the URI matcher can match the URI to a specific code
        int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            case PRODUCT_ID:
                // Extract out the ID from the URI.
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};

                // Perform query
                cursor = database.query(ProductEntry.TABLE_NAME, projection, selection, selectionArgs,
                        null, null, sortOrder);
                break;
            default:
                throw new IllegalArgumentException("Cannot query unknown URI " + uri);
        }

        cursor.setNotificationUri(getContext().getContentResolver(), uri);
        return cursor;
    }

    /**
     * Insert new data into the provider with the given ContentValues.
     */
    @Override
    public Uri insert(Uri uri, ContentValues contentValues) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return insertProduct(uri, contentValues);
            default:
                throw new IllegalArgumentException("Insertion is not supported for " + uri);
        }
    }

    /**
     * Insert a product into the database with the given content values and return the new content URI
     * for that specific row in the database.
     */
    private Uri insertProduct(Uri uri, ContentValues values) {

        String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
        if (TextUtils.isEmpty(name)) {
            throw new IllegalArgumentException("Product requires a name");
        }

        Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
        if (price == null || price < 0) {
            throw new IllegalArgumentException("Product requires a valid price");
        }

        Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        if (quantity == null || quantity < 0) {
            throw new IllegalArgumentException("Product requires valid quantity");
        }

        String supplierName = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        if (TextUtils.isEmpty(supplierName)) {
            throw new IllegalArgumentException("Product requires a supplier name");
        }

        String supplierPhoneNumber = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
        if (TextUtils.isEmpty(supplierPhoneNumber)) {
            throw new IllegalArgumentException("Product requires a supplier phone number");
        }
        // Get writeable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Insert the new product with the given values
        long id = database.insert(ProductEntry.TABLE_NAME, null, values);
        // If the ID is -1, then the insertion failed.
        if (id == -1) {
            return null;
        }

        getContext().getContentResolver().notifyChange(uri, null);

        // return the new URI with the ID appended to the end of it
        return ContentUris.withAppendedId(uri, id);
    }

    /**
     * Updates the data at the given selection and selection arguments, with the new ContentValues.
     */
    @Override
    public int update(Uri uri, ContentValues contentValues, String selection,
                      String[] selectionArgs) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return updateProduct(uri, contentValues, selection, selectionArgs);
            case PRODUCT_ID:
                // Extract out the ID from the URI,
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                return updateProduct(uri, contentValues, selection, selectionArgs);
            default:
                throw new IllegalArgumentException("Update is not supported for " + uri);
        }
    }

    private int updateProduct(Uri uri, ContentValues values, String selection, String[] selectionArgs) {

        // If there are no values to update, then don't try to update the database
        if (values.size() == 0) {
            return 0;
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_NAME)) {
            String name = values.getAsString(ProductEntry.COLUMN_PRODUCT_NAME);
            if (TextUtils.isEmpty(name)) {
                throw new IllegalArgumentException("Product requires a valid name");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_PRICE)) {
            Double price = values.getAsDouble(ProductEntry.COLUMN_PRODUCT_PRICE);
            if (price == null || price < 0) {
                throw new IllegalArgumentException("Product requires a valid price");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_QUANTITY)) {
            Integer quantity = values.getAsInteger(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            if (quantity == null || quantity < 0) {
                throw new IllegalArgumentException("Product requires a valid quantity");
            }
        }

        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME)) {
            String supplierName = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            if (TextUtils.isEmpty(supplierName)) {
                throw new IllegalArgumentException("Product requires a valid supplier name");
            }
        }


        if (values.containsKey(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER)) {
            String supplierPhoneNumber = values.getAsString(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);
            if (TextUtils.isEmpty(supplierPhoneNumber)) {
                throw new IllegalArgumentException("Product requires a valid supplier phone number");
            }
        }

        SQLiteDatabase database = dbHelper.getWritableDatabase();

        // Perform the update on the database and get the number of rows affected
        int rowsUpdated = database.update(ProductEntry.TABLE_NAME, values, selection, selectionArgs);

        // If 1 or more rows were updated, then notify all listeners that the data at the
        // given URI has changed
        if (rowsUpdated != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsUpdated;
    }

    /**
     * Delete the data at the given selection and selection arguments.
     */
    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        // Get writeable database
        SQLiteDatabase database = dbHelper.getWritableDatabase();

        final int match = uriMatcher.match(uri);
        // Track the number of rows that were deleted
        int rowsDeleted;
        switch (match) {
            case PRODUCTS:
                // Delete all rows that match the selection and selection args
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            case PRODUCT_ID:
                // Delete a single row given by the ID in the URI
                selection = ProductEntry._ID + "=?";
                selectionArgs = new String[]{String.valueOf(ContentUris.parseId(uri))};
                // Delete a single row given by the ID in the URI
                rowsDeleted = database.delete(ProductEntry.TABLE_NAME, selection, selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Deletion is not supported for " + uri);
        }
        // If 1 or more rows were deleted, then notify all listeners that the data at the
        // given URI has changed
        if (rowsDeleted != 0) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return rowsDeleted;
    }

    /**
     * Returns the MIME type of data for the content URI.
     */
    @Override
    public String getType(Uri uri) {
        final int match = uriMatcher.match(uri);
        switch (match) {
            case PRODUCTS:
                return ProductEntry.CONTENT_LIST_TYPE;
            case PRODUCT_ID:
                return ProductEntry.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalStateException("Unknown URI " + uri + " with match " + match);
        }
    }
}
