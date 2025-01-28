package com.imoonday.replicore.init;

import com.imoonday.replicore.PlatformHelper;
import com.imoonday.replicore.client.screen.menu.ReplicationMenu;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.MenuType;

import java.util.function.Supplier;

public class ModMenuType {

    public static final Supplier<MenuType<ReplicationMenu>> REPLICATION = PlatformHelper.registerMenu("replication", ReplicationMenu::new);

    public interface MenuSupplier<T extends AbstractContainerMenu> {

        T create(int i, Inventory inventory);
    }
}
