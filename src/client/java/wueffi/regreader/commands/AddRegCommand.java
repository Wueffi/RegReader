package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.BoolArgumentType;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.text.Text;
import wueffi.regreader.RegisterInteractionHandler;
import wueffi.regreader.RegisterManager;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class AddRegCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        registerCommand(dispatcher, "regreader");
        registerCommand(dispatcher, "rr");
    }

    private static void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher, String rootCommand) {
        dispatcher.register(literal(rootCommand)
                .then(literal("addreg")
                        .then(argument("name", StringArgumentType.word())
                                .then(argument("bits", IntegerArgumentType.integer(1))
                                        .then(argument("spacing", IntegerArgumentType.integer(1))
                                                .then(argument("inverted", BoolArgumentType.bool())
                                                        .executes(context -> {
                                                            String name = StringArgumentType.getString(context, "name");
                                                            int bits = IntegerArgumentType.getInteger(context, "bits");
                                                            int spacing = IntegerArgumentType.getInteger(context, "spacing");
                                                            boolean inverted = BoolArgumentType.getBool(context, "inverted");
                                                            RegisterManager.addRegister(name, bits, spacing, inverted);
                                                            RegisterInteractionHandler.setLastAddedRegisterName(name);
                                                            context.getSource().sendFeedback(Text.literal("Added register '" + name + "'"));
                                                            return 1;
                                                        })))))));
    }
}