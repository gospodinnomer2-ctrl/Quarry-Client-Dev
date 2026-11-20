package quarry.module;

import net.minecraft.client.MinecraftClient;
import java.util.List;

public final class ModuleManager {
    public static final AutoSprintModule AUTO_SPRINT = new AutoSprintModule();
    public static final NoFireModule NO_FIRE = new NoFireModule();

    public static final FeatureModule WATERMARK = new FeatureModule("Watermark", true);
    public static final FeatureModule TARGET_HUD = new FeatureModule("Target HUD");
    public static final FeatureModule COORDINATES = new FeatureModule("Coordinates");
    public static final FeatureModule FPS = new FeatureModule("FPS");
    public static final FeatureModule PING = new FeatureModule("Ping");
    public static final FeatureModule ARMOR_HUD = new FeatureModule("Armor HUD");
    public static final FeatureModule DIRECTION_HUD = new FeatureModule("Direction HUD");
    public static final FeatureModule SPEED = new FeatureModule("Speed");
    public static final FeatureModule CLOCK = new FeatureModule("Clock");
    public static final FeatureModule KEYSTROKES = new FeatureModule("Keystrokes");
    public static final FeatureModule POTION_HUD = new FeatureModule("Potion HUD");
    public static final FeatureModule ITEM_COUNTER = new FeatureModule("Item Counter");
    public static final FeatureModule SESSION_INFO = new FeatureModule("Session Info");
    public static final FeatureModule CUSTOM_CROSSHAIR = new FeatureModule("Custom Crosshair");

    private static final List<Module> MODULES = List.of(
            AUTO_SPRINT, NO_FIRE, WATERMARK, TARGET_HUD, COORDINATES,
            FPS, PING, ARMOR_HUD, DIRECTION_HUD, SPEED, CLOCK,
            KEYSTROKES, POTION_HUD, ITEM_COUNTER, SESSION_INFO, CUSTOM_CROSSHAIR
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
