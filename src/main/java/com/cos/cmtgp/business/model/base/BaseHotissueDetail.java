package com.cos.cmtgp.business.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseHotissueDetail<M extends BaseHotissueDetail<M>> extends Model<M> implements IBean {

	public M setDId(java.lang.Integer dId) {
		set("d_id", dId);
		return (M)this;
	}
	
	public java.lang.Integer getDId() {
		return getInt("d_id");
	}

	public M setHId(java.lang.Integer hId) {
		set("h_id", hId);
		return (M)this;
	}
	
	public java.lang.Integer getHId() {
		return getInt("h_id");
	}

	public M setUId(java.lang.Integer uId) {
		set("u_id", uId);
		return (M)this;
	}
	
	public java.lang.Integer getUId() {
		return getInt("u_id");
	}

	public M setDEvaluateContext(java.lang.String dEvaluateContext) {
		set("d_evaluate_context", dEvaluateContext);
		return (M)this;
	}
	
	public java.lang.String getDEvaluateContext() {
		return getStr("d_evaluate_context");
	}

	public M setDDatetime(java.util.Date dDatetime) {
		set("d_datetime", dDatetime);
		return (M)this;
	}
	
	public java.util.Date getDDatetime() {
		return get("d_datetime");
	}

}
