package cn.structured.message.repository.mapper;

import cn.structured.message.repository.po.InternalMessagePO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InternalMessageMapper extends BaseMapper<InternalMessagePO> {

    List<InternalMessagePO> selectByReceiver(@Param("receiver") String receiver);

    List<InternalMessagePO> selectByReceiverAndState(@Param("receiver") String receiver, @Param("state") Integer state);

    List<InternalMessagePO> selectByOrgId(@Param("orgId") Long orgId);

    long countByReceiverAndState(@Param("receiver") String receiver, @Param("state") Integer state);
}