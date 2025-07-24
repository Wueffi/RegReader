package wueffi.regreader;

import net.minecraft.util.math.BlockPos;

public class RedstoneRegister {
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
    }

    public int readValue() {
        return lastReadValue;
    }

}