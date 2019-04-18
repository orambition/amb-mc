package amb.sponge.plugin.service;

import amb.sponge.plugin.constant.TeleporterTypeEnum;
import amb.sponge.plugin.core.Config;
import amb.sponge.plugin.core.Teleporter;
import com.flowpowered.math.vector.Vector3d;
import com.google.common.reflect.TypeResolver;
import com.google.common.reflect.TypeToken;
import ninja.leaping.configurate.commented.CommentedConfigurationNode;
import ninja.leaping.configurate.objectmapping.ObjectMappingException;
import org.spongepowered.api.entity.living.player.Player;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

import static amb.sponge.plugin.core.Config.loader2;
import static amb.sponge.plugin.core.Config.playerDataNode;

public class TeleporterDataService {
    /**
     * 通过序号获取指定公共地点
     *
     * @param num
     * @return
     */
    public static Teleporter getPublicDataByNum(String num) {
        CommentedConfigurationNode node = playerDataNode.getNode("Public", "tp", num);
        if (node.isVirtual()) {
            return null;
        }
        return buildTeleporter(node, TeleporterTypeEnum.PublicTp);
    }

    /**
     * 通过指定序号获取私人传送点
     *
     * @param uuid
     * @param num
     * @return
     */
    public static Teleporter getPlayerDataByNum(String uuid, String num) {
        CommentedConfigurationNode node = playerDataNode.getNode("Player", uuid, "tp", num);
        if (node.isVirtual()) {
            return null;
        }
        return buildTeleporter(node, TeleporterTypeEnum.PlayerTp);
    }

    /**
     * 获取公共传送点
     *
     * @return
     */
    public static List<Teleporter> getPublicData() {
        List<Teleporter> teleporterList = new ArrayList<>();
        playerDataNode.getNode("Public", "tp").getChildrenList().forEach(node -> {
            teleporterList.add(buildTeleporter(node, TeleporterTypeEnum.PublicTp));
        });
        return teleporterList;
    }

    /**
     * 获取指定玩家的私人传送点
     *
     * @return
     */
    public static List<Teleporter> getPlayerData(String uuid) {
        List<Teleporter> teleporterList = new ArrayList<>();
        playerDataNode.getNode("Player", uuid, "tp").getChildrenMap().forEach((num,node)->{
            System.out.println(node);
            teleporterList.add(buildTeleporter(node, TeleporterTypeEnum.PlayerTp));
        });
        return teleporterList;
    }

    /**
     * 获取私人传送点数量
     *
     * @param uuid
     * @return
     */
    public static int getPlayerDataCount(String uuid) {
        return playerDataNode.getNode("Player", uuid, "tpCount").getInt();
    }

    /**
     * 获取指定玩家的死亡传送点
     *
     * @return
     */
    public static List<Teleporter> getPlayerDeadData(String uuid) {
        List<Teleporter> teleporterList = new ArrayList<>();
        playerDataNode.getNode("Player", uuid, "dead", "tp").getChildrenList().forEach(node -> {
            teleporterList.add(buildTeleporter(node, TeleporterTypeEnum.PlayerDeadTp));
        });
        return teleporterList;
    }

    /**
     * 获取指定玩家的开关
     *
     * @return
     */
    public static boolean notBeTPByPlayer(UUID uuid) {
        return playerDataNode.getNode("Player", uuid.toString(), "NotAllowBeTp").getBoolean();
    }

    /**
     * 保存玩家配置
     */
    public static void savePlayerData(Player player, Boolean allowBeTp, Text name) {
        String uuid = player.getUniqueId().toString();
        try {
            if (playerDataNode.getNode("Player", uuid).isVirtual()) {
                // 初始化配置
                playerDataNode.getNode("Player", uuid, "Name").setValue(TypeToken.of(Text.class), player.getDisplayNameData().displayName().get());
                playerDataNode.getNode("Player", uuid, "NotAllowBeTp").setValue(false);
                playerDataNode.getNode("Player", uuid, "tpNum").setValue(0);
                playerDataNode.getNode("Player", uuid, "tpCount").setValue(0);
            }
            if (null != allowBeTp) {
                // 设置允许传送至此开关
                playerDataNode.getNode("Player", uuid, "NotAllowBeTp").setValue(allowBeTp);
            } else {
                // 新增私人传送点
                String num = String.valueOf(playerDataNode.getNode("Player", uuid, "tpNum").getInt() + 1);
                if (null == name) {
                    name = Text.of("地点" + num);
                }
                int count = playerDataNode.getNode("Player", uuid, "tpCount").getInt() + 1;
                playerDataNode.getNode("Player", uuid, "tp", num, "location").setValue(TypeToken.of(Location.class), player.getLocation());
                playerDataNode.getNode("Player", uuid, "tp", num, "rotation").setValue(TypeToken.of(Vector3d.class), player.getRotation());
                playerDataNode.getNode("Player", uuid, "tp", num, "name").setValue(TypeToken.of(Text.class), name);
                playerDataNode.getNode("Player", uuid, "tp", num, "ctime").setValue(System.currentTimeMillis());
                playerDataNode.getNode("Player", uuid, "tpNum").setValue(num);
                playerDataNode.getNode("Player", uuid, "tpCount").setValue(count);
            }
            // 玩家很少改名字，所以已第一次添加的为准
            loader2.save(playerDataNode);
        } catch (ObjectMappingException | IOException e) {
            System.out.println(e.getMessage());
        }
    }

