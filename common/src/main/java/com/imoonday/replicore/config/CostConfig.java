package com.imoonday.replicore.config;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.stream.Collectors;

public class CostConfig {

    private double defaultCost = 0.25;
    private Map<String, Double> customCosts = new HashMap<>();
    private boolean calculateEnchantmentCosts = true;
    private double defaultEnchantmentCost = 2.0;
    private boolean ignoreCurses = true;
    private Map<String, Double> customEnchantmentCosts = new HashMap<>();

    public CostConfig() {

    }

    public CostConfig(double defaultCost, Map<String, Double> customCosts, boolean calculateEnchantmentCosts, double defaultEnchantmentCost, boolean ignoreCurses, Map<String, Double> customEnchantmentCosts) {
        this.defaultCost = defaultCost;
        this.customCosts = customCosts;
        this.calculateEnchantmentCosts = calculateEnchantmentCosts;
        this.defaultEnchantmentCost = defaultEnchantmentCost;
        this.ignoreCurses = ignoreCurses;
        this.customEnchantmentCosts = customEnchantmentCosts;
    }

    public double getDefaultCost() {
        return defaultCost;
    }

    public void setDefaultCost(double defaultCost) {
        this.defaultCost = defaultCost;
    }

    public Map<String, Double> getCustomCosts() {
        return customCosts;
    }

    public List<String> getCustomCostList() {
        return mapMapToList(customCosts);
    }

    public void setCustomCosts(Map<String, Double> customCosts) {
        this.customCosts = customCosts;
    }

    public void setCustomCostFromList(List<String> customCostList) {
        this.customCosts = mapListToMap(customCostList);
    }

    public boolean isCalculateEnchantmentCosts() {
        return calculateEnchantmentCosts;
    }

    public void setCalculateEnchantmentCosts(boolean calculateEnchantmentCosts) {
        this.calculateEnchantmentCosts = calculateEnchantmentCosts;
    }

    public double getDefaultEnchantmentCost() {
        return defaultEnchantmentCost;
    }

    public void setDefaultEnchantmentCost(double defaultEnchantmentCost) {
        this.defaultEnchantmentCost = defaultEnchantmentCost;
    }

    public boolean isIgnoreCurses() {
        return ignoreCurses;
    }

    public void setIgnoreCurses(boolean ignoreCurses) {
        this.ignoreCurses = ignoreCurses;
    }

    public Map<String, Double> getCustomEnchantmentCosts() {
        return customEnchantmentCosts;
    }

    public List<String> getCustomEnchantmentCostList() {
        return mapMapToList(customEnchantmentCosts);
    }

    public void setCustomEnchantmentCosts(Map<String, Double> customEnchantmentCosts) {
        this.customEnchantmentCosts = customEnchantmentCosts;
    }

    public void setCustomEnchantmentCostFromList(List<String> customEnchantmentCostList) {
        this.customEnchantmentCosts = mapListToMap(customEnchantmentCostList);
    }

    public int calculateCost(ItemStack stack) {
        if (stack.isEmpty() || stack.getCount() <= 0) {
            return -1;
        }
        double cost = calculateItemCost(stack);
        if (calculateEnchantmentCosts) {
            cost += calculateEnchantmentCost(stack);
        }
        if (cost < 0) {
            cost = 0;
        }
        return (int) Math.ceil(cost);
    }

    public double calculateItemCost(ItemStack stack) {
        int count = stack.getCount();
        Item item = stack.getItem();
        ResourceLocation key = BuiltInRegistries.ITEM.getKey(item);
        double costPerCount = defaultCost;
        String id = key.toString();
        if (customCosts.containsKey(id)) {
            costPerCount = customCosts.get(id);
        } else if (key.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
            String path = key.getPath();
            if (customCosts.containsKey(path)) {
                costPerCount = customCosts.get(path);
            }
        }
        return count * costPerCount;
    }

