package fr.mana.terrabank.loading;


import fr.mana.terrabank.commands.*;
import fr.mana.terrabank.events.*;

import java.util.*;
import java.util.concurrent.ConcurrentHashMap;

public class PluginInitializer {
    private fr.mana.terrabank.TerraBank main;
    public PluginInitializer(fr.mana.terrabank.TerraBank main){
        this.main = main;
    }
    public void initialize(){
        DatabaseManager databaseManager = new DatabaseManager(main);
        main.saveDefaultConfig();
        main.reloadConfig();
        databaseManager.connect();

        Objects.requireNonNull(main.getCommand("terrabank")).setExecutor(new TerraBankCommand(main));
        Objects.requireNonNull(main.getCommand("withdraw")).setExecutor(new WithdrawCommand());

        Withdraw withdraw = new Withdraw(main);
        main.getServer().getPluginManager().registerEvents(new CollectBanknote(main), main);
        main.getServer().getPluginManager().registerEvents(new Withdraw(main), main);
    }



}