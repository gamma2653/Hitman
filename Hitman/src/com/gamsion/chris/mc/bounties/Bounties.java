package com.gamsion.chris.mc.bounties;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;

import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.configuration.InvalidConfigurationException;
import org.bukkit.configuration.file.YamlConfiguration;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;

import com.gamsion.chris.mc.bounties.commands.BountyCommand;

import net.milkbowl.vault.economy.Economy;
import net.milkbowl.vault.permission.Permission;

public class Bounties extends JavaPlugin {

	public static Economy econ = null;
	public static Permission perms = null;

	public Map<String, Double> bounties = new HashMap<String, Double>();

	@Override
	public void onEnable() {
		setupEconomy();
		setupPermissions();
		// Set commands
		this.getCommand("bounty").setExecutor(new BountyCommand(this));
		// Register events
		this.getServer().getPluginManager().registerEvents(new BountyListener(this), this);
		// Config settings
		this.saveDefaultConfig();
		this.load();
		this.getLogger().info("Bounties has successfully been enabled.");
	}

	@Override
	public void onDisable() {
		this.save();
		this.getLogger().info("Bounties has successfully been disabled.");
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

	public void save() {
		if (this.getConfig().getBoolean("save.sqlite")) {

		} else {
			YamlConfiguration save = new YamlConfiguration();
			String directory = "./plugins/Bounties/saves/";
			String filename = "save.yml";
			File savefile = new File(directory + filename);

			try {
				if (!savefile.exists()) {
					new File(directory).mkdirs();
					savefile.createNewFile();
				}
				save.load(savefile);
				save.set("Bounty", bounties);
				save.save(savefile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}

		}
	}

	public void load() {
		this.bounties = new HashMap<String, Double>();
		if (this.getConfig().getBoolean("save.sqlite")) {

		} else {
			YamlConfiguration save = new YamlConfiguration();
			String directory = "./plugins/Bounties/saves/";
			String filename = "save.yml";
			File savefile = new File(directory + filename);
			try {
				if (!savefile.exists()) {
					this.getLogger().warning("No save file found.");
					return;
				}
				save.load(savefile);

				ConfigurationSection sec = save.getConfigurationSection("Bounty");
				if (sec == null || sec.getKeys(false) == null || sec.getKeys(false).isEmpty()) {

					this.getLogger().warning("No save found.");
					return;
				}
				Set<String> keys = sec.getKeys(false);
				for (String key : keys) {
					bounties.put(key, sec.getDouble(key));

				}
				save.save(savefile);
			} catch (FileNotFoundException e) {
				e.printStackTrace();
			} catch (IOException e) {
				e.printStackTrace();
			} catch (InvalidConfigurationException e) {
				e.printStackTrace();
			}
		}
	}

}
