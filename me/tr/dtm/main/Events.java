package me.tr.dtm.main;

import me.tr.dtm.main.database.PlayerData;
import me.tr.dtm.main.other.gui.Button;
import me.tr.dtm.main.other.gui.Gui;
import org.bukkit.ChatColor;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.inventory.InventoryCloseEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.AsyncPlayerPreLoginEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import java.util.UUID;

public class Events implements Listener {

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onAsyncLogin(AsyncPlayerPreLoginEvent e) {
        PlayerData.loadPlayer(e.getUniqueId());
    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();

        FileConfiguration config = Main.getInstance().getConfig();

        // Join message

        if (Main.getInstance().getConfig().getBoolean("connection-messages.server-join.enabled")) {

            String join_msg = Main.getInstance().getConfig().getString("connection-messages.server-join.message");
            join_msg = Messages.playerPlaceholders(player, join_msg);
            join_msg = Messages.serverPlaceholders(join_msg);

            e.setJoinMessage(ChatColor.translateAlternateColorCodes('&', join_msg));

        }

        // Message of Today

        if(config.getBoolean("message-of-today.enabled")) {

            for(String s : config.getStringList("message-of-today.motd")) {

                s = Messages.playerPlaceholders(player, s);
                s = Messages.serverPlaceholders(s);

                player.sendMessage(ChatColor.translateAlternateColorCodes('&', s));

            }
        }

        Messages.giveScoreboard(player);

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onQuit(PlayerQuitEvent e) {

        Player player = e.getPlayer();

        // Quit message

        if (Main.getInstance().getConfig().getBoolean("connection-messages.server-quit.enabled")) {

            String quit_msg = Main.getInstance().getConfig().getString("connection-messages.server-quit.message");
            quit_msg = Messages.playerPlaceholders(player, quit_msg);
            quit_msg = Messages.serverPlaceholders(quit_msg);

            e.setQuitMessage(ChatColor.translateAlternateColorCodes('&', quit_msg));

        }

        // Save the player's data
        DTM.async(() -> {
            PlayerData.savePlayer(player.getUniqueId());
        });

    }

    @EventHandler
    public void onChat(AsyncPlayerChatEvent e) {

        Player player = e.getPlayer();

        if(DTM.getConfig().getBoolean("chat-format.enabled")) {

            String message = e.getMessage();
            String rawFormat = DTM.getConfig().getString("chat-format.format");

            String format = Messages.playerPlaceholders(player, rawFormat);
            format = Messages.serverPlaceholders(format);

            if(DTM.getConfig().getBoolean("chat-format.colors.enabled")) {
                if(DTM.getConfig().getBoolean("chat-format.colors.only-for-permission-holders")) {
                    if(player.hasPermission("dtm.chat.color")) {
                        message = ChatColor.translateAlternateColorCodes('&', e.getMessage());
                    }
                } else {
                    message = ChatColor.translateAlternateColorCodes('&', e.getMessage());
                }
            }


            format = format.replaceAll("%message%", message);
            e.setFormat(format);

        }


    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInvClick(InventoryClickEvent e) {
        if(e.getClickedInventory() == null) return;
        if(e.getCurrentItem() == null) return;

        if(e.getView().getTitle().contains("§r")) e.setCancelled(true);

        Player player = (Player) e.getWhoClicked();

        if(!player.isOp()) e.setCancelled(true);

        if(Gui.getGui(player) != null) {
            Gui gui = Gui.getGui(player);
            if(e.getCurrentItem() != null) {

                for(Button b : gui.getButtons()) {
                    if(b.item.clone().equals(e.getCurrentItem())) {
                        b.onClick(player, e.getClick());
                    }
                }
            }

        }

    }

    @EventHandler(priority = EventPriority.HIGHEST)
    public void onInvClose(InventoryCloseEvent e) {

        Player player = (Player) e.getPlayer();
        if(e.getView().getTitle().contains("§r") && Gui.getGui(player) != null) {
            Gui.getGui(player).close(player);
        }

    }

}
