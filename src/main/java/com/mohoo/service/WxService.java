/*
 * WxService.java
 * 版权所有：南京摩虎网络科技有限公司 2010 - 2020
 * 南京摩虎网络科技有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.mohoo.service;

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.alibaba.fastjson.JSONObject;
import com.mohoo.config.BaseConfig;
import com.mohoo.entity.CardInfo;
import com.mohoo.util.OkHttpUtil;
import com.mohoo.util.PropertiesUtil;

/**
 * 类描述
 * <p>
 * 创建日期：2016年6月30日<br>
 * 修改历史：<br>
 * 修改日期：<br>
 * 修改作者：<br>
 * 修改内容：<br>
 * 
 * @author Administrator
 * @version 1.0
 */
public class WxService {
	private static final Logger logger = LoggerFactory
			.getLogger(WxService.class);

	protected BaseConfig baseConfig;
	protected final Object globalAccessTokenRefreshLock = new Object();

	protected static final int expiredTime = 7200;
	protected static final long sleepTime = 1000;
	// token有效时间2小时

	protected final Object globalWxCardTicketRefreshLock = new Object();

	public BaseConfig getBaseConfig() {
		return baseConfig;
	}

	public void setBaseConfig(BaseConfig baseConfig) {
		this.baseConfig = baseConfig;
	}
	/**
	 * 获取access_token
	 * 方法描述
	 * @param flag
	 * @return
	 */
	public String getAccessToken() {
		return getAccessToken(false);
	}
	/**
	 * 获取access_token
	 * 方法描述
	 * @param flag
	 * @return
	 */
	public String getAccessToken(boolean flag) {
		if (flag) {
			baseConfig.expireAccessToken();
		}
		if (baseConfig.isAccessTokenExpired()) {
			synchronized (globalAccessTokenRefreshLock) {
				while (true) {
					String accessToken = "";
					try {
						accessToken = findAccessToken();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (StringUtils.isNotEmpty(accessToken)) {
						baseConfig.updateAccessToken(accessToken, expiredTime);
						logger.info("==========accessToken:" + accessToken);
						break;
					}
					try {
						Thread.sleep(sleepTime);
						logger.info("==========token result is baded,after sleep one second,refresh token!");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return baseConfig.getAccessToken();
	}
	/**
	 * 调用接口获取参数
	 * 方法描述
	 * @return
	 * @throws IOException
	 */
	private String findAccessToken() throws IOException {
		String jsonMap = OkHttpUtil.doGetHttpRequest(PropertiesUtil
				.getPropertyPath("weixin.access_token"));
		if (StringUtils.isNotEmpty(jsonMap)) {
			Map<String, Object> resultMap = JSONObject.parseObject(jsonMap);
			if (resultMap.get("status") != null
					&& StringUtils.isNotEmpty(resultMap.get("status")
							.toString())
					&& StringUtils.equals(resultMap.get("status").toString(),
							"200") && resultMap.get("data") != null) {
				return resultMap.get("data").toString();
			}
		}
		return null;
	}

	public String getWxCardTicket() {
		return getWxCardTicket(false);
	}

	public String getWxCardTicket(boolean forceRefresh) {
		if (forceRefresh) {
			baseConfig.expireWxCardTicket();
		}
		if (baseConfig.isWxCardTicketExpired()) {
			synchronized (globalWxCardTicketRefreshLock) {
				while (true) {
					String wxCardTicket = "";
					try {
						wxCardTicket = findWxCardTicket();
					} catch (IOException e1) {
						e1.printStackTrace();
					}
					if (StringUtils.isNotEmpty(wxCardTicket)) {
						baseConfig.updateWxCardTicket(wxCardTicket, expiredTime);
						logger.info("==========wxCardTicket:" + wxCardTicket);
						break;
					}
					try {
						Thread.sleep(sleepTime);
						logger.info("==========token result is baded,after sleep one second,refresh token!");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
		return baseConfig.getWxCardTicket();
	}
	
	public String findWxCardTicket() throws IOException {
//		String jsonMap = OkHttpUtil.doGetHttpRequest(PropertiesUtil
//				.getPropertyPath("weixin.card").concat("?access_token=")
//				.concat(getAccessToken()));
//		if (StringUtils.isNotEmpty(jsonMap)) {
//			Map<String, Object> resultMap = JSONObject.parseObject(jsonMap);
//			if (resultMap.get("status") != null
//					&& StringUtils.isNotEmpty(resultMap.get("status")
//							.toString())
//					&& StringUtils.equals(resultMap.get("status").toString(),
//							"200") && resultMap.get("data") != null) {
//				return resultMap.get("data").toString();
//			}
//		}
		return null;
	}
	private String getRealyUrl(String url){
		if (StringUtils.isNotEmpty(url)) {
			if (url.indexOf("access_token=") != -1) {
				throw new IllegalArgumentException("uri参数中不允许有access_token: " + url);
			}
//			String accessToken = getAccessToken(false);
			String accessToken="EHtK5E15NwSOa87MhIoDRU1U4cpp2SVWCu74QZfoyWiqJDGMYnhRGiAxDHGpN4TqbVo-pk22XKk7R05TA0vV14lFijieIPWgCWCLLvfr0foGOCfAAAWES";
			url += url.indexOf('?') == -1 ? "?access_token=" + accessToken : "&access_token=" + accessToken;
		}
		return url;
	}
	/**
	 * 上传图片
	 * 方法描述
	 * http:\/\/mmbiz.qpic.cn\/mmbiz\/LLialCGQGiaEeTccVzMHyaFebQlxJUOy2vjkIsib8uTENiayyrdCF6WzpvVQn3CVXS2eqOLIsbiaHdj502GeHoRckVw\/0
	 * @return
	 * @throws IOException 
	 */
	public Map<String,Object> uploadImg(File file) throws IOException{
		String imgUrl=getRealyUrl(PropertiesUtil.getPropertyPath("weixin.uploadimg"));
		return excutePostFile(imgUrl,file);
	}
	public String uploadImgToJson(File file) throws IOException{
		String imgUrl=getRealyUrl(PropertiesUtil.getPropertyPath("weixin.uploadimg"));
		Map<String,Object> resultMap=excutePostFile(imgUrl,file);
		if (resultMap.get("url")!=null) {
			return resultMap.get("url").toString();
		}else{
			throw new IOException("code:"+resultMap.get("errcode")+",info"+resultMap.get("errmsg"));
		}
	}
	/**
	 * 普通get请求方法
	 * 方法描述
	 * @param url
	 * @return
	 * @throws IOException
	 */
	private Map<String,Object> excuteGet(String url) throws IOException{
		String info=OkHttpUtil.doGetHttpRequest(url);
		Map<String,Object> resultMap=JSONObject.parseObject(info);
		if (resultMap.get("errcode")!=null) {
			String errcode=resultMap.get("errcode").toString();
			if (StringUtils.equals(errcode, "42001")||StringUtils.equals(errcode, "40001")) {
				baseConfig.expireAccessToken();
				return excuteGet(url);
			}
		}
		return resultMap;
	}
	/**
	 * 普通post请求方法
	 * 方法描述
	 * @param url
	 * @param data
	 * @return
	 * @throws IOException
	 */
	private Map<String,Object> excutePost(String url ,String data) throws IOException{
		String info=OkHttpUtil.doPostHttpRequest(url,data);
		Map<String,Object> resultMap=JSONObject.parseObject(info);
		if (resultMap.get("errcode")!=null) {
			String errcode=resultMap.get("errcode").toString();
			if (StringUtils.equals(errcode, "42001")||StringUtils.equals(errcode, "40001")) {
				baseConfig.expireAccessToken();
				return excutePost(url,data);
			}
		}
		return resultMap;
	}
	/**
	 * 调用接口，上传文件
	 * 方法描述
	 * @param url
	 * @param file
	 * @return
	 * @throws IOException
	 */
	private Map<String,Object> excutePostFile(String url ,File file) throws IOException{
		String info=OkHttpUtil.doPostImgHttpRequest(url,file);
		Map<String,Object> resultMap=JSONObject.parseObject(info);
		if (resultMap.get("errcode")!=null) {
			String errcode=resultMap.get("errcode").toString();
			if (StringUtils.equals(errcode, "42001")||StringUtils.equals(errcode, "40001")) {
				baseConfig.expireAccessToken();
				return excutePostFile(url,file);
			}
		}
		return resultMap;
	}
//	public static void main(String[] args) throws IOException {
//		String info=OkHttpUtil.doPostImgHttpRequest(PropertiesUtil.getPropertyPath("weixin.uploadimg").concat("?access_token=").concat("EHtK5E15NwSOa87MhIoDRU1U4cpp2SVWCu74QZfoyWiqJDGMYnhRGiAxDHGpN4TqbVo-pk22XKk7R05TA0vV14lFijieIPWgCWCLLvfr0foGOCfAAAWES"),new File("D:/test.png"));
//		System.out.println(info);
//	}
	String imgUrl="http://mmbiz.qpic.cn/mmbiz/LLialCGQGiaEeTccVzMHyaFebQlxJUOy2vjkIsib8uTENiayyrdCF6WzpvVQn3CVXS2eqOLIsbiaHdj502GeHoRckVw/0";
	/**
	 * 创建卡劵
	 * 3种类型，6种方法
	 * @param cardInfo
	 * @return
	 * @throws IOException
	 * *************************************************************************************************
	 */
	public Map<String,Object> createCard(CardInfo cardInfo) throws IOException{
		String jsonObject=JSONObject.toJSONString(cardInfo);
		return callCreateCard(jsonObject);
	}
	public Map<String,Object> createCard(Map<String,Object> paramMap) throws IOException{
		String jsonObject=JSONObject.toJSONString(paramMap);
		return callCreateCard(jsonObject);
	}
	public Map<String,Object> createCard(String jsonObject) throws IOException{
		return callCreateCard(jsonObject);
	}
	
	public String createCardToJson(CardInfo cardInfo) throws IOException{
		String jsonObject=JSONObject.toJSONString(cardInfo);
		Map<String,Object> resultMap= callCreateCard(jsonObject);
		if (resultMap.get("card_id")!=null) {
			return resultMap.get("card_id").toString();
		}else{
			throw new IOException("code:"+resultMap.get("errcode")+",info"+resultMap.get("errmsg"));
		}
	}
	public String createCardToJson(Map<String,Object> paramMap) throws IOException{
		String jsonObject=JSONObject.toJSONString(paramMap);
		Map<String,Object> resultMap= callCreateCard(jsonObject);
		if (resultMap.get("card_id")!=null) {
			return resultMap.get("card_id").toString();
		}else{
			throw new IOException("code:"+resultMap.get("errcode")+",info"+resultMap.get("errmsg"));
		}
	}
	public String createCardToJson(String jsonObject) throws IOException{
		Map<String,Object> resultMap= callCreateCard(jsonObject);
		if (resultMap.get("card_id")!=null) {
			return resultMap.get("card_id").toString();
		}else{
			throw new IOException("code:"+resultMap.get("errcode")+",info"+resultMap.get("errmsg"));
		}
	}
	
	private Map<String,Object> callCreateCard(String json) throws IOException{
		String createUrl=getRealyUrl(PropertiesUtil.getPropertyPath("weixin.create_card"));
		return excutePost(createUrl,json);
	}
	/**
	 * *******************************************************************************************
	 */
}
