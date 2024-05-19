package fr.mana.terrabank.events;

import fr.mana.terrabank.TerraBank;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.Inventory;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.meta.ItemMeta;
import org.jdbi.v3.core.Jdbi;

import java.sql.*;
import java.util.*;

public class Withdraw implements Listener {


    private String menuTitle;
    private TerraBank main;
    private Map<UUID, Boolean> playerAmountState = new HashMap<>();
    private Map<UUID, Float> playerAmount = new HashMap<>();

    private Map<UUID, Boolean> playerNumberState = new HashMap<>();
    private Map<UUID, Integer> playerNumber = new HashMap<>();
    public Withdraw(TerraBank main) {
        this.main = main;
        this.menuTitle = ChatColor.translateAlternateColorCodes('&', Objects.requireNonNull(main.getConfig().getString("withdraw.menu.title")));
    }

    @EventHandler public void onPlayerJoin(PlayerJoinEvent event){UUID PlayerUUID = event.getPlayer().getUniqueId();playerNumberState.put(PlayerUUID, false);playerAmountState.put(PlayerUUID, false);}


    @EventHandler
    public void onInventoryClick(InventoryClickEvent event){
        Inventory clickedInventory = event.getClickedInventory();
        assert clickedInventory != null;
        if (event.getView().getTitle().equals(menuTitle)) {
            event.setCancelled(true);

            ItemStack clickedItem = event.getCurrentItem();
            if (clickedItem == null || clickedItem.getType() == Material.AIR) {
                return;
            }

            assert clickedItem != null;

            for (String key : main.getConfig().getConfigurationSection("withdraw.menu.items").getKeys(false)) {

                String material = main.getConfig().getString("withdraw.menu.items." + key + ".material");
                List<Integer> slots = main.getConfig().getIntegerList("withdraw.menu.items." + key + ".slot");
                String eventAction = main.getConfig().getString("withdraw.menu.items." + key + ".event");

                if (slots.contains(event.getSlot()) && clickedItem.getType().toString().equalsIgnoreCase(material)) {
                    event.setCancelled(true);
                    assert eventAction != null;
                    event.setCancelled(true);
                    handleEventAction(eventAction, event);
                    break;
                }

            }
        }

    }

    @EventHandler
    public void onPlayerChatEvent(AsyncPlayerChatEvent event){
        Player player = event.getPlayer();
        UUID PlayerUUID = player.getUniqueId();

        if(playerAmountState.getOrDefault(PlayerUUID, true)) {
            event.setCancelled(true);
            try {
                float amount = Float.parseFloat(event.getMessage().replace(",","."));
                if(amount == 0){
                    player.sendMessage(main.getConfig().getString("withdraw.messages.invalidAmount").replace("&","§"));
                    playerAmountState.put(PlayerUUID, false);
                    playerNumberState.put(PlayerUUID, false);
                }else{
                    playerAmount.put(PlayerUUID, amount);
                    playerAmountState.put(PlayerUUID, false);
                    enterNumber(player, amount);
                }

            }catch (NumberFormatException e){
                player.sendMessage(main.getConfig().getString("withdraw.messages.invalidAmount").replace("&","§"));
                playerAmountState.put(PlayerUUID, false);
                playerNumberState.put(PlayerUUID, false);
            }
            playerAmountState.put(PlayerUUID, false);
        }

        else if(playerNumberState.getOrDefault(PlayerUUID, true)){
            event.setCancelled(true);
            try{
                int number = Integer.parseInt(event.getMessage());
                if(number == 0){
                    player.sendMessage(main.getConfig().getString("withdraw.messages.invalidNumber").replace("&","§"));
                }else{
                    playerNumber.put(PlayerUUID, number);
                    playerNumberState.put(PlayerUUID, false);
                    Float amount = playerAmount.get(PlayerUUID);
                    checkPlayerBalance(player, amount, number);
                }

            }catch (NumberFormatException e){
                player.sendMessage(main.getConfig().getString("withdraw.messages.invalidNumber").replace("&","§"));
                playerNumberState.put(PlayerUUID, false);
            }
            playerNumberState.put(PlayerUUID, false);
        }



    }

    private void handleEventAction(String eventAction, InventoryClickEvent event){
        switch (eventAction) {
            case "$close":
                event.getWhoClicked().closeInventory();
                break;
            case "$decoration":
                break;
            case "$withdraw":
                Player player = (Player) event.getWhoClicked();
                enterAmount(player);
                break;
        }
    }

    private void enterAmount(Player player){
        List<String> message = main.getConfig().getStringList ("withdraw.messages.enterAmount");
        player.closeInventory();
        UUID PlayerUUID = player.getUniqueId();
        for (String line : message) {
            player.sendMessage(line.replace("&","§"));
        };

        playerAmountState.put(PlayerUUID, true);

    }

    private void enterNumber(Player player, Float amount){
        List<String> message = main.getConfig().getStringList("withdraw.messages.enterNumber");
        UUID PlayerUUID = player.getUniqueId();
        for (String line : message) {
            player.sendMessage(line.replace("&","§").replace("%enteredAmount%", amount.toString()));
        };

        playerNumberState.put(PlayerUUID, true);
    }

    private void checkPlayerBalance(Player player, Float amount, Integer number){
        UUID playerUUID = player.getUniqueId();
        try (Connection connection = getConnection()){
            String query = "SELECT money FROM MoneyTable WHERE uuid = ?";
            try (PreparedStatement statement = connection.prepareStatement(query)){
                statement.setString(1, playerUUID.toString());
                try(ResultSet resultSet = statement.executeQuery()){
                    if (resultSet.next()){
                        float currentBalance = resultSet.getFloat("money");
                        if (currentBalance >= amount*number){
                            String updateQuery="UPDATE MoneyTable SET money = money - ? WHERE uuid = ?";
                            try (PreparedStatement updateStatement = connection.prepareStatement(updateQuery)){
                                updateStatement.setFloat(1, amount*number);
                                updateStatement.setString(2,playerUUID.toString());
                                updateStatement.executeUpdate();
                            }
                            givePlayerMoney(player,amount,number);
                        }else{player.sendMessage(main.getConfig().getString("withdraw.messages.notEnoughMoney").replace("&","§"));}
                    } else{player.sendMessage("§cErreur, vous n'êtes pas dans la base de donnée 'TerraBank'");}
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    private void givePlayerMoney(Player player, Float amount, Integer number){
        ItemStack bankNote = new ItemStack(Material.valueOf(main.getConfig().getString("bankNote.item")), number);
        ItemMeta bankNoteMeta = bankNote.getItemMeta();

        if (bankNoteMeta != null) {
            bankNoteMeta.setDisplayName(main.getConfig().getString("bankNote.display-name")
                    .replace("&", "§")
                    .replace("%value%", String.valueOf(amount))
                    .replace("%keyword%", Objects.requireNonNull(Objects.requireNonNull(main.getConfig().getString("bankNote.keyword")).replace("&", "§")))
                    .replace("%currencySymbol%", Objects.requireNonNull(main.getConfig().getString("bankNote.currencySymbol"))));

            List<String> description = main.getConfig().getStringList("bankNote.description");
            List<String> lore = new ArrayList<>();
            for (String line : description) {
                lore.add(line.replace("&", "§"));
            }

            bankNoteMeta.setLore(lore);
            bankNote.setItemMeta(bankNoteMeta);

            player.getInventory().addItem(bankNote);
        }
        player.sendMessage(Objects.requireNonNull(main.getConfig().getString("withdraw.messages.received")).replace("&","§").replace("%amount%", amount.toString()).replace("%number%", number.toString()));
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
