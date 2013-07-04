/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model;

import java.util.ArrayList;
import java.util.Date;

import android.app.Activity;
import android.content.Context;

/**
 * 
 * @author papas
 */
public class Order implements OrderInterface {

	protected String _addressdeparture;
	protected Integer _carClass;
	protected String _comment;
	protected String _addressarrival;
	protected Integer _index;
	protected Integer _nominalcost;
	protected Date _timerDate;
	protected Integer quantity;
	protected String nickname;
	protected Context context;

	public Order(Context context, Integer costRide, String adress, Integer carClass, String orderText, String where,
			Integer index) {
		this.context = context;
		_addressdeparture = adress;
		_carClass = carClass;
		_comment = orderText;
		_addressarrival = where;
		_index = index;
		_nominalcost = costRide;
	}

	public int get_index() {
		return _index;
	}

	public void setTimerDate(Date date) {
		_timerDate = date;
	}

	public String getCarClass() {
		if (_carClass == 0)
			return "все равно";
		if (_carClass == 1)
			return "Эконом";
		if (_carClass == 2)
			return "Стандарт";
		if (_carClass == 3)
			return "Базовый";
		return null;
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

	public void setRides(int rides) {
		this.quantity = rides;
	}
}
