/*
 * WxService.java
 * 版权所有：南京摩虎网络科技有限公司 2010 - 2020
 * 南京摩虎网络科技有限公司保留所有权利，未经允许不得以任何形式使用。
 */
package com.mohoo.config;

import java.io.IOException;
import java.util.Map;

import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

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
public class WxService {
	private static final Logger logger = LoggerFactory
			.getLogger(WxService.class);
	
	protected BaseConfig baseConfig;
	protected final Object globalAccessTokenRefreshLock = new Object();
	
	protected final int expiredTime =7200;

	public BaseConfig getBaseConfig() {
		return baseConfig;
	}

	public void setBaseConfig(BaseConfig baseConfig) {
		this.baseConfig = baseConfig;
	}

	public void getAccessToken() {
		getAccessToken(false);
	}

	public void getAccessToken(boolean flag) {
		if (flag) {
			baseConfig.expireAccessToken();
		}
		if (baseConfig.isAccessTokenExpired()) {
			synchronized (globalAccessTokenRefreshLock) {
				while (true) {
					String accessToken="";
					try {
						accessToken = findAccessToken();
					} catch (IOException e) {
						e.printStackTrace();
					}
					if (StringUtils.isNotEmpty(accessToken)) {
						baseConfig.updateAccessToken(accessToken, expiredTime);
						logger.info("==========accessToken:"+accessToken);
						break;
					}
					try {
						Thread.sleep(1000);
						logger.info("==========token result is baded,after sleep one second,refresh token!");
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
			}
		}
	}

	public String findAccessToken() throws IOException {
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
}
