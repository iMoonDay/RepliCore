package com.imoonday.replicore.fabric;

import com.imoonday.replicore.RepliCore;
import com.imoonday.replicore.fabric.network.FabricNetworkHandler;
import com.imoonday.replicore.init.ModMenuType;
import com.imoonday.replicore.network.NetworkPacket;
import net.fabricmc.loader.api.FabricLoader;
import net.minecraft.core.Registry;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.flag.FeatureFlags;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class PlatformHelperImpl {

    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> supplier) {
        T item = Registry.register(BuiltInRegistries.ITEM, RepliCore.id(name), supplier.get());
        return () -> item;
    }

    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> supplier) {
        T block = Registry.register(BuiltInRegistries.BLOCK, RepliCore.id(name), supplier.get());
        return () -> block;
    }

    public static Supplier<CreativeModeTab> registerTab(String name, Supplier<CreativeModeTab> supplier) {
        CreativeModeTab tab = Registry.register(BuiltInRegistries.CREATIVE_MODE_TAB, RepliCore.id(name), supplier.get());
        return () -> tab;
    }

    public static Path getConfigDir() {
        return FabricLoader.getInstance().getConfigDir();
    }

    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String id, ModMenuType.MenuSupplier<T> supplier) {
        MenuType<T> type = Registry.register(BuiltInRegistries.MENU, RepliCore.id(id), new MenuType<>(supplier::create, FeatureFlags.VANILLA_SET));
        return () -> type;
    }

    public static boolean isModLoaded(String modId) {
        return FabricLoader.getInstance().isModLoaded(modId);
    }

    public static <P extends NetworkPacket> void sendToServer(P packet) {
        FabricNetworkHandler.sendToServer(packet);
    }

    public static <P extends NetworkPacket> void sendToPlayer(ServerPlayer player, P packet) {
        FabricNetworkHandler.sendToPlayer(player, packet);
    }

    public static <P extends NetworkPacket> void sendToAllPlayers(List<ServerPlayer> players, P packet) {
        FabricNetworkHandler.sendToAllPlayers(players, packet);
    }
}
