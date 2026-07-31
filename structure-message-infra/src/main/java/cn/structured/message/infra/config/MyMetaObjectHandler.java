package cn.structured.message.infra.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

/**
 * MyBatis Plus自动填充处理器
 * <p>
 * 实现MetaObjectHandler接口，在插入和更新操作时自动填充创建时间和更新时间字段。
 * </p>
 */
@Slf4j
@Component
public class MyMetaObjectHandler implements MetaObjectHandler {

    /**
     * 插入操作时自动填充字段
     * <p>
     * 在实体插入时，自动设置createTime和updateTime为当前时间。
     * </p>
     *
     * @param metaObject MyBatis元对象
     */
    @Override
    public void insertFill(MetaObject metaObject) {
        LocalDateTime now = LocalDateTime.now();
        strictInsertFill(metaObject, "createTime", LocalDateTime.class, now);
        strictInsertFill(metaObject, "updateTime", LocalDateTime.class, now);
    }

    /**
     * 更新操作时自动填充字段
     * <p>
     * 在实体更新时，自动设置updateTime为当前时间。
     * </p>
     *
     * @param metaObject MyBatis元对象
     */
    @Override
    public void updateFill(MetaObject metaObject) {
        strictUpdateFill(metaObject, "updateTime", LocalDateTime.class, LocalDateTime.now());
    }
}