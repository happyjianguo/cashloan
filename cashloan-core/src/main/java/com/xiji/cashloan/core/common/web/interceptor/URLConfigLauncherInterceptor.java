package com.xiji.cashloan.core.common.web.interceptor;

import java.util.Map;
import java.util.Map.Entry;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiji.cashloan.core.common.model.URLConfig;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;

/**
 * 缺省的url映射处理
 * @author wnb
 * @date 2018/11/27
 * @version 1.0.0
 */
public class URLConfigLauncherInterceptor extends HandlerInterceptorAdapter {
	
	private Map<String, URLConfig> urlConfigs;
	
	@Override
	public boolean preHandle(HttpServletRequest request,
		HttpServletResponse response, Object handler) throws Exception {
		for (Entry<String, URLConfig> entry : urlConfigs.entrySet()) {
			request.setAttribute(entry.getKey(), entry.getValue());
		}
		return super.preHandle(request, response, handler);
	}

	public Map<String, URLConfig> getUrlConfigs() {
		return urlConfigs;
	}

	public void setUrlConfigs(Map<String, URLConfig> urlConfigs) {
		this.urlConfigs = urlConfigs;
	}
}
