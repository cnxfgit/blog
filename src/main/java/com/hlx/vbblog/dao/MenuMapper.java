package com.hlx.vbblog.dao;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.hlx.vbblog.model.Menu;
import org.apache.ibatis.annotations.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MenuMapper extends BaseMapper<Menu> {
    /**
     * 根据用户ID查询菜单
     *
     * @param userId 用户ID
     * @return 菜单列表
     */
    List<Menu> listMenuByUserId(@Param("userId") Long userId);

    List<Menu> listPermissionByUserId(@Param("userId") Long userId);
}
