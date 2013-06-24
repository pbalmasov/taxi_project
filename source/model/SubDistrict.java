/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 *
 * @author papas
 */
public class SubDistrict {
    private int _drivers;
    private int _orders;
    private String _subDistrictName;

    public SubDistrict(int drivers, int orders, String subDistrictName) {
        _drivers = drivers;
        _orders = orders;
        _subDistrictName = subDistrictName;
    }

    public String toString(){
        return "     - "+_subDistrictName+"("+_drivers+"/"+_orders+")";
    }

    /**
     * @return the _drivers
     */
    public int getDrivers() {
        return _drivers;
    }

    /**
     * @param drivers the _drivers to set
     */
    public void setDrivers(int drivers) {
        this._drivers = drivers;
    }

    /**
     * @return the _orders
     */
    public int getOrders() {
        return _orders;
    }

    /**
     * @param orders the _orders to set
     */
    public void setOrders(int orders) {
        this._orders = orders;
    }

    /**
     * @return the _subDistrictName
     */
    public String getSubDistrictName() {
        return _subDistrictName;
    }

    /**
     * @param subDistrictName the _subDistrictName to set
     */
    public void setSubDistrictName(String subDistrictName) {
        this._subDistrictName = subDistrictName;
    }
}
