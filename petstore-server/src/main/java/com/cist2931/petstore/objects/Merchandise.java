package com.cist2931.petstore.objects;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class Merchandise {
    private int merchID;
    private String merchName;
    private double price;
    private String category;
    private String description;
    private int quantity;

    public Merchandise() {
        //default constructor
        merchID = 0;
        merchName = "";
        price = 0;
        category = "";
        description = "";
        quantity = 0;
    }

    public Merchandise(ResultSet rs) throws SQLException {
        // initialize from result set
        merchID = rs.getInt("MerchID");
        merchName = rs.getString("MerchName");
        price = rs.getDouble("Price");
        category = rs.getString("Category");
        description = rs.getString("Description");
        quantity = rs.getInt("Quantity on Hand");
    }

    public static Merchandise GetMerchByID(Connection dbConnection, int id) throws SQLException {
        final String selectQuery = "SELECT * FROM Merchandise WHERE MerchID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(selectQuery);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return new Merchandise(resultSet);
        } else return null;
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
}
