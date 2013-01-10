/**
 * 
 */

package me.merdril.randombattle.battle;

import java.util.ArrayList;

import me.merdril.randombattle.RandomBattle;

import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Monster;
import org.getspout.spoutapi.gui.GenericPopup;

/**
 * @author mark
 */
public class FightSys
{
	RandomBattle	          plugin;
	GenericPopup	          popup;
	ArrayList<RBLivingEntity>	creatures;
	LivingEntity[]	          turnList;
	
	/**
	 * 
	 */
	public FightSys(RandomBattle instance, GenericPopup popup, ArrayList<RBLivingEntity> involvedEntities)
	{
		plugin = instance;
		this.popup = popup;
		creatures = involvedEntities;
		populateTurnList(involvedEntities);
	}
	
	public FightSys(int andAdd, ArrayList<Monster> battleMonsters, RBPlayer loadPlayer)
	{
		// TODO Auto-generated constructor stub
	}
	
	public void populateTurnList(ArrayList<RBLivingEntity> involvedEntities)
	{
		// TODO Auto-generated method stub
		
	}
	
}
