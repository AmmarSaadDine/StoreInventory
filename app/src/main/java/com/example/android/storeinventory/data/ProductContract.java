package com.example.android.storeinventory.data;

import android.content.ContentResolver;
import android.net.Uri;
import android.provider.BaseColumns;

/**
 * Created by ammar_saaddine on 25.04.18.
 */

public final class ProductContract {

    public static final String CONTENT_AUTHORITY = "com.example.android.storeinventory";
    public static final Uri BASE_CONTENT_URI = Uri.parse("content://" + CONTENT_AUTHORITY);
    public static final String PATH_PRODUCTS = "products";

    private ProductContract() {}

    public static final class ProductEntry implements BaseColumns {

        /**
         * The MIME type of the {@link #CONTENT_URI} for a list of products.
         */
        public static final String CONTENT_LIST_TYPE =
                ContentResolver.CURSOR_DIR_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        /**
         * The MIME type of the {@link #CONTENT_URI} for a single product.
         */
        public static final String CONTENT_ITEM_TYPE =
                ContentResolver.CURSOR_ITEM_BASE_TYPE + "/" + CONTENT_AUTHORITY + "/" + PATH_PRODUCTS;

        public static final Uri CONTENT_URI = Uri.withAppendedPath(BASE_CONTENT_URI, PATH_PRODUCTS);
        public final static String TABLE_NAME = "products";


        public final static String _ID = BaseColumns._ID;
        public final static String COLUMN_PRODUCT_NAME = "product_name";
        public final static String COLUMN_PRODUCT_PRICE = "price";
        public final static String COLUMN_PRODUCT_QUANTITY = "quantity";
        public final static String COLUMN_PRODUCT_SUPPLIER_NAME = "supplier_name";
        public final static String COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER = "supplier_phone_number";
    }
}
