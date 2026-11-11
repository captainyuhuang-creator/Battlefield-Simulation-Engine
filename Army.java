import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Random;

public class Army {
    private final String name;
    private final List<Soldier> soldiers = new ArrayList<>();

    public Army(String name) {
        if (name == null || name.isBlank()) {
            throw new IllegalArgumentException("Army name must not be null or blank.");
        }
        this.name = name;
        addSoldiers(Rank.EMPEROR, 1);
        addSoldiers(Rank.GUARD, 2);
        addSoldiers(Rank.GENERAL, 6);
        addSoldiers(Rank.ELEPHANT, 4);
        addSoldiers(Rank.HORSE, 4);
        addSoldiers(Rank.INFANTRY, 10);
    }

    private void addSoldiers(Rank rank, int count) {
        for (int i = 0; i < count; i++) {
            soldiers.add(new Soldier(name, rank));
        }
    }

    public String getName() { return name; }

    public List<Soldier> getSoldiers() {
        return Collections.unmodifiableList(soldiers);
    }

    public int getTotalCount() { return soldiers.size(); }

    public int getAliveCount() {
        int count = 0;
        for (Soldier s : soldiers) {
            if (s.isAlive()) {
                count++;
            }
        }
        return count;
    }

    /** Picks a non-Emperor soldier; the Emperor only fights when he is the last one alive. */
    public Soldier selectCombatant(Random rand) {
        List<Soldier> eligible = new ArrayList<>();
        Soldier emperor = null;
        for (Soldier s : soldiers) {
            if (!s.isAlive()) {
                continue;
            }
            if (s.getRank() == Rank.EMPEROR) {
                emperor = s;
            } else {
                eligible.add(s);
            }
        }
        if (!eligible.isEmpty()) {
            return eligible.get(rand.nextInt(eligible.size()));
        }
        return emperor;
    }

    public Soldier getRandomAliveInfantry(Random rand) {
        List<Soldier> infantry = new ArrayList<>();
        for (Soldier s : soldiers) {
            if (s.isAlive() && s.getRank() == Rank.INFANTRY) {
                infantry.add(s);
            }
        }
        return infantry.isEmpty() ? null : infantry.get(rand.nextInt(infantry.size()));
    }

    public Soldier getRandomAliveNonEmperor(Soldier excluded, Random rand) {
        List<Soldier> eligible = new ArrayList<>();
        for (Soldier s : soldiers) {
            if (s.isAlive() && s.getRank() != Rank.EMPEROR && s != excluded) {
                eligible.add(s);
            }
        }
        return eligible.isEmpty() ? null : eligible.get(rand.nextInt(eligible.size()));
    }
}