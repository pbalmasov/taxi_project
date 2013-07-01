/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package orders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.peppers.R;

import model.Order;
import android.content.Context;

/**
 *
 * @author papas
 */
public class CostOrder extends Order {

    private String _costType;
    private int _cost;
    private Date _date;

    public CostOrder(Context context, int costRide, int index, Date date, String adress, String type,
            String orderText, String where, int cost, String costType) {
        super(context, costRide, adress, type, orderText, where, index);
        _date = date;
        _cost = cost;
        _costType = costType;

    }

    public String toString() {
        return getTimeString(_date) + ", " + _adress + ", " + _cost + " "
                + context.getString(R.string.currency);
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        if (abonent != null) {
            array.add(context.getString(R.string.abonent) + " " + abonent);
            array.add(context.getString(R.string.rides) + " " + rides);
        }
        array.add(context.getString(R.string.date) + " " + getTimeString(_date));
        array.add(context.getString(R.string.adress) + " " + _adress);
        array.add(context.getString(R.string.where) + " " + _where);
        array.add(context.getString(R.string.car_class) + " " + _carClass);
        array.add(context.getString(R.string.cost_type) + " " + _costType);
        array.add(_orderText);
        array.add(context.getString(R.string.cost) + " " + _cost + " " + context.getString(R.string.currency));
        array.add(context.getString(R.string.cost_ride) + " " + _costRide + " "
                + context.getString(R.string.currency));
        return array;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }
}
