/**
 * 
 */

package me.merdril.randombattle.battle;

import java.util.List;
import java.util.concurrent.locks.ReentrantLock;

import me.merdril.randombattle.RandomBattle;

import org.bukkit.entity.LivingEntity;
import org.getspout.spoutapi.player.SpoutPlayer;

/**
 * @author mark
 */
public class RBPlayer implements RBLivingEntity
{
	RandomBattle	    plugin;
	private SpoutPlayer	player;
	
	/**
	 * <code>public RBPlayer(RandomBattle instance, SpoutPlayer player, Integer[] stats)</code> <br/>
	 * <br/>
	 * Defines a Random Battle player which is a SpoutPlayer with stats and corresponding methods
	 * for manipulating them.
	 * @param instance
	 *            - The RandomBattle instance
	 * @param player
	 *            - The player to represent
	 * @param stats
	 *            - The stats of the player
	 * @throws Exception
	 */
	public RBPlayer(RandomBattle instance, SpoutPlayer player, int[] stat) throws ArrayIndexOutOfBoundsException
	{
		plugin = instance;
		this.player = player;
	}
	
	@Override
	public int getCurHP()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getCurMP()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getMaxHP()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getMaxMP()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getSTR()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getMAG()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getDEF()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getMDEF()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getAGL()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getACC()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getEVA()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getLUCK()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getEXP()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public int getLevel()
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
	@Override
	public List<RBSkill> getSkills()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<RBMagic> getMagicks()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<RBElem> getWeak()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public RBMove getMove(ReentrantLock lock)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public LivingEntity getEntity()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
}
