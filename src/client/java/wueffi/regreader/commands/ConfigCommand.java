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
                )
        );
    }
}