package com.structure.message.core.api;

import cn.structure.common.entity.ResResultVO;
import cn.structure.common.utils.ResultUtilSimpleImpl;
import cn.structure.common.vo.ResPage;
import cn.structured.mybatis.plus.starter.convert.ResPageConvert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.structure.message.core.domain.assembler.MessageChannelAssembler;
import com.structure.message.core.domain.dto.MessageChannelDTO;
import com.structure.message.core.domain.entity.MessageChannelEntity;
import com.structure.message.core.domain.vo.MessageChannelVO;
import com.structure.message.core.service.MessageChannelService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.AllArgsConstructor;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.stream.Collectors;

@Api(tags = "消息通道管理")
@RestController
@RequestMapping(value = "/api/message-channel")
@AllArgsConstructor
public class MessageChannelEndpoint {

    private final MessageChannelService messageChannelService;

    @ApiOperation(value = "保存消息通道")
    @PostMapping(value = "/")
    public ResResultVO<Long> save(@RequestBody @Validated MessageChannelDTO create) {
        MessageChannelEntity entity = MessageChannelAssembler.assembler(create);
        messageChannelService.save(entity);
        return ResultUtilSimpleImpl.success(entity.getId());
    }

    @ApiOperation(value = "更新消息通道")
    @PutMapping(value = "/{id}")
    public ResResultVO<Void> update(@ApiParam(value = "通道ID", example = "1") @PathVariable("id") Long id,
                                    @RequestBody @Validated MessageChannelDTO update) {
        MessageChannelEntity entity = MessageChannelAssembler.assembler(update);
        entity.setId(id);
        messageChannelService.updateById(entity);
        return ResultUtilSimpleImpl.success(null);
    }

    @ApiOperation(value = "删除消息通道")
    @DeleteMapping(value = "/{id}")
    public ResResultVO<Void> delete(@ApiParam(value = "通道ID", example = "1") @PathVariable("id") Long id) {
        messageChannelService.removeById(id);
        return ResultUtilSimpleImpl.success(null);
    }

    @ApiOperation(value = "查看消息通道详情")
    @GetMapping(value = "/{id}")
    public ResResultVO<MessageChannelVO> get(@ApiParam(value = "通道ID", example = "1")
                                             @PathVariable("id") Long id) {
        MessageChannelEntity entity = messageChannelService.getById(id);
        return ResultUtilSimpleImpl.success(MessageChannelAssembler.assembler(entity));
    }

    @ApiOperation(value = "消息通道列表")
    @GetMapping(value = "/{page}/{pageSize}/page")
    public ResResultVO<ResPage<MessageChannelVO>> page(@ApiParam(value = "通道编码", example = "EMAIL") @RequestParam(required = false) String channelCode,
                                                       @ApiParam(value = "通道名称", example = "邮件通道") @RequestParam(required = false) String channelName,
                                                       @ApiParam(value = "通道类型", example = "EMAIL") @RequestParam(required = false) String channelType,
                                                       @ApiParam(value = "状态", example = "1") @RequestParam(required = false) Integer status,
                                                       @ApiParam(value = "页码", example = "1") @PathVariable(value = "page") Long page,
                                                       @ApiParam(value = "页大小", example = "10") @PathVariable(value = "pageSize") Long pageSize) {
        LambdaQueryWrapper<MessageChannelEntity> queryWrapper = Wrappers.<MessageChannelEntity>lambdaQuery()
                .like(channelCode != null, MessageChannelEntity::getChannelCode, channelCode)
                .like(channelName != null, MessageChannelEntity::getChannelName, channelName)
                .eq(channelType != null, MessageChannelEntity::getChannelType, channelType)
                .eq(status != null, MessageChannelEntity::getStatus, status)
                .orderByDesc(MessageChannelEntity::getCreateTime);

        Page<MessageChannelEntity> pageResult = messageChannelService.page(new Page<>(page, pageSize), queryWrapper);

        ResPage<MessageChannelVO> resPage = ResPageConvert.convert(pageResult, MessageChannelAssembler::assembler);

        return ResultUtilSimpleImpl.success(resPage);
    }

    @ApiOperation("下拉列表")
    @GetMapping("/option")
    public ResResultVO<List<MessageChannelVO>> option() {
        List<MessageChannelEntity> list = messageChannelService.list(Wrappers.<MessageChannelEntity>lambdaQuery()
                .eq(MessageChannelEntity::getStatus, 1)
                .select(
                        MessageChannelEntity::getId,
                        MessageChannelEntity::getChannelCode,
                        MessageChannelEntity::getChannelName,
                        MessageChannelEntity::getChannelType
                ));
        List<MessageChannelVO> result = list.stream().map(MessageChannelAssembler::assembler).collect(Collectors.toList());
        return ResultUtilSimpleImpl.success(result);
    }
}
