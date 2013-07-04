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
public class MyPreliminaryOrder extends Order {

    private Date _date;

    public MyPreliminaryOrder(Activity activity, int costRide, int index, Date date, String adress,
            Integer type, String orderText, String where) {
        super(activity, costRide, adress, type, orderText, where, index);
        _date = date;
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        if (nickname != null) {
            array.add(context.getString(R.string.abonent) + " " + nickname);
            array.add(context.getString(R.string.rides) + " " + quantity);
        }
        array.add(context.getString(R.string.preliminary));
        array.add(context.getString(R.string.date) + " " + getTimeString(_date));
        array.add(context.getString(R.string.adress) + " " + _addressdeparture);
        array.add(context.getString(R.string.where) + " " + _addressarrival);
        array.add(context.getString(R.string.car_class) + " " + _carClass);
        array.add(context.getString(R.string.cost_ride) + " " + _nominalcost + " "
                + context.getString(R.string.currency));
        array.add(_comment);
        return array;
    }

    public String toString() {

        return getTimeString(_date) + ", " + context.getString(R.string.preliminary).toLowerCase() + " "
                + _addressdeparture;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
        return dateFormat.format(date);
    }

}
