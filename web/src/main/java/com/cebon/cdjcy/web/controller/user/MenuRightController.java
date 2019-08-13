package com.cebon.cdjcy.web.controller.user;

import com.cebon.cdjcy.common.exception.CustomException;
import com.cebon.cdjcy.common.restResult.PageParam;
import com.cebon.cdjcy.common.restResult.RestResult;
import com.cebon.cdjcy.common.restResult.ResultCode;
import com.cebon.cdjcy.common.restResult.ResultGenerator;
import com.cebon.cdjcy.security.utils.SecurityUtils;
import com.cebon.cdjcy.user.domain.MenuRight;
import com.cebon.cdjcy.user.domain.User;
import com.cebon.cdjcy.user.dto.LoginUserDTO;
import com.cebon.cdjcy.user.dto.menuRightWeb.MeunRightWebDTO;
import com.cebon.cdjcy.user.service.MenuRightService;
import com.cebon.cdjcy.user.service.UserService;
import com.cebon.cdjcy.web.aop.Log;
import com.github.pagehelper.PageInfo;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.collections.CollectionUtils;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;
import java.util.*;


/**
 * @author lirong
 * @date 2018-11-01 09:34:51
 */
@Slf4j
@RestController
@RequestMapping("/authmenu")
@Api(basePath = "authmenu", value = "权限字典类", tags = "权限字典相关操作")
@Transactional
public class MenuRightController {
    @Resource
    private MenuRightService menuRightService;

    @Resource
    private UserService userService;


    @Log(type = 2, value = 4, description = "当前登录用户信息")
    @ApiOperation(value = "查询当前登录用户信息", notes = "查询当前登录用户信息", code = 200, produces = "application/json")
    @GetMapping("/curuser")
    public RestResult curuser() {
        LoginUserDTO userDTO = SecurityUtils.getCurrentUser();
        if (userDTO == null) {
            return ResultGenerator.genUnauthResult();
        }
        long userId = userDTO.getId();
        User user = userService.findById(userId);
        return ResultGenerator.genSuccessResult(user);
    }


    @Log
    @ApiOperation(value = "权限字典分页信息查看", notes = "权限字典，分页查看，需要传入分页信息", produces = "application/json")
    @GetMapping(value = "/list")
    public RestResult queryList(@ApiParam(value = "分页信息") PageParam pageParam,
                                @ApiParam(value = "查询条件") @RequestParam(required = false, defaultValue = "") String query) {
        List<MenuRight> list = menuRightService.list(pageParam, query);
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult().setData(pageInfo);
    }

    @Log(description = "添加单条权限", type = 7, value = 1)
    @ApiOperation(value = "添加单条权限", notes = "添加单条权限", produces = "application/json")
    @PostMapping("/add")
    public RestResult add(@ApiParam(name = "权限信息", required = true) MenuRight menuRight) {
        Date date = new Date();
        menuRight.setCreateTime(date);
        menuRightService.addMenuRight(menuRight);
        return ResultGenerator.genSuccessResult();
    }


    @Log(description = "根据id删除权限", type = 7, value = 2)
    @ApiOperation(value = "根据id删除权限", notes = "权限", produces = "application/json")
    @DeleteMapping("/delete")
    public RestResult delete(@ApiParam(name = "权限id", required = true) Long id) {
        menuRightService.deleteMenuRight(id);
        return ResultGenerator.genSuccessResult();
    }

    @Log(description = "级联删除菜单", type = 7, value = 2)
    @ApiOperation(value = "级联删除菜单", notes = "权限", produces = "application/json")
    @DeleteMapping("/deleteCashcade")
    public RestResult deleteCash(@ApiParam(name = "权限id", required = true) Long id) {
        deleteCashcade(id);
        menuRightService.deleteMenuRight(id);
        return ResultGenerator.genSuccessResult();
    }

    private void deleteCashcade(Long id) {
        if (id == null) {
            return;
        }
        List<MenuRight> menuRightList = menuRightService.findMenuRightByPid(id);
        if (CollectionUtils.isNotEmpty(menuRightList)) {
            deleteList(menuRightList);
        } else {
            menuRightService.deleteMenuRight(id);
        }
    }

    private void deleteList(List<MenuRight> menuRights) {
        if (CollectionUtils.isEmpty(menuRights)) return;
        for (MenuRight mr : menuRights) {
            deleteCashcade(mr.getId());
            menuRightService.deleteMenuRight(mr.getId());
        }
    }

    @Log(description = "修改权限字典", type = 7, value = 3)
    @ApiOperation(value = "修改权限字典", notes = "权限字典", produces = "application/json")
    @PutMapping("/update")
    public RestResult update(@ApiParam(name = "权限信息", required = true) MenuRight menuRight) {
        menuRightService.updateMenuRight(menuRight);
        return ResultGenerator.genSuccessResult();
    }

