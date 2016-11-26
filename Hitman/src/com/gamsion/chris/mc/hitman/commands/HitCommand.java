package com.gamsion.chris.mc.hitman.commands;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamsion.chris.mc.hitman.Hitman;

/**
 * <p>
 * Adds command functionality for <b>/hit</b>
 * </p>
 * 
 * @author gamma2626
 **/
public class HitCommand implements CommandExecutor {
	Hitman p;

	public HitCommand(Hitman p) {
		this.p = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length < 2)
			return false;
		Player receiver = p.getServer().getPlayer(args[0]);
		if (receiver == null) {
			sender.sendMessage(ChatColor.DARK_RED + "Player not found.");
			return false;
		}

		double amount = 0;
		try {
			amount = Double.parseDouble(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.DARK_RED + "Must input a proper number! (Decimals are acceptable)");
			return false;
		}
		if (amount <= 0) {
			sender.sendMessage(ChatColor.DARK_RED + "Must be higher than 0!");
		}
		String sendername;
		if (sender instanceof Player) {
			Player player = (Player) sender;
			//Self-harm prevention and debug
			if((player.getUniqueId() == receiver.getUniqueId()) && !p.getConfig().getBoolean("DEBUG.bounty-yourself")){
				player.sendMessage(ChatColor.RED+"You can't put a bounty on yourself!");
				return false;
			}
			sendername = player.getDisplayName();
			Hitman.econ.withdrawPlayer((Player) sender, amount);
		} else {
			sendername = ChatColor.BLACK + p.getConfig().getString("settings.server-name");
		}
		if (p.bounties.containsKey(receiver.getUniqueId())) {
			p.bounties.put(receiver.getUniqueId(), p.bounties.get(receiver.getUniqueId()) + amount);
		} else {
			p.bounties.put(receiver.getUniqueId(), amount);
		}
		if (p.getConfig().getBoolean("settings.displaywhohit")) {
			Bukkit.broadcastMessage(ChatColor.GREEN + String.format(
					"A bounty has been placed on %s" + ChatColor.GREEN + " by " + "%s" + ChatColor.GREEN + " for "
							+ ChatColor.DARK_GREEN + "$%s!",
					receiver.getDisplayName(), sendername, new DecimalFormat("0.00").format(amount)));
		} else {
			Bukkit.broadcastMessage(ChatColor.GREEN + String.format(
					"A bounty has been placed on %s" + ChatColor.GREEN + " for " + ChatColor.DARK_GREEN + "$%s!",
					receiver.getDisplayName(), new DecimalFormat("0.00").format(amount)));
		}
		return true;
	}

}
