import java.io.PrintStream;
import java.util.Random;

public class Battle {
    private static final int RETREAT_ARMY_LIMIT = 15;

    private final Random rand;
    private final PrintStream out;
    private final int delayMs;

    public Battle(Random rand, PrintStream out, int delayMs) {
        if (rand == null || out == null) {
            throw new IllegalArgumentException("Random and PrintStream must not be null.");
        }
        if (delayMs < 0) {
            throw new IllegalArgumentException("Delay must not be negative.");
        }
        
        this.rand = rand;
        this.out = out;
        this.delayMs = delayMs;
    }

    private void pause(int ms) {
        if (delayMs == 0) {
            return;
        }
        try {
            Thread.sleep(ms);
        } 
        catch (InterruptedException e) {
            Thread.currentThread().interrupt();
        }
    }

    // ---------- Damage ----------
    public void attack(Soldier attacker, Soldier defender) {
        if (attacker == null || defender == null) {
            throw new IllegalArgumentException("Attacker and defender must not be null.");
        }
        if (!attacker.isAlive()) {
            throw new IllegalStateException("A defeated soldier cannot attack.");
        }
        if (!defender.isAlive()) {
            throw new IllegalStateException("Cannot attack a defeated soldier.");
        }
        
        int damage = attacker.getRank().getSpeed();
        
        defender.takeDamage(damage);
        
        System.out.println(attacker.getTeam() + " [" + attacker.getRank().getDisplayName()
                + "] attacks " + defender.getTeam() + " ["
                + defender.getRank().getDisplayName() + "] for " + damage + " damage!");
        
        if (!defender.isAlive()) {
            System.out.println("💥 ARMOR COMPROMISED! " + defender.getTeam() + " ["
                    + defender.getRank().getDisplayName() + "] is defeated!");
        } 
        else {
            System.out.println("🛡️ Remaining Armor: " + defender.getCurrentArmor()
                    + "/" + defender.getRank().getMaxArmor());
        }
    }

    // ---------- Combat modes ----------
    public void executeCombat(Soldier p1, Soldier p2) {
        if (p1 == null || p2 == null) {
            throw new IllegalArgumentException("Combatants must not be null.");
        }
        if (!p1.isAlive() || !p2.isAlive()) {
            throw new IllegalStateException("Both combatants must be alive.");
        }
        
        AttackMode mode = AttackMode.values()[rand.nextInt(AttackMode.values().length)];
        
        System.out.println("\n⚔️ COMBAT MODE: " + mode);
        
        pause(100);

        switch (mode) {
            case SINGLE_SIDED:
            	System.out.println("🗡️ ONE SOLDIER STRIKES!");
                
            	if (rand.nextBoolean()) {
                    attack(p1, p2);
                } 
                else {
                    attack(p2, p1);
                }
                
                break;

            case SIMULTANEOUS:
            	System.out.println("⚡ BOTH SOLDIERS SWING THEIR SWORDS SIMULTANEOUSLY!");
                
            	int damageToP1 = p2.getRank().getSpeed();
                int damageToP2 = p1.getRank().getSpeed();
                
                p1.takeDamage(damageToP1);
                p2.takeDamage(damageToP2);
                
                System.out.println(p1.getTeam() + " [" + p1.getRank().getDisplayName()
                        + "] receives " + damageToP1 + " damage.");
                System.out.println(p2.getTeam() + " [" + p2.getRank().getDisplayName()
                        + "] receives " + damageToP2 + " damage.");
                
                if (!p1.isAlive()) {
                    out.println("💥 " + p1.getTeam() + " [" + p1.getRank().getDisplayName() + "] is defeated!");
                }
                if (!p2.isAlive()) {
                    out.println("💥 " + p2.getTeam() + " [" + p2.getRank().getDisplayName() + "] is defeated!");
                }
                if (!p1.isAlive() && !p2.isAlive()) {
                    out.println("☠️ DOUBLE KNOCKOUT! BOTH SOLDIERS FALL!");
                }
                
                break;

            case ALTERNATING:
            	System.out.println("🔄 SOLDIERS TAKE TURNS ATTACKING!");
                
            	Soldier first;
                Soldier second;
                
                if (rand.nextBoolean()) {
                    first = p1;
                    second = p2;
                } 
                else {
                    first = p2;
                    second = p1;
                }
                
                System.out.println("🏹 First attacker: " + first.getTeam()
                        + " [" + first.getRank().getDisplayName() + "]");
                
                attack(first, second);
                
                if (first.isAlive() && second.isAlive()) {
                	System.out.println("⚔️ COUNTERATTACK!");
                    
                	attack(second, first);
                } 
                else {
                	System.out.println("🚫 Counterattack cancelled. The defeated soldier cannot retaliate.");
                }
                
                break;
        }
    }

