package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.sun.jdi.connect.Connector;
import it.unimi.dsi.fastutil.booleans.BooleanArrays;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.HUDManager;
import wueffi.regreader.RegReaderConfig;
import wueffi.regreader.RegReaderHUD;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

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
                                    context.getSource().sendFeedback(Text.literal("Config reloaded!"));
                                    return 1;
                                })
                        )
                        .then(literal("reset")
                                .executes(context -> {
                                    RegReaderConfig.resetToDefaults();
                                    context.getSource().sendFeedback(Text.literal("Config reset!"));
                                    return 1;
                                })
                        )
                        .then(literal("setTitleMode")
                                .then(argument("titleMode", BoolArgumentType.bool())
                                    .executes(context -> {
                                        boolean newMode = BoolArgumentType.getBool(context, "titleMode");
                                        RegReaderConfig.setTitleMode(newMode);
                                        context.getSource().sendFeedback(Text.literal("Title Mode set to " + newMode + "!"));
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
                                                    context.getSource().sendFeedback(Text.literal("Default-Bits set to " + defaultBits));
                                                    return 1;
                                                })
                                        )
                                )
                                .then(literal("Spacing")
                                        .then(argument("defaultSpacing", IntegerArgumentType.integer(2,400))
                                                .executes(context -> {
                                                    int defaultSpacing = IntegerArgumentType.getInteger(context, "defaultSpacing");
                                                    RegReaderConfig.setDefaultSpacing(defaultSpacing);
                                                    context.getSource().sendFeedback(Text.literal("Default-Spacing set to " + defaultSpacing));
                                                    return 1;
                                                })
                                        )
                                )
                                .then(literal("Inverted")
                                        .then(argument("defaultInverted", BoolArgumentType.bool())
                                                .executes(context -> {
                                                    boolean defaultInverted = BoolArgumentType.getBool(context,"defaultInverted");
                                                    RegReaderConfig.setDefaultInverted(defaultInverted);
                                                    context.getSource().sendFeedback(Text.literal("Default-Inverted set to " + defaultInverted));
                                                    return 1;
                                                })
                                        )
                                )
                        )
                )
        );
    }
}