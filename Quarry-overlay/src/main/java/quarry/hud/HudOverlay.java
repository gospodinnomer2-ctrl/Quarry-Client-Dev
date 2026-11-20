package quarry.hud;

import quarry.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;
import net.minecraft.entity.effect.StatusEffectInstance;
import net.minecraft.util.Formatting;

import java.time.LocalTime;
import java.time.format.DateTimeFormatter;

public final class HudOverlay {
    private static final long SESSION_START = System.currentTimeMillis();
    private static final DateTimeFormatter CLOCK_FORMAT = DateTimeFormatter.ofPattern("HH:mm:ss");

    private HudOverlay() {}

    public static void render(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.options.hudHidden) return;

        int sw = client.getWindow().getScaledWidth();
        int sh = client.getWindow().getScaledHeight();

        if (ModuleManager.WATERMARK.isEnabled()) {
            String label = "QUARRY";
            int textW = client.textRenderer.getWidth(label);
            int w = textW + 42;
            int x = (sw - w) / 2;
            int y = (sh - 30) / 2;
            pill(ctx, x, y, w, 28, 0xE610131D);
            pill(ctx, x + 2, y + 2, w - 4, 24, 0xFF1B2030);
            ctx.fill(x + 13, y + 8, x + 15, y + 20, 0xFFB58CFF);
            ctx.drawText(client.textRenderer, Text.literal(label), x + 22, y + 10, 0xFFF3ECFF, false);
            ctx.fill(x + w - 15, y + 8, x + w - 13, y + 20, 0xFFB58CFF);
        }

        int x = 8;
        int y = 8;
        StringBuilder line = new StringBuilder();
        if (ModuleManager.COORDINATES.isEnabled()) {
            line.append(String.format("XYZ %.1f %.1f %.1f",
                    client.player.getX(), client.player.getY(), client.player.getZ()));
        }
        if (ModuleManager.FPS.isEnabled()) append(line, client.getCurrentFps() + " FPS");
        if (ModuleManager.PING.isEnabled()) {
            var entry = client.getNetworkHandler() == null ? null :
                    client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            append(line, (entry == null ? "--" : entry.getLatency()) + " ms");
        }
        if (ModuleManager.DIRECTION_HUD.isEnabled()) {
            append(line, client.player.getHorizontalFacing().getName().toUpperCase());
        }
        if (ModuleManager.SPEED.isEnabled()) {
            double velocity = Math.sqrt(client.player.getVelocity().x * client.player.getVelocity().x
                    + client.player.getVelocity().z * client.player.getVelocity().z) * 20.0;
            append(line, String.format("%.2f m/s", velocity));
        }
        if (ModuleManager.CLOCK.isEnabled()) append(line, LocalTime.now().format(CLOCK_FORMAT));
        if (!line.isEmpty()) {
            int w = client.textRenderer.getWidth(line.toString()) + 16;
            panel(ctx, x, y, w, 20);
            ctx.drawText(client.textRenderer, Text.literal(line.toString()), x + 8, y + 6, 0xFFE6E8F0, false);
            y += 25;
        }

        if (ModuleManager.ARMOR_HUD.isEnabled()) {
            panel(ctx, 8, sh - 28, 142, 20);
            int slotX = 15;
            for (ItemStack stack : client.player.getArmorItems()) {
                String label = stack.isEmpty() ? "--" :
                        Math.round((1f - (float) stack.getDamage() / Math.max(1, stack.getMaxDamage())) * 100) + "%";
                ctx.drawText(client.textRenderer, Text.literal(label), slotX, sh - 22, 0xFFE6E8F0, false);
                slotX += 32;
            }
        }

        if (ModuleManager.KEYSTROKES.isEnabled()) {
            int kx = sw - 72, ky = sh - 75;
            key(ctx, client, kx + 24, ky, "W", client.options.forwardKey.isPressed());
            key(ctx, client, kx, ky + 24, "A", client.options.leftKey.isPressed());
            key(ctx, client, kx + 24, ky + 24, "S", client.options.backKey.isPressed());
            key(ctx, client, kx + 48, ky + 24, "D", client.options.rightKey.isPressed());
        }

        if (ModuleManager.POTION_HUD.isEnabled()) {
            int py = 8;
            for (StatusEffectInstance effect : client.player.getStatusEffects()) {
                String name = effect.getEffectType().value().getName().getString();
                String duration = StatusEffectInstance.durationToString(effect, 1.0f);
                String text = name + " " + duration;
                panel(ctx, sw - client.textRenderer.getWidth(text) - 22, py,
                        client.textRenderer.getWidth(text) + 14, 18);
                ctx.drawText(client.textRenderer, Text.literal(text), sw - client.textRenderer.getWidth(text) - 15,
                        py + 5, 0xFFE6E8F0, false);
                py += 21;
            }
        }

