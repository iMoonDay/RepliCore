package com.imoonday.replicore.config;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.imoonday.replicore.PlatformHelper;
import com.imoonday.replicore.item.CoreItem;
import com.mojang.logging.LogUtils;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.StringTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.Block;
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

    public boolean whitelistEnabled = false; //启用白名单
    public List<String> whitelist = new ArrayList<>(); //复制白名单
    public boolean blacklistEnabled = false; //启用黑名单
    public List<String> blacklist = new ArrayList<>(); //复制黑名单
    public boolean forbidReplicatingCores = true; //禁止复制核心
    public int crystalDroppingCount = 1; //龙魂结晶掉落数量
    public int firstCrystalDroppingCount = 3; //龙魂结晶首次掉落数量
    public int maxDropDistance = 256; //龙魂结晶掉落范围
    public CostConfig costConfig = new CostConfig(); //费用配置

    public boolean isBlacklisted(ItemStack stack) {
        if (forbidReplicatingCores && stack.getItem() instanceof CoreItem) return true;

        if (whitelistEnabled && matches(stack, whitelist)) {
            return false;
        }

        if (blacklistEnabled && matches(stack, blacklist)) {
            return true;
        }

        return whitelistEnabled;
    }

    private boolean matches(ItemStack stack, List<String> list) {
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(stack.getItem());
        String id = key.toString();
        String path = key.getPath();

        String namespace = key.getNamespace();

        for (String entry : list) {
            if (entry.startsWith("#")) {
                String tagId = entry.substring(1);
                ResourceLocation tagLocation = ResourceLocation.tryParse(tagId);
                if (tagLocation != null) {
                    TagKey<Item> itemKey = TagKey.create(BuiltInRegistries.ITEM.key(), tagLocation);
                    if (stack.is(itemKey)) {
                        return true;
                    }

                    if (stack.getItem() instanceof BlockItem blockItem) {
                        TagKey<Block> blockKey = TagKey.create(BuiltInRegistries.BLOCK.key(), tagLocation);
                        if (blockItem.getBlock().builtInRegistryHolder().is(blockKey)) {
                            return true;
                        }
                    }
                }
            } else if (entry.startsWith("@")) {
                String modId = entry.substring(1);
                if (namespace.equals(modId)) {
                    return true;
                }
            } else if (entry.startsWith("$")) {
                String regex = entry.substring(1);
                try {
                    if (id.matches(regex)) {
                        return true;
                    }
                } catch (Exception e) {
                    LOGGER.warn("Invalid regex in config: {}", regex);
                }
            } else {
                if (entry.equals(id) || namespace.equals(ResourceLocation.DEFAULT_NAMESPACE) && entry.equals(path)) {
                    return true;
                }
            }
        }
        return false;
    }

    public CompoundTag save(CompoundTag tag) {
        saveForClient(tag);
        tag.putInt("crystalDroppingCount", crystalDroppingCount);
        tag.putInt("firstCrystalDroppingCount", firstCrystalDroppingCount);
        tag.putInt("maxDropDistance", maxDropDistance);
        tag.put("costConfig", costConfig.writeNbt(new CompoundTag()));
        return tag;
    }

    public CompoundTag saveForClient(CompoundTag tag) {
        tag.putBoolean("whitelistEnabled", whitelistEnabled);
        ListTag whitelistTag = new ListTag();
        for (String item : whitelist) {
            whitelistTag.add(StringTag.valueOf(item));
        }
        tag.put("whitelist", whitelistTag);
        tag.putBoolean("blacklistEnabled", blacklistEnabled);
        ListTag blacklistTag = new ListTag();
        for (String item : blacklist) {
            blacklistTag.add(StringTag.valueOf(item));
        }
        tag.put("blacklist", blacklistTag);
        tag.putBoolean("forbidReplicatingCores", forbidReplicatingCores);
        return tag;
    }

    public void load(CompoundTag tag) {
        if (tag == null) return;
        if (tag.contains("whitelistEnabled")) {
            whitelistEnabled = tag.getBoolean("whitelistEnabled");
        }
        if (tag.contains("whitelist")) {
            whitelist.clear();
            ListTag whitelistTag = tag.getList("whitelist", 8);
            for (int i = 0; i < whitelistTag.size(); i++) {
                whitelist.add(whitelistTag.getString(i));
            }
        }
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
        if (tag.contains("forbidReplicatingCores")) {
            forbidReplicatingCores = tag.getBoolean("forbidReplicatingCores");
        }
        if (tag.contains("crystalDroppingCount")) {
            crystalDroppingCount = tag.getInt("crystalDroppingCount");
        }
        if (tag.contains("firstCrystalDroppingCount")) {
            firstCrystalDroppingCount = tag.getInt("firstCrystalDroppingCount");
        }
        if (tag.contains("maxDropDistance")) {
            maxDropDistance = tag.getInt("maxDropDistance");
        }
        if (tag.contains("costConfig")) {
            CompoundTag configTag = tag.getCompound("costConfig");
            costConfig = costConfig != null ? costConfig.readNbt(configTag) : CostConfig.fromNbt(configTag);
        }
    }

    public void reset() {
        whitelistEnabled = false;
        whitelist.clear();
        blacklistEnabled = false;
        blacklist.clear();
        forbidReplicatingCores = true;
        crystalDroppingCount = 1;
        firstCrystalDroppingCount = 3;
        maxDropDistance = 256;
        costConfig = new CostConfig();
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