    @Log
    @ApiOperation(value = "通过id获取单个权限信息", notes = "通过id获取单个权限信息", produces = "application/json")
    @GetMapping("/detail")
    public RestResult detail(@ApiParam(name = "权限id", required = true) Long id) {
        MenuRight menuRight = menuRightService.findMenuRightById(id);
        return ResultGenerator.genSuccessResult(menuRight);
    }

    @Log(description = "获取所有权限信息", type = 7, value = 4)
    @ApiOperation(value = "获取所有权限信息", notes = "获取所有权限信息", produces = "application/json")
    @RequestMapping("/findAll")
    public RestResult findAll() {
        List<MenuRight> list = menuRightService.findAllMenuRight();
        PageInfo pageInfo = new PageInfo(list);
        return ResultGenerator.genSuccessResult(pageInfo);
    }

    @Log(description = "获取所有权限集合信息", type = 7, value = 4)
    @ApiOperation(value = "获取所有权限集合信息", notes = "获取所有权限集合信息", produces = "application/json")
    @RequestMapping("/findAllList")
    public List<MenuRight> findAllList(@RequestParam(value = "id", defaultValue = "-1", required = false) Integer id) {
        List<MenuRight> menuRights = menuRightService.findAllMenuRight();
        if (CollectionUtils.isEmpty(menuRights)) {
            return new ArrayList<>();
        }
        List<MenuRight> showMenuList = new ArrayList<>(menuRights.size());
        Map<Long, Integer> menuMap = new HashMap<>();
        boolean show = false;
        for (MenuRight r : menuRights) {
            if (id != -1) {
                if (r.getParentId().intValue() == id) {
                    show = true;
                    menuMap.put(r.getId(), 0);
                }
                if (show && menuMap.get(r.getId()) != null) {
                    showMenuList.add(r);
                }
            } else {
                showMenuList.add(r);
            }
        }
        return showMenuList;
    }

    @Log(description = "获取登录用户的权限树-findtree", type = 7, value = 4)
    @ApiOperation(value = "获取登录用户的权限树", notes = "获取登录用户的权限树", produces = "application/json")
    @GetMapping("/findtree")
    public RestResult findtree() {

        List<MenuRight> meunRightWebDTO = null;
        LoginUserDTO loginUserDTO = SecurityUtils.getCurrentUser();
        String rolename = loginUserDTO.getRoles().get(0).getRoleName();
        if (rolename.equals("超级管理员") || rolename.equals("系统管理员") || rolename.equalsIgnoreCase("admin")) {
            meunRightWebDTO = menuRightService.FindAllMenuRightWebDtO();
        } else {
            long roleid = SecurityUtils.getCurrentUser().getRoles().get(0).getId();
            meunRightWebDTO = menuRightService.findListByRoleId(roleid);
        }
        return ResultGenerator.genSuccessResult(meunRightWebDTO);

    }

    @Log(description = "获取角色对应的权限树-findRoleMenu", type = 7, value = 4)
    @ApiOperation(value = "获取角色对应的权限树", notes = "获取角色对应的权限树", produces = "application/json")
    @GetMapping("/findRoleMenu")
    public RestResult findRoleMenu(@RequestParam Long id) {

        List<MenuRight> meunRightWebDTO = null;
        if (id.equals(1L)) {
            meunRightWebDTO = menuRightService.FindAllMenuRightWebDtO();
        } else {
            meunRightWebDTO = menuRightService.findListByRoleId(id);
        }
        return ResultGenerator.genSuccessResult(meunRightWebDTO);

    }

    @Log(description = "判断用户是否有此对应权限-ifEntitle", type = 7, value = 4)
    @ApiOperation(value = "判断用户是否有此对应权限", notes = "判断用户是否有此对应权限", produces = "application/json")
    @GetMapping("/ifEntitle")
    public RestResult ifEntitle(@RequestParam Long id) {
        try {
            LoginUserDTO userDTO = SecurityUtils.getCurrentUser();
            String rolename = userDTO.getRoles().get(0).getRoleName();
            if (rolename.equals("超级管理员") || rolename.equals("系统管理员")) {
                return ResultGenerator.genSuccessResult(true);
            } else {
                List<MenuRight> menuRightList = userDTO.getMenus();
                for (MenuRight menuRight : menuRightList) {
                    if (menuRight.getId().equals(id)) {
                        return ResultGenerator.genSuccessResult(true);
                    }
                }
                return ResultGenerator.genSuccessResult(false);
            }

        } catch (Exception e) {
            throw new CustomException(ResultCode.USER_FINDNOWUSER_FAIL);
        }

    }

}
