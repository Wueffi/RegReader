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
            MinecraftClient client = MinecraftClient.getInstance();

            if (RegReaderConfig.isHudEnabled()) return;
            int baseHeight = 10;
            int lineHeight = 12;
            int registerCount = RegisterManager.getRegisters().size();
            int rectangleHeight = baseHeight + (registerCount - 1) * lineHeight + 2;

            boolean ColoredNames = RegReaderConfig.getColoredName();

            String hudColor = RegReaderConfig.getHudColor();
            int rectangleWidth = RegReaderConfig.getRectangleWidth();
            int displayBase = RegReaderConfig.getDisplayBase();
            int rectangleX = RegReaderConfig.getXPos();
            int rectangleY = RegReaderConfig.getYPos();

            // Remove the # if it exists
            if (hudColor.startsWith("#")) {
                hudColor = hudColor.substring(1);
            }

            try {
                long color = parseColor(hudColor);
                drawContext.fill(rectangleX, rectangleY, rectangleX + rectangleWidth, rectangleY + rectangleHeight, 0x55FFFFFF);
                drawContext.drawBorder(rectangleX, rectangleY, rectangleWidth, rectangleHeight, (int) color);

                int yOffset = rectangleY + 2;
                for (RedstoneRegister register : RegisterManager.getRegisters()) {
                    int value = register.readValue();
                    int bitSize = register.bits;

                    LOGGER.debug("Register {}: Value={}, BitSize={}", register.name, value, bitSize);

                    String formattedValue = formatValue(value, displayBase, bitSize);
                    LOGGER.debug("Formatted Value for {}: {}", register.name, formattedValue);

                    // Display register name in HUD border color, and value in white
                    String registerName = register.name + ": ";
                    String registerValue = formattedValue;

                    if (ColoredNames) {
                        drawContext.drawText(client.textRenderer, Text.literal(registerName).styled(style -> style.withColor((int) color)), rectangleX + 4, yOffset, (int) color, true);
                    }
                    else {
                        drawContext.drawText(client.textRenderer, Text.literal(registerName), rectangleX + 4, yOffset, 0xFFFFFFFF, true);
                    }
                    drawContext.drawText(client.textRenderer, Text.literal(registerValue), rectangleX + 4 + client.textRenderer.getWidth(registerName), yOffset, 0xFFFFFFFF, true);
                    yOffset += lineHeight;
                }
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid color format: {}", hudColor, e);
                drawContext.fill(rectangleX, rectangleY, rectangleX + rectangleWidth, rectangleY + rectangleHeight, 0xFFFFFF);
            }
        });
    }

    private static long parseColor(String hudColor) {
        try {
            if (hudColor.length() == 8) {
                return Long.parseLong(hudColor, 16);
            } else if (hudColor.length() == 6) {
                return Long.parseLong(hudColor, 16) | 0xFF000000; // Ensure full opacity
            } else {
                throw new NumberFormatException("Invalid color format");
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to parse HUD color: {}", hudColor, e);
            return 0xFFFFFFFF; // Default to white
        }
    }

    private static String formatValue(int value, int base, int bitSize) {
        switch (base) {
            case 2: // Binary
                return "0b" + String.format("%" + bitSize + "s", Integer.toBinaryString(value & ((1 << bitSize) - 1))).replace(' ', '0');
            case 8: // Octal
                int octalDigits = (int) Math.ceil(bitSize / 3.0);
                return "0o" + String.format("%" + octalDigits + "s", Integer.toOctalString(value & ((1 << bitSize) - 1))).replace(' ', '0');
            case 16: // Hexadecimal
                int hexDigits = (int) Math.ceil(bitSize / 4.0);
                return "0x" + String.format("%" + hexDigits + "s", Integer.toHexString(value & ((1 << bitSize) - 1))).replace(' ', '0').toUpperCase();
            default: // Decimal
                return String.valueOf(value);
        }
    }
}
