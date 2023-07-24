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
    private HashMap<BigDecimal, BigDecimal> banknotes;

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

                        if (banknotes.containsKey(amount)) {
                            BigDecimal totalAmount = banknotes.get(amount);
                            totalAmount = totalAmount.add(amount);
                            banknotes.put(amount, totalAmount);
                        } else {
                            banknotes.put(amount, amount);
                        }

                        addMoney(player, amount);
                        player.sendMessage(ChatColor.GREEN + "Vous avez collecté " + amount + currencySymbol + "!");
                        event.setCancelled(true);

                        int totalBills = 0;
                        for (BigDecimal billAmount : banknotes.keySet()) {
                            int count = banknotes.get(billAmount).intValue();
                            totalBills += count;

                            // Retirer les billets originaux de l'inventaire du joueur
                            ItemStack[] inventoryContents = player.getInventory().getContents();
                            for (ItemStack stackItem : inventoryContents) {
                                if (stackItem != null && stackItem.getType() == Material.valueOf(material)) {
                                    ItemMeta stackMeta = stackItem.getItemMeta();
                                    if (stackMeta != null && stackMeta.hasDisplayName()) {
                                        String stackDisplayName = stackMeta.getDisplayName();
                                        Matcher stackMatcher = amountPattern.matcher(stackDisplayName);
                                        if (stackMatcher.find()) {
                                            String stackAmountString = stackMatcher.group(1);
                                            BigDecimal stackAmount = new BigDecimal(stackAmountString);
                                            if (stackAmount.compareTo(billAmount) == 0) {
                                                stackItem.setAmount(0);
                                            }
                                        }
                                    }
                                }
                            }
                        }

                        // Donner le nouveau billet avec la somme totale
                        ItemStack newBanknote = new ItemStack(Material.valueOf(material));
                        newBanknote.setAmount(totalBills);
                        ItemMeta stackedMeta = newBanknote.getItemMeta();
                        String stackedDisplayName = keyWord + currencySymbol + (amount.multiply(BigDecimal.valueOf(totalBills)));
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
