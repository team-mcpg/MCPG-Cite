package fr.milekat.MCPG_Cite;

import fr.milekat.MCPG_Cite.bank.BankManager;
import fr.milekat.MCPG_Cite.claims.ClaimManager;
import fr.milekat.MCPG_Cite.core.CoreManager;
import fr.milekat.MCPG_Cite.frozen.FrozenManager;
import fr.milekat.MCPG_Cite.rules.RulesManager;
import fr.milekat.MCPG_Cite.trades.TradesManager;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

public class MainCite extends JavaPlugin {
    public static String PREFIX = "§7[§bLa Cité Givrée§7]§r ";
    public static final DecimalFormat DECIMAL_FORMAT = new DecimalFormat("#,##0;");
    private static TradesManager trades;

    @Override
    public void onEnable() {
        FastInvManager.register(this);
        trades = new TradesManager(this);
        new ClaimManager(this);
        new CoreManager(this);
        new FrozenManager(this);
        new RulesManager(this);
        new BankManager(this);
    }

    public static TradesManager getTrades() { return trades; }
}
