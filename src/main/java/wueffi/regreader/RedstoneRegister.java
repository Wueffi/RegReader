package wueffi.regreader;

import net.minecraft.util.math.BlockPos;

public class RedstoneRegister {
    public String name;
    public int bits;
    public int spacing;
    public boolean inverted;
    private BlockPos position;
    private int lastReadValue;

    private String assignedHUD;

    public RedstoneRegister(String name, int bits, int spacing, boolean inverted, String assignedHUD) {
        this.name = name;
        this.bits = bits;
        this.spacing = spacing;
        this.inverted = inverted;
        this.assignedHUD = assignedHUD;
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

    public void setbits(Integer bits) {
        this.bits = bits;
    }

    public Integer getBits() {
        return bits;
    }

    public void setspacing(Integer spacing) {
        this.spacing = spacing;
    }

    public Integer getSpacing() {
        return spacing;
    }

    public void setinverted(boolean inverted) {
        this.inverted = inverted;
    }

    public boolean getInverted() {
        return inverted;
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

    public String getAssignedHUD() {
        return assignedHUD;
    }

    public void setAssignedHUD(String hudName) {
        this.assignedHUD = hudName;
    }
}
