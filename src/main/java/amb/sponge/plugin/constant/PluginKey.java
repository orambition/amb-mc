package amb.sponge.plugin.constant;

import amb.sponge.plugin.core.Teleporter;
import org.spongepowered.api.data.key.Key;
import org.spongepowered.api.data.value.mutable.Value;
import org.spongepowered.api.util.generator.dummy.DummyObjectProvider;

public class PluginKey {
    public static final Key<Value<Teleporter>> AMB_TELEPOTTER = DummyObjectProvider.createExtendedFor(Key.class,"Teleporter");
}
