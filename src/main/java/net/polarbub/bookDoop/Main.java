package net.polarbub.bookDoop;

import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.command.v1.ClientCommandManager;
import net.minecraft.text.LiteralText;
import static net.fabricmc.fabric.api.client.command.v1.ClientCommandManager.literal;

public class Main implements ClientModInitializer {
    @Override
    public void onInitializeClient() {
        ClientCommandManager.DISPATCHER.register(literal("book")
                .then(literal("keep").executes(ctx -> {
                    if (!BookDupe.createBook(ctx.getSource().getPlayer())) {
                        ctx.getSource().sendFeedback(new LiteralText("No writeable book in hotbar"));
                    }
                    return 0;
                }))
                .then(literal("yeet").executes(ctx -> {
                    if (!BookDupe.createAndThrowBook(ctx.getSource().getPlayer())) {
                        ctx.getSource().sendFeedback(new LiteralText("No writeable book in hotbar"));
                    }
                    return 0;
                }))
        );
    }
}
