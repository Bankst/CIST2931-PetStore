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

    public Merchandise(int merchID, String merchName, double price, String category, String description, int quantity) {
        this.merchID = merchID;
        this.merchName = merchName;
        this.price = price;
        this.category = category;
        this.description = description;
        this.quantity = quantity;
    }

    public Merchandise(ResultSet rs) throws SQLException {
        // initialize from result set
        merchID = rs.getInt("MerchID");
        merchName = rs.getString("MerchName");
        price = rs.getDouble("Price");
        category = rs.getString("Category");
        description = rs.getString("Description");
        quantity = rs.getInt("Quantity");
    }

    public boolean update(Connection dbConnection) throws SQLException {
        return updateMerchandise(dbConnection, this);
    }

    public boolean insert(Connection dbConnection) throws SQLException {
        return insertMerchandise(dbConnection, this);
    }

    public boolean delete(Connection dbConnection) throws SQLException {
        return deleteMerchandise(dbConnection, this.merchID);
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

    public static Merchandise getMerchandiseById(Connection dbConnection, int id) throws SQLException {
        final String selectQuery = "SELECT * FROM Merchandise WHERE MerchID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(selectQuery);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return new Merchandise(resultSet);
        } else return null;
    }

    public static boolean insertMerchandise(Connection dbConnection, Merchandise merchandise) throws SQLException {
        final String insertQuery = "INSERT INTO Merchandise(MerchID, MerchName, Price, Category, Description, Quantity) values (?, ?, ?, ?, ?, ?)";
        PreparedStatement statement = dbConnection.prepareStatement(insertQuery);
        statement.setInt(1, merchandise.merchID);
        statement.setString(2, merchandise.merchName);
        statement.setDouble(3, merchandise.price);
        statement.setString(4, merchandise.category);
        statement.setString(5, merchandise.description);
        statement.setInt(6, merchandise.quantity);

        return statement.executeUpdate() == 1;
    }

    public static boolean updateMerchandise(Connection dbConnection, Merchandise merchandise) throws SQLException {
        final String updateQuery = "UPDATE Merchandise set MerchName = ?, Price = ?, Category = ?, Description = ?, Quantity = ? WHERE MerchID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(updateQuery);
        statement.setString(1, merchandise.merchName);
        statement.setDouble(2, merchandise.price);
        statement.setString(3, merchandise.category);
        statement.setString(4, merchandise.description);
        statement.setInt(5, merchandise.quantity);
        statement.setInt(6, merchandise.merchID);

        return statement.executeUpdate() == 1;
    }

    public static boolean deleteMerchandise(Connection dbConnection, int merchID) throws SQLException {
        final String deleteQuery = "DELETE FROM Merchandise WHERE MerchID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(deleteQuery);
        statement.setInt(1, merchID);

        return statement.executeUpdate() == 1;
    }
}
