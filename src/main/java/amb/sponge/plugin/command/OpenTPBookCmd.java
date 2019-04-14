package amb.sponge.plugin.command;

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
 *  打卡传送书
 */
public class OpenTPBookCmd implements CommandExecutor {
    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player){
            // 传送到地点
            src.sendMessage(Text.of("设置传送点"));
        }else if (src instanceof ConsoleSource){
            src.sendMessage(Text.of("控制台无法执行该命令"));
        }else if (src instanceof CommandBlockSource){
            src.sendMessage(Text.of("不支持命令方块执行该命令"));
        }
        return CommandResult.success();
    }
}
