package com.hlx.vbblog.controller.admin;

import com.hlx.vbblog.anntation.OperationLog;
import com.hlx.vbblog.common.JsonResult;
import com.hlx.vbblog.common.TableResult;
import com.hlx.vbblog.model.Menu;
import com.hlx.vbblog.service.MenuService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotNull;
import java.util.Date;

@Api(tags = "后台：菜单管理")
@RestController
@RequestMapping("/admin/menu")
public class MenuController {

    @Autowired
    private MenuService menuService;

    @ApiOperation("查询菜单")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    @OperationLog("查询菜单")
    @GetMapping
    public TableResult listAll() {
        return TableResult.tableOk(menuService.listAll(), menuService.countAll());
    }

    @ApiOperation("查询单选菜单")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    @GetMapping("/radio-tree")
    public JsonResult listByRadioTree() {
        return JsonResult.ok(menuService.listBySelectTree());
    }

    @ApiOperation("查询多选菜单")
    @PreAuthorize("hasAuthority('sys:menu:query')")
    @GetMapping("/checkbox-tree")
    public JsonResult listByCheckboxTree() {
        return JsonResult.ok(menuService.listByCheckboxTree());
    }

    @ApiOperation("新增菜单")
    @PreAuthorize("hasAuthority('sys:menu:add')")
    @OperationLog("新增菜单")
    @PostMapping
    public JsonResult save(@Validated @RequestBody Menu menu) {
        menu.setStatus(menu.getStatus() != null);
        menu.setCreateTime(new Date());
        menu.setUpdateTime(menu.getCreateTime());
        menuService.saveOfUpdate(menu);
        return JsonResult.ok();
    }

    @ApiOperation("更新菜单")
    @PreAuthorize("hasAuthority('sys:menu:edit')")
    @OperationLog("更新菜单")
    @PutMapping
    public JsonResult update(@Validated @RequestBody Menu menu) {
        menu.setStatus(menu.getStatus() != null);
        menu.setUpdateTime(new Date());
        menuService.saveOfUpdate(menu);
        return JsonResult.ok();
    }

    @ApiOperation("删除菜单")
    @PreAuthorize("hasAuthority('sys:menu:delete')")
    @OperationLog("删除菜单")
    @DeleteMapping("/{id}")
    public JsonResult remove(@NotNull @PathVariable("id") Long id) {
        menuService.removeById(id);
        return JsonResult.ok();
    }
}
