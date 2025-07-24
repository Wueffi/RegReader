package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.RegReaderConfig;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class DeleteAllCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerCommand(dispatcher, "regreader");
        registerCommand(dispatcher, "rr");
    }

    private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String rootCommand) {
        dispatcher.register(literal(rootCommand)
                .then(literal("deleteall")
                        .executes(context -> {
                            RegReaderConfig.removeAll();
                            context.getSource().sendFeedback(Text.literal("Deleted all registers."));
                            return 1;
                        })));
    }
}