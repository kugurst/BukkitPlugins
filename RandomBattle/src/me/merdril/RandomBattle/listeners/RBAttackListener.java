
package me.merdril.RandomBattle.listeners;

import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import me.merdril.RandomBattle.BattleSetter;
import me.merdril.RandomBattle.RBUtilities;
import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.Arrow;
import org.bukkit.entity.Monster;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDamageByEntityEvent;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * <p>
 * This class is responsible for initializing a random battle if the stars have aligned.
 * </p>
 * @author Merdril
 */
public class RBAttackListener implements Listener
{
	private RandomBattle	        plugin;
	private int	                    randomChance	= 100;
	private Random	                generator	 = new Random();
	static Map<UUID, HashSet<UUID>>	alreadyEncountered;
	
	/**
	 * <p>
	 * Starts entity tracking services. The grunt work of this class is done in the onEntityDamage
	 * method.
	 * </p>
	 */
	public RBAttackListener(RandomBattle instance)
	{
		plugin = instance;
		if (alreadyEncountered == null)
			alreadyEncountered = Collections.synchronizedMap(new HashMap<UUID, HashSet<UUID>>());
	}
	
	/**
	 * <p>
	 * Launches a Random Battle based on the probability specified in the config file.
	 * </p>
	 * <p>
	 * This method also records which entities have been encountered by whom, so as not to cause
	 * Random Battle spamming.
	 * </p>
	 * @param event
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onEntityDamage(EntityDamageByEntityEvent event)
	{
		SpoutPlayer player = null;
		Monster monster = null;
		UUID entityID = null;
		UUID playerID = null;
		// Assigns the monster and player appropriately
		if (event.getDamager() instanceof Player) {
			player = (SpoutPlayer) event.getDamager();
			playerID = player.getUniqueId();
			if (event.getEntity() instanceof Monster) {
				monster = (Monster) event.getEntity();
				entityID = monster.getUniqueId();
			}
		}
		// Retrieves the shooter of the arrow
		else if (event.getDamager() instanceof Arrow) {
			Arrow arrow = (Arrow) event.getDamager();
			if (arrow.getShooter() instanceof Player) {
				player = (SpoutPlayer) arrow.getShooter();
				playerID = player.getUniqueId();
				if (event.getEntity() instanceof Monster) {
					monster = (Monster) event.getEntity();
					entityID = monster.getUniqueId();
				}
			}
		}
		// If a player was damaged, make sure the damager was a monster.
		else if (event.getEntity() instanceof Player) {
			player = (SpoutPlayer) event.getEntity();
			playerID = player.getUniqueId();
			if (event.getDamager() instanceof Monster) {
				monster = (Monster) event.getDamager();
				entityID = monster.getUniqueId();
			}
			// For a skeleton, if a skeleton ever stops triggering the above
			else if (event.getDamager() instanceof Arrow) {
				Arrow arrow = (Arrow) event.getDamager();
				if (arrow.getShooter() instanceof Monster) {
					monster = (Monster) event.getDamager();
					entityID = monster.getUniqueId();
				}
			}
		}
		// If it wasn't between a monster and a registered Spoutcraft player, return.
		if (player != null
		        && !RBUtilities.isRegisteredPlayer(player.getDisplayName(), 0, plugin.getServer().getConsoleSender()))
			return;
		// If it wasn't player vs player
		if (monster != null) {
			// Get the UUIDs of the monsters this player has encountered
			HashSet<UUID> checkList = alreadyEncountered.get(playerID);
			// In the case this is null, this is a new encounter
			if (checkList == null) {
				HashSet<UUID> temp = new HashSet<UUID>();
				temp.add(entityID);
				// Synchronized to prevent a race condition with the cleaner listener.
				synchronized (alreadyEncountered) {
					alreadyEncountered.put(playerID, temp);
				}
				// Debugging
				player.sendMessage(RandomBattle.prefix + SpoutManager.getEntityFromId(entityID) + " has been added.");
			}
			// In the case the list is not null, we should check to make sure we haven't already
			// encountered this monster
			else {
				// If it has been encountered, don't proceed further
				if (checkList.contains(entityID))
					return;
				// Otherwise, add it to the encountered list
				else {
					synchronized (alreadyEncountered) {
						checkList.add(entityID);
					}
					player.sendMessage(RandomBattle.prefix + SpoutManager.getEntityFromId(entityID)
					        + " has been added.");
				}
			}
			// Generates a number between 1 and 99, which ensures that 0 means that a battle will
			// never occur, and that 100 means a battle will always occur.
			int randomNumber = generator.nextInt(98) + 1;
			// If there is a battle, launch the event
			if (randomChance > randomNumber) {
				event.setCancelled(true);
				@SuppressWarnings ("unused")
				BattleSetter begin;
				if (monster != null)
					begin = new BattleSetter(plugin, player, monster, 123, 24, 16);
				player.sendMessage(RandomBattle.prefix + event.getDamage() + " random number: " + randomNumber);
			}
		}
	}
}
