package com.cos.cmtgp.business.model.base;

import com.jfinal.plugin.activerecord.Model;
import com.jfinal.plugin.activerecord.IBean;

/**
 * Generated by JFinal, do not modify this file.
 */
@SuppressWarnings({"serial", "unchecked"})
public abstract class BaseInterfaceInfo<M extends BaseInterfaceInfo<M>> extends Model<M> implements IBean {

	public M setId(java.lang.Integer id) {
		set("id", id);
		return (M)this;
	}
	
	public java.lang.Integer getId() {
		return getInt("id");
	}

	public M setInterfaceName(java.lang.String interfaceName) {
		set("interface_name", interfaceName);
		return (M)this;
	}
	
	public java.lang.String getInterfaceName() {
		return getStr("interface_name");
	}

	public M setInterfaceDesc(java.lang.String interfaceDesc) {
		set("interface_desc", interfaceDesc);
		return (M)this;
	}
	
	public java.lang.String getInterfaceDesc() {
		return getStr("interface_desc");
	}

	public M setExecuteSql(java.lang.String executeSql) {
		set("execute_sql", executeSql);
		return (M)this;
	}
	
	public java.lang.String getExecuteSql() {
		return getStr("execute_sql");
	}

	public M setReturnField(java.lang.String returnField) {
		set("return_field", returnField);
		return (M)this;
	}
	
	public java.lang.String getReturnField() {
		return getStr("return_field");
	}

	public M setInterfaceParameter(java.lang.String interfaceParameter) {
		set("interface_parameter", interfaceParameter);
		return (M)this;
	}
	
	public java.lang.String getInterfaceParameter() {
		return getStr("interface_parameter");
	}

}
