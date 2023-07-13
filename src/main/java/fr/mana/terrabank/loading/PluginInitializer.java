package fr.mana.terrabank.loading;

import fr.mana.terrabank.*;
import fr.mana.terrabank.commands.bank.*;
import fr.mana.terrabank.commands.plugin.*;

import java.util.*;

public class PluginInitializer {

    private TerraBank main;
    public PluginInitializer(TerraBank main){
        this.main = main;
    }




    public void initialize(){
        DatabaseManager databaseManager = new DatabaseManager(main);
        main.saveDefaultConfig();
        databaseManager.connect();
        // Register /terrabank reload command
        Objects.requireNonNull(main.getCommand("terrabank")).setExecutor(new Reload(main));
        // Register /terrabank give command
        Objects.requireNonNull(main.getCommand("terrabank")).setExecutor(new GiveBanknote(main));
    }

}