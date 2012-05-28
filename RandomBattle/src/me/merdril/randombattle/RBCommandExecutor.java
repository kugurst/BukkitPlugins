/**
 * 
 */

package me.merdril.randombattle;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.Set;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.atomic.AtomicBoolean;

import me.merdril.randombattle.battle.BattleSetter;
import me.merdril.randombattle.config.RBOS;

import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.OfflinePlayer;
import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.EntityType;
import org.bukkit.entity.LivingEntity;
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
	private RandomBattle	       plugin;
	/** This is the list of the players currently on the server who have registered. */
	public static Set<String>	   registeredPlayers;
	/**
	 * This is the list of players who have registered and have not unregistered that are currently
	 * not logged in.
	 */
	public static Set<String>	   inactiveRegisteredPlayers;
	/**
	 * This is the String that specifies the file name to use when creating/loading the list of
	 * registered players.
	 */
	public static String	       registeredPlayersFile	= "registeredPlayers.txt";
	private static Set<String>	   deactivatedPlayers;
	public static AtomicBoolean	   hasBeenStopped;
	private static ExecutorService	threadExec;
	
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
			registeredPlayers = Collections.synchronizedSet(new HashSet<String>());
		if (inactiveRegisteredPlayers == null)
			inactiveRegisteredPlayers = Collections.synchronizedSet(new HashSet<String>());
		if (hasBeenStopped == null)
			hasBeenStopped = new AtomicBoolean(false);
		if (threadExec == null)
			threadExec = Executors.newSingleThreadExecutor();
	}
	
	// The global method that sends the command to the appropriate function and
	// handles the case of there not being an appropriate function
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		plugin.getLogger().info(RandomBattle.prefix + Thread.currentThread());
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
		else if (cmd.getName().equalsIgnoreCase("spawnmobs")) {
			return spawnmobs(sender, cmd, label, args);
		}
		return false;
	}
	
	private boolean spawnmobs(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player) {
			Player player = (Player) sender;
			World world = plugin.getServer().getWorld("world");
			ArrayList<LivingEntity> monsters = new ArrayList<LivingEntity>();
			monsters.add(world.spawnCreature(player.getLocation(), EntityType.ENDERMAN));
			monsters.add(world.spawnCreature(player.getLocation(), EntityType.PIG_ZOMBIE));
			monsters.add(world.spawnCreature(player.getLocation(), EntityType.BLAZE));
			monsters.add(world.spawnCreature(player.getLocation(), EntityType.CREEPER));
			monsters.add(world.spawnCreature(player.getLocation(), EntityType.SKELETON));
			monsters.add(world.spawnCreature(player.getLocation(), EntityType.SPIDER));
			monsters.add(world.spawnCreature(player.getLocation(), EntityType.ZOMBIE));
			monsters.add(world.spawnCreature(player.getLocation(), EntityType.IRON_GOLEM));
			for (LivingEntity entity : monsters) {
				plugin.getLogger().info(RandomBattle.prefix + entity.toString());
			}
		}
		return true;
	}
	
	public boolean removeEditedBlocks(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (BattleSetter.allEditedBlockLocations == null || BattleSetter.allEditedBlockLocations.isEmpty()) {
			if (sender != null)
				sender.sendMessage(RandomBattle.prefix + "Nothing to remove.");
			if (sender == null || !(sender instanceof ConsoleCommandSender))
				plugin.getLogger().info("Nothing to remove.");
			return true;
		}
		HashSet<World> affectedWorlds = new HashSet<World>();
		synchronized (BattleSetter.allEditedBlockLocations) {
			while (BattleSetter.allEditedBlockLocations.size() > 0) {
				Location block = BattleSetter.allEditedBlockLocations.get(0);
				affectedWorlds.add(block.getWorld());
				block.getBlock().setType(Material.AIR);
				BattleSetter.allEditedBlockLocations.remove(0);
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
	
	private boolean register(CommandSender sender, Command cmd, String label, String[] args)
	{
		// If the plugin has been "stopped," keep it that way.
		boolean registered = false;
		if (hasBeenStopped.get()) {
			sender.sendMessage(RandomBattle.prefix + "This plugin has been stopped!");
			return registered;
		}
		// The name of the player (obtained from the command)
		String playerName = null;
		// The player to register
		Player player = null;
		if (args.length > 1) {
			sender.sendMessage(RandomBattle.prefix
			        + "This command only takes a single argument (can also take no arguments if called by a player). Ignoring all but the first argument.");
			playerName = args[0];
		}
		// If the sender is the console, use console specific messages and resolutions
		if (sender instanceof ConsoleCommandSender) {
			// If a player wasn't inputted, inform the console of that
			if (args.length < 1) {
				sender.sendMessage(RandomBattle.prefix + "This command requires a player context.");
				return registered;
			}
			// Don't assign it more than necessary
			if (args.length == 1)
				playerName = args[0];
			// At this point, we have a name, so let's get the player
			player = plugin.getServer().getPlayer(playerName);
		}
		// The only other possibility is that the sender is a player (at least for now)
		else {
			// If no arguments are specified, then we assume the player wants to register themselves
			if (args.length == 0)
				player = (Player) sender;
			// If execution reached here, then args[0] is not null
			else if (playerName == null)
				playerName = args[0];
			// If the player hasn't been assigned yet, let's do that. I will not add in the feature
			// of registering OfflinePlayers
			if (player == null)
				player = plugin.getServer().getPlayer(playerName);
		}
		// At this point, player will have been found it if existed
		if (player == null) {
			sender.sendMessage(RandomBattle.prefix + "A player match for the string: " + args[0] + " was not found.");
			return true;
		}
		// If the player existed, let's check to see if he's a SpoutPlayer
		else {
			SpoutPlayer spoutPlayer = (SpoutPlayer) player;
			// If the player is a SpoutCraft player, then let's register it.
			if (spoutPlayer.isSpoutCraftEnabled()) {
				registered = true;
				Boolean newlyRegistered = null;
				synchronized (registeredPlayers) {
					newlyRegistered = registeredPlayers.add(spoutPlayer.getName());
				}
				if (!newlyRegistered) {
					sender.sendMessage(RandomBattle.prefix + spoutPlayer.getName() + " is already registered!");
					return registered;
				}
				else {
					sender.sendMessage(RandomBattle.prefix + spoutPlayer.getName()
					        + " has been successfully registered for Random Battles!");
					threadExec.execute(new Runnable() {
						@Override
						public void run()
						{
							RBOS.saveRegisteredPlayers();
						}
					});
				}
			}
		}
		// Lets inform interested parties about their new status
		if (sender instanceof ConsoleCommandSender)
			// The player that was just registered by console should know
			player.sendMessage(RandomBattle.prefix + "You have been registered for Random Battles by console!");
		else {
			// If the player that was registered isn't the player that called the command
			if (!player.getName().equals(((Player) sender).getName()))
				player.sendMessage(RandomBattle.prefix + "You have been registered for Random Battles by "
				        + sender.getName() + "!");
		}
		return registered;
	}
	
	// This method is slightly more complex as the player can be in active or inactive registered
	// players.
	private boolean unRegister(CommandSender sender, Command cmd, String label, String[] args)
	{
		// If the plugin has been "stopped," keep it that way.
		if (hasBeenStopped.get()) {
			sender.sendMessage(RandomBattle.prefix + "This plugin has been stopped!");
			return false;
		}
		boolean unregistered = false;
		// The name of the player to unregister (obtained from the command)
		String playerName = null;
		// The player to be unregistered
		Player player = null;
		// Informing the sender that we only use one argument at most.
		if (args.length > 1) {
			sender.sendMessage(RandomBattle.prefix
			        + "This command only takes a single argument (can also take no arguments if called by a player). Ignoring all but the first argument.");
			playerName = args[0];
		}
		// The control path for a console sender
		if (sender instanceof ConsoleCommandSender) {
			// Insufficient arguments were supplied
			if (args.length < 1) {
				sender.sendMessage(RandomBattle.prefix + "This command requires a player context.");
				return unregistered;
			}
			// At this point, we know args[0] exists
			playerName = args[0];
		}
		// The control path for a player sender
		else {
			// If there was no argument, then the player wants to unregister himself, and this just
			// got easy
			if (args.length == 0) {
				unregistered = true;
				player = (Player) sender;
				if (registeredPlayers.contains(player.getName())) {
					sender.sendMessage(RandomBattle.prefix + "You have been unregistered from Random Battles!");
					synchronized (registeredPlayers) {
						registeredPlayers.remove(player.getName());
					}
					threadExec.execute(new Runnable() {
						@Override
						public void run()
						{
							RBOS.saveRegisteredPlayers();
						}
					});
				}
				// The player was not registered (online players should not be in inactive players)
				else
					sender.sendMessage(RandomBattle.prefix + "You were not registered for Random Battles.");
				return true;
			}
			// Otherwise, let's get this player to unregister
			else
				playerName = args[0];
		}
		// At this point, playerName is not null, so lets see if the player is online
		OfflinePlayer temp = getPlayer(plugin.getServer().getOnlinePlayers(), playerName, sender);
		if (temp != null)
			player = temp.getPlayer();
		// If the player is null, let's try searching through the offline players
		if (player == null) {
			OfflinePlayer offlinePlayer = getPlayer(plugin.getServer().getOfflinePlayers(), playerName, sender);
			// If the player is still null, then let's return
			if (offlinePlayer == null)
				sender.sendMessage(RandomBattle.prefix + "Unable to get a match for: " + playerName);
			// The offlinePlayer is not null, so lets remove it from the inactive players if it is
			// there
			else {
				if (inactiveRegisteredPlayers.contains(offlinePlayer.getName())) {
					sender.sendMessage(RandomBattle.prefix + offlinePlayer.getName()
					        + " has been unregistered from Random Battles.");
					unregistered = true;
					synchronized (inactiveRegisteredPlayers) {
						inactiveRegisteredPlayers.remove(offlinePlayer.getName());
					}
					threadExec.execute(new Runnable() {
						@Override
						public void run()
						{
							RBOS.saveRegisteredPlayers();
						}
					});
				}
				else
					sender.sendMessage(RandomBattle.prefix + offlinePlayer.getName()
					        + " is not registered for Random Battles!");
			}
		}
		// The player is not null, so lets remove it from the active registered players if it is
		// there
		else {
			if (registeredPlayers.contains(player.getName())) {
				sender.sendMessage(RandomBattle.prefix + player.getName()
				        + " has been unregistered from Random Battles.");
				unregistered = true;
				synchronized (registeredPlayers) {
					registeredPlayers.remove(player.getName());
				}
				threadExec.execute(new Runnable() {
					@Override
					public void run()
					{
						RBOS.saveRegisteredPlayers();
					}
				});
			}
			else
				sender.sendMessage(RandomBattle.prefix + player.getName() + " is not registered for Random Battles!");
		}
		// If the player was unregistered, let's inform the interested party
		if (unregistered) {
			if (player != null) {
				if (sender instanceof ConsoleCommandSender)
					player.sendMessage(RandomBattle.prefix
					        + "You have been unregistered from Random Battles by console!");
				// It was a player who sent it
				else {
					// If the player is not the one who issued the command
					if (!player.getName().equals(((Player) sender).getName()))
						player.sendMessage(RandomBattle.prefix + "You have been unregistered from Random Battles by "
						        + sender.getName() + "!");
				}
			}
		}
		return true;
	}
	
	// Returns an OfflinePlayer if a perfect match was found between the inputted playerName and the
	// array of OfflinePlayers. Null otherwise (including if multiple possible matches are found).
	private OfflinePlayer getPlayer(OfflinePlayer[] offlinePlayers, String playerName, CommandSender sender)
	{
		OfflinePlayer player = null;
		// Do a case insensitive search (minecraft registration rules)
		for (OfflinePlayer offlinePlayer : offlinePlayers)
			if (offlinePlayer.getName().equalsIgnoreCase(playerName))
				return offlinePlayer;
		// Finally, lets do a soft search and softer search, where we ignore case and look for
		// pattern matches.
		ArrayList<OfflinePlayer> softerPlayers = new ArrayList<OfflinePlayer>();
		ArrayList<OfflinePlayer> softPlayers = new ArrayList<OfflinePlayer>();
		for (OfflinePlayer offlinePlayer : offlinePlayers) {
			if (offlinePlayer.getName().toLowerCase().contains(playerName.toLowerCase()))
				softerPlayers.add(offlinePlayer);
			if (offlinePlayer.getName().toLowerCase().startsWith(playerName.toLowerCase()))
				softPlayers.add(offlinePlayer);
		}
		// If no match was found, don't bother going further
		if (softerPlayers.size() == 0) // softerPlayer contains anything softPlayers would contain
			return player;
		// Lets check to see if we got a perfect match
		if (softPlayers.size() == 1)
			return softPlayers.get(0);
		else if (softerPlayers.size() == 1)
			return softerPlayers.get(0);
		// At this point, we can't disambiguate what the command sender wanted, so lets show them
		// the possible matches, and move on with our lives.
		sender.sendMessage(RandomBattle.prefix + "Possible matches (in offline players): "
		        + softerPlayers.toString().replace("[", "").replace("]", "") + ". Please be more specific.");
		return player;
	}
	
	private boolean debugRegPlayers(CommandSender sender, Command cmd, String label, String[] args)
	{
		ArrayList<SpoutPlayer> regPlayers = new ArrayList<SpoutPlayer>();
		for (String playerName : registeredPlayers) {
			regPlayers.add(RBUtilities.getSpoutPlayerFromDisplayName(playerName, 0, plugin.getServer()
			        .getConsoleSender()));
		}
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
		deactivatedPlayers = registeredPlayers;
		registeredPlayers = new HashSet<String>();
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
		registeredPlayers = deactivatedPlayers;
		hasBeenStopped.set(false);
		sender.sendMessage(RandomBattle.prefix + "RandomBattle has resumed.");
		if (!(sender instanceof ConsoleCommandSender))
			plugin.getLogger().info(RandomBattle.prefix + "RandomBattle has resumed.");
		return true;
	}
}
// The following is the old code for register(...):
/*
 * if (sender instanceof Player) { boolean isSpoutPlayer = false; if (args.length == 0)
 * isSpoutPlayer = ((SpoutPlayer) sender).isSpoutCraftEnabled(); else if (args.length == 1) { Player
 * player = plugin.getServer().getPlayer(args[0]); if (player == null) isSpoutPlayer = false; else
 * isSpoutPlayer = ((SpoutPlayer) player).isSpoutCraftEnabled(); } else if (args.length > 1) {
 * sender.sendMessage(RandomBattle.prefix + "Too many arguments"); return false; } if
 * (!isSpoutPlayer) { sender.sendMessage(RandomBattle.prefix +
 * "The player name does not refer to a SpoutCraft player."); return true; } else { if (args.length
 * == 0) { registeredPlayers.add(sender.getName()); sender.sendMessage(RandomBattle.prefix +
 * sender.getName() + " is now registered!"); return true; } else if (sender.getName() == args[0]) {
 * registeredPlayers.add(sender.getName()); sender.sendMessage(RandomBattle.prefix +
 * sender.getName() + " is now registered!"); return true; } else {
 * registeredPlayers.add(RBUtilities.getSpoutPlayerFromDisplayName(args[0], 0, sender).getName());
 * sender.sendMessage(RandomBattle.prefix + args[0] + " is now registered!");
 * RBUtilities.getSpoutPlayerFromDisplayName(args[0], 0, sender).sendMessage( RandomBattle.prefix +
 * "You have been registered for Random Battles by " + sender.getName() + "!"); return true; } } }
 * else { if (args.length == 0) { sender.sendMessage(RandomBattle.prefix +
 * "This command requires a player context."); return true; } else if (args.length > 1) {
 * sender.sendMessage(RandomBattle.prefix + "Too many arguments."); return false; } else { Player
 * player = plugin.getServer().getPlayer(args[0]); boolean isSpoutPlayer; if (player == null)
 * isSpoutPlayer = false; else isSpoutPlayer = ((SpoutPlayer) player).isSpoutCraftEnabled(); if
 * (!isSpoutPlayer) { sender.sendMessage(RandomBattle.prefix +
 * "The player name does not refer to a SpoutCraft player."); return true; }
 * registeredPlayers.add(RBUtilities.getSpoutPlayerFromDisplayName(args[0], 0, sender).getName());
 * sender.sendMessage(RandomBattle.prefix + args[0] + " is now registered!");
 * RBUtilities.getSpoutPlayerFromDisplayName(args[0], 0, sender).sendMessage( RandomBattle.prefix +
 * "You have been registered for Random Battles by console!"); return true; } }
 */
// The following is the old code for unRegister(...):
/*
 * if (sender instanceof Player) { boolean isRegisteredPlayer =
 * registeredPlayers.contains(((SpoutPlayer) sender).getName()); if (args.length == 1) {
 * isRegisteredPlayer = registeredPlayers.contains(args[0]); if (!isRegisteredPlayer) {
 * sender.sendMessage(RandomBattle.prefix + args[0] + " is not registered for Random Battles.");
 * return true; } SpoutPlayer player = RBUtilities.getSpoutPlayerFromDisplayName(args[0], 0,
 * sender); registeredPlayers.remove(args[0]); sender.sendMessage(RandomBattle.prefix +
 * player.getDisplayName() + " has been unregistered from Random Battles."); if
 * (!sender.getName().equals(args[0])) player.sendMessage(RandomBattle.prefix +
 * "You have been unregistered from Random Battles by " + sender.getName() + "."); return true; }
 * else if (args.length > 1) { sender.sendMessage(RandomBattle.prefix +
 * "Incorrect arguments. Usage:"); return false; } if (!isRegisteredPlayer) {
 * sender.sendMessage(RandomBattle.prefix + sender.getName() +
 * " is not registered for Random Battles."); return true; } // At this point, the player is
 * registered. SpoutPlayer player = RBUtilities.getSpoutPlayerFromDisplayName(sender.getName(), 0,
 * sender); registeredPlayers.remove(sender.getName()); sender.sendMessage(RandomBattle.prefix +
 * player.getDisplayName() + " has been unregistered from Random Battles."); return true; } //
 * Command line issued if (args.length != 1) { sender.sendMessage(RandomBattle.prefix +
 * "This command takes one player as an argument. Usage:"); return false; } if
 * (registeredPlayers.contains(args[0])) { SpoutPlayer player =
 * RBUtilities.getSpoutPlayerFromDisplayName(args[0], 0, sender); registeredPlayers.remove(args[0]);
 * sender.sendMessage(RandomBattle.prefix + player.getDisplayName() +
 * " has been unregistered from Random Battles."); player.sendMessage(RandomBattle.prefix +
 * "You have been unregistered from Random Battles by console."); }
 */
