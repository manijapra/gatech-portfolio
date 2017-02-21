import java.util.List;
import java.util.ArrayList;

public class StringSearching implements StringSearchingInterface {

    @Override
    public List<Integer> kmp(CharSequence pattern, CharSequence text) {
        if (pattern == null || text == null || pattern.length() == 0) {
            throw new IllegalArgumentException(
                    "One or more of the parameters are invalid."
            );
        }

        ArrayList<Integer> list = new ArrayList<>();
        if (text.length() == 0) {
            return list;
        }

        int[] fail = buildFailureTable(pattern);
        int j = 0;
        int k = 0;

        while (j < text.length()) {
            if (text.charAt(j) == pattern.charAt(k)) {
                if (k == pattern.length() - 1) {
                    list.add(j - pattern.length() + 1);
                    k = 0;
                }
                j++;
                k++;
            } else if (k > 0) {
                k = fail[k - 1];
            } else {
                j++;
            }
        }

        return list;
    }

    @Override
    public int[] buildFailureTable(CharSequence pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException(
                    "The parameter is invalid."
            );
        }

        int[] arr = new int[pattern.length()];
        int j = 1;
        int k = 0;

        while (j < pattern.length()) {
            if (pattern.charAt(j) == pattern.charAt(k)) {
                arr[j] = k + 1;
                j++;
                k++;
            } else if (k > 0) {
                k = arr[k - 1];
            } else {
                j++;
            }
        }

        return arr;
    }

    @Override
    public List<Integer> boyerMoore(CharSequence pattern, CharSequence text) {
        if (pattern == null || pattern.length() == 0 || text == null) {
            throw new IllegalArgumentException(
                    "One or more of the parameters are invalid."
            );
        }

        int n = text.length();
        int m = pattern.length();

        int[] last = buildLastTable(pattern);
        ArrayList<Integer> list = new ArrayList<>();

        int i = m - 1;
        int j = m - 1;

        while (i < n - 1) {
            char cur = text.charAt(i);
            if (cur == pattern.charAt(j)) {
                if (j == 0) {
                    list.add(i);
                    i += m;
                } else {
                    i--;
                    j--;
                }
            } else {
                i += m - Math.min(j, 1 + last[cur]);
                j = m - 1;
            }
        }

        return list;
    }

    @Override
    public int[] buildLastTable(CharSequence pattern) {
        if (pattern == null) {
            throw new IllegalArgumentException(
                    "The parameter is invalid."
            );
        }

        int[] map = new int[Character.MAX_VALUE + 1];
        for (int i = 0; i < map.length; i++) {
            map[i] = -1;
        }
        for (int i = 0; i < pattern.length(); i++) {
            map[pattern.charAt(i)] =  i;
        }
        return map;
    }

    @Override
    public int generateHash(CharSequence current, int length) {
        if (current == null || length < 0 || length > current.length()) {
            throw new IllegalArgumentException(
                    "One or more of the parameters is invalid."
            );
        }

        int hash = 0;
        for (int i = 0; i < length; i++) {
            hash += current.charAt(i) * power(BASE, length - 1 - i);
        }

        return hash;
    }

    /**
     * A private power method
     * @param base The base
     * @param exp The exponent
     * @return The answer
     */
    private static int power(int base, int exp) {
        int result = base;

        for (int i = 1; i < exp; i++) {
            result = (base * result);
        }

        return (exp != 0) ? result : 1;
    }

    @Override
    public int updateHash(int oldHash, int length, char oldChar, char newChar) {
        if (length < 0) {
            throw new IllegalArgumentException(
                    "The parameter is invalid."
            );
        }

        return (int) (BASE * (oldHash - oldChar * power(BASE, length - 1))
                + newChar);
    }

    @Override
    public List<Integer> rabinKarp(CharSequence pattern, CharSequence text) {
        if (pattern == null || pattern.length() == 0 || text == null) {
            throw new IllegalArgumentException(
                    "One or more of the parameters is invalid."
            );
        }

        ArrayList<Integer> list = new ArrayList<>();

        if (pattern.length() <= text.length()) {
            int patternHash = generateHash(pattern, pattern.length());
            int textHash = generateHash(text, pattern.length());
            for (int i = 0; i <= text.length() - pattern.length(); i++) {
                if (patternHash == textHash) {
                    boolean match = true;
                    for (int j = 0; j < pattern.length() && match; j++) {
                        if (pattern.charAt(j) != text.charAt(i + j)) {
                            match = false;
                        }
                    }
                    if (match) {
                        list.add(i);
                    }
                }
                if (i < (text.length() - pattern.length())) {
                    textHash = updateHash(textHash, pattern.length(),
                            text.charAt(i), text.charAt(i + pattern.length()));
                }
            }
        }

        return list;
    }
}