package com.imoonday.replicore.forge.client;

import com.imoonday.replicore.client.screen.ReplicationScreen;
import com.imoonday.replicore.init.ModMenuType;
import net.minecraft.client.gui.screens.MenuScreens;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;

@Mod.EventBusSubscriber(value = Dist.CLIENT, bus = Mod.EventBusSubscriber.Bus.MOD)
public class ClientModEventHandler {

    @SubscribeEvent
    public static void registerMenuScreens(FMLClientSetupEvent e) {
        MenuScreens.register(ModMenuType.REPLICATION.get(), ReplicationScreen::new);
    }
}
