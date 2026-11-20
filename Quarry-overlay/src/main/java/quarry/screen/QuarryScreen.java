package quarry.screen;

import quarry.module.Module;
import quarry.module.ModuleManager;
import net.minecraft.client.gui.DrawContext;
import net.minecraft.client.gui.screen.Screen;
import net.minecraft.client.render.RenderLayer;
import net.minecraft.util.Identifier;
import net.minecraft.text.Text;
import org.lwjgl.glfw.GLFW;
import java.util.List;

public class QuarryScreen extends Screen {
    private static final String[] CATEGORIES = {"Combat", "Visuals", "Movement", "HUD", "Misc"};
    private static final Identifier SKY = Identifier.of("quarry", "textures/gui/quarry_sky.png");
    private int selectedCategory = 3;
    private int panelX, panelY, panelW = 560, panelH = 360;

    public QuarryScreen() { super(Text.literal("Quarry")); }

    @Override
    public void renderBackground(DrawContext ctx, int mouseX, int mouseY, float delta) {
        // Deliberately do not invoke vanilla menu blur.
    }

    @Override
    public void render(DrawContext ctx, int mouseX, int mouseY, float delta) {
        ctx.fill(0, 0, width, height, 0xFF080A11);
        ctx.drawTexture(RenderLayer::getGuiTextured, SKY, 0, 0, 0, 0, width, height, 128, 72);
        ctx.fill(0, 0, width, height, 0x99060916);
        panelX = (width - panelW) / 2; panelY = (height - panelH) / 2;
        rounded(ctx, panelX + 3, panelY + 4, panelW, panelH, 12, 0xFF05060A);
        rounded(ctx, panelX, panelY, panelW, panelH, 12, 0xFF10131D);
        ctx.fill(panelX, panelY, panelX + panelW, panelY + 3, 0xFF9B72F2);
        ctx.fill(panelX, panelY + 3, panelX + 116, panelY + panelH, 0xFF151925);
        ctx.fill(panelX + 116, panelY + 3, panelX + 117, panelY + panelH, 0xFF292D3B);
        rounded(ctx, panelX + 10, panelY + 10, 98, 38, 10, 0xE90A0C18);
        ctx.fill(panelX + 15, panelY + 17, panelX + 17, panelY + 41, 0xFFB58CFF);
        ctx.drawText(textRenderer, Text.literal("QUARRY").copy().styled(st -> st.withBold(true)), panelX + 23, panelY + 16, 0xFFFFFFFF, true);
        ctx.drawText(textRenderer, Text.literal("VISUAL CLIENT"), panelX + 23, panelY + 31, 0xFFD5BFFF, true);
        for (int i = 0; i < CATEGORIES.length; i++) {
            int y = panelY + 62 + i * 30; boolean active = selectedCategory == i;
            if (active) { rounded(ctx, panelX + 9, y - 5, 98, 22, 6, 0xFF302747); ctx.fill(panelX + 9, y - 5, panelX + 12, y + 17, 0xFFB58CFF); }
            ctx.drawText(textRenderer, Text.literal(CATEGORIES[i]), panelX + 20, y, active ? 0xFFFFFFFF : 0xFFB9BED0, true);
        }
        ctx.drawText(textRenderer, Text.literal(CATEGORIES[selectedCategory].toUpperCase()), panelX + 136, panelY + 20, 0xFFFFFFFF, true);
        ctx.drawText(textRenderer, Text.literal("Click a card to toggle a module"), panelX + 136, panelY + 35, 0xFF85899B, false);
        List<Module> modules = ModuleManager.getModules().stream().filter(m -> belongsToCategory(m, selectedCategory)).toList();
        if (modules.isEmpty()) {
            ctx.drawText(textRenderer, Text.literal("Modules for this category are not implemented yet."), panelX + 136, panelY + 100, 0xFFD0D4E0, true);
        } else {
            for (int i = 0; i < modules.size(); i++) {
                Module m = modules.get(i); int x = panelX + 136 + (i % 3) * 132; int y = panelY + 65 + (i / 3) * 52;
                boolean hover = mouseX >= x && mouseX <= x + 126 && mouseY >= y && mouseY <= y + 42;
                rounded(ctx, x, y, 126, 42, 7, hover ? 0xFF2B3041 : 0xFF1C2130);
                ctx.fill(x, y, x + 126, y + 1, m.isEnabled() ? 0xFF9B72F2 : 0xFF3A4052);
                ctx.drawText(textRenderer, Text.literal(m.getName()), x + 9, y + 8, 0xFFFFFFFF, true);
                ctx.drawText(textRenderer, Text.literal(m.isEnabled() ? "ON" : "OFF"), x + 9, y + 24, m.isEnabled() ? 0xFFD4BEFF : 0xFFB9BED0, true);
                rounded(ctx, x + 98, y + 14, 22, 12, 6, m.isEnabled() ? 0xFF8C65D8 : 0xFF414758);
                ctx.fill(m.isEnabled() ? x + 110 : x + 100, y + 16, m.isEnabled() ? x + 118 : x + 108, y + 24, 0xFFFFFFFF);
            }
        }
        ctx.drawText(textRenderer, Text.literal("QUARRY  •  VISUAL CLIENT"), panelX + 136, panelY + panelH - 19, 0xFFB9BED0, true);
        super.render(ctx, mouseX, mouseY, delta);
    }

