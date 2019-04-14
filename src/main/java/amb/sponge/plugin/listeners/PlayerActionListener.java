package amb.sponge.plugin.listeners;

import amb.sponge.plugin.core.Config;
import amb.sponge.plugin.service.TPUIService;
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
import org.spongepowered.api.service.permission.SubjectData;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.util.Tristate;

public class PlayerActionListener {
    @Listener
    public void onPlayerDead(DestructEntityEvent.Death event, @First Player player) {
        if (event.getTargetEntity().getType().equals(EntityTypes.PLAYER)) {
            TeleporterDataService.savePlayerDeadData(player);
            player.sendMessage(Text.of("死亡地址已记录"));
        }

    }

    @Listener
    public void onPlayerCliceItem(InteractItemEvent.Secondary event, @Root Player player) {
        ItemStackSnapshot item = event.getItemStack();
        if (item.getType() == ItemTypes.WRITTEN_BOOK) {
            String key = item.get(Keys.DISPLAY_NAME).get().toPlain();
            if (key.equals(Config.getConfigConter("keyCn")) || key.equals(Config.getConfigConter("keyEn"))){
                event.setCancelled(true);
                if (player.hasPermission("tpbook.use")){
                    TPUIService.ShowTPUI(player);
                } else {
                    int minUseLevel = Integer.parseInt(Config.getConfigConter("minUseLevel").toString());
                    if (player.get(Keys.EXPERIENCE_LEVEL).get() < minUseLevel){
                        player.sendMessage(Text.of(minUseLevel+"级以上可以解锁传送书!"));
                    }else {
                        player.getSubjectData().setPermission(SubjectData.GLOBAL_CONTEXT, "tpbook.use", Tristate.TRUE);
                        player.offer(Keys.TOTAL_EXPERIENCE,player.get(Keys.TOTAL_EXPERIENCE).get() - 30);
                        player.sendMessage(Text.of("消耗30点经验"));
                        TPUIService.ShowBookInfo(player);
                    }
                }
            }
        }

    }
}
