package com.zephyrr.superedit;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.Action;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SuperEdit extends JavaPlugin implements Listener {
	private HashMap<String, Location[]> selections;
	public void onEnable() {
		selections = new HashMap<String, Location[]>();
		saveDefaultConfig();
		for(World w : getServer().getWorlds())
			for(Player p : w.getPlayers())
				selections.put(p.getName(), new Location[2]);
		getServer().getPluginManager().registerEvents(this, this);
	}
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
		if(!(sender instanceof Player))
			return false;
		if(!sender.hasPermission("superedit." + label.toLowerCase()))
			return false;
		switch(label.toLowerCase()) {
		case "set":	return set(sender, args);
		}
		return true;
	}
	
	@EventHandler 
	public void onPlayerTeleport(PlayerTeleportEvent event) {
		if(selections.containsKey(event.getPlayer().getName()))
			selections.remove(event.getPlayer().getName());
	}
	
	@EventHandler
	public void onPlayerJoin(PlayerJoinEvent event) {
		if(selections.containsKey(event.getPlayer().getName()))
			selections.remove(event.getPlayer().getName());
		selections.put(event.getPlayer().getName(), new Location[2]);
	}
	
	@EventHandler
	public void onBlockInteract(PlayerInteractEvent event) {
		int which = -1;
		if(event.getAction() == Action.LEFT_CLICK_BLOCK)
			which = 0;
		else if(event.getAction() == Action.RIGHT_CLICK_BLOCK)
			which = 1;
		else return;
		if(event.getPlayer().getItemInHand().getTypeId() == getConfig().getInt("utility")) {
			event.setCancelled(true);
			setSelCoord(event.getPlayer().getName(), which, event.getClickedBlock().getLocation());
		}
	}
	
	private void setSelCoord(String who, int which, Location loc) {
		if(!selections.containsKey(who))
			selections.put(who, new Location[2]);
		selections.get(who)[which] = loc;
		getServer().getPlayer(who).sendMessage(ChatColor.GOLD + "[SuperEdit] Corner " + (which+1) + " set to {" + loc.getBlockX() + "," + loc.getBlockY() + "," + loc.getBlockZ() + "}");
	}
	
	private boolean set(CommandSender sender, String[] args) {
		if(args.length == 0)
			return false;
		if(selections.get(sender.getName())[0] == null ||
				selections.get(sender.getName())[1] == null) {
			sender.sendMessage(ChatColor.GOLD + "[SuperEdit] You must select both corners!");
			return true;
		}
		Material mat;
		try {
			mat = Material.valueOf(args[0].split(":")[0].toUpperCase());
		} catch(IllegalArgumentException ex) {
			return false;
		}
		Location[] sel = selections.get(sender.getName());
		Location min = new Location(sel[0].getWorld(),
									Math.min(sel[0].getX(), sel[1].getX()),
									Math.min(sel[0].getY(), sel[1].getY()),
									Math.min(sel[0].getZ(), sel[1].getZ()));
		Location max = new Location(sel[0].getWorld(),
				Math.max(sel[0].getX(), sel[1].getX()),
				Math.max(sel[0].getY(), sel[1].getY()),
				Math.max(sel[0].getZ(), sel[1].getZ()));
		for(int x = min.getBlockX(); x <= max.getBlockX(); x++) {
			for(int y = min.getBlockY(); y <= max.getBlockY(); y++) {
				for(int z = min.getBlockZ(); z <= max.getBlockZ(); z++) {
					Block block = min.getWorld().getBlockAt(new Location(min.getWorld(), x, y, z));
					block.setType(mat);
					if(args[0].split(":").length != 1)
						try {
							block.setData((byte)Integer.parseInt(args[0].split(":")[1]));
						} catch(NumberFormatException n) {};
				}
			}
		}
		return true;
	}
}
