/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
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
public class CostOrder extends Order {

    private String _costType;
    private int _cost;
    private Date _date;

    public CostOrder(Date date, String adress, String type, String orderText, String where,int cost,String costType) {
        super(adress, type, orderText, where);
        _date = date;
        _cost = cost;
        _costType = costType;
    }

    public String toString(){
        return getTimeString(_date)+", "+_adress+", "+_cost+" p";
    }

    public ArrayList<String> toArrayList(){
        ArrayList<String> array = new ArrayList<String>();
        array.add("Время заказа: "+getTimeString(_date));
        array.add("Адрес: "+_adress);
        array.add("Куда: "+_where);
        array.add("Класс: " + _carClass);
        array.add("Оплата: "+_costType);
        array.add(_orderText);
        array.add("Сумма: "+_cost+" р");
        return array;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }
}
