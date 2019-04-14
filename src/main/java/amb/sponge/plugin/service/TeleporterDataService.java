package amb.sponge.plugin.service;

import amb.sponge.plugin.constant.TeleporterTypeEnum;
import amb.sponge.plugin.core.Config;
import amb.sponge.plugin.core.Teleporter;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static amb.sponge.plugin.core.Config.loader2;
import static amb.sponge.plugin.core.Config.playerDataNode;

public class TeleporterDataService {

    /**
     * 获取公共传送点
     * @return
     */
    public static List<Teleporter> getPublicData(){
        List<Teleporter> teleporterList = new ArrayList<>();
        playerDataNode.getNode("Public", "tp").getChildrenList().forEach(node->{
            Teleporter teleporter = new Teleporter();
            try {
                teleporter.setId((Integer) node.getKey());
                teleporter.setName(node.getNode("name").getValue(TypeToken.of(Text.class)));
                teleporter.setLocation(node.getNode("location").getValue(TypeToken.of(Location.class)));
                teleporter.setRotation(node.getNode("rotation").getValue(TypeToken.of(Vector3d.class)));
                teleporter.setCtime(node.getNode("ctime").getLong());
                teleporter.setType(TeleporterTypeEnum.PublicTp);
                teleporterList.add(teleporter);
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }
        });
        return teleporterList;
    }

    /**
     * 获取指定玩家的私人传送点
     * @return
     */
    public static List<Teleporter> getPlayerData(String uuid){
        List<Teleporter> teleporterList = new ArrayList<>();
        playerDataNode.getNode("Player", uuid, "tp").getChildrenList().forEach(node->{
            Teleporter teleporter = new Teleporter();
            try {
                teleporter.setId((Integer) node.getKey());
                teleporter.setName(node.getNode("name").getValue(TypeToken.of(Text.class)));
                teleporter.setLocation(node.getNode("location").getValue(TypeToken.of(Location.class)));
                teleporter.setRotation(node.getNode("rotation").getValue(TypeToken.of(Vector3d.class)));
                teleporter.setCtime(node.getNode("ctime").getLong());
                teleporter.setType(TeleporterTypeEnum.PlayerTp);
                teleporterList.add(teleporter);
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }
        });
        return teleporterList;
    }

    /**
     * 获取指定玩家的死亡传送点
     * @return
     */
    public static List<Teleporter> getPlayerDeadData(String uuid){
        List<Teleporter> teleporterList = new ArrayList<>();
        playerDataNode.getNode("Player", uuid, "dead", "tp").getChildrenList().forEach(node->{
            Teleporter teleporter = new Teleporter();
            try {
                teleporter.setId((Integer) node.getKey());
                teleporter.setName(node.getNode("name").getValue(TypeToken.of(Text.class)));
                teleporter.setLocation(node.getNode("location").getValue(TypeToken.of(Location.class)));
                teleporter.setRotation(node.getNode("rotation").getValue(TypeToken.of(Vector3d.class)));
                teleporter.setCtime(node.getNode("ctime").getLong());
                teleporter.setType(TeleporterTypeEnum.PlayerDeadTp);
                teleporterList.add(teleporter);
            } catch (ObjectMappingException e) {
                e.printStackTrace();
            }
        });
        return teleporterList;
    }

    /**
     * 获取指定玩家的开关
     * @return
     */
    public static boolean notBeTPByPlayer(UUID uuid){
        return playerDataNode.getNode("Player", uuid.toString(), "NotAllowBeTp").getBoolean();
    }

