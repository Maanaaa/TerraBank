package fr.mana.terrabank.commands.bank;

import fr.mana.terrabank.*;
import org.bukkit.*;
import org.bukkit.command.*;
import org.bukkit.entity.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

public class GiveBanknote implements CommandExecutor {
    private TerraBank main;
    public GiveBanknote(TerraBank main) {
        this.main = main;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        // /terrabank give(args[0]) <player>(args[1]) <amount>(args[2])
        if(args.length > 0){
            if(args.length == 3){
                if(args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("g")){
                    Player target = Bukkit.getPlayer(args[1]);
                    if(target != null){ // If targeted player is online
                        // Check if arg 2 is a number
                        double amount;
                        try {
                            amount = Double.parseDouble(args[2]);
                        } catch (NumberFormatException e) { // Amount is invalid
                            sender.sendMessage(Objects.requireNonNull(Objects.requireNonNull(main.getConfig().getString("messages.giveCommand.invalidAmount")).
                                    replace("&","§")
                                    .replace("%amount%",args[2])));
                            return true;
                        }
                        // Amount is valid

                        //--------
                        // Register bankNote item
                        // -------

                        ItemStack bankNote = new ItemStack(Material.valueOf(main.getConfig().getString("bankNote.item")));
                        ItemMeta bankNoteMeta = bankNote.getItemMeta();

                        // Configure item
                        assert bankNoteMeta != null;

                        bankNoteMeta.setDisplayName(Objects.requireNonNull(main.getConfig().getString("bankNote.display-name"))
                                .replace("&","§")
                                .replace("%value%",String.valueOf(amount)
                                ));


                        List<String> description = main.getConfig().getStringList("bankNote.description");
                        List<String> lore = new ArrayList<>();
                        for (String line : description) {
                            lore.add(line);
                        }

                        // Set item description
                        bankNoteMeta.setLore(lore);
                        // Assing item configuration to banknote item
                        bankNote.setItemMeta(bankNoteMeta);
                        sender.sendMessage(Objects.requireNonNull(main.getConfig().getString("messages.giveCommand.success"))
                                .replace("&","§")
                                .replace("%amount%",String.valueOf(amount)
                                        .replace("%player%",target.getDisplayName())));
                        target.getInventory().addItem(bankNote);
                    }
                }
            }
        }
        return false;
    }
}