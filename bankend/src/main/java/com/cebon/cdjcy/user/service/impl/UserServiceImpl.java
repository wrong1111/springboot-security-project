package com.cebon.cdjcy.user.service.impl;

import com.cebon.cdjcy.common.config.ApplicationConfig;
import com.cebon.cdjcy.common.core.AbstractService;
import com.cebon.cdjcy.common.restResult.PageParam;
import com.cebon.cdjcy.user.dao.UserMapper;
import com.cebon.cdjcy.user.domain.User;
import com.cebon.cdjcy.user.dto.UserDTO;
import com.cebon.cdjcy.user.dto.UserInputDTO;
import com.cebon.cdjcy.user.dto.UserOutpDTO;
import com.cebon.cdjcy.user.service.UserService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Date;
import java.util.List;

@Slf4j
@Service
@Transactional
public class UserServiceImpl extends AbstractService<User>  implements UserService {

    @Resource
    private UserMapper userMapper;

    @Resource
    private ApplicationConfig applicationConfig;

    @Override
    public List<User> queryList() {

        return null;
    }

    //根据用户名查询是存在
    @Override
    public Integer findCountByName(String username) {
        return userMapper.findCountByName(username);
    }

    @Override
    public List<User> list(PageParam pageParam, String query) {
        Example example = new Example(User.class);
//        example.or().andLike("code", "%"+query+"%");
//        example.or().andLike("deviceId", "%"+query+"%");

        PageHelper.startPage(pageParam.getPage(), pageParam.getSize(), pageParam.getOrderBy());
        return userMapper.selectByExample(example);
    }

    @Override
    public User findByUsername(String username) {
        return userMapper.findByUserName(username);
    }


    @Override
    public UserDTO getRolesByUsername(String username) {
        return userMapper.getRolesByUsername(username);
    }

    //根据用户名和密码查找用户
    @Override
    public User findByNameAndPwd(String username, String password) {
        return userMapper.findByNameAndPwd(username,password);
    }



    //添加用户
    @Override
    public void addUser(User user) {
        user.setCreateTime(new Date());
        user.setModifyTime(new Date());
        userMapper.addUser(user);
    }


 //    根据用户编号查询是存在
    @Override
    public Integer findCountByUserNum(String userNum) {
        return userMapper.findCountByUserNum(userNum);
    }



    //修改用户信息
    @Override
    public void updateUser(User user) {
        user.setModifyTime(new Date());
        userMapper.updateUser(user);
    }

    //修改用户密码
    @Override
    public void updateUserPwd(User user) {
        user.setModifyTime(new Date());
        userMapper.updateUserPwd(user);
    }

    //删除用户
    @Override
    public void deleteUserById(Long id) {
        userMapper.deleteUserById(id);
    }


   //查询用户详细信息（用户，角色，权限集合，区域，业务组）
    @Override
    public UserDTO findUserMessage(Long id) {
        return userMapper.findUserMessage(id);
    }

    //根据区域获取所有用户信息集合。（分页，返回用户加角色）
    @Override
    public List<UserOutpDTO> findListByArea(UserInputDTO inputDTO, PageParam pageParam) {
       PageHelper.startPage(pageParam.getPage(), pageParam.getSize());
       return userMapper.findListByArea(inputDTO);
    }

}
