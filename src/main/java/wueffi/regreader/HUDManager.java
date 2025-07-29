package wueffi.regreader;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.util.*;

public class HUDManager {
    private static final List<RegReaderHUD> huds = RegReaderConfig.huds;
    private static final Logger LOGGER = LoggerFactory.getLogger("RegReaderHUD");

    public static List<RegReaderHUD> getHUDs() {
        return huds;
    }

    public static List<String> getHUDNames() {
        List<String> list = new ArrayList<>(List.of());
        for(RegReaderHUD hud : huds) {
            list.add(hud.getHUDName());
        }
        return list;
    }

    public static void addHUD(String name, String color, Integer base, Boolean coloredNames, Integer width, Integer x, Integer y) {
        if (name == null || name.isBlank()) return;
        if (base == null) base = 10;
        if (coloredNames == null) coloredNames = false;
        if (width == null) width = 50;
        if (x == null) x = 10;
        if (y == null) y = 10;
        if (color == null || (!color.matches("^#[0-9A-Fa-f]{6}$") && !color.matches("^#[0-9A-Fa-f]{8}$"))) {
            color = "#FFFFFFFF";
        }

        RegReaderHUD hud = new RegReaderHUD(name, color, base, coloredNames, width, x, y);
        RegReaderConfig.addHUD(hud);
        LOGGER.info("Added HUD: " + name);
    }

    public static void removeHUD(String name) {
        RegReaderHUD hud = findHUDByName(name);
        if (hud != null) {
            RegReaderConfig.removeHUD(name);
            LOGGER.info("Removed HUD: " + name);
        }
    }

    public static boolean renameHUD(String oldName, String newName) {
        RegReaderHUD hud = findHUDByName(oldName);
        if (hud == null || findHUDByName(newName) != null) return false;

        hud.setHUDName(newName);
        RegReaderConfig.save();
        return true;
    }

    public static RegReaderHUD findHUDByName(String name) {
        for (RegReaderHUD hud : huds) {
            if (hud.getHUDName().equalsIgnoreCase(name)) {
                return hud;
            }
        }
        return null;
    }
}
