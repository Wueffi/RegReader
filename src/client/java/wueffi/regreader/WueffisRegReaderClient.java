package wueffi.regreader;

import net.fabricmc.api.ClientModInitializer;
import wueffi.regreader.RegReaderConfig;
import wueffi.regreader.commands.AddRegCommand;
import wueffi.regreader.commands.DeleteRegCommand;
import wueffi.regreader.commands.EnableHUDCommand;
import wueffi.regreader.commands.DisableHUDCommand;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;

public class WueffisRegReaderClient implements ClientModInitializer {
    public static void initialize() {
        // Load config
        RegReaderConfig.load(); //owo-config??!

        // Initialize interaction handler
        RegisterInteractionHandler.initialize();

        // Initialize redstone reader
        RedstoneReader.initialize();

        //Initialize some more
        HUDRenderer.initialize();

        // Register commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            AddRegCommand.register(dispatcher);
            DeleteRegCommand.register(dispatcher);
            EnableHUDCommand.register(dispatcher);
            DisableHUDCommand.register(dispatcher);
        });
    }

    public void onInitializeClient() {}
}