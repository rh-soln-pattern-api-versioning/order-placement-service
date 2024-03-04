package org.globex.retail.model;
import java.util.Objects;

public class Order {

    
    private String payLoad;
    private String orderId;
    public Order() {
    } 

    public Order(String payLoad, String orderId) {
        this.payLoad = payLoad;
        this.orderId = orderId;
    }

    public String getPayLoad() {
        return payLoad;
    }

    public void setPayLoad(String payLoad) {
        this.payLoad = payLoad;
    }

    public String getOrderId() {
        return orderId;
    }

    public void setOrderId(String orderId) {
        this.orderId = orderId;
    }

    @Override
    public boolean equals(Object obj) {
        if (!(obj instanceof Order)) {
            return false;
        }

        Order other = (Order) obj;

        return Objects.equals(other.orderId, this.orderId);
    }

    @Override
    public int hashCode() {
        return Objects.hash(this.orderId);
    }

    @Override
    public String toString() {
        // TODO Auto-generated method stub
        return super.toString();
    }
    
    
    
    
} 