package de.moneygoal;

import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.network.chat.Component;

/**
 * Bearbeiten-Modus: HUD ziehen und mit dem Mausrad skalieren.
 */
public class MoneyGoalEditScreen extends Screen {

    private boolean dragging = false;
    private int grabX = 0;
    private int grabY = 0;

    public MoneyGoalEditScreen() {
        super(Component.literal("MoneyGoal HUD"));
    }

    @Override
    public void extractRenderState(GuiGraphicsExtractor graphics, int mouseX, int mouseY, float delta) {
        super.extractRenderState(graphics, mouseX, mouseY, delta);

        MoneyGoalConfig config = MoneyGoalClient.config();

        graphics.fill(0, 0, this.width, this.height, 0x77000000);
        MoneyGoalHud.extract(graphics, config);

        int boxWidth = Math.round(MoneyGoalHud.WIDTH * config.scale);
        int boxHeight = Math.round(MoneyGoalHud.HEIGHT * config.scale);
        graphics.outline(config.x - 2, config.y - 2, boxWidth + 4, boxHeight + 4, 0x99FFE28A);

        graphics.text(this.font,
                "Ziehen = verschieben   |   Mausrad = Groesse   |   ESC = speichern",
                12, this.height - 20, 0xFFEFF4F8, true);
    }

    @Override
    public boolean mouseClicked(double mouseX, double mouseY, int button) {
        MoneyGoalConfig config = MoneyGoalClient.config();
        if (isInside(mouseX, mouseY, config)) {
            dragging = true;
            grabX = (int) mouseX - config.x;
            grabY = (int) mouseY - config.y;
            return true;
        }
        return super.mouseClicked(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseDragged(double mouseX, double mouseY, int button, double deltaX, double deltaY) {
        MoneyGoalConfig config = MoneyGoalClient.config();
        if (dragging) {
            int maxX = Math.max(0, this.width - Math.round(MoneyGoalHud.WIDTH * config.scale));
            int maxY = Math.max(0, this.height - Math.round(MoneyGoalHud.HEIGHT * config.scale));
            config.x = clamp((int) mouseX - grabX, 0, maxX);
            config.y = clamp((int) mouseY - grabY, 0, maxY);
            return true;
        }
        return super.mouseDragged(mouseX, mouseY, button, deltaX, deltaY);
    }

    @Override
    public boolean mouseReleased(double mouseX, double mouseY, int button) {
        if (dragging) {
            dragging = false;
            MoneyGoalClient.config().save();
            return true;
        }
        return super.mouseReleased(mouseX, mouseY, button);
    }

    @Override
    public boolean mouseScrolled(double mouseX, double mouseY, double horizontalAmount, double verticalAmount) {
        MoneyGoalConfig config = MoneyGoalClient.config();
        float next = config.scale + (float) verticalAmount * 0.05f;
        config.scale = Math.max(0.5f, Math.min(2.0f, Math.round(next * 100.0f) / 100.0f));
        return true;
    }

    @Override
    public void onClose() {
        MoneyGoalClient.config().save();
        super.onClose();
    }

    private boolean isInside(double mouseX, double mouseY, MoneyGoalConfig config) {
        int boxWidth = Math.round(MoneyGoalHud.WIDTH * config.scale);
        int boxHeight = Math.round(MoneyGoalHud.HEIGHT * config.scale);
        return mouseX >= config.x && mouseX <= config.x + boxWidth
                && mouseY >= config.y && mouseY <= config.y + boxHeight;
    }

    private static int clamp(int value, int min, int max) {
        return Math.max(min, Math.min(max, value));
    }
}
