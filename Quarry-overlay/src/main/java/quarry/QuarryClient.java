package quarry;

import quarry.hud.HudOverlay;
import quarry.module.ModuleManager;
import quarry.screen.QuarryScreen;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.option.KeyBinding;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

public class QuarryClient implements ClientModInitializer {
    private static KeyBinding menuKey;
    private static KeyBinding sprintKey;
    private static KeyBinding noFireKey;

    @Override
    public void onInitializeClient() {
        ModuleManager.init();
        menuKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quarry.open_menu", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT, "category.quarry"));
        sprintKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quarry.toggle_autosprint", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_G, "category.quarry"));
        noFireKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.quarry.toggle_nofire", InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_H, "category.quarry"));

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (menuKey.wasPressed()) {
                if (client.currentScreen == null) client.setScreen(new QuarryScreen());
            }
            while (sprintKey.wasPressed()) ModuleManager.AUTO_SPRINT.toggle();
            while (noFireKey.wasPressed()) ModuleManager.NO_FIRE.toggle();
            ModuleManager.tick(client);
        });
        HudRenderCallback.EVENT.register((drawContext, tickCounter) -> HudOverlay.render(drawContext));
    }
}
