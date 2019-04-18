package amb.sponge.plugin.core;

import amb.sponge.plugin.constant.TeleporterTypeEnum;
import com.flowpowered.math.vector.Vector3d;
import org.spongepowered.api.text.Text;
import org.spongepowered.api.world.Location;
import org.spongepowered.api.world.World;


/**
 * 传送点
 */
public class Teleporter{
    private int id;
    private Text name;
    private Location<World> location;
    private Vector3d rotation;
    private Long ctime;
    private TeleporterTypeEnum type;

    public Teleporter() {
    }

    public Teleporter(Text name, TeleporterTypeEnum type) {
        this.name = name;
        this.type = type;
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

    public Long getCtime() {
        return ctime;
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
}
