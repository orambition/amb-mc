package amb.sponge.plugin.core;

import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;

import java.io.File;
import java.io.IOException;

public class Config {

    public static File folder, config, playerData;
    public static ConfigurationLoader<CommentedConfigurationNode> loader, loader2;
    public static CommentedConfigurationNode configurationNode, playerDataNode;

    public static String title; // 传送书标题
    public static ItemType itmePublicTp; // 表示公共传送点的物品
    public static ItemType itmeOnlineTp; // 表述在线玩家传送点的物品
    public static ItemType itmePlayerTp; // 表述私人传送点的物品
    public static ItemType itmeAddTp; // 表示增加私人传送点的物品
    public static ItemType itmeAllowBeTp; // 表示允许传送开关的物品
    public static ItemType itmeDeadTp; // 表示死亡地点的物品
    public static ItemType itmeInfo; // 表示传送书描述的物品
    public static String keyCn; // 创建传送书的中文关键字
    public static String keyEn; // 创建传送书的英文关键字
    public static int minUseLevel; // 可以使用传送书的最低等级
    public static ItemType currency; // 使用传送书消耗的物品
    public static String currencyShowName; // 使用传送书消耗的物品名称
    public static int maxDeadCount; // 死亡地点最多存储个数
    public static int maxPlayerTp; // 最多私人传送点个数

    public static void setup(File file) {
        folder = file;
    }

    /**
     * 加载配置
     */
    public static void load() {
        if (!folder.exists()) {
            folder.mkdir();
        }
        try {
            config = new File(folder, "configuration.conf");
            loader = HoconConfigurationLoader.builder().setFile(config).build();
            if (!config.exists()) {
                config.createNewFile();
                configurationNode = loader.load();
                addValues();
                loader.save(configurationNode);
            }
            configurationNode = loader.load();
            getConfigConter();
            playerData = new File(folder, "PlayerData.conf");
            loader2 = HoconConfigurationLoader.builder().setFile(playerData).build();
            if (!playerData.exists()) {
                playerData.createNewFile();
                // 没有默认值也没问题
            }
            playerDataNode = loader2.load();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 添加默认配置
     */
    private static void addValues() {
        try {
            configurationNode.getNode("tpbook", "title").setValue("Amb传送书").setComment("传送书界面的标题");
            configurationNode.getNode("tpbook", "itmePublicTp").setValue(TypeToken.of(ItemType.class), ItemTypes.SIGN);
            configurationNode.getNode("tpbook", "itmeOnlineTp").setValue(TypeToken.of(ItemType.class), ItemTypes.SKULL);
            configurationNode.getNode("tpbook", "itmePlayerTp").setValue(TypeToken.of(ItemType.class), ItemTypes.COMPASS);
            configurationNode.getNode("tpbook", "itmeAddTp").setValue(TypeToken.of(ItemType.class), ItemTypes.NAME_TAG);
            configurationNode.getNode("tpbook", "itmeAllowBeTp").setValue(TypeToken.of(ItemType.class), ItemTypes.BARRIER);
            configurationNode.getNode("tpbook", "itmeDeadTp").setValue(TypeToken.of(ItemType.class), ItemTypes.SKULL);
            configurationNode.getNode("tpbook", "itmeInfo").setValue(TypeToken.of(ItemType.class), ItemTypes.MAP);
            configurationNode.getNode("key", "cn").setValue("传送书");
            configurationNode.getNode("key", "en").setValue("tpbook");
            configurationNode.getNode("minUseLevel").setValue(10);
            configurationNode.getNode("currency").setValue(TypeToken.of(ItemType.class), ItemTypes.EMERALD);
            configurationNode.getNode("currencyShowName").setValue("绿宝石");
            configurationNode.getNode("maxDeadCount").setValue(6);
            configurationNode.getNode("maxPlayerTp").setValue(9);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public static void getConfigConter() {
        try {
            title = configurationNode.getNode("tpbook", "title").getString();
            itmePublicTp = configurationNode.getNode("tpbook", "itmePublicTp").getValue(TypeToken.of(ItemType.class));
            itmeOnlineTp = configurationNode.getNode("tpbook", "itmeOnlineTp").getValue(TypeToken.of(ItemType.class));
            itmePlayerTp = configurationNode.getNode("tpbook", "itmePlayerTp").getValue(TypeToken.of(ItemType.class));
            itmeAddTp = configurationNode.getNode("tpbook", "itmeAddTp").getValue(TypeToken.of(ItemType.class));
            itmeAllowBeTp = configurationNode.getNode("tpbook", "itmeAllowBeTp").getValue(TypeToken.of(ItemType.class));
            itmeDeadTp = configurationNode.getNode("tpbook", "itmeDeadTp").getValue(TypeToken.of(ItemType.class));
            itmeInfo = configurationNode.getNode("tpbook", "itmeInfo").getValue(TypeToken.of(ItemType.class));
            keyCn = configurationNode.getNode("key", "cn").getString();
            keyEn = configurationNode.getNode("key", "en").getString();
            minUseLevel = configurationNode.getNode("minUseLevel").getInt();
            currency = configurationNode.getNode("currency").getValue(TypeToken.of(ItemType.class));
            currencyShowName = configurationNode.getNode("currencyShowName").getString();
            maxDeadCount = configurationNode.getNode("maxDeadCount").getInt();
            maxPlayerTp = configurationNode.getNode("maxPlayerTp").getInt();
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }
}
