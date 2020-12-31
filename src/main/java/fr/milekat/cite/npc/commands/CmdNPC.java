package fr.milekat.cite.npc.commands;

import fr.milekat.MCPG_Core.MainCore;
import fr.milekat.MCPG_Core.utils.CmdUtils;
import fr.milekat.cite.MainCite;
import fr.milekat.cite.npc.NPCManager;
import fr.milekat.cite.npc.object.NpcProperties;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.*;

public class CmdNPC implements CommandExecutor {
    private final HashMap<CommandSender, NpcProperties> selectedNpc = new HashMap<>();
    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("modo.npc.command.manage")) {
            sender.sendMessage(MainCite.Prefix + "§cOnly for staff.");
            return true;
        }
        if (args.length >= 1) {
            switch (args[0]) {
                case "list": {
                    StringBuilder npclist = new StringBuilder();
                    for (NpcProperties npc : NPCManager.npcs) {
                        npclist.append(npc.getNames().toString());
                    }
                    sender.sendMessage(MainCore.prefix + "NPC list§6: ");
                    sender.sendMessage(npclist.toString());
                    break;
                }
                case "create": {
                    NpcProperties npc = new NpcProperties(NPCManager.npcs.size() + 1,
                            Collections.singletonList(args[1]),13206,((Player) sender).getLocation(),true);
                    NPCManager.setNPCsetup(npc);
                    selectedNpc.put(sender, npc);
                    break;
                }
                case "select": {
                    int npcid = 0;
                    if (args.length==2) {
                        npcid = Integer.parseInt(args[1]);
                    } else sendHelp(sender, label);
                    int finalNpcid = npcid;
                    Optional<NpcProperties> optionalNpc = NPCManager.npcs.stream()
                            .filter((npcstream) -> finalNpcid == npcstream.getId())
                            .findFirst();
                    if (optionalNpc.isPresent()) {
                        selectedNpc.put(sender, optionalNpc.get());
                    } else {
                        sender.sendMessage(MainCite.Prefix + "§cUnknow NPC id");
                    }
                    break;
                }
                case "rename": {
                    if (hasNoSelected(sender)) break;
                    selectedNpc.get(sender).setNames(new ArrayList<>(Arrays.asList(CmdUtils.getArgs(1, args).split("\\|"))));
                    break;
                }
                case "skin": {
                    if (hasNoSelected(sender)) break;

                    break;
                }
                case "tp": {
                    if (hasNoSelected(sender)) break;
                    ((Player) sender).teleport(selectedNpc.get(sender).getLocation());
                    break;
                }
                case "move": {
                    if (hasNoSelected(sender)) break;
                    selectedNpc.get(sender).setLocation(((Player) sender).getLocation());
                    selectedNpc.get(sender).getNpc().setLocation(((Player) sender).getLocation());
                    break;
                }
                case "visible": {
                    if (hasNoSelected(sender)) break;
                    NpcProperties npc = selectedNpc.get(sender);
                    npc.setVisible(Boolean.getBoolean(args[1]));
                    for (Player player : Bukkit.getOnlinePlayers()) {
                        if (npc.isVisible()) {
                            npc.getNpc().show(player);
                        } else {
                            npc.getNpc().hide(player);
                        }
                    }
                    break;
                }
                default: sendHelp(sender, label);
            }
        } else {
            sendHelp(sender, label);
        }
        return true;
    }

    private void sendHelp(CommandSender sender, String lbl) {
        sender.sendMessage(MainCore.prefix + "Help command NPC§6:");
        sender.sendMessage("§6/" + lbl + " list §b:§r Show you all loaded NPC");
        sender.sendMessage("§6/" + lbl + " create <name> §b:§r Crate a new NPC with named §b<name>");
        sender.sendMessage("§6/" + lbl + " select <npc_id> §b:§r Select the NPC");
        sender.sendMessage("§6/" + lbl + " rename <new_name> §b:§r Rename the selected NPC");
        sender.sendMessage("§6/" + lbl + " skin <skin_id> §b:§r Set the skin ID of the selected NPC");
        sender.sendMessage("§6/" + lbl + " tp §b:§r Teleport you to the selected NPC");
        sender.sendMessage("§6/" + lbl + " move §b:§r Teleport the selected NPC to you");
        sender.sendMessage("§6/" + lbl + " visible <true/false> §b:§r hide/show the selected NPC");
    }

    private boolean hasNoSelected(CommandSender sender) {
        if (selectedNpc.containsKey(sender)) {
            return false;
        } else {
            sender.sendMessage(MainCore.prefix + "Please, select a NPC before do this command.");
            return true;
        }
    }
}
