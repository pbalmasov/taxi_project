/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package myorders;


import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;

import model.Order;

/**
 *
 * @author papas
 */
public class MyCostOrder extends Order {

    private String _costType;
    private int _cost;
    private Date _date;
    private Date _dateInvite;
    private Date _dateAccept;

    public MyCostOrder(int costRide,int index,Date date, String adress, String type, String orderText, String where,int cost,String costType,Date dateInvite,Date dateAccept) {
        super(costRide,adress, type, orderText, where,index);
        _date = date;
        _dateInvite = dateInvite;
        _dateAccept = dateAccept;
        _cost = cost;
        _costType = costType;
    }

    public String toString(){
        return getTimeString(_dateAccept)+", "+_adress+", "+_cost+" p";
    }

    public ArrayList<String> toArrayList(){
        ArrayList<String> array = new ArrayList<String>();
        if (abonent != null) {
            array.add("Абонент: " + abonent);
            array.add("Кол-во поездок: " + rides);
        }
        array.add("Заказ принят: "+getTimeString(_dateAccept));
        array.add("Время подачи: "+getTimeString(_date));
        array.add("Время приглашения: "+getTimeString(_dateInvite));
        array.add("Адрес: "+_adress);
        array.add("Куда: "+_where);
        array.add("Класс: " + _carClass);
        array.add("Оплата: "+_costType);
        array.add(_orderText);
        array.add("Сумма: "+_cost+" р");
        array.add("Стоимость поездки: " + _costRide+" р");
        return array;
    }

    private String getTimeString(Date date) {
        SimpleDateFormat dateFormat = new SimpleDateFormat("HH:mm");
        return dateFormat.format(date);
    }
}
