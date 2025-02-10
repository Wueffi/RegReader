package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.RegisterManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class HUDCommand {
    private static final SuggestionProvider<FabricClientCommandSource> HUD_SUGGESTIONS = (context, builder) -> {
        builder.suggest("toggle");
        builder.suggest("on");
        builder.suggest("off");
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("regreader")
                .then(literal("hud")
                        .then(argument("state", StringArgumentType.word())
                                .suggests(HUD_SUGGESTIONS)
                                .executes(context -> {
                                    String state = StringArgumentType.getString(context, "state");
                                    boolean hudEnabled = RegisterManager.isHudEnabled();

                                    if (state.equalsIgnoreCase("toggle")) {
                                        RegisterManager.setHudEnabled(!hudEnabled);
                                    } else if (state.equalsIgnoreCase("on")) {
                                        RegisterManager.setHudEnabled(true);
                                    } else if (state.equalsIgnoreCase("off")) {
                                        RegisterManager.setHudEnabled(false);
                                    } else {
                                        context.getSource().sendError(Text.literal("Invalid argument! Use 'toggle', 'on', or 'off'."));
                                        return 0;
                                    }

                                    context.getSource().sendFeedback(Text.literal("HUD is now " + (RegisterManager.isHudEnabled() ? "enabled" : "disabled")));
                                    return 1;
                                }))
                        .then(literal("color")
                                .then(argument("color", StringArgumentType.string())
                                        .executes(context -> {
                                            String color = StringArgumentType.getString(context, "color");
                                            if (color.matches("^#[0-9A-Fa-f]{8}$")) {
                                                RegisterManager.setHudColor(color);
                                                context.getSource().sendFeedback(Text.literal("HUD color set to " + color));
                                            } else {
                                                context.getSource().sendError(Text.literal("Invalid color format. Please use a valid hex color code (#AARRGGBB). Remember to use '"));
                                            }
                                            return 1;
                                        })
                                )
                        )
                ));
    }
}
