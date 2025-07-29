package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.HUDManager;
import wueffi.regreader.RegReaderConfig;
import wueffi.regreader.RegReaderHUD;
import wueffi.regreader.RegisterManager;

import java.util.Arrays;
import java.util.List;

import static com.mojang.brigadier.arguments.IntegerArgumentType.integer;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class HUDCommand {
    private static final List<Integer> validBases = Arrays.asList(2, 8, 10, 16);
    private static final SuggestionProvider<FabricClientCommandSource> HUD_SUGGESTIONS = (context, builder) -> {
        builder.suggest("toggle");
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerCommand(dispatcher, "regreader");
        registerCommand(dispatcher, "rr");
    }

    private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String rootCommand) {
        dispatcher.register(literal(rootCommand)
                .then(literal("hud")
                        .then(argument("state", StringArgumentType.word())
                                .suggests(HUD_SUGGESTIONS)
                                .executes(context -> {
                                    String state = StringArgumentType.getString(context, "state");
                                    boolean hudEnabled = RegReaderConfig.isHudEnabled();

                                    if (state.equalsIgnoreCase("toggle")) {
                                        RegisterManager.setHudEnabled(!hudEnabled);
                                    } else if (state.equalsIgnoreCase("on")) {
                                        RegisterManager.setHudEnabled(true);
                                    } else if (state.equalsIgnoreCase("off")) {
                                        RegisterManager.setHudEnabled(false);
                                    } else {
                                        context.getSource().sendError(Text.literal("Invalid argument!"));
                                        return 0;
                                    }

                                    context.getSource().sendFeedback(Text.literal("All HUD's are now " + (RegReaderConfig.isHudEnabled() ? "enabled" : "disabled")));
                                    return 1;
                                }))
                        .then(literal("color")
                                .then(argument("HUD", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        for (RegReaderHUD hud : HUDManager.getHUDs()) {
                                            builder.suggest(hud.getHUDName());
                                        }
                                        return builder.buildFuture();
                                    })
                                    .then(argument("color", StringArgumentType.string())
                                            .executes(context -> {
                                                String color = StringArgumentType.getString(context, "color");
                                                String hud = StringArgumentType.getString(context, "HUD");
                                                if (color.matches("^#[0-9A-Fa-f]{8}$")) {
                                                    RegReaderHUD HUD = HUDManager.findHUDByName(hud);
                                                    if(HUD == null) {
                                                        context.getSource().sendError(Text.literal("HUD " + hud + " not found."));
                                                        return 0;
                                                    }
                                                    HUD.setHUDColor(color);
                                                    context.getSource().sendFeedback(Text.literal(hud + " color set to " + color));
                                                } else {
                                                    context.getSource().sendError(Text.literal("Invalid color format. Please use a valid hex color code (#AARRGGBB). Remember to use '"));
                                                }
                                                return 1;
                                            })
                                    )
                                )
                        )
                        .then(literal("add")
                                .then(argument("name", StringArgumentType.string())
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            if (!name.isEmpty()) {
                                                HUDManager.addHUD(name, "#FFFFFF", 10, true, 80, 100, 12);
                                                context.getSource().sendFeedback(Text.literal("Added HUD: " + name));
                                            } else {
                                                context.getSource().sendError(Text.literal("Name can not be empty!"));
                                            }
                                            return 1;
                                        })
                                )
                        )
                        .then(literal("remove")
                                .then(argument("name", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            for (RegReaderHUD hud : HUDManager.getHUDs()) {
                                                builder.suggest(hud.getHUDName());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            if (!name.isEmpty()) {
                                                HUDManager.removeHUD(name);
                                                context.getSource().sendFeedback(Text.literal("Removed HUD: " + name));
                                            } else {
                                                context.getSource().sendError(Text.literal("Name can not be empty!"));
                                            }
                                            return 1;
                                        })
                                )
                        )

                        .then(literal("rename")
                                .then(argument("name", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            for (RegReaderHUD hud : HUDManager.getHUDs()) {
                                                builder.suggest(hud.getHUDName());
                                            }
                                            return builder.buildFuture();
                                        })
                                        .then(argument("newName", StringArgumentType.string())
                                            .executes(context -> {
                                                String name = StringArgumentType.getString(context, "name");
                                                String newName = StringArgumentType.getString(context, "newName");
                                                if (name.isEmpty() || newName.isEmpty()) {
                                                    context.getSource().sendError(Text.literal("Name and new Name can not be empty!"));
                                                    return 0;
                                                }
                                                if(!HUDManager.renameHUD(name, newName)) {
                                                    context.getSource().sendError(Text.literal("Could not rename HUD."));
                                                    return 0;
                                                }
                                                context.getSource().sendFeedback(Text.literal("Renamed HUD: " + name + " to: " + newName));
                                                return 1;
                                            })
                                        )
                                )
                        )

                        .then(literal("setDisplayBase")
                                .then(argument("HUD", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            for (RegReaderHUD hud : HUDManager.getHUDs()) {
                                                builder.suggest(hud.getHUDName());
                                            }
                                            return builder.buildFuture();
                                        })
                                    .then(argument("base", integer(2, 16))
                                            .suggests((context, builder) -> {
                                                for (int base : validBases) {
                                                    builder.suggest(base);
                                                }
                                                return builder.buildFuture();
                                            })
                                            .executes(context -> {
                                                int base = IntegerArgumentType.getInteger(context, "base");
                                                String hud = StringArgumentType.getString(context, "HUD");
                                                if (validBases.contains(base)) {
                                                    RegReaderHUD HUD = HUDManager.findHUDByName(hud);
                                                    if(HUD == null) {
                                                        context.getSource().sendError(Text.literal("HUD " + hud + " not found."));
                                                        return 0;
                                                    }
                                                    HUD.setDisplayBase(base);
                                                    context.getSource().sendFeedback(Text.literal(hud + " base set to " + base));
                                                    return 1;
                                                } else {
                                                    context.getSource().sendError(Text.literal("Invalid base! Use 2, 8, 10, or 16."));
                                                    return 0;
                                                }
                                            })
                                    )
                                )
                        )

                        .then(literal("setColoredNames")
                                .then(argument("HUD", StringArgumentType.string())
                                        .suggests((context, builder) -> {
                                            for (RegReaderHUD hud : HUDManager.getHUDs()) {
                                                builder.suggest(hud.getHUDName());
                                            }
                                            return builder.buildFuture();
                                        })
                                    .then(argument("Boolean", BoolArgumentType.bool())
                                            .executes(context -> {
                                                boolean setting = BoolArgumentType.getBool(context, "Boolean");
                                                String hud = StringArgumentType.getString(context, "HUD");
                                                RegReaderHUD HUD = HUDManager.findHUDByName(hud);
                                                if(HUD == null) {
                                                    context.getSource().sendError(Text.literal("HUD " + hud + " not found."));
                                                    return 0;
                                                }
                                                HUD.setColoredNames(setting);
                                                context.getSource().sendFeedback(Text.literal(hud + " Colored names set to " + setting));
                                                return 1;
                                            })
                                    )
                                )
                        )

                        .then(literal("setHUDSize")
                            .then(argument("HUD", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        for (RegReaderHUD hud : HUDManager.getHUDs()) {
                                            builder.suggest(hud.getHUDName());
                                        }
                                        return builder.buildFuture();
                                    })
                                    .then(argument("Width", IntegerArgumentType.integer())
                                            .executes(context -> {
                                                int Width = IntegerArgumentType.getInteger(context, "Width");
                                                String hud = StringArgumentType.getString(context, "HUD");
                                                RegReaderHUD HUD = HUDManager.findHUDByName(hud);
                                                if(HUD == null) {
                                                    context.getSource().sendError(Text.literal("HUD " + hud + " not found."));
                                                    return 0;
                                                }
                                                HUD.setRectangleWidth(Width);
                                                context.getSource().sendFeedback(Text.literal(hud + " Width set to " + Width));
                                                return 1;
                                            })
                                    )
                            )
                        )

                        .then(literal("setHUDPos")
                            .then(argument("HUD", StringArgumentType.string())
                                    .suggests((context, builder) -> {
                                        for (RegReaderHUD hud : HUDManager.getHUDs()) {
                                            builder.suggest(hud.getHUDName());
                                        }
                                        return builder.buildFuture();
                                    })
                                    .then(literal("X")
                                            .then(argument("XPos", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        int X = IntegerArgumentType.getInteger(context, "XPos");
                                                        String hud = StringArgumentType.getString(context, "HUD");
                                                        RegReaderHUD HUD = HUDManager.findHUDByName(hud);
                                                        if(HUD == null) {
                                                            context.getSource().sendError(Text.literal("HUD " + hud + " not found."));
                                                            return 0;
                                                        }
                                                        HUD.setxPos(X);
                                                        context.getSource().sendFeedback(Text.literal(hud + " X-Position set to " + X));
                                                        return 1;
                                                    })
                                            )
                                    )
                                    .then(literal("Y")
                                            .then(argument("YPos", IntegerArgumentType.integer())
                                                    .executes(context -> {
                                                        int Y = IntegerArgumentType.getInteger(context, "YPos");
                                                        String hud = StringArgumentType.getString(context, "HUD");
                                                        RegReaderHUD HUD = HUDManager.findHUDByName(hud);
                                                        if(HUD == null) {
                                                            context.getSource().sendError(Text.literal("HUD " + hud + " not found."));
                                                            return 0;
                                                        }
                                                        HUD.setyPos(Y);
                                                        context.getSource().sendFeedback(Text.literal(hud + " Y-Position set to " + Y));
                                                        return 1;
                                                    })
                                            )
                                    )
                            )
                        )
                )
        );
    }
}
