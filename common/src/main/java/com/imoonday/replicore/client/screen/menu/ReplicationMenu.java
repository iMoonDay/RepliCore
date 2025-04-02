package com.imoonday.replicore.client.screen.menu;

import com.imoonday.replicore.block.ReplicationTableBlock;
import com.imoonday.replicore.config.ModConfig;
import com.imoonday.replicore.init.ModMenuType;
import com.imoonday.replicore.item.CoreItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.*;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

public class ReplicationMenu extends ItemCombinerMenu {

    private final Level level;
    private final DataSlot cost = DataSlot.standalone();
    private long lastTakeTime;

    public ReplicationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public ReplicationMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenuType.REPLICATION.get(), containerId, playerInventory, access);
        this.level = playerInventory.player.level();
        this.addDataSlot(this.cost).set(-1);
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        int cost = this.cost.get();
        return (player.getAbilities().instabuild || player.experienceLevel >= cost) && cost >= 0;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            int cost = this.cost.get();
            player.giveExperienceLevels(-cost);
        }

        ItemStack core = this.inputSlots.getItem(1);
        if (!core.isEmpty()) {
            core.shrink(1);
            this.inputSlots.setItem(1, core);

            this.access.execute((level, blockPos) -> {
                long time = System.currentTimeMillis();
                if (time - this.lastTakeTime > 250) {
                    level.levelEvent(LevelEvent.SOUND_SMITHING_TABLE_USED, blockPos, 0);
                }
                this.lastTakeTime = time;
            });
        }
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        Slot slot = this.slots.get(index);
        if (slot.container == this.resultSlots && !this.mayPickup(player, true)) {
            return ItemStack.EMPTY;
        }
        return super.quickMoveStack(player, index);
    }

    @Override
    protected boolean isValidBlock(BlockState state) {
        return state.getBlock() instanceof ReplicationTableBlock;
    }

    @Override
    public void createResult() {
        ModConfig config = ModConfig.get(level.isClientSide);
        ItemStack target = this.inputSlots.getItem(0);
        if (!target.isEmpty() && !config.isBlacklisted(target)) {
            ItemStack core = this.inputSlots.getItem(1);
            if (!core.isEmpty() && core.getItem() instanceof CoreItem coreItem && coreItem.canDuplicate(target)) {
                int cost = config.costConfig.calculateTotalCost(target, stack -> config.isBlacklisted(stack) || !coreItem.canDuplicate(stack));
                if (cost >= 0) {
                    ItemStack copied = target.copy();
                    this.resultSlots.setItem(0, copied);
                    this.cost.set(cost);
                    this.broadcastChanges();
                    return;
                }
            }
        }

        this.cost.set(-1);
        this.resultSlots.setItem(0, ItemStack.EMPTY);
        this.broadcastChanges();
    }

    @NotNull
    @Override
    protected ItemCombinerMenuSlotDefinition createInputSlotDefinitions() {
        return ItemCombinerMenuSlotDefinition.create()
                                             .withSlot(0, 27, 47, itemStack -> true)
                                             .withSlot(1, 76, 47, itemStack -> itemStack.getItem() instanceof CoreItem)
                                             .withResultSlot(2, 134, 47).build();
    }

    public int getCost() {
        return this.cost.get();
    }
}
