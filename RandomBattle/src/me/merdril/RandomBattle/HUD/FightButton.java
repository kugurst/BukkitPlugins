/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import java.util.ArrayList;

import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.Monster;
import org.getspout.spoutapi.event.screen.ButtonClickEvent;
import org.getspout.spoutapi.gui.GenericButton;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.gui.WidgetAnchor;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author Merdril
 * 
 */
public class FightButton extends GenericButton
{
	RandomBattle	                plugin;
	private RandomBattlePopupScreen	screen;
	private ArrayList<Monster>	    monsters;
	
	/**
	 * @param screen
	 * @param monsters
	 * 
	 */
	public FightButton(RandomBattle instance, RandomBattlePopupScreen screen, SpoutPlayer player,
	        ArrayList<Monster> monsters)
	{
		super();
		this.plugin = instance;
		this.screen = screen;
		this.monsters = monsters;
		this.setText("Fight");
	}
	
	@Override
	public void onButtonClick(ButtonClickEvent event)
	{
		GenericListWidget monsterList = new GenericListWidget();
		for (Monster monster : monsters)
		{
			String monsterName = monster.toString();
			if (monsterName.contains("Craft"))
				monsterName = monsterName.substring(5);
			monsterList.addItem(new ListWidgetItem(monsterName, "Health: "
			        + Integer.toString(monster.getHealth())));
		}
		monsterList.setHeight(screen.getHeight() / 5).setWidth(4 * screen.getWidth() / 7);
		screen.attachWidget(plugin, monsterList);
		monsterList.setAnchor(WidgetAnchor.BOTTOM_CENTER).shiftYPos(-monsterList.getHeight() - 20)
		        .shiftXPos(-monsterList.getWidth() / 2);
	}
}
