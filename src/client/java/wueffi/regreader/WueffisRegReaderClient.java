package wueffi.regreader;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.client.keybinding.v1.KeyBindingHelper;
import net.minecraft.client.KeyMapping;
import net.minecraft.resources.Identifier;
import wueffi.regreader.commands.*;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import com.mojang.blaze3d.platform.InputConstants;
import org.lwjgl.glfw.GLFW;

import static wueffi.regreader.RegisterManager.hudEnabled;

public class WueffisRegReaderClient implements ClientModInitializer {
    private static final Logger LOGGER = LoggerFactory.getLogger("RegReader");
    public static KeyMapping toggleHudKey;

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
            AssignHUDCommand.register(dispatcher);
            DefaultAddCommand.register(dispatcher);
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
        toggleHudKey = KeyBindingHelper.registerKeyBinding(new KeyMapping(
                "key.regreader.toggle_hud",
                InputConstants.KEY_F9,
                KeyMapping.Category.register(Identifier.parse("category.regreader"))
        ));
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            while (toggleHudKey.consumeClick()) {
                RegisterManager.setHudEnabled(!hudEnabled);
            }
        });
        LOGGER.info("RegReader activated!");
    }
}