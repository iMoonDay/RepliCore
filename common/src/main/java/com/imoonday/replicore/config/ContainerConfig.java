package com.imoonday.replicore.config;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

public class ContainerConfig {

    private boolean enabled = true;
    private boolean recursive = true;
    private boolean excludeOriginalCost = false;
    private String tag = "BlockEntityTag.Items";

    public boolean isEnabled() {
        return enabled;
    }

    public void setEnabled(boolean enabled) {
        this.enabled = enabled;
    }

    public boolean isRecursive() {
        return recursive;
    }

    public void setRecursive(boolean recursive) {
        this.recursive = recursive;
    }

    public boolean isExcludeOriginalCost() {
        return excludeOriginalCost;
    }

    public void setExcludeOriginalCost(boolean excludeOriginalCost) {
        this.excludeOriginalCost = excludeOriginalCost;
    }

    public String getTag() {
        return tag;
    }

    public void setTag(String tag) {
        this.tag = tag;
    }

    public CompoundTag writeNbt() {
        CompoundTag tag = new CompoundTag();
        tag.putBoolean("enabled", enabled);
        tag.putBoolean("recursive", recursive);
        tag.putBoolean("excludeOriginalCost", excludeOriginalCost);
        tag.putString("tag", this.tag);
        return tag;
    }

    public ContainerConfig readNbt(CompoundTag tag) {
        if (tag.contains("enabled")) enabled = tag.getBoolean("enabled");
        if (tag.contains("recursive")) recursive = tag.getBoolean("recursive");
        if (tag.contains("excludeOriginalCost")) excludeOriginalCost = tag.getBoolean("excludeOriginalCost");
        if (tag.contains("tag")) this.tag = tag.getString("tag");
        return this;
    }

    public static ContainerConfig fromNbt(CompoundTag tag) {
        return new ContainerConfig().readNbt(tag);
    }

    public List<ItemStack> parseContainedItems(ItemStack container) {
        if (container.isEmpty()) return Collections.emptyList();

        String[] path = tag.split("\\.");
        CompoundTag currentTag = container.getTag();
        if (currentTag == null) return Collections.emptyList();

        for (int i = 0; i < path.length - 1; i++) {
            if (!currentTag.contains(path[i], Tag.TAG_COMPOUND)) return Collections.emptyList();
            currentTag = currentTag.getCompound(path[i]);
        }

        String listKey = path[path.length - 1];
        if (currentTag.contains(listKey, Tag.TAG_LIST)) {
            ListTag itemsTag = currentTag.getList(listKey, Tag.TAG_COMPOUND);
            return itemsTag.stream()
                           .map(CompoundTag.class::cast)
                           .map(ItemStack::of)
                           .filter(stack -> !stack.isEmpty())
                           .collect(Collectors.toList());
        }
        return Collections.emptyList();
    }
}
