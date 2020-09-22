package com.cist2931.petstore.application.merchandise;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class Merchandise {
    private int merchID;
    private String merchName;
    private double price;
    private String category;
    private String description;
    private int quantity;

    public Merchandise(int merchID, String merchName, double price, String category, String description, int quantity) {
        this.merchID = merchID;
        this.merchName = merchName;
        this.price = price;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
    }

    protected Merchandise(ResultSet rs) throws SQLException {
        // initialize from result set
        this(
                rs.getInt("MerchID"),
                rs.getString("MerchName"),
                rs.getDouble("Price"),
                rs.getString("Category"),
                rs.getString("Description"),
                rs.getInt("Quantity")
        );
    }

    public boolean update(Connection dbConnection) throws SQLException {
        return MerchandiseSQL.updateMerchandise(dbConnection, this);
    }

    public boolean insert(Connection dbConnection) throws SQLException {
        return MerchandiseSQL.insertMerchandise(dbConnection, this);
    }

    public boolean delete(Connection dbConnection) throws SQLException {
        return MerchandiseSQL.deleteMerchandise(dbConnection, this.merchID);
    }

    public int getMerchID() {
        return merchID;
    }

    public void setMerchID(int merchID) {
        this.merchID = merchID;
    }

    public String getMerchName() {
        return merchName;
    }

    public void setMerchName(String merchName) {
        this.merchName = merchName;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getCategory() {
        return category;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    @Override
    public String toString() {
        return "Merchandise{" +
                "merchID=" + merchID +
                ", merchName='" + merchName + '\'' +
                ", price=" + price +
                ", category='" + category + '\'' +
                ", description='" + description + '\'' +
                ", quantity=" + quantity +
                '}';
    }
}
