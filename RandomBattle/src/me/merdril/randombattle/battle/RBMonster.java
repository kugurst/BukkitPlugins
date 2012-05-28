package me.merdril.randombattle.battle;

import java.util.List;
import java.util.Map;
import java.util.concurrent.locks.ReentrantLock;

import org.bukkit.entity.LivingEntity;

public class RBMonster implements RBLivingEntity
{
	
	@Override
	public Integer getStat(Stat stat)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Integer setStat(Stat stat, int amount)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Effect> getEffects()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<Effect> setEffect(int action, Effect... effect)
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<RBSkill> getSkills()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public List<RBSkill> setSkills(int action, RBSkill... skills)
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
	public List<RBMagic> setMagicks(int action, RBMagic... magicks)
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
	public List<RBElem> setWeak(RBElem... elems)
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
	
	@Override
	public Map<Stat, Integer> getStats()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public Map<Stat, Integer> getOriginalStats()
	{
		// TODO Auto-generated method stub
		return null;
	}
	
	@Override
	public int compareTo(RBLivingEntity other)
	{
		// TODO Auto-generated method stub
		return 0;
	}
	
}
