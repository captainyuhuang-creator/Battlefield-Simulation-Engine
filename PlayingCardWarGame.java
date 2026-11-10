import java.util.ArrayList;
import java.util.Random;

public class PlayingCardWarGame {
    // Definition of troop types and their baseline attributes
    enum Rank {
        EMPEROR("Emperor (Monarch)", 6, 3),        // Emperor: High armor, high armor penetration (Stays protected in rear until all troops fall)
        GUARD("Royal Guard (A)", 5, 2),            // Royal Guard
        GENERAL("General (K/Q/J)", 5, 3),          // General: Frontline breakthrough specialist
        ELEPHANT("Armored Elephant (10/2)", 8, 1), // Armored Elephant: High health absorption, low speed
        HORSE("Cavalry (3/9)", 4, 2),              // Cavalry: High burst mobility flanker
        INFANTRY("Heavy Infantry (4-8)", 3, 1);    // Heavy Infantry: Standard line holder

        final String name;
        final int maxArmor; // Total structural durability / Health points
        final int speed;    // Armor penetration efficiency per strike (Attack power)

        Rank(String name, int maxArmor, int speed) {
            this.name = name;
            this.maxArmor = maxArmor;
            this.speed = speed;
        }
    }

    // Individual combat unit class
    static class Soldier {
        String team;
        Rank rank;
        int currentArmor;
        boolean isAlive;

        public Soldier(String team, Rank rank) {
            this.team = team;
            this.rank = rank;
            this.currentArmor = rank.maxArmor;
            this.isAlive = true;
        }

        // Live battlefield state visualization interface
        public String getStatusString() {
            if (!isAlive) {
                return "[KIA - Card Flipped Face-Down Into Graveyard Area]";
            }
            return "[ACTIVE] Armor Integrity: " + currentArmor + "/" + rank.maxArmor;
        }
    }

    // Army structure logic (Strict 27-man battalion roster)
    static class Army {
        String name;
        ArrayList<Soldier> soldiers = new ArrayList<>();

        public Army(String name) {
            this.name = name;
            
            // 1. Commander: 1 Emperor (Index 0)
            soldiers.add(new Soldier(name, Rank.EMPEROR)); 
            
            // 2. Royal Guards: 2 units
            for(int i = 0; i < 2; i++) {
                soldiers.add(new Soldier(name, Rank.GUARD));   
            }
            
            // 3. Generals: 6 units
            for(int i = 0; i < 6; i++) {
                soldiers.add(new Soldier(name, Rank.GENERAL)); 
            }
            
            // 4. Armored Elephants: 4 units
            for(int i = 0; i < 4; i++) {
                soldiers.add(new Soldier(name, Rank.ELEPHANT)); 
            }
            
            // 5. Cavalry: 4 units
            for(int i = 0; i < 4; i++) {
                soldiers.add(new Soldier(name, Rank.HORSE));    
            }
            
            // 6. Heavy Infantry: 10 units
            for(int i = 0; i < 10; i++) {
                soldiers.add(new Soldier(name, Rank.INFANTRY)); 
            }
        }

        // Live active troop count monitoring
        public int getAliveCount() {
            int count = 0;
            for(Soldier s : soldiers) {
                if(s.isAlive) count++;
            }
            return count;
        }

        // Select an eligible soldier for battle (Emperor is protected until he is the last survivor)
        public Soldier selectCombatant(Random rand) {
            ArrayList<Soldier> eligibleTroops = new ArrayList<>();
            Soldier emperor = null;

            for (Soldier s : soldiers) {
                if (s.isAlive) {
                    if (s.rank == Rank.EMPEROR) {
                        emperor = s;
                    } else {
                        eligibleTroops.add(s);
                    }
                }
            }

            // If non-emperor troops are still alive, Emperor is protected
            if (!eligibleTroops.isEmpty()) {
                return eligibleTroops.get(rand.nextInt(eligibleTroops.size()));
            }

            // Emperor steps in only when everyone else has fallen
            return emperor;
        }

        // Target acquisition AI for redirected aggro (Finding an alive infantry)
        public Soldier getRandomAliveInfantry(Random rand) {
            ArrayList<Soldier> aliveInfantry = new ArrayList<>();
            for(Soldier s : soldiers) {
                if(s.isAlive && s.rank == Rank.INFANTRY) {
                    aliveInfantry.add(s);
                }
            }
            if(aliveInfantry.isEmpty()) return null;
            return aliveInfantry.get(rand.nextInt(aliveInfantry.size()));
        }

        public Soldier getEmperor() {
            return soldiers.get(0);
        }
    }

