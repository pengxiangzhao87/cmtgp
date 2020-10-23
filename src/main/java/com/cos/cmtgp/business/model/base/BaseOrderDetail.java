package com.cos.cmtgp.business.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseOrderDetail<M extends BaseOrderDetail<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public M setOId(java.lang.Integer oId) {
		set("o_id", oId);
		return (M)this;
	}
	
	public java.lang.Integer getOId() {
		return getInt("o_id");
	}

	public M setSId(java.lang.Integer sId) {
		set("s_id", sId);
		return (M)this;
	}
	
	public java.lang.Integer getSId() {
		return getInt("s_id");
	}

	public M setPaymentPrice(java.math.BigDecimal paymentPrice) {
		set("payment_price", paymentPrice);
		return (M)this;
	}
	
	public java.math.BigDecimal getPaymentPrice() {
		return get("payment_price");
	}

	public M setOrderNum(java.lang.Integer orderNum) {
		set("order_num", orderNum);
		return (M)this;
	}
	
	public java.lang.Integer getOrderNum() {
		return getInt("order_num");
	}

	public M setChargebackStatus(java.lang.Integer chargebackStatus) {
		set("chargeback_status", chargebackStatus);
		return (M)this;
	}
	
	public java.lang.Integer getChargebackStatus() {
		return getInt("chargeback_status");
	}

	public M setOutRefundNo(java.lang.String outRefundNo) {
		set("out_refund_no", outRefundNo);
		return (M)this;
	}
	
	public java.lang.String getOutRefundNo() {
		return getStr("out_refund_no");
	}

	public M setRefundId(java.lang.String refundId) {
		set("refund_id", refundId);
		return (M)this;
	}
	
	public java.lang.String getRefundId() {
		return getStr("refund_id");
	}

	public M setIsExtra(java.lang.Integer isExtra) {
		set("is_extra", isExtra);
		return (M)this;
	}
	
	public java.lang.Integer getIsExtra() {
		return getInt("is_extra");
	}

	public M setExtraWeight(java.lang.Double extraWeight) {
		set("extra_weight", extraWeight);
		return (M)this;
	}
	
	public java.lang.Double getExtraWeight() {
		return getDouble("extra_weight");
	}

	public M setExtraPrice(java.math.BigDecimal extraPrice) {
		set("extra_price", extraPrice);
		return (M)this;
	}
	
	public java.math.BigDecimal getExtraPrice() {
		return get("extra_price");
	}

	public M setExtraImgUrl(java.lang.String extraImgUrl) {
		set("extra_img_url", extraImgUrl);
		return (M)this;
	}
	
	public java.lang.String getExtraImgUrl() {
		return getStr("extra_img_url");
	}

	public M setExtraPayStatus(java.lang.Integer extraPayStatus) {
		set("extra_pay_status", extraPayStatus);
		return (M)this;
	}
	
	public java.lang.Integer getExtraPayStatus() {
		return getInt("extra_pay_status");
	}

	public M setExtraPayBackStatus(java.lang.Integer extraPayBackStatus) {
		set("extra_pay_back_status", extraPayBackStatus);
		return (M)this;
	}
	
	public java.lang.Integer getExtraPayBackStatus() {
		return getInt("extra_pay_back_status");
	}

	public M setExtraRefundId(java.lang.String extraRefundId) {
		set("extra_refund_id", extraRefundId);
		return (M)this;
	}
	
	public java.lang.String getExtraRefundId() {
		return getStr("extra_refund_id");
	}

	public M setExtraOutRefundNo(java.lang.String extraOutRefundNo) {
		set("extra_out_refund_no", extraOutRefundNo);
		return (M)this;
	}
	
	public java.lang.String getExtraOutRefundNo() {
		return getStr("extra_out_refund_no");
	}

	public M setExtraBackStatus(java.lang.Integer extraBackStatus) {
		set("extra_back_status", extraBackStatus);
		return (M)this;
	}
	
	public java.lang.Integer getExtraBackStatus() {
		return getInt("extra_back_status");
	}

}
