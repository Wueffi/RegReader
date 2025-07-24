package wueffi.regreader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

public class RegReaderConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "regreader.json");

    private static boolean hudEnabled = true;
    private static boolean coloredRegNames = true;
    private static String hudColor = "#56FFFFF";
    private static Integer RectangleWidth = 85;
    private static Integer hudXPos = 8;
    private static Integer hudYPos = 8;
    private static Integer displayBase = 10;
    public static final List<RedstoneRegister> registers = new ArrayList<>();

    public static boolean isHudEnabled() {
        return hudEnabled;
    }

    public static void setHudEnabled(boolean enabled) {
        hudEnabled = enabled;
        save();
    }

    public static String getHudColor() {
        return hudColor;
    }

    public static void setHudColor(String color) {
        hudColor = color;
        save();
    }

    public static boolean getColoredName() {
        return coloredRegNames;
    }

    public static void setColoredName(boolean setting) {
        coloredRegNames = setting;
        save();
    }

    public static Integer getRectangleWidth() {
        return RectangleWidth;
    }

    public static void setRectangleWidth(Integer Size) {
        RectangleWidth = Size;
        save();
    }

    public static Integer getXPos() {
        return hudXPos;
    }

    public static Integer getYPos() {
        return hudYPos;
    }

    public static void setHUDX(Integer Pos) {
        hudXPos = Pos;
        save();
    }

    public static void setHUDY(Integer Pos) {
        hudYPos = Pos;
        save();
    }

    public static int getDisplayBase() {
        return displayBase;
    }

    public static void setDisplayBase(int base) {
        if (base == 2 || base == 8 || base == 10 || base == 16) {
            displayBase = base;
            save();
        }
    }

    public static List<RedstoneRegister> getRegisters() {
        return registers;
    }

    public static void addRegister(int index, RedstoneRegister register) {
        registers.add(index, register);
        save();
    }

    public static void removeRegister(String name) {
        registers.removeIf(register -> register.name.equals(name));
        save();
    }

    public static void removeAll() {
        registers.clear();
        save();
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    hudEnabled = data.hudEnabled;
                    hudColor = data.hudColor;
                    coloredRegNames = data.coloredNames;
                    RectangleWidth = data.RectangleWidth;
                    hudXPos = data.XPos;
                    hudYPos = data.YPos;
                    displayBase = data.displayBase;
                    registers.clear();
                    registers.addAll(data.registers);
                }
                else {
                    resetToDefaults();
                }
            } catch (IOException | JsonSyntaxException e) {
                e.printStackTrace();
                resetToDefaults();
            }
        } else {
            resetToDefaults();
        }
    }

    public static void resetToDefaults() {
        hudEnabled = true;
        hudColor = "#FFFFFFFF";
        coloredRegNames = true;
        RectangleWidth = 85;
        hudXPos = 8;
        hudYPos = 8;
        displayBase = 10;
        registers.clear();
        save();
    }


    public static void save() {
        ConfigData data = new ConfigData(hudEnabled, hudColor, coloredRegNames, RectangleWidth, hudXPos, hudYPos, displayBase, registers);
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ConfigData {
        boolean hudEnabled;
        String hudColor;
        boolean coloredNames;
        int RectangleWidth;
        int XPos;
        int YPos;
        int displayBase;
        List<RedstoneRegister> registers;

        ConfigData(boolean hudEnabled, String hudColor, boolean coloredNames, int RectangleWidth, int hudXPos, int hudYPos, int displayBase, List<RedstoneRegister> registers) {
            this.hudEnabled = hudEnabled;
            this.hudColor = hudColor;
            this.coloredNames = coloredRegNames;
            this.RectangleWidth = RectangleWidth;
            this.XPos = hudXPos;
            this.YPos = hudYPos;
            this.displayBase = displayBase;
            this.registers = registers;
        }
    }
}