/**
 * 
 */

package me.merdril.randombattle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.concurrent.atomic.AtomicBoolean;

import me.merdril.randombattle.battle.BattleSetter;

import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.block.Block;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * <p>
 * Handles the execution of all commands related to this plugin, of which there are a few, with most
 * being for debugging purposes.
 * </p>
 * <p>
 * This class serves as a way to debug the functionality of this plugin, and will eventually support
 * stopping and starting this plugin all together.
 * </p>
 * @author Merdril
 */
public class RBCommandExecutor implements CommandExecutor
{
	public RandomBattle	                    plugin;
	static Map<String, SpoutPlayer>	        registeredPlayers;
	private static Map<String, SpoutPlayer>	deactivatedPlayerMap;
	private AtomicBoolean	                hasBeenStopped;
	
	/**
	 * <p>
	 * Creates the mapping of registered players in the server. Maps a player's name to a player
	 * (easier than a HashSet for unregistering, and it doesn't complicate registering too much).
	 * </p>
	 */
	public RBCommandExecutor(RandomBattle instance)
	{
		plugin = instance;
		if (registeredPlayers == null)
			registeredPlayers = Collections.synchronizedMap(new HashMap<String, SpoutPlayer>());
		if (hasBeenStopped == null)
			hasBeenStopped = new AtomicBoolean(false);
	}
	
