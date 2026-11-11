import java.util.Random;

public class PlayingCardWarGame {
    public static void main(String[] args) {
        System.out.println("================== ⚔️ 27-MAN HEAVY ARMOR WAR ENGINE ⚔️ ==================");
        System.out.println("Core Protocols: Royal Protection | Random Combat Modes | "
                + "Tactical Retreat | Dynamic Target Acquisition\n");

        Army red = new Army("The Crimson Dynasty (Hearts/Diamonds)");
        Army black = new Army("The Obsidian Kingdom (Spades/Clubs)");

        new Battle(new Random(), System.out, 100).runWar(red, black);
    }
}