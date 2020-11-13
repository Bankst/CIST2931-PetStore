package com.cist2931.petstore.application.merchandise;

import com.cist2931.petstore.logging.Logger;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

@SuppressWarnings("unused")
public class MerchandiseSQL {
    private static final Logger logger = new Logger(MerchandiseSQL.class);

    private MerchandiseSQL() {}

    public static Optional<List<Merchandise>> getMerchandiseByCategory(Connection dbConnection, String category) {
        final String selectQuery = "SELECT * FROM Merchandise WHERE Category = ?";

        try {
            PreparedStatement statement = dbConnection.prepareStatement(selectQuery);
            statement.setString(1, category);

            ResultSet resultSet = statement.executeQuery();

            List<Merchandise> merchandiseList = new ArrayList<>();

            while (resultSet.next()) {
                merchandiseList.add(new Merchandise(resultSet));
            }
            return Optional.of(merchandiseList);
        } catch (SQLException ex) {
            logger.error("Failed to get merchandise list for category " + category + ".", ex);
            return Optional.empty();
        }
    }

    public static Optional<List<Merchandise>> getAllMerchandise(Connection dbConnection) {
        final String selectQuery = "SELECT * FROM Merchandise";

        try {
            PreparedStatement statement = dbConnection.prepareStatement(selectQuery);
            ResultSet resultSet = statement.executeQuery();

            List<Merchandise> merchandiseList = new ArrayList<>();

            while (resultSet.next()) {
                merchandiseList.add(new Merchandise(resultSet));
            }
            return Optional.of(merchandiseList);
        } catch (SQLException ex) {
            logger.error("Failed to get merchandise list.", ex);
            return Optional.empty();
        }
    }

    public static Optional<Merchandise> getMerchandiseById(Connection dbConnection, int id) {
        final String selectQuery = "SELECT * FROM Merchandise WHERE MerchID = ?";

        try {
            PreparedStatement statement = dbConnection.prepareStatement(selectQuery);
            statement.setInt(1, id);

            ResultSet resultSet = statement.executeQuery();
            return resultSet.next() ? Optional.of(new Merchandise(resultSet)) : Optional.empty();
        } catch (SQLException ex) {
            logger.error("Failed to get merchandise from id.", ex);
            return Optional.empty();
        }
    }

    public static boolean insertMerchandise(Connection dbConnection, Merchandise merchandise) throws SQLException {
        final String insertQuery = "INSERT INTO Merchandise(MerchName, Price, Category, Description, Quantity) values (?, ?, ?, ?, ?)";
        PreparedStatement statement = dbConnection.prepareStatement(insertQuery);
        statement.setString(1, merchandise.getMerchName());
        statement.setDouble(2, merchandise.getPrice());
        statement.setString(3, merchandise.getCategory());
        statement.setString(4, merchandise.getDescription());
        statement.setInt(5, merchandise.getQuantity());

        int affectedRows = statement.executeUpdate();
        if (affectedRows == 0) {
            return false; // TODO: LOG
        } else {
            try(ResultSet generatedKeys = statement.getGeneratedKeys()) {
                if (generatedKeys.next()) {
                    int newKey = (int) generatedKeys.getLong(1);
                    merchandise.setMerchID(newKey);
                    return true;
                } else {
                    return false;
                }
            }
        }
    }

    public static boolean updateMerchandise(Connection dbConnection, Merchandise merchandise) throws SQLException {
        final String updateQuery = "UPDATE Merchandise set MerchName = ?, Price = ?, Category = ?, Description = ?, Quantity = ? WHERE MerchID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(updateQuery);
        statement.setString(1, merchandise.getMerchName());
        statement.setDouble(2, merchandise.getPrice());
        statement.setString(3, merchandise.getCategory());
        statement.setString(4, merchandise.getDescription());
        statement.setInt(5, merchandise.getQuantity());
        statement.setInt(6, merchandise.getMerchID());

        return statement.executeUpdate() == 1;
    }

    public static boolean deleteMerchandise(Connection dbConnection, int merchID) throws SQLException {
        final String deleteQuery = "DELETE FROM Merchandise WHERE MerchID = ?";
        PreparedStatement statement = dbConnection.prepareStatement(deleteQuery);
        statement.setInt(1, merchID);

        return statement.executeUpdate() == 1;
    }
}
