/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package myorders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;

import ru.peppers.MyOrderActivity;
import ru.peppers.R;

import model.Order;

/**
 *
 * @author papas
 */
public class MyNoCostOrder extends Order {

    private Date _date;
    private Date _dateAccept;

    public MyNoCostOrder(Activity activity, int costRide, int index, Date date, String adress, Integer type,
            String orderText, String where, Date dateAccept) {
        super(activity, costRide, adress, type, orderText, where, index,0);
        _date = date;
        _dateAccept = dateAccept;
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        if (nickname != null) {
            array.add(_context.getString(R.string.abonent) + " " + nickname);
            array.add(_context.getString(R.string.rides) + " " + quantity);
        }
        array.add(_context.getString(R.string.accepted) + " " + getTimeString(_dateAccept));
        array.add(_context.getString(R.string.date) + " " + getTimeString(_date));
        array.add(_context.getString(R.string.adress) + " " + _addressdeparture);
        array.add(_context.getString(R.string.where) + " " + _addressarrival);
        array.add(_context.getString(R.string.car_class) + " " + _carClass);
        array.add(_context.getString(R.string.cost_ride) + " " + _nominalcost + " "
                + _context.getString(R.string.currency));
        array.add(_comment);
        return array;
    }

    public String toString() {
        return getTimeString(_dateAccept) + ", " + _addressdeparture;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }

}
