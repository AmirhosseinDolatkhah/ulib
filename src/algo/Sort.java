package algo;

import utils.Utils;

import java.util.Arrays;

public final class Sort {
    public static void mergeSort(int[] arr) {
        mergeSort(arr, 0, arr.length);
    }

    private static void mergeSort(int[] arr, int start, int end) {
        if (start >= --end)
            return;
        var middle = (start + end) / 2;
        mergeSort(arr, start, middle);
        mergeSort(arr, middle + 1, end);

        merge(arr, start, end);
    }

    private static void merge(int[] arr, int start, int end) {
        var middle = (start + end) / 2;
        int lIndex  = 0;
        int rIndex = 0;
        int[] left = new int[middle - start + 1];
        int[] right = new int[end - middle];
        System.arraycopy(arr, start, left, 0, left.length);
        System.arraycopy(arr, middle + 1, right, 0, right.length);
        while (lIndex < left.length || rIndex < right.length) {
            if (rIndex < right.length && lIndex < left.length) {
                if (left[lIndex] <= right[rIndex])
                    arr[start++] = left[lIndex++];
                else
                    arr[start++] = right[rIndex++];
            }

            if (lIndex >= left.length)
                arr[start++] = right[rIndex++];

            if (rIndex >= right.length && lIndex < left.length)
                arr[start++] = left[lIndex++];
        }
    }

    //////////////

    public static void quickSort(int[] arr) {
        quickSort(arr, 0, arr.length);
    }

    private static void quickSort(int[] arr, int start, int end) {
        if (start >= --end)
            return;
        var partition = partition(arr, start, end);

        quickSort(arr, start, partition - 1);
        quickSort(arr, partition + 1, end);
    }

    private static int partition(int[] arr, int start, int end) {
        var pivot = arr[end];
        int index = start;
        for (int i = start; i < end; i++)
            if (arr[i] < pivot) {
                var temp = arr[i];
                arr[i] = arr[index];
                arr[index++] = temp;
            }
        var temp = arr[end];
        arr[end] = arr[index];
        arr[index] = temp;
        return index;
    }

    /////////////

    private static void checkTimeOfIntArraySort(int len, SortArrayFunc sorter) {
        var t = System.currentTimeMillis();
        int[] src = new int[len];
        for (int i = 0; i < len; i++)
            src[i] = (int) (Math.random() * Integer.MAX_VALUE);
        System.out.println("-".repeat(20));
        System.out.println("Time to generate array: " + (System.currentTimeMillis() - t) + " ms");
        t = System.currentTimeMillis();
        sorter.sort(src);
        System.out.println("Time to sort: " + (System.currentTimeMillis() - t) + " ms");
        System.out.println("-".repeat(20));
    }

    public static void main(String[] args) {
//        checkTimeOfIntArraySort(2_000_000, Sort::mergeSort);
        int[] arr = new int[] {1, 4, 3, 5, 6, 7, 2, 4, 2, 5, 12, 43, 1, 3, 5, 32, 2, 33};
        int[] arr2 = new int[] {1, 3, 2, 4, 5, 7, 6, 9, 8};
        mergeSort(arr);
        System.out.println(Arrays.toString(arr));
    }

    @FunctionalInterface
    private interface SortArrayFunc {
        void sort(int[] src);
    }
}
