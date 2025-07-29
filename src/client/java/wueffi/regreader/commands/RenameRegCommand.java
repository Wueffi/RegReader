package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.RedstoneRegister;
import wueffi.regreader.RegisterManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class RenameRegCommand {
    private static final SuggestionProvider<FabricClientCommandSource> REGISTER_NAME_SUGGESTIONS = (context, builder) -> {
        for (RedstoneRegister register : RegisterManager.getAllRegisters()) {
            builder.suggest(register.name);
        }
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerCommand(dispatcher, "regreader");
        registerCommand(dispatcher, "rr");
    }

    private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String rootCommand) {
        dispatcher.register(literal(rootCommand)
                .then(literal("renamereg")
                        .then(argument("oldName", StringArgumentType.word())
                                .suggests(REGISTER_NAME_SUGGESTIONS)
                                .then(argument("newName", StringArgumentType.word())
                                        .executes(context -> {
                                            String oldName = StringArgumentType.getString(context, "oldName");
                                            String newName = StringArgumentType.getString(context, "newName");

                                            if (RegisterManager.renameRegister(oldName, newName)) {
                                                context.getSource().sendFeedback(Text.literal("Renamed register '" + oldName + "' to '" + newName + "'"));
                                            } else {
                                                context.getSource().sendError(Text.literal("Register '" + oldName + "' not found!"));
                                            }

                                            return 1;
                                        })))));
    }
}
