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
    private static boolean titleMode = true;
    private static Integer defaultBits = 8;
    private static Integer defaultSpacing = 2;
    private static boolean defaultInverted = false;
    public static final List<RedstoneRegister> registers = new ArrayList<>();
    public static final List<RegReaderHUD> huds = new ArrayList<>();
    static final RegReaderHUD sampleHUD = new RegReaderHUD("Default", "#ffffffff", 10, true, 80, 12, 10);

    public static void addHUD(RegReaderHUD hud) {
        huds.add(hud);
        save();
    }

    public static void removeHUD(String name) {
        huds.removeIf(h -> h.getHUDName().equalsIgnoreCase(name));
        save();
    }

    public static boolean isHudEnabled() {
        return hudEnabled;
    }

    public static void setHudEnabled(boolean enabled) {
        hudEnabled = enabled;
        save();
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

    public static void removeAllHUDs() {
        huds.clear();
        save();
    }

    public static Integer getDefaultBits() {
        return defaultBits;
    }

    public static Integer getDefaultSpacing() {
        return defaultSpacing;
    }

    public static boolean getDefaultInverted() {
        return defaultInverted;
    }

    public static void setDefaultBits(Integer newBits) {
        defaultBits = newBits;
    }

    public static void setDefaultSpacing(Integer newSpacing) {
        defaultBits = newSpacing;
    }

    public static void setDefaultInverted(boolean newInverted) {
        defaultInverted = newInverted;
    }

    public static boolean getTitleMode() {
        return titleMode;
    }

    public static void setTitleMode(boolean newMode) {
        titleMode = newMode;
        save();
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    hudEnabled = data.hudEnabled;
                    titleMode = data.titleMode;
                    registers.clear();
                    registers.addAll(data.registers);
                    huds.clear();
                    huds.addAll(data.huds);
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
        titleMode = true;
        defaultBits = 8;
        defaultSpacing = 2;
        defaultInverted = false;
        registers.clear();
        huds.clear();
        huds.add(sampleHUD);
        save();
    }


    public static void save() {
        ConfigData data = new ConfigData(hudEnabled, titleMode, defaultBits, defaultSpacing, defaultInverted, registers, huds);
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ConfigData {
        boolean hudEnabled;
        boolean titleMode;
        Integer defaultBits;
        Integer defaultSpacing;
        Boolean defaultInverted;
        List<RedstoneRegister> registers;
        List<RegReaderHUD> huds;

        ConfigData(boolean hudEnabled, boolean titleMode, Integer defaultBits, Integer defaultSpacing, boolean defaultInverted, List<RedstoneRegister> registers, List<RegReaderHUD> huds) {
            this.hudEnabled = hudEnabled;
            this.titleMode = titleMode;
            this.defaultBits = defaultBits;
            this.defaultSpacing = defaultSpacing;
            this.defaultInverted = defaultInverted;
            this.registers = registers;
            this.huds = huds;
        }
    }
}