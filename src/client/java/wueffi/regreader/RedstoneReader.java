package wueffi.regreader;

import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.MinecraftClient;
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

        for (RedstoneRegister register : RegisterManager.getRegisters().values()) {
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
            BlockPos signalPos = pos.offset(Direction.UP, i * spacing);
            int signalStrength = world.getEmittedRedstonePower(signalPos, Direction.UP);
            if (signalStrength > 0) {
                value |= (1 << (bits - 1 - i)); // Set the bit if the signal is present
            }
        }
        return inverted ? ~value & ((1 << bits) - 1) : value; // Invert the value if needed
    }
}