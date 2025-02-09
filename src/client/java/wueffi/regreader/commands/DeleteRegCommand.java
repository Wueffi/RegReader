package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.RegisterManager;
import java.util.Set;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class DeleteRegCommand {
    private static final SuggestionProvider<FabricClientCommandSource> REGISTER_SUGGESTIONS = (context, builder) -> {
        Set<String> registerNames = RegisterManager.getRegisters().keySet();
        for (String name : registerNames) {
            builder.suggest(name);
        }
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("regreader")
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