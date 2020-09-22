package com.cist2931.petstore.application.order;

import com.cist2931.petstore.application.merchandise.Merchandise;

import java.util.*;

@SuppressWarnings("unused")
public class OrderMerchandiseContainer {
    private final HashMap<Integer, Integer> merchandiseQuantityByID = new LinkedHashMap<>();

    private int orderID;

    public OrderMerchandiseContainer(int orderID) {
        this.orderID = orderID;
    }

    protected void setOrderID(int orderID) {
        this.orderID = orderID;
    }

    public void addItem(int merchandiseID, int merchandiseQuantity) {
        merchandiseQuantityByID.putIfAbsent(merchandiseID, merchandiseQuantity);
    }

    public void addItem(Merchandise merch) {
        addItem(merch.getMerchID(), merch.getQuantity());
    }

    public void addItem(OrderMerchandise merchandise) {
        addItem(merchandise.getMerchandiseID(), merchandise.getMerchandiseQuantity());
    }

    public void addItems(List<OrderMerchandise> merchandiseList) {
        for (OrderMerchandise orderMerchandise : merchandiseList) {
            addItem(orderMerchandise.getMerchandiseID(), orderMerchandise.getMerchandiseQuantity());
        }
    }

    public void removeItem(int merchandiseID) {
        merchandiseQuantityByID.remove(merchandiseID);
    }

    public void updateItem(int merchandiseID, int newQuantity) {
        if (newQuantity == 0) {
            removeItem(merchandiseID);
        } else {
            merchandiseQuantityByID.put(merchandiseID, newQuantity);
        }
    }

    public HashMap<Integer, Integer> getMerchandiseQuantityByID() {
        return merchandiseQuantityByID;
    }

    public List<OrderMerchandise> getMerchandiseList() {
        List<OrderMerchandise> merchandiseList = new ArrayList<>();
        merchandiseQuantityByID.forEach((merchID, merchQty) -> merchandiseList.add(new OrderMerchandise(orderID, merchID, merchQty)));
        return merchandiseList;
    }

    @Override
    public String toString() {
        return "OrderMerchandiseContainer{ " +
                merchandiseQuantityByID +
                " }";
    }
}
