package amb.sponge.plugin.core;

import com.flowpowered.math.vector.Vector3d;
import com.google.common.collect.Maps;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.hocon.HoconConfigurationLoader;
import ninja.leaping.configurate.loader.ConfigurationLoader;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.item.ItemType;
import org.spongepowered.api.item.ItemTypes;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class Config {

    public static File folder, config, playerData;
    public static ConfigurationLoader<CommentedConfigurationNode> loader, loader2;
    public static CommentedConfigurationNode configurationNode, playerDataNode;

    public static Map<String,Object> configConter;

    public static void setup(File file){
        folder = file;
    }

    /**
     * 加载配置
     */
    public static void load(){
        if (!folder.exists()){
            folder.mkdir();
        }
        try {
            config = new File(folder, "configuration.conf");
            loader = HoconConfigurationLoader.builder().setFile(config).build();
            if (!config.exists()){
                config.createNewFile();
                configurationNode = loader.load();
                addValues();
                loader.save(configurationNode);
            }
            configurationNode = loader.load();

            playerData = new File(folder, "PlayerData.conf");
            loader2 = HoconConfigurationLoader.builder().setFile(playerData).build();
            if (!playerData.exists()){
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
    private static void addValues(){
        configurationNode.getNode("tpbook", "title").setValue("Amb传送书").setComment("传送书界面的标题");
        try {
            configurationNode.getNode("tpbook", "itmePublicTp").setValue(TypeToken.of(ItemType.class), ItemTypes.SIGN);
            configurationNode.getNode("tpbook", "itmePlayerTp").setValue(TypeToken.of(ItemType.class), ItemTypes.COMPASS);
            configurationNode.getNode("tpbook", "itmeAddTp").setValue(TypeToken.of(ItemType.class), ItemTypes.NAME_TAG);
            configurationNode.getNode("tpbook", "itmeAllowBeTp").setValue(TypeToken.of(ItemType.class), ItemTypes.BARRIER);
            configurationNode.getNode("tpbook", "itmeDeadTp").setValue(TypeToken.of(ItemType.class), ItemTypes.SKULL);
            configurationNode.getNode("key","ch").setValue("传送书");
            configurationNode.getNode("key","en").setValue("tpbook");
            configurationNode.getNode("minUseLevel").setValue(10);
            configurationNode.getNode("currency").setValue(TypeToken.of(ItemType.class), ItemTypes.EMERALD);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    public static Map<String,Object> getConfigConter(){
        if (null == configConter || configConter.size() ==0){
            configConter = new HashMap<>();
            configConter.put("title", configurationNode.getNode("tpbook", "title").getString());
            try {
                configConter.put("itmePublicTp", configurationNode.getNode("tpbook", "itmePublicTp").getValue(TypeToken.of(ItemType.class)));
                configConter.put("itmePlayerTp", configurationNode.getNode("tpbook", "itmePlayerTp").getValue(TypeToken.of(ItemType.class)));
                configConter.put("itmeAddTp", configurationNode.getNode("tpbook", "itmeAddTp").getValue(TypeToken.of(ItemType.class)));
                configConter.put("itmeAllowBeTp", configurationNode.getNode("tpbook", "itmeAllowBeTp").getValue(TypeToken.of(ItemType.class)));
                configConter.put("itmeDeadTp", configurationNode.getNode("tpbook", "itmeDeadTp").getValue(TypeToken.of(ItemType.class)));
                configConter.put("keyCh", configurationNode.getNode("key","ch").getString());
                configConter.put("keyEn", configurationNode.getNode("key","en").getString());
                configConter.put("minUseLevel", configurationNode.getNode("minUseLevel").getInt());
                configConter.put("currency", configurationNode.getNode("currency").getValue(TypeToken.of(ItemType.class)));

            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }

        }
        return configConter;
    }
}
