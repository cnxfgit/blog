package com.hlx.vbblog.controller.admin;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.anntation.OperationLog;
import com.hlx.vbblog.common.JsonResult;
import com.hlx.vbblog.common.TableResult;
import com.hlx.vbblog.model.AccessLog;
import com.hlx.vbblog.query.LogQuery;
import com.hlx.vbblog.service.AccessLogService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;
import java.util.List;


@Api(tags = "后台：访问日志管理")
@RestController
@RequestMapping("/admin/access-log")
public class AccessLogController {

    @Autowired
    private AccessLogService accessLogService;

    @ApiOperation("查询访问日志")
    @PreAuthorize("hasAuthority('sys:accesslog:query')")
    @OperationLog("查询访问日志")
    @GetMapping
    public TableResult listByPage(@RequestParam(value = "page", defaultValue = "1") Integer page,
                                  @RequestParam(value = "limit", defaultValue = "15") Integer limit,
                                  LogQuery logQuery) {
        Page<AccessLog> pageInfo = accessLogService.listByPage(page, limit, logQuery);
        return TableResult.tableOk(pageInfo.getRecords(), pageInfo.getTotal());
    }

    @ApiOperation("删除访问日志")
    @PreAuthorize("hasAuthority('sys:accesslog:delete')")
    @OperationLog("删除访问日志")
    @DeleteMapping("/{id}")
    public JsonResult removeById(@NotNull @PathVariable("id") Long id) {
        accessLogService.remove(id);
        return JsonResult.ok();
    }

    @ApiOperation("批量删除访问日志")
    @PreAuthorize("hasAuthority('sys:accesslog:delete')")
    @OperationLog("批量删除访问日志")
    @DeleteMapping
    public JsonResult removeBatch(@NotEmpty @RequestBody List<Long> idList) {
        accessLogService.removeByIdList(idList);
        return JsonResult.ok();
    }
}
