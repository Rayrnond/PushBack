package com.reflexian.pushback.events;

import com.reflexian.pushback.PushBack;
import org.bukkit.*;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.util.Vector;

import java.util.*;

public class PushAway implements Listener, CommandExecutor {

    private final static List<UUID> TOGGLE_LIST = new ArrayList<>();
    private final static List<UUID> ANTI_DAMAGE = new ArrayList<>();
    private final PushBack main = PushBack.getInstance();

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

        if (sender instanceof ConsoleCommandSender) {
            if (args.length == 0) {
                main.getLogger().warning("That is not a valid argument! Try (reload,updatelink,<player name>)!");
                return true;
            } else if (args[0].equalsIgnoreCase("reload")) {
                main.reloadConfig();
                main.getLogger().info(colorize(PushBack.RELOAD_MESSAGE));
                return true;
            } else if (args.length == 1) {
                Player player = Bukkit.getPlayerExact(args[0]);

                if (player == null) {
                    main.getLogger().warning("That is not a valid player or argument!");
                } else {
                    if (!TOGGLE_LIST.contains(player.getUniqueId())) {
                        TOGGLE_LIST.add(player.getUniqueId());
                        player.sendMessage(colorize(PushBack.ENABLED_MESSAGE));
                        main.getLogger().info((PushBack.EXECUTOR_ENABLED_MESSAGE.replace("%player%", player.getDisplayName()).replace("&", "")));
                    } else {
                        TOGGLE_LIST.remove(player.getUniqueId());
                        player.sendMessage(colorize(PushBack.DISABLED_MESSAGE));
                        main.getLogger().info((PushBack.EXECUTOR_DISABLED_MESSAGE.replace("%player%", player.getDisplayName()).replace("&", "")));
                    }
                }
                return true;

            }
            return true;
        }

        Player player = (Player) sender;

        if (!player.hasPermission(PushBack.PERMISSION)) {
            player.sendMessage(colorize(PushBack.NO_PERMISSION_MESSAGE));
            return true;
        }
        if (args.length == 0) {
            if (!TOGGLE_LIST.contains(player.getUniqueId())) {
                TOGGLE_LIST.add(player.getUniqueId());
                player.sendMessage(colorize(PushBack.ENABLED_MESSAGE));
            } else {
                TOGGLE_LIST.remove(player.getUniqueId());
                player.sendMessage(colorize(PushBack.DISABLED_MESSAGE));
            }
            return true;
        } else if (args[0].equalsIgnoreCase("reload")) {
            if (!player.hasPermission(PushBack.RELOAD_PERMISSION)) {
                player.sendMessage(colorize(PushBack.NO_PERMISSION_MESSAGE));
                return true;
            }
            player.sendMessage(colorize(colorize(PushBack.RELOAD_MESSAGE)));
            main.reloadConfig();
            return true;
        } else {
            Player target = Bukkit.getPlayerExact(args[0]);
            if (target == null) {
                player.sendMessage(ChatColor.RED + "That is not a valid argument or player.");
            } else {
                if (!TOGGLE_LIST.contains(target.getUniqueId())) {
                    TOGGLE_LIST.add(target.getUniqueId());
                    target.sendMessage(colorize(PushBack.ENABLED_MESSAGE));
                    player.sendMessage(colorize(PushBack.EXECUTOR_ENABLED_MESSAGE.replace("%player%", target.getDisplayName())));
                } else {
                    TOGGLE_LIST.remove(target.getUniqueId());
                    target.sendMessage(colorize(PushBack.DISABLED_MESSAGE));
                    player.sendMessage(colorize(PushBack.EXECUTOR_DISABLED_MESSAGE.replace("%player%", target.getDisplayName())));
                }
            }
            return true;

        }
    }

    @EventHandler
    public void isPlayerCheck(PlayerMoveEvent event) {
        if (!TOGGLE_LIST.contains(event.getPlayer().getUniqueId())) return;
        Player player = event.getPlayer();
        int range = PushBack.PUSH_BACK_LENGTH;

        PlayerPushedEvent p = new PlayerPushedEvent(player, (List<Player>) player.getNearbyEntities(range,range,range).stream().filter(entity -> (entity instanceof Player) && (!entity.hasMetadata("NPC"))));
        Bukkit.getPluginManager().callEvent(p);
        if (p.isCancelled()) return;

        player.getNearbyEntities(range,range,range).stream().filter(entity -> (entity instanceof Player) && (!entity.hasMetadata("NPC"))).forEach(entity -> {
            Vector v = entity.getLocation().getDirection().multiply(-0.83).setY(1);
            entity.setVelocity(v);
            ANTI_DAMAGE.add(entity.getUniqueId());
        });
    }

    @EventHandler
    public void notPlayerCheck(PlayerMoveEvent event) {
        int range = PushBack.PUSH_BACK_LENGTH;
        Player player = event.getPlayer();

        PlayerPushedEvent p = new PlayerPushedEvent(player, (List<Player>) player.getNearbyEntities(range,range,range).stream().filter(entity -> (entity instanceof Player) && (!entity.hasMetadata("NPC"))));
        Bukkit.getPluginManager().callEvent(p);
        if (p.isCancelled()) return;

        player.getNearbyEntities(range,range,range).stream().filter(entity -> (entity instanceof Player) && (!entity.hasMetadata("NPC"))).forEach(entity -> {
            if (TOGGLE_LIST.contains(entity.getUniqueId())) {
                Vector v = player.getLocation().getDirection().multiply(-0.83).setY(1);
                player.setVelocity(v);
                if (!ANTI_DAMAGE.contains(player.getUniqueId())) {
                    ANTI_DAMAGE.add(player.getUniqueId());
                }
            }
        });
    }

    private final boolean FALL_DAMAGE = PushBack.ENABLE_FALL_DAMAGE;
    @EventHandler
    public void onPlayerFall(EntityDamageEvent e) {
        if (!FALL_DAMAGE) return;
        if(e.getEntity() instanceof Player && e.getCause() == EntityDamageEvent.DamageCause.FALL) {
            ANTI_DAMAGE.remove(e.getEntity().getUniqueId());
            e.setCancelled(true);
        }
    }


}