package common;

import java.util.Random;

/**
 * Singleton utility for random number generation.
 * Centralizes randomness to allow for deterministic seeding during testing.
 */
public class RandomGenerator {
    private static RandomGenerator instance;
    private final Random random;

    private RandomGenerator() {
        this.random = new Random();
    }

    public static RandomGenerator getInstance() {
        if (instance == null) {
            instance = new RandomGenerator();
        }
        return instance;
    }

    /**
     * Set a fixed seed for reproducible results (Testing/Debugging).
     * @param seed The long seed value.
     */
    public void setSeed(long seed) {
        this.random.setSeed(seed);
    }

    public int nextInt(int bound) {
        return random.nextInt(bound);
    }

    public double nextDouble() {
        return random.nextDouble();
    }

    /**
     * Returns a random integer in the range [min, max].
     */
    public int nextInt(int min, int max) {
        return random.nextInt((max - min) + 1) + min;
    }
}