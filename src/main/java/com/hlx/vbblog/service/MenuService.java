package com.hlx.vbblog.service;


import com.hlx.vbblog.model.Menu;
import com.hlx.vbblog.vo.InitInfoVO;
import com.hlx.vbblog.vo.MenuCheckboxVO;
import com.hlx.vbblog.vo.MenuSelectVO;

import java.util.List;

public interface MenuService {
    /**
     * 获取菜单树
     *
     * @param userId 用户ID
     * @return 菜单树
     */
    InitInfoVO menu(Long userId);

    /**
     * 查询所有菜单
     *
     * @return 菜单列表
     */
    List<Menu> listAll();

    /**
     * 查询菜单记录数目
     *
     * @return 菜单记录数目
     */
    Long countAll();

    /**
     * 查询菜单树(单选)
     *
     * @return 菜单树
     */
    List<MenuSelectVO> listBySelectTree();

    /**
     * 保存或者更新菜单
     *
     * @param menu 菜单
     */
    void saveOfUpdate(Menu menu);

    /**
     * 根据ID查询菜单
     *
     * @param id 菜单ID
     * @return 菜单
     */
    Menu getById(Long id);

    /**
     * 根据ID删除菜单
     *
     * @param id 菜单ID
     */
    void removeById(Long id);

    /**
     * 查询菜单树（复选）
     *
     * @return 菜单树
     */
    List<MenuCheckboxVO> listByCheckboxTree();
}
