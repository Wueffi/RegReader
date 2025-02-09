package wueffi.regreader;

import net.fabricmc.api.ModInitializer;
import net.fabricmc.api.ClientModInitializer;

public class WueffisRegReader implements ModInitializer {
    @Override
    public void onInitialize() {
        // Server-side initialization (if needed)
    }

    public static class Client implements ClientModInitializer {
        @Override
        public void onInitializeClient() {
            // Client-side initialization
            WueffisRegReaderClient.initialize();
        }
    }
}