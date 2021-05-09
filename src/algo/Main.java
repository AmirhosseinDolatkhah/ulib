package algo;

import java.util.*;

public class Main {

    private static long ans = 0;
    private static int index = -1;
    private final static Map<Long, Integer> map = new HashMap<>();
    private static long[] pivots;

    private static void solve(int l, int r) {
        if (r - l >= 1)
            index++;
        if (r - l <= 1)
            return;
        ans += r - l - 1;
        int id = map.get(pivots[index]);
        solve(l, id);
        solve(id + 1, r);
    }

    public static void main(String[] args) {
        Scanner scanner = new Scanner(System.in);
        scanner.nextLine();
        long[] arr = Arrays.stream(scanner.nextLine().split(" ")).mapToLong(Long::parseLong).toArray();
        pivots = Arrays.stream(scanner.nextLine().split(" ")).mapToLong(Long::parseLong).toArray();
        Arrays.sort(arr);
        for (int i = 0; i < arr.length; i++)
            map.put(arr[i], i);
        solve(0, arr.length);
        System.out.println(ans);
    }
}
