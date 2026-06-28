package wueffi.regreader;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.fabricmc.fabric.api.event.player.UseBlockCallback;
import net.minecraft.core.BlockPos;
import net.minecraft.network.chat.Component;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.level.block.Block;
import net.minecraft.world.level.block.Blocks;

public class RegisterInteractionHandler {
    private static String lastAddedRegisterName = null;
    private static boolean messageSent = false;

    public static void initialize() {
        UseBlockCallback.EVENT.register((player, world, hand, hitResult) -> {
            if (lastAddedRegisterName != null && world.isClientSide()) {
                BlockPos pos = hitResult.getBlockPos();
                Block block = world.getBlockState(pos).getBlock();

                if (block == Blocks.REDSTONE_LAMP || block == Blocks.REDSTONE_TORCH || block == Blocks.REPEATER) {
                    RedstoneRegister register = RegisterManager.findRegisterByName(lastAddedRegisterName);
                    if (register != null) {
                        register.setPosition(pos);
                        player.sendSystemMessage(Component.literal("Associated block at " + pos.getX() + ", " + pos.getY() + ", " + pos.getZ() + " with register '" + lastAddedRegisterName + "'"));
                        lastAddedRegisterName = null;
                        messageSent = false;
                        return InteractionResult.SUCCESS;
                    }
                }
            }
            return InteractionResult.PASS;
        });

        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (lastAddedRegisterName != null && !messageSent && client.player != null) {
                client.player.sendSystemMessage(
                        Component.literal("Right-click a lamp, torch, or repeater to associate it with register '" + lastAddedRegisterName + "'")
                );
                messageSent = true;
            }
        });
    }

    public static void setLastAddedRegisterName(String name) {
        lastAddedRegisterName = name;
        messageSent = false;
    }
}