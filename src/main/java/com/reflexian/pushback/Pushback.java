package com.reflexian.pushback;

import com.reflexian.pushback.Events.Playercheck;
import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.Scanner;

public final class Pushback extends JavaPlugin implements Listener {

    FileConfiguration config = getConfig();
    public Object MetricsLite;

    @Override
    public void onEnable() {
        int pluginId = 8303;
        MetricsLite = new MetricsLite(this, pluginId);
        getCommand("pushback").setExecutor(new Playercheck(this));
        getServer().getPluginManager().registerEvents(new Playercheck(this), this);
        this.saveDefaultConfig();
        getLogger().info("--------------------------");
        getLogger().info("Loading PushBack Plugin");
        getLogger().info("--------------------------");
        getLogger().info("Loaded PushBack Plugin");
        getLogger().info("Made by Raymond#0001");
        getLogger().info(" ");
        getLogger().info("Problems? Contact me");
        getLogger().info("on discord: Raymond#0001");
        getLogger().info("--------------------------");
        URL url = null;
        try {
            url = new URL("https://pastebin.com/raw/r60S8YDv");
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
        Scanner scanner = null;
        try {
            scanner = new Scanner(url.openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        boolean activateplugin = Boolean.parseBoolean(scanner.nextLine());
        String pluginversion = scanner.nextLine();
        boolean checkforupdate = getConfig().getBoolean("check-for-update");
        String link = scanner.nextLine();
        scanner.close();
        String configversion = getConfig().getString("config-version");
        if (!activateplugin) {
            getLogger().info("================== DISABLED ==================");
            getLogger().info("This plugin has lost support and has been disabled");
            getLogger().info("Contact Raymond#0001 for support.");
            getLogger().info("================== DISABLED ==================");
            Bukkit.getServer().getPluginManager().disablePlugin(this);
        } else {
            if (checkforupdate) {
                if (!configversion.equals(pluginversion)) {
                    getLogger().info("================== WARNING ==================");
                    getLogger().info("You do not have the most updated version of PushBack!");
                    getLogger().info("Download at: " + link);
                    getLogger().info("================== WARNING ==================");
                }
            }
        }
    }
 
    @Override
    public void onDisable() {
    }


}
