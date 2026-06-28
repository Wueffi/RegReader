package wueffi.regreader;

import net.fabricmc.fabric.api.client.rendering.v1.hud.HudElementRegistry;
import net.fabricmc.fabric.api.client.rendering.v1.hud.VanillaHudElements;
import net.minecraft.client.DeltaTracker;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.GuiGraphicsExtractor;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;
import net.minecraft.network.chat.TextColor;
import net.minecraft.resources.Identifier;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class HUDRenderer {
    private static final Logger LOGGER = LoggerFactory.getLogger("RegReader");

    public static void initialize() {
        HudElementRegistry.attachElementBefore(VanillaHudElements.CHAT, Identifier.fromNamespaceAndPath("regreader", "before_chat"), HUDRenderer::render);
    }

    private static void render(GuiGraphicsExtractor guiGraphics, DeltaTracker deltaTracker) {
        Minecraft client = Minecraft.getInstance();

        if (!RegReaderConfig.isHudEnabled()) return;

        for (RegReaderHUD hud : HUDManager.getHUDs()) {
            int baseHeight = 10;
            int lineHeight = 12;
            int registerCount = RegisterManager.getRegisters(hud.getHUDName()).size();
            int rectangleHeight = baseHeight + (registerCount - 1) * lineHeight + 2;

            String hudColor = hud.getHUDColor();
            int rectangleWidth = hud.getRectangleWidth();
            int displayBase = hud.getDisplayBase();
            int rectangleX = hud.getxPos();
            int rectangleY = hud.getyPos();
            boolean coloredNames = Boolean.TRUE.equals(hud.getColoredNames());

            if (hudColor.startsWith("#")) {
                hudColor = hudColor.substring(1);
            }

            try {
                long color = parseColor(hudColor);
                guiGraphics.fill(rectangleX, rectangleY, rectangleX + rectangleWidth, rectangleY + rectangleHeight, 0x55FFFFFF);
                renderOutline(guiGraphics, rectangleX, rectangleY, rectangleWidth, rectangleHeight, (int) color);

                var matrices = guiGraphics.pose();

                if (RegReaderConfig.getTitleMode()) {
                    matrices.pushMatrix();
                    float scale = 0.8f;
                    matrices.translate((float) hud.xPos, (float) hud.yPos - 8);
                    matrices.scale(scale, scale);

                    if (coloredNames) {
                        guiGraphics.text(client.font, Component.literal(hud.getHUDName()).withStyle(Style.EMPTY.withColor(TextColor.fromRgb((int) color))), 0, 0, (int) color, true);
                    } else {
                        guiGraphics.text(client.font, Component.literal(hud.getHUDName()), 0, 0, 0xFFFFFFFF, true);
                    }

                    matrices.popMatrix();
                }

                int yOffset = rectangleY + 2;
                for (RedstoneRegister register : RegisterManager.getRegisters(hud.getHUDName())) {
                    int value = register.readValue();
                    int bitSize = register.bits;

                    String formattedValue = formatValue(value, displayBase, bitSize);
                    String registerName = register.name + ": ";

                    if (coloredNames) {
                        guiGraphics.text(client.font, Component.literal(registerName).withStyle(Style.EMPTY.withColor(TextColor.fromRgb((int) color))), rectangleX + 4, yOffset, (int) color, true);
                    } else {
                        guiGraphics.text(client.font, Component.literal(registerName), rectangleX + 4, yOffset, 0xFFFFFFFF, true);
                    }
                    guiGraphics.text(client.font, Component.literal(formattedValue), rectangleX + 4 + client.font.width(registerName), yOffset, 0xFFFFFFFF, true);
                    yOffset += lineHeight;
                }
            } catch (NumberFormatException e) {
                LOGGER.error("Invalid HUD color format for '{}': {}", hud.getHUDName(), hudColor, e);
                guiGraphics.fill(rectangleX, rectangleY, rectangleX + rectangleWidth, rectangleY + rectangleHeight, 0xFF0000);
            }
        }
    }

    private static long parseColor(String hudColor) {
        try {
            if (hudColor.length() == 8) {
                return Long.parseLong(hudColor, 16);
            } else if (hudColor.length() == 6) {
                return Long.parseLong(hudColor, 16) | 0xFF000000;
            } else {
                throw new NumberFormatException("Invalid color format");
            }
        } catch (NumberFormatException e) {
            LOGGER.error("Failed to parse HUD color: {}", hudColor, e);
            return 0xFFFFFFFF;
        }
    }

    private static String formatValue(int value, int base, int bitSize) {
        switch (base) {
            case 2:
                return "0b" + String.format("%" + bitSize + "s", Integer.toBinaryString(value & ((1 << bitSize) - 1))).replace(' ', '0');
            case 8:
                int octalDigits = (int) Math.ceil(bitSize / 3.0);
                return "0o" + String.format("%" + octalDigits + "s", Integer.toOctalString(value & ((1 << bitSize) - 1))).replace(' ', '0');
            case 16:
                int hexDigits = (int) Math.ceil(bitSize / 4.0);
                return "0x" + String.format("%" + hexDigits + "s", Integer.toHexString(value & ((1 << bitSize) - 1))).replace(' ', '0').toUpperCase();
            default:
                return String.valueOf(value);
        }
    }

    private static void renderOutline(GuiGraphicsExtractor guiGraphics, int rectangleX, int rectangleY, int rectangleWidth, int rectangleHeight, int color) {
        guiGraphics.fill(rectangleX, rectangleY, rectangleX + rectangleWidth, rectangleY + 1, color);
        guiGraphics.fill(rectangleX, rectangleY + rectangleHeight - 1, rectangleX + rectangleWidth, rectangleY + rectangleHeight, color);
        guiGraphics.fill(rectangleX, rectangleY, rectangleX + 1, rectangleY + rectangleHeight, color);
        guiGraphics.fill(rectangleX + rectangleWidth - 1, rectangleY, rectangleX + rectangleWidth, rectangleY + rectangleHeight, color);
    }
}
