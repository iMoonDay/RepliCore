package com.imoonday.replicore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imoonday.replicore.PlatformHelper;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.world.item.ItemStack;
import org.slf4j.Logger;

import java.io.File;
import java.io.FileWriter;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class ModConfig {

    private static final Logger LOGGER = LogUtils.getLogger();
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().create();
    private static ModConfig instance;
    private static ModConfig clientCache;
    private static File configFile;

    public boolean blacklistEnabled = false;
    public List<String> blacklist = new ArrayList<>();
    public int crystalDroppingCount = 1;
    public int firstCrystalDroppingCount = 3;
    public boolean extraCostForEnchantments = true;
    public int maxDropDistance = 256;

    public boolean isBlacklisted(ItemStack stack) {
        if (!blacklistEnabled) return false;
        String itemId = BuiltInRegistries.ITEM.getKey(stack.getItem()).toString();
        return blacklist.contains(itemId);
    }

    public CompoundTag save(CompoundTag tag) {
        tag.putBoolean("blacklistEnabled", blacklistEnabled);
        ListTag blacklistTag = new ListTag();
        for (String item : blacklist) {
            blacklistTag.add(StringTag.valueOf(item));
        }
        tag.put("blacklist", blacklistTag);
        tag.putInt("crystalDroppingCount", crystalDroppingCount);
        tag.putInt("firstCrystalDroppingCount", firstCrystalDroppingCount);
        tag.putBoolean("extraCostForEnchantments", extraCostForEnchantments);
        tag.putInt("maxDropDistance", maxDropDistance);
        return tag;
    }

    public void load(CompoundTag tag) {
        if (tag == null) return;
        if (tag.contains("blacklistEnabled")) {
            blacklistEnabled = tag.getBoolean("blacklistEnabled");
        }
        if (tag.contains("blacklist")) {
            blacklist.clear();
            ListTag blacklistTag = tag.getList("blacklist", 8);
            for (int i = 0; i < blacklistTag.size(); i++) {
                blacklist.add(blacklistTag.getString(i));
            }
        }
        if (tag.contains("crystalDroppingCount")) {
            crystalDroppingCount = tag.getInt("crystalDroppingCount");
        }
        if (tag.contains("firstCrystalDroppingCount")) {
            firstCrystalDroppingCount = tag.getInt("firstCrystalDroppingCount");
        }
        if (tag.contains("extraCostForEnchantments")) {
            extraCostForEnchantments = tag.getBoolean("extraCostForEnchantments");
        }
        if (tag.contains("maxDropDistance")) {
            maxDropDistance = tag.getInt("maxDropDistance");
        }
    }

    public void reset() {
        blacklistEnabled = false;
        blacklist.clear();
        crystalDroppingCount = 1;
        firstCrystalDroppingCount = 3;
        extraCostForEnchantments = true;
        maxDropDistance = 256;
    }

    public static ModConfig get(boolean isClient) {
        return isClient ? getClientCache() : get();
    }

    public static ModConfig get() {
        if (instance == null) {
            instance = new ModConfig();
        }
        return instance;
    }

    public static ModConfig getClientCache() {
        if (clientCache == null) {
            clientCache = new ModConfig();
        }
        return clientCache;
    }

    public static void load() {
        LOGGER.info("Loading config");
        try {
            File file = getConfigDir();
            if (file.exists()) {
                String json = Files.readString(Paths.get(file.toURI()));
                ModConfig config = fromJson(json);
                if (config != null) {
                    instance = config;
                    LOGGER.info("Config loaded");
                } else {
                    LOGGER.warn("Failed to parse config file, saving current config");
                    get().save();
                }
            } else {
                LOGGER.warn("Config file does not exist, creating new one");
                get().save();
            }
        } catch (Exception e) {
            LOGGER.error("Failed to read from config file", e);
        }
    }

    private static File getConfigDir() {
        if (configFile == null) {
            configFile = PlatformHelper.getConfigDir().resolve("replicore.json").toFile();
        }
        return configFile;
    }

    public void save() {
        File file = getConfigDir();
        try (FileWriter writer = new FileWriter(file)) {
            writer.write(toJson());
        } catch (Exception e) {
            LOGGER.error("Failed to write to config file", e);
        }
    }

    public String toJson() {
        return GSON.toJson(this);
    }

    public static ModConfig fromJson(String json) {
        return GSON.fromJson(json, ModConfig.class);
    }
}
