package com.cebon.cdjcy.security.hadler;


import com.alibaba.fastjson.JSON;
import com.cebon.cdjcy.common.config.IApplicationConfig;
import com.cebon.cdjcy.common.util.IPUtils;
import com.cebon.cdjcy.common.restResult.RestResult;
import com.cebon.cdjcy.common.restResult.ResultCode;
import com.cebon.cdjcy.log.domain.SysLog;
import com.cebon.cdjcy.log.service.SysLogService;
import com.cebon.cdjcy.security.utils.ResponseUtils;
import com.cebon.cdjcy.user.dao.MenuRightMapper;
import com.cebon.cdjcy.user.domain.MenuRight;
import com.cebon.cdjcy.user.dto.LoginUserDTO;
import com.cebon.cdjcy.user.dto.loginweb.LoginOutpDTO;
import com.cebon.cdjcy.user.dto.loginweb.MenuDTO;
import com.cebon.cdjcy.user.service.MenuRightService;
import com.cebon.cdjcy.user.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import javax.servlet.ServletException;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.io.PrintWriter;
import java.util.Collections;
import java.util.Date;
import java.util.List;

@Component("userLoginSuccessHandler")
@Slf4j
public class UserLoginSuccessHandler implements AuthenticationSuccessHandler {
    @Autowired
    private MenuRightService menuRightService;
    @Autowired
    private UserService userService;
    @Resource
    private MenuRightMapper menuRightMapper;
    @Autowired
    private IApplicationConfig applicationConfig;
    @Resource
    private SysLogService sysLogService;


    @Override
    public void onAuthenticationSuccess(HttpServletRequest request,
                                        HttpServletResponse response,
                                        Authentication authentication) throws IOException, ServletException {

        //获得授权后可得到用户信息   可使用SUserService进行数据库操作
        LoginUserDTO userDetails = (LoginUserDTO)authentication.getPrincipal();
        LoginOutpDTO dto = new LoginOutpDTO();
        BeanUtils.copyProperties(userDetails, dto);
        dto.setRoles(userDetails.getRoles());

        List<MenuRight> allMenus = this.getAllMenus();
        List<MenuRight> userMenus = userDetails.getMenus();

        MenuDTO menuNode = new MenuDTO();
        for (MenuRight menu:allMenus) {

            MenuDTO menuDTO = new MenuDTO();
            BeanUtils.copyProperties(menu, menuDTO);
            menuDTO.setId(menu.getId());
            menuDTO.setParentId(menu.getParentId());
            menuDTO.setPath(menu.getUrlpath());
            menuDTO.setDisabled(false);
            for (MenuRight m : userMenus) {
                if (null != m && menu.getId().equals(m.getId())){
                    menuDTO.setDisabled(true);
                }
            }
            menuNode.add(menuDTO);
        }
        List<MenuDTO> menus = menuNode.getChilds();
        Collections.sort(menus);
        dto.setMenus(menus);

        //输出登录提示信息
        log.info("管理员 " + userDetails.getUsername() + " 登录");
        log.info("管理员 " + JSON.toJSONString(authentication.getAuthorities()) + "角色权限");
        log.info("IP :"+IPUtils.getIpAddr(request));
        // 记录登录成功的日志
        this.saveLog(request, authentication);


        //登录成功，在线人数加1；若超过最大在线数不允许登录
//       int num=sysconfigService.updateLoginUserNum(1);
//       if(num==2){
//           throw new CustomException(ResultCode.OVER_MAX_LOGIN_NUM);
//       }

        //对比入参数据
        Cookie cookie = new Cookie("isLogin","true");
        response.addCookie(cookie);
        String originHeader = request.getHeader("Origin");
        ResponseUtils.addResponseHeader(response, applicationConfig.getOrigins(), originHeader);
        RestResult result = new RestResult(ResultCode.SUCCESS.getCode(),"登录成功", dto);
        response.setCharacterEncoding("UTF-8");
        response.setContentType("text/html;charset=UTF-8");
        PrintWriter writer = response.getWriter();
        writer.print(JSON.toJSON(result));
        writer.flush();
    }



    public List<MenuRight> getAllMenus() {
        Example example = new Example(MenuRight.class);
        example.createCriteria().andLessThan("grades", 3);
        return menuRightMapper.selectByExample(example);
    }

    /**
     * 记录登录成功的日志
     * @param request
     */
    private void saveLog(HttpServletRequest request, Authentication authentication) {
        LoginUserDTO user = (LoginUserDTO) authentication.getPrincipal();
        SysLog sysLog = new SysLog();
        sysLog.setOperation(5);
        sysLog.setLogUser(user.getId());
        sysLog.setCreateTime(new Date());
        sysLog.setLogIp(IPUtils.getIpAddr(request));
        sysLog.setLogDesc(user.getUsername() + " 登录了系统 ");
        sysLog.setLogMethod(request.getMethod());
        sysLog.setLogType(0);
        sysLogService.saveLog(sysLog);
    }
}

