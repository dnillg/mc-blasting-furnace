package com.dgiczi.mc.furnaceplugin;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Furnace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryAction;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.inventory.FurnaceInventory;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;

import java.util.Optional;
import java.util.logging.Logger;

public class FurnaceExplosionListener implements Listener {

    private static final int MIN_DELAY_TICKS = 100;
    private static final int MAX_DELAY_TICKS = 250;
    private static final int MAX_EXPLOSION_SIZE = 20;

    private final FurnacePlugin plugin;
    private final Logger logger;

    public FurnaceExplosionListener(FurnacePlugin plugin) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!targetIsFuelSlot(event)
                || !isGunpowder(event)
                || !isPlaceAction(event)
                || !isDestinationFurnace(event)) {
            return;
        }

        Player player = (Player) event.getWhoClicked();
        final ItemStack eventItemStack = player.getItemOnCursor();
        FurnaceInventory furnaceInventory = (FurnaceInventory) event.getClickedInventory();
        final Location location = furnaceInventory.getLocation();
        final Furnace furnace = furnaceInventory.getHolder();

        int sandCount = Optional.ofNullable(furnaceInventory.getSmelting())
                .filter(stack -> stack != null && Material.SAND.equals(stack.getType()))
                .map(stack -> Math.min(stack.getAmount(), 5))
                .orElse(0);
        int delayTicks = MIN_DELAY_TICKS + (MAX_DELAY_TICKS - MIN_DELAY_TICKS) / 5 * sandCount;

        player.setItemOnCursor(null);
        furnace.setBurnTime((short) delayTicks);
        furnace.update();

        player.sendMessage(ChatColor.DARK_RED + "Fire in the hole!");
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin, () -> {
            if (player.getWorld().getBlockAt(location).getType().equals(Material.FURNACE)) {
                final int explosionSize = Math.min(eventItemStack.getAmount() * 2, MAX_EXPLOSION_SIZE);
                logger.info(String.format(
                        "Furnace explosion at X:%d, Y:%d, Z:%d with size %d.",
                        location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                        explosionSize));
                player.getWorld().createExplosion(location, explosionSize);
            }
        }, delayTicks);
    }

    private boolean targetIsFuelSlot(InventoryClickEvent event) {
        return event.getSlotType().equals(InventoryType.SlotType.FUEL);
    }

    private boolean isGunpowder(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack eventItemStack = player.getItemOnCursor();
        return eventItemStack.getType().equals(Material.GUNPOWDER);
    }

    private boolean isPlaceAction(InventoryClickEvent event) {
        return event.getAction().equals(InventoryAction.PLACE_ALL);
    }

    private boolean isDestinationFurnace(InventoryClickEvent event) {
        final Inventory clickedInventory = event.getClickedInventory();
        return clickedInventory != null
                && clickedInventory.getType().equals(InventoryType.FURNACE)
                && clickedInventory.getLocation() != null;
    }

}
