package cn.structured.message.repository.mapper;

import cn.structured.message.repository.po.MessageRecordPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageRecordMapper extends BaseMapper<MessageRecordPO> {

    List<MessageRecordPO> selectByBusinessId(@Param("businessId") String businessId);

    List<MessageRecordPO> selectByChannelId(@Param("channelId") Long channelId);

    List<MessageRecordPO> selectByStatus(@Param("status") Integer status);

    List<MessageRecordPO> selectByBusinessIdAndChannelId(@Param("businessId") String businessId, @Param("channelId") Long channelId);

    List<MessageRecordPO> selectByReceiver(@Param("receiver") String receiver);

    List<MessageRecordPO> selectPendingMessages();

    long countByStatus(@Param("status") Integer status);
}