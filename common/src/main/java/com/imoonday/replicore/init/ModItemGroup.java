package com.imoonday.replicore.init;

import com.imoonday.replicore.PlatformHelper;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.ItemStack;

import java.util.function.Supplier;

public class ModItemGroup {

    public static final Supplier<CreativeModeTab> TAB = PlatformHelper.registerTab("replicore", () -> CreativeModeTab.builder(CreativeModeTab.Row.TOP, 0)
                                                                                                                     .title(Component.translatable("itemGroup.replicore"))
                                                                                                                     .icon(() -> new ItemStack(ModItems.VOID_CORE.get()))
                                                                                                                     .displayItems((parameters, output) -> {
                                                                                                                         output.accept(ModItems.REPLICATION_TABLE.get());
                                                                                                                         output.accept(ModItems.DRAGON_SOUL_CRYSTAL.get());
                                                                                                                         output.accept(ModItems.DAMAGED_CORE.get());
                                                                                                                         output.accept(ModItems.STANDARD_CORE.get());
                                                                                                                         output.accept(ModItems.REFINED_CORE.get());
                                                                                                                         output.accept(ModItems.VOID_CORE.get());
                                                                                                                     })
                                                                                                                     .build());

    public static void init() {

    }
}
