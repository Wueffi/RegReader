package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import wueffi.regreader.RegReaderConfig;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class ConfigCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerCommand(dispatcher, "regreader");
        registerCommand(dispatcher, "rr");
    }

    private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String rootCommand) {
        dispatcher.register(literal(rootCommand)
                .then(literal("config")
                        .then(literal("reload")
                                .executes(context -> {
                                    RegReaderConfig.load();
                                    context.getSource().sendFeedback(Component.literal("Config reloaded!"));
                                    return 1;
                                })
                        )
                        .then(literal("reset")
                                .executes(context -> {
                                    RegReaderConfig.resetToDefaults();
                                    context.getSource().sendFeedback(Component.literal("Config reset!"));
                                    return 1;
                                })
                        )
                        .then(literal("setTitleMode")
                                .then(argument("titleMode", BoolArgumentType.bool())
                                    .executes(context -> {
                                        boolean newMode = BoolArgumentType.getBool(context, "titleMode");
                                        RegReaderConfig.setTitleMode(newMode);
                                        context.getSource().sendFeedback(Component.literal("Title Mode set to " + newMode + "!"));
                                        return 1;
                                    })
                                )
                        )
                        .then(literal("setDefaults")
                                .then(literal("Bits")
                                        .then(argument("defaultBits", IntegerArgumentType.integer(2,32))
                                                .executes(context -> {
                                                    int defaultBits = IntegerArgumentType.getInteger(context, "defaultBits");
                                                    RegReaderConfig.setDefaultBits(defaultBits);
                                                    context.getSource().sendFeedback(Component.literal("Default-Bits set to " + defaultBits));
                                                    return 1;
                                                })
                                        )
                                )
                                .then(literal("Spacing")
                                        .then(argument("defaultSpacing", IntegerArgumentType.integer(2,400))
                                                .executes(context -> {
                                                    int defaultSpacing = IntegerArgumentType.getInteger(context, "defaultSpacing");
                                                    RegReaderConfig.setDefaultSpacing(defaultSpacing);
                                                    context.getSource().sendFeedback(Component.literal("Default-Spacing set to " + defaultSpacing));
                                                    return 1;
                                                })
                                        )
                                )
                                .then(literal("Inverted")
                                        .then(argument("defaultInverted", BoolArgumentType.bool())
                                                .executes(context -> {
                                                    boolean defaultInverted = BoolArgumentType.getBool(context,"defaultInverted");
                                                    RegReaderConfig.setDefaultInverted(defaultInverted);
                                                    context.getSource().sendFeedback(Component.literal("Default-Inverted set to " + defaultInverted));
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
        );
    }
}