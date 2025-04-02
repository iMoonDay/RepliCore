package com.imoonday.replicore.fabric;

import com.imoonday.replicore.EventHandler;
import com.imoonday.replicore.RepliCore;
import com.imoonday.replicore.fabric.network.FabricNetworkHandler;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents;
import net.fabricmc.fabric.api.networking.v1.ServerPlayConnectionEvents;

public final class RepliCoreFabric implements ModInitializer {

    @Override
    public void onInitialize() {
        RepliCore.init();
        FabricNetworkHandler.init();
        ServerPlayConnectionEvents.JOIN.register((listener, sender, server) -> EventHandler.syncConfig(listener.player));
        ServerLifecycleEvents.SERVER_STARTING.register(server -> EventHandler.loadConfig());
        ServerLifecycleEvents.START_DATA_PACK_RELOAD.register((server, manager) -> EventHandler.onReload(server));
    }
}
