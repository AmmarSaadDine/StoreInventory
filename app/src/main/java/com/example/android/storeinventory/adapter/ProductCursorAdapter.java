package com.example.android.storeinventory.adapter;

import android.content.Context;
import android.database.Cursor;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.TextView;

import com.example.android.storeinventory.R;
import com.example.android.storeinventory.activity.InventoryActivity;
import com.example.android.storeinventory.data.ProductContract.ProductEntry;

/**
 * Created by ammar_saaddine on 07.05.18.
 */

public class ProductCursorAdapter extends CursorAdapter {

    public ProductCursorAdapter(Context context, Cursor c) {
        super(context, c, 0 /* flags */);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.list_item, parent, false);
    }

    @Override
    public void bindView(View view, final Context context, Cursor cursor) {
        // Find individual views that we want to modify in the list item layout
        TextView nameTextView = view.findViewById(R.id.product_name);
        TextView priceTextView = view.findViewById(R.id.product_price);
        TextView quantityTextView = view.findViewById(R.id.quantity);
        Button button = view.findViewById(R.id.buy_button);

        // Find the columns of product attributes that we're interested in
        int nameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int idColumnIndex = cursor.getColumnIndex(ProductEntry._ID);

        // Read the product attributes from the Cursor for the current product
        String productName = cursor.getString(nameColumnIndex);
        Double price = cursor.getDouble(priceColumnIndex);
        final int quantity = cursor.getInt(quantityColumnIndex);
        final long id = cursor.getLong(idColumnIndex);

        // Update the TextViews with the attributes for the current product
        nameTextView.setText(productName);
        priceTextView.setText(context.getString(R.string.price_format, String.valueOf(price)));
        quantityTextView.setText(context.getString(R.string.quantity_format, String.valueOf(quantity)));

        // setup buy button
        button.setFocusable(false);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (context instanceof InventoryActivity) {
                    ((InventoryActivity) context).buyProduct(id, quantity);
                }
            }
        });
    }
}
