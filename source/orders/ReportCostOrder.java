/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package orders;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.Order;
import ru.peppers.R;
import android.content.Context;

/**
 * Заказ в отчет
 * @author p.balmasov
 */
public class ReportCostOrder extends Order {

    private Date _orderDate;
    private String _result;
    private String _drivercost;
    private String _actualcost;
    private Date _accepttime;

    public ReportCostOrder(Context context, String index, String nominalcost, String addressdeparture,
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
            over = ", " + _actualcost + " " + _context.getString(R.string.currency);
        return pred + _addressdeparture + over;
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        array.addAll(getAbonentArray());

        String departureTimeValue = _context.getString(R.string.no_data);
        if (_orderDate != null)
            departureTimeValue = getTimeString(_orderDate);

        array.add(_context.getString(R.string.date) + " " + departureTimeValue);

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
            array.add(_context.getString(R.string.car_class) + " не указано");

        if (getPayment() != null)
            array.add(_context.getString(R.string.cost_type) + " " + getPayment());
        else
            array.add(_context.getString(R.string.cost_type) + " не указано");

        if (_accepttime != null)
            array.add(_context.getString(R.string.date_invite) + " " + getTimeString(_accepttime));
        else
            array.add(_context.getString(R.string.date_invite) + " не указано");

        if (_result != null)
            array.add(_context.getString(R.string.result) + " " + _result);
        else
            array.add(_context.getString(R.string.result)  + " не указано");

        String costValue = "не указано";
        if (get_nominalcost() != null)
            costValue = String.valueOf(get_nominalcost()) + " " + _context.getString(R.string.currency);

        if (_drivercost != null)
            array.add(_context.getString(R.string.close_cost) + " " + _actualcost + " "
                    + _context.getString(R.string.currency));
        if (_actualcost != null)
            array.add(_context.getString(R.string.cost_disp) + " " + _drivercost + " "
                    + _context.getString(R.string.currency));

        array.add(_context.getString(R.string.ras_cost) + " " + costValue);

        if (get_comment() == null)
            set_comment(_context.getString(R.string.no_data));
        array.add(_context.getString(R.string.description) + " " + get_comment());
        return array;
    }

    private String getTimeString(Date date) {
        return new SimpleDateFormat("dd.MM HH:mm").format(date);
    }
}
