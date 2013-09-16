/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package model;

import android.content.Context;
import android.content.res.Resources;

import java.util.ArrayList;
import java.util.Date;

import ru.peppers.R;

/**
 * @author papas
 */
public class Order implements OrderInterface {

    protected String _addressdeparture;
    protected Integer _carClass;
    private String _comment;
    private String _addressarrival;
    protected String _index;
    private String _nominalcost;
    protected Date _timerDate;
    protected Integer quantity;
    protected String nickname;
    private Integer _paymenttype;
    protected Context _context;

    public Order(Context context, String nominalcost, String adress, Integer carClass, String orderText, String where,
                 Integer paymenttype, String index) {
        _context = context;
        set_paymenttype(paymenttype);
        _addressdeparture = adress;
        _carClass = carClass;
        set_comment(orderText);
        set_addressarrival(where);
        _index = index;
        set_nominalcost(nominalcost);
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
            return payment[get_paymenttype()].toLowerCase();
        }
    }

    public ArrayList<String> getAbonentArray() {
        ArrayList<String> array = new ArrayList<String>();
        if (nickname != null)
            array.add(_context.getString(R.string.abonent) + " " + nickname);
        if (quantity != null)
            array.add(_context.getString(R.string.rides) + " " + quantity);
        return array;
    }

    public String getPayment() {
        Resources res = _context.getResources();
        String[] payment = res.getStringArray(R.array.payment_array);
        return payment[get_paymenttype()];
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


    public Integer get_paymenttype() {
        return _paymenttype;
    }

    public void set_paymenttype(Integer _paymenttype) {
        this._paymenttype = _paymenttype;
    }

    public String get_addressarrival() {
        return _addressarrival;
    }

    public void set_addressarrival(String _addressarrival) {
        this._addressarrival = _addressarrival;
    }

    public String get_nominalcost() {
        return _nominalcost;
    }

    public void set_nominalcost(String _nominalcost) {
        this._nominalcost = _nominalcost;
    }

    public String get_comment() {
        return _comment;
    }

    public void set_comment(String _comment) {
        this._comment = _comment;
    }
}
