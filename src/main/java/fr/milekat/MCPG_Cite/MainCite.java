package fr.milekat.MCPG_Cite;

import fr.milekat.MCPG_Cite.trades.TradesManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MainCite extends JavaPlugin {
    private static MainCite mainCite;
    private TradesManager tradesManager;
    public static String PREFIX = "[MCPG-Cite] ";

    @Override
    public void onEnable() {
        mainCite = this;
        tradesManager = new TradesManager(this);
        tradesManager.CANTRADE = true;
    }

    @Override
    public void onDisable() {
        tradesManager.CANTRADE = false;
    }

    public static MainCite getInstance() { return mainCite; }
}
