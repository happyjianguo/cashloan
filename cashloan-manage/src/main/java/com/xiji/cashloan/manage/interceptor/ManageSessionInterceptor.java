package com.xiji.cashloan.manage.interceptor;

import java.util.HashMap;
import java.util.Map;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import com.xiji.cashloan.core.common.context.Constant;
import com.xiji.cashloan.core.common.util.ServletUtils;
import com.xiji.cashloan.system.domain.SysUser;
import org.apache.shiro.SecurityUtils;
import org.apache.shiro.session.Session;
import org.springframework.stereotype.Service;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;


@Service
public class ManageSessionInterceptor extends HandlerInterceptorAdapter {

	@Override
	public boolean preHandle(HttpServletRequest request,HttpServletResponse response, Object handler) throws Exception {
		if(SecurityUtils.getSubject() == null || SecurityUtils.getSubject().getSession() == null){
			return true;
		}
		
		Session session = SecurityUtils.getSubject().getSession();
		SysUser sysUser = (SysUser) session.getAttribute("SysUser");
		Object noSession = session.getAttribute("noSession");
	  	if(sysUser == null && noSession == null){
	  		SecurityUtils.getSubject().getSession().setAttribute("noSession", true);
	  		
	  		Map<String,Object> result = new HashMap<String,Object>();
			result.put(Constant.RESPONSE_CODE, Constant.NOSESSION_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "您未登录或登录已过期，请登录后再操作");
			ServletUtils.writeToResponse(response,result);
			
	  		return false;
	  	}
	  	if(sysUser != null && sysUser.getStatus().equals("1")){
	  		Map<String,Object> result = new HashMap<String,Object>();
			result.put(Constant.RESPONSE_CODE, Constant.NOSESSION_CODE_VALUE);
			result.put(Constant.RESPONSE_CODE_MSG, "您的账户已被锁定，请联系管理员解锁");
			ServletUtils.writeToResponse(response,result);
	  	}
		return true;
	}
}
