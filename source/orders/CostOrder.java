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
public class CostOrder extends Order {

    private String _costType;
    private int _cost;
    private Date _date;

    public CostOrder(int costRide, int index, Date date, String adress, String type, String orderText,
            String where, int cost, String costType) {
        super(costRide, adress, type, orderText, where, index);
        _date = date;
        _cost = cost;
        _costType = costType;

    }

    public String toString() {
        return getTimeString(_date) + ", " + _adress + ", " + _cost + " p";
    }

    public ArrayList<String> toArrayList() {
        ArrayList<String> array = new ArrayList<String>();
        if (abonent != null) {
            array.add("Абонент: " + abonent);
            array.add("Кол-во поездок: " + rides);
        }
        array.add("Время заказа: " + getTimeString(_date));
        array.add("Адрес: " + _adress);
        array.add("Куда: " + _where);
        array.add("Класс: " + _carClass);
        array.add("Оплата: " + _costType);
        array.add(_orderText);
        array.add("Сумма: " + _cost + " р");
        array.add("Стоимость поездки: " + _costRide + " р");
        return array;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }
}
