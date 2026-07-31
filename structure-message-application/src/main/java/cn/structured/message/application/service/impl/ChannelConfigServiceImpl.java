package cn.structured.message.application.service.impl;

import cn.structured.message.application.service.ChannelConfigService;
import cn.structured.message.common.exception.MessageException;
import cn.structured.message.domain.entity.ChannelConfig;
import cn.structured.message.domain.repository.ChannelConfigRepository;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * 通道配置服务实现类
 * <p>
 * 实现ChannelConfigService接口，处理通道配置的业务逻辑。
 * </p>
 */
@Slf4j
@Service
@AllArgsConstructor
public class ChannelConfigServiceImpl implements ChannelConfigService {

    /**
     * 通道配置仓储
     */
    private final ChannelConfigRepository channelConfigRepository;

    /**
     * 创建通道配置
     * <p>
     * 验证配置唯一性，设置默认状态为启用，保存配置信息。
     * </p>
     *
     * @param config 通道配置实体
     * @return 创建后的通道配置实体
     */
    @Override
    @Transactional
    public ChannelConfig create(ChannelConfig config) {
        if (channelConfigRepository.existsByOrgIdAndChannelIdAndConfigName(
                config.getOrgId(), config.getChannelId(), config.getConfigName())) {
            throw new MessageException("CONFIG_EXISTS", 
                    "配置已存在: orgId=" + config.getOrgId() + ", channelId=" + config.getChannelId() + ", configName=" + config.getConfigName());
        }
        if (config.getStatus() == null) {
            config.enable();
        }
        if (config.getIsDefault() == null) {
            config.unsetAsDefault();
        }
        log.info("创建通道配置: orgId={}, channelId={}, configName={}", config.getOrgId(), config.getChannelId(), config.getConfigName());
        return channelConfigRepository.save(config);
    }

    /**
     * 更新通道配置
     * <p>
     * 验证配置存在性，更新配置信息。
     * </p>
     *
     * @param id     配置ID
     * @param config 通道配置实体
     * @return 更新后的通道配置实体
     */
    @Override
    @Transactional
    public ChannelConfig update(Long id, ChannelConfig config) {
        ChannelConfig existing = channelConfigRepository.findById(id);
        if (existing == null) {
            throw new MessageException("CONFIG_NOT_FOUND", "配置不存在: " + id);
        }

        existing.updateConfigValue(config.getConfigValue());
        if (config.getStatus() != null) {
            if (config.getStatus() == 1) {
                existing.enable();
            } else {
                existing.disable();
            }
        }
        if (config.getIsDefault() != null) {
            if (config.getIsDefault() == 1) {
                existing.setAsDefault();
            } else {
                existing.unsetAsDefault();
            }
        }
        log.info("更新通道配置: id={}", id);
        return channelConfigRepository.save(existing);
    }

    /**
     * 删除通道配置
     * <p>
     * 验证配置存在性，删除配置信息。
     * </p>
     *
     * @param id 配置ID
     */
    @Override
    @Transactional
    public void delete(Long id) {
        if (channelConfigRepository.findById(id) == null) {
            throw new MessageException("CONFIG_NOT_FOUND", "配置不存在: " + id);
        }
        log.info("删除通道配置: id={}", id);
        channelConfigRepository.removeById(id);
    }

    /**
     * 根据ID查询通道配置
     *
     * @param id 配置ID
     * @return 通道配置实体
     */
    @Override
    public ChannelConfig findById(Long id) {
        ChannelConfig config = channelConfigRepository.findById(id);
        if (config == null) {
            throw new MessageException("CONFIG_NOT_FOUND", "配置不存在: " + id);
        }
        return config;
    }

    /**
     * 根据机构ID查询配置列表
     *
     * @param orgId 机构ID
     * @return 通道配置列表
     */
    @Override
    public List<ChannelConfig> findByOrgId(Long orgId) {
        return channelConfigRepository.findByOrgId(orgId);
    }

    /**
     * 根据通道ID查询配置列表
     *
     * @param channelId 通道ID
     * @return 通道配置列表
     */
    @Override
    public List<ChannelConfig> findByChannelId(Long channelId) {
        return channelConfigRepository.findByChannelId(channelId);
    }

    /**
     * 根据机构ID和通道ID查询配置列表
     *
     * @param orgId     机构ID
     * @param channelId 通道ID
     * @return 通道配置列表
     */
    @Override
    public List<ChannelConfig> findByOrgIdAndChannelId(Long orgId, Long channelId) {
        return channelConfigRepository.findByOrgIdAndChannelId(orgId, channelId);
    }

    /**
     * 根据机构ID、通道ID和配置名称查询配置
     *
     * @param orgId       机构ID
     * @param channelId   通道ID
     * @param configName  配置名称
     * @return 通道配置实体，如果不存在则返回null
     */
    @Override
    public ChannelConfig findByOrgIdAndChannelIdAndConfigName(Long orgId, Long channelId, String configName) {
        return channelConfigRepository.findByOrgIdAndChannelIdAndConfigName(orgId, channelId, configName)
                .orElse(null);
    }

    /**
     * 根据机构ID和通道ID查询默认配置
     *
     * @param orgId     机构ID
     * @param channelId 通道ID
     * @return 默认通道配置实体，如果不存在则返回null
     */
    @Override
    public ChannelConfig findDefaultByOrgIdAndChannelId(Long orgId, Long channelId) {
        return channelConfigRepository.findByOrgIdAndChannelIdAndIsDefault(orgId, channelId, 1)
                .orElse(null);
    }

    /**
     * 启用通道配置
     * <p>
     * 查询配置并调用enable()方法启用。
     * </p>
     *
     * @param id 配置ID
     */
    @Override
    @Transactional
    public void enable(Long id) {
        ChannelConfig config = findById(id);
        config.enable();
        channelConfigRepository.save(config);
        log.info("启用通道配置: id={}", id);
    }

    /**
     * 禁用通道配置
     * <p>
     * 查询配置并调用disable()方法禁用。
     * </p>
     *
     * @param id 配置ID
     */
    @Override
    @Transactional
    public void disable(Long id) {
        ChannelConfig config = findById(id);
        config.disable();
        channelConfigRepository.save(config);
        log.info("禁用通道配置: id={}", id);
    }

    /**
     * 设置为默认配置
     * <p>
     * 查询配置并调用setAsDefault()方法设置为默认配置。
     * </p>
     *
     * @param id 配置ID
     */
    @Override
    @Transactional
    public void setAsDefault(Long id) {
        ChannelConfig config = findById(id);
        config.setAsDefault();
        channelConfigRepository.save(config);
        log.info("设置为默认配置: id={}", id);
    }

    /**
     * 取消默认配置
     * <p>
     * 查询配置并调用unsetAsDefault()方法取消默认配置。
     * </p>
     *
     * @param id 配置ID
     */
    @Override
    @Transactional
    public void unsetAsDefault(Long id) {
        ChannelConfig config = findById(id);
        config.unsetAsDefault();
        channelConfigRepository.save(config);
        log.info("取消默认配置: id={}", id);
    }
}