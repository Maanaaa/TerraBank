package fr.mana.terrabank.loading;


import fr.mana.terrabank.*;
import fr.mana.terrabank.commands.*;
import fr.mana.terrabank.events.*;

import java.util.*;

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
        // Register /terrabank give command
        Objects.requireNonNull(main.getCommand("terrabank")).setExecutor(new GiveBanknote(main));
        // Register collect money event
        main.getServer().getPluginManager().registerEvents(new CollectBanknote(main), main);
    }

}