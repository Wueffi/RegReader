package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.network.chat.Component;
import wueffi.regreader.utils.ProfileUtils;

import java.util.List;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommands.*;

public class ProfileCommand {
    private static final SuggestionProvider<FabricClientCommandSource> PROFILE_NAME_SUGGESTIONS = (context, builder) -> {
        List<String> profiles = ProfileUtils.getAvailableProfiles();
        for (String profile : profiles) {
            builder.suggest(profile);
        }
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerCommand(dispatcher, "regreader");
        registerCommand(dispatcher, "rr");
    }

    private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String rootCommand) {
        dispatcher.register(literal(rootCommand)
                .then(literal("profile")
                        .then(literal("save")
                                .then(argument("name", StringArgumentType.word())
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            if (ProfileUtils.saveProfile(name)) {
                                                context.getSource().sendFeedback(Component.literal("Profile '" + name + "' saved."));
                                            } else {
                                                context.getSource().sendError(Component.literal("Failed to save profile '" + name + "'!"));
                                            }
                                            return 1;
                                        })))
                        .then(literal("load")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests(PROFILE_NAME_SUGGESTIONS)
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            if (ProfileUtils.loadProfile(name)) {
                                                context.getSource().sendFeedback(Component.literal("Loaded profile '" + name + "'."));
                                            } else {
                                                context.getSource().sendError(Component.literal("Profile '" + name + "' not found!"));
                                            }
                                            return 1;
                                        })))
                        .then(literal("delete")
                                .then(argument("name", StringArgumentType.word())
                                        .suggests(PROFILE_NAME_SUGGESTIONS)
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            if (ProfileUtils.deleteProfile(name)) {
                                                context.getSource().sendFeedback(Component.literal("Deleted profile '" + name + "'."));
                                            } else {
                                                context.getSource().sendError(Component.literal("Profile '" + name + "' not found!"));
                                            }
                                            return 1;
                                        })))
                ));
    }
}