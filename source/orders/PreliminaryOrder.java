/*
 * To change this template, choose Tools | Templates and open the template in the editor.
 */
package orders;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.Order;

/**
 *
 * @author papas
 */
public class PreliminaryOrder extends Order {

    private Date _date;

    public PreliminaryOrder(int costRide,int index,Date date, String adress, String type, String orderText, String where) {
        super(costRide,adress, type, orderText, where,index);
        _date = date;
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        if (abonent != null) {
            array.add("Абонент: " + abonent);
            array.add("Кол-во поездок: " + rides);
        }
        array.add("Предварительный заказ");
        array.add("Время подачи: " + getTimeString(_date));
        array.add("Адрес: " + _adress);
        array.add("Куда: " + _where);
        array.add("Класс: " + _carClass);
        array.add("Стоимость поездки: " + _costRide+" р");
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
