package wueffi.regreader;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;

public class HUDRenderer {
    public static void initialize() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!RegisterManager.isHudEnabled()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            int yOffset = 10;
            for (RedstoneRegister register : RegisterManager.getRegisters().values()) {
                String text = register.name + ": " + Integer.toBinaryString(register.readValue());
                drawContext.drawText(client.textRenderer, Text.literal(text), 12, yOffset, 0xFFFFFFFF, true);
                yOffset += 12;
            }
        });
    }
}