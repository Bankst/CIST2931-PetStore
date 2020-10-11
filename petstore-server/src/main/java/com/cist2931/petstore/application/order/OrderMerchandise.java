package com.cist2931.petstore.application.order;

import com.fasterxml.jackson.annotation.JsonCreator;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonProperty;

import java.sql.ResultSet;
import java.sql.SQLException;

@SuppressWarnings("unused")
public class OrderMerchandise {
    private final int orderID;
    private int merchandiseID;
    private int merchandiseQuantity;

    protected OrderMerchandise(ResultSet resultSet) throws SQLException {
        this(
                resultSet.getInt("OrderID"),
                resultSet.getInt("MerchID"),
                resultSet.getInt("MerchQuantity")
        );
    }

    @JsonCreator
    public OrderMerchandise(
            @JsonProperty("merchID") int merchandiseID,
            @JsonProperty("merchQuantity") int merchandiseQuantity
    ) {
        this.orderID = -1;
        this.merchandiseID = merchandiseID;
        this.merchandiseQuantity = merchandiseQuantity;
    }

    public OrderMerchandise(int orderID, int merchandiseID, int merchandiseQuantity) {
        this.orderID = orderID;
        this.merchandiseID = merchandiseID;
        this.merchandiseQuantity = merchandiseQuantity;
    }

    public int getOrderID() {
        return orderID;
    }

    public int getMerchandiseID() {
        return merchandiseID;
    }

    public void setMerchandiseID(int merchandiseID) {
        this.merchandiseID = merchandiseID;
    }

    public int getMerchandiseQuantity() {
        return merchandiseQuantity;
    }

    public void setMerchandiseQuantity(int merchandiseQuantity) {
        this.merchandiseQuantity = merchandiseQuantity;
    }
}
