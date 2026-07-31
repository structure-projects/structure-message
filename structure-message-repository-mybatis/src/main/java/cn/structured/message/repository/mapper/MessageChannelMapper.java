package cn.structured.message.repository.mapper;

import cn.structured.message.repository.po.MessageChannelPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageChannelMapper extends BaseMapper<MessageChannelPO> {

    List<MessageChannelPO> selectByStatus(@Param("status") Integer status);

    List<MessageChannelPO> selectByChannelType(@Param("channelType") String channelType);

    MessageChannelPO selectByChannelCode(@Param("channelCode") String channelCode);

    int countByChannelCode(@Param("channelCode") String channelCode);

    int countByChannelCodeAndNeId(@Param("channelCode") String channelCode, @Param("excludeId") Long excludeId);
}