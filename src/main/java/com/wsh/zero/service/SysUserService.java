package com.wsh.zero.service;

import com.google.common.base.Strings;
import com.wsh.util.*;
import com.wsh.zero.controller.aop.anno.SysLogTag;
import com.wsh.zero.entity.SysUserEntity;
import com.wsh.zero.mapper.SysUserMapper;
import com.wsh.zero.mapper.SysUserRoleMapper;
import com.wsh.zero.query.SysUserQuery;
import com.wsh.zero.service.base.BaseService;
import com.wsh.zero.vo.SysUserVO;
import org.apache.shiro.SecurityUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

import javax.servlet.http.HttpServletResponse;
import java.util.List;


@Service
public class SysUserService extends BaseService<SysUserMapper, SysUserQuery, SysUserVO, SysUserEntity> {
    @Autowired
    private SysUserMapper sysUserMapper;
    @Autowired
    private SysUserRoleMapper sysUserRoleMapper;

    @SysLogTag(value = "系统用户", operation = "导出用户信息")
    public ResultUtil exportExcel(HttpServletResponse response) {
        String[] heart = {"ID", "用户名", "账号", "密码"};
        List<SysUserVO> list = sysUserMapper.getList(null, 0, 10);
        new ExportExcelUtil<>(response, heart, list, "第一次导出", "wsh");
        return ResultUtil.success("导出成功");
    }

    @SysLogTag(value = "系统用户", operation = "导入用户信息")
    public ResultUtil importExcel(MultipartFile file) {
        List<List<String>> lists = ImportExcelUtil.importExcel(file);
        for (List<String> list : lists) {
            System.out.println(list.get(0));
        }
        return ResultUtil.success("导入成功");

    }

    public ResultUtil getUserNameByUserAmount() {
        String userName = sysUserMapper.getUserNameByUserAmount(SecurityUtils.getSubject().getPrincipal());
        return ResultUtil.success(Strings.isNullOrEmpty(userName) ? Consot.USER_NAME : userName);

    }

    @SysLogTag(value = "系统用户", operation = "修改用户")
    @Transactional
    public ResultUtil eidt(SysUserEntity entity, MultipartFile file) {
        String userId = entity.getId();
        sysUserRoleMapper.delByUserId(userId);
        for (String roleId : entity.getRoleIds()) {
            if (!Strings.isNullOrEmpty(roleId)) {
                sysUserRoleMapper.save(userId, roleId);
            }
        }
        String picture = entity.getPicture();
        String fileName = Utils.UUID() + FileUtil.SUFFIX_JPG;
        entity.setPicture(fileName);
        super.update(entity);
        //删除
        FileUtil.deleteFile(picture);
        FileUtil.uploadPicture(fileName, file);
        return ResultUtil.success("修改成功");
    }

    public ResultUtil loginDataBaseCheck(String userAmount, String userPwd) {
        boolean existUserName = sysUserMapper.isExistUserAmount(userAmount);
        if (existUserName) {
            boolean existUser = sysUserMapper.isExistUser(userAmount, userPwd);
            if (existUser) {
                boolean frozen = sysUserMapper.getFrozenValueByUserName(userAmount);
                if (frozen) {
                    return ResultUtil.failed(1, "账号已经被冻结，请联系管理员!");
                }
                return ResultUtil.success("登录成功!");
            }
            return ResultUtil.failed(1, "用户密码错误!");
        }
        return ResultUtil.failed(1, "用户不存在!");
    }

    @SysLogTag(value = "系统用户", operation = "保存用户")
    @Override
    @Transactional
    public ResultUtil save(SysUserEntity sysUserEntity) {
        String userId = Utils.UUID();
        for (String roleId : sysUserEntity.getRoleIds())
            if (!Strings.isNullOrEmpty(roleId)) {
                sysUserRoleMapper.save(userId, roleId);
            }
        sysUserEntity.setId(userId);
        return super.save(sysUserEntity);
    }

    @SysLogTag(value = "系统用户", operation = "删除用户")
    @Override
    @Transactional
    public ResultUtil del(String[] ids) {
        for (String userId : ids) {
            if (!Strings.isNullOrEmpty(userId)) {
                sysUserRoleMapper.delByUserId(userId);
            }
        }
        return super.del(ids);
    }
}
