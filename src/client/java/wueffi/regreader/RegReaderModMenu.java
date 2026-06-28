package wueffi.regreader;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.network.chat.Component;


import java.util.List;

public class RegReaderModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.literal("RegReader Config"));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory general = builder.getOrCreateCategory(Component.literal("General"));
            ConfigCategory defaults = builder.getOrCreateCategory(Component.literal("Defaults"));
            ConfigCategory registers = builder.getOrCreateCategory(Component.literal("Registers"));
            ConfigCategory huds = builder.getOrCreateCategory(Component.literal("HUDs"));

            // -- GENERAL --
            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Rendering enabled "), RegReaderConfig.isHudEnabled())
                    .setDefaultValue(false)
                    .setTooltip(Component.literal(""))
                    .setSaveConsumer(RegReaderConfig::setHudEnabled)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("Show HUD-Titles "), RegReaderConfig.getTitleMode())
                    .setDefaultValue(false)
                    .setTooltip(Component.literal(""))
                    .setSaveConsumer(RegReaderConfig::setTitleMode)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.literal("§cReset Config"), false)
                    .setDefaultValue(false)
                    .setSaveConsumer(clicked -> {
                        if (clicked) {
                            RegReaderConfig.resetToDefaults();
                        }
                    })
                    .setYesNoTextSupplier(clicked -> Component.literal("§cClick to Reset"))
                    .build());

            // -- RESET BUTTONS --
            registers.addEntry(entryBuilder.startBooleanToggle(Component.literal("§cReset Registers"), false)
                    .setDefaultValue(false)
                    .setSaveConsumer(clicked -> {
                        if (clicked) {
                            RegReaderConfig.removeAll();
                        }
                    })
                    .setYesNoTextSupplier(clicked -> Component.literal("§cClick to delete all"))
                    .build());

            huds.addEntry(entryBuilder.startBooleanToggle(Component.literal("§cReset HUD's"), false)
                    .setDefaultValue(false)
                    .setSaveConsumer(clicked -> {
                        if (clicked) {
                            RegReaderConfig.removeAllHUDs();
                        }
                    })
                    .setYesNoTextSupplier(clicked -> Component.literal("§cClick to delete all"))
                    .build());

            // -- DEFAULTS --
            defaults.addEntry(entryBuilder.startIntField(Component.literal("Default bits"), RegReaderConfig.getDefaultBits())
                    .setDefaultValue(8)
                    .setSaveConsumer(newBits -> {
                        if (!newBits.equals(RegReaderConfig.getDefaultBits())) {
                            if(33 > newBits && newBits > 0) {
                                RegReaderConfig.setDefaultBits(newBits);
                            }
                        }
                    })
                    .build());

            defaults.addEntry(entryBuilder.startIntField(Component.literal("Default Spacing"), RegReaderConfig.getDefaultSpacing())
                    .setDefaultValue(2)
                    .setSaveConsumer(newSpacing -> {
                        if (!newSpacing.equals(RegReaderConfig.getDefaultSpacing())) {
                            if(400 > newSpacing && newSpacing > 1) {
                                RegReaderConfig.setDefaultSpacing(newSpacing);
                            }
                        }
                    })
                    .build());

            defaults.addEntry(entryBuilder.startBooleanToggle(Component.literal("Default inverted"), RegReaderConfig.getDefaultInverted())
                    .setDefaultValue(false)
                    .setSaveConsumer(RegReaderConfig::setDefaultInverted)
                    .build());

            List<String> hudNames = HUDManager.getHUDNames();

            for (RedstoneRegister register : RegisterManager.getAllRegisters()) {
                String originalName = register.getName();


                SubCategoryBuilder regsub = entryBuilder.startSubCategory(Component.literal(originalName));

                regsub.add(entryBuilder.startStrField(Component.literal("Name"), originalName)
                        .setSaveConsumer(newName -> {
                            if (!newName.equals(originalName)) {
                                RegisterManager.renameRegister(originalName, newName);
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startIntField(Component.literal("Bits"), register.getBits())
                        .setDefaultValue(RegReaderConfig.getDefaultBits())
                        .setSaveConsumer(newBits -> {
                            if (!newBits.equals(register.getBits())) {
                                if(33 > newBits && newBits > 0) {
                                    register.setbits(newBits);
                                }
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startIntField(Component.literal("Spacing"), register.getSpacing())
                        .setDefaultValue(RegReaderConfig.getDefaultSpacing())
                        .setSaveConsumer(newSpacing -> {
                            if (!newSpacing.equals(register.getSpacing())) {
                                if(400 > newSpacing && newSpacing > 1) {
                                    register.setbits(newSpacing);
                                }
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startBooleanToggle(Component.literal("Inverted"), register.getInverted())
                        .setDefaultValue(RegReaderConfig.getDefaultInverted())
                        .setSaveConsumer(newInverted -> {
                            if (!(newInverted.equals(register.getInverted()))) {
                                register.setinverted(newInverted);
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startStringDropdownMenu(Component.literal("Assigned HUD"), register.getAssignedHUD())
                        .setSelections(hudNames)
                        .setSaveConsumer(register::setAssignedHUD)
                        .build());

                regsub.add(entryBuilder.startBooleanToggle(Component.literal("§cDelete this Register"), false)
                        .setDefaultValue(false)
                        .setSaveConsumer(clicked -> {
                            if (clicked) {
                                RegisterManager.removeRegister(register.getName());
                            }
                        })
                        .setYesNoTextSupplier(clicked -> Component.literal("§cClick to Delete"))
                        .build());

                registers.addEntry(regsub.build());
            }

            for (RegReaderHUD hud : HUDManager.getHUDs()) {
                String originalName = hud.getHUDName();

                SubCategoryBuilder hudsub = entryBuilder.startSubCategory(Component.literal(originalName));

                hudsub.add(entryBuilder.startStrField(Component.literal("Name"), originalName)
                        .setSaveConsumer(newName -> {
                            if (!newName.equals(originalName)) {
                                hud.setHUDName(newName);
                            }
                        })
                        .build());

                hudsub.add(entryBuilder.startAlphaColorField(Component.literal("Color (#AARRGGBB)"), (int) Long.parseLong(hud.getHUDColor().replaceFirst("^#", ""), 16))
                        .setDefaultValue(0xffffffff)
                        .setTooltip(Component.literal("HUD text color in ARGB format"))
                        .setSaveConsumer(newColorInt -> {
                            String hexString = String.format("#%08X", newColorInt);
                            hud.setHUDColor(hexString);
                        })
                        .build());

                hudsub.add(entryBuilder.startIntField(Component.literal("Display Base"), hud.getDisplayBase())
                        .setDefaultValue(2)
                        .setTooltip(Component.literal("Base: 2, 8, 10 or 16"))
                        .setSaveConsumer(hud::setDisplayBase)
                        .build());

                hudsub.add(entryBuilder.startBooleanToggle(Component.literal("Colored Names"), hud.getColoredNames())
                        .setDefaultValue(true)
                        .setSaveConsumer(hud::setColoredNames)
                        .build());

                hudsub.add(entryBuilder.startIntField(Component.literal("Rectangle Width"), hud.getRectangleWidth())
                        .setDefaultValue(80)
                        .setTooltip(Component.literal("Width of HUD"))
                        .setSaveConsumer(hud::setRectangleWidth)
                        .build());

                hudsub.add(entryBuilder.startIntField(Component.literal("X Position"), hud.getxPos())
                        .setDefaultValue(12)
                        .setTooltip(Component.literal("Horizontal screen position"))
                        .setSaveConsumer(hud::setxPos)
                        .build());

                hudsub.add(entryBuilder.startIntField(Component.literal("Y Position"), hud.getyPos())
                        .setDefaultValue(10)
                        .setTooltip(Component.literal("Vertical screen position"))
                        .setSaveConsumer(hud::setyPos)
                        .build());

                hudsub.add(entryBuilder.startBooleanToggle(Component.literal("§cDelete this HUD"), false)
                        .setDefaultValue(false)
                        .setSaveConsumer(clicked -> {
                            if (clicked) {
                                HUDManager.removeHUD(originalName);
                            }
                        })
                        .setYesNoTextSupplier(state -> Component.literal("§cClick to Delete"))
                        .build());

                huds.addEntry(hudsub.build());
            }


            return builder.build();
        };
    }
}
