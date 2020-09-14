package com.hlx.vbblog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.JsonResult;
import com.hlx.vbblog.common.TableResult;
import com.hlx.vbblog.model.OperationLog;
import com.hlx.vbblog.query.LogQuery;
import com.hlx.vbblog.service.OperationLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Api(tags = "后台：操作日志管理")
@RestController
@RequestMapping("/admin/operation-log")
public class OperationLogController {

    @Autowired
    private OperationLogService operationLogService;

    @ApiOperation("查询操作日志")
    @PreAuthorize("hasAuthority('sys:operationlog:query')")
    @com.hlx.vbblog.anntation.OperationLog("查询操作日志")
    @GetMapping
    public TableResult listByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                  @RequestParam(value = "limit", defaultValue = "15") Integer limit,
                                  LogQuery logQuery) {
        Page<OperationLog> pageInfo = operationLogService.listByPage(page, limit, logQuery);
        return TableResult.tableOk(pageInfo.getRecords(), pageInfo.getTotal());
    }

    @ApiOperation("删除操作日志")
    @PreAuthorize("hasAuthority('sys:operationlog:delete')")
    @com.hlx.vbblog.anntation.OperationLog("删除操作日志")
    @DeleteMapping("/{id}")
    public JsonResult removeById(@NotNull @PathVariable("id") Long id) {
        operationLogService.remove(id);
        return JsonResult.ok();
    }

    @ApiOperation("批量删除操作日志")
    @PreAuthorize("hasAuthority('sys:operationlog:delete')")
    @com.hlx.vbblog.anntation.OperationLog("批量删除操作日志")
    @DeleteMapping
    public JsonResult removeBatch(@NotEmpty @RequestBody List<Long> idList) {
        operationLogService.removeByIdList(idList);
        return JsonResult.ok();
    }
}
