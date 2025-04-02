package com.imoonday.replicore.client.screen;

import com.imoonday.replicore.RepliCore;
import com.imoonday.replicore.client.screen.menu.ReplicationMenu;
import com.imoonday.replicore.config.ModConfig;
import com.imoonday.replicore.core.CoreTier;
import com.imoonday.replicore.core.CoreTiers;
import com.imoonday.replicore.item.CoreItem;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.inventory.ItemCombinerScreen;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;

public class ReplicationScreen extends ItemCombinerScreen<ReplicationMenu> {

    private static final ResourceLocation TEXTURE = RepliCore.id("textures/gui/replication.png");
    private static final Component BLACKLIST_DENY_TEXT = Component.translatable("message.replicore.blacklist_deny");
    private static final Component NO_FULL_DURABILITY_TEXT = Component.translatable("message.replicore.no_full_durability");
    private static final Component TIER_MISMATCH_TEXT = Component.translatable("message.replicore.tier_mismatch");
    private final Player player;

    public ReplicationScreen(ReplicationMenu menu, Inventory playerInventory, Component title) {
        super(menu, playerInventory, title, TEXTURE);
        this.player = playerInventory.player;
        this.titleLabelX = 60;
        this.titleLabelY = 15;
    }

    @Override
    public boolean keyPressed(int keyCode, int scanCode, int modifiers) {
        if (keyCode == 256) {
            this.minecraft.player.closeContainer();
        }
        return super.keyPressed(keyCode, scanCode, modifiers);
    }

    @Override
    protected void renderLabels(GuiGraphics guiGraphics, int mouseX, int mouseY) {
        super.renderLabels(guiGraphics, mouseX, mouseY);

        Slot targetSlot = this.menu.getSlot(0);
        if (!targetSlot.hasItem()) return;

        int color = 0XFF6060;
        Component component = null;
        ItemStack stack = targetSlot.getItem();
        if (ModConfig.getClientCache().isBlacklisted(stack)) {
            component = BLACKLIST_DENY_TEXT;
        } else {
            Slot coreSlot = this.menu.getSlot(1);
            if (coreSlot.hasItem()) {
                ItemStack core = coreSlot.getItem();
                if (core.getItem() instanceof CoreItem coreItem) {
                    if (!this.menu.getSlot(2).hasItem()) {
                        CoreTier tier = coreItem.getTier();
                        if (!tier.canDuplicate(stack)) {
                            if (tier.getTier() < CoreTiers.VOID.getTier() && stack.isDamaged()) {
                                component = NO_FULL_DURABILITY_TEXT;
                            } else {
                                component = TIER_MISMATCH_TEXT;
                            }
                        }
                    } else {
                        int cost = this.menu.getCost();
                        if (cost >= 0) {
                            component = Component.translatable("message.replicore.cost", cost);
                            if (this.menu.getSlot(2).mayPickup(this.player)) {
                                color = 0X80FF20;
                            }
                        } else {
                            component = TIER_MISMATCH_TEXT;
                        }
                    }
                }
            }
        }

        if (component != null) {
            int x = this.imageWidth - 8 - this.font.width(component) - 2;
            int y = 69;
            guiGraphics.fill(x - 2, 67, this.imageWidth - 8, 79, 1325400064);
            guiGraphics.drawString(this.font, component, x, y, color);
        }
    }

    @Override
    protected void renderBg(GuiGraphics guiGraphics, float partialTick, int mouseX, int mouseY) {
        super.renderBg(guiGraphics, partialTick, mouseX, mouseY);
        guiGraphics.blit(TEXTURE, this.leftPos + 59, this.topPos + 20, 0, this.imageHeight + (this.menu.getSlot(0).hasItem() ? 0 : 16), 110, 16);
    }

    @Override
    protected void renderErrorIcon(GuiGraphics guiGraphics, int x, int y) {
        if (this.menu.getSlot(0).hasItem() && this.menu.getSlot(1).hasItem() && !this.menu.getSlot(this.menu.getResultSlot()).hasItem()) {
            guiGraphics.blit(TEXTURE, x + 99, y + 45, this.imageWidth, 0, 28, 21);
        }
    }
}
