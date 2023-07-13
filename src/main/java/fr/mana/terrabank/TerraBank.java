package fr.mana.terrabank;

import fr.mana.terrabank.loading.*;
import org.bukkit.plugin.java.JavaPlugin;

public final class TerraBank extends JavaPlugin {

    @Override
    public void onEnable() {
        // Initialize plugin
        PluginInitializer pluginInitializer = new PluginInitializer(this);
        pluginInitializer.initialize();
    }

    @Override
    public void onDisable() {
        System.out.println("TerraBank disabled");
    }
}
