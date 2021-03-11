package ch.zhaw.catan;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class DiceTest {

    @Test
    void testMinAmountTwoDices() {
        Dice dice = new Dice();
        int sum = dice.rollTheDice(2);
        assertTrue(sum >= 2);
    }

    @Test
    void testMaxAmountTwoDices() {
        Dice dice = new Dice();
        int sum = dice.rollTheDice(2);
        assertTrue(sum <= 12);
    }

    @Test
    void testMinAmountOneDice() {
        Dice dice = new Dice();
        int sum = dice.rollTheDice(1);
        assertTrue(sum >= 1);
    }

    @Test
    void testMaxAmountOneDice() {
        Dice dice = new Dice();
        int sum = dice.rollTheDice(1);
        assertTrue(sum <= 6);
    }

}