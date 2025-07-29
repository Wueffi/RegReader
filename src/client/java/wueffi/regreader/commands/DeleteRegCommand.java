package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.RedstoneRegister;
import wueffi.regreader.RegisterManager;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class DeleteRegCommand {
    private static final SuggestionProvider<FabricClientCommandSource> REGISTER_SUGGESTIONS = (context, builder) -> {
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
                .then(literal("deletereg")
                        .then(argument("name", StringArgumentType.word())
                                .suggests(REGISTER_SUGGESTIONS)
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    RegisterManager.removeRegister(name);
                                    context.getSource().sendFeedback(Text.literal("Deleted register '" + name + "'"));
                                    return 1;
                                }))));
    }
}