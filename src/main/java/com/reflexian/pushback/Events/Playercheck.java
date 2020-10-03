package com.reflexian.pushback.Events;

import com.reflexian.pushback.Utils.Effects;
import com.reflexian.pushback.Utils.Sounds;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerInteractEvent;
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
                main.getLogger().warning("That is not a valid argument! Try (reload,updatelink,<player name>)!");
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                main.reloadConfig();
                main.getLogger().info(colorize(Objects.requireNonNull(main.getConfig().getString("reload-message"))));
                return true;
            } else if (args[0].equalsIgnoreCase("updatelink")) {
                System.out.println("Update Link: XXX");
            } else if (args.length == 1) {
                Player player = Bukkit.getPlayerExact(args[0]);

                if (player == null) {
                    main.getLogger().warning("That is not a valid player or argument!");
                    return true;
                } else {
                    if (!toggleList.contains(player.getUniqueId())) {
                        toggleList.add(player.getUniqueId());
                        player.sendMessage(colorize(Objects.requireNonNull(main.getConfig().getString("enabled-message"))));
                        main.getLogger().info((main.getConfig().getString("executor-enabled-message").replace("%player%", player.getDisplayName()).replace("&", "")));
                        return true;
                    } else {
                        toggleList.remove(player.getUniqueId());
                        player.sendMessage(colorize(Objects.requireNonNull(main.getConfig().getString("disabled-message"))));
                        main.getLogger().info((main.getConfig().getString("executor-disabled-message").replace("%player%", player.getDisplayName()).replace("&", "")));
                        return true;
                    }
                }

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
            if (!toggleList.contains(player.getUniqueId())) {
                toggleList.add(player.getUniqueId());
                player.sendMessage(colorize(main.getConfig().getString("enabled-message")));
                return true;
            } else {
                toggleList.remove(player.getUniqueId());
                player.sendMessage(colorize(main.getConfig().getString("disabled-message")));
                return true;
            }
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission(main.getConfig().getString("reload-permission"))) {
                player.sendMessage(colorize(main.getConfig().getString("no-permission")));
                return true;
            }
            player.sendMessage(colorize(colorize(main.getConfig().getString("reload-message"))));
            main.reloadConfig();
            return true;
        } else {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "That is not a valid argument or player.");
                return true;
            } else {
                if (!toggleList.contains(target.getUniqueId())) {
                    toggleList.add(target.getUniqueId());
                    target.sendMessage(colorize(main.getConfig().getString("enabled-message")));
                    player.sendMessage(colorize(main.getConfig().getString("executor-enabled-message").replace("%player%", target.getDisplayName())));
                    return true;
                } else {
                    toggleList.remove(target.getUniqueId());
                    target.sendMessage(colorize(main.getConfig().getString("disabled-message")));
                    player.sendMessage(colorize(main.getConfig().getString("executor-disabled-message").replace("%player%", target.getDisplayName())));
                    return true;
                }
            }

        }
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
                    if (main.getConfig().getBoolean("sounds-enabled")) {
                        new Sounds().PlaySound(event.getPlayer(), Sound.valueOf(main.getConfig().getString("sound-effect-2")));
                    }
                    if (main.getConfig().getBoolean("particles-enabled")) {
                        new Effects().PlayEffect(((Player) en).getPlayer(), Effect.valueOf(main.getConfig().getString("particle")));
                    }
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
                    if (main.getConfig().getBoolean("sounds-enabled")) {
                        new Sounds().PlaySound(player, Sound.valueOf(main.getConfig().getString("sound-effect")));
                    }
                    if (main.getConfig().getBoolean("particles-enabled")) {
                        new Effects().PlayEffect(player, Effect.valueOf(main.getConfig().getString("particle")));
                    }
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