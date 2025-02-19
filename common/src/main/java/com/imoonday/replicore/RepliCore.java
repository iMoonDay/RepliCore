package com.imoonday.replicore;

import com.imoonday.replicore.init.ModBlocks;
import com.imoonday.replicore.init.ModItemGroup;
import com.imoonday.replicore.init.ModItems;
import com.imoonday.replicore.init.ModMenuType;
import com.mojang.logging.LogUtils;
import net.minecraft.resources.ResourceLocation;
import org.slf4j.Logger;

public final class RepliCore {

    public static final String MOD_ID = "replicore";
    public static Logger LOGGER = LogUtils.getLogger();
    public static boolean clothConfig = PlatformHelper.isModLoaded("cloth-config") || PlatformHelper.isModLoaded("cloth_config");

    public static void init() {
        ModBlocks.init();
        ModItems.init();
        ModItemGroup.init();
        ModMenuType.init();
    }

    public static ResourceLocation id(String path) {
        return new ResourceLocation(MOD_ID, path);
    }
}
