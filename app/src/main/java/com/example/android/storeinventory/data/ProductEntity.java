package com.example.android.storeinventory.data;

import android.database.Cursor;
import android.provider.BaseColumns;

import com.example.android.storeinventory.data.ProductContract.ProductEntry;
/**
 * Created by ammar_saaddine on 11.05.18.
 */

public class ProductEntity {
    private long id;
    private String name;
    private Double price;
    private int quantity;
    private String supplierName;
    private String supplierPhone;

    public ProductEntity(long id, String name, Double price, int quantity, String supplierName, String supplerPhone) {
        this.id = id;
        this.name = name;
        this.price = price;
        this.quantity = quantity;
        this.supplierName = supplierName;
        this.supplierPhone = supplerPhone;
    }

    public ProductEntity(Cursor cursor) {
        // Find the columns of product attributes that we're interested in
        int idColumnIndex = cursor.getColumnIndex(BaseColumns._ID);
        int productNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_NAME);
        int priceColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_PRICE);
        int quantityColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_QUANTITY);
        int supplierNameColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_NAME);
        int supplierPhoneColumnIndex = cursor.getColumnIndex(ProductEntry.COLUMN_PRODUCT_SUPPLIER_PHONE_NUMBER);

        // Extract out the value from the Cursor for the given column index
        id = cursor.getLong(idColumnIndex);
        name = cursor.getString(productNameColumnIndex);
        price = cursor.getDouble(priceColumnIndex);
        quantity = cursor.getInt(quantityColumnIndex);
        supplierName = cursor.getString(supplierNameColumnIndex);
        supplierPhone = cursor.getString(supplierPhoneColumnIndex);
    }

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Double getPrice() {
        return price;
    }

    public void setPrice(Double price) {
        this.price = price;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getSupplierName() {
        return supplierName;
    }

    public void setSupplierName(String supplierName) {
        this.supplierName = supplierName;
    }

    public String getSupplierPhone() {
        return supplierPhone;
    }

    public void setSupplierPhone(String supplerPhone) {
        this.supplierPhone = supplerPhone;
    }
}
