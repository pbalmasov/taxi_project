/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package orders;

import android.content.Context;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.Order;
import ru.peppers.R;

/**
 * @author papas
 */
public class ReportCostOrder extends Order {

    private Date _orderDate;
    private String _result;
    private String _drivercost;
    private String _actualcost;
    private Date _accepttime;

    public ReportCostOrder(Context context, String index, Integer nominalcost, String addressdeparture,
            Integer carClass, String comment, String addressarrival, Integer paymenttype, Date orderDate,
            String result, String drivercost, String actualcost, Date accepttime) {
        super(context, nominalcost, addressdeparture, carClass, comment, addressarrival, paymenttype, index);
        _orderDate = orderDate;
        _result = result;
        _drivercost = drivercost;
        _actualcost = actualcost;
        _accepttime = accepttime;

    }

    public String toString() {
        String pred = "";
        if (_orderDate != null)
            pred = "П " + getTimeString(_orderDate) + ", ";

        String over = "";
        if (get_nominalcost() != null)
            over = ", " + get_nominalcost() + " " + _context.getString(R.string.currency);
        return pred + _addressdeparture + over;
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        array.addAll(getAbonentArray());

        String departureTimeValue = "не указано";
        if (_orderDate != null)
            departureTimeValue = getTimeString(_orderDate);
        array.add(_context.getString(R.string.date) + " " + departureTimeValue);

        array.add(_context.getString(R.string.adress) + " " + _addressdeparture);
        array.add(_context.getString(R.string.where) + " " + get_addressarrival());

        array.add(_context.getString(R.string.car_class) + " " + getCarClass());
        array.add(_context.getString(R.string.cost_type) + " " + getPayment());

        if (_drivercost != null)
            array.add("Стоимость закрытия:" + " " + _drivercost);
        if (_actualcost != null)
            array.add("Оплата диспетчерской:" + " " + _actualcost);
        if (_accepttime != null)
            array.add("Время приглашения:" + " " + getTimeString(_accepttime));
        array.add("Результат:" + " " + _result);

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
        return new SimpleDateFormat("dd.MM HH:mm").format(date);
    }
}
