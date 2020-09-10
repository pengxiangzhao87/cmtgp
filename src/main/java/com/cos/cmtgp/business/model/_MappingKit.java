package com.cos.cmtgp.business.model;

import com.jfinal.plugin.activerecord.ActiveRecordPlugin;

/**
 * Generated by JFinal, do not modify this file.
 * <pre>
 * Example:
 * public void configPlugin(Plugins me) {
 *     ActiveRecordPlugin arp = new ActiveRecordPlugin(...);
 *     _MappingKit.mapping(arp);
 *     me.add(arp);
 * }
 * </pre>
 */
public class _MappingKit {
	
	public static void mapping(ActiveRecordPlugin arp) {
		arp.addMapping("t_address_info", "a_id", AddressInfo.class);
		arp.addMapping("t_commodity_info", "s_id", CommodityInfo.class);
		arp.addMapping("t_commodity_type_setting", "t_id", CommodityTypeSetting.class);
		arp.addMapping("t_customer_service", "id", CustomerService.class);
		arp.addMapping("t_feedback_info", "id", FeedbackInfo.class);
		arp.addMapping("t_global_conf", "c_id", GlobalConf.class);
		arp.addMapping("t_hotissue_basic", "h_id", HotissueBasic.class);
		arp.addMapping("t_hotissue_detail", "d_id", HotissueDetail.class);
		arp.addMapping("t_hotissue_liked", "id", HotissueLiked.class);
		arp.addMapping("t_hotissue_reply", "r_id", HotissueReply.class);
		arp.addMapping("t_interface_info", "id", InterfaceInfo.class);
		arp.addMapping("t_order_basic", "o_id", OrderBasic.class);
		arp.addMapping("t_order_detail", "id", OrderDetail.class);
		arp.addMapping("t_shopping_info", "id", ShoppingInfo.class);
		arp.addMapping("t_supplier_setting", "s_id", SupplierSetting.class);
		arp.addMapping("t_sys_log", "id", SysLog.class);
		arp.addMapping("t_user_relation", "id", UserRelation.class);
		arp.addMapping("t_user_setting", "u_id", UserSetting.class);
		arp.addMapping("t_wx_cash_info", "id", WxCashInfo.class);
	}
}

