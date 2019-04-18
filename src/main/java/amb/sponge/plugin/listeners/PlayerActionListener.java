package amb.sponge.plugin.listeners;

import amb.sponge.plugin.core.Config;
import amb.sponge.plugin.facade.TeleporterLogicFacade;
import amb.sponge.plugin.service.TeleporterDataService;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.entity.EntityTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.event.Listener;
import org.spongepowered.api.event.entity.DestructEntityEvent;
import org.spongepowered.api.event.filter.cause.First;
import org.spongepowered.api.event.filter.cause.Root;
import org.spongepowered.api.event.item.inventory.InteractItemEvent;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.ItemStackSnapshot;
import org.spongepowered.api.text.Text;

public class PlayerActionListener {
    /**
     * 玩家左键传送书
     * @param event
     * @param player
     */
    @Listener
    public void onPlayerPCliceItem(InteractItemEvent.Primary event, @Root Player player) {
        ItemStackSnapshot item = event.getItemStack();
        if (item.getType() == ItemTypes.WRITTEN_BOOK) {
            String key = item.get(Keys.DISPLAY_NAME).get().toPlain();
            if (key.equals(Config.keyCn) || key.equals(Config.keyEn)){
                event.setCancelled(true);
                TeleporterLogicFacade.OpenTPBook(player);
            }
        }
    }

    /**
     * 玩家右键传送书
     * @param event
     * @param player
     */
    @Listener
    public void onPlayerSCliceItem(InteractItemEvent.Secondary event, @Root Player player) {
        ItemStackSnapshot item = event.getItemStack();
        if (item.getType() == ItemTypes.WRITTEN_BOOK) {
            String key = item.get(Keys.DISPLAY_NAME).get().toPlain();
            if (key.equals(Config.keyCn) || key.equals(Config.keyEn)){
                event.setCancelled(true);
                TeleporterLogicFacade.OpenTPBook(player);
            }
        }
    }

    /**
     * 玩家死亡
     * @param event
     * @param player
     */
    @Listener
    public void onPlayerDead(DestructEntityEvent.Death event, @First Player player) {
        if (event.getTargetEntity().getType().equals(EntityTypes.PLAYER)) {
            TeleporterDataService.savePlayerDeadData(player);
            player.sendMessage(Text.of("死亡地址已记录"));
        }

    }
}
