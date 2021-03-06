package com.cos.cmtgp.business.controller;


import java.io.File;
import java.util.*;

import com.cos.cmtgp.business.model.CommodityInfo;
import com.cos.cmtgp.business.model.CommodityTypeSetting;
import com.cos.cmtgp.business.service.CommodityService;
import com.cos.cmtgp.common.base.BaseController;
import com.cos.cmtgp.common.vo.User;
import com.jfinal.json.FastJson;
import com.jfinal.kit.HttpKit;
import com.jfinal.kit.PathKit;
import com.jfinal.plugin.activerecord.Db;
import com.jfinal.plugin.activerecord.Page;
import com.jfinal.plugin.activerecord.Record;
import com.jfinal.upload.UploadFile;
import org.apache.commons.lang3.StringUtils;


/**
 *	  商品管理接口
 */
public class CommodityController extends BaseController {
	CommodityService commodityService = enhance(CommodityService.class);

	/**
	 * APP
	 * 查询所有类别
	 */
	public void queryCategoryList(){
		Integer sId = getParaToInt("sId");
		try{
			renderSuccess("",commodityService.selectCategoryList(sId));
		}catch(Exception ex){
			addOpLog("queryCategoryList ===>");
			ex.printStackTrace();
			renderFailed();
		}
	}

	/**
	 * 商户
	 * APP
	 * 查询商品
	 */
	public void queryGoods(){
		Integer tId = getParaToInt("tId");
		String tName = getPara("tName");
		Integer pId = getParaToInt("pId");
		Integer pageNo = getPager().getPage();
		Integer pageSize = getPager().getRows();
		String select = "select s_id,s_name ,concat(s_price,'/',s_unit) as unit,concat(price_unit,'/',case init_unit when 1 then '个' else '50g' end) as price,state";
		String from = "from t_commodity_info where dr=0 and p_id="+pId;
		if(tId!=0){
			from += " and t_id="+tId;
		}
		if(!"".equals(tName)){
			from += " and s_name like '%"+tName+"%'";
		}
		from += " order by update_time desc";
		Page<Record> paginate = Db.paginate(pageNo, pageSize, select, from);
		renderSuccess("",paginate);
	}

	public void queryOneGoods(){
		Integer sId = getParaToInt("sId");
		CommodityInfo commodityInfo = CommodityInfo.dao.findById(sId);
		renderSuccess("",commodityInfo);
	}

	/**
	 * 商户app
	 * 上下线
	 */
	public void updateState(){
		Integer sId = getParaToInt("sId");
		Integer state = getParaToInt("state");
		Db.update("update t_commodity_info set state="+state+",update_time=now() where s_id="+sId);
		renderSuccess();
	}


	/**
	 * 商户APP
	 * 删除
	 */
	public void deleteGoods(){
		Integer sId = getParaToInt("sId");
		CommodityInfo commodityInfo = CommodityInfo.dao.findById(sId);
		if(commodityInfo!=null){
			String sAddressImg = commodityInfo.getSAddressImg();
			if(!"".equals(sAddressImg) && sAddressImg!=null){
				String[] split = sAddressImg.split("~");
				for(String item : split){
					File file = new File(PathKit.getWebRootPath() + "/upload/" + item);
					if(file.exists()){
						file.delete();
					}
				}
			}
			String sDesc = commodityInfo.getSDesc();
			if(!"".equals(sDesc) && sDesc!=null){
				String[] split = sDesc.split("~");
				for(String item : split){
					File file = new File(PathKit.getWebRootPath() + "/upload/" + item);
					if(file.exists()){
						file.delete();
					}
				}
			}
			String sAddressVideo = commodityInfo.getSAddressVideo();
			if(!"".equals(sAddressVideo) && sAddressVideo!=null){
				File file = new File(PathKit.getWebRootPath() + "/upload/" + sAddressVideo);
				if(file.exists()){
					file.delete();
				}
			}
			commodityInfo.setDr(1);
			commodityInfo.update();
		}
		renderSuccess();
	}

