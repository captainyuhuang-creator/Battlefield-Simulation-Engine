import static org.junit.jupiter.api.Assertions.*;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayDeque;
import java.util.Deque;
import java.util.Random;
import org.junit.jupiter.api.Test;

class BattleTest {
    /** Random whose results are scripted, so tests are deterministic. */
    static class ScriptedRandom extends Random {
        private final Deque<Integer> ints = new ArrayDeque<>();
        private final Deque<Boolean> bools = new ArrayDeque<>();
        
        ScriptedRandom withInts(Integer... v) { 
        	ints.addAll(java.util.List.of(v)); 
        	
        	return this; 
        }
        
        ScriptedRandom bools(Boolean... v) { 
        	bools.addAll(java.util.List.of(v)); 
        	
        	return this;
        }

        @Override 
        public int nextInt(int bound) { 
        	return ints.isEmpty() ? 0 : ints.poll(); 
        }
        
        @Override 
        public boolean nextBoolean() { 
        	return bools.isEmpty() ? false : bools.poll(); 
        }
    }

    private Battle battle(Random rand) {
        return new Battle(rand, new PrintStream(new ByteArrayOutputStream()), 0);
    }

    // ---- Input validation ----
    @Test
    void deadSoldierCannotAttack() {
        Soldier dead = new Soldier("Red", Rank.GUARD);
        
        dead.takeDamage(99);
        
        Soldier alive = new Soldier("Black", Rank.GUARD);
        
        assertThrows(IllegalStateException.class, () -> battle(new Random()).attack(dead, alive));
    }

    @Test
    void cannotAttackDeadSoldierOrNull() {
        Soldier alive = new Soldier("Red", Rank.GUARD);
        Soldier dead = new Soldier("Black", Rank.GUARD);
        
        dead.takeDamage(99);
        
        Battle b = battle(new Random());
        
        assertThrows(IllegalStateException.class, () -> b.attack(alive, dead));
        assertThrows(IllegalArgumentException.class, () -> b.attack(alive, null));
    }

    // ---- Simultaneous attack ----
    @Test
    void simultaneousAttackCanKillBothSoldiers() {
        Soldier p1 = new Soldier("Red", Rank.INFANTRY);
        Soldier p2 = new Soldier("Black", Rank.INFANTRY);
        
        p1.takeDamage(2);   // 1 armor left
        p2.takeDamage(2);   // 1 armor left
        
        // nextInt(3) == 1 -> SIMULTANEOUS
        battle(new ScriptedRandom().withInts(1)).executeCombat(p1, p2);
        
        assertFalse(p1.isAlive());
        assertFalse(p2.isAlive());
    }

    // ---- Alternating attack ----
    @Test
    void deadFirstAttackerTargetCannotCounterattack() {
        Soldier p1 = new Soldier("Red", Rank.GENERAL);     // speed 3
        Soldier p2 = new Soldier("Black", Rank.INFANTRY);  // armor 3, speed 1
        
        // nextInt(3) == 2 -> ALTERNATING; nextBoolean true -> p1 attacks first
        battle(new ScriptedRandom().withInts(2).bools(true)).executeCombat(p1, p2);
        
        assertFalse(p2.isAlive());
        assertEquals(5, p1.getCurrentArmor());   // no counterattack happened
    }

    // ---- Retreat ----
    @Test
    void failedRetreatReturnsFalseAndSoldierKeepsFighting() {
        Army army = new Army("Red");
        
        Soldier elephant = new Soldier("Red", Rank.ELEPHANT);  // armor 8
        Soldier enemy = new Soldier("Black", Rank.INFANTRY);   // speed 1
        
        boolean escaped = battle(new ScriptedRandom().bools(false))
                .attemptTacticalRetreat(elephant, army, enemy);
        
        assertFalse(escaped);
        assertTrue(elephant.isAlive());
        assertEquals(7, elephant.getCurrentArmor());
    }

    @Test
    void aggroRedirectionFallsBackToNonInfantryWhenNoInfantryLeft() {
        Army army = new Army("Red");
        
        for (Soldier s : army.getSoldiers()) {
            if (s.getRank() == Rank.INFANTRY) {
                s.takeDamage(100);
            }
        }
        
        Soldier general = army.getSoldiers().get(3);           // a General
        Soldier enemy = new Soldier("Black", Rank.HORSE);      // speed 2
        
        // bools: retreat succeeds; ints: outcome 2 (aggro), then index 0 for target
        boolean escaped = battle(new ScriptedRandom().bools(true).withInts(2, 0))
        		.attemptTacticalRetreat(general, army, enemy);
        
        assertTrue(escaped);
        assertEquals(5, general.getCurrentArmor());            // retreating soldier untouched
        assertEquals(3, army.getSoldiers().get(1).getCurrentArmor()); // first Guard took 2
    }

    @Test
    void retreatOnlyForGeneralOrElephantWhenArmyBelow15() {
        Army army = new Army("Red");
        
        Soldier general = army.getSoldiers().get(3);
        
        Battle b = battle(new Random());
        
        assertFalse(b.canAttemptRetreat(general, army));       // 27 alive
        
        int toKill = 13;                                       // leave 14 alive
        
        for (Soldier s : army.getSoldiers()) {
            if (toKill > 0 && s.getRank() == Rank.INFANTRY) {
                s.takeDamage(100);
                toKill--;
            }
        }
        
        for (Soldier s : army.getSoldiers()) {
            if (toKill > 0 && s.getRank() == Rank.HORSE) {
                s.takeDamage(100);
                toKill--;
            }
        }
        
        assertTrue(army.getAliveCount() < 15);
        assertTrue(b.canAttemptRetreat(general, army));
        assertFalse(b.canAttemptRetreat(army.getSoldiers().get(1), army)); // Guard cannot
    }

    // ---- Full war ----
    @Test
    void fullWarAlwaysEndsWithExactlyOneWinner() {
        for (int seed = 0; seed < 20; seed++) {
            Army red = new Army("Red");
            Army black = new Army("Black");
            
            battle(new Random(seed)).runWar(red, black);
            
            assertTrue(red.getAliveCount() == 0 || black.getAliveCount() == 0);
        }
    }
}