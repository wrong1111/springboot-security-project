package com.example.core.security.hadler;

import com.example.core.common.config.IApplicationConfig;
import com.example.core.common.util.IPUtils;
import com.example.core.common.restResult.RestResult;
import com.example.core.common.restResult.ResultCode;
import com.example.core.log.domain.SysLog;
import com.example.core.log.service.SysLogService;
import com.example.core.security.utils.ResponseUtils;
import com.example.core.user.dto.LoginUserDTO;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.LogoutSuccessHandler;
import org.springframework.stereotype.Component;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Date;


@Component("securityLogoutSuccessHandler")
@Slf4j
public class SecurityLogoutSuccessHandler implements LogoutSuccessHandler {
	@Autowired
	private IApplicationConfig applicationConfig;
	@Resource
	private SysLogService sysLogService;

	@Override
	public void onLogoutSuccess(HttpServletRequest request , HttpServletResponse response , Authentication authentication) throws IOException,
            ServletException {

		//退出登录，在线人数减1
//		sysconfigService.updateLoginUserNum(-1);

        // 记录登出日志
        if (null != authentication) {
            this.saveLog(request, authentication);
        }
		log.info("退出成功");
		String originHeader = request.getHeader("Origin");
		ResponseUtils.addResponseHeader(response, applicationConfig.getOrigins(), originHeader);
		RestResult result = new RestResult(ResultCode.SUCCESS.getCode(),"退出成功");
		response.setCharacterEncoding("UTF-8");
		response.setContentType("text/html; charset=UTF-8");
		PrintWriter writer = response.getWriter();
		writer.print(result.toJson());
		writer.flush();
	}

	/**
	 * 记录登出日志
	 * @param request
	 */
	private void saveLog(HttpServletRequest request, Authentication authentication) {
        LoginUserDTO user = (LoginUserDTO) authentication.getPrincipal();
        SysLog sysLog = new SysLog();
		sysLog.setOperation(6);
		sysLog.setLogUser(user.getId());
		sysLog.setCreateTime(new Date());
		sysLog.setLogIp(IPUtils.getIpAddr(request));
		sysLog.setLogDesc(user.getUsername() + " 退出了系统 ");
		sysLog.setLogMethod(request.getMethod());
		sysLog.setLogType(0);
		sysLogService.saveLog(sysLog);
	}

}