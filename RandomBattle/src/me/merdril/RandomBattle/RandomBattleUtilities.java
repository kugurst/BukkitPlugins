/**
 * 
 */

package me.merdril.RandomBattle;

import java.util.ArrayList;

import org.bukkit.command.CommandSender;
import org.getspout.spoutapi.SpoutManager;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public final class RandomBattleUtilities
{
	/**
	 * 
	 */
	private RandomBattleUtilities()
	{
		throw new AssertionError();
	}
	
	/**
	 * <code>public static SpoutPlayer getSpoutPlayerFromDisplayName(String displayName)</code> <br/>
	 * <br/>
	 * Return a SpoutPlayer with the given name.
	 * 
	 * @param displayName
	 *            - A string containg the display name.
	 * @param searchType
	 *            - An int denoting the type of search to perform (0 for perfect match, 1 for
	 *            case-insensitive, 2 for soft)
	 * @return A SpoutPlayer, or null if a player matching the given name was not found.
	 */
	public static SpoutPlayer getSpoutPlayerFromDisplayName(String displayName, int searchType,
	        CommandSender sender)
	{
		ArrayList<SpoutPlayer> possiblePlayers = new ArrayList<SpoutPlayer>();
		if (searchType == 0)
		{
			for (SpoutPlayer player : SpoutManager.getOnlinePlayers())
				if (player.getDisplayName().equals(displayName))
					return player;
		}
		else if (searchType == 1)
		{
			for (SpoutPlayer player : SpoutManager.getOnlinePlayers())
				if (player.getDisplayName().equalsIgnoreCase(displayName))
					possiblePlayers.add(player);
		}
		else if (searchType == 2)
		{
			for (SpoutPlayer player : SpoutManager.getOnlinePlayers())
				if (player.getDisplayName().toLowerCase().startsWith(displayName.toLowerCase()))
					possiblePlayers.add(player);
		}
		if (possiblePlayers.size() == 1)
			return possiblePlayers.get(0);
		sender.sendMessage("[RandomBattle] Found the following matches: ");
		for (SpoutPlayer player : possiblePlayers)
			sender.sendMessage("[RandomBattle] " + player.getDisplayName());
		sender.sendMessage("[RandomBattle] Please use an exact match on the player you want: ");
		return null;
	}
}