    /**
     * 保存玩家配置
     */
    public static void savePlayerData(Player player, Boolean allowBeTp, Text name){
        String uuid = player.getUniqueId().toString();
        if (playerDataNode.getNode("Player", uuid).isVirtual()){
            // 初始化配置
            playerDataNode.getNode("Player", uuid, "Name").setValue(player.getDisplayNameData().displayName());
            playerDataNode.getNode("Player", uuid, "NotAllowBeTp").setValue(false);
            playerDataNode.getNode("Player", uuid, "tpNum").setValue(0);
        }
        try {
            if (null != allowBeTp){
                // 设置允许传送至此开关
                playerDataNode.getNode("Player", uuid, "NotAllowBeTp").setValue(allowBeTp);
            }else {
                // 新增私人传送点
                int num = playerDataNode.getNode("Player", uuid, "tpNum").getInt() + 1;
                playerDataNode.getNode("Player", uuid, "tp", num, "location").setValue(TypeToken.of(Location.class), player.getLocation());
                playerDataNode.getNode("Player", uuid, "tp", num, "rotation").setValue(TypeToken.of(Vector3d.class), player.getRotation());
                if (null == name){
                    name = Text.of("地点"+num);
                }
                playerDataNode.getNode("Player", uuid, "tp", num, "name").setValue(name);
                playerDataNode.getNode("Player", uuid, "tp", num, "ctime").setValue(System.currentTimeMillis());
                playerDataNode.getNode("Player", uuid, "tpNum").setValue(num);
            }
            // 玩家很少改名字，所以已第一次添加的为准
            loader2.save(playerDataNode);
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 记录玩家死亡地点
     * @param player
     */
    public static void savePlayerDeadData(Player player){
        String uuid = player.getUniqueId().toString();
        List<? extends CommentedConfigurationNode> deadNode = playerDataNode.getNode("Player", uuid, "dead","tp").getChildrenList();
        if (deadNode.size() >= (Integer)Config.getConfigConter("savedeadcount")){
            deadNode.remove(0);
        }
        try {
            int num = playerDataNode.getNode("Player", uuid, "dead","num").getInt()+1;
            playerDataNode.getNode("Player", uuid, "dead", "tp", num, "location").setValue(TypeToken.of(Location.class), player.getLocation());
            playerDataNode.getNode("Player", uuid, "dead", "tp", num, "rotation").setValue(TypeToken.of(Vector3d.class), player.getRotation());
            playerDataNode.getNode("Player", uuid, "dead", "tp", num, "name").setValue(Text.of("死亡地点"+(num)));
            playerDataNode.getNode("Player", uuid, "dead", "tp", num, "ctime").setValue(System.currentTimeMillis());
            playerDataNode.getNode("Player", uuid, "dead","num").setValue(num);
            loader2.save(playerDataNode);
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 删除私人传送点
     * @param player
     * @param num
     */
    public static void delPlayerData(Player player, int num){
        String uuid = player.getUniqueId().toString();
        if (!playerDataNode.getNode("Player", uuid).isVirtual() && !playerDataNode.getNode("Player", uuid, "tp", num).isVirtual()) {
            playerDataNode.getNode("Player", uuid, "tp").removeChild(num);
            try {
                loader2.save(playerDataNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 新增公共传送点
     * @param player
     * @param name
     */
    public static void savePublicData(Player player, Text name){
        int num = playerDataNode.getNode("Public", "tpNum").getInt() + 1;
        try {
            playerDataNode.getNode("Public", "tp", num, "location").setValue(TypeToken.of(Location.class), player.getLocation());
            playerDataNode.getNode("Public", "tp", num, "rotation").setValue(TypeToken.of(Vector3d.class), player.getRotation());
            if (null == name){
                name = Text.of("公共地点"+num);
            }
            playerDataNode.getNode("Public", "tp", num, "name").setValue(name);
            playerDataNode.getNode("Public", "tpNum").setValue(num);
            loader2.save(playerDataNode);
        } catch (ObjectMappingException | IOException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除公共传送点
     * @param num
     */
    public static void delPublicTeleporter(int num){
        if (num > 0 && !playerDataNode.getNode("Public", "tp", num).isVirtual()){
            playerDataNode.getNode("Public", "tp").removeChild(num);
            try {
                loader2.save(playerDataNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }
}
