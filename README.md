# Battlefield Simulation Engine

A text-based combat simulation written in Java using object-oriented design. A standard 54-card deck (52 cards + 2 Jokers) is turned into two armies of 27 heavily armored soldiers who fight until one side is wiped out.

Each war is resolved as a series of one-on-one skirmishes with random combat modes, a royal-protection rule for the Emperor, and a tactical retreat system. The logic is split into focused classes and covered by JUnit 5 tests.

---

## Features

- **Clean OOP structure:** data (`Rank`, `Soldier`, `Army`) is separated from battle logic (`Battle`) and the program entry point.
- **Encapsulation:** all fields are `private`, state is changed only through methods, and `Army` exposes its soldiers as an unmodifiable list.
- **Input validation:** negative damage, `null` or blank names, and attacks involving defeated soldiers are rejected with exceptions.
- **Emperor protection:** the Emperor (Joker) only enters battle when he is the last survivor of his army.
- **Three random combat modes:** single-sided, simultaneous, and alternating attacks.
- **Tactical retreat system:** elite units in a weakened army may try to withdraw, with several possible outcomes.
- **Deterministic testing:** `Battle` takes its `Random` as a constructor argument, so tests can script every random decision.

---

## Game Rules

### Armies

Each army has 27 soldiers. The Crimson Dynasty uses Hearts and Diamonds, and the Obsidian Kingdom uses Spades and Clubs. Each side also gets one Joker.

| Unit | Cards | Count | Max Armor | Damage per Hit (`speed`) |
| :--- | :--- | :---: | :---: | :---: |
| Emperor | Joker | 1 | 6 | 3 |
| Royal Guard | Ace | 2 | 5 | 2 |
| General | King / Queen / Jack | 6 | 5 | 3 |
| Armored Elephant | 10 / 2 | 4 | 8 | 1 |
| Cavalry | 3 / 9 | 4 | 4 | 2 |
| Heavy Infantry | 4 - 8 | 10 | 3 | 1 |

A hit removes armor equal to the attacker's damage value. A soldier is defeated when armor reaches 0.

### War flow

1. Each round, both armies send one random soldier into a skirmish (the Emperor is excluded while other troops remain).
2. Surviving fighters have their armor restored at the start of every skirmish.
3. Each combat exchange uses a randomly chosen mode:
   - `SINGLE_SIDED`: one randomly chosen soldier strikes once.
   - `SIMULTANEOUS`: both strike at the same time, so a double knockout is possible.
   - `ALTERNATING`: one strikes first, and the other counterattacks only if still alive.
4. A skirmish ends when one soldier is defeated or a retreat succeeds.
5. The war ends when one army has no soldiers left.

### Tactical retreat

From the 4th exchange of a skirmish, there is a 25% chance per exchange that a retreat is considered. It is only possible for a **General** or **Armored Elephant** whose army has **fewer than 15** survivors.

- **Retreat fails (50%):** the enemy gets a free strike and the fight continues.
- **Retreat succeeds (50%)**, with one of three outcomes:
  - **Clean escape:** the soldier withdraws with no damage.
  - **Enemy pursuit:** the enemy strikes the retreating soldier. If it survives, the retreat completes with damage; if not, the soldier is eliminated.
  - **Aggro redirection:** the enemy attacks a random surviving Infantry instead (or another non-Emperor unit if no Infantry remain), and the retreating soldier escapes.

---

## Project Structure

```text
Battlefield-Simulation-Engine/
├── PlayingCardWarGame.java   # Entry point: creates both armies and starts the war
├── Battle.java               # Combat engine: attacks, combat modes, retreats, skirmishes, war loop
├── Army.java                 # Roster management, alive counts, combatant and target selection
├── Soldier.java              # Single unit: armor, damage, life state, validation
├── Rank.java                 # Enum: unit types with max armor and damage values
├── AttackMode.java           # Enum: SINGLE_SIDED, SIMULTANEOUS, ALTERNATING
├── SoldierTest.java          # Tests: validation, lethal damage, armor reset
├── ArmyTest.java             # Tests: roster size, Emperor protection, fallback targeting
└── BattleTest.java           # Tests: combat modes, retreats, validation (scripted Random)
```

