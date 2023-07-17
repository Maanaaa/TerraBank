package fr.mana.terrabank.commands;

import fr.mana.terrabank.*;
import org.bukkit.Bukkit;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class GiveBanknote implements CommandExecutor {
    private TerraBank main;

    public GiveBanknote(TerraBank main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(args.length == 0){
            List<String> message = main.getConfig().getStringList("messages.helpCommand");
            for (String line : message) {
                sender.sendMessage(line.replace("&","§"));
            };
        }else if(args.length == 1){
            if(args[0].equalsIgnoreCase("") || args[0].equalsIgnoreCase("help")){
                List<String> message = main.getConfig().getStringList("messages.helpCommand");
                for (String line : message) {
                    sender.sendMessage(line.replace("&","§"));
                };
            }else if(args[0].equalsIgnoreCase("reload")){
                main.reloadConfig();
                sender.sendMessage("§6§lTerraBank configuration reloaded !");
            }
        }else if(args.length == 2){
            if(args[1].equalsIgnoreCase("")){
                List<String> message = main.getConfig().getStringList("messages.helpCommand");
                for (String line : message) {
                    sender.sendMessage(line.replace("&","§"));
                };
            }
        }

        else if (args.length == 3 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("g"))) {
            Player target = Bukkit.getPlayer(args[1]);
            if (target != null) {
                double amount;
                try {
                    amount = Double.parseDouble(args[2]);
                } catch (NumberFormatException e) {
                    sender.sendMessage(main.getConfig().getString("messages.giveCommand.invalidAmount")
                            .replace("&", "§")
                            .replace("%amount%", args[2]));
                    return true;
                }

                ItemStack bankNote = new ItemStack(Material.valueOf(main.getConfig().getString("bankNote.item")));
                ItemMeta bankNoteMeta = bankNote.getItemMeta();

                if (bankNoteMeta != null) {
                    bankNoteMeta.setDisplayName(main.getConfig().getString("bankNote.display-name")
                            .replace("&", "§")
                            .replace("%value%", String.valueOf(amount))
                            .replace("%keyword%", Objects.requireNonNull(Objects.requireNonNull(main.getConfig().getString("bankNote.keyword")).replace("&","§")))
                            .replace("%currencySymbol%", Objects.requireNonNull(main.getConfig().getString("bankNote.currencySymbol"))));

                    List<String> description = main.getConfig().getStringList("bankNote.description");
                    List<String> lore = new ArrayList<>();
                    for (String line : description) {
                        lore.add(line.replace("&", "§"));
                    }

                    bankNoteMeta.setLore(lore);
                    bankNote.setItemMeta(bankNoteMeta);

                    sender.sendMessage(main.getConfig().getString("messages.giveCommand.success")
                            .replace("&", "§")
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%player%", target.getName())
                            .replace("%currencySymbol%", Objects.requireNonNull(main.getConfig().getString("bankNote.currencySymbol")))
                            .replace("%keyword%", Objects.requireNonNull(main.getConfig().getString("bankNote.keyword"))));

                    target.getInventory().addItem(bankNote);
                }
                return true;
            }
        }
        return false;
    }
}