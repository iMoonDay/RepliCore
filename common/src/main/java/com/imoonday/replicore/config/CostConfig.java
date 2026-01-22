package com.imoonday.replicore.config;

import lombok.Data;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.jetbrains.annotations.Nullable;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.function.BiConsumer;
import java.util.function.BiFunction;
import java.util.function.Predicate;
import java.util.stream.Collectors;

@Data
public class CostConfig {

    private double defaultCost = 0.25; //默认费用
    private Map<String, Double> customCosts = new HashMap<>(); //自定义费用
    private boolean calculateEnchantmentCosts = true; //计算附魔费用
    private double defaultEnchantmentCost = 2.0; //默认附魔费用
    private boolean ignoreCurses = true; //忽略诅咒附魔
    private Map<String, Double> customEnchantmentCosts = new HashMap<>(); //自定义附魔费用
    private ContainerConfig defaultContainerConfig = new ContainerConfig(); //默认容器配置
    private Map<String, ContainerConfig> customContainerConfigs = new HashMap<>() {{ //自定义容器配置
        put("minecraft:bundle", new ContainerConfig().setTag("Items"));
    }};

    public List<String> getCustomCostList() {
        return mapMapToList(customCosts);
    }

    public void setCustomCostFromList(List<String> customCostList) {
        this.customCosts = mapListToMap(customCostList);
    }

    public List<String> getCustomEnchantmentCostList() {
        return mapMapToList(customEnchantmentCosts);
    }

    public void setCustomEnchantmentCostFromList(List<String> customEnchantmentCostList) {
        this.customEnchantmentCosts = mapListToMap(customEnchantmentCostList);
    }

    public int calculateTotalCost(ItemStack stack, Predicate<ItemStack> except) {
        if (stack.isEmpty() || except.test(stack)) return -1;

        double cost = calculateBaseCost(stack);

        ResourceLocation key = getItemKey(stack.getItem());
        ContainerConfig config = Objects.requireNonNullElseGet(getMatchedValue(customContainerConfigs, key), () -> defaultContainerConfig);
        if (config.isEnabled() && stack.hasTag()) {
            List<ItemStack> contents = config.parseContainedItems(stack);

            if (config.isExcludeOriginalCost() && !contents.isEmpty()) {
                cost = 0;
            }

            for (ItemStack content : contents) {
                if (except.test(content)) return -1;

                double contentCost = config.isRecursive() ? calculateTotalCost(content, except) : calculateBaseCost(content);
                if (contentCost > 0) {
                    cost += contentCost;
                }
            }
        }

        return (int) Math.ceil(Math.max(0, cost));
    }

    public double calculateBaseCost(ItemStack stack) {
        return calculateItemCost(stack) + (calculateEnchantmentCosts ? calculateEnchantmentCost(stack) : 0);
    }

    public double calculateItemCost(ItemStack stack) {
        ResourceLocation key = getItemKey(stack.getItem());
        Double value = getMatchedValue(customCosts, key);
        double costPerCount = Objects.requireNonNullElseGet(value, () -> defaultCost);
        return stack.getCount() * costPerCount;
    }

    public double calculateEnchantmentCost(ItemStack stack) {
        double cost = 0;
        Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
        for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
            Enchantment enchantment = entry.getKey();
            if (ignoreCurses && enchantment.isCurse()) continue;

            ResourceLocation key = BuiltInRegistries.ENCHANTMENT.getKey(enchantment);
            double costPerLvl = defaultEnchantmentCost;
            if (key != null) {
                Double value = getMatchedValue(customEnchantmentCosts, key);
                if (value != null) {
                    costPerLvl = value;
                }
            }
            cost += entry.getValue() * costPerLvl;
        }
        return cost;
    }

    public CompoundTag writeNbt(CompoundTag tag) {
        tag.putDouble("defaultCost", defaultCost);
        tag.put("customCosts", mapToNbt(customCosts, tag::putDouble));
        tag.putBoolean("calculateEnchantmentCosts", calculateEnchantmentCosts);
        tag.putDouble("defaultEnchantmentCost", defaultEnchantmentCost);
        tag.putBoolean("ignoreCurses", ignoreCurses);
        tag.put("customEnchantmentCosts", mapToNbt(customEnchantmentCosts, tag::putDouble));
        tag.put("defaultContainerConfig", defaultContainerConfig.writeNbt());
        tag.put("customContainerConfigs", mapToNbt(customContainerConfigs, (key, value) -> tag.put(key, value.writeNbt())));
        return tag;
    }

    public CostConfig readNbt(CompoundTag tag) {
        if (tag == null) return this;
        if (tag.contains("defaultCost")) defaultCost = tag.getDouble("defaultCost");
        if (tag.contains("customCosts")) customCosts = parseMap(tag.getCompound("customCosts"), CompoundTag::getDouble);
        if (tag.contains("calculateEnchantmentCosts")) calculateEnchantmentCosts = tag.getBoolean("calculateEnchantmentCosts");
        if (tag.contains("defaultEnchantmentCost")) defaultEnchantmentCost = tag.getDouble("defaultEnchantmentCost");
        if (tag.contains("ignoreCurses")) ignoreCurses = tag.getBoolean("ignoreCurses");
        if (tag.contains("customEnchantmentCosts")) customEnchantmentCosts = parseMap(tag.getCompound("customEnchantmentCosts"), CompoundTag::getDouble);
        if (tag.contains("defaultContainerConfig")) defaultContainerConfig = ContainerConfig.fromNbt(tag.getCompound("defaultContainerConfig"));
        if (tag.contains("customContainerConfigs")) {
            customContainerConfigs = parseMap(tag.getCompound("customContainerConfigs"), (tag1, key) -> ContainerConfig.fromNbt(tag1.getCompound(key)));
        }
        return this;
    }

    public static CostConfig fromNbt(CompoundTag tag) {
        return new CostConfig().readNbt(tag);
    }

    private static <T> CompoundTag mapToNbt(Map<String, T> map, BiConsumer<String, T> consumer) {
        CompoundTag tag = new CompoundTag();
        map.forEach(consumer);
        return tag;
    }

    private static <T> Map<String, T> parseMap(CompoundTag tag, BiFunction<CompoundTag, String, T> merger) {
        return tag.getAllKeys().stream().collect(Collectors.toMap(key -> key, key -> merger.apply(tag, key), (a, b) -> b));
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

    public static ResourceLocation getItemKey(Item item) {
        return BuiltInRegistries.ITEM.getKey(item);
    }

    @Nullable
    private static <T> T getMatchedValue(Map<String, T> map, ResourceLocation key) {
        String id = key.toString();
        if (map.containsKey(id)) {
            return map.get(id);
        } else if (key.getNamespace().equals(ResourceLocation.DEFAULT_NAMESPACE)) {
            String path = key.getPath();
            if (map.containsKey(path)) {
                return map.get(path);
            }
        }
        return null;
    }
}
