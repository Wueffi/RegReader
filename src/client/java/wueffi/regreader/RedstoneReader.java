package wueffi.regreader;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.block.RepeaterBlock;
import net.minecraft.block.RedstoneTorchBlock;
import net.minecraft.block.RedstoneLampBlock;
import net.minecraft.client.MinecraftClient;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class RedstoneReader {
    private static int tickCounter = 0;

    public static void initialize() {
        ClientTickEvents.START_CLIENT_TICK.register(client -> {
            if (tickCounter++ >= 2) {
                tickCounter = 0;
                readAllRegisters();
            }
        });
    }

    private static void readAllRegisters() {
        MinecraftClient client = MinecraftClient.getInstance();
        if (client.world == null || client.player == null) return;

        for (RedstoneRegister register : RegisterManager.getRegisters()) {
            BlockPos pos = register.getPosition();
            if (pos != null) {
                int value = readRedstoneSignal(client.world, pos, register.bits, register.spacing, register.inverted);
                register.setLastReadValue(value);
            }
        }
    }

    private static int readRedstoneSignal(World world, BlockPos pos, int bits, int spacing, boolean inverted) {
        int value = 0;
        for (int i = 0; i < bits; i++) {
            // Calculate the position of the current bit (starting from the top)
            BlockPos signalPos = pos.offset(Direction.DOWN, i * spacing);
            Block block = world.getBlockState(signalPos).getBlock();
            boolean isPowered = false;

            if (block instanceof RedstoneTorchBlock) {
                isPowered = world.getBlockState(signalPos).get(Properties.LIT);
            } else if (block instanceof RepeaterBlock) {
                isPowered = world.getBlockState(signalPos).get(Properties.POWERED);
            } else if (block instanceof RedstoneLampBlock) {
                isPowered = world.getBlockState(signalPos).get(Properties.LIT);
            }
            if (isPowered) {
                value |= (1 << (bits - 1 - i)); // Set the bit if the block is powered (MSB at top)
            }
        }
        int finalValue = inverted ? ~value & ((1 << bits) - 1) : value; // Invert the value if needed
        return finalValue;
    }
}