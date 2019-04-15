package amb.sponge.plugin.service;

import amb.sponge.plugin.constant.PluginText;
import amb.sponge.plugin.constant.TeleporterTypeEnum;
import amb.sponge.plugin.core.Config;
import amb.sponge.plugin.core.Teleporter;
import amb.sponge.plugin.listeners.ClickBookListener;
import org.spongepowered.api.data.DataQuery;
import org.spongepowered.api.data.key.Keys;
import org.spongepowered.api.data.type.SkullTypes;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.entity.living.player.tab.TabListEntry;
import org.spongepowered.api.event.item.inventory.ClickInventoryEvent;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.item.inventory.Inventory;
import org.spongepowered.api.item.inventory.InventoryArchetypes;
import org.spongepowered.api.item.inventory.ItemStack;
import org.spongepowered.api.item.inventory.property.InventoryDimension;
import org.spongepowered.api.item.inventory.property.InventoryTitle;
import org.spongepowered.api.profile.GameProfile;
import org.spongepowered.api.text.BookView;
import org.spongepowered.api.text.Text;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import static amb.sponge.plugin.core.PluginCore.instance;

public class TPUIService {

    /**
     * 传送界面
     * @param player
     */
    public static void ShowTPUI(Player player) {

        // 获取菜单数据
        List<Teleporter> publicTeleporters = TeleporterDataService.getPublicData();
        List<Teleporter> playerTeleporters = TeleporterDataService.getPlayerData(player.getUniqueId().toString());
        List<GameProfile> onlineProfiles = player.getTabList().getEntries().stream().map(TabListEntry::getProfile).filter(p->!TeleporterDataService.notBeTPByPlayer(p.getUniqueId()) && !p.getUniqueId().equals(player.getUniqueId())).collect(Collectors.toList());
        List<Teleporter> playerDeadTeleporters = TeleporterDataService.getPlayerDeadData(player.getUniqueId().toString());

        int publicTpCount = (int) Math.ceil(publicTeleporters.size()/9.0);
        int playerTpCount = (int) Math.ceil(playerTeleporters.size()/9.0);
        int olPlayerCount = (int) Math.ceil(onlineProfiles.size()/9.0);
        // 创建菜单界面
        Inventory inventory = Inventory.builder()
                .of(InventoryArchetypes.MENU_GRID)
                .property(new InventoryTitle(Text.of(Config.title)))
                .property(new InventoryDimension(9,publicTpCount + playerTpCount + olPlayerCount + 1))
                .listener(ClickInventoryEvent.class, new ClickBookListener())
                .build(instance);

        // 创建菜单内容
        Iterator<Inventory> slotIterator = inventory.slots().iterator();
        // 创建公共菜单内容
        publicTeleporters.forEach(teleporter -> {
            slotIterator.next().set(buildItem(teleporter, "公共地点", Config.itmePublicTp));
        });
        for(int i = 0; i < publicTpCount * 9 - publicTeleporters.size(); ++i)
            slotIterator.next();
        // 创建在线玩家菜单
        onlineProfiles.forEach(gameProfile -> {
            if (!TeleporterDataService.notBeTPByPlayer(player.getUniqueId())){
                // 菜单描述
                Teleporter teleporter = new Teleporter();
                teleporter.setPlayerUUID(gameProfile.getUniqueId());
                teleporter.setType(TeleporterTypeEnum.onlinePlayer);
                ItemStack itemStack = ItemStack.builder().itemType(ItemTypes.SKULL)
                        .add(Keys.SKULL_TYPE, SkullTypes.PLAYER)
                        .add(Keys.DISPLAY_NAME, Text.of(gameProfile.getName().get()))
                        .add(Keys.BOOK_AUTHOR, Text.of("GotoTeleporter"))
                        .build();
                itemStack.setRawData(itemStack.toContainer().set(DataQuery.of("UnsafeData"),teleporter));
                slotIterator.next().set(itemStack);
            }
        });
        for(int i = 0; i < olPlayerCount * 9 - onlineProfiles.size(); ++i)
            slotIterator.next();

        // 创建私人菜单
        playerTeleporters.forEach(teleporter -> {
            slotIterator.next().set(buildItem(teleporter, "私人传送点",Config.itmePlayerTp));
        });
        for(int i = 0; i < playerTpCount * 9 - playerTeleporters.size(); ++i)
            slotIterator.next();

        // 创建关闭传送其他玩家传送至此菜单
        List<Text> itemlore2 = new ArrayList<>();
        itemlore2.add(Text.of("当前设置为:"));
        if (TeleporterDataService.notBeTPByPlayer(player.getUniqueId())){
            itemlore2.add(Text.of("不允许"));
        }else {
            itemlore2.add(Text.of("允许"));
        }
        ItemStack itemStack2 = ItemStack.builder().itemType(Config.itmeAllowBeTp)
                .add(Keys.DISPLAY_NAME, Text.of("是否允许其他玩家传送到身边"))
                .add(Keys.BOOK_AUTHOR, Text.of("AllowBeTp"))
                .add(Keys.ITEM_LORE, itemlore2)
                .build();
        slotIterator.next().set(itemStack2);

        // 死亡地点
        playerDeadTeleporters.forEach(teleporter -> {
            slotIterator.next().set(buildItem(teleporter, "最近死亡地点", Config.itmeDeadTp));
        });
        for(int i = 0; i < Config.maxDeadCount - playerDeadTeleporters.size(); ++i)
            slotIterator.next();

        // 传送书介绍
        ItemStack itemStack3 = ItemStack.builder().itemType(Config.itmeInfo)
                .add(Keys.DISPLAY_NAME, Text.of("介绍"))
                .add(Keys.BOOK_AUTHOR, Text.of("BookInfo"))
                .build();
        slotIterator.next().set(itemStack3);

        // 创建添加地点菜单
        List<Text> itemlore4 = new ArrayList<>();
        itemlore4.add(Text.of("添加当前位置为新的传送点"));
        double pTpCount = playerTeleporters.size();
        if (pTpCount >= 1){
            itemlore4.add(Text.of("此次操作需要消耗"+(pTpCount*pTpCount)+"个"+Config.currencyShowName+"和20点经验"));
        }
        ItemStack itemStack4 = ItemStack.builder().itemType(Config.itmeAddTp)
                .add(Keys.DISPLAY_NAME, Text.of("添加传送点"))
                .add(Keys.ITEM_LORE, itemlore4)
                .add(Keys.WALKING_SPEED, pTpCount*pTpCount)
                .add(Keys.BOOK_AUTHOR, Text.of("AddTeleporter"))
                .build();
        itemStack4.offer(Keys.BOOK_AUTHOR, Text.of("AddTeleporter"));
        itemStack4.setRawData(itemStack4.toContainer().set(DataQuery.of("buttonType"),"AddTeleporter"));
        slotIterator.next().set(itemStack4);

        player.openInventory(inventory);
    }

