package fr.mana.terrabank.events;

import fr.mana.terrabank.TerraBank;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;

import java.util.List;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectBanknote implements Listener {
    private final TerraBank main;
    private final Pattern amountPattern = Pattern.compile("\\[(\\d+)]"); // Recherche le montant entre crochets

    public CollectBanknote(TerraBank main) {
        this.main = main;
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        if (event.hasItem() && Objects.requireNonNull(event.getItem()).getType() == Material.PAPER) {

            ItemStack item = event.getItem();
            ItemMeta meta = item.getItemMeta();
            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();
                Matcher matcher = amountPattern.matcher(displayName);
                if (matcher.find()) {
                    String amountString = matcher.group(1);
                    double amount = Double.parseDouble(amountString);

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
