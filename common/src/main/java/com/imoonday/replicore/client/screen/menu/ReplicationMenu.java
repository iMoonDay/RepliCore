package com.imoonday.replicore.client.screen.menu;

import com.imoonday.replicore.block.ReplicationTableBlock;
import com.imoonday.replicore.config.ModConfig;
import com.imoonday.replicore.init.ModMenuType;
import com.imoonday.replicore.item.CoreItem;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.DataSlot;
import net.minecraft.world.inventory.ItemCombinerMenu;
import net.minecraft.world.inventory.ItemCombinerMenuSlotDefinition;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.Enchantment;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.LevelEvent;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;

import java.util.Map;

public class ReplicationMenu extends ItemCombinerMenu {

    public static final int INPUT_SLOT = 0;
    public static final int ADDITIONAL_SLOT = 1;
    public static final int RESULT_SLOT = 2;
    private final Level level;
    private final DataSlot cost = DataSlot.standalone();

    public ReplicationMenu(int containerId, Inventory playerInventory) {
        this(containerId, playerInventory, ContainerLevelAccess.NULL);
    }

    public ReplicationMenu(int containerId, Inventory playerInventory, ContainerLevelAccess access) {
        super(ModMenuType.REPLICATION.get(), containerId, playerInventory, access);
        this.level = playerInventory.player.level();
        this.addDataSlot(this.cost);
    }

    @Override
    protected boolean mayPickup(Player player, boolean hasStack) {
        int cost = this.cost.get();
        return (player.getAbilities().instabuild || player.experienceLevel >= cost) && cost > 0;
    }

    @Override
    protected void onTake(Player player, ItemStack stack) {
        if (!player.getAbilities().instabuild) {
            player.giveExperienceLevels(-this.cost.get());
        }

        ItemStack core = this.inputSlots.getItem(1);
        if (!core.isEmpty()) {
            core.shrink(1);
            this.inputSlots.setItem(1, core);
        }

        this.cost.set(0);
        this.access.execute((level, blockPos) -> level.levelEvent(LevelEvent.SOUND_SMITHING_TABLE_USED, blockPos, 0));
    }

    @Override
    protected boolean isValidBlock(BlockState state) {
        return state.getBlock() instanceof ReplicationTableBlock;
    }

    @Override
    public void createResult() {
        ItemStack target = this.inputSlots.getItem(0);
        this.cost.set(0);
        ModConfig config = ModConfig.get(level.isClientSide);
        if (target.isEmpty() || config.isBlacklisted(target)) {
            this.resultSlots.setItem(0, ItemStack.EMPTY);
        } else {
            ItemStack core = this.inputSlots.getItem(1);
            if (!core.isEmpty() && core.getItem() instanceof CoreItem coreItem && coreItem.canDuplicate(target)) {
                ItemStack copied = target.copy();
                this.resultSlots.setItem(0, copied);
                int cost = calculateCost(target, config.extraCostForEnchantments);
                this.cost.set(cost);
            } else {
                this.resultSlots.setItem(0, ItemStack.EMPTY);
            }
        }

        this.broadcastChanges();
    }

    private int calculateCost(ItemStack stack, boolean extraCostForEnchantments) {
        int cost = stack.getCount();
        if (cost > 1) {
            cost = cost / 4 + (cost % 4 == 0 ? 0 : 1);
        }
        if (extraCostForEnchantments) {
            Map<Enchantment, Integer> enchantments = EnchantmentHelper.getEnchantments(stack);
            for (Map.Entry<Enchantment, Integer> entry : enchantments.entrySet()) {
                Enchantment enchantment = entry.getKey();
                if (enchantment.isCurse()) continue;
                cost += entry.getValue() * 2;
            }
        }
        return cost;
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
