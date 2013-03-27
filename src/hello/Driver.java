/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package hello;

import java.util.ArrayList;

/**
 *
 * @author papas
 */
public class Driver {

    private int _status;
    private String _area;
    private String _city;
    private int _carClass;

    private ArrayList<Message> _messages;
    private ArrayList<Order> _myOrders;
    private ArrayList<Order> _freeOrders;
    private int balance;
    //
    private int ordersCount;
    private int totalOrders;

    private String district;
    private String subdistrict;
    private ArrayList<Order> _reports;

    public Driver(int status, int carClass, int ordersCount, String district, String subdistrict) {
        this._status = status;
        this._carClass = carClass;
        this.ordersCount = ordersCount;
        this.district = district;
        this.subdistrict = subdistrict;
    }

    public String toString() {
        return getStatusString() + " " + _area + " " + _city + " " + getClassAuto();
    }

    public Message getMessage(int index) {
        return _messages.get(index);
    }

    public int reportsCount() {
        return _reports.size();
    }

    public Order getReport(int index) {
        return _reports.get(index);
    }

    public Order getFreeOrder(int index) {
        return _freeOrders.get(index);
    }

    public int ordersFreeCount() {
        return _freeOrders.size();
    }

    public Order getOrder(int index) {
        return getOrders().get(index);
    }

    public int ordersCount() {
        return _myOrders.size();
    }

    public int messagesCount() {
        return _messages.size();
    }

    /**
     * @return the _messages
     */
    public ArrayList<Message> getMessages() {
        return _messages;
    }

    /**
     * @param messages the _messages to set
     */
    public void setMessages(ArrayList<Message> messages) {
        this._messages = messages;
    }

    /**
     * @return the _orders
     */
    public ArrayList<Order> getOrders() {
        return _myOrders;
    }

    /**
     * @param orders the _orders to set
     */
    public void setOrders(ArrayList<Order> orders) {
        this._myOrders = orders;
    }

    /**
     * @return the _freeOrders
     */
    public ArrayList<Order> getFreeOrders() {
        return _freeOrders;
    }

    /**
     * @param freeOrders the _freeOrders to set
     */
    public void setFreeOrders(ArrayList<Order> freeOrders) {
        this._freeOrders = freeOrders;
    }

    /**
     * @return the _status
     */
    public int getStatus() {
        return _status;
    }

    /**
     * @param status the _status to set
     */
    public void setStatus(int status) {
        this._status = status;
    }

    public String getStatusString() {
        if (_status == 0)
            return "свободен";
        if (_status == 1)
            return "перерыв";
        if (_status == 2)
            return "недоступен";
        return null;
    }

    public String getClassAutoString() {
        if (_carClass == 0)
            return "эконом";
        if (_carClass == 1)
            return "стандарт";
        if (_carClass == 2)
            return "базовый";
        return null;
    }

    /**
     * @return the _classAuto
     */
    public int getClassAuto() {
        return _carClass;
    }

    /**
     * @param classAuto the _classAuto to set
     */
    public void setClassAuto(int classAuto) {
        this._carClass = classAuto;
    }

    /**
     * @return the balance
     */
    public int getBalance() {
        return balance;
    }

    /**
     * @param balance the balance to set
     */
    public void setBalance(int balance) {
        this.balance = balance;
    }

    /**
     * @return the totalOrders
     */
    public int getTotalOrders() {
        return totalOrders;
    }

    /**
     * @param totalOrders the totalOrders to set
     */
    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    /**
     * @return the ordersCount
     */
    public int getOrdersCount() {
        return ordersCount;
    }

    /**
     * @param ordersCount the ordersCount to set
     */
    public void setOrdersCount(int ordersCount) {
        this.ordersCount = ordersCount;
    }

    /**
     * @return the district
     */
    public String getDistrict() {
        return district;
    }

    /**
     * @param district the district to set
     */
    public void setDistrict(String district) {
        this.district = district;
    }

    /**
     * @return the subdistrict
     */
    public String getSubdistrict() {
        return subdistrict;
    }

    /**
     * @param subdistrict the subdistrict to set
     */
    public void setSubdistrict(String subdistrict) {
        this.subdistrict = subdistrict;
    }

    /**
     * @return the _reports
     */
    public ArrayList<Order> getReports() {
        return _reports;
    }

    /**
     * @param reports the _reports to set
     */
    public void setReports(ArrayList<Order> reports) {
        this._reports = reports;
    }
}
