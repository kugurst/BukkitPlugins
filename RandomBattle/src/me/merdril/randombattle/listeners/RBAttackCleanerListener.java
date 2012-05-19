/**
 * 
 */

package me.merdril.RandomBattle.listeners;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.UUID;

import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityDeathEvent;
import org.bukkit.event.entity.EntityExplodeEvent;
import org.bukkit.inventory.ItemStack;

/**
 * <p>
 * This class is responsible for cleaning up the list of monsters that RBAttackListener keeps track
 * of to prevent spamming players with RandomBattles for the same monster.
 * </p>
 * <p>
 * This class accomplishes this by keeping track of entities that have died, and once this number
 * reaches a specified threshold, all references within RBAttackListener to these monsters are
 * removed.
 * </p>
 * @author Merdril
 */
public class RBAttackCleanerListener implements Listener
{
	private RandomBattle	plugin;
	private UUID[]	     deadEntities;
	private Integer	     numDeadEntites;
	
	/**
	 * <p>
	 * Constructs a new listener for EntityDeathEvents and EntityExplodeEvents to clear those
	 * entities from the list of monsters it is keeping track of.
	 * </p>
	 * <p>
	 * The utility of this class lies in keeping its memory footprint low, and the cleaning method
	 * should not take a long time to execute, if the server has relatively few players, and even if
	 * it does, it should not block the functionality of this plugin if it cleans up relatively
	 * quickly enough.
	 * </p>
	 */
	public RBAttackCleanerListener(RandomBattle instance, int numReqEntities)
	{
		// Marks the plugin for future use.
		plugin = instance;
		// Initializes the variables this class uses to keep track of monsters to get rid of
		if (deadEntities == null)
			deadEntities = new UUID[numReqEntities];
		if (numDeadEntites == null)
			numDeadEntites = 0;
	}
	
	/**
	 * <p>
	 * This function looks through the HashMap of encountered entities and removes those that have
	 * died after a certain number of entities have died.
	 * </p>
	 * @param event
	 *            The EntityDeathEvent that triggered this call.
	 */
	@EventHandler (priority = EventPriority.LOW)
	public void onEntityDeath(EntityDeathEvent event)
	{
		// Verifies that the entity given was a monster
		boolean isMonster = event.getEntity() instanceof Monster;
		if (!isMonster)
			return;
		// Decides whether to add another entity to the list of entities, or to clean up the list of
		// entities
		if (numDeadEntites < deadEntities.length - 1) {
			deadEntities[numDeadEntites] = event.getEntity().getUniqueId();
			numDeadEntites++;
		}
		else {
			// Add the final dead entity
			deadEntities[deadEntities.length - 1] = event.getEntity().getUniqueId();
			// Reset the counter
			numDeadEntites = 0;
			// Retrieve the reference to the encounter mapping
			Collection<HashSet<UUID>> playerToMonsterList = RBAttackListener.alreadyEncountered.values();
			// If it's null or there's nothing in it, then there's no work to do.
			if (playerToMonsterList == null || playerToMonsterList.size() == 0)
				return;
			// Go through each player's list and remove the dead entities
			for (HashSet<UUID> playerList : playerToMonsterList) {
				synchronized (RBAttackListener.alreadyEncountered) {
					for (UUID entityID : deadEntities)
						playerList.remove(entityID);
				}
			}
		}
	}
	
	/**
	 * <p>
	 * Launches an onEntityDeath event whenever a creeper dies. I'm not aware if that's
	 * automatically triggered now when a creeper explodes, but in the last version that this plugin
	 * worked for, it did not.
	 * </p>
	 * <p>
	 * The creeper (Monster) and an empty list of items are passed in as the constructor for the
	 * EntityDeathEevent.
	 * </p>
	 * @param event
	 *            The EntityExplodedEvent that triggered this call.
	 */
	@EventHandler (priority = EventPriority.LOW)
	public void onEntityExplode(EntityExplodeEvent event)
	{
		if (event.getEntity() instanceof Monster) {
			ArrayList<ItemStack> nullList = new ArrayList<ItemStack>(0);
			plugin.getServer().getPluginManager()
			        .callEvent(new EntityDeathEvent((LivingEntity) event.getEntity(), nullList));
		}
	}
}
