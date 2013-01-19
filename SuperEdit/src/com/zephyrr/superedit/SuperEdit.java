package com.zephyrr.superedit;

import java.util.HashMap;

import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerTeleportEvent;
import org.bukkit.plugin.java.JavaPlugin;

public class SuperEdit extends JavaPlugin implements Listener {
	private HashMap<String, Location[]> selections;
	public void onEnable() {
		selections = new HashMap<String, Location[]>();
		saveDefaultConfig();
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
	}
	
	@EventHandler
	public void onBlockStrike(BlockDamageEvent event) {
		setSelCoord(event.getPlayer().getName(), 0, event.getBlock().getLocation());
	}
	
	@EventHandler
	public void onBlockInteract(PlayerInteractEntityEvent event) {
		if(event.getRightClicked() instanceof Block) 
			setSelCoord(event.getPlayer().getName(), 1, event.getRightClicked().getLocation());
	}
	
	private void setSelCoord(String who, int which, Location where) {
		if(!selections.containsKey(who))
			selections.put(who, new Location[2]);
		selections.get(who)[which] = where;
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
			mat = Material.valueOf(args[0].split(":")[0]);
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
