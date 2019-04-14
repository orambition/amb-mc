package amb.sponge.plugin.service;

import amb.sponge.plugin.constant.PluginKey;
import amb.sponge.plugin.core.Config;
import amb.sponge.plugin.core.Teleporter;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.query.QueryOperationTypes;
import org.spongepowered.api.scheduler.Task;
import org.spongepowered.api.text.Text;

import java.util.Optional;
import java.util.function.Predicate;

import static amb.sponge.plugin.core.PluginCore.instance;

/**
 * 传送逻辑
 */
public class TeleporterLogicService {

    /**
     * 添加传送点
     * @param player
     * @param tpCount
     */
    public static void AddTeleporter(Player player, double tpCount){
        if (player.get(Keys.TOTAL_EXPERIENCE).get() > 20){
            Optional arrow = player.getInventory().query(QueryOperationTypes.ITEM_TYPE.of((ItemType) Config.configConter.get("currency"))).poll((int) tpCount);
            if (arrow.isPresent()){
                player.sendMessage(Text.of("消耗" + tpCount + "颗" + ((ItemType) Config.configConter.get("currency")).getName()));
                player.offer(Keys.TOTAL_EXPERIENCE,player.get(Keys.TOTAL_EXPERIENCE).get() - 20);
                player.sendMessage(Text.of("消耗20点经验"));
                TeleporterDataService.savePlayerData(player, null, null);
                player.sendMessage(Text.of("已添加当前位置为新的传送点"));
            }else {
                player.sendMessage(Text.of("不足" + tpCount + "颗" + ((ItemType) Config.configConter.get("currency")).getName() +"无法设置传送点"));
            }
        }else {
            player.sendMessage(Text.of("经验不足20无法设置传送点"));
        }
    }

    /**
     * 传送消耗
     */
    public static void GotoTeleporter(Player player, Teleporter teleporter){
        boolean canGoto = false;
        Optional arrow = player.getInventory().query(QueryOperationTypes.ITEM_TYPE.of((ItemType) Config.configConter.get("currency"))).poll(2);
        if (arrow.isPresent()) {
            player.sendMessage(Text.of("消耗2颗"+((ItemType) Config.configConter.get("currency")).getName()));
            canGoto = true;
        }else if (player.get(Keys.TOTAL_EXPERIENCE).get() > 20) {
            player.sendMessage(Text.of("消耗20点经验"));
            canGoto = true;
        }else {
            ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.WRITTEN_BOOK)
                    .add(Keys.DISPLAY_NAME, Text.of(Config.configConter.get("keyEn").toString())).build();
            Optional arrowBook = player.getInventory().query(QueryOperationTypes.ITEM_STACK_IGNORE_QUANTITY.of(itemStack)).poll(1);
            if (arrowBook.isPresent()){
                player.sendMessage(Text.of("消耗一本传送书"));
            }else {
                itemStack = ItemStack.builder().itemType(ItemTypes.WRITTEN_BOOK)
                        .add(Keys.DISPLAY_NAME, Text.of(Config.configConter.get("keyCn").toString())).build();
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
