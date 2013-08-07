/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myorders;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.DateUtils;
import model.Order;
import ru.peppers.R;

/**
 * @author papas
 */
public class MyCostOrder extends Order {

    private Date _invitationtime;
    private Date _departuretime;
    private Date _accepttime;
    private Integer _driverstate;

    public MyCostOrder(Context context, String index, Integer nominalcost,
                       String addressdeparture, Integer carClass, String comment, String addressarrival, Integer paymenttype,
                       Date invitationtime, Date departuretime, Date accepttime, Integer driverstate) {
        super(context, nominalcost, addressdeparture, carClass, comment, addressarrival, paymenttype, index);
        // TODO:wrong index
        set_invitationtime(invitationtime);
        _departuretime = departuretime;
        _accepttime = accepttime;
        _driverstate = driverstate;
        
    }

    public String toString() {
        String pred = "";
        if (_departuretime != null)
            pred = getTimeString(_departuretime) + ", ";
//		else
//			pred = getTimeString(_registrationtime) + ", ";

        String over = "";
        if (get_nominalcost() != null)
            over = ", " + get_nominalcost() + " " + _context.getString(R.string.currency);

        return /*"К " +*/ pred + _addressdeparture + over;
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        array.addAll(getAbonentArray());

        //String departureTimeValue = "не указано";
        if (_departuretime != null) {
            array.add(_context.getString(R.string.date) + " " + getTimeString(_departuretime));
        }
        if (_invitationtime != null)
            array.add(_context.getString(R.string.date_invite) + " " + getTimeString(_invitationtime));
        else
            array.add(_context.getString(R.string.date_invite) + " " + "не приглашены");


        array.add(_context.getString(R.string.adress) + " " + _addressdeparture);
        array.add(_context.getString(R.string.where) + " " + get_addressarrival());

        array.add(_context.getString(R.string.car_class) + " " + getCarClass());
        array.add(_context.getString(R.string.cost_type) + " " + getPayment());

        if (get_nominalcost() != null)
            array.add(_context.getString(R.string.cost_ride) + " " + get_nominalcost() + " " + _context.getString(R.string.currency));

        if (_accepttime != null)
            array.add("Время приглашения:" + " " + getTimeString(_accepttime));

        if (_comment != null)
            array.add(_context.getString(R.string.description) + " " + _comment);
        return array;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        if (DateUtils.isToday(date))
            return "Сегодня " + dateFormat.format(date);
        if (DateUtils.isWithinDaysFuture(date, 1))
            return "Завтра " + dateFormat.format(date);
        return new SimpleDateFormat("dd.MM HH:mm").format(date);
    }

    public Date get_invitationtime() {
        return _invitationtime;
    }

    public void set_invitationtime(Date _invitationtime) {
        this._invitationtime = _invitationtime;
    }

	public Integer get_driverstate() {
		return _driverstate;
	}

}
