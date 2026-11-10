# Battlefield Simulation & AI Logic Engine (Java / OOP Architecture)

## 📌 Project Overview
**Battlefield Simulation & AI Logic Engine** is a high-performance, object-oriented text-based combat simulation engine built in Java. The project acts as a deterministic state machine modeling a tactical cold-weapon military campaign. It is strictly configured around a **precise 54-card standard playing deck allocation system** (partitioned dynamically into two 27-man heavy-armored strategic factions). 

The application utilizes strict type-safety architectures, complex conditional logic flows, and real-time behavioral decision trees to resolve intricate macro-level skirmishes down to microscopic armor penetration events.

---

## 🛠️ Core Technical Features & Architecture

### 1. Advanced State Control via Finite State Machine (FSM)
* **Lifecycle Synchronization:** Every tactical asset's lifecycle is systematically managed using explicit state transitions tracking running statuses (`ACTIVE` vs. `KIA_GRAVEYARD`).
* **Memory Segregation:** Eliminated units undergo an automated structural check. Once a unit's structural durability reaches zero (`currentArmor <= 0`), its object references are decoupled from the combat pool and securely locked in the graveyard zone, matching real-world card flipping mechanics.

### 2. Guard Selection Protocols & Warlord Protection AI
* **The Monarch Protection Layer:** Implements a strict target-acquisition filter (`selectCombatant`) keeping the army commander completely insulated behind auxiliary troop waves.
* **Last Stand Activation:** Faction leaders are mathematically constrained from joining active skirmishes until the auxiliary operational roster is completely exhausted, triggering an automatic "Last Stand" system alert log.

### 3. Dynamic AI Behavioral Trees & Redirection Loops
* **Tactical Retreat Protocol:** High-tier elite armored classes evaluate live asset thresholds. If localized engagement logs prolong beyond optimal metrics, units execute a strategic withdrawal sub-routine.
* **Opponent Reaction Branching:** When a tactical retreat occurs, the opposing combat unit evaluates the battlefield dynamically to choose between two branches:
  * **[Pursuit Mode]:** Launches a high-impact flanking sequence affecting the retreating unit's posterior armor variables.
  * **[Aggro Redirection]:** Switches threat parameters immediately to identify and neutralize isolated baseline infantry assets.

### 4. Robust Type-Safety & Mutability Prevention
* **Java Enum Hardlocking:** Unit base structures (Max Armor, Speed Co-efficients) are strictly contained inside Java Enums to ensure rigid immutability across multi-threaded execution windows.
* **Refresh Thresholding:** Fighting units reset their armor parameters cleanly at each localized skirmish boundary initialization, eliminating state corruption bugs.

---

## 📊 Deployment Roster Configuration (54-Card Split)

The program simulates a balanced 27-vs-27 total war matrix modeled directly on playing card splits:

| Faction Name | Suite Representation | Roster Breakdown per Side (27 Total) |
| :--- | :--- | :--- |
| **The Crimson Dynasty** | ❤️ Hearts / ♦️ Diamonds | 1 Emperor, 2 Royal Guards (A), 6 Elite Generals (K/Q/J), 4 Armored Elephants (10/2), 4 Cavalry (3/9), 10 Heavy Infantry (4-8) |
| **The Obsidian Kingdom** | ♠️ Spades / ♣️ Clubs | 1 Emperor, 2 Royal Guards (A), 6 Elite Generals (K/Q/J), 4 Armored Elephants (10/2), 4 Cavalry (3/9), 10 Heavy Infantry (4-8) |

---

## 🚀 Execution & Runtime Logs Sample

```text
================== ⚔️ 27-MAN HEAVY ARMOR WAR ENGINE (FIXED) ⚔️ ==================
Core AI Protocols: Royal Protection Enabled | RNG Battlefields | Tactical Retreat | State Visualized

----------------------------------------------------------------------
🏹 BATTLEFIELD ENGAGEMENT - ROUND 42 🏹
🔴 Crimson Dynasty: 1/27 Active | ⚫ Obsidian Kingdom: 8/27 Active
----------------------------------------------------------------------
👑 THE MONARCH STANDS ALONE! The Crimson Dynasty's Emperor draws his blade for a final last stand!
⚔️ SKIRMISH INITIALIZED: [The Crimson Dynasty - Emperor (Monarch)] vs [The Obsidian Kingdom - Heavy Infantry (4-8)]
CLANG! CLANG! CLANG! 
💥 ARMOR COMPROMISED! The Crimson Dynasty [Emperor (Monarch)] drives a lethal blade piercing through the armor gap!
💀 The Obsidian Kingdom [Heavy Infantry (4-8)] [KIA - Card Flipped Face-Down Into Graveyard Area]
```

---

## 💻 Tech Stack & Environment
* **Language:** Java (JDK 8 or higher)
* **Development Environment:** Eclipse IDE
* **Programming Paradigm:** Object-Oriented Programming (OOP)
* **Design Pattern:** Finite State Machine (FSM) / Behavioral AI Trees
