import java.util.Comparator;
import java.util.Random;
import java.util.List;
import java.util.ArrayList;

public class Sorting {

    /**
     * Implement bubble sort.
     *
     * It should be:
     *  in-place
     *  stable
     *
     * Have a worst case running time of:
     *  O(n^2)
     *
     * And a best case running time of:
     *  O(n)
     *
     * Any duplicates in the array should be in the same relative position after
     * sorting as they were before sorting.
     *
     * @throws IllegalArgumentException if the array or comparator is null
     * @param <T> data type to sort
     * @param arr the array that must be sorted after the method runs
     * @param comparator the Comparator used to compare the data in arr
     */
    public static <T> void bubblesort(T[] arr, Comparator<T> comparator) {
        if (arr == null || comparator == null) {
            throw new IllegalArgumentException(
                    "One of the inputs supplied was null."
            );
        }

        boolean isSorted = false;
        int length = arr.length;
        while (!isSorted) {
            isSorted = true;
            for (int i = 0; i < length - 1; i++) {
                if (comparator.compare(arr[i], arr[i + 1]) > 0) {
                    T temp = arr[i + 1];
                    arr[i + 1] = arr[i];
                    arr[i] = temp;
                    isSorted = false;
                }
            }
            length--;
        }
    }

    /**
     * Implement insertion sort.
     *
     * It should be:
     *  in-place
     *  stable
     *
     * Have a worst case running time of:
     *  O(n^2)
     *
     * And a best case running time of:
     *  O(n)
     *
     * Any duplicates in the array should be in the same relative position after
     * sorting as they were before sorting.
     *
     * @throws IllegalArgumentException if the array or comparator is null
     * @param <T> data type to sort
     * @param arr the array that must be sorted after the method runs
     * @param comparator the Comparator used to compare the data in arr
     */
    public static <T> void insertionsort(T[] arr, Comparator<T> comparator) {
        if (arr == null || comparator == null) {
            throw new IllegalArgumentException(
                    "One of the inputs supplied was null."
            );
        }

        int length = arr.length;
        for (int i = 0; i < length; i++) {
            for (int j = i; j > 0; j--) {
                if (comparator.compare(arr[j], arr[j - 1]) < 0) {
                    T temp = arr[j];
                    arr[j] = arr[j - 1];
                    arr[j - 1] = temp;
                }
            }
        }
    }

    /**
     * Implement shell sort.
     *
     * It should be:
     *  in-place
     *
     * Have a worst case running time of:
     *  O(n^2)
     *
     * And a best case running time of:
     *  O(n log(n))
     *
     * Note that there may be duplicates in the array.
     *
     * @throws IllegalArgumentException if the array or comparator is null
     * @param <T> data type to sort
     * @param arr the array that must be sorted after the method runs
     * @param comparator the Comparator used to compare the data in arr
     */
    public static <T> void shellsort(T[] arr, Comparator<T> comparator) {
        if (arr == null || comparator == null) {
            throw new IllegalArgumentException(
                    "One of the inputs supplied was null."
            );
        }

        int length = arr.length;
        int k;
        for (int i = length / 2; i > 0; i /= 2) {
            for (int j = i; j < length; j++) {
                T temp = arr[j];
                for (k = j; k >= i; k -= i) {
                    if (comparator.compare(temp, arr[k - i]) < 0) {
                        arr[k] = arr[k - i];
                    }
                }
                arr[k] = temp;
            }
        }
    }

    /**
     * Implement quick sort.
     *
     * Use the provided random object to select your pivots.
     * For example if you need a pivot between a (inclusive)
     * and b (exclusive) where b > a, use the following code:
     *
     * int pivotIndex = r.nextInt(b - a) + a;
     *
     * It should be:
     *  in-place
     *
     * Have a worst case running time of:
     *  O(n^2)
     *
     * And a best case running time of:
     *  O(n log n)
     *
     * Note that there may be duplicates in the array.
     *
     * @throws IllegalArgumentException if the array or comparator or rand is
     * null
     * @param <T> data type to sort
     * @param arr the array that must be sorted after the method runs
     * @param comparator the Comparator used to compare the data in arr
     * @param rand the Random object used to select pivots
     */
    public static <T> void quicksort(T[] arr, Comparator<T> comparator,
            Random rand) {
        if (arr == null || comparator == null || rand == null) {
            throw new IllegalArgumentException(
                    "One of the inputs supplied was null."
            );
        }

        arr = quickSort(arr, 0, arr.length - 1, comparator, rand);
    }

