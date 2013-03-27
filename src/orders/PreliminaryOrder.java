/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package orders;

import hello.Order;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

/**
 *
 * @author papas
 */
public class PreliminaryOrder extends Order {

    private Date _date;

    public PreliminaryOrder(Date date, String adress, String type, String orderText, String where) {
        super(adress, type, orderText, where);
        _date = date;
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        array.add("Предварительный заказ");
        array.add("Время подачи: " + getTimeString(_date));
        array.add("Адрес: " + _adress);
        array.add("Куда: " + _where);
        array.add("Класс: " + _carClass);
        array.add(_orderText);
        return array;
    }

    public String toString() {

        return getTimeString(_date) + ", предварительный, " + _adress;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM HH:mm");
        return dateFormat.format(date);
    }
}
