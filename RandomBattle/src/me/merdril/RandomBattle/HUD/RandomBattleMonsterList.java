/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import java.util.ArrayList;

import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.Monster;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.InGameHUD;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mark
 * 
 */
public class RandomBattleMonsterList extends GenericListWidget
{
	RandomBattle	  plugin;
	private InGameHUD	playerScreen;
	
	/**
	 * 
	 */
	public RandomBattleMonsterList(RandomBattle instance, SpoutPlayer player,
	        ArrayList<Monster> monsterList)
	{
		plugin = instance;
		playerScreen = player.getMainScreen();
		for (Monster monster : monsterList)
		{
			String monsterName = monster.toString();
			if (monsterName.contains("Craft"))
				monsterName = monsterName.substring(4);
			this.addItem(new ListWidgetItem(monsterName, "Health: "
			        + Integer.toString(monster.getHealth())));
		}
		this.setHeight(playerScreen.getHeight() / 5).setWidth(4 * playerScreen.getWidth() / 7);
	}
	
}
