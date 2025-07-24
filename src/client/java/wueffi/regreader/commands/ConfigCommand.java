package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.RegReaderConfig;

import java.util.Arrays;
import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;
import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;

public class ConfigCommand {
    private static final List<Integer> validBases = Arrays.asList(2, 8, 10, 16);

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
                                }))
                        .then(literal("reset")
                                .executes(context -> {
                                    RegReaderConfig.resetToDefaults();
                                    context.getSource().sendFeedback(Text.literal("Config reset!"));
                                    return 1;
                                }))
                        .then(literal("setDisplayBase")
                                .then(argument("base", integer(2, 16))
                                        .suggests((context, builder) -> {
                                            for (int base : validBases) {
                                                builder.suggest(base);
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> {
                                            int base = IntegerArgumentType.getInteger(context, "base");
                                            if (validBases.contains(base)) {
                                                RegReaderConfig.setDisplayBase(base);
                                                context.getSource().sendFeedback(Text.literal("Display base set to " + base));
                                                return 1;
                                            } else {
                                                context.getSource().sendError(Text.literal("Invalid base! Use 2, 8, 10, or 16."));
                                                return 0;
                                            }
                                        })))
                .then(literal("setColoredNames")
                        .then(argument("Boolean", BoolArgumentType.bool())
                                .executes(context -> {
                                    boolean setting = BoolArgumentType.getBool(context, "Boolean");
                                    RegReaderConfig.setColoredName(setting);
                                    context.getSource().sendFeedback(Text.literal("Colored Names set to " + setting));
                                    return 1;
                                })))
                .then(literal("setHUDSize")
                        .then(argument("Width", IntegerArgumentType.integer())
                                .executes(context -> {
                                    int Width = IntegerArgumentType.getInteger(context, "Width");
                                    RegReaderConfig.setRectangleWidth(Width);
                                    context.getSource().sendFeedback(Text.literal("HUD Width set to " + Width));
                                    return 1;
                                })))
                .then(literal("setHUDPos")
                        .then(literal("X")
                                .then(argument("XPos", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int X = IntegerArgumentType.getInteger(context, "XPos");
                                            RegReaderConfig.setHUDX(X);
                                            context.getSource().sendFeedback(Text.literal("HUD X-Position set to " + X));
                                            return 1;
                                        })))
                        .then(literal("Y")
                                .then(argument("YPos", IntegerArgumentType.integer())
                                        .executes(context -> {
                                            int Y = IntegerArgumentType.getInteger(context, "YPos");
                                            RegReaderConfig.setHUDY(Y);
                                            context.getSource().sendFeedback(Text.literal("HUD Y-Position set to " + Y));
                                            return 1;
                                        }))))
                )
        );
    }
}