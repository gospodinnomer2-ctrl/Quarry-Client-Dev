package quarry.screen;

import quarry.module.Module;
import quarry.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;

import java.util.List;

public class QuarryScreen extends Screen {
    private static final String[] CATEGORIES = {"Combat", "Visuals", "Movement", "Misc"};
    private int selectedCategory = 1;
    private int panelX, panelY, panelW = 430, panelH = 270;

    public QuarryScreen() {
        super(Text.literal("Quarry"));
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0x99060910);
        panelX = (width - panelW) / 2;
        panelY = (height - panelH) / 2;

        rounded(ctx, panelX + 3, panelY + 4, panelW, panelH, 12, 0x55000000);
        rounded(ctx, panelX, panelY, panelW, panelH, 12, 0xFF10131D);
        ctx.fill(panelX, panelY, panelX + panelW, panelY + 3, 0xFF9B72F2);
        ctx.fill(panelX, panelY + 3, panelX + 112, panelY + panelH, 0xFF151925);
        ctx.fill(panelX + 112, panelY + 3, panelX + 113, panelY + panelH, 0xFF292D3B);

        ctx.drawTextWithShadow(textRenderer, Text.literal("QUARRY"), panelX + 17, panelY + 17, 0xFFE5D8FF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("CLIENT  /  1.21.4"), panelX + 17, panelY + 31, 0xFF85899B);

        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = panelY + 62 + i *  thirty();
            boolean active = selectedCategory == i;
            if (active) {
                rounded(ctx, panelX + 9, y - 5, 94, 22, 6, 0xFF302747);
                ctx.fill(panelX + 9, y - 5, panelX + 12, y + 17, 0xFFB58CFF);
            }
            ctx.drawTextWithShadow(textRenderer, Text.literal(CATEGORIES[i]), panelX + 20, y, active ? 0xFFFFFFFF : 0xFF9DA2B2);
        }

        ctx.drawTextWithShadow(textRenderer, Text.literal(CATEGORIES[selectedCategory].toUpperCase()), panelX + 132, panelY + 20, 0xFFF0E9FF);
        ctx.drawTextWithShadow(textRenderer, Text.literal("Toggle modules  •  Right Shift to open/close"), panelX + 132, panelY + 35, 0xFF85899B);

        List<Module> modules = ModuleManager.getModules().stream()
                .filter(m -> belongsToCategory(m, selectedCategory)).toList();
        if (modules.isEmpty()) {
            ctx.drawTextWithShadow(textRenderer, Text.literal("Nothing here yet"), panelX + 132, panelY + 100, 0xFF777D90);
            ctx.drawTextWithShadow(textRenderer, Text.literal("More Quarry modules are coming."), panelX + 132, panelY + 115, 0xFF777D90);
        } else {
            int index = 0;
            for (Module module : modules) {
                int x = panelX + 132 + (index % 2) * 139;
                int y = panelY + 65 + (index / 2) * 68;
                boolean hover = mouseX >= x && mouseX <= x + 128 && mouseY >= y && mouseY <= y + 54;
                rounded(ctx, x, y, 128, 54, 8, hover ? 0xFF2B3041 : 0xFF1C2130);
                ctx.fill(x, y, x + 128, y + 1, module.isEnabled() ? 0xFF9B72F2 : 0xFF3A4052);
                ctx.drawTextWithShadow(textRenderer, Text.literal(module.getName()), x + 10, y + 11, 0xFFF0F0F7);
                ctx.drawTextWithShadow(textRenderer, Text.literal(module.isEnabled() ? "ENABLED" : "DISABLED"),
                        x + 10, y + 31, module.isEnabled() ? 0xFFB99AFF : 0xFF85899B);
                int sx = x + 96, sy = y + 19;
                rounded(ctx, sx, sy, 22, 12, 6, module.isEnabled() ? 0xFF8C65D8 : 0xFF414758);
                ctx.fill(module.isEnabled() ? sx + 12 : sx + 2, sy + 2,
                        module.isEnabled() ? sx + 20 : sx + 10, sy + 10, 0xFFFFFFFF);
                index++;
            }
        }

        ctx.drawTextWithShadow(textRenderer, Text.literal("QUARRY  •  VISUAL CLIENT"), panelX + 132, panelY + panelH - 20, 0xFF686F82);
        super.render(ctx, mouseX, mouseY, delta);
    }

    // Rounded scanline rendering keeps the UI soft without external graphics libraries.
    private void rounded(DrawContext ctx, int x, int y, int w, int h, int radius, int color) {
        int r = Math.min(radius, Math.min(w, h) / 2);
        for (int row = 0; row < h; row++) {
            double dy = row < r ? r - row - 0.5 : row >= h - r ? row - (h - r) + 0.5 : 0;
            int inset = dy == 0 ? 0 : (int) Math.ceil(r - Math.sqrt(Math.max(0, r * r - dy * dy)));
            ctx.fill(x + inset, y + row, x + w - inset, y + row + 1, color);
        }
    }

    private int thirty() { return 30; }

    private boolean belongsToCategory(Module module, int category) {
        return switch (category) {
            case 1 -> module == ModuleManager.NO_FIRE;
            case 2 -> module == ModuleManager.AUTO_SPRINT;
            default -> false;
        };
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        if (button != 0) return super.mouseClicked(mouseX, mouseY, button);
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = panelY + 57 + i * 30;
            if (mouseX >= panelX + 9 && mouseX <= panelX + 103 && mouseY >= y && mouseY <= y + 22) {
                selectedCategory = i;
                return true;
            }
        }
        List<Module> modules = ModuleManager.getModules().stream()
                .filter(m -> belongsToCategory(m, selectedCategory)).toList();
        for (int i = 0; i < modules.size(); i++) {
            int x = panelX + 132 + (i % 2) * 139;
            int y = panelY + 65 + (i / 2) * 68;
            if (mouseX >= x && mouseX <= x + 128 && mouseY >= y && mouseY <= y + 54) {
                modules.get(i).toggle();
                return true;
            }
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == GLFW.GLFW_KEY_ESCAPE || keyCode == GLFW.GLFW_KEY_RIGHT_SHIFT) {
            close();
            return true;
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    public boolean shouldPause() {
        return false;
    }
}
