/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import me.merdril.RandomBattle.RandomBattle;

import org.getspout.spoutapi.gui.InGameScreen;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public class RandomBattleHUD
{
	RandomBattle	               plugin;
	private InGameScreen	       playerScreen;
	private PlayerSelectionWidgets	actionButtons;
	
	/**
	 * @param player
	 * 
	 */
	public RandomBattleHUD(RandomBattle instance, SpoutPlayer player)
	{
		this.plugin = instance;
		playerScreen = (InGameScreen) player.getMainScreen();
		actionButtons = new PlayerSelectionWidgets(plugin, player, playerScreen);
	}
	
}
