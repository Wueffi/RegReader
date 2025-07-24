package wueffi.regreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.css.Rect;

import java.util.*;

public class RegisterManager {
    private static final List<RedstoneRegister> registers = RegReaderConfig.registers;
    public static boolean hudEnabled = RegReaderConfig.isHudEnabled();
    private static final Logger LOGGER = LoggerFactory.getLogger("RegReader");

    public static void setHudEnabled(boolean enabled) {
        hudEnabled = enabled;
        RegReaderConfig.setHudEnabled(hudEnabled);
    }

    public static void setHudColor(String color) {
        if (color.matches("^#[0-9A-Fa-f]{6}$") || color.matches("^#[0-9A-Fa-f]{8}$")) {
            RegReaderConfig.setHudColor(color);
        } else {
            RegReaderConfig.setHudColor("#FFFFFFFF");
        }
    }

    public static List<RedstoneRegister> getRegisters() {
        return registers;
    }

    public static void addRegister(String name, Integer bits, Integer spacing, Boolean inverted) {
        if (bits == null) bits = 8;
        if (spacing == null) spacing = 2;
        if (inverted == null) inverted = false;

        RedstoneRegister register = new RedstoneRegister(name, bits, spacing, inverted);
        int index = 0;
        while (index < registers.size() && registers.get(index).name.compareTo(register.name) < 0) {
            index++;
        }
        RegReaderConfig.addRegister(index, register);
    }

    public static void removeRegister(String name) {
        RegReaderConfig.removeRegister(name);
    }

    public static void moveRegister(String name, String direction) {
        RedstoneRegister register = findRegisterByName(name);
        if (register == null) return;

        int index = registers.indexOf(register);
        if (direction.equalsIgnoreCase("up") && index > 0) {
            Collections.swap(registers, index, index - 1);
        } else if (direction.equalsIgnoreCase("down") && index < registers.size() - 1) {
            Collections.swap(registers, index, index + 1);
        }
        RegReaderConfig.save();
    }

    public static void moveRegister(String name, int position) {
        RedstoneRegister register = findRegisterByName(name);
        if (register == null || position < 0 || position >= registers.size()) return;

        registers.remove(register);
        registers.add(position, register);
        RegReaderConfig.save();
    }

    public static boolean renameRegister(String oldName, String newName) {
        RedstoneRegister register = findRegisterByName(oldName);
        if (register == null || findRegisterByName(newName) != null) {
            return false;
        }

        register.setName(newName);
        RegReaderConfig.save();
        return true;
    }

    static RedstoneRegister findRegisterByName(String name) {
        for (RedstoneRegister register : registers) {
            if (register.name.equals(name)) {
                return register;
            }
        }
        return null;
    }

    private static void sortRegistersAlphabetically() {
        registers.sort(Comparator.comparing(register -> register.name));
    }
}