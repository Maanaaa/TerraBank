package fr.mana.terrabank.loading;

import fr.mana.terrabank.*;
import fr.mana.terrabank.commands.*;

public class PluginInitializer {

    private TerraBank main;
    public PluginInitializer(TerraBank main){
        this.main = main;
    }


    public void initialize(){
        DatabaseManager databaseManager = new DatabaseManager(main);
        main.saveDefaultConfig();
        databaseManager.connect();
        main.getCommand("terrabank").setExecutor(new Reload(main));
    }

}