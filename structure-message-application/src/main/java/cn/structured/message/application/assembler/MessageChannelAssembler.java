package cn.structured.message.application.assembler;

import cn.structured.message.common.dto.MessageChannelDTO;
import cn.structured.message.common.vo.MessageChannelVO;
import cn.structured.message.domain.entity.MessageChannel;

/**
 * 消息通道组装器
 * <p>
 * 负责消息通道领域实体与DTO、VO之间的转换。
 * </p>
 */
public class MessageChannelAssembler {

    /**
     * 将DTO转换为领域实体
     *
     * @param dto 消息通道DTO
     * @return 消息通道领域实体
     */
    public static MessageChannel toEntity(MessageChannelDTO dto) {
        if (dto == null) {
            return null;
        }
        MessageChannel channel = MessageChannel.create(
                dto.getChannelCode(),
                dto.getChannelName(),
                dto.getChannelType(),
                dto.getPluginClass()
        );
        // 设置状态
        Integer status = dto.getStatus();
        if (status != null) {
            if (status == 1) {
                channel.enable();
            } else {
                channel.disable();
            }
        }
        return channel;
    }

    /**
     * 将领域实体转换为VO
     *
     * @param entity 消息通道领域实体
     * @return 消息通道VO
     */
    public static MessageChannelVO toVO(MessageChannel entity) {
        if (entity == null) {
            return null;
        }
        MessageChannelVO vo = new MessageChannelVO();
        vo.setId(entity.getId());
        vo.setChannelCode(entity.getChannelCode());
        vo.setChannelName(entity.getChannelName());
        vo.setChannelType(entity.getChannelType());
        vo.setPluginClass(entity.getPluginClass());
        vo.setStatus(entity.getStatus() != null ? entity.getStatus().getCode() : null);
        vo.setCreateTime(entity.getCreateTime());
        vo.setUpdateTime(entity.getUpdateTime());
        return vo;
    }
}