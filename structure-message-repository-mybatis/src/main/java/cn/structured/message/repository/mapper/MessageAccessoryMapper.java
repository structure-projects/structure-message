package cn.structured.message.repository.mapper;

import cn.structured.message.repository.po.MessageAccessoryPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface MessageAccessoryMapper extends BaseMapper<MessageAccessoryPO> {

    List<MessageAccessoryPO> selectByMessageId(@Param("messageId") Long messageId);

    int deleteByMessageId(@Param("messageId") Long messageId);
}