    /**
     * 传送书介绍界面
     */
    public static void ShowBookInfo(Player player){
        BookView bookView = BookView.builder()
                .title(Text.of("传送书"))
                .author(Text.of("Amb"))
                .addPage(PluginText.bookInfo)
                .build();
        player.sendBookView(bookView);
    }

    /**
     * 构建传送物品表示
     * @param teleporter
     * @param str
     * @param itemType
     * @return
     */
    private static ItemStack buildItem(Teleporter teleporter, String str, ItemType itemType){
        List<Text> itemlore = new ArrayList<>();
        itemlore.add(Text.of(str));
        itemlore.add(Text.of(teleporter.getLocation().getExtent().getName()));
        itemlore.add(Text.of("X坐标:"+teleporter.getLocation().getPosition().getFloorX()));
        itemlore.add(Text.of("Y坐标:"+teleporter.getLocation().getPosition().getFloorY()));
        itemlore.add(Text.of("Z坐标:"+teleporter.getLocation().getPosition().getFloorZ()));
        itemlore.add(Text.of("创建时间:"+teleporter.getCtime()));
        ItemStack itemStack = ItemStack.builder().itemType(itemType)
                .add(Keys.DISPLAY_NAME, Text.of(teleporter.getName()))
                .add(Keys.ITEM_LORE, itemlore)
                .add(Keys.BOOK_AUTHOR, Text.of("GotoTeleporter"))
                .build();
        itemStack.setRawData(itemStack.toContainer().set(DataQuery.of("UnsafeData"),teleporter));
        return itemStack;
    }
}
