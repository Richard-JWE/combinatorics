import java.util.Arrays;
import java.util.Random;

public class combinatorics {
    public static void main(String[] args) {
        // n = 5, B = 2
        // String[] test = new String[5];
        // test[0]="00000";
        // test[1]="0XXX0";
        // test[2]="0XXX0";
        // test[3]="0XXX0";
        // test[4]="00000";

        // center = (n/2, n/2)
        // top right X = (n/2-B+1, n/2+B-1)
        // top left X = (n/2-B+1, n/2-B+1)
        // cell above top right X = (n/2-B, n/2+B-1)
        // cell above top left X = (n/2-B, n/2-B+1)
        // Need: xCy where x = (n-1)-2*B+1 ... n/2+n/2-1, y = n/2-B

        // I am assuming we are modding a number otherwise when
        // n = 99999, B = 0, we have about 6.3*1e30099 ways which does not fit
        // into a long or int at all ...
        Random random = new Random();
        for (int k = 0; k < 10; k++) {
            int n = random.nextInt(5000) * 2 + 1;
            int B = random.nextInt((n + 1) / 2);
            String[] test = new String[n];
            for (int i = 0; i < n; i++) {
                char[] cur = new char[n];
                for (int j = 0; j < n; j++) {
                    cur[j] = i >= n / 2 - B + 1 && i <= n / 2 + B - 1 && j >= n / 2 - B + 1 && j <= n / 2 + B - 1 ? 'X'
                            : '0';
                }
                test[i] = String.valueOf(cur);
            }
            // For n = 80651, B = 10614
            // O(n) : Took 10 ms
            // O(n^2): Took 58524 ms
            long before = System.currentTimeMillis();
            long got = solve(n, B, test);
            long mid = System.currentTimeMillis();
            long expected = naive(test);
            long done = System.currentTimeMillis();
            System.out.printf("For n = %d, B = %d\n", n, B);
            System.out.printf("O(n)  : Took %d ms\n", mid - before);
            System.out.printf("O(n^2): Took %d ms\n", done - mid);
            System.out.println();
            if (got != expected) {
                System.out.printf("For n = %d, B = %d. Expected %d, got %d\n", n, B, expected, got);
                throw new IllegalStateException("No Way!");
            }
        }
    }

    private static long solve(int n, int B, String[] grid) {
        if (n == 1) {
            return 1 - B;
        }
        --n;
        int M = (int) 1e9 + 7;
        long[] factInv = new long[2 * n + 1];
        long[] fact = new long[2 * n + 1];
        long[] inv = new long[2 * n + 1];
        fact[0] = fact[1] = inv[1] = factInv[0] = factInv[1] = 1;
        for (int i = 2; i <= 2 * n; i++) {
            inv[i] = M - M / i * inv[M % i] % M;
            fact[i] = fact[i - 1] * i % M;
            factInv[i] = factInv[i - 1] * inv[i] % M;
        }
        long del = 0;
        for (int i = 0; i < 2 * B - 1; i++) {
            int x = n / 2 - B + 1;
            int y = x + i;
            long first = fact[x + y - 1] * factInv[x - 1] % M * factInv[y] % M;
            long second = fact[n - x + n - y] * factInv[n - x] % M * factInv[n - y] % M;
            del = (del + first * second % M) % M;
        }
        long total = fact[2 * n] * factInv[n] % M * factInv[n] % M;
        return (total - 2 * del % M + M) % M;
    }

    private static long naive(String[] grid) {
        if (grid[0].charAt(0) == 'X') {
            return 0;
        }
        int M = (int) 1e9 + 7;
        int n = grid.length;
        long[] dp = new long[n];
        Arrays.fill(dp, 1);
        for (int i = 1; i < n; i++) {
            for (int j = 1; j < n; j++) {
                dp[j] = grid[i].charAt(j) == '0' ? (dp[j] + dp[j - 1]) % M : 0;
            }
        }
        return dp[n - 1];
    }
}
