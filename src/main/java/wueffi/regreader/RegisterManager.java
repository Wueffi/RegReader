package wueffi.regreader;

import java.util.Map;

public class RegisterManager {
    public static boolean isHudEnabled() {
        return RegReaderConfig.isHudEnabled();
    }

    public static void setHudEnabled(boolean enabled) {
        RegReaderConfig.setHudEnabled(enabled);
    }

    public static Map<String, RedstoneRegister> getRegisters() {
        return RegReaderConfig.getRegisters();
    }

    public static void addRegister(String name, int bits, int spacing, boolean inverted) {
        RedstoneRegister register = new RedstoneRegister(name, bits, spacing, inverted);
        RegReaderConfig.addRegister(name, register);
    }

    public static void removeRegister(String name) {
        RegReaderConfig.removeRegister(name);
    }
}