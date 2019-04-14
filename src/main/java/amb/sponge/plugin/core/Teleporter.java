package amb.sponge.plugin.core;

import amb.sponge.plugin.constant.TeleporterTypeEnum;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.data.Property;
import org.spongepowered.api.data.property.AbstractProperty;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;


/**
 * 传送点
 */
public class Teleporter extends AbstractProperty<String,String> {
    private int id;
    private Text name;
    private Location<World> location;
    private Vector3d rotation;
    private Long ctime;
    private TeleporterTypeEnum type;
    private UUID playerUUID;

    public UUID getPlayerUUID() {
        return playerUUID;
    }

    public void setPlayerUUID(UUID playerUUID) {
        this.playerUUID = playerUUID;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public Text getName() {
        return name;
    }

    public void setName(Text name) {
        this.name = name;
    }

    public Location<World> getLocation() {
        return location;
    }

    public void setLocation(Location<World> location) {
        this.location = location;
    }

    public Vector3d getRotation() {
        return rotation;
    }

    public void setRotation(Vector3d rotation) {
        this.rotation = rotation;
    }

    public String getCtime() {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(ctime));
    }

    public void setCtime(Long ctime) {
        this.ctime = ctime;
    }

    public TeleporterTypeEnum getType() {
        return type;
    }

    public void setType(TeleporterTypeEnum type) {
        this.type = type;
    }

    @Override
    public int compareTo(Property<?, ?> o) {
        return 0;
    }
}
