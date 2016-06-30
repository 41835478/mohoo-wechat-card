/*
 * BaseConfig.java
 * 版权所有：南京摩虎网络科技有限公司 2010 - 2020
 * 南京摩虎网络科技有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.mohoo.config;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;

import com.alibaba.fastjson.JSONObject;
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
public class BaseConfig {
	protected volatile String accessToken;

	protected volatile String accessTokenExpiresTime;
	
	protected volatile String wxCardTicket;
	protected volatile long wxCardTicketExpiresTime;
	
	public synchronized String findAccessToken() throws IOException {
		String jsonMap = OkHttpUtil.doGetHttpRequest(PropertiesUtil
				.getPropertyPath("weixin.access_token"));
		if (StringUtils.isNotEmpty(jsonMap)) {
			Map<String, Object> resultMap = JSONObject.parseObject(jsonMap);
			if (resultMap.get("status") != null
					&& StringUtils.isNotEmpty(resultMap.get("status").toString())
					&& StringUtils.equals(resultMap.get("status").toString(), "200")
					&& resultMap.get("data") != null
				) {
				return resultMap.get("data").toString();
			}
		}
		return null;
	}
	
	public String getAccessToken() {
		return accessToken;
	}

	public void setAccessToken(String accessToken) {
		this.accessToken = accessToken;
	}
	
	public String getAccessTokenExpiresTime() {
		return accessTokenExpiresTime;
	}

	public void setAccessTokenExpiresTime(String accessTokenExpiresTime) {
		this.accessTokenExpiresTime = accessTokenExpiresTime;
	}
	
	public String getWxCardTicket() {
		return wxCardTicket;
	}

	public void setWxCardTicket(String wxCardTicket) {
		this.wxCardTicket = wxCardTicket;
	}

	public long getWxCardTicketExpiresTime() {
		return wxCardTicketExpiresTime;
	}

	public void setWxCardTicketExpiresTime(long wxCardTicketExpiresTime) {
		this.wxCardTicketExpiresTime = wxCardTicketExpiresTime;
	}

	public boolean isWxCardTicketExpired() {
		return System.currentTimeMillis() > this.wxCardTicketExpiresTime;
	}

	
	
	public synchronized void updateWxCardTicket(String wxCardTicket,
			int expiresInSeconds) {
		this.wxCardTicket = wxCardTicket;
		// 预留200秒的时间
		this.wxCardTicketExpiresTime = System.currentTimeMillis()
				+ (expiresInSeconds - 200) * 1000l;
	}

	public void expireWxCardTicket() {
		this.wxCardTicketExpiresTime = 0;
	}
}
