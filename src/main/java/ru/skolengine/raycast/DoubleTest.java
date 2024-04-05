package ru.skolengine.raycast;

public class DoubleTest {

    public static void main(String[] args) {
        final double x = 10019.999999999937;
        System.out.println((1 - ((x - (int) x) * (x - (int) x) * (x - (int) x))));
        double xInc = x;
        final double inc = 0.01;
        final int count = 2000;
        for (int i = 0; i < count; i++) {
            xInc += inc;
        }
        System.out.println(x + inc * count);
        System.out.println(xInc);
    }

}
