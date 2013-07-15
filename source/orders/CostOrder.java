/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package orders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import ru.peppers.R;

import model.DateUtils;
import model.Order;
import android.content.Context;

/**
 * 
 * @author papas
 */
public class CostOrder extends Order {

	private Date _departuretime;

	public CostOrder(Context context, String index, Integer nominalcost,
			String addressdeparture, Integer carClass, String comment, String addressarrival, Integer paymenttype,
			Date departuretime) {
		super(context, nominalcost, addressdeparture, carClass, comment, addressarrival, paymenttype, index);
		_departuretime = departuretime;
	}

	public String toString() {
		String pred = "";
		if (_departuretime != null)
			pred = "П " + getTimeString(_departuretime) + ", ";

		String over = "";
		if (get_nominalcost() != null)
			over = ", " + get_nominalcost() + " " + _context.getString(R.string.currency);
		return pred + _addressdeparture + over;
	}

	public ArrayList<String> toArrayList() {
		ArrayList<String> array = new ArrayList<String>();
		array.addAll(getAbonentArray());
		if (_departuretime != null)
		array.add(_context.getString(R.string.date) + " " + getTimeString(_departuretime));

		array.add(_context.getString(R.string.adress) + " " + _addressdeparture);
		array.add(_context.getString(R.string.where) + " " + _addressarrival);

		array.add(_context.getString(R.string.car_class) + " " + getCarClass());
		array.add(_context.getString(R.string.cost_type) + " " + getPayment());
		if (get_nominalcost() != null)
			array.add(_context.getString(R.string.cost_ride) + " " + get_nominalcost() + " "
					+ _context.getString(R.string.currency));
		if (_comment != null)
			array.add(_context.getString(R.string.description) + " " + _comment);
		return array;
	}

	private String getTimeString(Date date) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
		if(DateUtils.isToday(date))
			return "Сегодня "+dateFormat.format(date);
		if(DateUtils.isWithinDaysFuture(date,1))
			return "Завтра "+dateFormat.format(date);
		return new SimpleDateFormat("MM-dd HH:mm").format(date);
	}
}
