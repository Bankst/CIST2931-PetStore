package com.cist2931.petstore.application.order;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public final class OrderSQL {
    private OrderSQL() {}

    public static List<Order> getAllOrders(Connection conn) throws SQLException {
        final String selectQuery = "SELECT * FROM Orders ORDER BY OrderID";
        PreparedStatement statement = conn.prepareStatement(selectQuery);

        ResultSet resultSet = statement.executeQuery();
        List<Order> orders = new ArrayList<>();
        while (resultSet.next()) {
            Order order = new Order(resultSet);
            List<OrderMerchandise> orderMerchandiseList = OrderMerchandiseSQL.getOrderMerchandisesByOrderID(conn, order.getOrderID());
            order.getOrderMerchandiseContainer().addItems(orderMerchandiseList);
            orders.add(order);
        }
        return orders;
    }

    public static Optional<Order> getOrderByOrderID(Connection conn, int orderID) throws SQLException {
        final String selectQuery = "SELECT * FROM Orders WHERE OrderID = ?";
        PreparedStatement statement = conn.prepareStatement(selectQuery);
        statement.setInt(1, orderID);

        ResultSet resultSet = statement.executeQuery();
        return resultSet.next() ? Optional.of(new Order(resultSet)) : Optional.empty();
    }

    public static List<Order> getOrdersByCustomerID(Connection conn, int customerID) throws SQLException {
        final String selectQuery = "SELECT * FROM Orders WHERE CustomerID = ? ORDER BY OrderID";
        PreparedStatement statement = conn.prepareStatement(selectQuery);
        statement.setInt(1, customerID);

        ResultSet resultSet = statement.executeQuery();
        List<Order> orders = new ArrayList<>();
        while (resultSet.next()) {
            Order order = new Order(resultSet);
            List<OrderMerchandise> orderMerchandiseList = OrderMerchandiseSQL.getOrderMerchandisesByOrderID(conn, order.getOrderID());
            order.getOrderMerchandiseContainer().addItems(orderMerchandiseList);
            orders.add(order);
        }
        return orders;
    }

    public static boolean insertOrder(Connection conn, Order order) throws SQLException {
        final String insertQuery = "INSERT INTO Orders(CustomerID, Status) values (?, ?)";
        PreparedStatement statement = conn.prepareStatement(insertQuery, Statement.RETURN_GENERATED_KEYS);
        statement.setInt(1, order.getCustomerID());
        statement.setString(2, order.getStatus());

        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
            return false; // TODO: LOG
        } else {
            try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newKey = (int) generatedKeys.getLong(1);
                    order.setOrderID(newKey);

                    return OrderMerchandiseSQL.insertManyOrderMerchandises(conn, order.getOrderMerchandiseContainer().getMerchandiseList()) > 0;
                } else {
                    return false;
                }
            }
        }
    }

    public static boolean updateOrder(Connection conn, Order order) throws SQLException {
        final String updateQuery = "UPDATE Orders set Status = ? WHERE OrderID = ?";
        PreparedStatement statement = conn.prepareStatement(updateQuery);

        statement.setString(1, order.getStatus());
        statement.setInt(2, order.getOrderID());

        return statement.executeUpdate() == 1;
    }

    public static boolean updateOrderMerchandise(Connection conn, Order order) throws SQLException {
        return OrderMerchandiseSQL.updateManyOrderMerchandise(conn, order.getOrderMerchandiseContainer().getMerchandiseList()) > 0;
    }

    public static boolean deleteOrderByID(Connection conn, int orderID) throws SQLException {
        final String deleteQuery = "DELETE FROM Orders WHERE OrderID = ?";
        PreparedStatement statement = conn.prepareStatement(deleteQuery);
        statement.setInt(1, orderID);

        return statement.executeUpdate() == 1 && OrderMerchandiseSQL.deleteOrderMerchandisesByOrderID(conn, orderID);
    }
}
