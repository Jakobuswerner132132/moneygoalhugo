package de.moneygoal;

import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;

import java.text.DecimalFormat;
import java.text.DecimalFormatSymbols;
import java.util.Locale;

/**
 * Zeichnet die Anzeige im Spiel.
 */
public final class MoneyGoalHud {

    public static final int WIDTH = 178;
    public static final int HEIGHT = 62;

    private static final DecimalFormat NUMBER =
            new DecimalFormat("#,##0", DecimalFormatSymbols.getInstance(Locale.GERMANY));

    private static final int SHADOW = 0x66000000;
    private static final int BG = 0xD90A0D12;
    private static final int TRACK = 0x33FFFFFF;
    private static final int ACCENT = 0xFF3DDC97;
    private static final int ACCENT_LIGHT = 0xFF8BF3C2;
    private static final int GOLD = 0xFFF5C542;
    private static final int WHITE = 0xFFEFF4F8;
    private static final int MUTED = 0xFF8B96A5;

    private MoneyGoalHud() {
    }

    public static void extract(GuiGraphicsExtractor graphics, MoneyGoalConfig config) {
        Minecraft minecraft = Minecraft.getInstance();
        var matrices = graphics.pose();

        matrices.pushMatrix();
        matrices.translate(config.x, config.y);
        if (config.scale != 1.0f) {
            matrices.scale(config.scale, config.scale);
        }

        rounded(graphics, 0, 2, WIDTH, HEIGHT, 5, SHADOW);
        rounded(graphics, 0, 0, WIDTH, HEIGHT, 5, BG);
        rounded(graphics, 1, 5, 3, HEIGHT - 10, 2, ACCENT);

        graphics.text(minecraft.font, "ZIEL", 12, 7, MUTED, false);
        graphics.text(minecraft.font, format(config.goal) + " " + config.symbol, 42, 7, GOLD, false);
        graphics.text(minecraft.font, format(config.balance) + " " + config.symbol, 12, 19, WHITE, true);

        if (config.goal > 0.0d) {
            graphics.text(minecraft.font, Math.round(config.progress() * 100) + " %", 12, 34, ACCENT, false);
            graphics.text(minecraft.font,
                    "noch " + format(Math.max(0.0d, config.goal - config.balance)) + " " + config.symbol,
                    46, 34, MUTED, false);
        } else {
            graphics.text(minecraft.font, "/moneygoal set <Betrag>", 12, 34, MUTED, false);
        }

        int barX = 12;
        int barY = 47;
        int barWidth = WIDTH - 24;
        int barHeight = 5;
        rounded(graphics, barX, barY, barWidth, barHeight, 2, TRACK);

        int filled = (int) Math.round(barWidth * config.progress());
        if (filled > 2) {
            rounded(graphics, barX, barY, filled, barHeight, 2, ACCENT);
            graphics.fill(barX + 1, barY, barX + filled - 1, barY + 1, ACCENT_LIGHT);
        }

        matrices.popMatrix();
    }

    public static String format(double value) {
        return NUMBER.format(Math.round(Math.max(0.0d, value)));
    }

    /** Rechteck mit abgerundeten Ecken, aus duennen Zeilen zusammengesetzt. */
    private static void rounded(GuiGraphicsExtractor graphics, int x, int y, int width, int height, int radius, int color) {
        if (width <= 0 || height <= 0) {
            return;
        }
        int r = Math.max(0, Math.min(radius, Math.min(width / 2, height / 2)));
        for (int row = 0; row < height; row++) {
            int inset = 0;
            if (row < r) {
                int dy = r - 1 - row;
                inset = r - (int) Math.round(Math.sqrt(Math.max(0, r * r - dy * dy)));
            } else if (row >= height - r) {
                int dy = row - (height - r);
                inset = r - (int) Math.round(Math.sqrt(Math.max(0, r * r - dy * dy)));
            }
            graphics.fill(x + inset, y + row, x + width - inset, y + row + 1, color);
        }
    }
}