	/**
	 * 商户APP
	 * 新增
	 */
	public void addGoods(){
		String json = HttpKit.readData(getRequest());
		CommodityInfo commodity = FastJson.getJson().parse(json,CommodityInfo.class);
		commodity.setUpdateTime(new Date());
		commodity.save();
		renderSuccess("",commodity.getSId());
	}

	/**
	 * 商户APP
	 * 修改
	 */
	public void updateGoods(){
		String json = HttpKit.readData(getRequest());
		Map parse = FastJson.getJson().parse(json, Map.class);
		String delete = (String)parse.get("delete");
		Object model = parse.get("model");
		CommodityInfo commodity = FastJson.getJson().parse(FastJson.getJson().toJson(model),CommodityInfo.class);
		commodity.setUpdateTime(new Date());
 		commodity.update();
 		if(!"".equals(delete)){
			String[] split = delete.split("~");
			for(String item : split){
				File file = new File(PathKit.getWebRootPath() + "/upload/" + item);
				if(file.exists()){
					file.delete();
				}
			}
		}
		renderSuccess();
	}

	public void addGoodsPic(){
		synchronized(CommodityInfo.class) {
			UploadFile file = getFile();
			Integer sId = getParaToInt("sId");
			Integer idx = getParaToInt("idx");
			if (file != null) {
				CommodityInfo commodity = CommodityInfo.dao.findById(sId);
				String sAddressImg = commodity.getSAddressImg();
				String name = sId + "_" + System.currentTimeMillis() + ".png";
				String path = PathKit.getWebRootPath() + "/upload/" + name;
				file.getFile().renameTo(new File(path));
				String[] split = sAddressImg.split("~");
				List<String> strList = new ArrayList<String>(Arrays.asList(split));
				strList.set(idx,name);
				String join = StringUtils.join(strList, "~");
				commodity.setSAddressImg(join);
				commodity.update();
			}
			renderSuccess();
		}
	}

	public void addGoodsDesc(){
		synchronized(CommodityInfo.class) {
			UploadFile file = getFile();
			Integer sId = getParaToInt("sId");
			Integer idx = getParaToInt("idx");
			if(file!=null) {
				CommodityInfo commodity = CommodityInfo.dao.findById(sId);
				String sDesc = commodity.getSDesc();
				String name = sId+"_"+System.currentTimeMillis()+".png";
				String path = PathKit.getWebRootPath()+"/upload/"+name;
				file.getFile().renameTo(new File(path));
				String[] split = sDesc.split("~");
				List<String> strList = new ArrayList<String>(Arrays.asList(split));
				strList.set(idx,name);
				String join = StringUtils.join(strList, "~");
		 		commodity.setSDesc(join);
				commodity.update();
			}
			renderSuccess();
		}
	}

	public void addGoodsVideo(){
		UploadFile file = getFile();
		Integer sId = getParaToInt("sId");
		if(file!=null) {
			CommodityInfo commodity = CommodityInfo.dao.findById(sId);
			String name = sId+"_"+System.currentTimeMillis()+".mp4";
			String path = PathKit.getWebRootPath()+"/upload/"+name;
			file.getFile().renameTo(new File(path));
			commodity.setSAddressVideo(name);
			commodity.update();
		}
		renderSuccess();
	}

	/**
	 * APP
	 * 热销商品分页查询/模糊查询/类别查询
	 * pageNo 页码
	 * tId 商品分类
	 * @return
	 */
	public void queryCommodityByPage(){
		Integer pageNo = getPager().getPage();
		Integer pageSize = getPager().getRows();
		Integer tId = getParaToInt("tId");
		String sName = getPara("sName");
		Integer userId = getParaToInt("userId");
		String areaFlag = getPara("areaFlag");
		try{
			renderSuccess("",commodityService.queryCommodityList(userId,areaFlag,pageNo,pageSize, tId, sName));
		}catch(Exception ex){
			addOpLog("queryCommodityByPage ===> pageNo"+pageNo+",areaFlag="+areaFlag+",tId="+tId+",sName="+sName+",userId="+userId);
			ex.printStackTrace();
			renderFailed();
		}
	}

