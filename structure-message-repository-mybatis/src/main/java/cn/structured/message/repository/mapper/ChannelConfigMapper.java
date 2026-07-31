package cn.structured.message.repository.mapper;

import cn.structured.message.repository.po.OrgChannelConfigPO;
import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface ChannelConfigMapper extends BaseMapper<OrgChannelConfigPO> {

    List<OrgChannelConfigPO> selectByOrgId(@Param("orgId") Long orgId);

    List<OrgChannelConfigPO> selectByChannelId(@Param("channelId") Long channelId);

    List<OrgChannelConfigPO> selectByOrgIdAndChannelId(@Param("orgId") Long orgId, @Param("channelId") Long channelId);

    OrgChannelConfigPO selectByOrgIdAndChannelIdAndConfigName(@Param("orgId") Long orgId, @Param("channelId") Long channelId, @Param("configName") String configName);

    OrgChannelConfigPO selectByOrgIdAndChannelIdAndIsDefault(@Param("orgId") Long orgId, @Param("channelId") Long channelId, @Param("isDefault") Integer isDefault);

    List<OrgChannelConfigPO> selectByStatus(@Param("status") Integer status);

    int countByOrgIdAndChannelIdAndConfigName(@Param("orgId") Long orgId, @Param("channelId") Long channelId, @Param("configName") String configName);
}