package com.example.android.storeinventory.activity;

import android.app.AlertDialog;
import android.app.LoaderManager;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.example.android.storeinventory.data.ProductContract.ProductEntry;

import com.example.android.storeinventory.R;
import com.example.android.storeinventory.data.ProductEntity;

public class ProductDetailsActivity extends AppCompatActivity implements LoaderManager.LoaderCallbacks<Cursor> {
    /**
     * Identifier for the product data loader
     */
    private static final int EXISTING_PRODUCT_LOADER = 0;

    /**
     * Content URI for the existing product (null if it's a new product)
     */
    private Uri currentProductUri;

    /**
     * Current fetched product entity
     */
    private ProductEntity currentProduct;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_product_details);

        Intent intent = getIntent();
        currentProductUri = intent.getData();
        getLoaderManager().initLoader(EXISTING_PRODUCT_LOADER, null, this);

        // setup call button
        findViewById(R.id.supplier_call_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentProduct != null && currentProduct.getSupplierPhone() != null) {
                            Intent intent = new Intent(Intent.ACTION_DIAL);
                            intent.setData(Uri.parse("tel:" + currentProduct.getSupplierPhone()));
                            startActivity(intent);
                        }
                    }
                });

        // setup plus & minus buttons click listeners
        findViewById(R.id.minus_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentProduct != null && currentProduct.getQuantity() > 0) {
                            currentProduct.setQuantity(currentProduct.getQuantity() - 1);
                            updateProductQuantity();
                        }
                    }
                });
        findViewById(R.id.plus_button)
                .setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if (currentProduct != null) {
                            currentProduct.setQuantity(currentProduct.getQuantity() + 1);
                            updateProductQuantity();
                        }
                    }
                });
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu options from the res/menu/menu_editor.xml file.
        // This adds menu items to the app bar.
        getMenuInflater().inflate(R.menu.menu_pdp, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // User clicked on a menu option in the app bar overflow menu
        switch (item.getItemId()) {
            case R.id.action_delete:
                showDeleteConfirmationDialog();
                return true;
            case R.id.action_edit:
                openEditScreen();
        }
        return super.onOptionsItemSelected(item);
    }

    private void showDeleteConfirmationDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage(R.string.delete_dialog_msg);
        builder.setPositiveButton(R.string.delete, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                deleteProduct();
            }
        });
        builder.setNegativeButton(R.string.cancel, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                if (dialog != null) {
                    dialog.dismiss();
                }
            }
        });

        // Create and show the AlertDialog
        AlertDialog alertDialog = builder.create();
        alertDialog.show();
    }

    /**
     * Open edit product intent
     */
    private void openEditScreen() {
        Intent intent = new Intent(ProductDetailsActivity.this, EditorActivity.class);
        intent.setData(currentProductUri);
        startActivity(intent);
    }

    /**
     * Perform the deletion of the product in the database.
     */
    private void deleteProduct() {
        // Only perform the delete if this is an existing product.
        if (currentProductUri != null) {
            try {
                int rowsDeleted = getContentResolver().delete(currentProductUri, null, null);
                // Show a toast message depending on whether or not the delete was successful.
                if (rowsDeleted == 0) {
                    // If no rows were deleted, then there was an error with the delete.
                    Toast.makeText(this, getString(R.string.delete_product_failed),
                            Toast.LENGTH_SHORT).show();
                } else {
                    // Otherwise, the delete was successful and we can display a toast.
                    Toast.makeText(this, getString(R.string.delete_product_successful),
                            Toast.LENGTH_SHORT).show();
                }
                // Close the activity
                finish();
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    /**
     * Update quantity of product in the database.
     */
    private void updateProductQuantity() {
        if (currentProduct != null) {
            Uri productUri = ContentUris.withAppendedId(ProductEntry.CONTENT_URI, currentProduct.getId());
            // Fill new values
            ContentValues values = new ContentValues();
            values.put(ProductEntry.COLUMN_PRODUCT_QUANTITY, currentProduct.getQuantity());
            // Update the product
            try {
                int rows = getContentResolver().update(productUri, values, null, null);

                if (rows == 0) {
                    Toast.makeText(this, getString(R.string.update_quantity_failed),
                            Toast.LENGTH_SHORT).show();

                }
            } catch (IllegalArgumentException e) {
                Toast.makeText(this, e.getMessage(), Toast.LENGTH_SHORT).show();
            }
        }
    }

    @Override
    public Loader<Cursor> onCreateLoader(int id, Bundle args) {
        String[] projection = {
                ProductEntry._ID,
                ProductEntry.COLUMN_PRODUCT_NAME,
                ProductEntry.COLUMN_PRODUCT_PRICE,
                ProductEntry.COLUMN_PRODUCT_QUANTITY,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME,
                ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER
        };

        // This loader will execute the ContentProvider's query method on a background thread
        return new CursorLoader(this,
                currentProductUri,
                projection,
                null,
                null,
                null);
    }

    @Override
    public void onLoadFinished(Loader<Cursor> loader, Cursor cursor) {

        // Bail early if the cursor is null or there is less than 1 row in the cursor
        if (cursor == null || cursor.getCount() < 1) {
            return;
        }
        // Proceed with moving to the first row of the cursor and reading data from it
        // (This should be the only row in the cursor)
        if (cursor.moveToFirst()) {
            // Find the columns of product attributes that we're interested in
            currentProduct = new ProductEntity(cursor);

            // Update the views on the screen with the values from the database
            TextView productNameTextView = findViewById(R.id.product_name);
            TextView priceTextView = findViewById(R.id.product_price);
            TextView quantityTextView = findViewById(R.id.quantity);
            TextView supplierNameTextView = findViewById(R.id.supplier_name);
            TextView supplierPhoneTextView = findViewById(R.id.supplier_phone);

            productNameTextView.setText(currentProduct.getName());
            priceTextView.setText(getString(R.string.pdp_price_format, String.valueOf(currentProduct.getPrice())));
            quantityTextView.setText(String.valueOf(currentProduct.getQuantity()));
            supplierNameTextView.setText(currentProduct.getSupplierName());
            supplierPhoneTextView.setText(currentProduct.getSupplierPhone());
        }
    }

    @Override
    public void onLoaderReset(Loader<Cursor> loader) {
        currentProduct = null;
    }
}
