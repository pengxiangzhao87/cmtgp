package com.cos.cmtgp.business.controller;

import java.io.File;
import java.math.BigDecimal;
import java.text.ParseException;
import java.util.*;
import cn.hutool.core.util.ArrayUtil;
import com.cos.cmtgp.business.dto.MiniTempDataDTO;
import com.cos.cmtgp.business.dto.OrderDTO;
import com.cos.cmtgp.business.model.ExpressInfo;
import com.cos.cmtgp.business.model.GlobalConf;
import com.cos.cmtgp.business.model.OrderBasic;
import com.cos.cmtgp.business.model.OrderDetail;
import com.cos.cmtgp.business.service.MiniService;
import com.cos.cmtgp.business.service.OrderService;
import com.cos.cmtgp.common.base.BaseController;
import com.cos.cmtgp.common.mini.SubscribeMessage;
import com.cos.cmtgp.common.mini.WXPayConstants;
import com.cos.cmtgp.common.mini.WXPayUtil;
import com.cos.cmtgp.common.util.*;
import com.jfinal.aop.Before;
import com.jfinal.json.FastJson;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.plugin.activerecord.tx.Tx;
import com.jfinal.plugin.ehcache.CacheKit;
import com.jfinal.upload.UploadFile;
import org.apache.log4j.LogManager;
import org.apache.log4j.Logger;

/**
 *	  订单接口
 */
public class OrderController extends BaseController {
	Logger logger = LogManager.getLogger(getClass());
	OrderService orderService = enhance(OrderService.class);
	MiniService miniService = enhance(MiniService.class);


	/**
	 * 商户
	 * APP
	 * 同意退单
	 */
	public void agreeRefundDetail(){
		Integer id = getParaToInt("id");
		Integer oId = getParaToInt("oId");
		String result = "";
		try{
			result = orderService.agreeRefund(id, oId);
		}catch(Exception ex){
			ex.printStackTrace();
			logger.error(ex);
			addOpLog("agreeRefundDetail ===> id="+id+",oId="+oId+",result="+result);
		}
		renderSuccess(result);
	}

	/**
	 * 商户
	 * APP
	 * 发送订单
	 */
	public void sendOrder(){
		Integer oId = getParaToInt("oId");
		try{
			List<Record> recordList = Db.find("select GROUP_CONCAT(b.chargeback_status) as status from t_order_basic a,t_order_detail b where a.o_id=b.o_id and a.o_id=" + oId);
			boolean flag = true;
			if(recordList.size()>0){
				if(recordList.get(0).getStr("status").contains("1")){
					flag = false;
				}
			}
			if(flag){
				Db.update("update t_order_basic set order_status=2 where o_id="+oId);
				renderSuccess();
			}else{
				renderSuccess("1");
			}
		}catch(Exception ex){}
	}

	/**
	 * 确认已送达
	 */
	public void confirmSend(){
		Integer oId = getParaToInt("oId");
		try {
			Db.update("update t_order_basic set order_status=3,last_time=now() where o_id="+oId);
			renderSuccess();
		}catch (Exception ex){
			addOpLog("confirmSend ===> oId="+oId);
		}
	}

	/**
	 * APP
	 * 商户返还差价
	 */
	public void toBackPrice(){
		Integer oId = getParaToInt("oId");
		String backPrice = getPara("backPrice");
		boolean stringObjectMap = false;
		try{
			stringObjectMap = orderService.toBackPrice(oId, backPrice);
		}catch(Exception ex){}
		if(stringObjectMap){
			renderSuccess();
		}else{
			addOpLog("toBackPrice ===> oId="+oId+", backPrice="+backPrice);
			renderFailed();
		}
	}

	/**
	 * APP
	 * 商户通知用户补差价
	 */
	public void sendPayPrice(){
		Integer oId = getParaToInt("oId");
		String payPrice = getPara("payPrice");
		String orderTime = getPara("orderTime");
		boolean flag = false;
		try {
			flag = orderService.sendPayPrice(oId, payPrice, orderTime);
		}catch (Exception ex){
			ex.printStackTrace();
		}
		if(flag){
			renderSuccess();
		}else{
			addOpLog("sendPayPrice ===> oId="+oId);
			renderFailed();
		}
	}

