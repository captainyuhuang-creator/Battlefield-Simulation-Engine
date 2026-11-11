import static org.junit.jupiter.api.Assertions.*;
import java.util.Random;
import org.junit.jupiter.api.Test;

class ArmyTest {
    @Test
    void armyStartsWith27Soldiers() {
        Army army = new Army("Red");
        assertEquals(27, army.getTotalCount());
        assertEquals(27, army.getAliveCount());
    }

    @Test
    void emperorIsProtectedWhileOthersAlive() {
        Army army = new Army("Red");
        Random rand = new Random(42);
        for (int i = 0; i < 500; i++) {
            assertNotEquals(Rank.EMPEROR, army.selectCombatant(rand).getRank());
        }
    }

    @Test
    void emperorFightsWhenLastSurvivor() {
        Army army = new Army("Red");
        for (Soldier s : army.getSoldiers()) {
            if (s.getRank() != Rank.EMPEROR) {
                s.takeDamage(100);
            }
        }
        assertEquals(1, army.getAliveCount());
        assertEquals(Rank.EMPEROR, army.selectCombatant(new Random()).getRank());
    }

    @Test
    void infantryDeathReducesAliveCount() {
        Army army = new Army("Red");
        Soldier infantry = army.getRandomAliveInfantry(new Random());
        infantry.takeDamage(100);
        assertEquals(26, army.getAliveCount());
    }

    @Test
    void noInfantryReturnsNullAndFallbackFindsOtherTarget() {
        Army army = new Army("Red");
        for (Soldier s : army.getSoldiers()) {
            if (s.getRank() == Rank.INFANTRY) {
                s.takeDamage(100);
            }
        }
        Random rand = new Random();
        assertNull(army.getRandomAliveInfantry(rand));

        Soldier excluded = army.getSoldiers().get(3);
        Soldier target = army.getRandomAliveNonEmperor(excluded, rand);
        assertNotNull(target);
        assertNotEquals(Rank.EMPEROR, target.getRank());
        assertNotSame(excluded, target);
    }

    @Test
    void soldierListCannotBeModifiedFromOutside() {
        Army army = new Army("Red");
        assertThrows(UnsupportedOperationException.class, () -> army.getSoldiers().clear());
    }
}