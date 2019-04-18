package amb.sponge.plugin.core;

import amb.sponge.plugin.command.DelTeleporterCmd;
import amb.sponge.plugin.command.SetTeleporterCmd;
import amb.sponge.plugin.listeners.PlayerActionListener;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.command.CommandManager;
import org.spongepowered.api.command.args.GenericArguments;
import org.spongepowered.api.command.spec.CommandSpec;
import org.spongepowered.api.config.ConfigDir;
import org.spongepowered.api.config.DefaultConfig;
import org.spongepowered.api.event.EventManager;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.game.GameReloadEvent;
import org.spongepowered.api.event.game.state.GameInitializationEvent;
import org.spongepowered.api.event.game.state.GamePreInitializationEvent;
import org.spongepowered.api.plugin.Plugin;
import org.spongepowered.api.text.Text;

import javax.inject.Inject;
import java.io.File;
import java.util.logging.Logger;

@Plugin(id = "ambtpbook", name = "Amb Server Transport Book Plugin", version = "1.0", description = "transport plugin for private server")
public class PluginCore {

    public static PluginCore instance;

    @Inject
    private Logger logger;

    @Inject
    @ConfigDir(sharedRoot = false)
    private File folder;

    @Inject
    @DefaultConfig(sharedRoot = false)
    private ConfigurationLoader<CommentedConfigurationNode> loader;

    /**
     * 服务器准备初始化
     * @param event
     */
    @Listener
    public void onServerPreInit(GamePreInitializationEvent event){
        // 加载配置文件
        Config.setup(folder);
        Config.load();
        logger.info("[TPBook]--配置文件加载完成");
    }

    /**
     * 服务器初始化
     * @param event
     */
    @Listener
    public void onServerInit(GameInitializationEvent event){
        instance = this;
        // 注册插件监听器
        EventManager eventManager = Sponge.getEventManager();
        eventManager.registerListeners(this, new PlayerActionListener());
        // 注册插件命令
        CommandManager commandManager = Sponge.getCommandManager();

        CommandSpec commandSpec1 = CommandSpec.builder()
                .description(Text.of("设置当前位置为公共传送点"))
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("地点名称"))))
                .permission("minecraft.command.op")
                .executor(new SetTeleporterCmd()).build();
        commandManager.register(this, commandSpec1, "tpbset");
        CommandSpec commandSpec2 = CommandSpec.builder()
                .description(Text.of("删除指定编号的公共传送点"))
                .arguments(GenericArguments.onlyOne(GenericArguments.string(Text.of("地点id"))))
                .permission("minecraft.command.op")
                .executor(new DelTeleporterCmd()).build();
        commandManager.register(this, commandSpec2, "tpbdel");
        /*CommandSpec commandSpec3 = CommandSpec.builder()
                .description(Text.of("是否运行其他玩家传送到自己"))
                .arguments(GenericArguments.onlyOne(GenericArguments.bool(Text.of("0=不允许|1=允许"))))
                .executor(new AllowBeTpCmd()).build();
        commandManager.register(this, commandSpec3, "tpb");

        CommandSpec commandSpec4 = CommandSpec.builder()
                .description(Text.of("打开传送书界面"))
                .permission("minecraft.command.op")
                .executor(new OpenTPBookCmd()).build();
        commandManager.register(this, commandSpec4, "opentpbook");*/

        logger.info("[TPBook]--注册命令完成");
    }
    /**
     * 服务器初始化
     * @param event
     */
    @Listener
    public void onServerReload(GameReloadEvent event){
        Config.load();
        logger.info("[TPBook]--配置文件加载完成");
    }
}
