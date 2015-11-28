package net.ctomer.sorina;

import java.util.*;

public class Main {

    //JAN - divisible by 31
    //FEB - divisible by 28
    //APR - divisible by 30
    //MAY - divisible by 31


    public static void main(String[] args) {
        long before = System.currentTimeMillis();
        HashMapBasedNumberProcessor hp = new HashMapBasedNumberProcessor();
        hp.findFebruary();
        long after = System.currentTimeMillis();
        System.out.println(String.format("Hash map way of determining FEB took %d milliseconds", after - before));
        before = System.currentTimeMillis();
        BitwiseBasedNumberProcessor np = new BitwiseBasedNumberProcessor();
        np.processMonths();
        after = System.currentTimeMillis();
        System.out.println(String.format("Bitwise way of determining FEB took %d milliseconds", after - before));
    }


}
