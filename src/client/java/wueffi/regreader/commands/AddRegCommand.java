package wueffi.regreader.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.arguments.IntegerArgumentType;
import com.mojang.brigadier.arguments.StringArgumentType;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import wueffi.regreader.RegisterInteractionHandler;
import wueffi.regreader.RegisterManager;
import static net.fabricmc.fabric.api.client.command.v2.ClientCommandManager.*;

public class AddRegCommand {
    public static void register(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        dispatcher.register(literal("regreader")
                .then(literal("addreg")
                        .then(argument("name", StringArgumentType.word())
                                .then(argument("bits", IntegerArgumentType.integer(1))
                                        .then(argument("spacing", IntegerArgumentType.integer(1))
                                                .then(argument("inverted", StringArgumentType.word())
                                                        .executes(context -> {
                                                            String name = StringArgumentType.getString(context, "name");
                                                            int bits = IntegerArgumentType.getInteger(context, "bits");
                                                            int spacing = IntegerArgumentType.getInteger(context, "spacing");
                                                            boolean inverted = Boolean.parseBoolean(StringArgumentType.getString(context, "inverted"));
                                                            RegisterManager.addRegister(name, bits, spacing, inverted);
                                                            RegisterInteractionHandler.setLastAddedRegisterName(name); // Set the last added register
                                                            return 1;
                                                        })))))));
    }
}