package com.imoonday.replicore.config;

import lombok.Data;
import lombok.experimental.Accessors;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.nbt.ListTag;
import net.minecraft.nbt.Tag;
import net.minecraft.world.item.ItemStack;

import java.util.Collections;
import java.util.List;
import java.util.stream.Collectors;

@Data
@Accessors(chain = true)
public class ContainerConfig {

    private boolean enabled = true; //是否计算容器内的物品费用
    private boolean recursive = true; //是否递归计算容器内的物品费用
    private boolean excludeOriginalCost = false; //是否排除原容器的费用
    private String tag = "BlockEntityTag.Items"; //检测容器物品的NBT标签路径，用“.”分割，最后一项需为列表类型数据

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
