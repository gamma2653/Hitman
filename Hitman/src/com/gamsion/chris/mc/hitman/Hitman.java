package com.gamsion.chris.mc.hitman;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gamsion.chris.mc.hitman.commands.CheckHitCommand;
import com.gamsion.chris.mc.hitman.commands.HitCommand;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Hitman extends JavaPlugin {

	public static Economy econ = null;
	public static Permission perms = null;

	public Map<UUID, Double> bounties = new HashMap<UUID, Double>();
	
	@Override
	public void onEnable() {
		setupEconomy();
		setupPermissions();
		//Set commnads
		this.getCommand("hit").setExecutor(new HitCommand(this));
		this.getCommand("checkhit").setExecutor(new CheckHitCommand(this));
		//Register events
		this.getServer().getPluginManager().registerEvents(new HitListener(this), this);
		//Config settings
		this.saveDefaultConfig();
		
		this.getLogger().info("Hitman has successfully been enabled.");
	}

	@Override
	public void onDisable() {
		this.getLogger().info("Hitman has successfully been disabled.");
	}

	private boolean setupEconomy() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Economy> rsp = getServer().getServicesManager().getRegistration(Economy.class);
		if (rsp == null) {
			return false;
		}
		econ = rsp.getProvider();
		return econ != null;
	}

	private boolean setupPermissions() {
		if (getServer().getPluginManager().getPlugin("Vault") == null) {
			return false;
		}
		RegisteredServiceProvider<Permission> rsp = getServer().getServicesManager().getRegistration(Permission.class);
		if (rsp == null) {
			return false;
		}

		perms = rsp.getProvider();
		return perms != null;
	}
	
}
