/**
 * 
 */

package me.merdril.RandomBattle.HUD;

import java.util.ArrayList;

import me.merdril.RandomBattle.RandomBattle;

import org.bukkit.entity.Monster;
import org.bukkit.inventory.PlayerInventory;
import org.getspout.spoutapi.gui.GenericListWidget;
import org.getspout.spoutapi.gui.ListWidgetItem;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mark
 * 
 */
public class RandomBattleMonsterList extends GenericListWidget
{
	RandomBattle	           plugin;
	private ArrayList<Monster>	monsters;
	private SpoutPlayer	       player;
	
	/**
	 * 
	 */
	public RandomBattleMonsterList(RandomBattle instance, SpoutPlayer player, ArrayList<Monster> monsters)
	{
		super();
		this.plugin = instance;
		this.player = player;
		PlayerInventory inventory = player.getInventory();
		inventory.contains(	)
		this.monsters = monsters;
		for (Monster monster : monsters)
		{
			String monsterName = monster.toString();
			if (monsterName.contains("Craft"))
				monsterName = monsterName.substring(5);
			this.addItem(new ListWidgetItem(monsterName, "Health: "
			        + Integer.toString(monster.getHealth())));
		}
	}
	
	@Override
	public void onSelected(int item, boolean doubleClick)
	{
		Monster selMonster = monsters.get(item);
	}
}
