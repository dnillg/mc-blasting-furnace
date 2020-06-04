package com.dgiczi.mc.blastingfurnace;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
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

    private static final String PERMISSION_USE = "blasting-furnace.use";

    private final PluginConfig cfg;
    private final BlastingFurnacePlugin plugin;
    private final Logger logger;

    public FurnaceExplosionListener(BlastingFurnacePlugin plugin, PluginConfig pluginConfig) {
        this.plugin = plugin;
        this.logger = plugin.getLogger();
        this.cfg = pluginConfig;
    }

    @EventHandler
    public void onInventoryClick(InventoryClickEvent event) {
        if (!hasPermission(event) ||
                !targetIsFuelSlot(event)
                || !isExplosionFuel(event)
                || !isPlaceAction(event)
                || !isDestinationFurnace(event)) {
            return;
        }

        final int maxSandAmount = cfg.getMaxDelayItemAmount();
        final int maxDelayTicks = cfg.getMaxDelayTicks();
        final int minDelayTicks = cfg.getMinDelayTicks();

        final Player player = (Player) event.getWhoClicked();
        final World world = player.getWorld();
        final ItemStack eventItemStack = player.getItemOnCursor();
        final FurnaceInventory furnaceInventory = (FurnaceInventory) event.getClickedInventory();
        final Location location = furnaceInventory.getLocation();
        final Furnace furnace = furnaceInventory.getHolder();

        int sandCount = Optional.ofNullable(furnaceInventory.getSmelting())
                .filter(stack -> cfg.getDelayMaterial().equals(stack.getType()))
                .map(stack -> Math.min(stack.getAmount(), maxSandAmount))
                .orElse(0);

        player.setItemOnCursor(null);
        final int powderCount = addRemainingPowderToPlayerInventory(player, eventItemStack);
        int delayTicks = minDelayTicks + (maxDelayTicks - minDelayTicks) / maxSandAmount * sandCount;
        burnFurnace(furnace, (short) delayTicks);

        if (cfg.getWarnMessageEnabled()) {
            player.sendMessage(ChatColor.DARK_RED + cfg.getWarnMessageText());
        }

        final int explosionSize = calculateExplosionSize(powderCount);
        plugin.getServer().getScheduler().scheduleSyncDelayedTask(plugin,
                        () -> createExplosion(world, location, player, explosionSize), delayTicks);
    }

    private boolean hasPermission(InventoryClickEvent event) {
        return !cfg.isPermissionsEnabled() || event.getWhoClicked().hasPermission(PERMISSION_USE);
    }

    private void createExplosion(World world, Location location, Player player, int explosionSize) {
        if (world.getBlockAt(location).getType().equals(Material.FURNACE)) {

            logger.info(String.format(
                    "Furnace explosion in world \"%s\" at X:%d, Y:%d, Z:%d, Size: %d, Player: %s.",
                    location.getWorld().getName(), location.getBlockX(), location.getBlockY(), location.getBlockZ(),
                    explosionSize, player.getName()));
            world.createExplosion(location, explosionSize);
        }
    }

    private int calculateExplosionSize(int powderCount) {
        final double multiplier =
                (cfg.getMaxExplosionSize() - cfg.getMinExplosionSize()) / (cfg.getMaxFuelAmount() - 1.0);

        final int offset = (int) Math.round((powderCount - 1) * multiplier);
        return Math.min(cfg.getMinExplosionSize() + offset, cfg.getMaxExplosionSize());
    }

    private void burnFurnace(Furnace furnace, short delayTicks) {
        furnace.setBurnTime(delayTicks);
        furnace.update();
    }

    private int addRemainingPowderToPlayerInventory(Player player, ItemStack eventItemStack) {
        final int powderCount = eventItemStack.getAmount();
        if (powderCount > cfg.getMaxFuelAmount()) {
            final ItemStack remainderItemStack = eventItemStack.clone();
            remainderItemStack.setAmount(powderCount - cfg.getMaxFuelAmount());
            player.getInventory().addItem(remainderItemStack);
        }
        return powderCount;
    }

    private boolean targetIsFuelSlot(InventoryClickEvent event) {
        return event.getSlotType().equals(InventoryType.SlotType.FUEL);
    }

    private boolean isExplosionFuel(InventoryClickEvent event) {
        final Player player = (Player) event.getWhoClicked();
        final ItemStack eventItemStack = player.getItemOnCursor();
        return eventItemStack.getType().equals(cfg.getFuelMaterial());
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
