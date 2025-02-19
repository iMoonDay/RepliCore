package com.imoonday.replicore.init;

import com.imoonday.replicore.PlatformHelper;
import com.imoonday.replicore.block.ReplicationTableBlock;
import net.minecraft.world.level.block.Blocks;
import net.minecraft.world.level.block.state.BlockBehaviour;
import net.minecraft.world.level.block.state.properties.NoteBlockInstrument;
import net.minecraft.world.level.material.MapColor;

import java.util.function.Supplier;

public class ModBlocks {

    //复制台
    public static final Supplier<ReplicationTableBlock> REPLICATION_TABLE = PlatformHelper.registerBlock("replication_table", () -> new ReplicationTableBlock(BlockBehaviour.Properties.of().mapColor(MapColor.STONE).instrument(NoteBlockInstrument.BASEDRUM).requiresCorrectToolForDrops().strength(3.5F)));

    public static void init() {
    }
}
