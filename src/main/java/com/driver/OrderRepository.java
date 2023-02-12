package com.driver;

import org.springframework.stereotype.Repository;

import java.util.*;

@Repository
public class OrderRepository {

    HashMap<String, Order> orders;
    HashMap<String, DeliveryPartner> deliveryPartners;
    HashMap<String, List<String>>  pairDb;
    HashMap<String, String> assignedDb;

    public OrderRepository(){
        this.orders = new HashMap<>();
        this.deliveryPartners = new HashMap<>();
        this.pairDb=new HashMap<>();
        this.assignedDb=new HashMap<>();
    }

    public String addOrder(Order order){
        orders.put(order.getId(),order);
        return "New order added successfully";
    }

    public String addPartner(String partnerId)
    {
        DeliveryPartner deliveryPartner = new DeliveryPartner(partnerId);
        deliveryPartners.put(deliveryPartner.getId(),deliveryPartner);
        return "New delivery partner added successfully";
    }
    public String addOrderPartnerPair(String orderId,String partnerId)
    {
        List<String> list = pairDb.getOrDefault(partnerId, new ArrayList<>());
        list.add(orderId);
        pairDb.put(partnerId, list);
        assignedDb.put(orderId, partnerId);
        DeliveryPartner partner = deliveryPartners.get(partnerId);
        partner.setNumberOfOrders(list.size());
        return "Added";

    }

    public Order getOrderById(String orderId)
    {
        for(String s: orders.keySet())
        {
            if(s.equals(orders.containsKey(orderId)))
                return orders.get(orderId);
        }
        return null;
    }

    public DeliveryPartner getPartnerById(String partnerId)
    {
        for(String s: deliveryPartners.keySet())
        {
            if(s.equals(partnerId))
            {
                return deliveryPartners.get(partnerId);
            }
        }
        return null;
    }

    public int getOrderCountByPartnerId(String partnerId)
    {

            if(deliveryPartners.containsKey(partnerId))
            {
                DeliveryPartner deliveryPartner=deliveryPartners.get(partnerId);
                return deliveryPartner.getNumberOfOrders();
            }

        return 0;
    }

    public List<String> getOrdersByPartnerId(String partnerId) {
        // orders should contain a list of orders by PartnerId

        List<String> order = pairDb.getOrDefault(partnerId, new ArrayList<>());
        return order;
    }

    public List<String> getAllOrders() {
        // Get all orders
        List<String> order = new ArrayList<>();
        for (String s : orders.keySet()) {
            order.add(s);
        }
        return order;

    }

    public int getCountOfUnassignedOrders() {
        // Count of orders that have not been assigned to any DeliveryPartner
        int countOfOrders = orders.size() - assignedDb.size();
        return countOfOrders;
    }

    public int getOrdersLeftAfterGivenTimeByPartnerId(String time, String partnerId) {
        // countOfOrders that are left after a particular time of a DeliveryPartner
        int countOfOrders = 0;
        List<String> list = pairDb.get(partnerId);
        int deliveryTime = Integer.parseInt(time.substring(0, 2)) * 60 + Integer.parseInt(time.substring(3));
        for (String s : list) {
            Order order = orders.get(s);
            if (order.getDeliveryTime() > deliveryTime) {
                countOfOrders++;
            }
        }
        return countOfOrders;
    }

    public String getLastDeliveryTimeByPartnerId(String partnerId) {
        // Return the time when that partnerId will deliver his last delivery order.
        String time = "";
        List<String> list = pairDb.get(partnerId);
        int deliveryTime = 0;
        for (String s : list) {
            Order order = orders.get(s);
            deliveryTime = Math.max(deliveryTime, order.getDeliveryTime());
        }
        int hour = deliveryTime / 60;
        String sHour = "";
        if (hour < 10) {
            sHour = "0" + String.valueOf(hour);
        } else {
            sHour = String.valueOf(hour);
        }

        int min = deliveryTime % 60;
        String sMin = "";
        if (min < 10) {
            sMin = "0" + String.valueOf(min);
        } else {
            sMin = String.valueOf(min);
        }

        time = sHour + ":" + sMin;

        return time;

    }

    public String deletePartnerById(String partnerId) {
        // Delete the partnerId
        // And push all his assigned orders to unassigned orders.
        deliveryPartners.remove(partnerId);

        List<String> list = pairDb.getOrDefault(partnerId, new ArrayList<>());
        ListIterator<String> itr = list.listIterator();
        while (itr.hasNext()) {
            String s = itr.next();
            assignedDb.remove(s);
        }
        pairDb.remove(partnerId);
        return "Deleted";
    }

    public String deleteOrderById(String orderId) {

        // Delete an order and also
        // remove it from the assigned order of that partnerId
        orders.remove(orderId);
        String partnerId = assignedDb.get(orderId);
        assignedDb.remove(orderId);
        List<String> list = pairDb.get(partnerId);

        ListIterator<String> itr = list.listIterator();
        while (itr.hasNext()) {
            String s = itr.next();
            if (s.equals(orderId)) {
                itr.remove();
            }
        }
        pairDb.put(partnerId, list);

        return "Deleted";

    }




}
