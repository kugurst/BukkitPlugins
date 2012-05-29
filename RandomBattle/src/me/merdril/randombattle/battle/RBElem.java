
package me.merdril.randombattle.battle;

import java.util.HashMap;

public enum RBElem {
	
	FIRE, // ("earth;water;", "ice;poison;", "lightning;wind;"),
	ICE, // ("fire;lightning;", "water;earth;", "wind;poison;"),
	LIGHTNING, // ("earth;", "water;ice", "wind;gravity;"),
	EARTH, // ("ice;wind;", "lightning;fire;", "poison;lightning;"),
	WIND, // ("gravity;lightning;", "earth;", "water;");
	WATER, //
	HOLY, //
	DARK, //
	POISON, //
	GRAVITY;
	
	// Initialize the enum map for quicker access the enumeration
	public static final HashMap<String, RBElem>	elemMap	= new HashMap<String, RBElem>();
	
	// Initialize the enums
	static {
		FIRE.weakTo = new RBElem[] {EARTH, WATER};
		FIRE.strongAgainst = new RBElem[] {ICE, POISON};
		FIRE.immuneTo = new RBElem[] {LIGHTNING, WIND};
		
		ICE.weakTo = new RBElem[] {FIRE, LIGHTNING};
		ICE.strongAgainst = new RBElem[] {WATER, EARTH};
		ICE.immuneTo = new RBElem[] {WIND, POISON};
		
		LIGHTNING.weakTo = new RBElem[] {EARTH};
		LIGHTNING.strongAgainst = new RBElem[] {WATER, ICE};
		LIGHTNING.immuneTo = new RBElem[] {WIND, GRAVITY};
		
		EARTH.weakTo = new RBElem[] {ICE, WIND, POISON};
		EARTH.strongAgainst = new RBElem[] {LIGHTNING, FIRE};
		EARTH.immuneTo = new RBElem[] {};
		
		WIND.weakTo = new RBElem[] {};
		WIND.strongAgainst = new RBElem[] {EARTH};
		WIND.immuneTo = new RBElem[] {};
		
		WATER.weakTo = new RBElem[] {ICE, LIGHTNING, POISON};
		WATER.strongAgainst = new RBElem[] {EARTH, FIRE};
		WATER.immuneTo = new RBElem[] {GRAVITY};
		
		HOLY.weakTo = new RBElem[] {DARK, POISON};
		HOLY.strongAgainst = new RBElem[] {DARK, POISON};
		HOLY.immuneTo = new RBElem[] {LIGHTNING};
		
		DARK.weakTo = new RBElem[] {HOLY, LIGHTNING};
		DARK.strongAgainst = new RBElem[] {HOLY};
		DARK.immuneTo = new RBElem[] {POISON};
		
		POISON.weakTo = new RBElem[] {FIRE, HOLY};
		POISON.strongAgainst = new RBElem[] {EARTH, WATER, HOLY};
		POISON.immuneTo = new RBElem[] {WIND};
		
		GRAVITY.weakTo = new RBElem[] {};
		GRAVITY.strongAgainst = new RBElem[] {};
		GRAVITY.immuneTo = new RBElem[] {};
		
		for (RBElem elem : RBElem.values())
			elemMap.put(elem.toString().toLowerCase(), elem);
	}
	
	private RBElem[]	                        weakTo;
	private RBElem[]	                        strongAgainst;
	private RBElem[]	                        immuneTo;
	
	public boolean weakTo(RBElem other)
	{
		for (RBElem elem : weakTo)
			if (elem.equals(other))
				return true;
		return false;
	}
	
	public boolean strongAgainst(RBElem other)
	{
		for (RBElem elem : strongAgainst)
			if (elem.equals(other))
				return true;
		return false;
	}
	
	public boolean immuneTo(RBElem other)
	{
		for (RBElem elem : immuneTo)
			if (elem.equals(other))
				return true;
		return false;
	}
}
