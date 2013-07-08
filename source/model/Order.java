/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Date;

import ru.peppers.R;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;

/**
 * 
 * @author papas
 */
public class Order implements OrderInterface {

	protected String _addressdeparture;
	protected Integer _carClass;
	protected String _comment;
	protected String _addressarrival;
	protected String _index;
	protected Integer _nominalcost;
	protected Date _timerDate;
	protected Integer quantity;
	protected String nickname;
	private Integer _paymenttype;
	protected Context _context;

	public Order(Context context, Integer costRide, String adress, Integer carClass, String orderText, String where,
			Integer paymenttype, String index) {
		_context = context;
		_paymenttype = paymenttype;
		_addressdeparture = adress;
		_carClass = carClass;
		_comment = orderText;
		_addressarrival = where;
		_index = index;
		_nominalcost = costRide;
	}

	public String get_index() {
		return _index;
	}

	public void setTimerDate(Date date) {
		_timerDate = date;
	}

	public String getCarClass() {
		if (_carClass == 0)
			return _context.getString(R.string.dont_care);
		else {
			Resources res = _context.getResources();
			String[] payment = res.getStringArray(R.array.class_array);
			return payment[_paymenttype].toLowerCase();
		}
	}

	public ArrayList<String> getAbonentArray(){
        ArrayList<String> array = new ArrayList<String>();
        if (nickname != null) {
        array.add(_context.getString(R.string.abonent) + " " + nickname);
        if(quantity != null)
        array.add(_context.getString(R.string.rides) + " " + quantity);
    	}
		return array;
	}
	
	public String getPayment() {
		Resources res = _context.getResources();
		String[] payment = res.getStringArray(R.array.payment_array);
		return payment[_paymenttype];
	}

	public Date getTimerDate() {
		return _timerDate;
	}

	public ArrayList<String> toArrayList() {
		return null;
	}

	public String getAbonent() {
		return nickname;
	}

	public void setAbonent(String abonent) {
		this.nickname = abonent;
	}

	public Integer getRides() {
		return quantity;
	}

	public void setRides(Integer rides) {
		this.quantity = rides;
	}
}
