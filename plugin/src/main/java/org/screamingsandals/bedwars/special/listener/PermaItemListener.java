package org.screamingsandals.bedwars.special.listener;

import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.inventory.ItemStack;
import org.screamingsandals.bedwars.Main;
import org.screamingsandals.bedwars.api.APIUtils;
import org.screamingsandals.bedwars.events.ApplyPropertyToBoughtItemEventImpl;
import org.screamingsandals.bedwars.lib.debug.Debug;
import org.screamingsandals.bedwars.player.PlayerManager;
import org.screamingsandals.lib.event.OnEvent;
import org.screamingsandals.lib.utils.annotations.Service;
import org.screamingsandals.lib.utils.annotations.methods.OnPostEnable;

import java.util.HashSet;

@Service
public class PermaItemListener implements Listener {
    private static final String PERMA_ITEM_PREFIX = "Module:PermaItem:";
    private static final String PERMA_ITEM_PROPERTY_KEY = "lose-upon-death";
    private static final HashSet<InventoryAction> blockedInventoryActions = initializeBlockedInventoryActions();

    @OnPostEnable
    public void postEnable() {
        Main.getInstance().registerBedwarsListener(this); // TODO: get rid of platform events
    }

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

                APIUtils.hashIntoInvisibleString(stack, PERMA_ITEM_PREFIX);
                gamePlayer.addPermaItem(stack);
                event.setStack(stack);
            }
        }
    }

    @EventHandler
    public void onItemRemoval(InventoryClickEvent event) {
        Player player = (Player) event.getWhoClicked();
        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }

        if (event.getClickedInventory() != null) {
            if (event.getClickedInventory().getType() != InventoryType.PLAYER) {
                return;
            }
        }

        ItemStack cursorItem = event.getCursor();
        ItemStack slotItem = event.getCurrentItem();
        InventoryAction action = event.getAction();

        String cursorItemUnhashedProp = null;
        String slotItemUnhashedProp = null;

        if (cursorItem != null) {
            cursorItemUnhashedProp = APIUtils.unhashFromInvisibleStringStartsWith(cursorItem, PERMA_ITEM_PREFIX);
        }

        if (slotItem != null) {
            slotItemUnhashedProp = APIUtils.unhashFromInvisibleStringStartsWith(slotItem, PERMA_ITEM_PREFIX);
        }

        if ((cursorItemUnhashedProp != null || slotItemUnhashedProp != null) && blockedInventoryActions.contains(action)) {
            event.setCancelled(true);
        }
    }

    @EventHandler
    public void onItemDrop(PlayerDropItemEvent event) {
        Player player = event.getPlayer();
        if (!PlayerManager.getInstance().isPlayerInGame(player.getUniqueId())) {
            return;
        }
        
        Item droppedItem = event.getItemDrop();
        String unhashedProperty = APIUtils.unhashFromInvisibleStringStartsWith(droppedItem.getItemStack(), PERMA_ITEM_PREFIX);
        if (unhashedProperty != null) {
            event.setCancelled(true);
        }
    }

    private static HashSet<InventoryAction> initializeBlockedInventoryActions() {
        HashSet<InventoryAction> blockedInventoryActions = new HashSet<>();

        blockedInventoryActions.add(InventoryAction.DROP_ALL_CURSOR);
        blockedInventoryActions.add(InventoryAction.DROP_ALL_SLOT);
        blockedInventoryActions.add(InventoryAction.DROP_ONE_SLOT);
        blockedInventoryActions.add(InventoryAction.DROP_ONE_CURSOR);
        blockedInventoryActions.add(InventoryAction.MOVE_TO_OTHER_INVENTORY);

        return blockedInventoryActions;
    }

    public static String getPermItemPropKey() {
        return PERMA_ITEM_PROPERTY_KEY;
    }
}
