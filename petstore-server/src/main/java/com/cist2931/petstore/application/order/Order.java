package com.cist2931.petstore.application.order;

import java.sql.*;
import java.util.Arrays;

@SuppressWarnings("unused")
public class Order {
    private int orderID;
    private int customerID;
    private final OrderMerchandiseContainer orderMerchandiseContainer;
    private String status;

    public Order() {
        orderID = -1;
        customerID = 0;
        status = "Unknown";
        orderMerchandiseContainer = new OrderMerchandiseContainer(orderID);
    }

    public Order(int customerID, String status, OrderMerchandise... merchandises) {
        this.orderID = -1;
        this.customerID = customerID;
        this.status = status;
        orderMerchandiseContainer = new OrderMerchandiseContainer(orderID);
        orderMerchandiseContainer.addItems(Arrays.asList(merchandises));
    }

    protected Order(ResultSet rs) throws SQLException {
        orderID = rs.getInt("OrderID");
        customerID = rs.getInt("CustID");
        status = rs.getString("Status");
        orderMerchandiseContainer = new OrderMerchandiseContainer(orderID);
    }

    public boolean update(Connection dbConnection) throws SQLException {
        return OrderSQL.updateOrder(dbConnection, this);
    }

    public boolean insert(Connection dbConnection) throws SQLException {
        return OrderSQL.insertOrder(dbConnection, this);
    }

    public boolean delete(Connection dbConnection) throws SQLException {
        return OrderSQL.deleteOrderByID(dbConnection, this.orderID);
    }

    public int getOrderID() {
        return orderID;
    }

    protected void setOrderID(int orderID) {
        this.orderID = orderID;
        orderMerchandiseContainer.setOrderID(orderID);
    }

    public int getCustomerID() {
        return customerID;
    }

    public void setCustomerID(int customerID) {
        this.customerID = customerID;
    }

    public OrderMerchandiseContainer getOrderMerchandiseContainer() {
        return orderMerchandiseContainer;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    @Override
    public String toString() {
        return "Order{ " +
                "orderID: " + orderID +
                ", customerID: " + customerID +
                ", " + orderMerchandiseContainer +
                ", status: \"" + status +
                "\" }";
    }
}
