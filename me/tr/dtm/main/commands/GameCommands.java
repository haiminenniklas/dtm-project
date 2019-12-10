package me.tr.dtm.main.commands;

import me.tr.dtm.main.Messages;
import me.tr.dtm.main.game.Game;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class GameCommands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {

        if(sender instanceof Player) {

            Player player = (Player) sender;

            if(command.getLabel().equalsIgnoreCase("join")) {

                Game.join(player);

            } else if(command.getLabel().equalsIgnoreCase("spec")) {

                Game.spectate(player);

            } else if(command.getLabel().equalsIgnoreCase("leave")) {

                Game.leave(player);

            }
        } else {
            Messages.send(sender, "command-for-players");
        }

        return false;
    }
}