    public double calculateEnchantmentCost(ItemStack stack) {
        double cost = 0;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (ignoreCurses && enchantment.isCurse()) continue;

            int level = entry.getValue();
            ResourceLocation key = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
            double costPerLvl = defaultEnchantmentCost;
            if (key != null) {
                String id = key.toString();
                if (customEnchantmentCosts.containsKey(id)) {
                    costPerLvl = customEnchantmentCosts.get(id);
                } else if (key.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
                    String path = key.getPath();
                    if (customEnchantmentCosts.containsKey(path)) {
                        costPerLvl = customEnchantmentCosts.get(path);
                    }
                }
            }
            cost += level * costPerLvl;
        }
        return cost;
    }

    public CompoundTag writeNbt(CompoundTag tag) {
        tag.putDouble("defaultCost", defaultCost);
        tag.put("customCosts", mapToNbt(customCosts));
        tag.putBoolean("calculateEnchantmentCosts", calculateEnchantmentCosts);
        tag.putDouble("defaultEnchantmentCost", defaultEnchantmentCost);
        tag.putBoolean("ignoreCurses", ignoreCurses);
        tag.put("customEnchantmentCosts", mapToNbt(customEnchantmentCosts));
        return tag;
    }

    public CostConfig readNbt(CompoundTag tag) {
        if (tag == null) {
            return this;
        }
        if (tag.contains("defaultCost")) {
            defaultCost = tag.getDouble("defaultCost");
        }
        if (tag.contains("customCosts")) {
            customCosts = parseMap(tag.getCompound("customCosts"));
        }
        if (tag.contains("calculateEnchantmentCosts")) {
            calculateEnchantmentCosts = tag.getBoolean("calculateEnchantmentCosts");
        }
        if (tag.contains("defaultEnchantmentCost")) {
            defaultEnchantmentCost = tag.getDouble("defaultEnchantmentCost");
        }
        if (tag.contains("ignoreCurses")) {
            ignoreCurses = tag.getBoolean("ignoreCurses");
        }
        if (tag.contains("customEnchantmentCosts")) {
            customEnchantmentCosts = parseMap(tag.getCompound("customEnchantmentCosts"));
        }
        return this;
    }

    public static CostConfig fromNbt(CompoundTag tag) {
        return new CostConfig().readNbt(tag);
    }

    private static CompoundTag mapToNbt(Map<String, Double> map) {
        CompoundTag tag = new CompoundTag();
        map.forEach(tag::putDouble);
        return tag;
    }

    private static Map<String, Double> parseMap(CompoundTag tag) {
        Map<String, Double> map = new HashMap<>();
        for (String key : tag.getAllKeys()) {
            map.put(key, tag.getDouble(key));
        }
        return map;
    }

    private static List<String> mapMapToList(Map<String, Double> map) {
        return map.entrySet().stream()
                  .map(entry -> entry.getKey() + " " + entry.getValue())
                  .collect(Collectors.toList());
    }

    private static Map<String, Double> mapListToMap(List<String> list) {
        return list.stream()
                   .map(s -> s.split("\\s+", 2))
                   .filter(arr -> arr.length == 2)
                   .map(arr -> {
                       try {
                           return Map.entry(arr[0], Double.parseDouble(arr[1]));
                       } catch (NumberFormatException e) {
                           return null;
                       }
                   })
                   .filter(Objects::nonNull)
                   .collect(Collectors.toMap(Map.Entry::getKey, Map.Entry::getValue, (a, b) -> b, HashMap::new));
    }

    public static boolean isValidCostEntry(String entry) {
        String[] strings = entry.split("\\s+", 2);
        return strings.length == 2 && isResourceLocation(strings[0]) && isDouble(strings[1]);
    }

    private static boolean isResourceLocation(String s) {
        return !s.isEmpty() && ResourceLocation.isValidResourceLocation(s);
    }

    private static boolean isDouble(String s) {
        try {
            Double.parseDouble(s);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    @Override
    public boolean equals(Object obj) {
        if (obj == this) return true;
        if (obj == null || obj.getClass() != this.getClass()) return false;
        var that = (CostConfig) obj;
        return Double.doubleToLongBits(this.defaultCost) == Double.doubleToLongBits(that.defaultCost) &&
               Objects.equals(this.customCosts, that.customCosts) &&
               this.calculateEnchantmentCosts == that.calculateEnchantmentCosts &&
               Double.doubleToLongBits(this.defaultEnchantmentCost) == Double.doubleToLongBits(that.defaultEnchantmentCost) &&
               this.ignoreCurses == that.ignoreCurses &&
               Objects.equals(this.customEnchantmentCosts, that.customEnchantmentCosts);
    }

    @Override
    public int hashCode() {
        return Objects.hash(defaultCost, customCosts, calculateEnchantmentCosts, defaultEnchantmentCost, ignoreCurses, customEnchantmentCosts);
    }

    @Override
    public String toString() {
        return "CostConfig[" +
               "defaultCost=" + defaultCost + ", " +
               "customCosts=" + customCosts + ", " +
               "calculateEnchantmentCosts=" + calculateEnchantmentCosts + ", " +
               "defaultEnchantmentCost=" + defaultEnchantmentCost + ", " +
               "ignoreCurses=" + ignoreCurses + ", " +
               "customEnchantmentCosts=" + customEnchantmentCosts + ']';
    }
}
