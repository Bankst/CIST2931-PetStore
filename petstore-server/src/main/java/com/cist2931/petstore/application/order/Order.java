package com.cist2931.petstore.application.order;

import java.sql.*;

public class Order {
    private int orderID;
    private int merchID;
    private int customerID;
    private int quantity;
    private String status;

    public Order(ResultSet rs) throws SQLException {
        orderID = rs.getInt("OrderID");
        merchID = rs.getInt("MerchID");
        customerID = rs.getInt("CustID");
        quantity = rs.getInt("Quantity");
        status = rs.getString("Status");
    }

    @Override
    public String toString() {
        return "Orders{" +
                "orderID=" + orderID +
                ", merchID='" + merchID + '\'' +
                ", customerID='" + customerID + '\'' +
                ", quantity=" + quantity + '\'' +
                ", status=" + status +
                '}';
    }

    public boolean update(Connection dbConnection) throws SQLException {
        return updateOrders(dbConnection, this);
    }

    public boolean insert(Connection dbConnection) throws SQLException {
        return insertOrders(dbConnection, this);
    }

    public boolean delete(Connection dbConnection) throws SQLException {
        return deleteOrders(dbConnection, this.orderID);
    }

    public int getOrderID() {
        return orderID;
    }

    public void setOrderID(int orderID){
        this.orderID = orderID;
    }

    public int getMerchID() {
        return merchID;
    }

    public void setMerchID(int merchID){
        this.merchID = merchID;
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public int getQuantity(){
        return quantity;
    }

    public void setQuantity(int quantity) {
        this.quantity = quantity;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public static Order GetOrdersByID(Connection dbConnection, int id) throws SQLException {
        final String selectQuery = "SELECT * FROM Orders WHERE OrderID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(selectQuery);
        statement.setInt(1, id);

        ResultSet resultSet = statement.executeQuery();
        if (resultSet.next()) {
            return new Order(resultSet);
        }
        else return null;
    }

    public static boolean insertOrders(Connection dbConnection, Order order) throws SQLException {
        final String insertQuery = "INSERT INTO Orders(OrderID, MerchID, CustID, Quantity, Status) values (?, ?, ?, ?, ?)";
        PreparedStatement statement = dbConnection.prepareStatement(insertQuery);
        statement.setInt(1, order.orderID);
        statement.setInt(2, order.merchID);
        statement.setInt(3, order.customerID);
        statement.setInt(4, order.quantity);
        statement.setString(5, order.status);

        return statement.executeUpdate() == 1;
    }

    public static boolean updateOrders(Connection dbConnection, Order order) throws SQLException {
        final String updateQuery = "UPDATE Orders set MerchID = ?, CustID = ?, Quantity = ?, Status = ? WHERE OrderID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(updateQuery);
        statement.setInt(1, order.merchID);
        statement.setInt(2, order.customerID);
        statement.setInt(3, order.quantity);
        statement.setString(4, order.status);
        statement.setInt(5, order.orderID);

        return statement.executeUpdate() == 1;
    }

    public static boolean deleteOrders(Connection dbConnection, int orderID) throws SQLException {
        final String deleteQuery = "DELETE FROM Orders WHERE OrderID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(deleteQuery);
        statement.setInt(1, orderID);

        return statement.executeUpdate() == 1;
    }
}
