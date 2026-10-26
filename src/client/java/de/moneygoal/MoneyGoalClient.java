package de.moneygoal;

import com.mojang.blaze3d.platform.InputConstants;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyMappingHelper;
import net.fabricmc.fabric.api.client.message.v1.ClientReceiveMessageEvents;
import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.KeyMapping;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.Identifier;
import org.lwjgl.glfw.GLFW;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Einstiegspunkt der Mod: Befehl /moneygoal, automatische /money-Abfrage und das HUD.
 * Geschrieben fuer Minecraft 26.3 mit den offiziellen (unobfuskierten) Namen.
 */
public class MoneyGoalClient implements ClientModInitializer {

    public static final String MOD_ID = "moneygoal";
    public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

    private static MoneyGoalConfig config;
    private static KeyMapping openKey;

    private int tickCounter = 0;
    private int joinDelay = 0;

    public static Identifier id(String path) {
        return Identifier.fromNamespaceAndPath(MOD_ID, path);
    }

    public static MoneyGoalConfig config() {
        if (config == null) {
            config = MoneyGoalConfig.load();
        }
        return config;
    }

    @Override
    public void onInitializeClient() {
        config = MoneyGoalConfig.load();

        KeyMapping.Category category = KeyMapping.Category.register(id("hud"));
        openKey = KeyMappingHelper.registerKeyMapping(new KeyMapping(
                "key.moneygoal.open",
                InputConstants.Type.KEYSYM,
                GLFW.GLFW_KEY_RIGHT_SHIFT,
                category
        ));

        ClientCommandRegistrationCallback.EVENT.register(
                (dispatcher, commandBuildContext) -> MoneyGoalCommand.register(dispatcher));

        ClientReceiveMessageEvents.GAME.register(
                (message, overlay) -> handleServerMessage(message.getString()));
        ClientReceiveMessageEvents.CHAT.register(
                (message, playerChatMessage, sender, boundChatType, timeStamp) -> handleServerMessage(message.getString()));

        HudElementRegistry.attachElementBefore(
                VanillaHudElements.CHAT,
                id("hud"),
                (graphics, deltaTracker) -> MoneyGoalHud.extract(graphics, config())
        );

        ClientTickEvents.END_CLIENT_TICK.register(this::onEndTick);
    }

    private void onEndTick(Minecraft client) {
        while (openKey.consumeClick()) {
            openEditScreen(client);
        }

        if (client.player == null) {
            tickCounter = 0;
            joinDelay = 0;
            return;
        }

        // Kurz nach dem Beitritt warten, damit der Server nicht sofort zugemuellt wird.
        if (joinDelay < 60) {
            joinDelay++;
            return;
        }

        tickCounter++;
        int interval = Math.max(5, config().intervalSeconds) * 20;
        if (tickCounter >= interval) {
            tickCounter = 0;
            queryMoney(client);
        }
    }

    public static void openEditScreen(Minecraft client) {
        client.gui.setScreen(new MoneyGoalEditScreen());
    }

    /** Schickt /money an den Server. */
    public static void queryMoney(Minecraft client) {
        if (client == null || client.player == null) {
            return;
        }
        client.player.connection.sendCommand("money");
    }

    /** Liest den Kontostand aus einer Server-Nachricht. */
    private static void handleServerMessage(String raw) {
        Double value = MoneyGoalParser.parse(raw, config());
        if (value == null) {
            return;
        }
        if (Math.abs(value - config().balance) > 0.0001d) {
            config().balance = value;
        }
        config().balanceUpdatedAt = System.currentTimeMillis();
        config().save();
    }
}
