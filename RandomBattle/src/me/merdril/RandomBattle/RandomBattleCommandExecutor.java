/**
 * 
 */

package me.merdril.RandomBattle;

import java.util.ArrayList;
import java.util.HashMap;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public class RandomBattleCommandExecutor implements CommandExecutor
{
	public RandomBattle	                plugin;
	static HashMap<String, SpoutPlayer>	registeredPlayers	= new HashMap<String, SpoutPlayer>();
	
	/**
	 * 
	 */
	public RandomBattleCommandExecutor(RandomBattle instance)
	{
		plugin = instance;
	}
	
	// The global method that sends the command to the appropriate function and handles the case of
	// there not being an appropriate function
	@Override
	public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (cmd.getName().equalsIgnoreCase("regbattle"))
			return register(sender, cmd, label, args);
		else if (cmd.getName().equalsIgnoreCase("unregbattle"))
			return unRegister(sender, cmd, label, args);
		else if (cmd.getName().equalsIgnoreCase("stopbattles"))
			return stop(sender, cmd, label, args);
		else if (cmd.getName().equalsIgnoreCase("resumebattles"))
			return start(sender, cmd, label, args);
		else if (cmd.getName().equalsIgnoreCase("showregplayers"))
			return debugRegPlayers(sender, cmd, label, args);
		else if (cmd.getName().equalsIgnoreCase("showspoutplayers"))
			return debugSpoutPlayers(sender, cmd, label, args);
		return false;
	}
	
	private boolean register(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			boolean isSpoutPlayer = false;
			if (args.length == 0)
				isSpoutPlayer =
				        RandomBattleSpoutListener.spoutPlayers.containsKey(((Player) sender)
				                .getDisplayName());
			else if (args.length == 1)
				isSpoutPlayer = RandomBattleSpoutListener.spoutPlayers.containsKey(args[0]);
			else if (args.length > 1)
			{
				sender.sendMessage("[RandomBattle] Too many arguments");
				return false;
			}
			if (!isSpoutPlayer)
			{
				sender.sendMessage("[RandomBattle] The player name does not refer to a SpoutCraft player.");
				return true;
			}
			else
			{
				if (args.length == 0)
				{
					registeredPlayers.put(sender.getName(), (SpoutPlayer) sender);
					sender.sendMessage("[RandomBattle] " + sender.getName() + " is now registered!");
					return true;
				}
				else if (sender.getName() == args[0])
				{
					registeredPlayers.put(sender.getName(), (SpoutPlayer) sender);
					sender.sendMessage("[RandomBattle] " + sender.getName() + " is now registered!");
					return true;
				}
				else
				{
					registeredPlayers
					        .put(args[0], RandomBattleUtilities.getSpoutPlayerFromDisplayName(
					                args[0], 0, sender));
					sender.sendMessage("[RandomBattle] " + args[0] + " is now registered!");
					registeredPlayers.get(args[0]).sendMessage(
					        "[RandomBattle] You have been registered for Random Battles by "
					                + sender.getName() + "!");
					return true;
				}
			}
		}
		else
		{
			if (args.length == 0)
			{
				sender.sendMessage("[RandomBattle] This command requires a player context.");
				return true;
			}
			else if (args.length > 1)
			{
				sender.sendMessage("[RandomBattle] Too many arguments.");
				return false;
			}
			else
			{
				boolean isSpoutPlayer = RandomBattleSpoutListener.spoutPlayers.containsKey(args[0]);
				if (!isSpoutPlayer)
				{
					sender.sendMessage("[RandomBattle] The player name does not refer to a SpoutCraft player.");
					return true;
				}
				registeredPlayers.put(args[0],
				        RandomBattleUtilities.getSpoutPlayerFromDisplayName(args[0], 0, sender));
				sender.sendMessage("[RandomBattle] " + args[0] + " is now registered!");
				registeredPlayers.get(args[0]).sendMessage(
				        "[RandomBattle] You have been registered for Random Battles by console!");
				return true;
			}
		}
	}
	
	private boolean unRegister(CommandSender sender, Command cmd, String label, String[] args)
	{
		if (sender instanceof Player)
		{
			boolean isRegisteredPlayer = registeredPlayers.containsValue((SpoutPlayer) sender);
			if (args.length == 1)
			{
				isRegisteredPlayer = registeredPlayers.containsKey(args[0]);
				if (!isRegisteredPlayer)
				{
					sender.sendMessage("[RandomBattle] " + args[0]
					        + " is not registered for Random Battles.");
					return true;
				}
				SpoutPlayer player = registeredPlayers.remove(args[0]);
				sender.sendMessage("[RandomBattle] " + player.getDisplayName()
				        + " has been unregistered from Random Battles.");
				if (!sender.getName().equals(args[0]))
					player.sendMessage("[RandomBattle] You have been unregistered from Random Battles by "
					        + sender.getName() + ".");
				return true;
			}
			else if (args.length > 1)
			{
				sender.sendMessage("[RandomBattle] Incorrect arguments. Usage:");
				return false;
			}
			if (!isRegisteredPlayer)
			{
				sender.sendMessage("[RandomBattle] " + sender.getName()
				        + " is not registered for Random Battles.");
				return true;
			}
			// At this point, the player is registered.
			SpoutPlayer player = registeredPlayers.remove(sender.getName());
			sender.sendMessage("[RandomBattle] " + player.getDisplayName()
			        + " has been unregistered from Random Battles.");
			return true;
		}
		// Command line issued
		if (args.length != 1)
		{
			sender.sendMessage("[RandomBattle] This command takes one player as an argument. Usage:");
			return false;
		}
		if (registeredPlayers.containsKey(args[0]))
		{
			SpoutPlayer player = registeredPlayers.remove(args[0]);
			sender.sendMessage("[RandomBattle] " + player.getDisplayName()
			        + " has been unregistered from Random Battles.");
			player.sendMessage("[RandomBattle] You have been unregistered from Random Battles by console.");
		}
		return true;
	}
	
	private boolean debugRegPlayers(CommandSender sender, Command cmd, String label, String[] args)
	{
		ArrayList<SpoutPlayer> regPlayers = new ArrayList<SpoutPlayer>(registeredPlayers.values());
		for (SpoutPlayer player : regPlayers)
			sender.sendMessage("[RandomBattle] " + player.getDisplayName());
		return true;
	}
	
	private boolean
	        debugSpoutPlayers(CommandSender sender, Command cmd, String label, String[] args)
	{
		ArrayList<SpoutPlayer> spoutPlayers =
		        new ArrayList<SpoutPlayer>(RandomBattleSpoutListener.spoutPlayers.values());
		for (SpoutPlayer player : spoutPlayers)
			sender.sendMessage("[RandomBattle] " + player.getDisplayName());
		return true;
	}
	
	private boolean stop(CommandSender sender, Command cmd, String label, String[] args)
	{
		return false;
		
	}
	
	private boolean start(CommandSender sender, Command cmd, String label, String[] args)
	{
		return false;
		
	}
}
