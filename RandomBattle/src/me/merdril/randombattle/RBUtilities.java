/**
 * 
 */

package me.merdril.randombattle;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.Set;

import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 */
public final class RBUtilities
{
	/** The Block IDs that will be considered weapons along with their damage rank */
	public static int[]	          weaponIDs	  = {267, 268, 272, 276, 283, 258, 271, 275, 279, 286};
	/**
	 * The various attributes of an entity. Should be obvious to anyone familiar with RPGs
	 * (c=current)
	 */
	public static HashSet<String>	statNames	= new HashSet<String>();
	static {
		statNames.add("hp");
		statNames.add("mp");
		statNames.add("chp");
		statNames.add("cmp");
		statNames.add("str");
		statNames.add("mag");
		statNames.add("def");
		statNames.add("mdef");
		statNames.add("agl");
		statNames.add("acc");
		statNames.add("eva");
		statNames.add("luck");
		statNames.add("skills");
		statNames.add("magicks");
		statNames.add("name");
		statNames.add("exp");
	};
	
	/* Being a utility class, there's no reason to instantiate. None at all */
	private RBUtilities() throws AssertionError
	{
		throw new AssertionError();
	}
	
	/**
	 * <code>public static SpoutPlayer getSpoutPlayerFromDisplayName(String displayName)</code> <br/>
	 * <br/>
	 * Return a SpoutPlayer with the given name.
	 * @param displayName
	 *            - A string containg the display name.
	 * @param searchType
	 *            - An int denoting the type of search to perform (0 for perfect match, 1 for
	 *            case-insensitive, 2 for soft)
	 * @return A SpoutPlayer, or null if a player matching the given name was not found.
	 */
	public static SpoutPlayer getSpoutPlayerFromDisplayName(String displayName, int searchType, CommandSender sender)
	{
		ArrayList<SpoutPlayer> possiblePlayers = new ArrayList<SpoutPlayer>();
		if (searchType == 0) {
			for (SpoutPlayer player : SpoutManager.getOnlinePlayers())
				if (player.getDisplayName().equals(displayName))
					return player;
		}
		else if (searchType == 1) {
			for (SpoutPlayer player : SpoutManager.getOnlinePlayers())
				if (player.getDisplayName().equalsIgnoreCase(displayName))
					possiblePlayers.add(player);
		}
		else if (searchType == 2) {
			for (SpoutPlayer player : SpoutManager.getOnlinePlayers())
				if (player.getDisplayName().toLowerCase().startsWith(displayName.toLowerCase()))
					possiblePlayers.add(player);
		}
		if (possiblePlayers.size() == 1)
			return possiblePlayers.get(0);
		else if (possiblePlayers.size() == 0) {
			if (sender != null)
				sender.sendMessage(RandomBattle.prefix + "No match found.");
			return null;
		}
		if (sender != null)
			sender.sendMessage(RandomBattle.prefix + "Found the following matches: ");
		if (sender != null)
			for (SpoutPlayer player : possiblePlayers)
				sender.sendMessage(RandomBattle.prefix + player.getDisplayName());
		if (sender != null)
			sender.sendMessage(RandomBattle.prefix + "Please use an exact match on the player you want.");
		return null;
	}
	
	/**
	 * <code>public static boolean isRegisteredPlayer(String displayName, int searchType)</code> <br/>
	 * <br/>
	 * Finds out if a player is registered.
	 * @param name
	 *            - The name to search for.
	 * @param searchType
	 *            - The type of search to perform. 0 for exact match, 1 for case insensitive, 2 for
	 *            soft search
	 * @return True if the player is registered. False otherwise.
	 */
	public static boolean isRegisteredPlayer(String name, int searchType, CommandSender sender)
	{
		ArrayList<String> possiblePlayers = new ArrayList<String>();
		Set<String> allPlayers = RBCommandExecutor.registeredPlayers;
		if (searchType == 0) {
			for (String playerName : allPlayers)
				if (playerName.equals(name))
					return true;
			return false;
		}
		else if (searchType == 1) {
			for (String playerName : allPlayers)
				if (playerName.equalsIgnoreCase(name))
					possiblePlayers.add(playerName);
		}
		else if (searchType == 2) {
			for (String playerName : allPlayers)
				if (playerName.toLowerCase().startsWith(name.toLowerCase()))
					possiblePlayers.add(playerName);
		}
		if (possiblePlayers.size() == 0) {
			sender.sendMessage(RandomBattle.prefix + "No match found.");
			return false;
		}
		else if (possiblePlayers.size() == 1)
			return true;
		else {
			sender.sendMessage(RandomBattle.prefix + "The following matches were found:");
			for (String player : possiblePlayers)
				sender.sendMessage(RandomBattle.prefix + player);
			return false;
		}
	}
}
