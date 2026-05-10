package com.structure.message.plugin.internal.mapper;

import com.structure.message.plugin.internal.InternalMessageDTO;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface InternalMessageMapper {

    void insert(InternalMessageDTO message);

    void update(InternalMessageDTO message);

    void delete(@Param("id") Long id);

    InternalMessageDTO selectById(@Param("id") Long id);

    List<InternalMessageDTO> selectByUser(
            @Param("userId") String userId,
            @Param("orgId") Long orgId,
            @Param("isRead") Boolean isRead,
            @Param("limit") Integer limit);

    long countUnread(@Param("userId") String userId, @Param("orgId") Long orgId);

    void batchMarkAsRead(@Param("messageIds") List<Long> messageIds);
}