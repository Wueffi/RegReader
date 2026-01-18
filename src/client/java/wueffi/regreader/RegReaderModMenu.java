package wueffi.regreader;

import com.terraformersmc.modmenu.api.ConfigScreenFactory;
import com.terraformersmc.modmenu.api.ModMenuApi;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.network.chat.Component;


import java.util.List;
import java.util.Set;

public class RegReaderModMenu implements ModMenuApi {
    @Override
    public ConfigScreenFactory<?> getModConfigScreenFactory() {
        return parent -> {
            ConfigBuilder builder = ConfigBuilder.create().setParentScreen(parent).setTitle(Component.nullToEmpty(("RegReader Config")));
            ConfigEntryBuilder entryBuilder = builder.entryBuilder();

            ConfigCategory general = builder.getOrCreateCategory(Component.nullToEmpty("General"));
            ConfigCategory defaults = builder.getOrCreateCategory(Component.nullToEmpty("Defaults"));
            ConfigCategory registers = builder.getOrCreateCategory(Component.nullToEmpty("Registers"));
            ConfigCategory huds = builder.getOrCreateCategory(Component.nullToEmpty("HUDs"));

            // -- GENERAL --
            general.addEntry(entryBuilder.startBooleanToggle(Component.nullToEmpty("Rendering enabled "), RegReaderConfig.isHudEnabled())
                    .setDefaultValue(false)
                    .setTooltip(Component.nullToEmpty(""))
                    .setSaveConsumer(RegReaderConfig::setHudEnabled)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.nullToEmpty("Show HUD-Titles "), RegReaderConfig.getTitleMode())
                    .setDefaultValue(false)
                    .setTooltip(Component.nullToEmpty(""))
                    .setSaveConsumer(RegReaderConfig::setTitleMode)
                    .build());

            general.addEntry(entryBuilder.startBooleanToggle(Component.nullToEmpty("§cReset Config"), false)
                    .setDefaultValue(false)
                    .setSaveConsumer(clicked -> {
                        if (clicked) {
                            RegReaderConfig.resetToDefaults();
                        }
                    })
                    .setYesNoTextSupplier(clicked -> Component.nullToEmpty("§cClick to Reset"))
                    .build());

            // -- RESET BUTTONS --
            registers.addEntry(entryBuilder.startBooleanToggle(Component.nullToEmpty("§cReset Registers"), false)
                    .setDefaultValue(false)
                    .setSaveConsumer(clicked -> {
                        if (clicked) {
                            RegReaderConfig.removeAll();
                        }
                    })
                    .setYesNoTextSupplier(clicked -> Component.nullToEmpty("§cClick to delete all"))
                    .build());

            huds.addEntry(entryBuilder.startBooleanToggle(Component.nullToEmpty("§cReset HUD's"), false)
                    .setDefaultValue(false)
                    .setSaveConsumer(clicked -> {
                        if (clicked) {
                            RegReaderConfig.removeAllHUDs();
                        }
                    })
                    .setYesNoTextSupplier(clicked -> Component.nullToEmpty("§cClick to delete all"))
                    .build());

            // -- DEFAULTS --
            defaults.addEntry(entryBuilder.startIntField(Component.nullToEmpty("Default bits"), RegReaderConfig.getDefaultBits())
                    .setDefaultValue(8)
                    .setSaveConsumer(newBits -> {
                        if (!newBits.equals(RegReaderConfig.getDefaultBits())) {
                            if(33 > newBits && newBits > 0) {
                                RegReaderConfig.setDefaultBits(newBits);
                            }
                        }
                    })
                    .build());

            defaults.addEntry(entryBuilder.startIntField(Component.nullToEmpty("Default Spacing"), RegReaderConfig.getDefaultSpacing())
                    .setDefaultValue(2)
                    .setSaveConsumer(newSpacing -> {
                        if (!newSpacing.equals(RegReaderConfig.getDefaultSpacing())) {
                            if(400 > newSpacing && newSpacing > 1) {
                                RegReaderConfig.setDefaultSpacing(newSpacing);
                            }
                        }
                    })
                    .build());

            defaults.addEntry(entryBuilder.startBooleanToggle(Component.nullToEmpty("Default inverted"), RegReaderConfig.getDefaultInverted())
                    .setDefaultValue(false)
                    .setSaveConsumer(RegReaderConfig::setDefaultInverted)
                    .build());

            List<String> hudNames = HUDManager.getHUDNames();

            for (RedstoneRegister register : RegisterManager.getAllRegisters()) {
                String originalName = register.getName();


                SubCategoryBuilder regsub = entryBuilder.startSubCategory(Component.nullToEmpty(originalName));

                regsub.add(entryBuilder.startStrField(Component.nullToEmpty("Name"), originalName)
                        .setSaveConsumer(newName -> {
                            if (!newName.equals(originalName)) {
                                RegisterManager.renameRegister(originalName, newName);
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startIntField(Component.nullToEmpty("Bits"), register.getBits())
                        .setDefaultValue(RegReaderConfig.getDefaultBits())
                        .setSaveConsumer(newBits -> {
                            if (!newBits.equals(register.getBits())) {
                                if(33 > newBits && newBits > 0) {
                                    register.setbits(newBits);
                                }
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startIntField(Component.nullToEmpty("Spacing"), register.getSpacing())
                        .setDefaultValue(RegReaderConfig.getDefaultSpacing())
                        .setSaveConsumer(newSpacing -> {
                            if (!newSpacing.equals(register.getSpacing())) {
                                if(400 > newSpacing && newSpacing > 1) {
                                    register.setbits(newSpacing);
                                }
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startBooleanToggle(Component.nullToEmpty("Inverted"), register.getInverted())
                        .setDefaultValue(RegReaderConfig.getDefaultInverted())
                        .setSaveConsumer(newInverted -> {
                            if (!(newInverted.equals(register.getInverted()))) {
                                register.setinverted(newInverted);
                            }
                        })
                        .build());

                regsub.add(entryBuilder.startStringDropdownMenu(Component.nullToEmpty("Assigned HUD"), register.getAssignedHUD())
                        .setSelections(hudNames)
                        .setSaveConsumer(register::setAssignedHUD)
                        .build());

                regsub.add(entryBuilder.startBooleanToggle(Component.nullToEmpty("§cDelete this Register"), false)
                        .setDefaultValue(false)
                        .setSaveConsumer(clicked -> {
                            if (clicked) {
                                RegisterManager.removeRegister(register.getName());
                            }
                        })
                        .setYesNoTextSupplier(clicked -> Component.nullToEmpty("§cClick to Delete"))
                        .build());

                registers.addEntry(regsub.build());
            }

            for (RegReaderHUD hud : HUDManager.getHUDs()) {
                String originalName = hud.getHUDName();

                SubCategoryBuilder hudsub = entryBuilder.startSubCategory(Component.nullToEmpty(originalName));

                hudsub.add(entryBuilder.startStrField(Component.nullToEmpty("Name"), originalName)
                        .setSaveConsumer(newName -> {
                            if (!newName.equals(originalName)) {
                                hud.setHUDName(newName);
                            }
                        })
                        .build());

                hudsub.add(entryBuilder.startAlphaColorField(Component.nullToEmpty("Color (#AARRGGBB)"), (int) Long.parseLong(hud.getHUDColor().replaceFirst("^#", ""), 16))
                        .setDefaultValue(0xffffffff)
                        .setTooltip(Component.nullToEmpty("HUD text color in ARGB format"))
                        .setSaveConsumer(newColorInt -> {
                            String hexString = String.format("#%08X", newColorInt);
                            hud.setHUDColor(hexString);
                        })
                        .build());

                hudsub.add(entryBuilder.startIntField(Component.nullToEmpty("Display Base"), hud.getDisplayBase())
                        .setDefaultValue(2)
                        .setTooltip(Component.nullToEmpty("Base: 2, 8, 10 or 16"))
                        .setSaveConsumer(hud::setDisplayBase)
                        .build());

                hudsub.add(entryBuilder.startBooleanToggle(Component.nullToEmpty("Colored Names"), hud.getColoredNames())
                        .setDefaultValue(true)
                        .setSaveConsumer(hud::setColoredNames)
                        .build());

                hudsub.add(entryBuilder.startIntField(Component.nullToEmpty("Rectangle Width"), hud.getRectangleWidth())
                        .setDefaultValue(80)
                        .setTooltip(Component.nullToEmpty("Width of HUD"))
                        .setSaveConsumer(hud::setRectangleWidth)
                        .build());

                hudsub.add(entryBuilder.startIntField(Component.nullToEmpty("X Position"), hud.getxPos())
                        .setDefaultValue(12)
                        .setTooltip(Component.nullToEmpty("Horizontal screen position"))
                        .setSaveConsumer(hud::setxPos)
                        .build());

                hudsub.add(entryBuilder.startIntField(Component.nullToEmpty("Y Position"), hud.getyPos())
                        .setDefaultValue(10)
                        .setTooltip(Component.nullToEmpty("Vertical screen position"))
                        .setSaveConsumer(hud::setyPos)
                        .build());

                hudsub.add(entryBuilder.startBooleanToggle(Component.nullToEmpty("§cDelete this HUD"), false)
                        .setDefaultValue(false)
                        .setSaveConsumer(clicked -> {
                            if (clicked) {
                                HUDManager.removeHUD(originalName);
                            }
                        })
                        .setYesNoTextSupplier(state -> Component.nullToEmpty("§cClick to Delete"))
                        .build());

                huds.addEntry(hudsub.build());
            }


            return builder.build();
        };
    }
}
