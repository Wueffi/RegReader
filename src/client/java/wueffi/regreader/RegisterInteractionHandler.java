package wueffi.regreader;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import net.minecraft.util.ActionResult;
import net.minecraft.util.math.BlockPos;

public class RegisterInteractionHandler {
    private static String lastAddedRegisterName = null;

    public static void initialize() {
        // Listen for block interactions
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (lastAddedRegisterName != null && player.getWorld().isClient) {
                BlockPos pos = hitResult.getBlockPos();
                Block block = world.getBlockState(pos).getBlock();

                // Check if the block is a lamp, torch, or repeater
                if (block == Blocks.REDSTONE_LAMP || block == Blocks.REDSTONE_TORCH || block == Blocks.REPEATER) {
                    // Associate the block coordinates with the last added register
                    wueffi.regreader.RedstoneRegister register = wueffi.regreader.RegisterManager.getRegisters().get(lastAddedRegisterName);
                    if (register != null) {
                        register.setPosition(pos);
                        player.sendMessage(Text.literal("Associated block at " + pos + " with register '" + lastAddedRegisterName + "'"), true);
                        lastAddedRegisterName = null; // Reset after association
                        return ActionResult.SUCCESS;
                    }
                }
            }
            return ActionResult.PASS;
        });

        // Reset lastAddedRegisterName after a short delay (optional)
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (lastAddedRegisterName != null && MinecraftClient.getInstance().player != null) {
                MinecraftClient.getInstance().player.sendMessage(Text.literal("Right-click a lamp, torch, or repeater to associate it with register '" + lastAddedRegisterName + "'"), true);
            }
        });
    }

    public static void setLastAddedRegisterName(String name) {
        lastAddedRegisterName = name;
    }
}