package com.cebon.cdjcy.log.service.impl;

import com.cebon.cdjcy.common.config.ApplicationConfig;
import com.cebon.cdjcy.common.core.AbstractService;
import com.cebon.cdjcy.common.restResult.PageParam;
import com.cebon.cdjcy.log.dao.SysLogMapper;
import com.cebon.cdjcy.log.domain.SysLog;
import com.cebon.cdjcy.log.dto.SysLogInputDTO;
import com.cebon.cdjcy.log.dto.SysLogOutpDTO;
import com.cebon.cdjcy.log.service.SysLogService;
import com.github.pagehelper.PageHelper;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import tk.mybatis.mapper.entity.Example;

import javax.annotation.Resource;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

/**
* @author lirong
* @Description: 日志业务层
* @date 2018-12-05 03:21:19
*/
@Slf4j
@Service
@Transactional
public class SysLogServiceImpl extends AbstractService<SysLog> implements SysLogService {
    @Resource
    private SysLogMapper sysLogMapper;

    @Resource
    private ApplicationConfig applicationConfig;

    private static final int FIND = 4;
    /**
     * 根据分页、排序信息和检索条件查询 @size 条 字典表数据
     * @param pageParam 分页参数
     * @param inputDTO  查询关键字
     * @return
     */
    @Override
    public List<SysLogOutpDTO> list(PageParam pageParam, SysLogInputDTO inputDTO) {
        PageHelper.startPage(pageParam.getPage(), pageParam.getSize(), pageParam.getOrderBy());
        return sysLogMapper.selectLogByQuery(inputDTO);
    }

    /**
     * 保存日志
     * @param sysLog
     */
    @Override
    public void saveLog(SysLog sysLog) {
        //todo 查看不做操作 Log注解上没写注释的也不保存
        Integer operation = sysLog.getOperation();
        if (operation != FIND && operation != 0){
            sysLogMapper.insert(sysLog);
        }
    }

    /**
     * 删除日志k
     * @param ids
     */
    @Override
    public void deleteById(String ids) {
        List<String> list = Arrays.stream(ids.split(",")).collect(Collectors.toList());
        Example example = new Example(SysLog.class);
        example.and().andIn("id",list);
        sysLogMapper.deleteByExample(example);
    }

}
