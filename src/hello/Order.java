/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package hello;

import java.util.ArrayList;

/**
 *
 * @author papas
 */
public class Order implements OrderInterface {

    protected String _adress;
    protected String _carClass;
    protected String _orderText;
    protected String _where;

    public Order(String adress, String carClass, String orderText, String where) {
        _adress = adress;
        _carClass = carClass;
        _orderText = orderText;
        _where = where;
    }

    public ArrayList<String> toArrayList(){
        return null;
    }
}
