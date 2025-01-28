package com.imoonday.replicore.fabric.client;

import com.imoonday.replicore.EventHandler;
import com.imoonday.replicore.client.screen.ReplicationScreen;
import com.imoonday.replicore.init.ModMenuType;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.networking.v1.ClientPlayConnectionEvents;
import net.minecraft.client.gui.screens.MenuScreens;

public final class RepliCoreFabricClient implements ClientModInitializer {

    @Override
    public void onInitializeClient() {
        MenuScreens.register(ModMenuType.REPLICATION.get(), ReplicationScreen::new);
        ClientPlayConnectionEvents.DISCONNECT.register((listener, minecraft) -> {
            EventHandler.onDisconnect();
        });
    }
}
