package com.reflexian.pushback.Events;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class Playercheck implements Listener, CommandExecutor {

    public static List<UUID> toggleList = new ArrayList<UUID>();
    public static List<UUID> antiDamage = new ArrayList<UUID>();

    public com.reflexian.pushback.Pushback main;

    public Playercheck(com.reflexian.pushback.Pushback main) {
        this.main = main;
    }

    public String colorize(String msg) {
        String coloredMsg = "";
        for (int i = 0; i < msg.length(); i++) {
            if(msg.charAt(i) == '&') {
                coloredMsg += '§';
            } else {
                coloredMsg += msg.charAt(i);
            }
        }
        return coloredMsg;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String alias, String[] args) {

        String nopermissionmessage = main.getConfig().getString("no-permission");
        if (sender instanceof ConsoleCommandSender) {
            if (args.length == 0) {
                System.out.println("You are console! Console is not a player and cannot execute /pushback! Console can only '/pushback reload' and '/pushback updatelink'");
            } else if (args[0].equalsIgnoreCase("reload")) {
                main.reloadConfig();
                System.out.println("You have successfully reload the PushBack configuration files!");
            } else if (args[0].equalsIgnoreCase("updatelink")) {
                System.out.println("Update Link: XXX");
            } else {
                System.out.println("That is not a valid command for console to execute!");
            }
            return true;
        }

        Player player = (Player) sender;
        String permissionneeded = main.getConfig().getString("permission");
        if (!player.hasPermission(permissionneeded)) {
            player.sendMessage(colorize(nopermissionmessage));
            return true;
        }
        if (args.length == 0) {
            UUID ID = player.getUniqueId();
            if (!toggleList.contains(player.getUniqueId())) {
                toggleList.add(player.getUniqueId());
                String enabledpushback = main.getConfig().getString("enabled-message");
                player.sendMessage(colorize(enabledpushback));
                return true;
            } else {
                toggleList.remove(player.getUniqueId());
                String disabledpushback = main.getConfig().getString("disabled-message");
                player.sendMessage(colorize(disabledpushback));
                return true;
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            String reloadpermission = main.getConfig().getString("reload-permission");
            if (!player.hasPermission(reloadpermission)) {
                player.sendMessage(colorize(reloadpermission));
                return true;
            }
            player.sendMessage(ChatColor.GREEN + "You have successfully reloaded the configuration files!");
            main.reloadConfig();
        } else {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "That is not a valid argument or player.");
                return true;
            } else {
                if (!toggleList.contains(target.getUniqueId())) {
                    toggleList.add(target.getUniqueId());
                    String enabledpushback = main.getConfig().getString("enabled-message");
                    String executorenabledpushback = main.getConfig().getString("executor-enabled-message");
                    executorenabledpushback = executorenabledpushback.replace("%player%", target.getDisplayName());
                    target.sendMessage(colorize(enabledpushback));
                    player.sendMessage(colorize(executorenabledpushback));
                    return true;
                } else {
                    toggleList.remove(target.getUniqueId());
                    String disabledpushback = main.getConfig().getString("disabled-message");
                    String executordisabledpushback = main.getConfig().getString("executor-disabled-message");
                    executordisabledpushback = executordisabledpushback.replace("%player%", target.getDisplayName());
                    target.sendMessage(colorize(disabledpushback));
                    player.sendMessage(colorize(executordisabledpushback));
                    return true;
                }
            }

        }
        return true;
    }

    @EventHandler
    public void isPlayerCheck(PlayerMoveEvent event) {
        if (!toggleList.contains(event.getPlayer().getUniqueId())) {
            return;
        } else {
            Player player = event.getPlayer();
            double range = main.getConfig().getDouble("push-back-length");
            for (Entity en : player.getNearbyEntities(range, range, range)) {
                if (en.hasMetadata("NPC")) {
                    return;
                }
                if ((en instanceof Player)) {
                    Vector v = en.getLocation().getDirection().multiply(-0.83).setY(1);
                    en.setVelocity(v);
                    if (antiDamage.contains(en.getUniqueId())) {
                        return;
                    } else {
                        antiDamage.add(en.getUniqueId());
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void notPlayerCheck(PlayerMoveEvent event) {
        double range = main.getConfig().getDouble("push-back-length");
        Player player = event.getPlayer();
        for (Entity e : player.getNearbyEntities(range, range, range)) {
            if (e.hasMetadata("NPC")) {
                return;
            }
            if ((e instanceof Player)) {
                if (!toggleList.contains(e.getUniqueId())) {
                    return;
                } else {
                    UUID ID = e.getUniqueId();
                    Vector v = player.getLocation().getDirection().multiply(-0.83).setY(1);
                    player.setVelocity(v);
                    if (antiDamage.contains(player.getUniqueId())) {
                        return;
                    } else {
                        antiDamage.add(player.getUniqueId());
                        return;
                    }
                }
            }
        }
    }

    @EventHandler
    public void onPlayerFall(EntityDamageEvent e) {
        Boolean enablefalldamage = main.getConfig().getBoolean("enable-fall-damage");
        if (!enablefalldamage) {
            return;
        }
        if(e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            if (!antiDamage.contains(e.getEntity().getUniqueId())) {
                return;
            } else {
                antiDamage.remove(e.getEntity().getUniqueId());
                e.setCancelled(true);
            }
        }
    }
}