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
    private HashMap<String, BigDecimal> banknotes;

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

                        addMoney(player, amount);
                        player.sendMessage(ChatColor.GREEN + "You collected " + amount + currencySymbol + "!");
                        event.setCancelled(true);

                        // Check for existing banknote with the same value and stack them
                        if (banknotes.containsKey(displayName)) {
                            BigDecimal currentAmount = banknotes.get(displayName);
                            banknotes.put(displayName, currentAmount.add(amount));
                        } else {
                            banknotes.put(displayName, amount);
                        }

                        // Remove the original banknote from the player's inventory
                        item.setAmount(item.getAmount() - 1);
                        player.updateInventory();

                        // Give the stacked banknote back to the player
                        ItemStack stackedBanknote = new ItemStack(Material.valueOf(material));
                        ItemMeta stackedMeta = stackedBanknote.getItemMeta();
                        String stackedDisplayName = keyWord + currencySymbol + getTotalAmountForDisplayName(displayName);
                        stackedMeta.setDisplayName(stackedDisplayName);
                        stackedBanknote.setItemMeta(stackedMeta);
                        player.getInventory().addItem(stackedBanknote);
                    }
                }
            }
        }
    }

    private BigDecimal getTotalAmountForDisplayName(String displayName) {
        BigDecimal totalAmount = BigDecimal.ZERO;
        for (String key : banknotes.keySet()) {
            if (key.contains(displayName)) {
                totalAmount = totalAmount.add(banknotes.get(key));
            }
        }
        return totalAmount;
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