    public static void main(String[] args) throws InterruptedException {
        System.out.println("================== ⚔️ 27-MAN HEAVY ARMOR WAR ENGINE (FIXED) ⚔️ ==================");
        System.out.println("Core AI Protocols: Royal Protection Enabled | RNG Battlefields | Tactical Retreat | State Visualized\n");

        Army redArmy = new Army("The Crimson Dynasty (Hearts/Diamonds)");
        Army blackArmy = new Army("The Obsidian Kingdom (Spades/Clubs)");
        
        Random rand = new Random();
        int round = 1;

        // War loop processing calculations until total structural wipeout of either battalion
        while(redArmy.getAliveCount() > 0 && blackArmy.getAliveCount() > 0) {
            System.out.println("\n----------------------------------------------------------------------");
            System.out.println("🏹 BATTLEFIELD ENGAGEMENT - ROUND " + round + " 🏹");
            System.out.println("🔴 Crimson Dynasty: " + redArmy.getAliveCount() + "/27 Active | ⚫ Obsidian Kingdom: " + blackArmy.getAliveCount() + "/27 Active");
            System.out.println("----------------------------------------------------------------------");

            // Select combatants respecting the Emperor Protection protocol
            Soldier p1 = redArmy.selectCombatant(rand);
            Soldier p2 = blackArmy.selectCombatant(rand);

            // Special log broadcast when an Emperor is forced onto the field
            if (p1.rank == Rank.EMPEROR && redArmy.getAliveCount() == 1) {
                System.out.println("👑 THE MONARCH STANDS ALONE! " + redArmy.name + "'s Emperor draws his blade for a final last stand!");
            }
            if (p2.rank == Rank.EMPEROR && blackArmy.getAliveCount() == 1) {
                System.out.println("👑 THE MONARCH STANDS ALONE! " + blackArmy.name + "'s Emperor draws his blade for a final last stand!");
            }

            // Reset participating soldiers' armor at the start of each skirmish
            p1.currentArmor = p1.rank.maxArmor;
            p2.currentArmor = p2.rank.maxArmor;

            System.out.println("⚔️ SKIRMISH INITIALIZED: [" + redArmy.name + " - " + p1.rank.name + "] vs [" + blackArmy.name + " - " + p2.rank.name + "]");

            int hits = 0;

            // Micro-combat event handling loop
            while(p1.isAlive && p2.isAlive) {
                hits++;
                
                // Dynamic Tactical Decision AI: High-tier heavy-armor units trigger strategic retreat
                if(hits > 3 && rand.nextDouble() < 0.25) {
                    if((p1.rank == Rank.GENERAL || p1.rank == Rank.ELEPHANT) && redArmy.getAliveCount() < 15) {
                        System.out.println("🔄 TACTICAL RETREAT! " + redArmy.name + " [" + p1.rank.name + "] breaks formation to secure the rear!");
                        
                        // Opponent AI branch processing: Pursuit mode vs Aggro redirection
                        if(rand.nextBoolean()) {
                            System.out.println("🏹 ENEMY DECISION: [PURSUIT MODE]! " + blackArmy.name + " [" + p2.rank.name + "] presses forward, striking their exposed back plate!");
                            p1.currentArmor -= p2.rank.speed; 
                            if(p1.currentArmor <= 0) p1.isAlive = false;
                        } 
                        else {
                            // Enemy attacks isolated infantry units of the opponent army
                            Soldier targetInfantry = redArmy.getRandomAliveInfantry(rand);
                            if (targetInfantry != null) {
                                System.out.println("⚔️ ENEMY DECISION: [AGGRO REDIRECTION]! " + blackArmy.name + " flanks and strikes " + redArmy.name + " [" + targetInfantry.rank.name + "]!");
                                targetInfantry.currentArmor = 0;
                                targetInfantry.isAlive = false;
                            }
                        }
                        break;
                    }
                }

                // Real-time blade clash execution logging with pacing delay
                System.out.print("CLANG! ");
                Thread.sleep(100);

                // Standard clash attack round
                if(rand.nextBoolean()) {
                    p2.currentArmor -= p1.rank.speed;
                    if(p2.currentArmor <= 0) {
                        p2.isAlive = false;
                        System.out.println("\n💥 ARMOR COMPROMISED! " + redArmy.name + " [" + p1.rank.name + "] delivers a lethal strike!");
                    }
                } 
                else {
                    p1.currentArmor -= p2.rank.speed;
                    if(p1.currentArmor <= 0) {
                        p1.isAlive = false;
                        // ✅ FIX: Changed p1.rank.name to p2.rank.name so the correct killer rank is printed
                        System.out.println("\n💥 ARMOR COMPROMISED! " + blackArmy.name + " [" + p2.rank.name + "] delivers a lethal strike!");
                    }
                }

                // Instant assassination / Critical strike verification logic
                if(hits == 3 && (!p1.isAlive || !p2.isAlive)) {
                    System.out.println("⚡ CRITICAL COLLISION! Elite duel resolved in a rapid 3-strike execution sequence!");
                }
            }

            // Sync structural update outputs for fallen units
            if(!p1.isAlive) {
                System.out.println("💀 " + redArmy.name + " [" + p1.rank.name + "] " + p1.getStatusString());
            }
            if(!p2.isAlive) {
                System.out.println("💀 " + blackArmy.name + " [" + p2.rank.name + "] " + p2.getStatusString());
            }

            round++;
            Thread.sleep(400); // Visual frame delay for log tracking
        }

        // Termination conditions resolved (Total decapitation victory)
        System.out.println("\n======================================================================");
        System.out.println("🏆 TOTAL ANNIHILATION SEQUENCE RESOLVED 🏆");
        System.out.println("======================================================================");
        if(redArmy.getAliveCount() == 0) {
            System.out.println("🎉 TRIUMPHANT VICTORY: " + blackArmy.name + "! Every single enemy combatant has been swept into the graveyard area.");
        } 
        else {
            System.out.println("🎉 TRIUMPHANT VICTORY: " + redArmy.name + "! Every single enemy combatant has been swept into the graveyard area.");
        }
    }
}