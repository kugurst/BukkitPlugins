
package me.merdril.randombattle.listeners;

import me.merdril.randombattle.RBCommandExecutor;
import me.merdril.randombattle.RandomBattle;

import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerKickEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.getspout.spoutapi.event.spout.SpoutCraftEnableEvent;

public class RBLoginListener implements Listener
{
	
	private RandomBattle	plugin;
	
	public RBLoginListener(RandomBattle instance)
	{
		plugin = instance;
	}
	
	/**
	 * <p>
	 * Calls the onPlayerLogoff(PlayerQuitEvent event) method of this class. Does not make an API
	 * call to avoid cluttering any other plugins that would like to know when a player logs off.
	 * </p>
	 * @param event
	 *            The PlayerKickEvent of the player that was kicked.
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerLogoff(PlayerKickEvent event)
	{
		PlayerQuitEvent quitEvent = new PlayerQuitEvent(event.getPlayer(), event.getReason());
		onPlayerLogoff(quitEvent);
	}
	
	/**
	 * <p>
	 * Removes a player from the list of registered players and moves it to the list of inactive
	 * players.
	 * </p>
	 * <p>
	 * This enables players who have already registered to still be registered when they log back
	 * in.
	 * </p>
	 * @param event
	 *            The PlayerQuitEvent that triggered this method call. Could also really be a
	 *            PlayerKickEvent, though both amount to the player becoming offline.
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onPlayerLogoff(PlayerQuitEvent event)
	{
		// Simply move the player from the list of registered players to inactive players.
		if (RBCommandExecutor.registeredPlayers.contains(event.getPlayer().getName())) {
			synchronized (RBCommandExecutor.inactiveRegisteredPlayers) {
				RBCommandExecutor.inactiveRegisteredPlayers.add(event.getPlayer().getName());
			}
			synchronized (RBCommandExecutor.registeredPlayers) {
				RBCommandExecutor.registeredPlayers.remove(event.getPlayer().getName());
			}
			plugin.getLogger().info(
			        RandomBattle.prefix + event.getPlayer().getName()
			                + " has been moved to the inactive registered players.");
		}
	}
	
	/**
	 * <p>
	 * Moves players from all players to registered players if they have working Spoutcraft and have
	 * already registered once.
	 * </p>
	 * <p>
	 * This method eliminates the former need of players needing to re-register every time the
	 * server restarts, along with the persistence file of the player list in the data folder.
	 * </p>
	 * @param event
	 *            The SpoutCraftEnableEvent that triggered this method call and contains the player
	 *            to check to see whether to add to the list of registered players.
	 */
	@EventHandler (priority = EventPriority.HIGHEST)
	public void onSpoutcraftEnable(SpoutCraftEnableEvent event)
	{
		// Simply move the player from the list of inactive players to registered players.
		if (RBCommandExecutor.inactiveRegisteredPlayers.contains(event.getPlayer().getName())) {
			synchronized (RBCommandExecutor.registeredPlayers) {
				RBCommandExecutor.registeredPlayers.add(event.getPlayer().getName());
			}
			synchronized (RBCommandExecutor.inactiveRegisteredPlayers) {
				RBCommandExecutor.inactiveRegisteredPlayers.remove(event.getPlayer().getName());
			}
			plugin.getLogger().info(
			        RandomBattle.prefix + event.getPlayer().getName()
			                + " has been moved to the active registered players.");
		}
	}
}
