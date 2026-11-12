import static org.junit.jupiter.api.Assertions.*;
import org.junit.jupiter.api.Test;

class SoldierTest {
    @Test
    void negativeDamageIsRejected() {
        Soldier s = new Soldier("Red", Rank.INFANTRY);
        
        assertThrows(IllegalArgumentException.class, () -> s.takeDamage(-1));
        assertEquals(3, s.getCurrentArmor());
    }

    @Test
    void invalidConstructorArgumentsAreRejected() {
        assertThrows(IllegalArgumentException.class, () -> new Soldier(null, Rank.GUARD));
        assertThrows(IllegalArgumentException.class, () -> new Soldier(" ", Rank.GUARD));
        assertThrows(IllegalArgumentException.class, () -> new Soldier("Red", null));
    }

    @Test
    void lethalDamageKillsAndClampsArmorToZero() {
        Soldier s = new Soldier("Red", Rank.INFANTRY);
        
        s.takeDamage(99);
        
        assertFalse(s.isAlive());
        assertEquals(0, s.getCurrentArmor());
    }

    @Test
    void resetArmorDoesNotReviveDeadSoldier() {
        Soldier s = new Soldier("Red", Rank.INFANTRY);
        
        s.takeDamage(99);
        s.resetArmor();
        
        assertFalse(s.isAlive());
        assertEquals(0, s.getCurrentArmor());
    }
}