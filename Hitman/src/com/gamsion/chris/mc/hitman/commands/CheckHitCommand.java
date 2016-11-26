package com.gamsion.chris.mc.hitman.commands;

import java.text.DecimalFormat;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamsion.chris.mc.hitman.Hitman;

public class CheckHitCommand implements CommandExecutor {

	private final Hitman p;

	public CheckHitCommand(Hitman p) {
		this.p = p;

	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length < 1)
			return false;
		Player receiver = p.getServer().getPlayer(args[0]);
		if (receiver == null) {
			sender.sendMessage(ChatColor.DARK_RED + "Player not found.");
			return false;
		}

		if (!p.bounties.keySet().contains(receiver.getUniqueId()) || p.bounties.get(receiver.getUniqueId()).isNaN()) {
			sender.sendMessage(ChatColor.RED + "Player not found on bounty list.");
			return true;
		}
		sender.sendMessage(ChatColor.GREEN + String.format("%s currently has $%s bounty on his head.",
				receiver.getName(), new DecimalFormat("0.00").format(p.bounties.get(receiver.getUniqueId()))));
		
		return true;
	}

}
