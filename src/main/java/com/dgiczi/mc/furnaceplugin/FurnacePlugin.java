package com.dgiczi.mc.furnaceplugin;

import org.bukkit.Server;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.logging.Logger;

public class FurnacePlugin extends JavaPlugin {

    @Override
    public void onEnable() {
        final Logger logger = getLogger();
        final Server server = getServer();
        final FurnaceExplosionListener listener = new FurnaceExplosionListener(this);
        server.getPluginManager().registerEvents(listener, this);

        logger.info("furnace-plugin enabled...");

        super.onEnable();
    }

    @Override
    public void onDisable() {
        getLogger().info("furnace-plugin disabled...");
        super.onDisable();
    }
}
