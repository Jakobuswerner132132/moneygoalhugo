package de.moneygoal;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.IOException;
import java.io.Reader;
import java.io.Writer;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * Alle Einstellungen der Mod. Wird als config/moneygoal.json gespeichert.
 */
public class MoneyGoalConfig {

    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().disableHtmlEscaping().create();
    private static final Path FILE = FabricLoader.getInstance().getConfigDir().resolve("moneygoal.json");

    /** Geld-Ziel. 0 = kein Ziel gesetzt. */
    public double goal = 0.0d;
    /** Zuletzt gelesener Kontostand. */
    public double balance = 0.0d;
    /** Zeitpunkt der letzten Aktualisierung in Millisekunden. */
    public long balanceUpdatedAt = 0L;
    /** HUD anzeigen? */
    public boolean visible = true;
    /** Position des HUD in Pixeln. */
    public int x = 16;
    public int y = 16;
    /** Groesse des HUD, 0.5 bis 2.0. */
    public float scale = 1.0f;
    /** Abstand zwischen zwei automatischen /money-Abfragen. */
    public int intervalSeconds = 30;
    /** Waehrungssymbol in der Anzeige. */
    public String symbol = "$";
    /** Suchmuster fuer den Betrag. Der erste Treffer gewinnt. */
    public List<String> patterns = new ArrayList<>(Arrays.asList(
            "(?:geld|money|balance|kontostand|vermoegen|coins?)[^0-9]{0,12}([0-9][0-9.,' ]{0,18})",
            "([0-9][0-9.,' ]{0,18})\\s*(?:\\$|EUR|euro|dollar|coins)"
    ));
    /** Notfall-Suche: nur Nachrichten mit einem dieser Woerter werden ausgewertet. */
    public List<String> keywords = new ArrayList<>(Arrays.asList(
            "geld", "money", "balance", "kontostand", "vermoegen", "coins", "euro", "$"
    ));

    /** Fortschritt von 0.0 bis 1.0. */
    public double progress() {
        if (goal <= 0.0d) {
            return 0.0d;
        }
        return Math.max(0.0d, Math.min(1.0d, balance / goal));
    }

    public static MoneyGoalConfig load() {
        if (Files.exists(FILE)) {
            try (Reader reader = Files.newBufferedReader(FILE, StandardCharsets.UTF_8)) {
                MoneyGoalConfig loaded = GSON.fromJson(reader, MoneyGoalConfig.class);
                if (loaded != null) {
                    return loaded;
                }
            } catch (IOException | RuntimeException error) {
                MoneyGoalClient.LOGGER.warn("config/moneygoal.json konnte nicht gelesen werden", error);
            }
        }
        MoneyGoalConfig fresh = new MoneyGoalConfig();
        fresh.save();
        return fresh;
    }

    public void save() {
        try (Writer writer = Files.newBufferedWriter(FILE, StandardCharsets.UTF_8)) {
            GSON.toJson(this, writer);
        } catch (IOException error) {
            MoneyGoalClient.LOGGER.warn("config/moneygoal.json konnte nicht gespeichert werden", error);
        }
    }
}
