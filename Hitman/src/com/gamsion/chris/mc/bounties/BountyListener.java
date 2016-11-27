package com.gamsion.chris.mc.bounties;

import java.text.DecimalFormat;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;

public class BountyListener implements Listener {
	Bounties p;
	private static final DecimalFormat moneyform = new DecimalFormat("0.00");
	public BountyListener(Bounties p) {
		this.p = p;
	}

	@EventHandler
	public void onPlayerKillPlayer(PlayerDeathEvent event) {
		Player killed = event.getEntity();
		Player killer = event.getEntity().getKiller();
		// if killer is a player
		if (killer != null) {
			// What to do if killer is self and not allowed by config (return)
			if (killed == killer && !p.getConfig().getBoolean("DEBUG.bounty-get-yourself")) {
				// If config allows, remove bounty
				if (p.getConfig().getBoolean("settings.lose-bounty-without-claim")) {
					Bukkit.broadcastMessage(
							String.format("%s " + ChatColor.DARK_GRAY + "has died without anyone claiming his bounty!",
									killed.getDisplayName()));
					p.bounties.remove(killed.getUniqueId().toString());
				}
				return;
			}
			// if killed is on bounty list, carry put transaction and remove
			// bounty
			if (p.bounties.containsKey(killed.getUniqueId().toString())) {
				double bounty = p.bounties.get(killed.getUniqueId().toString());
				Bounties.econ.depositPlayer(killer, bounty);
				if (p.getConfig().getBoolean("settings.displaywhoclaimed")) {
					Bukkit.broadcastMessage(killer.getDisplayName() + ChatColor.GREEN + " has killed "
							+ killed.getDisplayName() + ChatColor.GREEN + " for $" + moneyform.format(bounty));
				} else {
					Bukkit.broadcastMessage(String.format("%s's " + ChatColor.GREEN + "bounty ($%s) has been claimed!",
							killed.getDisplayName(), moneyform.format(bounty)));
				}

				p.bounties.remove(killed.getUniqueId().toString());
			}
			// if killer is not a player, check if config removes bounty
		} else {
			if (p.getConfig().getBoolean("settings.lose-bounty-without-claim")) {
				Bukkit.broadcastMessage(
						String.format("%s " + ChatColor.DARK_GRAY + "has died without anyone claiming his bounty!",
								killed.getDisplayName()));
				p.bounties.remove(killed.getUniqueId().toString());
			}
		}
	}

}
