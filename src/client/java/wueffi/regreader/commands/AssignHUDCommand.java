package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.HUDManager;
import wueffi.regreader.RedstoneRegister;
import wueffi.regreader.RegReaderHUD;
import wueffi.regreader.RegisterManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.argument;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.literal;

public class AssignHUDCommand {
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
                .then(literal("assignHUD")
                        .then(argument("name", StringArgumentType.word())
                                .suggests(REGISTER_SUGGESTIONS)
                                .then(argument("HUD", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            for (RegReaderHUD hud : HUDManager.getHUDs()) {
                                                builder.suggest(hud.getHUDName());
                                            }
                                            return builder.buildFuture();
                                        })
                                .executes(context -> {
                                    String name = StringArgumentType.getString(context, "name");
                                    RedstoneRegister Reg = RegisterManager.findRegisterByName(name);
                                    String hud = StringArgumentType.getString(context, "HUD");
                                    RegReaderHUD HUD = HUDManager.findHUDByName(hud);
                                    if(HUD == null) {
                                        context.getSource().sendError(Text.literal("HUD " + hud + " not found."));
                                        return 0;
                                    }
                                    if (Reg == null) {
                                        context.getSource().sendError(Text.literal("Couldn't find register '" + name + "'"));
                                        return 0;
                                    }
                                    Reg.setAssignedHUD(hud);
                                    context.getSource().sendFeedback(Text.literal("Assigned register '" + name + "' to HUD " + hud + "."));
                                    return 1;
                                })))));
    }
}