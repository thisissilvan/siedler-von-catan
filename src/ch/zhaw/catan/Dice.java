package ch.zhaw.catan;

import java.util.Random;

/**
 * The class Dice can throw two dices and returns an Integer-number.
 * @author Silvan
 * @version 11-29-2019
 */
public class Dice {
    private final int MAX_AMOUNT_OF_DICE = 6;
    private final int CORRECT_IF_ZERO = 1;
    private Random random = new Random();

    /**
     * Each player can roll the dice. This method throws two dices at the same time and returns an Integer-number
     * that shows the value of both the dices.
     * @return the value of both the dices
     */
    public int rollTheDice(int numberOfDices) {
        int diceSum = 0;
        for(int i = 0; i < numberOfDices; i++) {
            diceSum = diceSum + random.nextInt(MAX_AMOUNT_OF_DICE) + CORRECT_IF_ZERO;
        }
        return diceSum;

    }
}
