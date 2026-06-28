package wueffi.regreader.utils;

import wueffi.regreader.RegReaderConfig;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class ProfileUtils {
    private static final Path PROFILE_DIR = new File("config/regreader/profiles").toPath();
    private static final Path CONFIG_FILE = new File("config/regreader.json").toPath();

    static {
        if (!Files.exists(PROFILE_DIR)) {
            try {
                Files.createDirectories(PROFILE_DIR);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public static boolean saveProfile(String profileName) {
        Path profilePath = PROFILE_DIR.resolve(profileName + ".json");
        try {
            Files.copy(CONFIG_FILE, profilePath);
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean loadProfile(String profileName) {
        Path profilePath = PROFILE_DIR.resolve(profileName + ".json");
        if (!Files.exists(profilePath)) {
            return false;
        }
        try {
            Files.copy(profilePath, CONFIG_FILE, java.nio.file.StandardCopyOption.REPLACE_EXISTING);
            RegReaderConfig.load();
            return true;
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static boolean deleteProfile(String profileName) {
        Path profilePath = PROFILE_DIR.resolve(profileName + ".json");
        try {
            return Files.deleteIfExists(profilePath);
        } catch (IOException e) {
            e.printStackTrace();
            return false;
        }
    }

    public static List<String> getAvailableProfiles() {
        File[] files = PROFILE_DIR.toFile().listFiles((dir, name) -> name.endsWith(".json"));
        if (files == null) return List.of();
        return Arrays.stream(files)
                .map(file -> file.getName().replace(".json", ""))
                .collect(Collectors.toList());
    }
}