	/**
	 * APP
	 * 查询单个商品
	 */
	public void queryCollage(){
		Integer sId = getParaToInt("sId");
		Integer userId = getParaToInt("userId");
		String areaFlag = getPara("areaFlag");
		try{
			renderSuccess("",commodityService.queryCommodity(sId,userId,areaFlag));
		}catch (Exception ex){
			addOpLog("queryCollage ====> sId"+sId+",userId="+userId);
			ex.printStackTrace();
			renderFailed();
		}

	}

	/**
	 * APP
	 * 查看时令、新商品
	 *
	 */
	public void queryActive(){
		Integer userId = getParaToInt("userId");
		Integer status = getParaToInt("status");
		String areaFlag = getPara("areaFlag");
		try{
			String sql = " select a.s_id,a.s_name,SUBSTRING_INDEX(a.s_address_img,'~',1) as coverUrl,a.init_unit,a.init_num,a.price_unit,c.s_corporate_name, " +
					" a.s_price,concat('/',a.s_unit) as unit,case when b.id is null then 0 else 1 end as isCar,count(d.id) as sales,a.state,find_in_set(a.delivery_area,'"+areaFlag+"') as areaFlag " +
					" from t_commodity_info a " +
					" inner join t_commodity_type_setting e on a.t_id=e.t_id " +
					" left join t_shopping_info b on a.s_id=b.s_id and b.u_id="+ userId +
					" left join t_supplier_setting c on a.p_id=c.s_id " +
					" left join t_order_detail d on d.s_id=a.s_id " +
					" where a.dr=0 and e.t_off=0 and a.is_active="+ status +
					" group by a.s_id order by FIELD(a.delivery_area,"+areaFlag+")desc,sales desc limit 20";
			List<Record> recordList = Db.find(sql);
			renderSuccess("",recordList);
		}catch (Exception ex){
			addOpLog("queryActive === >userId="+userId);
			ex.printStackTrace();
			renderFailed();
		}
	}

	/**
	 * APP
	 * 打折
	 *
	 */
	public void queryOnSales(){
		Integer userId = getParaToInt("userId");
		String areaFlag = getPara("areaFlag");
		try{
			List<Record> recordList = new ArrayList<Record>();
			String sqlOne = " select a.s_id,a.s_name,SUBSTRING_INDEX(a.s_address_img,'~',1) as coverUrl,a.init_unit,a.init_num,a.price_unit,a.original_price, " +
					" a.s_price,concat('/',a.s_unit) as unit,c.s_corporate_name,case when b.id is null then 0 else 1 end as isCar,count(d.id) as sales,a.state,find_in_set(a.delivery_area,'"+areaFlag+"') as areaFlag " +
					" from t_commodity_info a " +
					" left join t_supplier_setting c on a.p_id=c.s_id " +
					" left join t_shopping_info b on a.s_id=b.s_id and b.u_id="+ userId +
					" left join t_order_detail d on d.s_id=a.s_id " +
					" where a.dr=0 and a.p_id=1 and a.is_active=3 " +
					" group by a.s_id order by FIELD(a.delivery_area,"+areaFlag+")desc,sales desc limit 15";
			recordList.addAll(Db.find(sqlOne));
			String sqlTwo = " select a.s_id,a.s_name,SUBSTRING_INDEX(a.s_address_img,'~',1) as coverUrl,a.init_unit,a.init_num,a.price_unit,a.original_price, " +
					" a.s_price,concat('/',a.s_unit) as unit,c.s_corporate_name,case when b.id is null then 0 else 1 end as isCar,count(d.id) as sales,a.state,find_in_set(a.delivery_area,'"+areaFlag+"') as areaFlag " +
					" from t_commodity_info a " +
					" left join t_supplier_setting c on a.p_id=c.s_id " +
					" left join t_shopping_info b on a.s_id=b.s_id and b.u_id="+ userId +
					" left join t_order_detail d on d.s_id=a.s_id " +
					" where a.dr=0 and a.p_id=2 and a.is_active=3 " +
					" group by a.s_id order by FIELD(a.delivery_area,"+areaFlag+")desc,sales desc limit 15";
			recordList.addAll(Db.find(sqlTwo));
			Collections.shuffle(recordList,new Random(47));
			renderSuccess("",recordList);
		}catch (Exception ex){
			addOpLog("queryNews");
			ex.printStackTrace();
			renderFailed();
		}
	}


