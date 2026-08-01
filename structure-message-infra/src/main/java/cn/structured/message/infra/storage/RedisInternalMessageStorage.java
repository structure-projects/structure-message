package cn.structured.message.infra.storage;

import cn.structured.message.domain.entity.InternalMessage;
import com.alibaba.fastjson2.JSON;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.stereotype.Component;

import java.util.List;
import java.util.concurrent.TimeUnit;

/**
 * Redis内部消息存储实现类
 * <p>
 * 使用Redis存储内部消息，实现消息的缓存和快速查询。
 * </p>
 */
@Slf4j
@Component
public class RedisInternalMessageStorage {

    /**
     * Redis key前缀
     */
    private static final String MESSAGE_KEY_PREFIX = "message:internal:";

    /**
     * Redis模板
     */
    private final StringRedisTemplate redisTemplate;

    /**
     * 构造函数
     *
     * @param redisTemplate Redis模板
     */
    public RedisInternalMessageStorage(StringRedisTemplate redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    /**
     * 保存内部消息到Redis
     * <p>
     * 将消息对象序列化为JSON字符串存储到Redis，设置过期时间为24小时。
     * </p>
     *
     * @param message 内部消息实体
     */
    public void save(InternalMessage message) {
        if (message == null || message.getId() == null) {
            log.warn("保存内部消息: message或message.id为空");
            return;
        }
        String key = MESSAGE_KEY_PREFIX + message.getId();
        redisTemplate.opsForValue().set(key, JSON.toJSONString(message), 24, TimeUnit.HOURS);
    }

    /**
     * 从Redis获取内部消息
     * <p>
     * 根据消息ID从Redis中获取消息并反序列化为实体对象。
     * </p>
     *
     * @param id 消息ID
     * @return 内部消息实体，如果不存在则返回null
     */
    public InternalMessage get(Long id) {
        if (id == null) {
            return null;
        }
        String key = MESSAGE_KEY_PREFIX + id;
        String json = redisTemplate.opsForValue().get(key);
        return json != null ? JSON.parseObject(json, InternalMessage.class) : null;
    }

    /**
     * 从Redis删除内部消息
     * <p>
     * 根据消息ID删除Redis中的消息。
     * </p>
     *
     * @param id 消息ID
     */
    public void delete(Long id) {
        if (id == null) {
            return;
        }
        String key = MESSAGE_KEY_PREFIX + id;
        redisTemplate.delete(key);
    }

    /**
     * 根据接收人查询消息列表
     * <p>
     * 预留方法，暂未实现。
     * </p>
     *
     * @param receiver 接收人ID
     * @return 消息列表
     */
    public List<InternalMessage> findByReceiver(String receiver) {
        return List.of();
    }
}