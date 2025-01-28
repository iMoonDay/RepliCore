package com.imoonday.replicore.forge;

import com.imoonday.replicore.forge.network.ForgeNetworkHandler;
import com.imoonday.replicore.init.ModMenuType;
import com.imoonday.replicore.network.NetworkPacket;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;
import net.minecraftforge.fml.ModList;
import net.minecraftforge.fml.loading.FMLPaths;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class PlatformHelperImpl {

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> supplier) {
        return RepliCoreForge.ITEMS.register(name, supplier);
    }

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> supplier) {
        return RepliCoreForge.BLOCKS.register(name, supplier);
    }

    public static Supplier<CreativeModeTab> registerTab(String name, Supplier<CreativeModeTab> supplier) {
        return RepliCoreForge.CREATIVE_MODE_TABS.register(name, supplier);
    }

    public static Path getConfigDir() {
        return FMLPaths.CONFIGDIR.get();
    }

    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String id, ModMenuType.MenuSupplier<T> supplier) {
        return RepliCoreForge.MENU_TYPES.register(id, () -> new MenuType<>(supplier::create, FeatureFlags.VANILLA_SET));
    }

    public static boolean isModLoaded(String modId) {
        return ModList.get().isLoaded(modId);
    }

    public static <P extends NetworkPacket> void sendToServer(P packet) {
        ForgeNetworkHandler.sendToServer(packet);
    }

    public static <P extends NetworkPacket> void sendToPlayer(ServerPlayer player, P packet) {
        ForgeNetworkHandler.sendToPlayer(player, packet);
    }

    public static <P extends NetworkPacket> void sendToAllPlayers(List<ServerPlayer> players, P packet) {
        for (ServerPlayer player : players) {
            sendToPlayer(player, packet);
        }
    }
}