	/**
	 * APP
	 * 获取默认时间
	 */
	public void getRangeTime(){
		try {
			List<GlobalConf> confList = GlobalConf.dao.find("select * from t_global_conf where c_type in (0,1)");
			String beggin = "";
			String end = "";
			for(GlobalConf conf : confList){
				if(conf.getCType()==0){
					end = conf.getCTime();
				}else if(conf.getCType()==1){
					beggin = conf.getCTime();
				}
			}
			Calendar ca = Calendar.getInstance();
			String year = ca.get(Calendar.YEAR)+"";
			String month = ca.get(Calendar.MONTH) + 1 < 10 ? "0" + (ca.get(Calendar.MONTH) + 1) : ca.get(Calendar.MONTH) + 1+"";
			String day = ca.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + ca.get(Calendar.DAY_OF_MONTH) : ca.get(Calendar.DAY_OF_MONTH)+"";
			Date now = ca.getTime();
			long nowTime = now.getTime();
			Date begginDate = DateUtil.getStringToDate(year + "-" + month + "-" + day + " " + beggin);
			long begginTime = begginDate.getTime();
			long endTime = DateUtil.getStringToDate(year+"-"+month+"-"+day+" "+end).getTime();
			String range="";
			if(nowTime>endTime){
				range = "请选择时间";
			}else{
				if(nowTime<begginTime){
					now = begginDate;
				}
				Date rangeStart = this.getRangeStart(now, year + "-" + month + "-" + day);
				Date rangeEnd = new Date(rangeStart.getTime()+1000*60*30);
				ca.setTime(rangeStart);
				String startRange = "今天 "+ca.get(Calendar.HOUR_OF_DAY)+":"+(ca.get(Calendar.MINUTE)==0?"00":ca.get(Calendar.MINUTE));
				ca.setTime(rangeEnd);
				String endRange = " - "+ca.get(Calendar.HOUR_OF_DAY)+":"+(ca.get(Calendar.MINUTE)==0?"00":ca.get(Calendar.MINUTE));
				range = startRange + endRange;
			}
			renderSuccess("",range);
		} catch (ParseException e) {
			e.printStackTrace();
			renderFailed();
		}
	}


	public void  getRangeList(){
		try {
			List<GlobalConf> confList = GlobalConf.dao.find("select * from t_global_conf");
			String beggin = "";
			String end = "";
			for(GlobalConf conf : confList){
				if(conf.getCType()==0){
					end = conf.getCTime();
				}else if(conf.getCType()==1){
					beggin = conf.getCTime();
				}
			}
			Calendar ca = Calendar.getInstance();
			String year = ca.get(Calendar.YEAR)+"";
			String month = ca.get(Calendar.MONTH) + 1 < 10 ? "0" + (ca.get(Calendar.MONTH) + 1) : ca.get(Calendar.MONTH) + 1+"";
			String day = ca.get(Calendar.DAY_OF_MONTH) < 10 ? "0" + ca.get(Calendar.DAY_OF_MONTH) : ca.get(Calendar.DAY_OF_MONTH)+"";
			Date now = ca.getTime();
			long nowTime = now.getTime();
			Date begginDate = DateUtil.getStringToDate(year + "-" + month + "-" + day + " " + beggin);
			Date endDate = DateUtil.getStringToDate(year+"-"+month+"-"+day+" "+end);
			long endTime = endDate.getTime();

			List<Record> result = new ArrayList<Record>();
			if(nowTime<endTime){
				if(nowTime<begginDate.getTime()){
					now = begginDate;
				}
				List<String> todayList = new ArrayList<String>();
				Date rangeStart = this.getRangeStart(now, year + "-" + month + "-" + day);
				for(int i=0;i<1000;i++){
					ca.setTime(rangeStart);
					String startRange = ca.get(Calendar.HOUR_OF_DAY)+":"+(ca.get(Calendar.MINUTE)==0?"00":ca.get(Calendar.MINUTE));
					Date rangeEnd = new Date(rangeStart.getTime()+1000*60*30);
					ca.setTime(rangeEnd);
					String endRange = " - "+ca.get(Calendar.HOUR_OF_DAY)+":"+(ca.get(Calendar.MINUTE)==0?"00":ca.get(Calendar.MINUTE));
					todayList.add(startRange+endRange);
					if(ca.get(Calendar.HOUR_OF_DAY)>=21){
						break;
					}
					rangeStart = rangeEnd;
					++i;
				}
				Record today = new Record();
				today.set("content","今天");
				today.set("rangeList",todayList);
				result.add(today);
			}
			List<String> tommorrowList = new ArrayList<String>();

			for(int i=0;i<1000;i++){
				ca.setTime(begginDate);
				String startRange = ca.get(Calendar.HOUR_OF_DAY)+":"+(ca.get(Calendar.MINUTE)==0?"00":ca.get(Calendar.MINUTE));
				Date rangeEnd = new Date(begginDate.getTime()+1000*60*30);
				ca.setTime(rangeEnd);
				String endRange = " - "+ca.get(Calendar.HOUR_OF_DAY)+":"+(ca.get(Calendar.MINUTE)==0?"00":ca.get(Calendar.MINUTE));
				tommorrowList.add(startRange+endRange);
				if(ca.get(Calendar.HOUR_OF_DAY)>=21){
					break;
				}
				begginDate = rangeEnd;
				++i;
			}
			Record tommow = new Record();
			tommow.set("content","明天");
			tommow.set("rangeList",tommorrowList);
			result.add(tommow);
			renderSuccess("",result);
		} catch (ParseException e) {
			e.printStackTrace();
			renderFailed();
		}
	}

