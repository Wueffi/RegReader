package wueffi.regreader;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import net.fabricmc.loader.api.FabricLoader;

import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class RegReaderConfig {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static final File CONFIG_FILE = new File(FabricLoader.getInstance().getConfigDir().toFile(), "wueffis_regreader.json");

    private static boolean hudEnabled = true;
    private static final Map<String, 
            RedstoneRegister> registers = new HashMap<>();

    public static boolean isHudEnabled() {
        return hudEnabled;
    }

    public static void setHudEnabled(boolean enabled) {
        hudEnabled = enabled;
        save();
    }

    public static Map<String, RedstoneRegister> getRegisters() {
        return registers;
    }

    public static void addRegister(String name, RedstoneRegister register) {
        registers.put(name, register);
        save();
    }

    public static void removeRegister(String name) {
        registers.remove(name);
        save();
    }

    public static void load() {
        if (CONFIG_FILE.exists()) {
            try (FileReader reader = new FileReader(CONFIG_FILE)) {
                ConfigData data = GSON.fromJson(reader, ConfigData.class);
                if (data != null) {
                    hudEnabled = data.hudEnabled;
                    registers.clear();
                    registers.putAll(data.registers);
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static void save() {
        ConfigData data = new ConfigData(hudEnabled, registers);
        try (FileWriter writer = new FileWriter(CONFIG_FILE)) {
            GSON.toJson(data, writer);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private static class ConfigData {
        boolean hudEnabled;
        Map<String, RedstoneRegister> registers;

        ConfigData(boolean hudEnabled, Map<String, RedstoneRegister> registers) {
            this.hudEnabled = hudEnabled;
            this.registers = registers;
        }
    }
}