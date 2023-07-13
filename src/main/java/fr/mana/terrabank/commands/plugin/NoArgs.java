package fr.mana.terrabank.commands.plugin;

import fr.mana.terrabank.*;
import org.bukkit.command.*;

import java.util.*;

public class NoArgs implements CommandExecutor {
    private TerraBank main;
    public NoArgs(TerraBank main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // Player just make /terrabank command
        if(args.length == 0){
            List<String> message = main.getConfig().getStringList("messages.helpCommand");
            for (String line : message) {
                sender.sendMessage(line.replace("&","§"));
            };
        // Player make /terrabank help command
        }else if(args.length == 1){
            if(args[0].equalsIgnoreCase("help")){
                List<String> message = main.getConfig().getStringList("messages.helpCommand");
                for (String line : message) {
                    sender.sendMessage(line.replace("&","§"));
                };
            }
        }
        return false;
    }
}
