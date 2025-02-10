package wueffi.regreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class RegisterManager {
    private static final List<RedstoneRegister> registers = new ArrayList<>();
    public static boolean hudEnabled = true;
    private static String hudColor = "#66ffffff";
    private static final Logger LOGGER = LoggerFactory.getLogger("RegReader");

    public static boolean isHudEnabled() {
        return hudEnabled;
    }

    public static void setHudEnabled(boolean enabled) {
        hudEnabled = enabled;
    }

    public static String getHudColor() {
        return hudColor;
    }

    public static void setHudColor(String color) {
        if (color.matches("^#[0-9A-Fa-f]{6}$") || color.matches("^#[0-9A-Fa-f]{8}$")) {
            hudColor = color;
        } else {
            hudColor = "#66ffffff";
        }
        LOGGER.info("hudcolor" + hudColor);
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
        registers.add(index, register);
        RegReaderConfig.save();
    }

    public static void removeRegister(String name) {
        registers.removeIf(register -> register.name.equals(name));
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
    }

    public static void moveRegister(String name, int position) {
        RedstoneRegister register = findRegisterByName(name);
        if (register == null || position < 0 || position >= registers.size()) return;

        registers.remove(register);
        registers.add(position, register);
    }

    public static boolean renameRegister(String oldName, String newName) {
        RedstoneRegister register = findRegisterByName(oldName);  // Find the register by old name
        if (register == null || findRegisterByName(newName) != null) {
            return false;  // Return false if register is not found or new name already exists
        }

        register.setName(newName);  // Update the register's name
        return true;  // Successfully renamed
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