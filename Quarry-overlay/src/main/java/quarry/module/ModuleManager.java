package quarry.module;

import net.minecraft.client.MinecraftClient;
import java.util.List;

public final class ModuleManager {
    public static final AutoSprintModule AUTO_SPRINT = new AutoSprintModule();
    public static final NoFireModule NO_FIRE = new NoFireModule();

    public static final FeatureModule WATERMARK = new FeatureModule("Watermark");
    public static final FeatureModule TARGET_HUD = new FeatureModule("Target HUD");
    public static final FeatureModule COORDINATES = new FeatureModule("Coordinates");
    public static final FeatureModule FPS = new FeatureModule("FPS");
    public static final FeatureModule PING = new FeatureModule("Ping");
    public static final FeatureModule ARMOR_HUD = new FeatureModule("Armor HUD");
    public static final FeatureModule DIRECTION_HUD = new FeatureModule("Direction HUD");

    private static final List<Module> MODULES = List.of(
            AUTO_SPRINT, NO_FIRE, WATERMARK, TARGET_HUD, COORDINATES,
            FPS, PING, ARMOR_HUD, DIRECTION_HUD
    );

    private ModuleManager() {}

    public static void init() {}

    public static List<Module> getModules() {
        return MODULES;
    }

    public static void tick(MinecraftClient client) {
        for (Module module : MODULES) {
            if (module.isEnabled()) module.onTick(client);
        }
    }
}
