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

    private Integer _paymenttype;
    private Date _registrationtime;
    private Date _departuretime;

    public CostOrder(Context context, Integer nominalcost, Date registrationtime, String addressdeparture, Integer carClass, String comment, String addressarrival,Integer paymenttype,Date departuretime) {
        super(context,nominalcost,addressdeparture, carClass, comment, addressarrival,0);
        _registrationtime = registrationtime;
        _paymenttype = paymenttype;
        _departuretime  = departuretime;
    }

    public String toString() {
    	String pred = "";
    	if(_departuretime!=null)
    		pred = "П "+getTimeString(_departuretime)+", ";
    	else pred = getTimeString(_registrationtime);
    		
    	String over = "";
    	if(_nominalcost!=null)
    		over = ", "+_nominalcost+" "+context.getString(R.string.currency);
        return pred + ", " + _addressdeparture + over;
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        if (nickname != null) {
            array.add(context.getString(R.string.abonent) + " " + nickname);
            array.add(context.getString(R.string.rides) + " " + quantity);
        }
        if(_departuretime!=null)
        array.add(context.getString(R.string.accepted)+" "+getTimeString(_departuretime));
        array.add(context.getString(R.string.date) + " " + getTimeString(_registrationtime));
        array.add(context.getString(R.string.adress) + " " + _addressdeparture);
        array.add(context.getString(R.string.where) + " " + _addressarrival);
        array.add(context.getString(R.string.car_class) + " " + getCarClass());
        array.add(context.getString(R.string.cost_type) + " " + getPayment());
        array.add(_comment);
        if(_nominalcost!=null)
        array.add(context.getString(R.string.cost_ride) + " " + _nominalcost + " "
                + context.getString(R.string.currency));
        return array;
    }

	public String getPayment() {
		if (_paymenttype == 0)
			return "наличные";
		if (_paymenttype == 1)
			return "безнал";
		return null;
	}
	
    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }
}
