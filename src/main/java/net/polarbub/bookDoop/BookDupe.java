package net.polarbub.bookDoop;

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import net.fabricmc.api.EnvType;
import net.fabricmc.api.Environment;

import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.entity.player.PlayerInventory;
import net.minecraft.item.ItemStack;
import net.minecraft.item.Items;
import net.minecraft.nbt.NbtList;
import net.minecraft.nbt.NbtString;
import net.minecraft.network.packet.c2s.play.BookUpdateC2SPacket;
import net.minecraft.network.packet.c2s.play.ClickSlotC2SPacket;
import net.minecraft.screen.slot.SlotActionType;

@Environment(EnvType.CLIENT)
public class BookDupe {
    private static final ItemStack WRITEABLE_BOOK_STACK = new ItemStack(Items.WRITABLE_BOOK);
    private static final ItemStack DUPE_BOOK_STACK = new ItemStack(Items.WRITABLE_BOOK);
    private static final List<String> PAGES = new ArrayList<>();

    static {
        // Create large page and add it to the pages list
        StringBuilder builder = new StringBuilder();
        for (int i = 0; i < 65536/3; i++) {
            builder.append((char)2048);
        }
        PAGES.add(builder.toString());

        // Initialize the item stack
        NbtList pages = new NbtList();
        pages.add(NbtString.of(builder.toString()));
        DUPE_BOOK_STACK.setSubNbt("pages", pages);
    }

    private static int createBookInternal(ClientPlayerEntity player) {
        // Search for book
        int slot = player.getInventory().selectedSlot; // First check if there's a writeable book in the main hand
        if (!ItemStack.areItemsEqual(WRITEABLE_BOOK_STACK, player.getInventory().getStack(slot))) {
            // Otherwise, search the entire inventory for an empty writeable book
            slot = player.getInventory().getSlotWithStack(WRITEABLE_BOOK_STACK);
        }

        // Ensure slot is valid and in hotbar
        if (!PlayerInventory.isValidHotbarIndex(slot)) {
            return -1;
        }

        // Update book item
        player.networkHandler.sendPacket(new BookUpdateC2SPacket(slot, PAGES, Optional.of("")));
        return slot;
    }

    public static boolean createBook(ClientPlayerEntity player) {
        return createBookInternal(player) >= 0;
    }

    public static boolean createAndThrowBook(ClientPlayerEntity player) {
        // Create book
        int slot = createBookInternal(player);
        if (slot < 0) return false;

        // Throw book
        Int2ObjectOpenHashMap<ItemStack> map = new Int2ObjectOpenHashMap<>();
        map.put(36 + slot, DUPE_BOOK_STACK);
        player.networkHandler.sendPacket(new ClickSlotC2SPacket(
                player.currentScreenHandler.syncId,
                player.currentScreenHandler.getRevision(),
                36 + slot, 0,
                SlotActionType.THROW,
                DUPE_BOOK_STACK,
                map
        ));
        return true;
    }
}