package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.suggestion.SuggestionProvider;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.RedstoneRegister;
import wueffi.regreader.RegisterManager;

import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class MoveRegCommand {
    private static final SuggestionProvider<FabricClientCommandSource> REGISTER_SUGGESTIONS = (context, builder) -> {
        for (RedstoneRegister register : RegisterManager.getRegisters()) {
            builder.suggest(register.name);
        }
        return builder.buildFuture();
    };

    private static final SuggestionProvider<FabricClientCommandSource> DIRECTION_SUGGESTIONS = (context, builder) -> {
        builder.suggest("up");
        builder.suggest("down");
        return builder.buildFuture();
    };

    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerCommand(dispatcher, "regreader");
        registerCommand(dispatcher, "rr");
    }

    private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String rootCommand) {
        dispatcher.register(literal(rootCommand)
                .then(literal("movereg")
                        .then(argument("name", StringArgumentType.word())
                                .suggests(REGISTER_SUGGESTIONS)
                                .then(argument("direction", StringArgumentType.word())
                                        .suggests(DIRECTION_SUGGESTIONS)
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            String direction = StringArgumentType.getString(context, "direction");
                                            RegisterManager.moveRegister(name, direction);
                                            context.getSource().sendFeedback(Text.literal("Moved register '" + name + "' " + direction));
                                            return 1;
                                        }))
                                .then(argument("position", IntegerArgumentType.integer(0))
                                        .executes(context -> {
                                            String name = StringArgumentType.getString(context, "name");
                                            int position = IntegerArgumentType.getInteger(context, "position");
                                            RegisterManager.moveRegister(name, position);
                                            context.getSource().sendFeedback(Text.literal("Moved register '" + name + "' to position " + position));
                                            return 1;
                                        })))));
    }
}