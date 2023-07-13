package fr.mana.terrabank.events;

import fr.mana.terrabank.*;
import org.bukkit.*;
import org.bukkit.entity.*;
import org.bukkit.event.*;
import org.bukkit.event.player.*;
import org.bukkit.inventory.*;
import org.bukkit.inventory.meta.*;

import java.util.*;

public class CollectBanknote implements Listener {
    private TerraBank main;
    public CollectBanknote(TerraBank main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.hasItem() && Objects.requireNonNull(event.getItem()).getType() == Material.PAPER) {
            ItemStack item = event.getItem();
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasLocalizedName() && meta.hasLore()) {
                String localizedName = meta.getLocalizedName();
                List<String> lore = meta.getLore();
                if (localizedName.replace("&", "§").startsWith("§9Billet de ") && lore != null && lore.get(0).equals("§7Cliquez pour réclamer le montant !")) {
                    // Récupérer la valeur du montant à partir du localizedName
                    String amountString = localizedName.substring(13);
                    double amount = Double.parseDouble(amountString);

                    // Vérifier si le montant est valide
                    if (Double.isNaN(amount)) {
                        player.sendMessage("§cErreur, montant invalide !");
                        return;
                    }

                    // Ajouter ici le code pour donner l'argent au joueur et retirer 1 papier
                    // Utilisez les valeurs de votre fichier de configuration pour effectuer ces actions

                    // Exemple :
                    String successMessage = main.getConfig().getString("messages.collected")
                            .replace("&", "§")
                            .replace("%amount%", String.valueOf(amount))
                            .replace("%player%", player.getName());
                    player.sendMessage(successMessage);

                    item.setAmount(item.getAmount() - 1);
                    event.setCancelled(true);
                }
            }
        }
    }

}
