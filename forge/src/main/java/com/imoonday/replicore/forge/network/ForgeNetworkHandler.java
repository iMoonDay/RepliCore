package com.imoonday.replicore.forge.network;

import com.imoonday.replicore.RepliCore;
import com.imoonday.replicore.network.NetworkPacket;
import com.imoonday.replicore.network.SyncConfigS2CPacket;
import net.minecraft.client.Minecraft;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerPlayer;
import net.minecraftforge.network.NetworkDirection;
import net.minecraftforge.network.NetworkEvent;
import net.minecraftforge.network.NetworkRegistry;
import net.minecraftforge.network.simple.SimpleChannel;

import java.util.function.Supplier;

public class ForgeNetworkHandler {

    private static final String PROTOCOL_VERSION = "1";
    public static final SimpleChannel SIMPLE_CHANNEL = NetworkRegistry.newSimpleChannel(
            new ResourceLocation(RepliCore.MOD_ID, "network"),
            () -> PROTOCOL_VERSION,
            PROTOCOL_VERSION::equals,
            PROTOCOL_VERSION::equals
    );

    public static void init() {
        RepliCore.LOGGER.info(String.format("Initializing %s network...", RepliCore.MOD_ID));
        SIMPLE_CHANNEL.registerMessage(0, SyncConfigS2CPacket.class, SyncConfigS2CPacket::write, SyncConfigS2CPacket::new, ForgeNetworkHandler::handle);
        RepliCore.LOGGER.info(String.format("Initialized %s network!", RepliCore.MOD_ID));
    }

    public static <T extends NetworkPacket> void handle(T packet, Supplier<NetworkEvent.Context> ctx) {
        NetworkEvent.Context context = ctx.get();
        if (context.getDirection().getReceptionSide().isClient()) {
            context.enqueueWork(() -> Client.clientHandle(packet));
        } else {
            context.enqueueWork(() -> packet.handle(context.getSender()));
        }
        context.setPacketHandled(true);
    }

    public static <T extends NetworkPacket> void sendToPlayer(ServerPlayer playerEntity, T packet) {
        SIMPLE_CHANNEL.sendTo(packet, playerEntity.connection.connection, NetworkDirection.PLAY_TO_CLIENT);
    }

    public static void sendToServer(Object objectToSend) {
        SIMPLE_CHANNEL.sendToServer(objectToSend);
    }

    private static class Client {

        private static <T extends NetworkPacket> void clientHandle(T packet) {
            packet.handle(Minecraft.getInstance().player);
        }
    }
}
