/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myorders;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;

import ru.peppers.MyOrderActivity;
import ru.peppers.R;

import model.Order;

/**
 *
 * @author papas
 */
public class MyCostOrder extends Order {

    private Integer _paymenttype;
    private Date _registrationtime;
    private Date _invitationtime;
    private Date _departuretime;

    public MyCostOrder(Context context, Integer nominalcost, Date registrationtime, String addressdeparture, Integer carClass, String comment, String addressarrival,Integer paymenttype,Date invitationtime,Date departuretime) {
        super(context,nominalcost,addressdeparture, carClass, comment, addressarrival,0);
        //TODO:wrong index
        _registrationtime = registrationtime;
        _invitationtime = invitationtime;
        _departuretime = departuretime;
        _paymenttype = paymenttype;
    }

    public String toString(){
    	String pred = "";
    	if(_departuretime!=null)
    		pred = "П "+getTimeString(_departuretime)+", ";
    	String over = "";
    	if(_nominalcost!=0)
    		over = ", "+_nominalcost+" "+context.getString(R.string.currency);
    	
    	return pred+_addressdeparture+over;
    }

    public ArrayList<String> toArrayList(){
        ArrayList<String> array = new ArrayList<String>();
        if (nickname != null) {
            array.add(context.getString(R.string.abonent)+" " + nickname);
            array.add(context.getString(R.string.rides)+" " + quantity);
        }
        if(_departuretime!=null)
        array.add(context.getString(R.string.accepted)+" "+getTimeString(_departuretime));
        array.add(context.getString(R.string.date)+" "+getTimeString(_registrationtime));
        array.add(context.getString(R.string.date_invite)+" "+getTimeString(_invitationtime));
        
        array.add(context.getString(R.string.adress)+" "+_addressdeparture);
        array.add(context.getString(R.string.where)+" "+_addressarrival);
        
        array.add(context.getString(R.string.car_class)+" " + _carClass);
        array.add(context.getString(R.string.cost_type)+" "+_paymenttype);
        array.add(_comment);
        array.add(context.getString(R.string.cost)+" "+_nominalcost+" "+context.getString(R.string.currency));
        return array;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }
}
