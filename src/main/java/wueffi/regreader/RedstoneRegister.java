package wueffi.regreader;

import net.minecraft.util.math.BlockPos;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

public class RedstoneRegister {
    private static final Logger LOGGER = LogManager.getLogger("RedstoneRegister");

    public String name;
    public final int bits;
    public final int spacing;
    public final boolean inverted;
    private BlockPos position;
    private int lastReadValue;

    public RedstoneRegister(String name, int bits, int spacing, boolean inverted) {
        this.name = name;
        this.bits = bits;
        this.spacing = spacing;
        this.inverted = inverted;
    }

    public void setPosition(BlockPos position) {
        this.position = position;
        LOGGER.info("Register '{}': Position set to {}", name, position);
    }

    public BlockPos getPosition() {
        return position;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public void setLastReadValue(int value) {
        this.lastReadValue = value;
        LOGGER.info("Register '{}': Last read value set to {}", name, value);
    }

    public int readValue() {
        LOGGER.info("Register '{}': Returning last read value = {}", name, lastReadValue);
        return lastReadValue;
    }
}