    private void rounded(DrawContext ctx, int x, int y, int w, int h, int radius, int color) {
        int r = Math.min(radius, Math.min(w, h) / 2);
        for (int row = 0; row < h; row++) {
            double dy = row < r ? r - row - 0.5 : row >= h - r ? row - (h - r) + 0.5 : 0;
            int inset = dy == 0 ? 0 : (int)Math.ceil(r - Math.sqrt(Math.max(0, r*r - dy*dy)));
            ctx.fill(x + inset, y + row, x + w - inset, y + row + 1, color);
        }
    }

    private boolean belongsToCategory(Module m, int c) {
        return switch (c) {
            case 0 -> m == ModuleManager.TARGET_HUD;
            case 1 -> m == ModuleManager.NO_FIRE;
            case 2 -> m == ModuleManager.AUTO_SPRINT;
            case 3 -> m == ModuleManager.WATERMARK || m == ModuleManager.COORDINATES ||
                    m == ModuleManager.FPS || m == ModuleManager.PING ||
                    m == ModuleManager.ARMOR_HUD || m == ModuleManager.DIRECTION_HUD ||
                    m == ModuleManager.SPEED || m == ModuleManager.CLOCK ||
                    m == ModuleManager.KEYSTROKES || m == ModuleManager.POTION_HUD ||
                    m == ModuleManager.ITEM_COUNTER || m == ModuleManager.SESSION_INFO ||
                    m == ModuleManager.CUSTOM_CROSSHAIR;
            default -> false;
        };
    }

    @Override public boolean mouseClicked(double mx, double my, int button) {
        if (button != 0) return super.mouseClicked(mx, my, button);
        for (int i=0;i<CATEGORIES.length;i++) {
            int y=panelY+57+i*30;
            if(mx>=panelX+9&&mx<=panelX+107&&my>=y&&my<=y+22){selectedCategory=i;return true;}
        }
        List<Module> modules=ModuleManager.getModules().stream().filter(m->belongsToCategory(m,selectedCategory)).toList();
        for(int i=0;i<modules.size();i++){
            int x=panelX+136+(i%3)*132; int y=panelY+65+(i/3)*52;
            if(mx>=x&&mx<=x+126&&my>=y&&my<=y+42){modules.get(i).toggle();return true;}
        }
        return super.mouseClicked(mx,my,button);
    }
    @Override public boolean keyPressed(int keyCode,int scanCode,int modifiers){
        if(keyCode==GLFW.GLFW_KEY_ESCAPE||keyCode==GLFW.GLFW_KEY_RIGHT_SHIFT){close();return true;}
        return super.keyPressed(keyCode,scanCode,modifiers);
    }
    @Override public boolean shouldPause(){return false;}
}
