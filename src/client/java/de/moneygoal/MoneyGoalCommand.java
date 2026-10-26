package de.moneygoal;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.DoubleArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.context.CommandContext;
import net.fabricmc.fabric.api.client.command.v2.ClientCommands;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.client.Minecraft;
import net.minecraft.network.chat.Component;

/**
 * Der Befehl /moneygoal.
 */
public final class MoneyGoalCommand {

    private MoneyGoalCommand() {
    }

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(ClientCommands.literal("moneygoal")
                .executes(MoneyGoalCommand::status)
                .then(ClientCommands.literal("set")
                        .then(ClientCommands.argument("betrag", DoubleArgumentType.doubleArg(0.0d))
                                .executes(context -> {
                                    MoneyGoalConfig config = MoneyGoalClient.config();
                                    config.goal = DoubleArgumentType.getDouble(context, "betrag");
                                    config.save();
                                    return reply(context, "Ziel gesetzt: "
                                            + MoneyGoalHud.format(config.goal) + " " + config.symbol);
                                })))
                .then(ClientCommands.literal("balance")
                        .then(ClientCommands.argument("betrag", DoubleArgumentType.doubleArg(0.0d))
                                .executes(context -> {
                                    MoneyGoalConfig config = MoneyGoalClient.config();
                                    config.balance = DoubleArgumentType.getDouble(context, "betrag");
                                    config.balanceUpdatedAt = System.currentTimeMillis();
                                    config.save();
                                    return reply(context, "Kontostand gesetzt: "
                                            + MoneyGoalHud.format(config.balance) + " " + config.symbol);
                                })))
                .then(ClientCommands.literal("interval")
                        .then(ClientCommands.argument("sekunden", IntegerArgumentType.integer(5, 600))
                                .executes(context -> {
                                    MoneyGoalConfig config = MoneyGoalClient.config();
                                    config.intervalSeconds = IntegerArgumentType.getInteger(context, "sekunden");
                                    config.save();
                                    return reply(context, "Automatische Abfrage alle "
                                            + config.intervalSeconds + " Sekunden.");
                                })))
                .then(ClientCommands.literal("check").executes(context -> {
                    MoneyGoalClient.queryMoney(Minecraft.getInstance());
                    return reply(context, "/money abgefragt ...");
                }))
                .then(ClientCommands.literal("edit").executes(context -> {
                    MoneyGoalClient.openEditScreen(Minecraft.getInstance());
                    return 1;
                }))
                .then(ClientCommands.literal("show").executes(context -> {
                    MoneyGoalClient.config().visible = true;
                    MoneyGoalClient.config().save();
                    return reply(context, "HUD eingeblendet.");
                }))
                .then(ClientCommands.literal("hide").executes(context -> {
                    MoneyGoalClient.config().visible = false;
                    MoneyGoalClient.config().save();
                    return reply(context, "HUD ausgeblendet.");
                }))
                .then(ClientCommands.literal("reset").executes(context -> {
                    MoneyGoalClient.config().goal = 0.0d;
                    MoneyGoalClient.config().save();
                    return reply(context, "Ziel geloescht.");
                })));
    }

    private static int status(CommandContext<FabricClientCommandSource> context) {
        MoneyGoalConfig config = MoneyGoalClient.config();
        return reply(context,
                "Ziel: " + MoneyGoalHud.format(config.goal) + " " + config.symbol
                        + "  |  Kontostand: " + MoneyGoalHud.format(config.balance) + " " + config.symbol
                        + "  |  " + Math.round(config.progress() * 100) + " %");
    }

    private static int reply(CommandContext<FabricClientCommandSource> context, String message) {
        context.getSource().sendFeedback(Component.literal(message));
        return 1;
    }
}
