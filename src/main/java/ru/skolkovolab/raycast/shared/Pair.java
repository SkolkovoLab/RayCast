package ru.skolkovolab.raycast.shared;

public class Pair<A, B> {
    public A a;
    public B b;

    public Pair() {
        this(null, null);
    }

    public Pair(A a, B b) {
        this.a = a;
        this.b = b;
    }
}
