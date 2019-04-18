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
 * 删除传送地点
 */
public class DelTeleporterCmd implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            // 删除公共地点
            TeleporterDataService.delPublicTeleporter(args.<Integer>getOne("地点id").get().intValue());
            src.sendMessage(Text.of("公共地点已删除"));

        } else if (src instanceof ConsoleSource) {
            src.sendMessage(Text.of("控制台无法执行该命令"));
        } else if (src instanceof CommandBlockSource) {
            src.sendMessage(Text.of("不支持命令方块执行该命令"));
        }
        // 失败
        //return CommandResult.empty();
        return CommandResult.success();
    }
}
