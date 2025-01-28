package com.imoonday.replicore;

import com.imoonday.replicore.config.ModConfig;
import com.imoonday.replicore.init.ModItems;
import com.imoonday.replicore.network.SyncConfigS2CPacket;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.MinecraftServer;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

import java.util.List;

public class EventHandler {

    public static void onDragonKilled(ServerLevel level, boolean previouslyKilled, BlockPos center) {
        Item crystal = ModItems.DRAGON_SOUL_CRYSTAL.get();
        ModConfig config = ModConfig.get();
        int count = previouslyKilled ? config.crystalDroppingCount : config.firstCrystalDroppingCount;
        List<ServerPlayer> players = level.getPlayers(serverPlayer -> !serverPlayer.isSpectator() &&
                                                                      serverPlayer.isAlive() &&
                                                                      center.distToCenterSqr(serverPlayer.position()) <= config.maxDropDistance * config.maxDropDistance
        );
        for (ServerPlayer player : players) {
            player.getInventory().placeItemBackInInventory(new ItemStack(crystal, count));
        }
    }

    public static void loadConfig() {
        ModConfig.load();
    }

    public static void syncConfig(Player player) {
        if (player instanceof ServerPlayer serverPlayer) {
            PlatformHelper.sendToPlayer(serverPlayer, new SyncConfigS2CPacket(ModConfig.get().save(new CompoundTag())));
        }
    }

    public static void updateConfig(MinecraftServer server) {
        PlatformHelper.sendToAllPlayers(server.getPlayerList().getPlayers(), new SyncConfigS2CPacket(ModConfig.get().save(new CompoundTag())));
    }

    public static void onDisconnect() {
        ModConfig.getClientCache().reset();
    }
}
