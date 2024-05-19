package fr.mana.terrabank.commands;

import fr.mana.terrabank.*;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.entity.Player;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.*;

public class TerraBankCommand implements CommandExecutor {
    private TerraBank main;

    public TerraBankCommand(TerraBank main) {
        this.main = main;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {

        if(args.length == 0){
            if(sender.hasPermission("terrabank.use")){
                List<String> message = main.getConfig().getStringList("messages.helpCommand");
                for (String line : message) {
                    sender.sendMessage(line.replace("&","§"));
                };
            }

        }if(args.length == 1){
            if(args[0].equalsIgnoreCase("") || args[0].equalsIgnoreCase("help")){
                if (sender.hasPermission("terrabank.use")){
                    List<String> message = main.getConfig().getStringList("messages.helpCommand");
                    for (String line : message) {
                        sender.sendMessage(line.replace("&","§"));
                    };
                }

            }else if(args[0].equalsIgnoreCase("reload")) {
                if (sender.hasPermission("terrabank.use")){
                    main.reloadConfig();
                    sender.sendMessage("§6§lTerraBank configuration reloaded !");
                }

            }
            else if (args[0].equalsIgnoreCase("withdraw")){
                Player player = (Player) sender;

                Inventory inventory = Bukkit.createInventory(null, main.getConfig().getInt("withdraw.menu.rows")*9, ChatColor.translateAlternateColorCodes('&',
                        Objects.requireNonNull(main.getConfig().getString("withdraw.menu.title"))));
                registerItems(player, inventory);
                player.openInventory(inventory);
            }
        }if(args.length == 2){
            if(args[1].equalsIgnoreCase("")){
                if (sender.hasPermission("terrabank.use")){
                    List<String> message = main.getConfig().getStringList("messages.helpCommand");
                    for (String line : message) {
                        sender.sendMessage(line.replace("&","§"));
                    };
                }

            }
        }

        else if (args.length == 3 && (args[0].equalsIgnoreCase("give") || args[0].equalsIgnoreCase("g"))) {
            if (sender.hasPermission("terrabank.use")){
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

        }
        return false;
    }

    public void newItem(Player player, Inventory inventory, String material, String name, List<String> description, List<Integer> slots){
        ItemStack item;
        item = new ItemStack(Material.valueOf(material));

        ItemMeta itemMeta;
        itemMeta = item.getItemMeta();

        assert itemMeta != null;

        itemMeta.setDisplayName(name);

        List<String> replacedDescription = new ArrayList<>();
        for (String line : description){
            line = line.replace("&","§");
            line = line.replace("%player%", player.getDisplayName());
            replacedDescription.add(line);
        }
        itemMeta.setLore(replacedDescription);
        item.setItemMeta(itemMeta);

        for (int slot : slots){
            inventory.setItem(slot, item);
        }
    }

    public void registerItems(Player player, Inventory inventory){
        ConfigurationSection itemsSection = main.getConfig().getConfigurationSection("withdraw.menu.items");

        assert itemsSection != null;
        for (String key : itemsSection.getKeys(false)){
            String material = main.getConfig().getString("withdraw.menu.items."+key+".material");
            String name = main.getConfig().getString("withdraw.menu.items."+key+".name").replace("&","§");
            List<String> description = main.getConfig().getStringList("withdraw.menu.items."+key+".lore");
            List<Integer> slots = main.getConfig().getIntegerList("withdraw.menu.items."+key+".slot");

            newItem(player, inventory, material, name, description, slots);
        }

    }
}