	private Date getRangeStart(Date now, String preDate){
		try {
			Calendar ca = Calendar.getInstance();
			long time = now.getTime() + 1000*60*15;
			Date afterDate = new Date(time);
			ca.setTime(afterDate);
			int afterHour = ca.get(Calendar.HOUR_OF_DAY);
			int afterMinute = ca.get(Calendar.MINUTE);
			String endMinute = " 00";
			if(afterMinute>=5 && afterMinute<20){
				endMinute = " 15";
			}else if(afterMinute>=20 && afterMinute<35){
				endMinute = " 30";
			}else if(afterMinute>=35 && afterMinute<50){
				endMinute = " 45";
			}else if(afterMinute>=50 || afterMinute<5){
				endMinute = " 00";
			}
			Date endDate = DateUtil.getStringToDate(preDate+" "+afterHour+":"+endMinute+":00");
			return endDate;
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return null;
	}


	/**
	 * APP
	 * 新增订单
	 */
	@Before(Tx.class)
	public void addOrder(){
		String json = HttpKit.readData(getRequest());
		Map<String, Object> result = null;
		try{
			OrderDTO orderDTO = FastJson.getJson().parse(json, OrderDTO.class);
			result = orderService.addOrder(orderDTO);
		}catch(Exception ex){}
		if(result!=null){
			renderSuccess("",result);
		}else{
			addOpLog("addOrder ===> json="+json);
			renderFailed();
		}

	}


	/**
	 * APP
	 * 小程序没有用
	 * 二次支付
	 */
	public void extraOrder(){
		String json = HttpKit.readData(getRequest());
		try{
			OrderBasic orderBasic = FastJson.getJson().parse(json, OrderBasic.class);
			if(orderService.extraOrder(orderBasic)){
				renderSuccess();
			}else{
				renderFailed();
			}
		}catch(Exception ex){
			addOpLog("extraOrder ===> json="+json);
			ex.printStackTrace();
			renderFailed();
		}
	}

	/**
	 * APP
	 * 查询订单
	 */
	public void queryOrderBasicByPage(){
		Integer pageNo = getPager().getPage();
		Integer pageSize = getPager().getRows();
		Integer userId = getParaToInt("userId");
		Integer status = getParaToInt("status");
		try{
			renderSuccess("",orderService.selectOrderBasicList(pageNo,pageSize,userId,status));
		}catch(Exception ex){
			addOpLog("queryOrderBasicByPage ===> pageNo="+pageNo+",pageSize="+pageSize+",userId="+userId+",status="+status);
			ex.printStackTrace();
			renderFailed();
		}
	}

	/**
	 * APP
	 * 订单明细
	 */
	public void queryOrderDetail(){
		Integer oId = getParaToInt("oId");
		try{
			renderSuccess("",orderService.queryOrderDetail(oId));
		}catch(Exception ex){
			addOpLog("queryOrderDetail ===> oId="+oId);
			ex.printStackTrace();
			renderFailed();
		}
	}

	/**
	 * APP
	 * 物流明细
	 */
	public void getExpressInfo(){
		String type = getPara("type");
		String no = getPara("no");
		Integer oId = getParaToInt("oId");
		Integer express = getParaToInt("express");
		try{
			if(express==2){
				List<Record> recordList = Db.find(" select * from t_express_info where o_id=" + oId);
				renderSuccess("",recordList);
			}else if(express==0){
				String json= "{\"code\":\"OK\",\"no\":\"780098068058\",\"type\":\"ZTO\",\"list\":[{\"content\":\"【石家庄市】 快件已在 【长安三部】 签收,签收人: 本人, 感谢使用中通快递,期待再次为您服务!\",\"time\":\"2018-03-09 11:59:26\"},{\"content\":\"【石家庄市】 快件已到达 【长安三部】（0311-85344265）,业务员 容晓光（13081105270） 正在第1次派件, 请保持电话畅通,并耐心等待\",\"time\":\"2018-03-09 09:03:10\"},{\"content\":\"【石家庄市】 快件离开 【石家庄】 发往 【长安三部】\",\"time\":\"2018-03-08 23:43:44\"},{\"content\":\"【石家庄市】 快件到达 【石家庄】\",\"time\":\"2018-03-08 21:00:44\"},{\"content\":\"【广州市】 快件离开 【广州中心】 发往 【石家庄】\",\"time\":\"2018-03-07 01:38:45\"},{\"content\":\"【广州市】 快件到达 【广州中心】\",\"time\":\"2018-03-07 01:36:53\"},{\"content\":\"【广州市】 快件离开 【广州花都】 发往 【石家庄中转】\",\"time\":\"2018-03-07 00:40:57\"},{\"content\":\"【广州市】 【广州花都】（020-37738523） 的 马溪 （18998345739） 已揽收\",\"time\":\"2018-03-07 00:01:55\"}],\"state\":\"3\",\"msg\":\"查询成功\",\"name\":\"中通快递\",\"site\":\"www.zto.com\",\"phone\":\"95311\",\"logo\":\"https://img3.fegine.com/express/zto.jpg\",\"courier\":\"容晓光\",\"courierPhone\":\"13081105270\",\"updateTime\":\"2019-10-12 15:26:26\",\"takeTime\":\"5天23小时12分\"}";
				Map<String, Object> result = FastJson.getJson().parse(json, Map.class);
				if(result.get("code").equals("OK")){
					List<Map<String,Object>> resultList =FastJson.getJson().parse(result.get("list").toString(), List.class);
					Collections.sort(resultList, new Comparator<Map<String, Object>>() {
						public int compare(Map<String, Object> a, Map<String, Object> b) {
							Date dateA = null;
							Date dateB = null;
							try {
								dateA = DateUtil.getStringToDate(a.get("time").toString());
								dateB =  DateUtil.getStringToDate(b.get("time").toString());
							} catch (ParseException e) {
								e.printStackTrace();
							}

							return dateB.compareTo(dateA);
						}
					});
					boolean flag = false;
					for(Map<String,Object> map : resultList){
						if(map.get("content").toString().contains("签收")){
							flag = true;
							break;
						}
					}
					if(flag){
						Db.delete("delete from t_express_info where o_id="+oId);
						List<ExpressInfo> expressInfoList = new ArrayList<ExpressInfo>();
						for(Map<String,Object> map : resultList){
							ExpressInfo expressInfo = new ExpressInfo();
							expressInfo.setOId(oId);
							expressInfo.setTime(DateUtil.getStringToDate(map.get("time").toString()));
							expressInfo.setContent(map.get("content").toString());
							expressInfoList.add(expressInfo);
						}
						Db.batchSave(expressInfoList,expressInfoList.size());
						Db.update("update t_order_basic set is_express=2 where o_id="+oId);
					}
					renderSuccess("",resultList);
				}
//				List<Map<String, Object>> resultList = UrlUtil.getExpressInfo(type, no);
//				boolean flag = false;
//				for(Map<String,Object> map : resultList){
//					if(map.get("content").toString().contains("签收")){
//						flag = true;
//						break;
//					}
//				}
//				if(flag){
//					Db.delete("delete from t_express_info where o_id="+oId);
//					List<ExpressInfo> expressInfoList = new ArrayList<ExpressInfo>();
//					for(Map<String,Object> map : resultList){
//						ExpressInfo expressInfo = new ExpressInfo();
//						expressInfo.setOId(oId);
//						expressInfo.setTime(DateUtil.getStringToDate(map.get("time").toString()));
//						expressInfo.setContent(map.get("content").toString());
//						expressInfoList.add(expressInfo);
//					}
//					Db.batchSave(expressInfoList,expressInfoList.size());
//				}
//				renderSuccess("",resultList);
			}
		}catch(Exception ex){
			addOpLog("queryOrderDetail ===> type="+type+",no="+no);
			ex.printStackTrace();
			renderFailed();
		}
	}


	/**
	 * APP
	 * 取消订单
	 */
	public void closeOrder(){
		Integer oId = getParaToInt("oId");
		boolean flag = false;
		try{
			flag = orderService.closeOrder(oId);
		}catch(Exception ex){}
		if(flag){
			renderSuccess();
		}else{
			addOpLog("closeOrder ===> oId="+oId);
			renderFailed();
		}
	}

	/**
	 * APP
	 * 申请退单
	 */
	public void applyForRefundDetail(){
		Integer id = getParaToInt("id");
		OrderDetail orderDetail = OrderDetail.dao.findById(id);
		orderDetail.setChargebackStatus(1);
		orderDetail.update();
		//发送订阅消息
		Map<String, Object> idMap = new HashMap<String, Object>();
		idMap.put("value", orderDetail.getOId());
		Map<String, Object> amountMap = new HashMap<String, Object>();
		amountMap.put("value", orderDetail.getPaymentPrice().setScale(2, BigDecimal.ROUND_HALF_UP));
		Map<String, Object> phraseMap = new HashMap<String, Object>();
		phraseMap.put("value", "退款申请");
		Map<String, Object> timeMap = new HashMap<String, Object>();
		try {
			timeMap.put("value", DateUtil.getDayToString(new Date()));
		} catch (ParseException e) {
			e.printStackTrace();
		}
		MiniTempDataDTO dataDTO = new MiniTempDataDTO();
		dataDTO.setTime2(timeMap);
		dataDTO.setAmount3(amountMap);
		dataDTO.setPhrase4(phraseMap);
		dataDTO.setCharacter_string1(idMap);
		List<Record> recordList = Db.find("select a.s_phone from t_supplier_setting a,t_commodity_info b,t_order_detail c where c.s_id=b.s_id and b.p_id=a.s_id and c.s_id=" + orderDetail.getSId());
		if(recordList.size()>0){
			//new SubscribeMessage(orderDetail.getOId(), dataDTO,MiniUtil.REFUND_TEMP,2,recordList.get(0).getStr("s_openid")).start();
			//发送短信
			PhoneVerificationCode.sendMini(recordList.get(0).getStr("s_phone"), orderDetail.getOId().toString(), 2);
		}
		renderSuccess();

	}





	/**
	 * APP
	 * 商户查询待处理订单
	 * 1:待发货，2：待收货
	 */
	public void selectPendOrder(){
		Integer status = getParaToInt("status");
		Integer sId = getParaToInt("sId");
		try{
			StringBuffer sb = new StringBuffer();
			sb.append(" select a.o_id,a.consignee_name,a.consignee_range_time,a.consignee_phone,a.consignee_address, find_in_set('1',GROUP_CONCAT(b.chargeback_status)) as backStatus ");
			sb.append(" from t_order_basic a,t_order_detail b ");
			sb.append(" where a.o_id=b.o_id and a.s_id= "+sId);
			sb.append(" and a.order_status ="+status);
			if(status==1){
				sb.append(" or (a.order_status in (1,3) and b.chargeback_status=1)");
			}
			sb.append(" order by a.order_time ASC ");
			List<Record> records = Db.find(sb.toString());
			renderSuccess("",records);
		}catch(Exception ex){
			addOpLog("selectPendOrder ===> sId="+sId);
			ex.printStackTrace();
			renderFailed();
		}
	}

	/**
	 * 商户
	 * APP
	 * 待处理明细
	 */
	public void selectPenderDetail(){
		Integer oId = getParaToInt("oId");
		try{
			StringBuffer sb = new StringBuffer();
			sb.append(" select a.is_express,a.order_status,DATE_FORMAT(a.order_time,'%Y-%m-%d %T') as order_time,SUBSTRING_INDEX(c.s_address_img,'~',1) as coverUrl,a.total_back_price,a.extra_status,a.o_id,b.id,a.consignee_name,a.consignee_phone,a.consignee_range_time,a.consignee_address,b.extra_img_url,b.is_extra,b.extra_weight,b.extra_price ");
			sb.append(" ,a.back_price_status ,b.extra_back_status,b.extra_pay_status,CONCAT(c.s_name,' ￥',c.s_price,'/',c.s_unit) as sName,case c.init_unit when 1 then concat(b.order_num,'个') else concat(b.order_num,'g') end as num,b.payment_price,b.chargeback_status,a.post_cost,a.express_type,a.express_no ");
			sb.append("  ,case a.extra_status when 1 then '已支付' when 2 then '未支付' when 3 then '支付中' when 4 then '转入退款' when 5 then '支付失败' else '待补差价' end as payText " +
					" ,case a.back_price_status when 1 then '申请退款中' when 2 then '已退款' when 3 then '退款处理中' when 4 then '退款异常' else '待返还' end as backText " +
					" ,case b.chargeback_status when 1 then '待退款' when 2 then '已退款' when 3 then '退款中' when 4 then '退款异常' when 5 then '退款关闭' else '' end as refundBack ");
			sb.append(" ,case a.payment_status when 1 then '已支付' when 2 then '未支付' when 3 then '支付中' when 4 then '转入退款' else '支付失败' end as paymentStatus ");
			sb.append(" ,DATE_FORMAT(a.last_time,'%Y-%m-%d %T') as last_time,a.extra_payment,a.extra_time,extra_pay_back_status,a.total_price ");
			sb.append(" ,case a.order_status when 1 then '待发货' when 2 then '待收货' when 3 then '已送达' when 4 then '已关闭' else '已取消' end as orderStatus ");
			sb.append(" from t_order_basic a,t_order_detail b,t_commodity_info c,t_supplier_setting d  ");
			sb.append(" where a.o_id=b.o_id and b.s_id=c.s_id and c.p_id=d.s_id and a.o_id="+oId);
			List<Record> records = Db.find(sb.toString());
			if(records.size()>0){
				renderSuccess("",records);
			}else{
				renderFailed();
			}
		}catch(Exception ex){
			addOpLog("selectPenderDetail ===> oId="+oId);
			ex.printStackTrace();

		}
	}

	public void updateExtraWeight(){
		Integer id = getParaToInt("id");
		String value = getPara("value");
		int update = 0;
		try{
			update = Db.update("update t_order_detail set extra_weight=" + value + " where id=" + id);
		}catch(Exception ex){}
		if(update==0){
			addOpLog("updateExtra ===> id="+id+",value="+value);
			renderFailed();
		}else{
			renderSuccess();
		}
	}

	public void updateExtraPrice(){
		Integer id = getParaToInt("id");
		String value = getPara("value");
		int update = 0 ;
		try{
			update = Db.update("update t_order_detail set extra_price=" + value + " where id=" + id);
		}catch(Exception ex){}
		if(update==0){
			addOpLog("updateExtraPrice ===> id="+id+",value="+value);
			renderFailed();
		}else{
			renderSuccess();
		}
	}



	/**
	 * APP
	 */
	public void changeIsExtra(){
		Integer id = getParaToInt("id");
		Integer isExtra = getParaToInt("isExtra");
		int update =0;
		try{
			update = Db.update("update t_order_detail set is_extra=" + isExtra + " where id=" + id);
		}catch(Exception ex){}
		if(update==0){
			addOpLog("changeIsExtra ===> id="+id+", isExtra="+isExtra);
			renderFailed();
		}else{
			renderSuccess();
		}
	}


	/**
	 * APP
	 * 商户上传图片
	 */
	public void uploadPics(){
		synchronized (OrderController.class) {
			UploadFile file = getFile();
			Integer id = getParaToInt("id");
			try{
				if(file != null) {
					String name = id+"_"+System.currentTimeMillis()+".png";
					String path = PathKit.getWebRootPath()+"/upload/"+name;
					file.getFile().renameTo(new File(path));
					OrderDetail detail = OrderDetail.dao.findById(id);
					//new File(PathKit.getWebRootPath()+"/upload/"+file).delete();
					String imageName = (detail.getExtraImgUrl() == null || "".equals(detail.getExtraImgUrl())) ? name : (detail.getExtraImgUrl() + "~" + name);
					detail.setExtraImgUrl(imageName);
					if(detail.update()){
						renderSuccess("",name);
					}else{
						renderFailed("添加图片失败");
					}
				}else{
					renderFailed("请添加图片");
				}
			}catch(Exception ex){
				addOpLog("uploadPics ===>id="+id);
				ex.printStackTrace();
				renderFailed();
			}
		}
	}

	/**
	 * 商户删除图片
	 */
	@Before(Tx.class)
	public void deletePic(){
		Integer id = getParaToInt("id");
		Integer idx = getParaToInt("idx");
		boolean flag = false;
		try{
			flag = orderService.deletePic(id,idx);
		}catch(Exception ex){}
		if(flag){
			renderSuccess();
		}else{
			addOpLog("deletePic ===>id="+id+",idx="+idx);
			renderFailed();
		}
	}


	/**
	 * 商户查询：待收货、已送达、已关闭的订单
	 */
	public void getOrderBySupplier(){
		Integer sId = getParaToInt("sId");
		String startDate = getPara("startDate");
		String endDate = getPara("endDate");
		String name = getPara("name");
		String phone = getPara("phone");
		Integer oId = getParaToInt("oId");
		Integer pageNo = getParaToInt("pageNo");
		Integer pageSize = getParaToInt("pageSize");

		try{
			String select = " select CASE a.order_status when 2 then '待收货' when 3 then '已送达' else '已关闭' END AS STATUS," +
					"a.o_id,DATE_FORMAT(a.order_time,'%Y-%m-%d') orderTime,a.consignee_name,a.consignee_phone,a.is_express,a.express_type,a.express_no ";
			StringBuffer sb = new StringBuffer();
			sb.append(" from t_order_basic a ");
			sb.append(" where a.s_id="+sId);
			if(!"".equals(startDate) && !"".equals(endDate)){
				sb.append(" and a.order_time>='"+startDate+"' and a.order_time<='"+endDate+"'");
			}
			if(!"".equals(name) && name!=null){
				sb.append(" and a.consignee_name like '%"+name+"%'");
			}
			if(!"".equals(phone) && phone!=null){
				sb.append(" and a.consignee_phone = '"+phone+"'");
			}
			if(oId!=null){
				sb.append(" and a.o_id ="+oId);
			}
			sb.append(" order by a.order_time desc ");
			Page<Record> paginate = Db.paginate(pageNo, pageSize, select, sb.toString());
			renderSuccess("",paginate);
		}catch(Exception ex){
			addOpLog("getOrderBySupplier ===>sId="+sId+",startDate="+startDate+",endDate="+endDate+",name="+name+",pageNo="+pageNo+",pageSize="+pageSize);
			ex.printStackTrace();
			renderFailed();
		}
	}


	/**
	 * 商户
	 * APP
	 * 订单管理明细
	 */
	public void selectOrderDetail(){
		String oId = getPara("oId");
		String token = getPara("token");
		String openid = CacheKit.get("miniProgram", token);
		try{
			if(!"".equals(openid)){
				StringBuffer sb = new StringBuffer();
				sb.append(" select a.o_id,b.id,a.consignee_name,a.payment_status,a.consignee_phone,a.consignee_range_time,a.consignee_address,b.extra_img_url,b.is_extra ");
				sb.append(" ,CONCAT(c.s_name,' ￥',c.s_price,'/',c.s_unit) as sName,case c.init_unit when 1 then concat(b.order_num,'个') else concat(b.order_num,'g') end as num,b.payment_price,b.chargeback_status ");
				sb.append(" from t_order_basic a,t_order_detail b,t_commodity_info c ,t_supplier_setting d  ");
				sb.append(" where a.o_id=b.o_id and b.s_id=c.s_id and a.o_id="+oId+" and c.p_id=d.s_id and d.s_openid='"+openid+"'");
				List<Record> records = Db.find(sb.toString());
				if(records.size()>0){
					renderSuccess("",records);
				}else{
					renderFailed();
				}
			}else{
				renderSuccess("1");
			}

		}catch(Exception ex){
			addOpLog("selectOrderDetail ===> oId="+oId);
			ex.printStackTrace();

		}
	}


	/**
	 * 订单查询
	 */
	public void queryOrder() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//订单id
			String oId = getPara("o_id");
			OrderBasic orderInfoo = orderService.selectOrder(oId);
			map.put("msg", "成功请求");
			map.put("result", "success");
			map.put("data", orderInfoo);
		} catch (Exception e) {
			map.put("msg", "服务异常");
			map.put("result", "failed");
			e.printStackTrace();
		}
		renderJson(map);
	}

	/**
	 * 下单-订单生成
	 */
	private void generateOrder() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			String json = HttpKit.readData(getRequest());
			OrderBasic orderInfo = FastJson.getJson().parse(json, OrderBasic.class);
			boolean b = orderService.generateOrder(orderInfo);
			map.put("msg", "下单成功");
			map.put("result", "success");
		} catch (Exception e) {
			map.put("msg", "下单异常");
			map.put("result", "failed");
			e.printStackTrace();
		}
		renderJson(map);
	}

	/**
	 * 更新订单状态
	 */
	private void updateOrderstatus() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//订单id
			String oId = getPara("o_id");
			//订单状态
			String orderStatus = getPara("order_status");
			boolean b = orderService.updateOrderstatus(oId,orderStatus);
			map.put("msg", "订单状态更新成功");
			map.put("result", "success");
		} catch (Exception e) {
			map.put("msg", "订单状态更新异常");
			map.put("result", "failed");
			e.printStackTrace();
		}
		renderJson(map);
	}

	/**
	 * 订单管理列表查询页面
	 */
	public void listPage() {
		render("list.html");
	}

	/**
	 * 订单管理列表查询数据
	 */
	public void listData() {
		Map<String, String[]> paraMap = getParaMap();
		String orderBy = getOrderBy();
		int pageNum = getPager().getPage();
		int pageSize = getPager().getRows();
		renderJson(orderService.findPage(pageSize,pageNum,paraMap,orderBy));
	}

	/**
	 * 订单详情页面
	 */
	public void detailPage() {
		setAttr("id", getPara("id"));
		render("detail.html");
	}

	/**
	 * 订单详情列表查询数据
	 */
	public void detailData() {
		Map<String, String[]> paraMap = getParaMap();
		String orderBy = getOrderBy();
		int pageNum = getPager().getPage();
		int pageSize = getPager().getRows();
		renderJson(orderService.findDetailPage(pageSize,pageNum,paraMap,orderBy));
	}

	/**
	 * 订单物流信息查询
	 */
	private void logisticsFind() {
		Map<String, Object> map = new HashMap<String, Object>();
		try {
			//订单id
			String oId = getPara("o_id");
			Record record = orderService.logisticsFind(oId);
			map.put("msg", "请求成功");
			map.put("result", "success");
			map.put("data", record);
		} catch (Exception e) {
			map.put("msg", "查询异常");
			map.put("result", "failed");
			e.printStackTrace();
		}
		renderJson(map);
	}

}
