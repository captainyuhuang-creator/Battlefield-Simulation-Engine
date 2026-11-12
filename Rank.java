public enum Rank {
    EMPEROR("Emperor (Joker)", 6, 3),
    GUARD("Royal Guard (Ace)", 5, 2),
    GENERAL("General (King/Queen/Jack)", 5, 3),
    ELEPHANT("Armored Elephant (10/2)", 8, 1),
    HORSE("Cavalry (3/9)", 4, 2),
    INFANTRY("Heavy Infantry (4-8)", 3, 1);

    private final String displayName;
    private final int maxArmor;
    private final int speed;

    Rank(String displayName, int maxArmor, int speed) {
        this.displayName = displayName;
        this.maxArmor = maxArmor;
        this.speed = speed;
    }

    public String getDisplayName() { 
    	return displayName; 
    }
    
    public int getMaxArmor() { 
    	return maxArmor; 
    }
    
    public int getSpeed() { 
    	return speed; 
    }
}