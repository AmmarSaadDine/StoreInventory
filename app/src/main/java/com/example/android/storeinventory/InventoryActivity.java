package com.example.android.storeinventory;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.example.android.storeinventory.data.InventoryDbHelper;
import com.example.android.storeinventory.data.ProductContract.ProductEntry;

public class InventoryActivity extends AppCompatActivity {

    private InventoryDbHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_inventory);

        // initialize the DB helper
        dbHelper = new InventoryDbHelper(this);
        displayDatabaseInfo();
    }

    private void insertProduct() {
        // get a writable reference to the DB
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(ProductEntry.COLUMN_PRODUCT_NAME, "God of War");
        values.put(ProductEntry.COLUMN_PRODUCT_PRICE, 60.0);
        values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, 3);
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME, "Sony Interactive Entertainment");
        values.put(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER, "xx-xxx-xxxxx");

        db.insert(ProductEntry.TABLE_NAME, null, values);
    }

    private void displayDatabaseInfo() {
        // Create and/or open a database to read from it
        SQLiteDatabase db = dbHelper.getReadableDatabase();

        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        Cursor cursor = db.query(
                ProductEntry.TABLE_NAME,
                projection,
                null,
                null,
                null,
                null,
                null
        );

        TextView displayView = findViewById(R.id.text_view_product);

        try {
            // Display the number of rows in the Cursor (which reflects the number of rows in the
            // products table in the database).
            displayView.setText("Number of rows in inventory database table: " + cursor.getCount() + "\n\n");
            displayView.append(ProductEntry._ID + " - "
                    + ProductEntry.COLUMN_PRODUCT_NAME + " - "
                    + ProductEntry.COLUMN_PRODUCT_PRICE + " - "
                    + ProductEntry.COLUMN_PRODUCT_QUANTITY + " - "
                    + ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME + " - "
                    + ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER + " - "
                    + "\n");

            // Figure out the index of each column
            int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);
            int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
            int productPriceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
            int productQuantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
            int productSupplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
            int productSupplierPhoneNumberColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

            while (cursor.moveToNext()) {
                int currentId = cursor.getInt(idColumnIndex);
                String productName = cursor.getString(productNameColumnIndex);
                float productPrice = cursor.getFloat(productPriceColumnIndex);
                int productQuantity = cursor.getInt(productQuantityColumnIndex);
                String productSupplierName = cursor.getString(productSupplierNameColumnIndex);
                String productSupplierPhoneNumber = cursor.getString(productSupplierPhoneNumberColumnIndex);
                displayView.append("\n" + currentId + " - " + productName
                        + " - " + productPrice
                        + " - " + productQuantity
                        + " - " + productSupplierName
                        + " - " + productSupplierPhoneNumber);
            }

        } finally {
            // Always close the cursor when you're done reading from it. This releases all its
            // resources and makes it invalid.
            cursor.close();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_inventory, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            // Respond to a click on the "Insert dummy data" menu option
            case R.id.action_insert_dummy_data:
                insertProduct();
                displayDatabaseInfo();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }
}
