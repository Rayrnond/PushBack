package com.reflexian.pushback.Utils;

import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Sounds {

    public Boolean PlaySound(Player player, Sound sound) {

        if (sound == null) {
            return false;
        } else {
            player.playSound(player.getLocation(), sound, 1F, 2F);
            return true;
        }

    }

}
