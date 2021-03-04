package fr.milekat.MCPG_Cite;

import fr.milekat.MCPG_Cite.claims.ClaimManager;
import fr.milekat.MCPG_Cite.trades.TradesManager;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.plugin.java.JavaPlugin;

public class MainCite extends JavaPlugin {
    private static MainCite mainCite;
    private TradesManager tradesManager;
    public static String PREFIX = "[MCPG-Cite] ";

    @Override
    public void onEnable() {
        mainCite = this;
        FastInvManager.register(this);
        tradesManager = new TradesManager(this);
        new ClaimManager(this);
        tradesManager.CANTRADE = true;
    }

    @Override
    public void onDisable() {
        tradesManager.CANTRADE = false;
    }

    public static MainCite getInstance() { return mainCite; }
}