    /**
     * 记录玩家死亡地点
     *
     * @param player
     */
    public static void savePlayerDeadData(Player player) {
        String uuid = player.getUniqueId().toString();
        int count = playerDataNode.getNode("Player", uuid, "dead", "tpCount").getInt();
        if (count >= Config.maxDeadCount) {
            playerDataNode.getNode("Player", uuid, "dead", "tp").removeChild(playerDataNode.getNode("Player", uuid, "dead", "tp").getChildrenList().get(0).getKey());
        } else {
            playerDataNode.getNode("Player", uuid, "dead", "tpCount").setValue(count + 1);
        }
        try {
            String num = String.valueOf(playerDataNode.getNode("Player", uuid, "dead", "num").getInt() + 1);
            playerDataNode.getNode("Player", uuid, "dead", "tp", num, "location").setValue(TypeToken.of(Location.class), player.getLocation());
            playerDataNode.getNode("Player", uuid, "dead", "tp", num, "rotation").setValue(TypeToken.of(Vector3d.class), player.getRotation());
            playerDataNode.getNode("Player", uuid, "dead", "tp", num, "name").setValue(TypeToken.of(Text.class), Text.of("死亡地点" + (num)));
            playerDataNode.getNode("Player", uuid, "dead", "tp", num, "ctime").setValue(System.currentTimeMillis());
            playerDataNode.getNode("Player", uuid, "dead", "num").setValue(num);
            loader2.save(playerDataNode);
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
        }
    }

    /**
     * 新增公共传送点
     *
     * @param player
     * @param name
     */
    public static void savePublicData(Player player, Text name) {
        String num = String.valueOf(playerDataNode.getNode("Public", "tpNum").getInt() + 1);
        if (null == name) {
            name = Text.of("公共地点" + num);
        }
        try {
            playerDataNode.getNode("Public", "tp", num, "location").setValue(TypeToken.of(Location.class), player.getLocation());
            playerDataNode.getNode("Public", "tp", num, "rotation").setValue(TypeToken.of(Vector3d.class), player.getRotation());
            playerDataNode.getNode("Public", "tp", num, "name").setValue(TypeToken.of(Text.class), name);
            playerDataNode.getNode("Public", "tp", num, "ctime").setValue(System.currentTimeMillis());
            playerDataNode.getNode("Public", "tpNum").setValue(num);
            loader2.save(playerDataNode);
        } catch (IOException | ObjectMappingException e) {
            e.printStackTrace();
        }

    }

    /**
     * 删除私人传送点
     *
     * @param player
     * @param num
     */
    public static void delPlayerData(Player player, int num) {
        String uuid = player.getUniqueId().toString();
        if (!playerDataNode.getNode("Player", uuid).isVirtual() && !playerDataNode.getNode("Player", uuid, "tp", num).isVirtual()) {
            playerDataNode.getNode("Player", uuid, "tp").removeChild(num);
            int count = playerDataNode.getNode("Player", uuid, "tpCount").getInt() - 1;
            playerDataNode.getNode("Player", uuid, "tpCount").setValue(count);
            try {
                loader2.save(playerDataNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * 删除公共传送点
     *
     * @param num
     */
    public static void delPublicTeleporter(Integer num) {

        if (num > 0 && !playerDataNode.getNode("Public", "tp", num.toString()).isVirtual()) {
            playerDataNode.getNode("Public", "tp").removeChild(num.toString());
            try {
                loader2.save(playerDataNode);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    private static Teleporter buildTeleporter(CommentedConfigurationNode node, TeleporterTypeEnum type) {
        Teleporter teleporter = new Teleporter();
        try {
            teleporter.setId(Integer.parseInt(node.getKey().toString()));
            teleporter.setName(node.getNode("name").getValue(TypeToken.of(Text.class)));
            teleporter.setLocation(node.getNode("location").getValue(TypeToken.of(Location.class)));
            teleporter.setRotation(node.getNode("rotation").getValue(TypeToken.of(Vector3d.class)));
            teleporter.setCtime(node.getNode("ctime").getLong());
            teleporter.setType(type);
        } catch (ObjectMappingException e) {
            e.printStackTrace();
        }
        return teleporter;
    }
}
