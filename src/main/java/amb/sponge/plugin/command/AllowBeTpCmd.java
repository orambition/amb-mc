package amb.sponge.plugin.command;

import amb.sponge.plugin.service.TeleporterDataService;
import org.spongepowered.api.command.CommandException;
import org.spongepowered.api.command.CommandResult;
import org.spongepowered.api.command.CommandSource;
import org.spongepowered.api.command.args.CommandContext;
import org.spongepowered.api.command.source.CommandBlockSource;
import org.spongepowered.api.command.source.ConsoleSource;
import org.spongepowered.api.command.spec.CommandExecutor;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;

/**
 * 是否开启其他玩家传送到此处
 */
public class AllowBeTpCmd implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player){
            // 传送到地点
            TeleporterDataService.savePlayerData((Player)src,args.<Boolean>getOne("0=不允许|1=允许").get().booleanValue(), null);
            src.sendMessage(Text.of("设置开关为:"+args.<Boolean>getOne("0=不允许|1=允许").get().booleanValue()));
        }else if (src instanceof ConsoleSource){
            src.sendMessage(Text.of("控制台无法执行该命令"));
        }else if (src instanceof CommandBlockSource){
            src.sendMessage(Text.of("不支持命令方块执行该命令"));
        }
        return CommandResult.success();
    }
}
