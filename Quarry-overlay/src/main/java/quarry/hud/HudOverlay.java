package quarry.hud;

import quarry.module.ModuleManager;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.entity.LivingEntity;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.hit.EntityHitResult;
import net.minecraft.util.hit.HitResult;
import net.minecraft.item.ItemStack;
import net.minecraft.text.Text;

public final class HudOverlay {
    private HudOverlay() {}

    public static void render(DrawContext ctx) {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.player == null || client.world == null || client.options.hudHidden) return;

        int x = 8;
        int y = 8;
        if (ModuleManager.WATERMARK.isEnabled()) {
            panel(ctx, x, y, 112, 22);
            ctx.fill(x, y, x + 2, y + 22, 0xFFAA83FF);
            ctx.drawText(client.textRenderer, Text.literal("QUARRY"), x + 9, y + 7, 0xFFF0E8FF, false);
            y += 27;
        }

        StringBuilder line = new StringBuilder();
        if (ModuleManager.COORDINATES.isEnabled()) {
            line.append(String.format("XYZ %.1f %.1f %.1f",
                    client.player.getX(), client.player.getY(), client.player.getZ()));
        }
        if (ModuleManager.FPS.isEnabled()) {
            if (!line.isEmpty()) line.append("  |  ");
            line.append(client.getCurrentFps()).append(" FPS");
        }
        if (ModuleManager.PING.isEnabled()) {
            var entry = client.getNetworkHandler() == null ? null :
                    client.getNetworkHandler().getPlayerListEntry(client.player.getUuid());
            if (!line.isEmpty()) line.append("  |  ");
            line.append(entry == null ? "--" : entry.getLatency()).append(" ms");
        }
        if (ModuleManager.DIRECTION_HUD.isEnabled()) {
            if (!line.isEmpty()) line.append("  |  ");
            line.append(client.player.getHorizontalFacing().getName().toUpperCase());
        }
        if (!line.isEmpty()) {
            int w = client.textRenderer.getWidth(line.toString()) + 16;
            panel(ctx, x, y, w, 20);
            ctx.drawText(client.textRenderer, Text.literal(line.toString()), x + 8, y + 6, 0xFFE6E8F0, false);
        }

        if (ModuleManager.ARMOR_HUD.isEnabled()) {
            int ax = 8;
            int ay = client.getWindow().getScaledHeight() - 28;
            panel(ctx, ax, ay, 142, 20);
            int slotX = ax + 7;
            for (ItemStack stack : client.player.getArmorItems()) {
                String label = stack.isEmpty() ? "--" :
                        String.valueOf(Math.round((1f - (float) stack.getDamage() / Math.max(1, stack.getMaxDamage())) * 100)) + "%";
                ctx.drawText(client.textRenderer, Text.literal(label), slotX, ay + 6, 0xFFE6E8F0, false);
                slotX += 32;
            }
        }

        if (ModuleManager.TARGET_HUD.isEnabled()) {
            HitResult hit = client.crosshairTarget;
            if (hit instanceof EntityHitResult entityHit && entityHit.getEntity() instanceof LivingEntity target
                    && target != client.player) {
                drawTarget(ctx, client, target);
            }
        }
    }

    private static void drawTarget(DrawContext ctx, MinecraftClient client, LivingEntity target) {
        int x = client.getWindow().getScaledWidth() / 2 + 12;
        int y = client.getWindow().getScaledHeight() / 2 + 12;
        float max = Math.max(1f, target.getMaxHealth());
        float health = Math.max(0f, Math.min(max, target.getHealth()));
        panel(ctx, x, y, 150, 43);
        ctx.drawText(client.textRenderer, target.getName(), x + 8, y + 7, 0xFFFFFFFF, false);
        String hp = String.format("%.1f / %.1f HP", health, max);
        ctx.drawText(client.textRenderer, Text.literal(hp), x + 8, y + 20, 0xFFBFC4D4, false);
        int barX = x + 8, barY = y + 34, barW = 134;
        ctx.fill(barX, barY, barX + barW, barY + 4, 0xFF34394A);
        ctx.fill(barX, barY, barX + Math.round(barW * health / max), barY + 4, 0xFF9B72F2);
    }

    private static void panel(DrawContext ctx, int x, int y, int w, int h) {
        ctx.fill(x + 1, y + 1, x + w + 1, y + h + 1, 0x99000000);
        ctx.fill(x, y, x + w, y + h, 0xE610131D);
        ctx.fill(x, y, x + w, y + 1, 0xFF34394A);
    }
}
