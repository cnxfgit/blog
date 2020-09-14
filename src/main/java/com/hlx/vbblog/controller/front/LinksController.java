package com.hlx.vbblog.controller.front;

import com.hlx.vbblog.service.LinkService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Api(tags = "前台：友链页面")
@RestController
@RequestMapping("/links")
public class LinksController {

    @Autowired
    private LinkService linkService;

    @ApiOperation("查询友链")
    @GetMapping
    public ResponseEntity<Object> listByPage(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                             @RequestParam(value = "size", defaultValue = "15") Integer size) {
        return new ResponseEntity<>(linkService.listByPage(current, size), HttpStatus.OK);
    }

}