	/**
	 * 商品类型添加页面
	 */
	public void addTypePage() {
		render("CommodityType_add.html");
	}
	
	/**
	 * 商品类型添加
	 */
	public void addCommodityType() {
		CommodityTypeSetting commodityType = getModel(CommodityTypeSetting.class, "model");
		if(commodityType.save()) {
			renderSuccess("商品类型添加成功");
		}else {
			renderFailed("商品类型加失败");
		}
	}
	
	/**
	 * 商品类型修改页面
	 */
	public void updateTypePage() {
		setAttr("model", CommodityTypeSetting.dao.findById(getPara("id")));
		render("CommodityType_update.html");
	}
	
	/**
	 * 商品类型修改
	 */
	public void updateCommodityType() {
		if(getModel(CommodityTypeSetting.class, "model").update()) {
			renderSuccess();
		}else {
			renderFailed();
		}
	}	
	
	/**
	 * 商品类型删除
	 */
	public void deleteCommodityType() {
		try {
			Integer[] ids = getParaValuesToInt("id[]");
			for (Integer id : ids) {
				new CommodityTypeSetting().set("t_id", id).delete();
			}
			renderSuccess();
		} catch (Exception e) {
			renderFailed("商品类型删除失败");
			e.printStackTrace();
		}
	}	
	
	/**
	 * 商品类型查询页面
	 */
	public void listTypePage() {
		render("CommodityType_list.html");
	}
	
	/**
	 * 商品类型查询数据
	 */
	public void listTypeData() {
		Map<String, String[]> paraMap = getParaMap();
		String orderBy = getOrderBy();
		int pageNum = getPager().getPage();
		int pageSize = getPager().getRows();
		renderJson(commodityService.findTypePage(pageSize,pageNum,paraMap,orderBy));
	}
	
	/**
	 * 商品添加页面
	 */
	public void addPage() {
		render("Commodity_add.html");
	}
	
	/**
	 * 商品添加数据
	 */
	public void addCommodity() {
		List<UploadFile> uploadFiles = getFiles();
		CommodityInfo commodity = getModel(CommodityInfo.class, "model");
		commodity.set("sales_desc","sales_desc.jpg");
		if(uploadFiles!=null&&uploadFiles.size()>0) {
			StringBuffer buf = new StringBuffer();
			for(UploadFile f:uploadFiles) {
				if(f.getFileName().contains(".jpg")||f.getFileName().contains(".gif")||f.getFileName().contains(".png")) {
					if(f.getParameterName().contains("model.s_address_img")){
						buf.append(f.getFileName()+"~");
					}else if(f.getParameterName().equals("model.s_desc")){
						commodity.set("s_desc",f.getFileName());
					}else if(f.getParameterName().equals("model.sales_desc")){
						commodity.set("sales_desc",f.getFileName());
					}
				}else if(f.getFileName().contains(".mp4")){
					commodity.set("s_address_video",f.getFileName());
				}
			}
			if(buf.length()>0){
				commodity.set("s_address_img", buf.toString().substring(0,buf.length()-1));
			}
		}
		if(commodity.save()) {
			renderSuccess("商品添加成功");
		}else {
			renderFailed("商品添加失败");
		}
	}
	
	/**
	 * 商品修改页面
	 */
	public void updatePage() {
		setAttr("model", CommodityInfo.dao.findById(getPara("id")));
		render("Commodity_update.html");
	}

