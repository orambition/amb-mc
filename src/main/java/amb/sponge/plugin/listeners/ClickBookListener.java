package amb.sponge.plugin.listeners;

import amb.sponge.plugin.constant.TeleporterTypeEnum;
import amb.sponge.plugin.core.Config;
import amb.sponge.plugin.core.Teleporter;
import amb.sponge.plugin.service.TPUIService;
import amb.sponge.plugin.service.TeleporterDataService;
import amb.sponge.plugin.facade.TeleporterLogicFacade;
import org.checkerframework.checker.units.qual.K;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

import java.util.function.Consumer;

/**
 * 监听玩家点击书
 */
public class ClickBookListener implements Consumer<ClickInventoryEvent> {

    @Override
    public void accept(ClickInventoryEvent event) {
        event.setCancelled(true);
        ItemStackSnapshot item = event.getCursorTransaction().getFinal();
        Player player = event.getCause().first(Player.class).get();

        if (event instanceof ClickInventoryEvent.Drop.Outside.Primary
                && item.getType().equals(Config.itmePlayerTp)) {
            // 鼠标左键拖拽出去
            // 删除私人传送点
            int tpId = Integer.parseInt(item.get(Keys.ITEM_LORE).get().get(0).toPlain().split("#")[1]);
            TeleporterDataService.delPlayerData(player, tpId);
            player.sendMessage(Text.of("传送点[" + item.get(Keys.DISPLAY_NAME) + "]已删除"));
            player.closeInventory();
        } else if (event instanceof ClickInventoryEvent.Primary) {
            // 鼠标左键点击
            if (item.getType().equals(Config.itmeAllowBeTp)) {
                // 修改开关
                boolean tpSwitch = item.get(Keys.ITEM_LORE).get().get(1).equals("不允许") ? false : true;
                TeleporterLogicFacade.SwitchBeTp(player, tpSwitch);
            } else if (item.getType().equals(Config.itmeAddTp)) {
                // 增加传送点
                int tpCount = TeleporterDataService.getPlayerDataCount(player.getUniqueId().toString());
                if (tpCount < Config.maxPlayerTp){
                    TeleporterLogicFacade.AddTeleporter(player, tpCount*tpCount);
                }else {
                    player.sendMessage(Text.of("私人传送点最多为"+tpCount+"个"));
                }
            } else if (item.getType().equals(Config.itmePublicTp)) {
                // 点击公共传送点
                String tpId = item.get(Keys.ITEM_LORE).get().get(0).toPlain().split("#")[1];
                TeleporterLogicFacade.GotoTeleporter(player, TeleporterDataService.getPublicDataByNum(tpId));
            } else if (item.getType().equals(Config.itmePlayerTp)) {
                // 点击私人传送点
                String tpId = item.get(Keys.ITEM_LORE).get().get(0).toPlain().split("#")[1];
                TeleporterLogicFacade.GotoTeleporter(player, TeleporterDataService.getPlayerDataByNum(player.getUniqueId().toString(), tpId));
            } else if (item.getType().equals(Config.itmeOnlineTp)) {
                // 点击在线玩家
                TeleporterLogicFacade.GotoTeleporter(player, new Teleporter(item.get(Keys.DISPLAY_NAME).get(), TeleporterTypeEnum.onlinePlayer));
            } else if (item.getType().equals(Config.itmeInfo)) {
                // 点击介绍
                TPUIService.ShowBookInfo(player);
            }
        }
    }

    @Override
    public Consumer<ClickInventoryEvent> andThen(Consumer<? super ClickInventoryEvent> after) {
        return null;
    }
}