    // ---------- Retreat ----------
    public boolean canAttemptRetreat(Soldier soldier, Army army) {
        if (soldier == null || army == null) {
            throw new IllegalArgumentException("Soldier and army must not be null.");
        }
        
        return soldier.isAlive()
                && (soldier.getRank() == Rank.GENERAL || soldier.getRank() == Rank.ELEPHANT)
                && army.getAliveCount() < RETREAT_ARMY_LIMIT;
    }

    /** @return true if the soldier leaves the skirmish alive; false if the fight must continue. */
    public boolean attemptTacticalRetreat(Soldier retreating, Army ownArmy, Soldier enemy) {
        if (retreating == null || ownArmy == null || enemy == null) {
            throw new IllegalArgumentException("Arguments must not be null.");
        }
        if (!retreating.isAlive() || !enemy.isAlive()) {
            throw new IllegalStateException("Both soldiers must be alive to retreat.");
        }

        System.out.println("\n🔄 TACTICAL RETREAT! " + ownArmy.getName() + " ["
                + retreating.getRank().getDisplayName() + "] attempts to withdraw!");

        if (!rand.nextBoolean()) {
        	System.out.println("❌ RETREAT FAILED! The enemy catches "
                    + retreating.getRank().getDisplayName() + " before it can escape!");
        	System.out.println("🏹 ENEMY COUNTERATTACK!");
            
        	attack(enemy, retreating);
            
        	return false;
        }

        System.out.println("✅ RETREAT SUCCESSFUL! The soldier breaks formation.");
        
        int outcome = rand.nextInt(3);

        if (outcome == 0) {
        	System.out.println("🛡️ CLEAN ESCAPE! The soldier reaches the rear safely.");
        	System.out.println("✨ NO CASUALTIES! The retreat succeeds without losses.");
            
        	return true;
        } 
        else if (outcome == 1) {
        	System.out.println("🏹 ENEMY PURSUIT MODE! The enemy chases the retreating soldier!");
            
        	attack(enemy, retreating);
            
        	if (retreating.isAlive()) {
        		System.out.println("⚠️ RETREAT COMPLETED WITH DAMAGE! "
                        + retreating.getRank().getDisplayName() + " escapes with reduced armor.");
                
        		return true;
            }
            
        	System.out.println("💀 RETREAT INTERCEPTED! The soldier is eliminated.");
            
        	return false;
        } 
        else {
        	System.out.println("⚔️ AGGRO REDIRECTION! The enemy changes its target.");
            
        	Soldier target = ownArmy.getRandomAliveInfantry(rand);
            
        	if (target == null) {
        		System.out.println("⚠️ NO SURVIVING INFANTRY! Enemy AI searches for another target.");
                
        		target = ownArmy.getRandomAliveNonEmperor(retreating, rand);
            }
            if (target == null) {
            	System.out.println("🛡️ NO ELIGIBLE TARGET! The enemy cannot redirect its attack.");
            	System.out.println("✨ RETREAT COMPLETED WITHOUT CASUALTIES.");
                
            	return true;
            }
            
            System.out.println("🎯 NEW TARGET ACQUIRED: " + ownArmy.getName()
                    + " [" + target.getRank().getDisplayName() + "]");
            
            attack(enemy, target);
            
            System.out.println("🏃 " + retreating.getRank().getDisplayName() + " successfully reaches the rear.");
            
            return true;
        }
    }

