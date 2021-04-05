package notmine;

import java.util.Arrays;
import java.util.Scanner;

public class HW1Q1 {
    private final static Scanner scanner = new Scanner(System.in);

    private static void q1() {
        int[] counters = new int[4];
        scanner.nextLine();
        Integer[] arr = Arrays.stream(scanner.nextLine().split(" ")).map(Integer::parseInt).toArray(Integer[]::new);
        for (int i : arr)
            counters[i - 1]++;
        int diff = counters[2] - counters[0];
        if (diff == 0) {
            counters[3] += counters[2] + (int) Math.ceil(counters[1] / 2.0);
        } else if (diff < 0) {
            counters[3] += counters[2] + counters[1] / 2;
            if (counters[1] % 2 == 1) {
                diff += diff < -1 ? 2 : 1;
                counters[3]++;
            }
            counters[3] += (int) Math.ceil(-diff / 4.0);
        } else {
            counters[3] += counters[0] + (int) Math.ceil(counters[1] / 2.0) + diff;
        }
        System.out.println(counters[3]);
    }

    public static void main(String[] args) {
        q1();
    }
}
