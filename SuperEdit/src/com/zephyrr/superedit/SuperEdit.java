package com.zephyrr.superedit;

import java.util.HashMap;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.BlockDamageEvent;
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
	public void onDisable() {
		
	}
	
	@EventHandler
	public void onBlockStrike(BlockDamageEvent event) {
		Player who = event.getPlayer();
	}
	
	private boolean set(CommandSender sender, String[] args) {
		if(args.length == 0)
			return false;
//		if(sel1 == null) {
//			
//		}
		Material mat;
		try {
			mat = Material.valueOf(args[0].split(":")[0]);
		} catch(IllegalArgumentException ex) {
			return false;
		}
		return true;
	}
}