    private static <T> T[] quickSort(T[] arr, int lowerIndex, int higherIndex, Comparator<T> comparator, Random rand) {
        int i = lowerIndex;
        int j = higherIndex;
        int pivotIndex = rand.nextInt(j - i) + i;

        T pivot = arr[pivotIndex];

        while (i <= j) {
            while (comparator.compare(arr[i], pivot) < 0) {
                i++;
            }
            while (comparator.compare(arr[j], pivot) > 0) {
                j--;
            }
            if (i <= j) {
                arr = exchange(arr, i, j);
                i++;
                j--;
            }
        }

        if (lowerIndex < j){
            arr = quickSort(arr, lowerIndex, j, comparator, rand);
        }

        if (i < higherIndex) {
            arr = quickSort(arr, i, higherIndex, comparator, rand);
        }

        return arr;
    }

    private static <T> T[] exchange(T[] arr, int i, int j) {
        T temp = arr[i];
        arr[i] = arr[j];
        arr[j] = temp;
        return arr;
    }

    /**
     * Implement merge sort.
     *
     * It should be:
     *  stable
     *
     * Have a worst case running time of:
     *  O(n log n)
     *
     * And a best case running time of:
     *  O(n log n)
     *
     * You can create more arrays to run mergesort, but at the end,
     * everything should be merged back into the original T[]
     * which was passed in.
     *
     * ********************* IMPORTANT ************************
     * FAILURE TO DO SO MAY CAUSE ClassCastException AND CAUSE
     * YOUR METHOD TO FAIL ALL THE TESTS FOR MERGE SORT
     * ********************************************************
     *
     * Any duplicates in the array should be in the same relative position after
     * sorting as they were before sorting.
     *
     * @throws IllegalArgumentException if the array or comparator is null
     * @param <T> data type to sort
     * @param arr the array to be sorted
     * @param comparator the Comparator used to compare the data in arr
     */
    public static <T> void mergesort(T[] arr, Comparator<T> comparator) {
        if (arr == null || comparator == null) {
            throw new IllegalArgumentException(
                    "One of the inputs supplied was null."
            );
        }
        arr = doMergeSort(arr, 0, arr.length - 1, comparator);
    }

    private static <T> T[] doMergeSort(T[] arr, int lowerIndex, int higherIndex, Comparator<T> comparator) {
        if (lowerIndex < higherIndex) {
            int middle = lowerIndex + (higherIndex - lowerIndex) / 2;
            arr = doMergeSort(arr, lowerIndex, middle, comparator);
            arr = doMergeSort(arr, middle + 1, higherIndex, comparator);
            arr = mergeParts(arr, lowerIndex, middle, higherIndex, comparator);
        }
        return arr;
    }

    private static <T> T[] mergeParts(T[] arr, int lowerIndex, int middle, int higherIndex, Comparator<T> comparator) {
        T[] temp = (T[]) new Object[arr.length];
        for (int i = lowerIndex; i <= higherIndex; i++) {
            temp[i] = arr[i];
        }
        int i = lowerIndex;
        int j = middle + 1;
        int k = lowerIndex;
        while (i <= middle && j <= higherIndex) {
            if (comparator.compare(temp[i], temp[j]) <= 0) {
                arr[k] = temp[i];
                i++;
            } else {
                arr[k] = temp[j];
                j++;
            }
            k++;
        }
        while (i <= middle) {
            arr[k] = temp[i];
            k++;
            i++;
        }
        return arr;
    }

    /**
     * Implement radix sort.
     *
     * Remember you CANNOT convert the ints to strings.
     *
     * It should be:
     *  stable
     *
     * Have a worst case running time of:
     *  O(kn)
     *
     * And a best case running time of:
     *  O(kn)
     *
     * Any duplicates in the array should be in the same relative position after
     * sorting as they were before sorting.
     *
     * You may use an ArrayList or LinkedList if you wish,
     * but it may only be used inside radixsort and any radix sort helpers
     * Do NOT use these classes with other sorts.
     *
     * @throws IllegalArgumentException if the array is null
     * @param arr the array to be sorted
     * @return the sorted array
     */
    public static int[] radixsort(int[] arr) {
        if (arr == null) {
            throw new IllegalArgumentException(
                    "The input provided was null."
            );
        }

        int RADIX = 10;
        List<Integer>[] bucket = new ArrayList[RADIX];
        for (int i = 0; i < bucket.length; i++) {
            bucket[i] = new ArrayList<Integer>();
        }

        boolean maxLength = false;
        int tmp = -1, placement = 1;
        while (!maxLength) {
            maxLength = true;
            for (Integer i : arr) {
                tmp = i / placement;
                bucket[tmp % RADIX].add(i);
                if (maxLength && tmp > 0) {
                    maxLength = false;
                }
            }
            int a = 0;
            for (int b = 0; b < RADIX; b++) {
                for (Integer i : bucket[b]) {
                    arr[a++] = i;
                }
                bucket[b].clear();
            }
            placement *= RADIX;
        }

        return arr;
    }
}