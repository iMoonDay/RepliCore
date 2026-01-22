package com.imoonday.replicore.mixin;

import com.imoonday.replicore.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.entity.boss.enderdragon.EnderDragon;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import net.minecraft.world.level.levelgen.Heightmap;
import net.minecraft.world.level.levelgen.feature.EndPodiumFeature;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

import java.util.UUID;

@Mixin(EndDragonFight.class)
public class EndDragonFightMixin {

    @Shadow
    @Final
    private ServerLevel level;

    @Shadow
    private boolean previouslyKilled;

    @Shadow
    @Final
    private BlockPos origin;

    @Shadow
    private @Nullable UUID dragonUUID;

    @Inject(method = "setDragonKilled", at = @At("HEAD"))
    private void setDragonKilled(EnderDragon dragon, CallbackInfo ci) {
        if (dragon.getUUID().equals(this.dragonUUID)) {
            EventHandler.onDragonKilled(this.level, this.previouslyKilled, this.level.getHeightmapPos(Heightmap.Types.MOTION_BLOCKING, EndPodiumFeature.getLocation(this.origin)));
        }
    }
}
