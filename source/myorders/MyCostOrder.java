/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class MyCostOrder extends Order {

    private String _costType;
    private int _cost;
    private Date _date;
    private Date _dateInvite;
    private Date _dateAccept;

    public MyCostOrder(Activity activity, int costRide,int index,Date date, String adress, String type, String orderText, String where,int cost,String costType,Date dateInvite,Date dateAccept) {
        super(activity,costRide,adress, type, orderText, where,index);
        _date = date;
        _dateInvite = dateInvite;
        _dateAccept = dateAccept;
        _cost = cost;
        _costType = costType;
    }

    public String toString(){
        return getTimeString(_dateAccept)+", "+_adress+", "+_cost+" "+context.getString(R.string.currency);
    }

    public ArrayList<String> toArrayList(){
        ArrayList<String> array = new ArrayList<String>();
        if (abonent != null) {
            array.add(context.getString(R.string.abonent)+" " + abonent);
            array.add(context.getString(R.string.rides)+" " + rides);
        }
        array.add(context.getString(R.string.accepted)+" "+getTimeString(_dateAccept));
        array.add(context.getString(R.string.date)+" "+getTimeString(_date));
        array.add(context.getString(R.string.date_invite)+" "+getTimeString(_dateInvite));
        array.add(context.getString(R.string.adress)+" "+_adress);
        array.add(context.getString(R.string.where)+" "+_where);
        array.add(context.getString(R.string.car_class)+" " + _carClass);
        array.add(context.getString(R.string.cost_type)+" "+_costType);
        array.add(_orderText);
        array.add(context.getString(R.string.cost)+" "+_cost+" "+context.getString(R.string.currency));
        array.add(context.getString(R.string.cost_ride)+" " + _costRide+" "+context.getString(R.string.currency));
        return array;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }
}
