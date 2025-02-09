package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class DisableHUDCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("regreader")
                .then(literal("disableHUD")
                        .executes(context -> {
                            // TODO: Disable HUD in RegisterManager and update config
                            return 1;
                        })));
    }
}