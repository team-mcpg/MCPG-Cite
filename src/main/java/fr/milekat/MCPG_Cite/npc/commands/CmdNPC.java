package fr.milekat.MCPG_Cite.npc.commands;

import fr.milekat.MCPG_Cite.MainCite;
import fr.milekat.MCPG_Cite.npc.NPCManager;
import fr.milekat.MCPG_Cite.npc.object.NpcProperties;
import fr.milekat.MCPG_Core.MainCore;
import fr.milekat.MCPG_Core.utils.CmdUtils;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;

public class CmdNPC implements CommandExecutor {
    private final HashMap<CommandSender, NpcProperties> selectedNpc = new HashMap<>();
    private final NPCManager npcManager;

    public CmdNPC(NPCManager npcManager) {
        this.npcManager = npcManager;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        if (!(sender instanceof Player) || !sender.hasPermission("modo.npc.command.manage")) {
            sender.sendMessage(MainCite.prefix + "§cOnly for staff.");
            return true;
        }
        if (args.length >= 1) {
            switch (args[0]) {
                case "list": {
                    int page = 0;
                    if (args.length == 2) page = Integer.parseInt(args[1]);
                    StringBuilder npclist = new StringBuilder();
                    for (int loop = 0; loop < NPCManager.npcs.size(); loop++) {
                        if (!(Math.floor(loop / 10f) == page)) continue;
                        NpcProperties npc = NPCManager.npcs.get(loop);
                        npclist.append("§r - §2").append(npc.getId()).append(" §6- §b")
                                .append(npc.getNames().toString()).append(System.lineSeparator());
                    }
                    sender.sendMessage(MainCore.prefix + "NPC list§6: ");
                    sender.sendMessage(npclist.toString());
                    break;
                }
                case "create": {
                    NpcProperties npc = new NpcProperties(NPCManager.npcs.size() + 1, Collections.singletonList(args[1]),
                            13206,((Player) sender).getLocation(),true);
                    npcManager.setNPCsetup(npc);
                    NPCManager.npcs.add(npc);
                    selectedNpc.put(sender, npc);
                    sender.sendMessage(MainCite.prefix + "§cCreated !");
                    break;
                }
                case "select": {
                    int npcid = 0;
                    if (args.length==2) {
                        npcid = Integer.parseInt(args[1]);
                    } else sendHelp(sender, label);
                    NpcProperties npc = null;
                    for (NpcProperties loopNPC: NPCManager.npcs) {
                        if (loopNPC.getId()==npcid) {
                            npc = loopNPC;
                            break;
                        }
                    }
                    if (npc!=null) {
                        selectedNpc.put(sender, npc);
                        sender.sendMessage(MainCite.prefix + "§6Select §b" + npc.getNames().toString());
                    } else {
                        sender.sendMessage(MainCite.prefix + "§cUnknow NPC id");
                    }
                    break;
                }
                case "rename": {
                    if (hasNoSelected(sender)) break;
                    selectedNpc.get(sender).setNames(
                            new ArrayList<>(Arrays.asList(CmdUtils.getArgs(1, args).split("\\|"))));
                    npcManager.setNPCsetup(selectedNpc.get(sender));
                    sender.sendMessage(MainCite.prefix + "§aNpc renamed");
                    break;
                }
                case "skin": {
                    if (hasNoSelected(sender)) break;
                    if (args.length==2) {
                        try {
                            selectedNpc.get(sender).setSkinId(Integer.parseInt(args[1]));
                            npcManager.setNPCsetup(selectedNpc.get(sender));
                        } catch (Exception exception) {
                            sender.sendMessage(MainCore.prefix + "§cPlease, use a valid skin id from mineskin.org");
                        }
                    } else sendHelp(sender, label);
                    break;
                }
                case "tp": {
                    if (hasNoSelected(sender)) break;
                    ((Player) sender).teleport(selectedNpc.get(sender).getLocation());
                    sender.sendMessage(MainCite.prefix + "§2Teleport to " + selectedNpc.get(sender).getNames().get(0));
                    break;
                }
                case "move": {
                    if (hasNoSelected(sender)) break;
                    NpcProperties npc = selectedNpc.get(sender);
                    npc.setLocation(((Player) sender).getLocation());
                    npc.getNpc().destroy();
                    npcManager.setNPCsetup(npc);
                    sender.sendMessage(MainCite.prefix + "§a" + selectedNpc.get(sender).getNames().toString() +
                            " has been moved to you.");
                    break;
                }
                case "visible": {
                    try {
                        if (hasNoSelected(sender)) break;
                        NpcProperties npc = selectedNpc.get(sender);
                        npc.setVisible(Boolean.parseBoolean(args[1]));
                        sender.sendMessage("§aVisiblity: " + npc.isVisible());
                        npcManager.setNPCsetup(npc);
                    } catch (Exception exception) {
                        sender.sendMessage("§6/" + label + " visible <true/false> §b:§r hide/show the selected NPC");
                    }
                    break;
                }
                case "save": {
                    npcManager.saveNPC(NPCManager.npcs);
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
        sender.sendMessage("§6/" + lbl + " list <page> §b:§r Show you all loaded NPC in §b<page>");
        sender.sendMessage("§6/" + lbl + " create <name> §b:§r Crate a new NPC with named §b<name>");
        sender.sendMessage("§6/" + lbl + " select <npc_id> §b:§r Select the NPC");
        sender.sendMessage("§6/" + lbl + " rename <new_name> §b:§r Rename the selected NPC");
        sender.sendMessage("§6/" + lbl + " skin <skin_id> §b:§r Set the skin ID of the selected NPC");
        sender.sendMessage("§6/" + lbl + " tp §b:§r Teleport you to the selected NPC");
        sender.sendMessage("§6/" + lbl + " move §b:§r Teleport the selected NPC to you");
        sender.sendMessage("§6/" + lbl + " visible <true/false> §b:§r hide/show the selected NPC");
        sender.sendMessage("§6/" + lbl + " save §b:§r save all NPC data to MongoDB");
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
