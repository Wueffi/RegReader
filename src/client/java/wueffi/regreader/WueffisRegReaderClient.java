package wueffi.regreader;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.option.KeyBinding;
import wueffi.regreader.commands.*;
import wueffi.regreader.RegisterManager;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import net.minecraft.client.util.InputUtil;
import org.lwjgl.glfw.GLFW;

import static wueffi.regreader.RegisterManager.hudEnabled;

public class WueffisRegReaderClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("RegReader");
    public static KeyBinding toggleHudKey;

    public static void initialize() {
        // Initialize interaction handler
        RegisterInteractionHandler.initialize();

        // Initialize redstone reader
        RedstoneReader.initialize();

        // Initialize HUD renderer
        HUDRenderer.initialize();

        // Register commands
        ClientCommandRegistrationCallback.EVENT.register((dispatcher, registryAccess) -> {
            AddRegCommand.register(dispatcher);
            DeleteRegCommand.register(dispatcher);
            HUDCommand.register(dispatcher);
            MoveRegCommand.register(dispatcher);
            RenameRegCommand.register(dispatcher);
            ConfigCommand.register(dispatcher);
            DeleteAllCommand.register(dispatcher);
            ProfileCommand.register(dispatcher);
        });

        // Load config
        LOGGER.info("Loading Config..");
        RegReaderConfig.load();
    }
    @Override
    public void onInitializeClient() {
        LOGGER.info("Registering Commands...");
        initialize();
        LOGGER.info("Registering Keybind...");
        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyBinding(
                "key.regreader.toggle_hud",
                InputUtil.Type.KEYSYM,
                GLFW.GLFW_KEY_F9,
                "category.regreader"
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.wasPressed()) {
                RegisterManager.setHudEnabled(!hudEnabled);
            }
        });
        LOGGER.info("RegReader activated!");
    }
}