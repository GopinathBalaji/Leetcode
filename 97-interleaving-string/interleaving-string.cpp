// Method 1: 2D DP
/*
# LeetCode 97. Interleaving String — Hints

This is a Dynamic Programming + 2D Grid DP (String Matching) problem.

The key idea is that instead of trying to construct `s3` greedily from `s1` and `s2`, you track whether different prefixes of the two strings can successfully form a prefix of `s3`.

This is related to other string DP problems:

- 1143\. Longest Common Subsequence: Find the longest subsequence shared by two strings.
- 72\. Edit Distance: Find the minimum operations needed to transform one string into another.
- 115\. Distinct Subsequences: Count the ways one string can form another as a subsequence.

The major difference is that you must use every character of both `s1` and `s2`, while preserving the relative order of characters within each string.

### Hint 1: Understand what an interleaving string means

Suppose:

```
s1 = "abc";
s2 = "def";
s3 = "adbecf";
```

Can you form `s3` by interleaving `s1` and `s2`?

Yes.

```
s1: a   b   c
s2:   d   e   f
    -----------
s3: a d b e c f
```

Notice:

- The characters from `s1` appear in the order `a → b → c`.
- The characters from `s2` appear in the order `d → e → f`.
- You can switch between the strings as many times as necessary.

Therefore:

```
return true;
```

But consider:

```
s1 = "abc";
s2 = "def";
s3 = "adcebf";
```

The characters originating from `s1` would appear in the order:

```
a → c → b
```

This violates the original order of `s1`.

So the important rule is:

You can interleave characters from both strings, but you cannot rearrange characters within either string.

### Hint 2: Check the lengths first

Suppose:

```
s1 = "abc";
s2 = "def";
s3 = "abcd";
```

Can `s3` be an interleaving of `s1` and `s2`?

No.

You must use every character from both strings exactly once.

Therefore, a necessary condition is:

\\[ |s1|+|s2|=|s3| \\]

In C++:

```
int m = s1.size();
int n = s2.size();

if (m + n != s3.size()) {
    return false;
}
```

If the lengths don't match, you can immediately return `false`.

However, matching lengths does not guarantee that the interleaving is valid.

You still need to verify the character ordering.

### Hint 3: Why might a greedy approach fail?

You might try using two pointers:

```
int i = 0;  // Pointer into s1
int j = 0;  // Pointer into s2
```

For each character in `s3`:

1. If it matches the next character in `s1`, take it from `s1`.
2. Otherwise, if it matches the next character in `s2`, take it from `s2`.
3. Otherwise, return `false`.

But consider:

```
s1 = "aa";
s2 = "ab";
s3 = "aaba";
```

The first character of `s3` is:

```
'a'
```

Both strings start with `'a'`.

Which string should you choose?

If you always choose `s1` when both match, you could consume its two `'a'` characters first.

Then you'd be unable to form the remaining `"ba"` using `s2`, whose next characters are `"ab"`.

However, there is a valid interleaving:

```
s1: a     a
s2:   a b
    -------
s3: a a b a
```

So the answer is:

```
true
```

Important observation: When both strings have a matching character, you may need to explore both choices.

This overlapping-decision structure makes dynamic programming a natural approach.

### Hint 4: Define your DP state

Instead of tracking only one pointer, track how many characters you've consumed from both strings.

Define:

```
dp[i][j]
```

to represent:

```
Whether it is possible to form
the first i + j characters of s3

using:
- The first i characters of s1
- The first j characters of s2
```

For example:

```
dp[2][3]
```

means:

```
Can we form the first 5 characters of s3

using:
- First 2 characters of s1
- First 3 characters of s2?
```

Since you're checking whether an interleaving is possible, the DP values are boolean:

```
true
false
```

You can initialize:

```
vector<vector<bool>> dp(
    m + 1,
    vector<bool>(n + 1, false)
);
```

Why `m + 1` and `n + 1`?

Because you also need to represent using zero characters from either string.

### Hint 5: Understand which character of `s3` you're matching

Suppose you're calculating:

```
dp[i][j]
```

You have consumed:

```
i characters from s1
j characters from s2
```

So you have consumed:

```
i + j
```

characters from `s3`.

The last character of this prefix has index:

```
i + j - 1
```

because C++ uses zero-based indexing.

Therefore, the character of `s3` you need to match is:

```
s3[i + j - 1]
```

This is one of the most important indexing details in this problem.

Remember:

```
dp[i][j]:
First i + j characters of s3

Last character index:
i + j - 1
```

### Hint 6: Derive the first transition — Take a character from `s1`

Suppose you want to calculate:

```
dp[i][j]
```

Imagine the last character you used came from `s1`.

What must be true?

First, you must have already formed the previous `i + j - 1` characters using:

```
dp[i - 1][j]
```

Second, the next character from `s1` must match the corresponding character in `s3`.

The character from `s1` is:

```
s1[i - 1]
```

And the character in `s3` is:

```
s3[i + j - 1]
```

Therefore, this choice is valid when:

```
dp[i - 1][j] &&
s1[i - 1] == s3[i + j - 1]
```

This transition only applies when:

```
i > 0
```

because you need at least one character from `s1`.

Mental model:

```
dp[i - 1][j]
      ↓
Previous prefix is valid
      ↓
Take one character from s1
      ↓
Does s1[i - 1] match s3[i + j - 1]?
      ↓
If yes:
dp[i][j] can be true
```

### Hint 7: Derive the second transition — Take a character from `s2`

Now imagine the last character came from `s2` instead.

You must have already formed the previous prefix using:

```
dp[i][j - 1]
```

And the next character from `s2` must match the corresponding character in `s3`.

Compare:

```
s2[j - 1]
```

with:

```
s3[i + j - 1]
```

Therefore, this choice is valid when:

```
dp[i][j - 1] &&
s2[j - 1] == s3[i + j - 1]
```

This transition only applies when:

```
j > 0
```

The important observation is that you only need one of the two choices to succeed.

So you combine both transitions using logical OR.

### Hint 8: Combine the two transitions

You now have two possibilities for reaching:

```
dp[i][j]
```

Option 1: Take the next character from `s1`.

```
dp[i - 1][j] &&
s1[i - 1] == s3[i + j - 1]
```

Option 2: Take the next character from `s2`.

```
dp[i][j - 1] &&
s2[j - 1] == s3[i + j - 1]
```

Since either choice can produce a valid interleaving:

```
dp[i][j] =
    (dp[i - 1][j] &&
     s1[i - 1] == s3[i + j - 1])
    ||
    (dp[i][j - 1] &&
     s2[j - 1] == s3[i + j - 1]);
```

Remember that the first branch requires `i > 0`, and the second requires `j > 0`.

Visualize the DP transitions

Each state can be reached from above (take from `s1`) or from the left (take from `s2`), provided the next character matches.

This is the core recurrence of the problem.

### Hint 9: Initialize the base cases

Consider:

```
dp[0][0]
```

This means:

```
Can we form an empty string
using zero characters from s1
and zero characters from s2?
```

Yes.

So:

```
dp[0][0] = true;
```

But what about the first column?

```
dp[i][0]
```

Here, you're not using any characters from `s2`.

Therefore, the first `i` characters of `s1` must exactly match the first `i` characters of `s3`.

You can write:

```
dp[i][0] =
    dp[i - 1][0] &&
    s1[i - 1] == s3[i - 1];
```

Similarly, for the first row:

```
dp[0][j]
```

You're not using any characters from `s1`.

So:

```
dp[0][j] =
    dp[0][j - 1] &&
    s2[j - 1] == s3[j - 1];
```

You can initialize the first row and column separately or handle them within your main nested loops using boundary checks.

### Hint 10: Walk through an example

Consider:

```
s1 = "ab";
s2 = "cd";
s3 = "acbd";
```

We know that:

```
s1: a   b
s2:   c   d
    -------
s3: a c b d
```

So the expected answer is:

```
true
```

Your DP table will look like this:

| `dp[i][j]`   | j=0 (`""`) | j=1 (`"c"`) | j=2 (`"cd"`) |
| ------------ | ---------- | ----------- | ------------ |
| i=0 (`""`)   | T          | F           | F            |
| i=1 (`"a"`)  | T          | T           | F            |
| i=2 (`"ab"`) | F          | T           | T            |

Notice that:

```
dp[2][2] = true;
```

That means you can form all four characters of `s3` using all characters from `s1` and `s2`.

Explore the DP calculation step by step:

Computing `dp[{cur97.i}][{cur97.j}]`

Step 1 of 9



j=0

j=1

j=2

i=0

T

F

F

i=1

F

F

F

i=2

F

F

F

Base case: two empty prefixes form an empty prefix of s3.

&#x20;PreviousNext state&#x20;

The important pattern is that you're gradually determining whether prefixes of `s1` and `s2` can form the corresponding prefix of `s3`.

### Hint 11: Where is the final answer?

After filling the DP table, you want to know:

```
Can we form the entirety of s3

using all characters of s1
and all characters of s2?
```

Since:

```
m = s1.size();
n = s2.size();
```

The answer is:

```
dp[m][n]
```

If this is `true`, there is at least one valid interleaving.

Otherwise, no valid interleaving exists.

### Hint 12: Can you optimize the space?

Look at the recurrence:

```
dp[i][j] =
    (dp[i - 1][j] &&
     s1[i - 1] == s3[i + j - 1])
    ||
    (dp[i][j - 1] &&
     s2[j - 1] == s3[i + j - 1]);
```

Notice that you only need:

```
dp[i - 1][j] → Previous row

dp[i][j - 1] → Current row, previous column
```

You don't need the entire 2D DP table once you've finished processing earlier rows.

So you can optimize to:

```
vector<bool> dp(n + 1, false);
```

Where:

```
dp[j]
```

represents whether a particular prefix of `s3` can be formed using the current prefix of `s1` and the first `j` characters of `s2`.

When processing each row, be careful:

- Before updating `dp[j]`, it represents the previous row.
- After updating `dp[j]`, it represents the current row.
- `dp[j - 1]` must already represent the current row.

Question: Should you iterate `j` forward or backward?

Think about which direction ensures that `dp[j - 1]` has already been updated for the current row.

Hint: It's forward.

This is a different reason for forward iteration than the one in Unbounded Knapsack.

### Hint 13: Analyze the complexity

Let:

```
m = s1.size();
n = s2.size();
```

Your DP table contains:

```
(m + 1) * (n + 1)
```

states.

Each state takes constant time to calculate because you're only checking up to two previous states.

Therefore:

```
Time Complexity: O(m × n)
```

For the 2D DP:

```
Space Complexity: O(m × n)
```

For the optimized 1D DP:

```
Space Complexity: O(n)
```

You can even reduce space to `O(min(m, n))` by choosing the shorter string as the dimension of the DP array.

### Skeleton — 2D DP (C++)

Try filling in the missing transitions yourself.

```
class Solution {
public:
    bool isInterleave(string s1, string s2, string s3) {

        int m = s1.size();
        int n = s2.size();

        // Check whether the lengths match
        if ( ? ) {
            return false;
        }

        // dp[i][j]:
        // Can we form the first i+j characters of s3
        // using first i characters of s1
        // and first j characters of s2?

        vector<vector<bool>> dp(
            m + 1,
            vector<bool>(n + 1, false)
        );

        // Base case:
        // Empty strings form an empty string
        dp[0][0] =  ? ;

        for (int i = 0; i <= m; i++) {
            for (int j = 0; j <= n; j++) {

                // Already initialized
                if (i == 0 && j == 0) {
                    continue;
                }

                // Option 1:
                // Take a character from s1
                if (i > 0) {

                    // Check the previous state
                    // and whether characters match

                    // dp[i][j] = ?
                }

                // Option 2:
                // Take a character from s2
                if (j > 0) {

                    // Check the previous state
                    // and whether characters match

                    // dp[i][j] = ?
                }
            }
        }

        // Did we successfully use
        // all characters from both strings?

        return  ? ;
    }
};
```

One detail to remember when filling in the skeleton: since either path can succeed, don't overwrite a `true` state with `false` when evaluating the second option.

Use logical OR to combine the possibilities.

### Core recurrence

Define:

```
dp[i][j] = true
```

if the first `i` characters of `s1` and the first `j` characters of `s2` can form the first `i + j` characters of `s3`.

Base case:

```
dp[0][0] = true;
```

Transition from `s1`:

```
if (i > 0) {
    dp[i][j] =
        dp[i][j] ||
        (dp[i - 1][j] &&
         s1[i - 1] == s3[i + j - 1]);
}
```

Transition from `s2`:

```
if (j > 0) {
    dp[i][j] =
        dp[i][j] ||
        (dp[i][j - 1] &&
         s2[j - 1] == s3[i + j - 1]);
}
```

Final answer:

```
return dp[m][n];
```

Or mentally:

```
Given s1, s2, s3
         ↓
Check length:
|s1| + |s2| == |s3|?
         ↓
Define DP state
         ↓
dp[i][j] = Can the first i characters of s1
           and first j characters of s2
           form the first i+j characters of s3?
         ↓
Initialize dp[0][0] = true
         ↓
For each (i, j):
         ↓
Option 1: Take from s1
         ↓
Check dp[i-1][j]
and matching character
         ↓
Option 2: Take from s2
         ↓
Check dp[i][j-1]
and matching character
         ↓
Combine using OR
         ↓
Return dp[m][n]
```

### Connection to previous DP problems

| Problem                          | DP meaning                                              | Goal                        |
| -------------------------------- | ------------------------------------------------------- | --------------------------- |
| 1143. Longest Common Subsequence | Longest common subsequence of two prefixes              | Maximize subsequence length |
| 72. Edit Distance                | Minimum edits between two prefixes                      | Minimize operations         |
| 115. Distinct Subsequences       | Ways a source prefix can form a target prefix           | Count subsequences          |
| 97. Interleaving String          | Whether two prefixes can form a target prefix           | Determine validity          |
| 139. Word Break                  | Whether a prefix can be segmented into dictionary words | Determine validity          |

The trickiest conceptual part of Interleaving String is recognizing that you need to track how many characters you've consumed from each input string, rather than tracking only your position in `s3`.

Once you make that connection, the problem becomes a standard 2D DP with two possible transitions.

The three most important things to remember are:

```
Rule 1:
Use all characters while preserving order
→ Track prefixes of both strings.

Rule 2:
The next character can come from either string
→ Combine two transitions using OR.

Rule 3:
If you've consumed i characters from s1
and j characters from s2
→ You've consumed i + j characters from s3.
```

The biggest takeaway: LeetCode 97 is a classic example of prefix-based string DP. Instead of making one greedy choice between `s1` and `s2`, you keep track of all reachable prefix combinations and determine whether the final state `dp[m][n]` is reachable.
*/
class Solution {
public:
    bool isInterleave(string s1, string s2, string s3) {
        int m = s1.size();
        int n = s2.size();

        if(m + n != s3.size()){
            return false;
        }

        vector<vector<bool>> dp(m+1, vector<bool>(n+1, false));
        dp[0][0] = true;

        for(int i=1; i<m; i++){
            dp[i][0] = dp[i-1][0] && s1[i-1] == s3[i-1];
        }

        for(int j=1; j<n; j++){
            dp[0][j] = dp[0][j-1] && s2[j-1] == s3[j-1];
        }

        for(int i=0; i<=m; i++){
            for(int j=0; j<=n; j++){
                if(i == 0 && j == 0){
                    continue;
                }

                if(i > 0){
                    dp[i][j] = dp[i][j] || (dp[i-1][j] && s1[i-1] == s3[i + j - 1]);
                }

                if(j > 0){
                    dp[i][j] = dp[i][j] || (dp[i][j - 1] && s2[j - 1] == s3[i + j - 1]);
                }
            }
        }

        return dp[m][n];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna