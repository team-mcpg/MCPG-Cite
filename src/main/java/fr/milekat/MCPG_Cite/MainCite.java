package fr.milekat.MCPG_Cite;

import fr.milekat.MCPG_Cite.trades.TradesManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MainCite extends JavaPlugin {
    private static MainCite mainCite;
    private TradesManager tradesManager;
    public static String prefix = "[MCPG-Cite] ";

    @Override
    public void onEnable() {
        mainCite = this;
        tradesManager = new TradesManager(this);
        tradesManager.allowTrades = true;
    }

    @Override
    public void onDisable() {
        tradesManager.allowTrades = false;
    }

    public static MainCite getInstance() {
        return mainCite;
    }
}
