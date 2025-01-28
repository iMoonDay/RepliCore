package com.imoonday.replicore.init;

import com.imoonday.replicore.PlatformHelper;
import com.imoonday.replicore.core.CoreTier;
import com.imoonday.replicore.item.CoreItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class ModItems {

    //龙魂结晶
    public static final Supplier<Item> DRAGON_SOUL_CRYSTAL = PlatformHelper.registerItem("dragon_soul_crystal", () -> new Item(new Item.Properties().rarity(Rarity.EPIC)));

    //复制台
    public static final Supplier<BlockItem> REPLICATION_TABLE = PlatformHelper.registerItem("replication_table", () -> new BlockItem(ModBlocks.REPLICATION_TABLE.get(), new Item.Properties()));

    //复制核心
    public static final Supplier<CoreItem> DAMAGED_CORE = PlatformHelper.registerItem("damaged_core", () -> new CoreItem(CoreTier.DAMAGED, new Item.Properties().stacksTo(1)));
    public static final Supplier<CoreItem> STANDARD_CORE = PlatformHelper.registerItem("standard_core", () -> new CoreItem(CoreTier.STANDARD, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final Supplier<CoreItem> REFINED_CORE = PlatformHelper.registerItem("refined_core", () -> new CoreItem(CoreTier.REFINED, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Supplier<CoreItem> VOID_CORE = PlatformHelper.registerItem("void_core", () -> new CoreItem(CoreTier.VOID, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static void init() {

    }
}
