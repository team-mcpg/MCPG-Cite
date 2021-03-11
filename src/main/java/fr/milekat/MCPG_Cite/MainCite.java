package fr.milekat.MCPG_Cite;

import fr.milekat.MCPG_Cite.bank.AtmEvent;
import fr.milekat.MCPG_Cite.claims.ClaimManager;
import fr.milekat.MCPG_Cite.core.CoreManager;
import fr.milekat.MCPG_Cite.trades.TradesManager;
import fr.mrmicky.fastinv.FastInvManager;
import org.bukkit.plugin.java.JavaPlugin;

import java.text.DecimalFormat;

public class MainCite extends JavaPlugin {
    public static String PREFIX = "§8[§6MCPG§8]§r ";
    public static final DecimalFormat df = new DecimalFormat("#,##0;");

    @Override
    public void onEnable() {
        FastInvManager.register(this);
        new TradesManager(this);
        new ClaimManager(this);
        new CoreManager(this);
        getServer().getPluginManager().registerEvents(new AtmEvent(), this);
    }
}
