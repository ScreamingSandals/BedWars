package org.screamingsandals.bedwars.special.listener;

import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.screamingsandals.bedwars.utils.ItemUtils;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.PlayerManagerImpl;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.event.player.SPlayerDropItemEvent;
import org.screamingsandals.lib.event.player.SPlayerInventoryClickEvent;
import org.screamingsandals.lib.utils.InventoryAction;
import org.screamingsandals.lib.utils.annotations.Service;

import java.util.List;

@Service
public class PermaItemListener {
    private static final String PERMA_ITEM_PREFIX = "Module:PermaItem:";
    private static final String PERMA_ITEM_PROPERTY_KEY = "lose-upon-death";
    private static final List<InventoryAction> blockedInventoryActions = List.of(
            InventoryAction.DROP_ALL_CURSOR,
            InventoryAction.DROP_ALL_SLOT,
            InventoryAction.DROP_ONE_SLOT,
            InventoryAction.DROP_ONE_CURSOR,
            InventoryAction.MOVE_TO_OTHER_INVENTORY
    );

    @OnEvent
    public void onItemRegister(ApplyPropertyToBoughtItemEventImpl event) {
        var gamePlayer = event.getPlayer();
        if (event.hasProperty(PERMA_ITEM_PROPERTY_KEY)) {
            if (!event.getBooleanProperty(PERMA_ITEM_PROPERTY_KEY)) {
                var stack = event.getStack().as(ItemStack.class); // TODO: get rid of this transformation

                if (stack.getMaxStackSize() > 1) {
                    Debug.warn(String.format("Item [%s] can be stacked and players will lose this item upon dying. Remove the 'lose-upon-death' flag for this item", stack), true);
                    return;
                }

                ItemUtils.hashIntoInvisibleString(stack, PERMA_ITEM_PREFIX);
                gamePlayer.addPermaItem(event.getStack());
                event.setStack(stack);
            }
        }
    }

    @OnEvent
    public void onItemRemoval(SPlayerInventoryClickEvent event) {
        var player = event.getPlayer();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }

        if (event.getInventory() != null) {
            if (!(event.getInventory() instanceof PlayerInventory)) {
                return;
            }
        }

        var cursorItem = event.getCursorItem();
        var slotItem = event.getCurrentItem();
        var action = event.getAction();

        String cursorItemUnhashedProp = null;
        String slotItemUnhashedProp = null;

        if (cursorItem != null) {
            cursorItemUnhashedProp = ItemUtils.unhashFromInvisibleStringStartsWith(cursorItem.as(ItemStack.class), PERMA_ITEM_PREFIX);
        }

        if (slotItem != null) {
            slotItemUnhashedProp = ItemUtils.unhashFromInvisibleStringStartsWith(slotItem.as(ItemStack.class), PERMA_ITEM_PREFIX);
        }

        if ((cursorItemUnhashedProp != null || slotItemUnhashedProp != null) && blockedInventoryActions.contains(action)) {
            event.setCancelled(true);
        }
    }

    @OnEvent
    public void onItemDrop(SPlayerDropItemEvent event) {
        var player = event.getPlayer();
        if (!PlayerManagerImpl.getInstance().isPlayerInGame(player)) {
            return;
        }
        
        var droppedItem = event.getItemDrop().getItem();
        var unhashedProperty = ItemUtils.unhashFromInvisibleStringStartsWith(droppedItem.as(ItemStack.class), PERMA_ITEM_PREFIX);
        if (unhashedProperty != null) {
            event.setCancelled(true);
        }
    }

    public static String getPermItemPropKey() {
        return PERMA_ITEM_PROPERTY_KEY;
    }
}
