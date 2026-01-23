package com.imoonday.replicore.mixin;

import com.imoonday.replicore.EventHandler;
import net.minecraft.core.BlockPos;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.level.dimension.end.EndDragonFight;
import org.jetbrains.annotations.Nullable;
import org.spongepowered.asm.mixin.Final;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(value = EndDragonFight.class)
public class EndDragonFightMixin {

    @Shadow
    @Final
    private ServerLevel level;

    @Shadow
    private boolean previouslyKilled;

    @Shadow
    private @Nullable BlockPos portalLocation;

    @Inject(method = "spawnNewGateway()V", at = @At("HEAD"))
    private void beforeSpawnNewGateway(CallbackInfo ci) {
        EventHandler.onDragonKilled(this.level, this.previouslyKilled, this.portalLocation.above());
    }
}
