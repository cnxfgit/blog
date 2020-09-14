package com.hlx.vbblog.controller.front;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.hlx.vbblog.common.Constant;
import com.hlx.vbblog.common.JsonResult;
import com.hlx.vbblog.model.Message;
import com.hlx.vbblog.service.MessageService;
import com.hlx.vbblog.utils.StringUtils;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.util.Date;

@Api(tags = "前台：留言管理")
@RestController
@RequestMapping("/messages")
public class MessagesController {

    @Autowired
    private MessageService messageService;

    @ApiOperation("查询留言")
    @GetMapping
    public ResponseEntity<Object> listByPage(@RequestParam(value = "current", defaultValue = "1") Integer current,
                                             @RequestParam(value = "size", defaultValue = Constant.MESSAGES_PAGE_SIZE) Integer size) {
        Page<Message> pageInfo = messageService.listByPage(current, size);
        return new ResponseEntity<>(pageInfo, HttpStatus.OK);
    }

    @ApiOperation("新增留言")
    @PostMapping
    public JsonResult save(@Validated @RequestBody Message message, HttpServletRequest request) {
        if(message.getNickname()==null||"".equals(message.getNickname())){
            message.setNickname("匿名");
        }
        message.setCreateTime(new Date());
        message.setAvatar(Constant.DEFAULT_AVATAR);
        message.setBrowser(StringUtils.getBrowser(request));
        message.setOs(StringUtils.getClientOS(request));
        message.setRequestIp(StringUtils.getIp(request));
        message.setAddress(StringUtils.getCityInfo(message.getRequestIp()));
        messageService.save(message);
        return JsonResult.ok();
    }
}