	/**
	 * 商品修改
	 */
	public void updateCommodity() {
		List<UploadFile> uploadFiles = getFiles();
		CommodityInfo model = getModel(CommodityInfo.class, "model");
		if(uploadFiles!=null&&uploadFiles.size()>0) {
			StringBuffer buf = new StringBuffer();
			for(UploadFile f:uploadFiles) {
				if(f.getFileName().contains(".jpg")||f.getFileName().contains(".gif")||f.getFileName().contains(".png")) {
					if(f.getParameterName().contains("model.s_address_img")){
						buf.append(f.getFileName()+"~");
					}else if(f.getParameterName().equals("model.s_desc")){
						model.set("s_desc", f.getFileName());
					}else if(f.getParameterName().equals("model.sales_desc")){
						model.set("sales_desc", f.getFileName());
					}
				}else if(f.getFileName().contains(".mp4")){
					model.set("s_address_video",  f.getFileName());
				}
			}
			if (buf.length()>0) {
				model.set("s_address_img", buf.toString().substring(0, buf.length()-1));
			}
		}
		if(model.update()) {
			renderSuccess("商品修改成功");
		}else {
			renderFailed("商品修改失败");
		}
	}	
	
	/**
	 * 商品删除
	 */
	public void deleteCommodity() {
		try {
			Integer[] ids = getParaValuesToInt("id[]");
			for (Integer id : ids) {
				new CommodityInfo().set("s_id", id).delete();
			}
			renderSuccess("商品上传完成");
		} catch (Exception e) {
			renderFailed("商品删除失败");
			e.printStackTrace();
		}
	}	
	
	/**
	 * 商品查询页面
	 */
	public void listPage() {
		render("Commodity_list.html");
	}
	
	/**
	 * 商品查询数据
	 */
	public void listData() {
		Map<String, String[]> paraMap = getParaMap();
		String orderBy = getOrderBy();
		int pageNum = getPager().getPage();
		int pageSize = getPager().getRows();
		String supplierId = ((User) getSession().getAttribute("sysUser")).getSupplierId();
		renderJson(commodityService.findPage(pageSize,pageNum,paraMap,orderBy,supplierId));
	}
	
	/**
	 * 获取配置列表数据
	 */
	public void getCommodityList() {
		 String type = getPara("type");
		if(type.equals("commodity")) {
			renderJson(Db.find("select t_id id,t_name text from t_commodity_type_setting"));
		}else if(type.equals("commoditytype")) {
			StringBuffer buf = new StringBuffer();
			buf.append("select s_id id,s_corporate_name text from t_supplier_setting");
			String supplierId = ((User) getSession().getAttribute("sysUser")).getSupplierId();
			if(null!=supplierId) {
				buf.append(" where s_id = "+supplierId);
			}
			List<Record> recordList = Db.find(buf.toString());
			Record record = recordList.get(0);
			record.set("selected",true);
			renderJson(recordList);
		}else if(type.equals("isActive")) {
			String supplierId = ((User) getSession().getAttribute("sysUser")).getSupplierId();
			List<Record> list = new ArrayList<Record>();
			Record r = new Record();
			r.set("id", 0);
			r.set("text", "否");
			list.add(r);
			Record r2 = new Record();
			r2.set("id", 3);
			r2.set("text", "折扣");
			list.add(r2);
			if(null!=supplierId) {
				if(supplierId.equals("1")) {
					Record r3 = new Record();
					r3.set("id", 1);
					r3.set("text", "时令水果");
					list.add(r3);
				}else {
					Record r4 = new Record();
					r4.set("id", 2);
					r4.set("text", "新商品");
					list.add(r4);
				}
			}else {
				Record r3 = new Record();
				r3.set("id", 1);
				r3.set("text", "时令水果");
				list.add(r3);
				Record r4 = new Record();
				r4.set("id", 2);
				r4.set("text", "新商品");
				list.add(r4);
			}
			renderJson(list);
		}
	}
	
}
