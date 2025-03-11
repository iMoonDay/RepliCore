package com.imoonday.replicore.init;

import com.imoonday.replicore.PlatformHelper;
import com.imoonday.replicore.core.CoreTiers;
import com.imoonday.replicore.item.CoreItem;
import com.imoonday.replicore.item.DragonSoulCrystalItem;
import net.minecraft.world.item.BlockItem;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.Rarity;

import java.util.function.Supplier;

public class ModItems {

    //龙魂结晶
    public static final Supplier<DragonSoulCrystalItem> DRAGON_SOUL_CRYSTAL = PlatformHelper.registerItem("dragon_soul_crystal", () -> new DragonSoulCrystalItem(new Item.Properties().rarity(Rarity.EPIC)));

    //复制台
    public static final Supplier<BlockItem> REPLICATION_TABLE = PlatformHelper.registerItem("replication_table", () -> new BlockItem(ModBlocks.REPLICATION_TABLE.get(), new Item.Properties()));

    //复制核心
    public static final Supplier<CoreItem> DAMAGED_CORE = PlatformHelper.registerItem("damaged_core", () -> new CoreItem(CoreTiers.DAMAGED, 4, new Item.Properties().stacksTo(1)));
    public static final Supplier<CoreItem> STANDARD_CORE = PlatformHelper.registerItem("standard_core", () -> new CoreItem(CoreTiers.STANDARD, 4, new Item.Properties().stacksTo(1).rarity(Rarity.UNCOMMON)));
    public static final Supplier<CoreItem> REFINED_CORE = PlatformHelper.registerItem("refined_core", () -> new CoreItem(CoreTiers.REFINED, 3, new Item.Properties().stacksTo(1).rarity(Rarity.RARE)));
    public static final Supplier<CoreItem> VOID_CORE = PlatformHelper.registerItem("void_core", () -> new CoreItem(CoreTiers.VOID, 1, new Item.Properties().stacksTo(1).rarity(Rarity.EPIC)));

    public static void init() {

    }
}