	// The global method that sends the command to the appropriate function and
	// handles the case of there not being an appropriate function
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("regbattle")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("randombattle.register"))
					return register(sender, cmd, label, args);
				else {
					sender.sendMessage("&c" + RandomBattle.prefix
					        + "You do not have permission to execute this command.");
					return true;
				}
			}
			else
				return register(sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("unregbattle")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("randombattle.unregister"))
					return unRegister(sender, cmd, label, args);
				else {
					sender.sendMessage("&c" + RandomBattle.prefix
					        + "You do not have permission to execute this command.");
					return true;
				}
			}
			else
				return unRegister(sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("stopbattles")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("randombattle.battle"))
					return stop(sender, cmd, label, args);
				else {
					sender.sendMessage("&c" + RandomBattle.prefix
					        + "You do not have permission to execute this command.");
					return true;
				}
			}
			else
				return stop(sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("resumebattles")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("randombattle.battle"))
					return start(sender, cmd, label, args);
				else {
					sender.sendMessage("&c" + RandomBattle.prefix
					        + "You do not have permission to execute this command.");
					return true;
				}
			}
			else
				return start(sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("showregplayers")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("randombattle.debug"))
					return debugRegPlayers(sender, cmd, label, args);
				else {
					sender.sendMessage("&c" + RandomBattle.prefix
					        + "You do not have permission to execute this command.");
					return true;
				}
			}
			else
				return debugRegPlayers(sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("showspoutplayers")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("randombattle.debug"))
					return debugSpoutPlayers(sender, cmd, label, args);
				else {
					sender.sendMessage("&c" + RandomBattle.prefix
					        + "You do not have permission to execute this command.");
					return true;
				}
			}
			else
				return debugSpoutPlayers(sender, cmd, label, args);
		}
		else if (cmd.getName().equalsIgnoreCase("removeblocks")) {
			if (sender instanceof Player) {
				if (sender.hasPermission("randombattle.removeblocks"))
					return removeEditedBlocks(sender, cmd, label, args);
				else {
					sender.sendMessage("&c" + RandomBattle.prefix
					        + "You do not have permission to execute this command.");
					return true;
				}
			}
			else
				return removeEditedBlocks(sender, cmd, label, args);
		}
		return false;
	}
	
	public boolean removeEditedBlocks(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (BattleSetter.allEditedBlocks == null || BattleSetter.allEditedBlocks.isEmpty()) {
			if (sender != null)
				sender.sendMessage(RandomBattle.prefix + "Nothing to remove.");
			if (sender == null || !(sender instanceof ConsoleCommandSender))
				plugin.getLogger().info("Nothing to remove.");
			return true;
		}
		HashSet<World> affectedWorlds = new HashSet<World>();
		synchronized (BattleSetter.allEditedBlocks) {
			while (BattleSetter.allEditedBlocks.size() > 0) {
				Block block = BattleSetter.allEditedBlocks.get(0);
				affectedWorlds.add(block.getWorld());
				block.setType(Material.AIR);
				BattleSetter.allEditedBlocks.remove(0);
			}
		}
		for (World world : affectedWorlds)
			world.save();
		if (sender != null)
			sender.sendMessage(RandomBattle.prefix + "All modified blocks removed.");
		if (sender == null || !(sender instanceof ConsoleCommandSender))
			plugin.getLogger().info("All modified blocks removed.");
		return true;
	}
	
	/**
	 * <p>
	 * Determines whether or not the specified player is a player or not.
	 * </p>
	 * @param sender
	 * @param cmd
	 * @param label
	 * @param args
	 * @return
	 */
	private boolean register(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (hasBeenStopped.get()) {
			sender.sendMessage(RandomBattle.prefix + "This plugin has been stopped!");
			return false;
		}
		if (sender instanceof Player) {
			boolean isSpoutPlayer = false;
			if (args.length == 0)
				isSpoutPlayer = ((SpoutPlayer) sender).isSpoutCraftEnabled();
			else if (args.length == 1) {
				Player player = plugin.getServer().getPlayer(args[0]);
				if (player == null)
					isSpoutPlayer = false;
				else
					isSpoutPlayer = ((SpoutPlayer) player).isSpoutCraftEnabled();
			}
			else if (args.length > 1) {
				sender.sendMessage(RandomBattle.prefix + "Too many arguments");
				return false;
			}
			if (!isSpoutPlayer) {
				sender.sendMessage(RandomBattle.prefix + "The player name does not refer to a SpoutCraft player.");
				return true;
			}
			else {
				if (args.length == 0) {
					registeredPlayers.put(sender.getName(), (SpoutPlayer) sender);
					sender.sendMessage(RandomBattle.prefix + sender.getName() + " is now registered!");
					return true;
				}
				else if (sender.getName() == args[0]) {
					registeredPlayers.put(sender.getName(), (SpoutPlayer) sender);
					sender.sendMessage(RandomBattle.prefix + sender.getName() + " is now registered!");
					return true;
				}
				else {
					registeredPlayers.put(args[0], RBUtilities.getSpoutPlayerFromDisplayName(args[0], 0, sender));
					sender.sendMessage(RandomBattle.prefix + args[0] + " is now registered!");
					registeredPlayers.get(args[0]).sendMessage(
					        RandomBattle.prefix + "You have been registered for Random Battles by " + sender.getName()
					                + "!");
					return true;
				}
			}
		}
		else {
			if (args.length == 0) {
				sender.sendMessage(RandomBattle.prefix + "This command requires a player context.");
				return true;
			}
			else if (args.length > 1) {
				sender.sendMessage(RandomBattle.prefix + "Too many arguments.");
				return false;
			}
			else {
				Player player = plugin.getServer().getPlayer(args[0]);
				boolean isSpoutPlayer;
				if (player == null)
					isSpoutPlayer = false;
				else
					isSpoutPlayer = ((SpoutPlayer) player).isSpoutCraftEnabled();
				if (!isSpoutPlayer) {
					sender.sendMessage(RandomBattle.prefix + "The player name does not refer to a SpoutCraft player.");
					return true;
				}
				registeredPlayers.put(args[0], RBUtilities.getSpoutPlayerFromDisplayName(args[0], 0, sender));
				sender.sendMessage(RandomBattle.prefix + args[0] + " is now registered!");
				registeredPlayers.get(args[0]).sendMessage(
				        RandomBattle.prefix + "You have been registered for Random Battles by console!");
				return true;
			}
		}
	}
	
	private boolean unRegister(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (hasBeenStopped.get()) {
			sender.sendMessage(RandomBattle.prefix + "This plugin has been stopped!");
			return false;
		}
		if (sender instanceof Player) {
			boolean isRegisteredPlayer = registeredPlayers.containsValue((SpoutPlayer) sender);
			if (args.length == 1) {
				isRegisteredPlayer = registeredPlayers.containsKey(args[0]);
				if (!isRegisteredPlayer) {
					sender.sendMessage(RandomBattle.prefix + args[0] + " is not registered for Random Battles.");
					return true;
				}
				SpoutPlayer player = registeredPlayers.remove(args[0]);
				sender.sendMessage(RandomBattle.prefix + player.getDisplayName()
				        + " has been unregistered from Random Battles.");
				if (!sender.getName().equals(args[0]))
					player.sendMessage(RandomBattle.prefix + "You have been unregistered from Random Battles by "
					        + sender.getName() + ".");
				return true;
			}
			else if (args.length > 1) {
				sender.sendMessage(RandomBattle.prefix + "Incorrect arguments. Usage:");
				return false;
			}
			if (!isRegisteredPlayer) {
				sender.sendMessage(RandomBattle.prefix + sender.getName() + " is not registered for Random Battles.");
				return true;
			}
			// At this point, the player is registered.
			SpoutPlayer player = registeredPlayers.remove(sender.getName());
			sender.sendMessage(RandomBattle.prefix + player.getDisplayName()
			        + " has been unregistered from Random Battles.");
			return true;
		}
		// Command line issued
		if (args.length != 1) {
			sender.sendMessage(RandomBattle.prefix + "This command takes one player as an argument. Usage:");
			return false;
		}
		if (registeredPlayers.containsKey(args[0])) {
			SpoutPlayer player = registeredPlayers.remove(args[0]);
			sender.sendMessage(RandomBattle.prefix + player.getDisplayName()
			        + " has been unregistered from Random Battles.");
			player.sendMessage(RandomBattle.prefix + "You have been unregistered from Random Battles by console.");
		}
		return true;
	}
	
	private boolean debugRegPlayers(CommandSender sender, Command cmd, String label, String[] args)
	{
		ArrayList<SpoutPlayer> regPlayers = new ArrayList<SpoutPlayer>(registeredPlayers.values());
		if (regPlayers.size() == 0)
			sender.sendMessage(RandomBattle.prefix + "No registered players!");
		for (SpoutPlayer player : regPlayers)
			sender.sendMessage(RandomBattle.prefix + player.getDisplayName());
		return true;
	}
	
	private boolean debugSpoutPlayers(CommandSender sender, Command cmd, String label, String[] args)
	{
		ArrayList<SpoutPlayer> spoutPlayers = new ArrayList<SpoutPlayer>();
		for (Player player : plugin.getServer().getOnlinePlayers())
			spoutPlayers.add((SpoutPlayer) player);
		if (spoutPlayers.size() == 0)
			sender.sendMessage(RandomBattle.prefix + "No Spout players!");
		for (SpoutPlayer player : spoutPlayers)
			sender.sendMessage(RandomBattle.prefix + player.getDisplayName());
		return true;
	}
	
	private synchronized boolean stop(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (hasBeenStopped.get()) {
			sender.sendMessage(RandomBattle.prefix + "RandomBattle has already been halted.");
			return false;
		}
		deactivatedPlayerMap = registeredPlayers;
		registeredPlayers = new HashMap<String, SpoutPlayer>();
		hasBeenStopped.set(true);
		sender.sendMessage(RandomBattle.prefix + "RandomBattle has halted.");
		if (!(sender instanceof ConsoleCommandSender))
			plugin.getLogger().info(RandomBattle.prefix + "RandomBattle has halted.");
		return true;
	}
	
	private synchronized boolean start(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (!hasBeenStopped.get()) {
			sender.sendMessage(RandomBattle.prefix + "RandomBattle is already running.");
			return false;
		}
		registeredPlayers = deactivatedPlayerMap;
		hasBeenStopped.set(false);
		sender.sendMessage(RandomBattle.prefix + "RandomBattle has resumed.");
		if (!(sender instanceof ConsoleCommandSender))
			plugin.getLogger().info(RandomBattle.prefix + "RandomBattle has resumed.");
		return true;
	}
}
