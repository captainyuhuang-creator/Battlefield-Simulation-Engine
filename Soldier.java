public class Soldier {
    private final String team;
    private final Rank rank;
    private int currentArmor;
    private boolean alive;

    public Soldier(String team, Rank rank) {
        if (team == null || team.isBlank()) {
            throw new IllegalArgumentException("Team name must not be null or blank.");
        }
        if (rank == null) {
            throw new IllegalArgumentException("Rank must not be null.");
        }
        this.team = team;
        this.rank = rank;
        this.currentArmor = rank.getMaxArmor();
        this.alive = true;
    }

    public String getTeam() { return team; }
    public Rank getRank() { return rank; }
    public int getCurrentArmor() { return currentArmor; }
    public boolean isAlive() { return alive; }

    /** Applies damage. Negative damage is rejected; a dead soldier ignores further damage. */
    public void takeDamage(int damage) {
        if (damage < 0) {
            throw new IllegalArgumentException("Damage must not be negative: " + damage);
        }
        if (!alive) {
            return;
        }
        currentArmor -= damage;
        if (currentArmor <= 0) {
            currentArmor = 0;
            alive = false;
        }
    }

    public void resetArmor() {
        if (alive) {
            currentArmor = rank.getMaxArmor();
        }
    }

    public String getStatusString() {
        if (!alive) {
            return "[KIA - Card Flipped Face-Down Into Graveyard Area]";
        }
        return "[ACTIVE] Armor Integrity: " + currentArmor + "/" + rank.getMaxArmor();
    }
}