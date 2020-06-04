package com.dgiczi.mc.blastingfurnace;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class BlastingFurnacePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final Logger logger = getLogger();
        final Server server = getServer();

        PluginConfig.setDefaults(this);
        final PluginConfig pluginConfig = new PluginConfig(getConfig());
        final FurnaceExplosionListener listener = new FurnaceExplosionListener(this, pluginConfig);
        server.getPluginManager().registerEvents(listener, this);

        logger.info("blasting-furnace plugin enabled...");

        getConfig().options().copyDefaults(true);
        saveConfig();

        super.onEnable();
    }

    @Override
    public void onDisable() {
        getLogger().info("blasting-furnace plugin disabled...");
        super.onDisable();
    }
}
