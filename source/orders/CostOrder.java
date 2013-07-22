/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package orders;

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
public class CostOrder extends Order {

    private Date _departuretime;

    public CostOrder(Context context, String index, Integer nominalcost,
                     String addressdeparture, Integer carClass, String comment, String addressarrival, Integer paymenttype,
                     Date departuretime) {
        super(context, nominalcost, addressdeparture, carClass, comment, addressarrival, paymenttype, index);
        set_departuretime(departuretime);
    }

    public String toString() {
        String pred = "";
        if (get_departuretime() != null)
            pred = "П " + getTimeString(get_departuretime()) + ", ";

        String over = "";
        if (get_nominalcost() != null)
            over = ", " + get_nominalcost() + " " + _context.getString(R.string.currency);
        return pred + _addressdeparture + over;
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        array.addAll(getAbonentArray());

        String departureTimeValue = "не указано";
        if (get_departuretime() != null)
            departureTimeValue = getTimeString(get_departuretime());
        array.add(_context.getString(R.string.date) + " " + departureTimeValue);

        array.add(_context.getString(R.string.adress) + " " + _addressdeparture);
        array.add(_context.getString(R.string.where) + " " + get_addressarrival());

        array.add(_context.getString(R.string.car_class) + " " + getCarClass());
        array.add(_context.getString(R.string.cost_type) + " " + getPayment());

        String costValue = "не указано";
        if (get_nominalcost() != null)
            costValue = String.valueOf(get_nominalcost()) + " " + _context.getString(R.string.currency);

        array.add(_context.getString(R.string.cost_ride) + " " + costValue);

        if (_comment == null)
            _comment = "не указано";
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

    public Date get_departuretime() {
        return _departuretime;
    }

    public void set_departuretime(Date _departuretime) {
        this._departuretime = _departuretime;
    }
}
