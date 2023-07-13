package fr.mana.terrabank.commands;

import fr.mana.terrabank.*;
import org.bukkit.command.*;

public class Reload implements CommandExecutor {
    private TerraBank main;
    public Reload(TerraBank main) {
        this.main = main;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if(args.length > 0){
            if(args.length == 1){
                if(args[0].equalsIgnoreCase("reload")){
                    main.reloadConfig();
                    sender.sendMessage("§6§lTerraBank configuration reloaded !");
                }
            }
        }
        return false;
    }
}
