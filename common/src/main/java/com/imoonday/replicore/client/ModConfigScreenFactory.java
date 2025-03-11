package com.imoonday.replicore.client;

import com.imoonday.replicore.EventHandler;
import com.imoonday.replicore.RepliCore;
import com.imoonday.replicore.config.CostConfig;
import com.imoonday.replicore.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import me.shedaniel.clothconfig2.impl.builders.SubCategoryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;
import java.util.Optional;

public class ModConfigScreenFactory {

    public static Screen create(Screen parent) {
        if (!RepliCore.clothConfig) {
            return parent;
        }

        try {
            ConfigBuilder builder = ConfigBuilder.create()
                                                 .setParentScreen(parent)
                                                 .setTitle(Component.translatable("config.replicore.title"))
                                                 .setSavingRunnable(ModConfigScreenFactory::save);
            ConfigEntryBuilder entryBuilder = ConfigEntryBuilder.create();

            ConfigCategory category = builder.getOrCreateCategory(Component.translatable("config.replicore.category.common"));

            ModConfig config = ModConfig.get();

            category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.replicore.blacklistEnabled"), config.blacklistEnabled)
                                          .setDefaultValue(false)
                                          .setSaveConsumer(newValue -> config.blacklistEnabled = newValue)
                                          .build());

            category.addEntry(entryBuilder.startStrList(Component.translatable("config.replicore.blacklist"), config.blacklist)
                                          .setDefaultValue(new ArrayList<>())
                                          .setSaveConsumer(newValue -> config.blacklist = newValue)
                                          .build());

            category.addEntry(entryBuilder.startIntField(Component.translatable("config.replicore.crystalDroppingCount"), config.crystalDroppingCount)
                                          .setDefaultValue(1)
                                          .setMin(0)
                                          .setSaveConsumer(newValue -> config.crystalDroppingCount = newValue)
                                          .build());

            category.addEntry(entryBuilder.startIntField(Component.translatable("config.replicore.firstCrystalDroppingCount"), config.firstCrystalDroppingCount)
                                          .setDefaultValue(3)
                                          .setMin(0)
                                          .setSaveConsumer(newValue -> config.firstCrystalDroppingCount = newValue)
                                          .build());

            category.addEntry(entryBuilder.startIntField(Component.translatable("config.replicore.maxDropDistance"), config.maxDropDistance)
                                          .setDefaultValue(256)
                                          .setMin(0)
                                          .setSaveConsumer(newValue -> config.maxDropDistance = newValue)
                                          .build());

            SubCategoryBuilder costConfigCategory = entryBuilder.startSubCategory(Component.translatable("config.replicore.costConfig.title"));

            CostConfig costConfig = config.costConfig;
            CostConfig defaultConfig = new CostConfig();

            costConfigCategory.add(entryBuilder.startDoubleField(Component.translatable("config.replicore.costConfig.defaultCost"), costConfig.getDefaultCost())
                                               .setDefaultValue(defaultConfig.getDefaultCost())
                                               .setSaveConsumer(costConfig::setDefaultCost)
                                               .build());

            costConfigCategory.add(entryBuilder.startBooleanToggle(Component.translatable("config.replicore.costConfig.calculateEnchantmentCosts"), costConfig.isCalculateEnchantmentCosts())
                                               .setDefaultValue(defaultConfig.isCalculateEnchantmentCosts())
                                               .setSaveConsumer(costConfig::setCalculateEnchantmentCosts)
                                               .build());

            costConfigCategory.add(entryBuilder.startDoubleField(Component.translatable("config.replicore.costConfig.defaultEnchantmentCost"), costConfig.getDefaultEnchantmentCost())
                                               .setDefaultValue(defaultConfig.getDefaultEnchantmentCost())
                                               .setSaveConsumer(costConfig::setDefaultEnchantmentCost)
                                               .build());

            costConfigCategory.add(entryBuilder.startBooleanToggle(Component.translatable("config.replicore.costConfig.ignoreCurses"), costConfig.isIgnoreCurses())
                                               .setDefaultValue(defaultConfig.isIgnoreCurses())
                                               .setSaveConsumer(costConfig::setIgnoreCurses)
                                               .build());

            costConfigCategory.add(entryBuilder.startTextDescription(Component.translatable("config.replicore.costConfig.costEntryDescription")).build());

            costConfigCategory.add(entryBuilder.startStrList(Component.translatable("config.replicore.costConfig.customCosts"), costConfig.getCustomCostList())
                                               .setDefaultValue(defaultConfig.getCustomCostList())
                                               .setSaveConsumer(costConfig::setCustomCostFromList)
                                               .setCellErrorSupplier(s -> CostConfig.isValidCostEntry(s) ? Optional.empty() : Optional.of(Component.translatable("config.replicore.costConfig.invalidCostEntry", s)))
                                               .build());

            costConfigCategory.add(entryBuilder.startStrList(Component.translatable("config.replicore.costConfig.customEnchantmentCosts"), costConfig.getCustomEnchantmentCostList())
                                               .setDefaultValue(defaultConfig.getCustomEnchantmentCostList())
                                               .setSaveConsumer(costConfig::setCustomEnchantmentCostFromList)
                                               .setCellErrorSupplier(s -> CostConfig.isValidCostEntry(s) ? Optional.empty() : Optional.of(Component.translatable("config.replicore.costConfig.invalidCostEntry", s)))
                                               .build());

            category.addEntry(costConfigCategory.build());

            return builder.build();
        } catch (Exception e) {
            return parent;
        }
    }

    private static void save() {
        ModConfig.get().save();

        IntegratedServer server = Minecraft.getInstance().getSingleplayerServer();
        if (server != null) {
            EventHandler.updateConfig(server);
        }
    }
}
