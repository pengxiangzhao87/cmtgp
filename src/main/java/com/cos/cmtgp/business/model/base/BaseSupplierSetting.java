package com.cos.cmtgp.business.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseSupplierSetting<M extends BaseSupplierSetting<M>> extends Model<M> implements IBean {

	public M setSId(java.lang.Integer sId) {
		set("s_id", sId);
		return (M)this;
	}
	
	public java.lang.Integer getSId() {
		return getInt("s_id");
	}

	public M setSRegionCode(java.lang.String sRegionCode) {
		set("s_region_code", sRegionCode);
		return (M)this;
	}
	
	public java.lang.String getSRegionCode() {
		return getStr("s_region_code");
	}

	public M setSCorporateName(java.lang.String sCorporateName) {
		set("s_corporate_name", sCorporateName);
		return (M)this;
	}
	
	public java.lang.String getSCorporateName() {
		return getStr("s_corporate_name");
	}

	public M setBgImg(java.lang.String bgImg) {
		set("bg_img", bgImg);
		return (M)this;
	}
	
	public java.lang.String getBgImg() {
		return getStr("bg_img");
	}

	public M setSContactsName(java.lang.String sContactsName) {
		set("s_contacts_name", sContactsName);
		return (M)this;
	}
	
	public java.lang.String getSContactsName() {
		return getStr("s_contacts_name");
	}

	public M setSPhone(java.lang.String sPhone) {
		set("s_phone", sPhone);
		return (M)this;
	}
	
	public java.lang.String getSPhone() {
		return getStr("s_phone");
	}

	public M setSQualificationsCode(java.lang.String sQualificationsCode) {
		set("s_Qualifications_code", sQualificationsCode);
		return (M)this;
	}
	
	public java.lang.String getSQualificationsCode() {
		return getStr("s_Qualifications_code");
	}

	public M setSAddress(java.lang.String sAddress) {
		set("s_address", sAddress);
		return (M)this;
	}
	
	public java.lang.String getSAddress() {
		return getStr("s_address");
	}

	public M setSIdNumber(java.lang.String sIdNumber) {
		set("s_id_number", sIdNumber);
		return (M)this;
	}
	
	public java.lang.String getSIdNumber() {
		return getStr("s_id_number");
	}

	public M setSAccount(java.lang.String sAccount) {
		set("s_account", sAccount);
		return (M)this;
	}
	
	public java.lang.String getSAccount() {
		return getStr("s_account");
	}

	public M setSPassword(java.lang.String sPassword) {
		set("s_password", sPassword);
		return (M)this;
	}
	
	public java.lang.String getSPassword() {
		return getStr("s_password");
	}

	public M setSOpenid(java.lang.String sOpenid) {
		set("s_openid", sOpenid);
		return (M)this;
	}
	
	public java.lang.String getSOpenid() {
		return getStr("s_openid");
	}

	public M setLatitude(java.lang.String latitude) {
		set("latitude", latitude);
		return (M)this;
	}
	
	public java.lang.String getLatitude() {
		return getStr("latitude");
	}

	public M setLongitude(java.lang.String longitude) {
		set("longitude", longitude);
		return (M)this;
	}
	
	public java.lang.String getLongitude() {
		return getStr("longitude");
	}

	public M setShortStart(java.math.BigDecimal shortStart) {
		set("short_start", shortStart);
		return (M)this;
	}
	
	public java.math.BigDecimal getShortStart() {
		return get("short_start");
	}

	public M setShortDeal(java.math.BigDecimal shortDeal) {
		set("short_deal", shortDeal);
		return (M)this;
	}
	
	public java.math.BigDecimal getShortDeal() {
		return get("short_deal");
	}

	public M setShortFree(java.math.BigDecimal shortFree) {
		set("short_free", shortFree);
		return (M)this;
	}
	
	public java.math.BigDecimal getShortFree() {
		return get("short_free");
	}

	public M setCityStart(java.math.BigDecimal cityStart) {
		set("city_start", cityStart);
		return (M)this;
	}
	
	public java.math.BigDecimal getCityStart() {
		return get("city_start");
	}

	public M setCityDeal(java.math.BigDecimal cityDeal) {
		set("city_deal", cityDeal);
		return (M)this;
	}
	
	public java.math.BigDecimal getCityDeal() {
		return get("city_deal");
	}

	public M setCityFree(java.math.BigDecimal cityFree) {
		set("city_free", cityFree);
		return (M)this;
	}
	
	public java.math.BigDecimal getCityFree() {
		return get("city_free");
	}

	public M setLongStart(java.math.BigDecimal longStart) {
		set("long_start", longStart);
		return (M)this;
	}
	
	public java.math.BigDecimal getLongStart() {
		return get("long_start");
	}

	public M setLongDeal(java.math.BigDecimal longDeal) {
		set("long_deal", longDeal);
		return (M)this;
	}
	
	public java.math.BigDecimal getLongDeal() {
		return get("long_deal");
	}

	public M setLongFree(java.math.BigDecimal longFree) {
		set("long_free", longFree);
		return (M)this;
	}
	
	public java.math.BigDecimal getLongFree() {
		return get("long_free");
	}

}
