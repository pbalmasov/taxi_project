/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

/**
 * Модель подрайона
 * @author p.balmasov
 */
public class SubDistrict {
    private int _drivers;
    private String _subDistrictName;
    private String _subDistrictId;

    public SubDistrict(int drivers, String subDistrictName, String subDistrictId) {
        _drivers = drivers;
        _subDistrictName = subDistrictName;
		_subDistrictId = subDistrictId;
    }

    public String toString(){
        return _subDistrictName+" "+_drivers;
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

	public String get_subDistrictId() {
		return _subDistrictId;
	}

}
