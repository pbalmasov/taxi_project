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
 * Заказ
 * @author p.balmasov
 */
public class CostOrder extends Order {

    private Date _departuretime;

    public CostOrder(Context context, String index, String nominalcost, String addressdeparture,
            Integer carClass, String comment, String addressarrival, Integer paymenttype, Date departuretime) {
        super(context, nominalcost, addressdeparture, carClass, comment, addressarrival, paymenttype, index);
        set_departuretime(departuretime);
    }

    public String toString() {
        String pred = "";
        if (get_departuretime() != null)
            pred = "П " + getTimeString(get_departuretime()) + ", ";

        String over = "";
        if (get_nominalcost() != null)
            over = " = " + get_nominalcost() + " " + _context.getString(R.string.currency);
        String adressdeparture = _context.getString(R.string.no_data);
        String addressarrival = _context.getString(R.string.no_data);
        if (_addressdeparture != null)
            adressdeparture = _addressdeparture.trim();
        if (get_addressarrival() != null)
            addressarrival = get_addressarrival().trim();

        return pred + adressdeparture + " >> " + addressarrival + over;
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        array.addAll(getAbonentArray());

        if (get_departuretime() != null)
            array.add(_context.getString(R.string.date) + " " + getTimeString(get_departuretime()));

        if (_addressdeparture != null)
            array.add(_context.getString(R.string.adress) + " " + _addressdeparture);
        else
            array.add(_context.getString(R.string.adress) + " не указан");

        if (get_addressarrival() != null)
            array.add(_context.getString(R.string.where) + " " + get_addressarrival());
        else
            array.add(_context.getString(R.string.where) + " не указан");

        if (getCarClass() != null)
            array.add(_context.getString(R.string.car_class) + " " + getCarClass());
        else
            array.add(_context.getString(R.string.car_class) + " не указан");

        if (getPayment() != null)
            array.add(_context.getString(R.string.cost_type) + " " + getPayment());
        else
            array.add(_context.getString(R.string.cost_type) + " не указано");

        if (get_nominalcost() != null)
            array.add(_context.getString(R.string.cost_ride) + " " + get_nominalcost() + " "
                    + _context.getString(R.string.currency));
        else
            array.add(_context.getString(R.string.cost_ride) + " не указано");

        if (get_comment() != null)
            array.add(_context.getString(R.string.description) + " " + get_comment());
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
