package fr.mana.terrabank.events;

import fr.mana.terrabank.TerraBank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jdbi.v3.core.Jdbi;

import java.math.BigDecimal;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.HashMap;
import java.util.Objects;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class CollectBanknote implements Listener {
    private final TerraBank main;
    private String keyWord;
    private String material;
    private String currencySymbol;
    private final Pattern amountPattern;
    private HashMap<BigDecimal, Integer> banknotes;

    public CollectBanknote(TerraBank main) {
        this.main = main;
        this.keyWord = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.getConfig().getString("bankNote.keyword")));
        this.material = main.getConfig().getString("bankNote.item");
        this.currencySymbol = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.getConfig().getString("bankNote.currencySymbol")));
        this.amountPattern = Pattern.compile(Pattern.quote(currencySymbol) + "(\\d+(\\.\\d{1,2})?)");
        this.banknotes = new HashMap<>();
    }

    @EventHandler
    public void onPlayerInteract(PlayerInteractEvent event) {
        Player player = event.getPlayer();
        ItemStack item = event.getItem();

        if (item != null && item.getType() == Material.valueOf(material)) {
            ItemMeta meta = item.getItemMeta();

            if (meta != null && meta.hasDisplayName()) {
                String displayName = meta.getDisplayName();

                if (displayName.contains(keyWord) && displayName.contains(currencySymbol)) {
                    Matcher matcher = amountPattern.matcher(displayName);

                    if (matcher.find()) {
                        String amountString = matcher.group(1);
                        BigDecimal amount = new BigDecimal(amountString);

                        // Vérifier si un billet de même valeur existe déjà dans la banque de l'utilisateur
                        int amountToAdd = item.getAmount();
                        if (banknotes.containsKey(amount)) {
                            int existingAmount = banknotes.get(amount);
                            amountToAdd += existingAmount;
                        }
                        banknotes.put(amount, amountToAdd);

                        addMoney(player, amount.multiply(BigDecimal.valueOf(item.getAmount())));
                        player.sendMessage(ChatColor.GREEN + "Vous avez collecté " + amount + currencySymbol + "!");
                        event.setCancelled(true);

                        // Retirer le billet original de l'inventaire du joueur
                        item.setAmount(0);
                        player.getInventory().remove(item);

                        // Donner le nouveau billet avec la somme totale
                        ItemStack newBanknote = new ItemStack(Material.valueOf(material));
                        newBanknote.setAmount(amountToAdd);
                        ItemMeta stackedMeta = newBanknote.getItemMeta();
                        String stackedDisplayName = keyWord + currencySymbol + (amount.multiply(BigDecimal.valueOf(amountToAdd)));
                        stackedMeta.setDisplayName(stackedDisplayName);
                        newBanknote.setItemMeta(stackedMeta);
                        player.getInventory().addItem(newBanknote);
                    }
                }
            }
        }
    }

    public void addMoney(Player player, BigDecimal amount) {
        try (Connection connection = getConnection();
             PreparedStatement statement = connection.prepareStatement("UPDATE MoneyTable SET money = money + ? WHERE name = ?")) {
            statement.setBigDecimal(1, amount);
            statement.setString(2, player.getName());
            statement.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public Connection getConnection() throws SQLException {
        String host = main.getConfig().getString("database.host");
        int port = main.getConfig().getInt("database.port");
        String database = main.getConfig().getString("database.name");

        String jdbcUrl = "jdbc:mysql://" + host + ":" + port + "/" + database;

        String username = main.getConfig().getString("database.user");
        String password = main.getConfig().getString("database.password");

        assert username != null;
        assert password != null;

        Jdbi jdbi = Jdbi.create(jdbcUrl, username, password);

        return DriverManager.getConnection(jdbcUrl, username, password);
    }
}
