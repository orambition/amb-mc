package amb.sponge.plugin.facade;

import amb.sponge.plugin.constant.TeleporterTypeEnum;
import amb.sponge.plugin.core.Config;
import amb.sponge.plugin.core.Teleporter;
import amb.sponge.plugin.service.TPUIService;
import amb.sponge.plugin.service.TeleporterDataService;
import org.spongepowered.api.Sponge;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;

import java.util.Optional;

import static amb.sponge.plugin.core.PluginCore.instance;

/**
 * 传送逻辑
 */
public class TeleporterLogicFacade {

    /**
     * 打开传送书
     * @param player
     */
    public static void OpenTPBook(Player player){
        if (player.hasPermission("tpbook.use")){
            TPUIService.ShowTPUI(player);
        } else {
            if (player.get(Keys.EXPERIENCE_LEVEL).get() < Config.minUseLevel){
                player.sendMessage(Text.of(Config.minUseLevel+"级以上可以解锁传送书!"));
            }else {
                player.getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, "tpbook.use", Tristate.TRUE);
                player.offer(Keys.TOTAL_EXPERIENCE,player.get(Keys.TOTAL_EXPERIENCE).get() - 30);
                player.sendMessage(Text.of("消耗30点经验"));
                TPUIService.ShowBookInfo(player);
            }
        }
    }

    /**
     * 添加传送点
     * @param player
     * @param tpCount
     */
    public static void AddTeleporter(Player player, double tpCount){
        if (player.get(Keys.TOTAL_EXPERIENCE).get() > 20){
            Optional arrow = player.getInventory().query(QueryOperationTypes.ITEM_TYPE.of(Config.currency)).poll((int) tpCount);
            if (arrow.isPresent()){
                player.sendMessage(Text.of("消耗" + tpCount + "个" + Config.currencyShowName));
                player.offer(Keys.TOTAL_EXPERIENCE,player.get(Keys.TOTAL_EXPERIENCE).get() - 20);
                player.sendMessage(Text.of("消耗20点经验"));
                TeleporterDataService.savePlayerData(player, null, null);
                player.sendMessage(Text.of("已添加当前位置为新的传送点"));
            }else {
                player.sendMessage(Text.of("不足" + tpCount + "个" + Config.currencyShowName +"无法设置传送点"));
            }
        }else {
            player.sendMessage(Text.of("经验不足20无法设置传送点"));
        }
        player.closeInventory();
    }

    /**
     * 传送消耗
     */
    public static void GotoTeleporter(Player player, Teleporter teleporter){
        boolean canGoto = false;
        if (teleporter.getType().equals(TeleporterTypeEnum.onlinePlayer)){
            Player gotoPlayer = Sponge.getServer().getPlayer(teleporter.getPlayerUUID()).orElse(null);
            if (null == gotoPlayer){
                player.sendMessage(Text.of("玩家不在了"));
                return;
            }
            teleporter.setLocation(gotoPlayer.getLocation());
            teleporter.setRotation(gotoPlayer.getRotation());
        }
        Optional arrow = player.getInventory().query(QueryOperationTypes.ITEM_TYPE.of(Config.currency)).poll(2);
        if (arrow.isPresent()) {
            player.sendMessage(Text.of("消耗2个"+Config.currencyShowName));
            canGoto = true;
        }else if (player.get(Keys.TOTAL_EXPERIENCE).get() > 20) {
            player.sendMessage(Text.of("消耗20点经验"));
            canGoto = true;
        }else {
            ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.WRITTEN_BOOK)
                    .add(Keys.DISPLAY_NAME, Text.of(Config.keyEn)).build();
            Optional arrowBook = player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(itemStack)).poll(1);
            if (arrowBook.isPresent()){
                player.sendMessage(Text.of("消耗一本传送书"));
            }else {
                itemStack = ItemStack.builder().itemType(ItemTypes.WRITTEN_BOOK)
                        .add(Keys.DISPLAY_NAME, Text.of(Config.keyCn)).build();
                arrowBook = player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(itemStack)).poll(1);
                if (arrowBook.isPresent()){
                    player.sendMessage(Text.of("消耗一本传送书"));
                }else {
                    player.sendMessage(Text.of("原料不足，无法传送"));
                }
            }
        }
        if (canGoto){
            Task.builder().execute(() -> {
                player.closeInventory();
                player.setLocationSafely(teleporter.getLocation());
                player.setRotation(teleporter.getRotation());
            }).delayTicks(1L).submit(instance);
            player.sendMessage(Text.of("已传送至地点"+teleporter.getName()));
        }
    }
}