        if (ModuleManager.ITEM_COUNTER.isEnabled()) {
            int count = 0;
            for (int i = 0; i < client.player.getInventory().size(); i++) {
                ItemStack stack = client.player.getInventory().getStack(i);
                if (!stack.isEmpty() && stack.isOf(client.player.getMainHandStack().getItem())) count += stack.getCount();
            }
            String text = "Held item: " + count;
            panel(ctx, 8, sh - 52, client.textRenderer.getWidth(text) + 16, 19);
            ctx.drawText(client.textRenderer, Text.literal(text), 16, sh - 46, 0xFFE6E8F0, false);
        }

        if (ModuleManager.SESSION_INFO.isEnabled()) {
            long seconds = (System.currentTimeMillis() - SESSION_START) / 1000L;
            String text = String.format("Session %02d:%02d:%02d", seconds / 3600, (seconds / 60) % 60, seconds % 60);
            panel(ctx, 8, sh - 76, client.textRenderer.getWidth(text) + 16, 19);
            ctx.drawText(client.textRenderer, Text.literal(text), 16, sh - 70, 0xFFE6E8F0, false);
        }

        if (ModuleManager.CUSTOM_CROSSHAIR.isEnabled()) {
            int cx = sw / 2, cy = sh / 2;
            ctx.fill(cx - 7, cy, cx - 2, cy + 1, 0xFFE6D8FF);
            ctx.fill(cx + 2, cy, cx + 7, cy + 1, 0xFFE6D8FF);
            ctx.fill(cx, cy - 7, cx + 1, cy - 2, 0xFFE6D8FF);
            ctx.fill(cx, cy + 2, cx + 1, cy + 7, 0xFFE6D8FF);
        }

        if (ModuleManager.TARGET_HUD.isEnabled()) {
            HitResult hit = client.crosshairTarget;
            if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity target
                    && target != client.player) drawTarget(ctx, client, target, sw, sh);
        }
    }

    private static void append(StringBuilder line, String value) {
        if (!line.isEmpty()) line.append("  |  ");
        line.append(value);
    }

    private static void key(DrawContext ctx, MinecraftClient client, int x, int y, String label, boolean pressed) {
        panel(ctx, x, y, 22, 20);
        ctx.drawText(client.textRenderer, Text.literal(label), x + 7, y + 6,
                pressed ? 0xFFBFA2FF : 0xFFE6E8F0, false);
    }

    private static void drawTarget(DrawContext ctx, MinecraftClient client, LivingEntity target, int sw, int sh) {
        int x = sw / 2 + 12;
        int y = sh / 2 + 12;
        float max = Math.max(1f, target.getMaxHealth());
        float health = Math.max(0f, Math.min(max, target.getHealth()));
        panel(ctx, x, y, 150, 43);
        ctx.drawText(client.textRenderer, target.getName(), x + 8, y + 7, 0xFFFFFFFF, false);
        ctx.drawText(client.textRenderer, Text.literal(String.format("%.1f / %.1f HP", health, max)),
                x + 8, y + 20, 0xFFBFC4D4, false);
        int barX = x + 8, barY = y + 34, barW = 134;
        ctx.fill(barX, barY, barX + barW, barY + 4, 0xFF34394A);
        ctx.fill(barX, barY, barX + Math.round(barW * health / max), barY + 4, 0xFF9B72F2);
    }

    private static void pill(DrawContext ctx, int x, int y, int w, int h, int color) {
        int radius = h / 2;
        for (int row = 0; row < h; row++) {
            double dy = row < radius ? radius - row - 0.5 : row >= h - radius ? row - (h - radius) + 0.5 : 0;
            int inset = dy == 0 ? 0 : (int)Math.ceil(radius - Math.sqrt(Math.max(0, radius * radius - dy * dy)));
            ctx.fill(x + inset, y + row, x + w - inset, y + row + 1, color);
        }
    }

    private static void panel(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x + 1, y + 1, x + w + 1, y + h + 1, 0x99000000);
        ctx.fill(x, y, x + w, y + h, 0xF010131D);
        ctx.fill(x, y, x + w, y + 1, 0xFF9B72F2);
    }
}
