package hoten.perlin;

import hoten.perlin.interpolator.CosineInterpolator;
import hoten.perlin.interpolator.Interpolator;
import java.math.BigInteger;
import java.util.Random;

/**
 * Perlin1d.java
 *
 * @author Hoten
 */
public class Perlin1d {

    private Interpolator interpolator;
    private int p1, p2, p3, seed, octaves;
    private double persistence;
    private int[] p1s, p2s, p3s;//primes

    public Perlin1d(double persistence, int octaves, int seed, Interpolator interpolator) {
        this.seed = seed;
        this.octaves = octaves;
        this.persistence = persistence;
        this.interpolator = interpolator;
        setPrimes(seed);
    }

    public Perlin1d(double persistence, int octaves, int seed) {
        this.seed = seed;
        this.octaves = octaves;
        this.persistence = persistence;
        interpolator = new CosineInterpolator();
        setPrimes(seed);
    }

    //returns a noise array which loops seamlessly
    //see http://webstaff.itn.liu.se/~stegu/TNM022-2005/perlinnoiselinks/perlin-noise-math-faq.html
    public double[] tile(double[] noise) {
        //treat noise as a function with domain -t to t, where t = noise.length / 2
        //i.e. noise[0] = f(-t), noise[noise.length-1] = f(t)
        int t = noise.length / 2;
        double[] tiled = new double[t];
        for (int z = 0; z < t; z++) {
            tiled[z] = ((t - z) * noise[z + t] + (z) * noise[z]) / (t);
        }
        return tiled;
    }

    public double[] createTiledArray(int size) {
        double[] tiledNoise = tile(createRawArray(size * 2));
        clamp(tiledNoise);
        return tiledNoise;
    }

    public double[] createArray(int size) {
        double[] noise = createRawArray(size);
        clamp(noise);
        return noise;
    }

    //not clamped
    private double[] createRawArray(int size) {
        double[] y = new double[size];
        final int regionWidth = 3;
        int smallSeed = Math.abs(seed / 1000);
        for (int i = 0; i < size; i++) {
            double nx = 1.0 * i / size * regionWidth;
            y[i] = perlinNoise1(smallSeed + nx);
        }
        return y;
    }

    //stretched range of the noise from 0.0 to 1.0
    private void clamp(double[] noise) {
        int size = noise.length;
        double max = -1, min = 1;
        for (int i = 0; i < size; i++) {
            max = Math.max(max, noise[i]);
            min = Math.min(min, noise[i]);
        }
        double range = max - min;
        for (int i = 0; i < size; i++) {
            noise[i] = (noise[i] - min) / range;
        }
    }

    /**
     * Generates three random primes for each octave. These numbers are used in
     * the noise function, and contribute to it's quasi-nondeterministic nature.
     */
    private void setPrimes(int seed) {
        p1s = new int[octaves];
        p2s = new int[octaves];
        p3s = new int[octaves];
        for (int i = 0; i < octaves; ++i) {
            Random ran = new Random(i + seed);
            p1s[i] = BigInteger.probablePrime(23, ran).intValue();
            p2s[i] = BigInteger.probablePrime(24, ran).intValue();
            p3s[i] = BigInteger.probablePrime(25, ran).intValue();
        }
    }

    private double perlinNoise1(double x) {
        double total = 0;
        int f = 1;
        double a = 1;
        for (int i = 0; i < octaves; ++i) {
            //set this octave's primes. this saves on array lookups
            p1 = p1s[i];
            p2 = p2s[i];
            p3 = p3s[i];
            total += interpolatedNoise1(x * f) * a;
            f *= 2;
            a *= persistence;
        }
        return total;
    }

    private double noise1(int x) {
        x = (x << 13) ^ x;
        return (1.0 - ((x * (x * x * p1 + p2) + p3) & 0x7fffffff) / 1073741824.0);
    }

    private double smoothedNoise1(int x) {
        return noise1(x) / 2 + noise1(x - 1) / 4 + noise1(x + 1) / 4;
    }

    private double interpolatedNoise1(double x) {
        int intX = (int) x;
        double fractionalX = x - intX;

        double v1 = smoothedNoise1(intX);
        double v2 = smoothedNoise1(intX + 1);

        return interpolate(v1, v2, fractionalX);
    }

    private double interpolate(double v1, double v2, double fractionalX) {
        return interpolator.interpolate(v1, v2, fractionalX);
    }
}
