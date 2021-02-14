package com.reflexian.pushback;

import com.reflexian.pushback.events.PushAway;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.event.Listener;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.IOException;
import java.net.URL;
import java.util.Scanner;

public final class PushBack extends JavaPlugin implements Listener {

    private Object MetricsLite;

    private static PushBack plugin;

    @Override
    public void onEnable() {
        plugin=this;
        registerVariables();
        int pluginId = 8303;
        MetricsLite = new MetricsLite(this, pluginId);
        getCommand("pushback").setExecutor(new PushAway());
        getServer().getPluginManager().registerEvents(new PushAway(), this);
        this.saveDefaultConfig();

        getLogger().info("--------------------------");
        getLogger().info("Loaded PushBack Plugin");
        getLogger().info("Made by Raymond#0001");
        getLogger().info(" ");
        getLogger().info("Problems? Contact me");
        getLogger().info("on discord: Raymond#0001");
        getLogger().info("--------------------------");

        Scanner scanner = null;
        try {
            scanner = new Scanner(new URL("https://api.spigotmc.org/legacy/update.php?resource=81904").openStream());
        } catch (IOException e) {
            e.printStackTrace();
        }
        String streamVersion = scanner.nextLine();
        scanner.close();

        if (!streamVersion.equals(getDescription().getVersion())) {
            getLogger().info("================== WARNING ==================");
            getLogger().info("You do not have the most updated version of PushBack!");
            getLogger().info("https://www.spigotmc.org/resources/pushback-push-away-the-nons-with-style.81904/");
            getLogger().info("================== WARNING ==================");
        }
    }

    public static PushBack getInstance() {
        return plugin;
    }

    @Override
    public void onDisable() {
    }


    public static boolean CHECK_FOR_UPDATES, ENABLE_FALL_DAMAGE;
    public static int PUSH_BACK_LENGTH;
    public static String PERMISSION, RELOAD_PERMISSION, MULTIPLAYER_PERMISSION, NO_PERMISSION_MESSAGE, ENABLED_MESSAGE, RELOAD_MESSAGE, DISABLED_MESSAGE, EXECUTOR_ENABLED_MESSAGE, EXECUTOR_DISABLED_MESSAGE;

    private static void registerVariables () {
        FileConfiguration f = PushBack.plugin.getConfig();

        CHECK_FOR_UPDATES = f.getBoolean("check-for-update");
        ENABLE_FALL_DAMAGE = f.getBoolean("enable-fall-damage");
        PUSH_BACK_LENGTH = f.getInt("push-back-length");
        PERMISSION = f.getString("permission");
        RELOAD_PERMISSION = f.getString("reload-permission");
        RELOAD_MESSAGE = f.getString("reload-message");
        MULTIPLAYER_PERMISSION = f.getString("multiplayer-permission");
        NO_PERMISSION_MESSAGE = f.getString("no-permission");
        ENABLED_MESSAGE = f.getString("enabled-message");
        DISABLED_MESSAGE = f.getString("disabled-message");
        EXECUTOR_ENABLED_MESSAGE = f.getString("executor-enabled-message");
        EXECUTOR_DISABLED_MESSAGE = f.getString("executor-disabled-message");
    }


}
