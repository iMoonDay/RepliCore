package com.imoonday.replicore.init;

import com.imoonday.replicore.PlatformHelper;
import com.imoonday.replicore.block.ReplicationTableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;

import java.util.function.Supplier;

public class ModBlocks {

    //复制台
    public static final Supplier<ReplicationTableBlock> REPLICATION_TABLE = PlatformHelper.registerBlock("replication_table", () -> new ReplicationTableBlock(BlockBehaviour.Properties.copy(Blocks.CRAFTING_TABLE)));

    public static void init() {
    }
}
