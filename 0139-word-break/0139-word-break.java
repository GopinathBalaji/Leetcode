// Method 1: Using Top-Down DP
/*
# Logic
* **Base case** uses `i == n`.
* **Memo** uses `Boolean[]` to distinguish unknown vs false.
* **State** is only the start index `i`.
* We **only** set `memo[i] = true` when a prefix leads to a successful suffix (`dfs(end)` is true).
* No shared `StringBuilder` mutation; we use `substring` or you could track indices only.
* **Pruning** with `maxLen` avoids useless long prefixes.

---

## Mini walkthrough (`"leetcode"`, dict = {"leet","code"})

* `dfs(0)` tries `s[0:4]="leet"` → in dict → calls `dfs(4)`
* `dfs(4)` tries `s[4:8]="code"` → in dict → calls `dfs(8)`
* `dfs(8)` hits `i==n` → `true`
  Memo bubbles back: `memo[4]=true`, `memo[0]=true` → overall `true`.

If a prefix fails to complete the rest, we try the **next** `end`; only when all fail do we store `memo[i]=false`.

## What was going wrong with my attempt:

1. **Wrong base case.**
   You return `true` at `i == s.length() - 1`. The correct success is **when you’ve consumed all characters** → `i == s.length()`.

2. **Memo can’t represent “unknown.”**
   `boolean[] memo` defaults to `false`, so you can’t tell “not computed yet” from “computed false.” Use a tri-state (`Boolean[]` with `null`) or an `int[]` with `-1/0/1`.

3. **State should be by start index only.**
   Top-down DP state is `f(i)` = “can `s[i:]` be segmented?”. `j` is just a local splitter you loop over; it **shouldn’t** be part of the memo key. Also, you don’t need a `StringBuilder` in the state.

4. **You mark `memo[i] = true` just because a prefix exists.**
   It must be true **only if** that prefix leads to a successful segmentation of the **rest** (i.e., `f(j+1)` is true). Your code sets `memo[i]=true` as soon as `words.contains(subS)` without checking the suffix.

5. **`StringBuilder` mutation across branches.**
   You append to one `sb` while exploring different `j`s but never undo it when backtracking. That yields incorrect substrings. Prefer `s.substring(i, end)` (or manage `sb` carefully by popping).

6. **Loop/termination conditions.**
   `if (i >= n || j >= n) return false;` prevents exploring a valid cut at the **end**. You want to allow `end == n` and succeed if recursion from that cut reaches `i == n`.
*/
class Solution {
    public boolean wordBreak(String s, List<String> wordDict) {
        Set<String> dict = new HashSet<>(wordDict);

        // optional pruning: max word length
        int maxLen = 0;
        for (String w : dict) maxLen = Math.max(maxLen, w.length());

        Boolean[] memo = new Boolean[s.length() + 1]; // null = unknown
        return dfs(0, s, dict, memo, maxLen);
    }

    private boolean dfs(int i, String s, Set<String> dict, Boolean[] memo, int maxLen) {
        if (i == s.length()) return true;          // consumed all chars → success
        if (memo[i] != null) return memo[i];

        int n = s.length();
        int limit = Math.min(n, i + maxLen);       // prune by max word length

        // try all end positions for the next word
        for (int end = i + 1; end <= limit; end++) {
            // check prefix s[i:end]
            if (dict.contains(s.substring(i, end)) && dfs(end, s, dict, memo, maxLen)) {
                return memo[i] = true;
            }
        }
        return memo[i] = false;
    }
}





// Method 2: Bottom-Up Approach
/*
Here’s a clean **bottom-up DP** for **LeetCode 139 — Word Break** (Java), plus a quick why/how and a walkthrough.

## Idea (bottom-up)

Let `dp[i]` mean: **the prefix `s[0..i)` can be segmented into dictionary words**.

* `dp[0] = true` (empty string is segmentable).
* For each end index `i` (1..n), check a split point `j < i`.
  If `dp[j]` is true **and** `s[j..i)` is in the dictionary → `dp[i]=true`.

Use a set for O(1) lookups, and prune with `maxWordLen` to avoid useless checks.

## Why this forbids “gaps”

`dp[i]` can only become true if there exists a `j < i` where the **entire** prefix up to `j` is valid (`dp[j]`) **and** the next chunk `s[j..i)` is a dictionary word. Thus, every `true` at `i` is backed by a chain of valid words covering `s[0..i)` with no gaps/overlaps.

## Complexity

* Worst-case time: `O(n * L)` substring checks with pruning, where `L = maxWordLen`.
  (Without pruning it’s `O(n^2)`.)
* Space: `O(n)` for `dp`, `O(|dict|)` for the set.

## Walkthrough (“leetcode”, {“leet”, “code”})

* `dp[0]=true`
* `i=4`: check `j` in `[0..4)`; `s[0..4)="leet"` ∈ dict and `dp[0]=true` → `dp[4]=true`
* `i=8`: check `j` in `[4..8)` (others fail or `dp[j]=false`);
  `s[4..8)="code"` ∈ dict and `dp[4]=true` → `dp[8]=true`
* Return `dp[8]=true`.

Tip: If TLE on huge inputs, you can also pre-group dict by length or first character to cut checks further, but the `maxLen` prune usually suffices.
*/
// class Solution {
//     public boolean wordBreak(String s, List<String> wordDict) {
//         int n = s.length();
//         Set<String> dict = new HashSet<>(wordDict);

//         // Optional (but very helpful) pruning: limit substring checks
//         int maxLen = 0;
//         for (String w : dict) maxLen = Math.max(maxLen, w.length());

//         boolean[] dp = new boolean[n + 1];
//         dp[0] = true; // empty prefix

//         for (int i = 1; i <= n; i++) {
//             // only need to look back up to maxLen characters
//             int start = Math.max(0, i - maxLen);
//             for (int j = i - 1; j >= start; j--) {
//                 if (!dp[j]) continue; // prefix up to j isn't segmentable
//                 // candidate word = s[j..i)
//                 if (dict.contains(s.substring(j, i))) {
//                     dp[i] = true;
//                     break; // no need to search more splits for i
//                 }
//             }
//         }
//         return dp[n];
//     }
// }