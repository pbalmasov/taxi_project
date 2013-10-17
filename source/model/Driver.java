/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model;

import java.util.ArrayList;

import ru.ntechs.R;
import android.app.Activity;
import android.content.res.Resources;
import android.util.Log;

/**
 * Модель водителя
 * @author p.balmasov
 */
public class Driver {

    private Integer _status;
    private String _area;
    private String _city;
    private Integer _carClass = null;

    private ArrayList<Message> _messages;
    private ArrayList<Order> _myOrders;
    private ArrayList<Order> _freeOrders;
    private ArrayList<Order> _districtOrders;
    private String balance;
    //
    private int totalOrders;

    private String district;
    private String subdistrict;
    private ArrayList<Order> _reports;

    private Activity _context;
    private Integer carId;
    private String waitString;

    public Driver(Activity context, int status, int carClass, String district, String subdistrict) {
        this._context = context;
        this._status = status;
        this._carClass = carClass;
        this.district = district;
        this.subdistrict = subdistrict;
    }

    public String getFullDisctrict() {
        String rayonString = "";
        if (district != null) {
            rayonString = district;
            if (subdistrict != null)
                rayonString += ", " + subdistrict;
        } else
            rayonString = _context.getString(R.string.no_region);
        return rayonString;
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
     * @param messages
     *            the _messages to set
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
     * @param orders
     *            the _orders to set
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
     * @param freeOrders
     *            the _freeOrders to set
     */
    public void setFreeOrders(ArrayList<Order> freeOrders) {
        this._freeOrders = freeOrders;
    }

    /**
     * @return the _status
     */
    public Integer getStatus() {
        return _status;
    }

    /**
     * @param status
     *            the _status to set
     */
    public void setStatus(Integer status) {
        this._status = status;
    }

    public String getStatusString() {
        if (_status == null)
            return _context.getString(R.string.no_status);
        Resources res = _context.getResources();
        String[] status = res.getStringArray(R.array.status_array);
        return status[_status].toLowerCase();
    }

    public String getClassAutoString() {
        if(_carClass==null)
            return "";

        Resources res = _context.getResources();
        String[] carClass = res.getStringArray(R.array.class_array);
        Log.d("My_tag", String.valueOf(getCarId()));
        Log.d("My_tag", String.valueOf(_carClass));
        String result = "";
        int i = _carClass;

        for (; i < getCarId(); i++)
            result = result + carClass[i].toLowerCase() + ", ";
        Log.d("My_tag",result);
        return result.substring(0, result.length() - 2);
    }

    /**
     * @return the _classAuto
     */
    public Integer getClassAuto() {
        return _carClass;
    }

    /**
     * @param classAuto
     *            the _classAuto to set
     */
    public void setClassAuto(Integer classAuto) {
        if (classAuto != null)
            this._carClass = classAuto - 1;
    }

    /**
     * @return the balance
     */
    public String getBalance() {
        if(balance == null)
            return "0";
        return balance;
    }

    /**
     * @param balance
     *            the balance to set
     */
    public void setBalance(String balance) {
        this.balance = balance;
    }

    /**
     * @return the totalOrders
     */
    public int getTotalOrders() {
        return totalOrders;
    }

    /**
     * @param totalOrders
     *            the totalOrders to set
     */
    public void setTotalOrders(int totalOrders) {
        this.totalOrders = totalOrders;
    }

    /**
     * @return the district
     */
    public String getDistrict() {
        return district;
    }

    /**
     * @param district
     *            the district to set
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
     * @param subdistrict
     *            the subdistrict to set
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
     * @param reports
     *            the _reports to set
     */
    public void setReports(ArrayList<Order> reports) {
        this._reports = reports;
    }

    public ArrayList<Order> get_districtOrders() {
        return _districtOrders;
    }

    public void set_districtOrders(ArrayList<Order> _districtOrders) {
        this._districtOrders = _districtOrders;
    }

    public Integer getCarId() {
        return carId;
    }

    public void setCarId(Integer carId) {
        this.carId = carId;
    }

    public String getWaitString() {
        if (waitString == null)
            return _context.getString(R.string.no_wait);
        return waitString;
    }

    public void setWaitString(String waitString) {
        this.waitString = waitString;
    }

}
