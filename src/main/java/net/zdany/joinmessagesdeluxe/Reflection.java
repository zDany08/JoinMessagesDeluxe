package net.zdany.joinmessagesdeluxe;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import java.util.logging.Level;

public class Reflection {

    public static Class<?> getNMSClass(String name) {
        try {
            return Class.forName("net.minecraft.server." + JoinMessagesDeluxe.getInstance().getStrVersion() + "." + name);
        }catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while getting the NMS class \"" + name + "\": " + e);
            return null;
        }
    }

    public static void sendPacket(Player p, Object packet) {
        try {
            Object handle = p.getClass().getMethod("getHandle").invoke(p);
            Object playerConnection = handle.getClass().getField("playerConnection").get(handle);
            playerConnection.getClass().getMethod("sendPacket", getNMSClass("Packet")).invoke(playerConnection, packet);
        }catch(Exception e) {
            Bukkit.getLogger().log(Level.SEVERE, "Error while sending a packet to \"" + p.getName() + "\": " + e);
        }
    }
}
