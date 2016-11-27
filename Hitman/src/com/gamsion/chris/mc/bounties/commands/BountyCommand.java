package com.gamsion.chris.mc.bounties.commands;

import java.text.DecimalFormat;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

import com.gamsion.chris.mc.bounties.Bounties;

/**
 * <p>
 * Adds command functionality for <b>/bounty</b>
 * </p>
 * 
 * @author gamma2626
 **/
public class BountyCommand implements CommandExecutor {
	Bounties p;
	private static final DecimalFormat moneyform = new DecimalFormat("0.00");

	public BountyCommand(Bounties p) {
		this.p = p;
	}

	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String lbl, String[] args) {
		if (args.length == 0) {
			sender.sendMessage(getHitMessage());
			return false;
		} else if (args.length == 1) {
			if (args[0].trim().equalsIgnoreCase("cost")) {
				sender.sendMessage(ChatColor.WHITE + "Hits cost " + ChatColor.DARK_GREEN
						+ moneyform.format(p.getConfig().getDouble("settings.fee")) + ChatColor.WHITE + " to set.");
				return true;
			} else if (args[0].trim().equalsIgnoreCase("list") || args[0].trim().equalsIgnoreCase("all")) {
				sender.sendMessage(this.getHitMessage());
				return true;
			} else if (args[0].trim().equalsIgnoreCase("help")) {
				sender.sendMessage(ChatColor.DARK_RED + "Still under development.");
				return true;
			} else {
				return false;
			}
		} else if (args.length == 2) {
			if (args[0].trim().equalsIgnoreCase("get")) {
				Player receiver = p.getServer().getPlayer(args[0]);
				if (receiver == null) {
					sender.sendMessage(ChatColor.DARK_RED + "Player not found.");
					return false;
				}
				if (!p.bounties.containsKey(receiver.getUniqueId().toString())) {
					sender.sendMessage(ChatColor.RED + "Player not found on bounty list.");
					return true;
				}
				double amount = p.bounties.get(receiver.getUniqueId().toString());
				sender.sendMessage(ChatColor.GREEN + String.format(
						"%s currently has " + ChatColor.DARK_GREEN + "$%s " + ChatColor.WHITE + "bounty on his head.",
						receiver.getDisplayName(), moneyform.format(amount)));

				return true;
			} else {
				return false;
			}
		} else {
			if (args[0].trim().equalsIgnoreCase("put") || args[0].trim().equalsIgnoreCase("add")
					|| args[0].trim().equalsIgnoreCase("set")) {
				Player receiver = p.getServer().getPlayer(args[1].trim());
				if (receiver == null) {
					sender.sendMessage(ChatColor.DARK_RED + "Player not found.");
					return false;
				}

				double amount = 0;
				try {
					amount = Double.parseDouble(args[2]);
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
					// Self-harm prevention and debug
					if ((player.getUniqueId().toString().equalsIgnoreCase(receiver.getUniqueId().toString()))
							&& !p.getConfig().getBoolean("DEBUG.bounty-yourself")) {
						player.sendMessage(ChatColor.RED + "You can't put a bounty on yourself!");
						return true;
					}
					sendername = player.getDisplayName();
					double fee = p.getConfig().getDouble("settings.fee");
					if (Bounties.econ.getBalance(player) < (amount + fee)) {
						sender.sendMessage(ChatColor.DARK_RED + "Sorry, you do not have enough to place that hit!");
						return true;
					}
					Bounties.econ.withdrawPlayer((Player) sender, amount + fee);
				} else {
					sendername = ChatColor.BLACK + p.getConfig().getString("settings.server-name");
				}
				if (p.bounties.containsKey(receiver.getUniqueId().toString())) {
					p.bounties.put(receiver.getUniqueId().toString(),
							p.bounties.get(receiver.getUniqueId().toString()) + amount);
				} else {
					p.bounties.put(receiver.getUniqueId().toString(), amount);
				}
				if (p.getConfig().getBoolean("settings.displaywhohit")) {
					Bukkit.broadcastMessage(ChatColor.GREEN + String.format(
							"A bounty has been placed on %s" + ChatColor.GREEN + " by %s" + ChatColor.GREEN + " for "
									+ ChatColor.DARK_GREEN + "$%s!",
							receiver.getDisplayName(), sendername, moneyform.format(amount)));
				} else {
					Bukkit.broadcastMessage(
							ChatColor.GREEN
									+ String.format(
											"A bounty has been placed on %s" + ChatColor.GREEN + " for "
													+ ChatColor.DARK_GREEN + "$%s!",
											receiver.getDisplayName(), moneyform.format(amount)));
				}

			}
		}

		return true;
	}

	private String getHitMessage() {
		StringBuilder message = new StringBuilder();
		message.append(ChatColor.WHITE + "\nHits cost " + ChatColor.DARK_GREEN
				+ moneyform.format(p.getConfig().getDouble("settings.fee")) + ChatColor.WHITE + " to set.");
		message.append("\n");
		if (p.getConfig().getBoolean("settings.displaywhohit")) {
			message.append(
					"When you set a hit on someone, everyone online will be alerted that a bounty has been set on someone by you.");
		} else {
			message.append(
					"When you set a hit on someone, everyone online will be alerted that a bounty has been set on someone, but not by who.");
		}
		message.append("\n");
		if (p.getConfig().getBoolean("settings.displaywhoclaimed")) {
			message.append(
					"When the player with the bounty is assassinated, it is announced to the server that the killer has completed the hit and received the amount that was set (excluding the fee).");
		} else {
			message.append(
					"When the player with the bounty is assassinated, it is announced to the server that the bounty has been completed.");
		}
		message.append("\n");
		if (p.getConfig().getBoolean("settings.lose-bounty-without-claim")) {
			message.append("If the player dies of natural or otherwise causes, the bounty is removed.");
		} else {
			message.append("If the player dies of other causes not involving a player, the bounty remains.");
		}

		return message.toString();
	}

	public String getHitList() {
		StringBuilder hitlist = new StringBuilder(
				ChatColor.DARK_RED + "\n                                   BOUNTY LIST:\n\n" + ChatColor.DARK_AQUA
						+ "~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~~\n" + ChatColor.WHITE);
		for (String key : p.bounties.keySet()) {
			String entry = String.format("%s" + ChatColor.DARK_GREEN + ": $%s\n",
					Bukkit.getPlayer(UUID.fromString(key)).getDisplayName(), moneyform.format(p.bounties.get(key)));
			hitlist.append(entry);
		}
		return hitlist.toString().trim();
	}

}
