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

import static amb.sponge.plugin.service.TeleporterDataService.savePlayerData;
import static amb.sponge.plugin.service.TeleporterDataService.savePublicData;

/**
 * 设置传送地点
 */
public class SetTeleporterCmd implements CommandExecutor {

    @Override
    public CommandResult execute(CommandSource src, CommandContext args) throws CommandException {
        if (src instanceof Player) {
            // 设置公共地点
            savePublicData((Player) src, (Text) args.getOne("地点名称").orElse(null));
            src.sendMessage(Text.of("公共传送点已设置"));
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
