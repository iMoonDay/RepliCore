package com.imoonday.replicore.command;

import com.imoonday.replicore.EventHandler;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.minecraft.commands.CommandSourceStack;
import net.minecraft.network.chat.Component;

import static net.minecraft.commands.Commands.literal;

public class ReloadCommand {

    public static LiteralArgumentBuilder<CommandSourceStack> builder() {
        return literal("reload").executes(context -> {
            EventHandler.loadConfig();
            EventHandler.updateConfig(context.getSource().getServer());
            context.getSource().sendSuccess(() -> Component.translatable("message.replicore.reload"), true);
            return 1;
        });
    }
}
