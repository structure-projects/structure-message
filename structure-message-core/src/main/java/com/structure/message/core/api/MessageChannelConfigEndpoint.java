package com.structure.message.core.api;

import cn.structure.common.entity.ResResultVO;
import cn.structure.common.utils.ResultUtilSimpleImpl;
import cn.structure.common.vo.ResPage;
import cn.structured.mybatis.plus.starter.convert.ResPageConvert;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.Wrappers;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.structure.message.core.domain.assembler.OrgChannelConfigAssembler;
import com.structure.message.core.domain.dto.ChannelConfigDTO;
import com.structure.message.core.domain.entity.ChannelConfigEntity;
import com.structure.message.core.domain.vo.OrgChannelConfigVO;
import com.structure.message.core.service.ChannelConfigService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import javax.annotation.Resource;

/**
 * 通道配置管理
 *
 * @author chuck
 * @version 2024/07/19 下午11:40
 * @since 1.8
 */
@Api(tags = "通道配置管理")
@RestController
@RequestMapping(value = "/api/channel-config")
public class MessageChannelConfigEndpoint {

    @Resource
    private ChannelConfigService service;

    @ApiOperation(value = "保存配置")
    @PostMapping(value = "/")
    public ResResultVO<Long> save(@RequestBody @Validated ChannelConfigDTO create) {
        ChannelConfigEntity config = OrgChannelConfigAssembler.assembler(create);
        service.save(config);
        return ResultUtilSimpleImpl.success(config.getId());
    }

    @ApiOperation(value = "修改配置")
    @PutMapping(value = "/{id}")
    public ResResultVO<Long> update(@RequestBody @Validated ChannelConfigDTO create,
                                    @ApiParam(value = "配置ID", example = "1", required = true)
                                    @PathVariable(value = "id") Long id) {
        ChannelConfigEntity config = OrgChannelConfigAssembler.assembler(create);
        config.setId(id);
        service.updateById(config);
        return ResultUtilSimpleImpl.success(config.getId());
    }

    @ApiOperation(value = "配置列表")
    @GetMapping(value = "/{page}/{pageSize}/page")
    public ResResultVO<ResPage<OrgChannelConfigVO>> page(@ApiParam(value = "组织ID", example = "1") @RequestParam(required = false) Long orgId,
                                                         @ApiParam(value = "通道ID", example = "1") @RequestParam(required = false) Long channelId,
                                                         @ApiParam(value = "配置键", example = "smtp.host") @RequestParam(required = false) String configKey,
                                                         @ApiParam(value = "页码", example = "1") @PathVariable(value = "page") Long page,
                                                         @ApiParam(value = "页大小", example = "10") @PathVariable(value = "pageSize") Long pageSize) {
        LambdaQueryWrapper<ChannelConfigEntity> queryWrapper = Wrappers.<ChannelConfigEntity>lambdaQuery()
                .eq(orgId != null, ChannelConfigEntity::getOrgId, orgId)
                .eq(channelId != null, ChannelConfigEntity::getChannelId, channelId)
                .orderByDesc(ChannelConfigEntity::getCreateTime);

        Page<ChannelConfigEntity> pageResult = service.page(new Page<>(page, pageSize), queryWrapper);

        ResPage<OrgChannelConfigVO> resPage = ResPageConvert.convert(pageResult, OrgChannelConfigAssembler::assembler);

        return ResultUtilSimpleImpl.success(resPage);
    }

    @ApiOperation(value = "查看配置详情")
    @GetMapping(value = "/{id}")
    public ResResultVO<OrgChannelConfigVO> get(@ApiParam(value = "配置ID", example = "1")
                                               @PathVariable("id") Long id) {
        ChannelConfigEntity config = service.getById(id);
        return ResultUtilSimpleImpl.success(OrgChannelConfigAssembler.assembler(config));
    }


    @ApiOperation(value = "启用配置")
    @PostMapping(value = "/enable/{configId}")
    public ResResultVO<Void> enable(@ApiParam(value = "配置ID", example = "1")
                                    @PathVariable("configId") Long configId) {
        service.enableConfig(configId);
        return ResultUtilSimpleImpl.success(null);
    }

    @ApiOperation(value = "禁用配置")
    @PostMapping(value = "/disable/{configId}")
    public ResResultVO<Void> disable(@ApiParam(value = "配置ID", example = "1")
                                     @PathVariable("configId") Long configId) {
        service.disableConfig(configId);
        return ResultUtilSimpleImpl.success(null);
    }

    @ApiOperation(value = "重新加载配置")
    @PostMapping(value = "/reload")
    public ResResultVO<Void> reload(@ApiParam(value = "组织ID", example = "1") @RequestParam Long orgId,
                                    @ApiParam(value = "通道ID", example = "1") @RequestParam Long channelId) {
        service.reloadConfig(orgId, channelId);
        return ResultUtilSimpleImpl.success(null);
    }

}
