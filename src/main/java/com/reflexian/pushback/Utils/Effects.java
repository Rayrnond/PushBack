package com.reflexian.pushback.Utils;

import org.bukkit.Effect;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class Effects {

    public Boolean PlayEffect(Player player, Effect effect) {

        if (effect == null) {
            return false;
        } else {
            player.playEffect(player.getLocation(), effect, null);
            return true;
        }

    }

}
