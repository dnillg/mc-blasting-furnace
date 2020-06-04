package com.dgiczi.mc.blastingfurnace;

import org.bukkit.Material;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.plugin.Plugin;

import java.util.Optional;

public class PluginConfig {

    private static final String DEFAULT_MSG_EXPLOSION_WARN_TEXT = "Fire in the hole!";
    private static final boolean DEFAULT_MSG_EXPLOSION_WARN_ENABLED = true;
    private static final int DEFAULT_MIN_DELAY_TICKS = 100;
    private static final int DEFAULT_MAX_DELAY_TICKS = 250;
    private static final int DEFAULT_MAX_EXPLOSION_SIZE = 20;
    private static final int DEFAULT_MIN_EXPLOSION_SIZE = 3;
    private static final int DEFAULT_MAX_FUEL_AMOUNT = 5;
    private static final String DEFAULT_EXP_FUEL_ITEM_NAME = "GUNPOWDER";
    private static final int DEFAULT_MAX_DELAY_ITEM_AMOUNT = 5;
    private static final String DEFAULT_DELAY_ITEM_NAME = "SAND";
    private static final boolean DEFAULT_PERMISSIONS_ENABLED = false;

    private static final String KEY_DELAY_MAX_TICKS = "delay.max-ticks";
    private static final String KEY_DELAY_MIN_TICKS = "delay.min-ticks";
    private static final String KEY_DELAY_ITEM_MAX_AMOUNT = "delay.item-max-amount";
    private static final String KEY_DELAY_ITEM_NAME = "delay.item-name";
    private static final String KEY_MESSAGE_EXPLOSION_WARNING_TEXT = "message.explosion-warning.text";
    private static final String KEY_MESSAGE_EXPLOSION_WARNING_ENABLED = "message.explosion-warning.enabled";
    private static final String KEY_EXPLOSION_MAX_SIZE = "explosion.max-size";
    private static final String KEY_EXPLOSION_MIN_SIZE = "explosion.min-size";
    private static final String KEY_FUEL_ITEM_MAX_AMOUNT = "fuel.item-max-amount";
    private static final String KEY_FUEL_ITEM_NAME = "fuel.item-name";
    private static final String KEY_PERMISSIONS_ENABLED = "permissions.enabled";

    private final int maxDelayItemAmount;
    private final int maxDelayTicks;
    private final int minDelayTicks;
    private final Material delayMaterial;
    private final String warnMessageText;
    private final boolean warnMessageEnabled;
    private final int maxExplosionSize;
    private final int minExplosionSize;
    private final int maxFuelAmount;
    private final Material fuelMaterial;
    private final boolean permissionsEnabled;

    public static void setDefaults(Plugin plugin) {
        final FileConfiguration config = plugin.getConfig();
        config.addDefault(KEY_DELAY_MAX_TICKS, DEFAULT_MAX_DELAY_TICKS);
        config.addDefault(KEY_DELAY_MIN_TICKS, DEFAULT_MIN_DELAY_TICKS);
        config.addDefault(KEY_DELAY_ITEM_MAX_AMOUNT, DEFAULT_MAX_DELAY_ITEM_AMOUNT);
        config.addDefault(KEY_DELAY_ITEM_NAME, DEFAULT_DELAY_ITEM_NAME);
        config.addDefault(KEY_MESSAGE_EXPLOSION_WARNING_TEXT, DEFAULT_MSG_EXPLOSION_WARN_TEXT);
        config.addDefault(KEY_MESSAGE_EXPLOSION_WARNING_ENABLED, DEFAULT_MSG_EXPLOSION_WARN_ENABLED);
        config.addDefault(KEY_EXPLOSION_MAX_SIZE, DEFAULT_MAX_EXPLOSION_SIZE);
        config.addDefault(KEY_EXPLOSION_MIN_SIZE, DEFAULT_MIN_EXPLOSION_SIZE);
        config.addDefault(KEY_FUEL_ITEM_MAX_AMOUNT, DEFAULT_MAX_FUEL_AMOUNT);
        config.addDefault(KEY_FUEL_ITEM_NAME, DEFAULT_EXP_FUEL_ITEM_NAME);
        config.addDefault(KEY_PERMISSIONS_ENABLED, DEFAULT_PERMISSIONS_ENABLED);
    }

    public PluginConfig(FileConfiguration config) {
        this.maxDelayTicks = config.getInt(KEY_DELAY_MAX_TICKS, DEFAULT_MAX_DELAY_TICKS);
        this.minDelayTicks = config.getInt(KEY_DELAY_MIN_TICKS, DEFAULT_MIN_DELAY_TICKS);
        final String delayMaterialName = config.getString(KEY_DELAY_ITEM_NAME, DEFAULT_DELAY_ITEM_NAME);
        this.delayMaterial = Optional.ofNullable(delayMaterialName)
                .map(Material::getMaterial)
                .orElseThrow(() -> new IllegalArgumentException("Could not find material: " + delayMaterialName));
        this.maxDelayItemAmount = config.getInt(KEY_DELAY_ITEM_MAX_AMOUNT, DEFAULT_MAX_DELAY_ITEM_AMOUNT);
        this.warnMessageText = config.getString(KEY_MESSAGE_EXPLOSION_WARNING_TEXT, DEFAULT_MSG_EXPLOSION_WARN_TEXT);
        this.warnMessageEnabled =
                config.getBoolean(KEY_MESSAGE_EXPLOSION_WARNING_ENABLED, DEFAULT_MSG_EXPLOSION_WARN_ENABLED);
        this.maxExplosionSize = config.getInt(KEY_EXPLOSION_MAX_SIZE, DEFAULT_MAX_EXPLOSION_SIZE);
        this.minExplosionSize = config.getInt(KEY_EXPLOSION_MIN_SIZE, DEFAULT_MIN_EXPLOSION_SIZE);
        this.maxFuelAmount = config.getInt(KEY_FUEL_ITEM_MAX_AMOUNT, DEFAULT_MAX_FUEL_AMOUNT);
        final String fuelMaterialName = config.getString(KEY_FUEL_ITEM_NAME, DEFAULT_EXP_FUEL_ITEM_NAME);
        this.fuelMaterial = Optional.ofNullable(fuelMaterialName)
                .map(Material::getMaterial)
                .orElseThrow(() -> new IllegalArgumentException("Could not find material: " + delayMaterialName));
        this.permissionsEnabled = config.getBoolean(KEY_PERMISSIONS_ENABLED, DEFAULT_PERMISSIONS_ENABLED);
    }

    public int getMaxDelayItemAmount() {
        return maxDelayItemAmount;
    }

    public int getMaxDelayTicks() {
        return maxDelayTicks;
    }

    public int getMinDelayTicks() {
        return minDelayTicks;
    }

    public Material getDelayMaterial() {
        return delayMaterial;
    }

    public String getWarnMessageText() {
        return warnMessageText;
    }

    public boolean getWarnMessageEnabled() {
        return warnMessageEnabled;
    }

    public int getMaxExplosionSize() {
        return maxExplosionSize;
    }

    public int getMinExplosionSize() {
        return minExplosionSize;
    }

    public int getMaxFuelAmount() {
        return maxFuelAmount;
    }

    public Material getFuelMaterial() {
        return fuelMaterial;
    }

    public boolean isPermissionsEnabled() {
        return permissionsEnabled;
    }
}