    // ---------- One skirmish ----------
    public void runSkirmish(Soldier p1, Army redArmy, Soldier p2, Army blackArmy) {
        p1.resetArmor();
        p2.resetArmor();
        
        System.out.println("\n⚔️ SKIRMISH INITIALIZED: [" + redArmy.getName() + " - "
                + p1.getRank().getDisplayName() + "] vs [" + blackArmy.getName()
                + " - " + p2.getRank().getDisplayName() + "]");

        int hits = 0;
        
        while (p1.isAlive() && p2.isAlive()) {
            hits++;
            
            System.out.println("\n========== COMBAT EXCHANGE " + hits + " ==========");

            if (hits > 3 && rand.nextDouble() < 0.25) {
                boolean redCan = canAttemptRetreat(p1, redArmy);
                boolean blackCan = canAttemptRetreat(p2, blackArmy);

                if (redCan || blackCan) {
                    boolean redRetreats = (redCan && blackCan) ? rand.nextBoolean() : redCan;
                    
                    Soldier retreating = redRetreats ? p1 : p2;
                    Soldier enemy = redRetreats ? p2 : p1;
                    Army ownArmy = redRetreats ? redArmy : blackArmy;

                    if (attemptTacticalRetreat(retreating, ownArmy, enemy)) {
                    	System.out.println("🏳️ SKIRMISH ENDED: " + retreating.getRank().getDisplayName()
                                + " has withdrawn from the battlefield.");
                        
                    	break;
                    }
                }
            }

            if (p1.isAlive() && p2.isAlive()) {
            	System.out.println("\nCLANG! ⚔️");
                
            	pause(100);
                
            	executeCombat(p1, p2);
            }

            if (hits == 3 && (!p1.isAlive() || !p2.isAlive())) {
            	System.out.println("⚡ CRITICAL COLLISION! Elite duel resolved in three exchanges!");
            }
        }
    }

    // ---------- Whole war ----------
    public void runWar(Army redArmy, Army blackArmy) {
        if (redArmy == null || blackArmy == null) {
            throw new IllegalArgumentException("Armies must not be null.");
        }
        
        int round = 1;
        int redTotal = redArmy.getTotalCount();
        int blackTotal = blackArmy.getTotalCount();

        while (redArmy.getAliveCount() > 0 && blackArmy.getAliveCount() > 0) {
        	System.out.println("\n----------------------------------------------------------------------");
        	System.out.println("🏹 BATTLEFIELD ENGAGEMENT - ROUND " + round);
        	System.out.println("🔴 Crimson Dynasty: " + redArmy.getAliveCount() + "/" + redTotal
                    + " Active | ⚫ Obsidian Kingdom: " + blackArmy.getAliveCount() + "/" + blackTotal + " Active");
        	System.out.println("----------------------------------------------------------------------");

            Soldier p1 = redArmy.selectCombatant(rand);
            Soldier p2 = blackArmy.selectCombatant(rand);

            if (p1.getRank() == Rank.EMPEROR && redArmy.getAliveCount() == 1) {
            	System.out.println("👑 THE MONARCH STANDS ALONE! " + redArmy.getName() + "'s Emperor enters the battlefield!");
            }
            if (p2.getRank() == Rank.EMPEROR && blackArmy.getAliveCount() == 1) {
            	System.out.println("👑 THE MONARCH STANDS ALONE! " + blackArmy.getName() + "'s Emperor enters the battlefield!");
            }

            runSkirmish(p1, redArmy, p2, blackArmy);

            System.out.println("\n========== SKIRMISH REPORT ==========");
            
            if (!p1.isAlive()) {
                out.println("💀 " + redArmy.getName() + " [" + p1.getRank().getDisplayName() + "] " + p1.getStatusString());
            }
            if (!p2.isAlive()) {
                out.println("💀 " + blackArmy.getName() + " [" + p2.getRank().getDisplayName() + "] " + p2.getStatusString());
            }
            
            System.out.println("🔴 Remaining Crimson Troops: " + redArmy.getAliveCount());
            System.out.println("⚫ Remaining Obsidian Troops: " + blackArmy.getAliveCount());

            round++;
            
            pause(400);
        }

        System.out.println("\n======================================================================");
        System.out.println("🏆 TOTAL ANNIHILATION SEQUENCE RESOLVED 🏆");
        System.out.println("======================================================================");
        
        Army winner = redArmy.getAliveCount() == 0 ? blackArmy : redArmy;
        
        System.out.println("🎉 TRIUMPHANT VICTORY: " + winner.getName() + "! Every enemy combatant has fallen.");
        System.out.println("\nFINAL STATISTICS:");
        System.out.println("🔴 Crimson Dynasty Survivors: " + redArmy.getAliveCount());
        System.out.println("⚫ Obsidian Kingdom Survivors: " + blackArmy.getAliveCount());
        System.out.println("⚔️ Total Battlefield Rounds: " + (round - 1));
    }
}