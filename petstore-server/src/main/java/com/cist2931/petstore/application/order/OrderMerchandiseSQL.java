package com.cist2931.petstore.application.order;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@SuppressWarnings("unused")
public class OrderMerchandiseSQL {
    private OrderMerchandiseSQL() {}

    protected static int insertManyOrderMerchandises(Connection conn, List<OrderMerchandise> merchandiseList) throws SQLException {
        final String insertQuery = "INSERT INTO OrderMerchandise VALUES (?, ?, ?)";

        PreparedStatement statement = conn.prepareStatement(insertQuery);
        for (OrderMerchandise orderMerchandise : merchandiseList) {
            statement.setInt(1, orderMerchandise.getOrderID());
            statement.setInt(2, orderMerchandise.getMerchandiseID());
            statement.setInt(3, orderMerchandise.getMerchandiseQuantity());
            statement.addBatch();
        }

        int[] updates = statement.executeBatch();
        return Arrays.stream(updates).sum();
    }

    public static boolean updateOrderMerchandise(Connection conn, OrderMerchandise merchandise) throws SQLException {
        final String updateQuery = "UPDATE OrderMerchandise SET MerchQuantity = ? WHERE MerchID = ?";

        PreparedStatement statement = conn.prepareStatement(updateQuery);
        statement.setInt(1, merchandise.getMerchandiseQuantity());
        statement.setInt(2, merchandise.getMerchandiseID());

        return statement.executeUpdate() == 1;
    }

    protected static int updateManyOrderMerchandise(Connection conn, List<OrderMerchandise> merchandiseList) throws SQLException {
        final String updateQuery = "UPDATE OrderMerchandise SET MerchQuantity = ? WHERE MerchID = ?";

        PreparedStatement statement = conn.prepareStatement(updateQuery);
        for (OrderMerchandise orderMerchandise : merchandiseList) {
            statement.setInt(1, orderMerchandise.getMerchandiseQuantity());
            statement.setInt(2, orderMerchandise.getMerchandiseID());
            statement.addBatch();
        }

        int[] updates = statement.executeBatch();
        return Arrays.stream(updates).sum();
    }

    protected static boolean deleteOrderMerchandiseByMerchID(Connection conn, int merchandiseID) throws SQLException {
        final String deleteQuery = "DELETE FROM OrderMerchandise WHERE MerchID = ?";

        PreparedStatement statement = conn.prepareStatement(deleteQuery);
        statement.setInt(1, merchandiseID);

        return statement.executeUpdate() == 1;
    }

    protected static boolean deleteOrderMerchandisesByOrderID(Connection conn, int orderID) throws SQLException {
        final String deleteQuery = "DELETE FROM OrderMerchandise WHERE OrderID = ?";

        PreparedStatement statement = conn.prepareStatement(deleteQuery);
        statement.setInt(1, orderID);

        return statement.executeUpdate() == 1;
    }

    protected static List<OrderMerchandise> getOrderMerchandisesByOrderID(Connection conn, int orderID) throws SQLException {
        final String selectQuery = "SELECT * FROM OrderMerchandise WHERE OrderID = ?";

        PreparedStatement statement = conn.prepareStatement(selectQuery);
        statement.setInt(1, orderID);

        List<OrderMerchandise> orderMerchandiseList = new ArrayList<>();
        ResultSet resultSet = statement.executeQuery();
        while(resultSet.next()) {
            orderMerchandiseList.add(new OrderMerchandise(resultSet));
        }
        return orderMerchandiseList;
    }
}
