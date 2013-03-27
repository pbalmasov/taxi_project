/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myorders;

import hello.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author papas
 */
public class MyNoCostOrder extends Order {

    private Date _date;
    private Date _dateAccept;

    public MyNoCostOrder(Date date, String adress, String type, String orderText, String where,Date dateAccept) {
        super(adress, type, orderText, where);
        _date = date;
        _dateAccept = dateAccept;
    }

    public ArrayList<String> toArrayList(){
        ArrayList<String> array = new ArrayList<String>();
        array.add("Заказ принят: " + getTimeString(_dateAccept));
        array.add("Время подачи: " + getTimeString(_date));
        array.add("Адрес: " + _adress);
        array.add("Куда: " + _where);
        array.add("Класс: " + _carClass);
        array.add(_orderText);
        return array;
    }


    public String toString(){
        return getTimeString(_dateAccept)+", "+_adress;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }
}
