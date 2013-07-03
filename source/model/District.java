/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package model;

import java.util.ArrayList;

/**
 *
 * @author papas
 */
public class District {

	private int _drivers;
	private int _orders;
	private String _districtName;
	private ArrayList<SubDistrict>  _subdistrics;
	private String _districtId;

	public District(int drivers, int orders, String districtName, String districtId,ArrayList<SubDistrict> subdistrict) {
		_districtId = districtId;
		_drivers = drivers;
		_orders = orders;
		_districtName = districtName;
		_subdistrics = subdistrict;
	}
	public String toString(){
		return "- "+_districtName+"("+_drivers+"/"+_orders+")";
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
	 * @return the _districtName
	 */
	public String getDistrictName() {
		return _districtName;
	}

	/**
	 * @param districtName the _districtName to set
	 */
	public void setDistrictName(String districtName) {
		this._districtName = districtName;
	}

	/**
	 * @return the _subdistrics
	 */
	public ArrayList<SubDistrict> getSubdistrics() {
		return _subdistrics;
	}

	/**
	 * @param subdistrics the _subdistrics to set
	 */
	public void setSubdistrics(ArrayList<SubDistrict> subdistrics) {
		this._subdistrics = subdistrics;
	}
	public String getDistrictId() {
		return _districtId;
	}
}
