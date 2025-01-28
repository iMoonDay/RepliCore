package com.imoonday.replicore;

import com.imoonday.replicore.init.ModMenuType;
import com.imoonday.replicore.network.NetworkPacket;
import dev.architectury.injectables.annotations.ExpectPlatform;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;
import net.minecraft.world.item.CreativeModeTab;
import net.minecraft.world.item.Item;
import net.minecraft.world.level.block.Block;

import java.nio.file.Path;
import java.util.List;
import java.util.function.Supplier;

public class PlatformHelper {

    @ExpectPlatform
    public static <T extends Item> Supplier<T> registerItem(String name, Supplier<T> supplier) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <T extends Block> Supplier<T> registerBlock(String name, Supplier<T> supplier) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Supplier<CreativeModeTab> registerTab(String name, Supplier<CreativeModeTab> supplier) {
        throw new AssertionError();
    }


    @ExpectPlatform
    public static <T extends AbstractContainerMenu> Supplier<MenuType<T>> registerMenu(String id, ModMenuType.MenuSupplier<T> supplier) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static Path getConfigDir() {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static boolean isModLoaded(String modId) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <P extends NetworkPacket> void sendToServer(P packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <P extends NetworkPacket> void sendToAllPlayers(List<ServerPlayer> players, P packet) {
        throw new AssertionError();
    }

    @ExpectPlatform
    public static <P extends NetworkPacket> void sendToPlayer(ServerPlayer player, P packet) {
        throw new AssertionError();
    }
}
