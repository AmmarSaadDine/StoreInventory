package com.example.android.storeinventory.activity;

import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;
import android.widget.Toast;

import com.example.android.storeinventory.R;
import com.example.android.storeinventory.adapter.ProductCursorAdapter;
import com.example.android.storeinventory.data.ProductContract.ProductEntry;

public class InventoryActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {

    private static final int PRODUCT_LOADER = 0;
    private ProductCursorAdapter cursorAdapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // Setup FAB to open EditorActivity
        FloatingActionButton fab = findViewById(R.id.fab);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(InventoryActivity.this, EditorActivity.class);
                startActivity(intent);
            }
        });

        // Find the ListView which will be populated with the products data
        ListView productListView = findViewById(R.id.list);

        // Find and set empty view on the ListView, so that it only shows when the list has 0 items.
        View emptyView = findViewById(R.id.empty_view);
        productListView.setEmptyView(emptyView);

        cursorAdapter = new ProductCursorAdapter(this, null);
        productListView.setAdapter(cursorAdapter);

        productListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent pdpIntent = new Intent(InventoryActivity.this, ProductDetailsActivity.class);
                Uri currentProductUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, id);
                pdpIntent.setData(currentProductUri);
                startActivity(pdpIntent);
            }
        });

        getLoaderManager().initLoader(PRODUCT_LOADER, null, this);
    }

    private void insertDummyProduct() {
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "God of War");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 60.0);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 3);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Sony Interactive Entertainment");
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, "xx-xxx-xxxxx");

        try {
            getContentResolver().insert(ProductEntry.CONTENT_URI, values);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertDummyProduct();
                return true;
            // Respond to a click on the "Delete all entries" menu option
            case R.id.action_delete_all_entries:
                deleteAllProducts();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        // Define a projection that specifies the columns from the table we care about
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY
        };

        return new CursorLoader(this,
                ProductEntry.CONTENT_URI,
                projection,
                null,
                null,
                null
        );
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
        cursorAdapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        cursorAdapter.swapCursor(null);
    }

    /**
     * Helper method to delete all products in the database.
     */
    private void deleteAllProducts() {
        try {
            getContentResolver().delete(ProductEntry.CONTENT_URI, null, null);
        } catch (IllegalArgumentException e) {
            Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
        }
    }

    public void buyProduct(long productID, int currentQuantity) {
        if (currentQuantity > 0) {
            // Get the uri to update the product with the specified id
            Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, productID);
            // Fill new values
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, currentQuantity - 1);
            // Update the product
            try {
                getContentResolver().update(productUri, values, null, null);
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        } else {
            Toast.makeText(this, getString(R.string.product_sold),
                    Toast.LENGTH_SHORT).show();
        }
    }
}
