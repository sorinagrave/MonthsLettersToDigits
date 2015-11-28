package net.ctomer.sorina;

class Main {

    //JAN - divisible by 31
    //FEB - divisible by 28
    //APR - divisible by 30
    //MAY - divisible by 31

    public static void main(String[] args) {
        long before = System.nanoTime();
        MonthProcessor hashMapBased = new HashMapBasedMonthProcessor();
        hashMapBased.findFebruary();
        long after = System.nanoTime();
        System.out.println(String.format("Hash map way of determining FEB took %d nanoseconds", after - before));

        before = System.nanoTime();
        MonthProcessor bitwiseBased = new BitwiseBasedMonthProcessor();
        bitwiseBased.findFebruary();
        after = System.nanoTime();
        System.out.println(String.format("Bitwise way of determining FEB took %d nanoseconds", after - before));
    }


}
