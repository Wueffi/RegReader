package wueffi.regreader;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.MinecraftClient;
import net.minecraft.text.Text;


import java.util.List;
import java.util.Set;

public class RegReaderModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Text.literal("RegReader Config"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory general = builder.getOrCreateCategory(Text.literal("General"));
            ConfigCategory defaults = builder.getOrCreateCategory(Text.literal("Defaults"));
            ConfigCategory registers = builder.getOrCreateCategory(Text.literal("Registers"));
            ConfigCategory huds = builder.getOrCreateCategory(Text.literal("HUDs"));

            // -- GENERAL --
            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Rendering enabled "), RegReaderConfig.isHudEnabled())
                    .setDefaultValue(false)
                    .setTooltip(Text.literal(""))
                    .setSaveConsumer(RegReaderConfig::setHudEnabled)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("Show HUD-Titles "), RegReaderConfig.getTitleMode())
                    .setDefaultValue(false)
                    .setTooltip(Text.literal(""))
                    .setSaveConsumer(RegReaderConfig::setTitleMode)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Text.literal("§cReset Config"), false)
                    .setDefaultValue(false)
                    .setSaveConsumer(clicked -> {
                        if (clicked) {
                            RegReaderConfig.resetToDefaults();
                        }
                    })
                    .setYesNoTextSupplier(clicked -> Text.literal("§cClick to Reset"))
                    .build());

            // -- RESET BUTTONS --
            registers.addEntry(entryBuilder.startBooleanToggle(Text.literal("§cReset Registers"), false)
                    .setDefaultValue(false)
                    .setSaveConsumer(clicked -> {
                        if (clicked) {
                            RegReaderConfig.removeAll();
                        }
                    })
                    .setYesNoTextSupplier(clicked -> Text.literal("§cClick to delete all"))
                    .build());

            huds.addEntry(entryBuilder.startBooleanToggle(Text.literal("§cReset HUD's"), false)
                    .setDefaultValue(false)
                    .setSaveConsumer(clicked -> {
                        if (clicked) {
                            RegReaderConfig.removeAllHUDs();
                        }
                    })
                    .setYesNoTextSupplier(clicked -> Text.literal("§cClick to delete all"))
                    .build());

            // -- DEFAULTS --
            defaults.addEntry(entryBuilder.startIntField(Text.literal("Default bits"), RegReaderConfig.getDefaultBits())
                    .setDefaultValue(8)
                    .setSaveConsumer(newBits -> {
                        if (!newBits.equals(RegReaderConfig.getDefaultBits())) {
                            if(33 > newBits && newBits > 0) {
                                RegReaderConfig.setDefaultBits(newBits);
                            }
                        }
                    })
                    .build());

            defaults.addEntry(entryBuilder.startIntField(Text.literal("Default Spacing"), RegReaderConfig.getDefaultSpacing())
                    .setDefaultValue(2)
                    .setSaveConsumer(newSpacing -> {
                        if (!newSpacing.equals(RegReaderConfig.getDefaultSpacing())) {
                            if(400 > newSpacing && newSpacing > 1) {
                                RegReaderConfig.setDefaultSpacing(newSpacing);
                            }
                        }
                    })
                    .build());

            defaults.addEntry(entryBuilder.startBooleanToggle(Text.literal("Default inverted"), RegReaderConfig.getDefaultInverted())
                    .setDefaultValue(false)
                    .setSaveConsumer(RegReaderConfig::setDefaultInverted)
                    .build());

            List<String> hudNames = HUDManager.getHUDNames();

            for (RedstoneRegister register : RegisterManager.getAllRegisters()) {
                String originalName = register.getName();


                SubCategoryBuilder regsub = entryBuilder.startSubCategory(Text.literal(originalName));

                regsub.add(entryBuilder.startStrField(Text.literal("Name"), originalName)
                        .setSaveConsumer(newName -> {
                            if (!newName.equals(originalName)) {
                                RegisterManager.renameRegister(originalName, newName);
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startIntField(Text.literal("Bits"), register.getBits())
                        .setDefaultValue(RegReaderConfig.getDefaultBits())
                        .setSaveConsumer(newBits -> {
                            if (!newBits.equals(register.getBits())) {
                                if(33 > newBits && newBits > 0) {
                                    register.setbits(newBits);
                                }
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startIntField(Text.literal("Spacing"), register.getSpacing())
                        .setDefaultValue(RegReaderConfig.getDefaultSpacing())
                        .setSaveConsumer(newSpacing -> {
                            if (!newSpacing.equals(register.getSpacing())) {
                                if(400 > newSpacing && newSpacing > 1) {
                                    register.setbits(newSpacing);
                                }
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startBooleanToggle(Text.literal("Inverted"), register.getInverted())
                        .setDefaultValue(RegReaderConfig.getDefaultInverted())
                        .setSaveConsumer(newInverted -> {
                            if (!(newInverted.equals(register.getInverted()))) {
                                register.setinverted(newInverted);
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startStringDropdownMenu(Text.literal("Assigned HUD"), register.getAssignedHUD())
                        .setSelections(hudNames)
                        .setSaveConsumer(register::setAssignedHUD)
                        .build());

                regsub.add(entryBuilder.startBooleanToggle(Text.literal("§cDelete this Register"), false)
                        .setDefaultValue(false)
                        .setSaveConsumer(clicked -> {
                            if (clicked) {
                                RegisterManager.removeRegister(register.getName());
                            }
                        })
                        .setYesNoTextSupplier(clicked -> Text.literal("§cClick to Delete"))
                        .build());

                registers.addEntry(regsub.build());
            }

            for (RegReaderHUD hud : HUDManager.getHUDs()) {
                String originalName = hud.getHUDName();

                SubCategoryBuilder hudsub = entryBuilder.startSubCategory(Text.literal(originalName));

                hudsub.add(entryBuilder.startStrField(Text.literal("Name"), originalName)
                        .setSaveConsumer(newName -> {
                            if (!newName.equals(originalName)) {
                                hud.setHUDName(newName);
                            }
                        })
                        .build());

                hudsub.add(entryBuilder.startAlphaColorField(Text.literal("Color (#AARRGGBB)"), (int) Long.parseLong(hud.getHUDColor().replaceFirst("^#", ""), 16))
                        .setDefaultValue(0xffffffff)
                        .setTooltip(Text.literal("HUD text color in ARGB format"))
                        .setSaveConsumer(newColorInt -> {
                            String hexString = String.format("#%08X", newColorInt);
                            hud.setHUDColor(hexString);
                        })
                        .build());

                hudsub.add(entryBuilder.startIntField(Text.literal("Display Base"), hud.getDisplayBase())
                        .setDefaultValue(2)
                        .setTooltip(Text.literal("Base: 2, 8, 10 or 16"))
                        .setSaveConsumer(hud::setDisplayBase)
                        .build());

                hudsub.add(entryBuilder.startBooleanToggle(Text.literal("Colored Names"), hud.getColoredNames())
                        .setDefaultValue(true)
                        .setSaveConsumer(hud::setColoredNames)
                        .build());

                hudsub.add(entryBuilder.startIntField(Text.literal("Rectangle Width"), hud.getRectangleWidth())
                        .setDefaultValue(80)
                        .setTooltip(Text.literal("Width of HUD"))
                        .setSaveConsumer(hud::setRectangleWidth)
                        .build());

                hudsub.add(entryBuilder.startIntField(Text.literal("X Position"), hud.getxPos())
                        .setDefaultValue(12)
                        .setTooltip(Text.literal("Horizontal screen position"))
                        .setSaveConsumer(hud::setxPos)
                        .build());

                hudsub.add(entryBuilder.startIntField(Text.literal("Y Position"), hud.getyPos())
                        .setDefaultValue(10)
                        .setTooltip(Text.literal("Vertical screen position"))
                        .setSaveConsumer(hud::setyPos)
                        .build());

                hudsub.add(entryBuilder.startBooleanToggle(Text.literal("§cDelete this HUD"), false)
                        .setDefaultValue(false)
                        .setSaveConsumer(clicked -> {
                            if (clicked) {
                                HUDManager.removeHUD(originalName);
                            }
                        })
                        .setYesNoTextSupplier(state -> Text.literal("§cClick to Delete"))
                        .build());

                huds.addEntry(hudsub.build());
            }


            return builder.build();
        };
    }
}
