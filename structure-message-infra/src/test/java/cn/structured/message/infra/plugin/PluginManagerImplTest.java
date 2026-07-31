package cn.structured.message.infra.plugin;

import cn.structured.message.common.model.MessageContext;
import cn.structured.message.common.model.MessageResult;
import cn.structured.message.common.plugin.MessageChannelConfig;
import cn.structured.message.common.plugin.MessageChannelPlugin;
import cn.structured.message.infra.plugin.PluginManagerImpl;
import java.util.List;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;

import static org.junit.jupiter.api.Assertions.*;

/**
 * 插件管理器单元测试
 * <p>
 * 测试插件的注册、获取、注销和启用状态判断逻辑
 * </p>
 */
class PluginManagerImplTest {

    private PluginManagerImpl pluginManager;

    @Mock
    private MessageChannelPlugin mockPlugin;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
        pluginManager = new PluginManagerImpl();
    }

    @Test
    void registerPlugin_shouldAddPluginToCache() {
        pluginManager.registerPlugin("SMS_ALIYUN", mockPlugin);

        assertTrue(pluginManager.isPluginEnabled("SMS_ALIYUN", 100L, "default"));
    }

    @Test
    void getPlugin_withRegisteredPlugin_shouldReturnPlugin() {
        pluginManager.registerPlugin("SMS_ALIYUN", mockPlugin);

        MessageChannelPlugin result = pluginManager.getPlugin("SMS_ALIYUN", 100L, "default");

        assertNotNull(result);
        assertSame(mockPlugin, result);
    }

    @Test
    void getPlugin_withUnregisteredPlugin_shouldReturnNull() {
        MessageChannelPlugin result = pluginManager.getPlugin("UNKNOWN_CHANNEL", 100L, "default");

        assertNull(result);
    }

    @Test
    void isPluginEnabled_withRegisteredPlugin_shouldReturnTrue() {
        pluginManager.registerPlugin("SMS_ALIYUN", mockPlugin);

        assertTrue(pluginManager.isPluginEnabled("SMS_ALIYUN", 100L, "default"));
    }

    @Test
    void isPluginEnabled_withUnregisteredPlugin_shouldReturnFalse() {
        assertFalse(pluginManager.isPluginEnabled("UNKNOWN_CHANNEL", 100L, "default"));
    }

    @Test
    void unregisterPlugin_shouldRemovePluginFromCache() {
        pluginManager.registerPlugin("SMS_ALIYUN", mockPlugin);
        
        pluginManager.unregisterPlugin("SMS_ALIYUN");

        assertFalse(pluginManager.isPluginEnabled("SMS_ALIYUN", 100L, "default"));
        assertNull(pluginManager.getPlugin("SMS_ALIYUN", 100L, "default"));
    }

    @Test
    void unregisterPlugin_withUnregisteredPlugin_shouldNotThrowException() {
        assertDoesNotThrow(() -> pluginManager.unregisterPlugin("UNKNOWN_CHANNEL"));
    }

    @Test
    void registerPlugin_withSameChannelCode_shouldOverride() {
        MessageChannelPlugin anotherPlugin = new MessageChannelPlugin() {
            @Override
            public String getChannelCode() { return "SMS_ALIYUN"; }

            @Override
            public String getChannelName() { return "阿里云短信"; }

            @Override
            public ChannelType getChannelType() { return ChannelType.SMS; }

            @Override
            public void initialize(MessageChannelConfig config) {}

            @Override
            public MessageResult send(MessageContext context) {
                return MessageResult.success(1L, "SMS_ALIYUN", "13800138000", "another");
            }

            @Override
            public List<MessageResult> sendBatch(List<MessageContext> contexts) { return null; }

            @Override
            public boolean validate(MessageContext context) { return true; }

            @Override
            public boolean isHealthy() { return true; }

            @Override
            public void destroy() {}
        };

        pluginManager.registerPlugin("SMS_ALIYUN", mockPlugin);
        pluginManager.registerPlugin("SMS_ALIYUN", anotherPlugin);

        MessageChannelPlugin result = pluginManager.getPlugin("SMS_ALIYUN", 100L, "default");
        assertSame(anotherPlugin, result);
    }

    @Test
    void getPlugin_withNullChannelCode_shouldReturnNull() {
        assertNull(pluginManager.getPlugin(null, 100L, "default"));
    }

    @Test
    void isPluginEnabled_withNullChannelCode_shouldReturnFalse() {
        assertFalse(pluginManager.isPluginEnabled(null, 100L, "default"));
    }
}