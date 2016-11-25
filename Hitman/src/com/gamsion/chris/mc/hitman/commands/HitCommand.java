package com.gamsion.chris.mc.hitman.commands;

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
			amount = Integer.parseInt(args[1]);
		} catch (NumberFormatException e) {
			sender.sendMessage(ChatColor.DARK_RED + "Must input a proper number! (Decimals are acceptable)");
			return false;
		}
		if (amount <= 0) {
			sender.sendMessage(ChatColor.DARK_RED + "Must be higher than 0!");
		}

		p.bounties.put(receiver.getUniqueId(), amount);

		return false;
	}

}