---

## Getting Started

### Requirements

- Java 11 or later (developed with JavaSE-21 in Eclipse)
- JUnit 5 (only needed to run the tests)

### Run the simulation

In Eclipse: right-click `PlayingCardWarGame.java` → **Run As → Java Application**.

From the command line:

```bash
javac Rank.java AttackMode.java Soldier.java Army.java Battle.java PlayingCardWarGame.java
java PlayingCardWarGame
```

The result is different on every run because combat is random.

### Run the tests

In Eclipse: add JUnit 5 to the build path (**Project → Properties → Java Build Path → Add Library → JUnit 5**), then right-click the project → **Run As → JUnit Test**.

---

## Testing

| Test class | What it checks |
| :--- | :--- |
| `SoldierTest` | Negative damage and invalid constructor arguments are rejected; lethal damage clamps armor to 0; a dead soldier is not revived by an armor reset |
| `ArmyTest` | Army starts with 27 soldiers; Emperor is never selected while others live and fights when last; Infantry deaths reduce the alive count; fallback target selection works when no Infantry remain; the soldier list cannot be modified from outside |
| `BattleTest` | Dead soldiers cannot attack and cannot be attacked; simultaneous attacks can kill both soldiers; no counterattack after the second soldier is defeated; failed retreat returns `false` and the fight continues; aggro redirection falls back correctly; retreat eligibility rules; full wars always finish |

`BattleTest` uses a `ScriptedRandom` class that returns predefined values, so edge cases can be reproduced exactly instead of relying on luck.

---

## Sample Output

An excerpt from one run (output varies every time):

```text
================== ⚔️ 27-MAN HEAVY ARMOR WAR ENGINE ⚔️ ==================
Core Protocols: Royal Protection | Random Combat Modes | Tactical Retreat | Dynamic Target Acquisition

----------------------------------------------------------------------
🏹 BATTLEFIELD ENGAGEMENT - ROUND 42
🔴 Crimson Dynasty: 1/27 Active | ⚫ Obsidian Kingdom: 8/27 Active
----------------------------------------------------------------------
👑 THE MONARCH STANDS ALONE! The Crimson Dynasty (Hearts/Diamonds)'s Emperor enters the battlefield!

⚔️ SKIRMISH INITIALIZED: [The Crimson Dynasty (Hearts/Diamonds) - Emperor (Joker)] vs [The Obsidian Kingdom (Spades/Clubs) - Heavy Infantry (4-8)]

========== COMBAT EXCHANGE 1 ==========

CLANG! ⚔️

⚔️ COMBAT MODE: SINGLE_SIDED
🗡️ ONE SOLDIER STRIKES!
The Crimson Dynasty (Hearts/Diamonds) [Emperor (Joker)] attacks The Obsidian Kingdom (Spades/Clubs) [Heavy Infantry (4-8)] for 3 damage!
💥 ARMOR COMPROMISED! The Obsidian Kingdom (Spades/Clubs) [Heavy Infantry (4-8)] is defeated!

========== SKIRMISH REPORT ==========
💀 The Obsidian Kingdom (Spades/Clubs) [Heavy Infantry (4-8)] [KIA - Card Flipped Face-Down Into Graveyard Area]
🔴 Remaining Crimson Troops: 1
⚫ Remaining Obsidian Troops: 7
```

---

## Possible Improvements

- Move the project to Maven or Gradle so JUnit is managed automatically.
- Put the classes into a package instead of the default package.
- Make army composition and retreat probabilities configurable.
- Add tests for the full retreat outcome table (pursuit and clean escape).
