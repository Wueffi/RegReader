package wueffi.regreader;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.world.level.block.Block;
import net.minecraft.client.Minecraft;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.RedstoneLampBlock;
import net.minecraft.world.level.block.RedstoneTorchBlock;
import net.minecraft.world.level.block.RepeaterBlock;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.block.state.properties.BlockStateProperties;

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
        Minecraft client = Minecraft.getInstance();
        if (client.level == null || client.player == null) return;

        for (RedstoneRegister register : RegisterManager.getAllRegisters()) {
            BlockPos pos = register.getPosition();
            if (pos != null) {
                int value = readRedstoneSignal(client.level, pos, register.bits, register.spacing, register.inverted);
                register.setLastReadValue(value);
            }
        }
    }

    private static int readRedstoneSignal(Level world, BlockPos pos, int bits, int spacing, boolean inverted) {
        int value = 0;
        for (int i = 0; i < bits; i++) {
            BlockPos signalPos = pos.relative(Direction.DOWN, i * spacing);
            BlockState state = world.getBlockState(signalPos);
            Block block = state.getBlock();
            boolean isPowered = false;

            if (block instanceof RedstoneTorchBlock || block instanceof RedstoneLampBlock) {
                isPowered = state.getValue(BlockStateProperties.LIT);
            } else if (block instanceof RepeaterBlock) {
                isPowered = state.getValue(BlockStateProperties.POWERED);
            }

            if (isPowered) {
                value |= (1 << (bits - 1 - i));
            }
        }
        return inverted ? ~value & ((1 << bits) - 1) : value;
    }
}