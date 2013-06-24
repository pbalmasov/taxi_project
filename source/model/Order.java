/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author papas
 */
public class Order implements OrderInterface {

    protected String _adress;
    protected String _carClass;
    protected String _orderText;
    protected String _where;
    protected int _index;
    protected int _costRide;
    protected Date _timerDate;
    protected int rides;
    protected String abonent;

    public Order(int costRide, String adress, String carClass, String orderText, String where, int index) {
        _adress = adress;
        _carClass = carClass;
        _orderText = orderText;
        _where = where;
        _index = index;
        _costRide = costRide;
    }

    public int get_index() {
        return _index;
    }

    public void setTimerDate(Date date) {
        _timerDate = date;
    }

    public Date getTimerDate() {
        return _timerDate;
    }

    public ArrayList<String> toArrayList() {
        return null;
    }

    public String getAbonent() {
        return abonent;
    }

    public void setAbonent(String abonent) {
        this.abonent = abonent;
    }

    public int getRides() {
        return rides;
    }

    public void setRides(int rides) {
        this.rides = rides;
    }
}
