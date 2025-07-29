package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.*;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class DefaultAddCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerCommand(dispatcher, "regreader");
        registerCommand(dispatcher, "rr");
    }

    private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String rootCommand) {
        dispatcher.register(literal(rootCommand)
                .then(literal("defaultadd")
                    .then(argument("hud", StringArgumentType.word())
                        .suggests((context, builder) -> {
                            for (RegReaderHUD hud : HUDManager.getHUDs()) {
                                builder.suggest(hud.getHUDName());
                            }
                            return builder.buildFuture();
                        })
                        .then(argument("name", StringArgumentType.word())
                            .executes(context -> {
                                String name = StringArgumentType.getString(context, "name");
                                int bits = RegReaderConfig.getDefaultBits();
                                int spacing = RegReaderConfig.getDefaultSpacing();
                                boolean inverted = RegReaderConfig.getDefaultInverted();
                                String hud = StringArgumentType.getString(context, "hud");

                                RegisterManager.addRegister(name, bits, spacing, inverted, hud);
                                RegisterInteractionHandler.setLastAddedRegisterName(name);
                                context.getSource().sendFeedback(Text.literal("Added register '" + name + "' to HUD '" + hud + "'"));
                                return 1;
                            })
                        )
                    )
                )
        );
    }
}