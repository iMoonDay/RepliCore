package com.imoonday.replicore.client;

import com.imoonday.replicore.EventHandler;
import com.imoonday.replicore.RepliCore;
import com.imoonday.replicore.config.ModConfig;
import me.shedaniel.clothconfig2.api.ConfigBuilder;
import me.shedaniel.clothconfig2.api.ConfigCategory;
import me.shedaniel.clothconfig2.api.ConfigEntryBuilder;
import net.minecraft.client.Minecraft;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.client.server.IntegratedServer;
import net.minecraft.network.chat.Component;

import java.util.ArrayList;

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

            category.addEntry(entryBuilder.startBooleanToggle(Component.translatable("config.replicore.extraCostForEnchantments"), config.extraCostForEnchantments)
                                          .setDefaultValue(true)
                                          .setSaveConsumer(newValue -> config.extraCostForEnchantments = newValue)
                                          .build());

            category.addEntry(entryBuilder.startIntField(Component.translatable("config.replicore.maxDropDistance"), config.maxDropDistance)
                .setDefaultValue(256)
                .setMin(0)
                .setSaveConsumer(newValue -> config.maxDropDistance = newValue)
                .build());

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
