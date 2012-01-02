/**
 * 
 */

package me.merdril.RandomBattle;

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
	 * @return A SpoutPlayer, or null if a player matching the given name was not found.
	 */
	public static SpoutPlayer getSpoutPlayerFromDisplayName(String displayName)
	{
		for (SpoutPlayer player : SpoutManager.getOnlinePlayers())
			if (player.getDisplayName().equals(displayName))
				return player;
		return null;
	}
}
