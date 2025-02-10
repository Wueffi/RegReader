package wueffi.regreader;

import net.fabricmc.fabric.api.client.rendering.v1.HudRenderCallback;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HUDRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger("RegReader");

    public static void initialize() {
        HudRenderCallback.EVENT.register((drawContext, tickDelta) -> {
            if (!RegisterManager.isHudEnabled()) return;

            MinecraftClient client = MinecraftClient.getInstance();
            if (client.player == null) return;

            int rectangleX = 8;
            int rectangleY = 8;
            int rectangleWidth = 85;
            int baseHeight = 10;
            int lineHeight = 12;
            int registerCount = RegisterManager.getRegisters().size();
            int rectangleHeight = baseHeight + (registerCount - 1) * lineHeight + 2;

            String hudColor = RegisterManager.getHudColor();

            // Remove the '#' character if it exists
            if (hudColor.startsWith("#")) {
                hudColor = hudColor.substring(1);
            }

            try {
                long color = 0;
                if (hudColor.length() == 8) {
                    color = Long.parseLong(hudColor, 16);
                } else if (hudColor.length() == 6) {
                    color = Long.parseLong(hudColor, 16) | 0x66FFFFFF;
                } else {
                    throw new NumberFormatException("Invalid color format");
                }

                drawContext.fill(rectangleX, rectangleY, rectangleX + rectangleWidth, rectangleY + rectangleHeight, (int) color);
                drawContext.drawBorder(rectangleX, rectangleY, rectangleWidth, rectangleHeight, 0xFFFFFFFF);

                int yOffset = rectangleY + 2;
                for (RedstoneRegister register : RegisterManager.getRegisters()) {
                    String text = register.name + ": " + register.readValue(); // Display decimal value
                    drawContext.drawText(client.textRenderer, Text.literal(text), 12, yOffset, 0xFFFFFFFF, true);
                    yOffset += lineHeight;
                }
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid color format: {}", hudColor);
                drawContext.fill(rectangleX, rectangleY, rectangleX + rectangleWidth, rectangleY + rectangleHeight, 0xFFFFFF); // Default to white
            }
        });
    }
}
