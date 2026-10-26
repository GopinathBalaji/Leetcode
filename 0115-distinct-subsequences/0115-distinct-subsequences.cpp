// Method 1: 
/*
# LeetCode 115. Distinct Subsequences — Hints

This is a Dynamic Programming + String Matching + Counting DP problem.

The key idea is that instead of generating every possible subsequence of `s`, you can count how many ways different prefixes of `s` can form different prefixes of `t`.

This is closely related to:

- 97\. Interleaving String: Determine whether prefixes of two strings can form a third string.
- 1143\. Longest Common Subsequence: Find the longest common subsequence between two strings.
- 518\. Coin Change II: Count the number of ways to form a target.
- 494\. Target Sum: Count the number of valid choices using 0/1 Knapsack DP.

The major difference is that you must count every distinct choice of character positions in `s` that produces `t`, while preserving the original character order.

We'll first derive the 2D DP solution and then optimize it to 1D DP.

### Hint 1: Understand what you're counting

Suppose:

```
s = "rabbbit";
t = "rabbit";
```

You want to determine how many different subsequences of `s` equal `t`.

Remember that a subsequence is formed by deleting zero or more characters without changing the relative order of the remaining characters.

In this example, `s` contains three consecutive `b` characters, while `t` needs only two.

You can choose:

```
s = r a b b b i t
        ↑ ↑
        1 2

s = r a b b b i t
        ↑   ↑
        1   3

s = r a b b b i t
          ↑ ↑
          2 3
```

All three choices produce:

```
r a b b i t
```

Therefore:

```
answer = 3;
```

Important observation: Even if two subsequences produce the same string, they count as different ways when they use different character positions.

You're counting the number of ways to choose positions, not the number of different resulting strings.

### Hint 2: Recognize the include-or-skip pattern

Suppose:

```
s = "bab";
t = "ab";
```

You're processing characters of `s` one at a time.

For each character, you have two possible decisions:

1. Skip the current character.
2. Use the current character to match part of `t`.

For example:

```
s = b a b
t =   a b
```

You can skip the first `b` and use the remaining characters.

But you cannot reorder characters.

For instance:

```
s = b a
t = a b
```

is not a valid match because `a` appears after `b` in the source.

This suggests a DP approach where we consider how many characters from each string have already been processed.

Question: What information do you need to remember to determine how many ways the remaining characters can match?

Think about tracking two indices.

### Hint 3: Define your DP state

Let:

```
dp[i][j]
```

represent:

```
The number of distinct ways
to form the first j characters of t

using the first i characters of s.
```

For example:

```
s = "babgbag";
t = "bag";
```

Consider:

```
dp[3][2]
```

This asks:

```
How many ways can we form "ba"

using the first 3 characters of s?
```

The first three characters are:

```
"bab"
```

There's exactly one way:

```
b a b
↑ ↑
```

Therefore:

```
dp[3][2] = 1;
```

Another example:

```
dp[3][1]
```

This asks:

```
How many ways can we form "b"
using "bab"?
```

There are two choices:

```
b a b
↑

b a b
    ↑
```

Therefore:

```
dp[3][1] = 2;
```

Notice the difference from LeetCode 97.

In Interleaving String:

```
dp[i][j] = true or false;
```

But in Distinct Subsequences:

```
dp[i][j] = number of ways;
```

You're using counting DP, not boolean DP.

### Hint 4: Think about the last character

Suppose you're calculating:

```
dp[i][j]
```

You are considering:

```
First i characters of s

First j characters of t
```

The last character in the current prefix of `s` is:

```
s[i - 1]
```

The last character in the current prefix of `t` is:

```
t[j - 1]
```

There are two possible situations:

Case 1: The characters match.

```
s[i - 1] == t[j - 1]
```

You have two choices:

- Skip the current character from `s`.
- Use the current character to match the last character of the prefix of `t`.

Case 2: The characters don't match.

```
s[i - 1] != t[j - 1]
```

You cannot use the current character of `s` to complete the current prefix of `t`.

You must skip it.

This gives us the two cases needed to derive the recurrence.

### Hint 5: Derive the first transition — Characters match

Suppose:

```
s[i - 1] == t[j - 1]
```

How can you form the first `j` characters of `t` using the first `i` characters of `s`?

There are two possibilities.

Option 1: Skip the current character of `s`.

You ignore:

```
s[i - 1]
```

Then you must form the same prefix of `t` using only the previous `i - 1` characters of `s`.

The number of ways is:

```
dp[i - 1][j]
```

Option 2: Use the current character of `s`.

Since:

```
s[i - 1] == t[j - 1]
```

the current character can match the last character in the target prefix.

Now you only need to match:

```
First j - 1 characters of t
```

using:

```
First i - 1 characters of s
```

The number of ways is:

```
dp[i - 1][j - 1]
```

Because both choices represent different selections of character positions, you add their counts.

Therefore:

```
dp[i][j] =
    dp[i - 1][j] +
    dp[i - 1][j - 1];
```

This is the most important transition in the problem.

Mental model:

```
s[i-1] == t[j-1]
          |
     Two choices
      /       \
    Skip      Use
     |         |
dp[i-1][j]  dp[i-1][j-1]
      \       /
          ADD
           |
       dp[i][j]
```

Notice that you use addition rather than `max()`.

Why?

Because you're counting all possible subsequences, not finding the single best one.

### Hint 6: Derive the second transition — Characters don't match

Suppose:

```
s[i - 1] != t[j - 1]
```

For example:

```
s = "abc";
t = "ab";
```

Consider:

```
dp[3][2]
```

You're asking:

```
How many ways can we form "ab"
using "abc"?
```

The last characters are:

```
s[2] = 'c'
t[1] = 'b'
```

They don't match.

Can the character `'c'` help you finish `"ab"`?

No.

So the only valid choice is to skip `'c'`.

You must rely on:

```
dp[2][2]
```

which represents the number of ways to form `"ab"` from `"ab"`.

Therefore:

```
dp[i][j] = dp[i - 1][j];
```

Unlike the matching case, there's no second term to add.

### Hint 7: Combine the two transitions

You now have the complete recurrence.

Case 1: Characters match

```
if (s[i - 1] == t[j - 1]) {
    dp[i][j] =
        dp[i - 1][j] +
        dp[i - 1][j - 1];
}
```

Case 2: Characters don't match

```
else {
    dp[i][j] = dp[i - 1][j];
}
```

Or mathematically:

\\[ dp[i][j]= \begin{cases} dp[i-1][j]+dp[i-1][j-1] & \text{if }s[i-1]=t[j-1]\\\ dp[i-1][j] & \text{otherwise} \end{cases} \\]

The important thing to remember is:

```
Characters match:
    Skip OR Use
    → ADD both counts.

Characters don't match:
    Must skip
    → Keep previous count.
```

### Hint 8: Initialize the base cases

This is one of the trickiest parts of the problem.

Suppose:

```
s = "abc";
t = "ab";
```

Create a DP table:

```
int n = s.size();
int m = t.size();

vector<vector<long long>> dp(
    n + 1,
    vector<long long>(m + 1, 0)
);
```

Why `n + 1` and `m + 1`?

Because you also need to represent empty prefixes.

Base case 1: The target prefix is empty.

Consider:

```
dp[i][0]
```

This means:

```
How many ways can we form
an empty target string

using the first i characters of s?
```

There's exactly one way:

```
Delete every character.
```

Therefore:

```
dp[i][0] = 1;
```

For every:

```
0 <= i <= n
```

Base case 2: The source prefix is empty.

Consider:

```
dp[0][j]
```

where:

```
j > 0
```

This means:

```
How many ways can we form
a nonempty target

using an empty source?
```

There are zero ways.

Therefore:

```
dp[0][j] = 0;
```

For every `j > 0`.

Finally:

```
dp[0][0] = 1;
```

because there's exactly one way to form an empty target from an empty source: choose nothing.

Your initialized table looks like this:

|         | `""` | `"a"` | `"ab"` |
| ------- | ---- | ----- | ------ |
| `""`    | 1    | 0     | 0      |
| `"a"`   | 1    | 0     | 0      |
| `"ab"`  | 1    | 0     | 0      |
| `"abc"` | 1    | 0     | 0      |

The first column is initialized to `1`; the remaining values will be calculated by DP.

### Hint 9: Walk through an example

Consider:

```
s = "babgbag";
t = "bag";
```

The correct answer is:

```
5
```

Let's fill the DP table using the recurrence.

Number of ways to form each prefix of `t`

| Source prefix | `""` | `"b"` | `"ba"` | `"bag"` |
| ------------- | ---- | ----- | ------ | ------- |
| `""`          | 1    | 0     | 0      | 0       |
| `"b"`         | 1    | 1     | 0      | 0       |
| `"ba"`        | 1    | 1     | 1      | 0       |
| `"bab"`       | 1    | 2     | 1      | 0       |
| `"babg"`      | 1    | 2     | 1      | 1       |
| `"babgb"`     | 1    | 3     | 1      | 1       |
| `"babgba"`    | 1    | 3     | 4      | 1       |
| `"babgbag"`   | 1    | 3     | 4      | 5       |

Try tracing the table interactively.

Initialize base cases

Step 1 of 8

s / t

∅

b

ba

bag

∅

1

0

0

0

b

1

—

—

—

ba

1

—

—

—

bab

1

—

—

—

babg

1

—

—

—

babgb

1

—

—

—

babgba

1

—

—

—

babgbag

1

—

—

—

The empty target has exactly one matching subsequence for every source prefix.

&#x20;PreviousNext step&#x20;

Let's examine the final cell:

```
dp[7][3]
```

The last characters match:

```
s[6] == t[2]  // 'g' == 'g'
```

So:

```
dp[7][3] = dp[6][3] + dp[6][2];
```

From the table:

```
dp[6][3] = 1
dp[6][2] = 4
```

Therefore:

```
dp[7][3] = 1 + 4;
         = 5;
```

This shows exactly how the recurrence counts both choices:

- The one existing `"bag"` that skips the final `'g'`.
- Four additional subsequences that use the final `'g'`.

### Hint 10: Why can't you simply count matching characters?

You might consider counting how many times each character in `t` appears in `s`.

But that doesn't work because the relative order matters.

For example:

```
s = "bbaa";
t = "ab";
```

The source contains:

```
Two 'a' characters
Two 'b' characters
```

But can you form:

```
"ab"
```

as a subsequence?

No.

Every `'a'` occurs after every `'b'`.

Therefore:

```
answer = 0;
```

Even though both required characters are present.

This is why your DP must track prefixes, not just character frequencies.

### Hint 11: Determine the final answer

After filling the DP table, you want to know:

```
How many ways can we form
the ENTIRE target string t

using the ENTIRE source string s?
```

Let:

```
int n = s.size();
int m = t.size();
```

The answer is:

```
dp[n][m]
```

Notice that the DP already includes the possibilities where you skip some characters from `s`.

You don't need to sum over all possible ending positions manually.

The value of `dp[n][m]` already counts all valid subsequences using the entire available source.

### Hint 12: Can you optimize the space?

Look at the recurrence:

```
dp[i][j] =
    dp[i - 1][j] +
    dp[i - 1][j - 1];
```

Notice that each state only depends on values from the previous row.

You don't need to store all `n + 1` rows.

Instead, you can use:

```
vector<long long> dp(m + 1, 0);
```

Where:

```
dp[j]
```

represents:

```
The number of ways to form
the first j characters of t

using the source characters
processed so far.
```

Initialize:

```
dp[0] = 1;
```

Then process each character of `s`.

For each source character, try to match the prefixes of `t`.

But there's one important question:

Should you iterate through the target string forward or backward?

Think carefully before revealing the next hint.

### Hint 13: Why must the 1D DP iterate backward?

Suppose:

```
s = "a";
t = "aa";
```

There should be:

```
0
```

valid subsequences.

You only have one `'a'`, but the target needs two.

Now imagine iterating forward:

```
for (int j = 1; j <= m; j++) {
    if (s[i - 1] == t[j - 1]) {
        dp[j] += dp[j - 1];
    }
}
```

Initially:

```
dp = [1, 0, 0]
```

Process the only `'a'`.

First:

```
dp[1] += dp[0];
```

Now:

```
dp = [1, 1, 0]
```

Then:

```
dp[2] += dp[1];
```

Now:

```
dp = [1, 1, 1]
```

But this is wrong.

You just used the same source character `'a'` twice.

This is similar to the 0/1 Knapsack problem.

To ensure each source character is used at most once, iterate backward:

```
for (int j = m; j >= 1; j--) {
    if (s[i - 1] == t[j - 1]) {
        dp[j] += dp[j - 1];
    }
}
```

Why does this work?

Because when processing target position `j`, the value of:

```
dp[j - 1]
```

still represents the count from before the current source character was processed.

Therefore, the current source character cannot be reused within the same iteration.

Remember:

```
0/1 Knapsack:
Each element can be used once.
→ Iterate backward.

115. Distinct Subsequences:
Each source character can be used once.
→ Iterate backward.
```

This is an important connection to LeetCode 494. Target Sum.

### Hint 14: Consider an important edge case

Suppose:

```
s = "aaa";
t = "aa";
```

How many subsequences form `"aa"`?

You can choose positions:

```
(0, 1)

(0, 2)

(1, 2)
```

So:

```
answer = 3;
```

This is useful for verifying that your implementation counts combinations of positions correctly.

Another edge case:

```
s = "abc";
t = "abcd";
```

Can you form a target that's longer than the source?

No.

Therefore, you can immediately return:

```
if (s.size() < t.size()) {
    return 0;
}
```

This check isn't strictly necessary for correctness if your DP is implemented properly, but it avoids unnecessary work.

Also remember that the empty target can be formed in exactly one way, regardless of the source.

### Hint 15: Analyze the complexity

Let:

```
n = s.size();
m = t.size();
```

For every character in `s`, you potentially examine every character in `t`.

So:

```
Time Complexity: O(n × m)
```

For the 2D DP:

```
Space Complexity: O(n × m)
```

For the optimized 1D DP:

```
Space Complexity: O(m)
```

A note about numeric types: The problem guarantees that the final result fits in a signed 32-bit integer, but intermediate counts may be larger. Using `long long` gives extra headroom; for complete protection against intermediate overflow, you can cap nonnegative DP counts at `INT_MAX`, since the final answer is guaranteed not to exceed that value.

### Skeleton — 2D DP (C++)

Try filling in the missing expressions yourself.

```
class Solution {
public:
    int numDistinct(string s, string t) {

        int n = s.size();
        int m = t.size();

        // If the source is shorter than
        // the target, matching is impossible.
        if ( ? ) {
            return 0;
        }

        // dp[i][j] = number of ways
        // first i characters of s
        // can form first j characters of t

        vector<vector<long long>> dp(
            n + 1,
            vector<long long>(m + 1, 0)
        );

        // Base case:
        // Empty target can always be formed
        for (int i = 0; i <= n; i++) {
            dp[i][0] =  ? ;
        }

        for (int i = 1; i <= n; i++) {
            for (int j = 1; j <= m; j++) {

                // Case 1:
                // Current characters match
                if (s[i - 1] == t[j - 1]) {

                    // Option 1: Skip s[i - 1]
                    // Option 2: Use s[i - 1]

                    dp[i][j] =  ? ;

                } else {

                    // Case 2:
                    // Characters don't match
                    // Must skip s[i - 1]

                    dp[i][j] =  ? ;
                }
            }
        }

        // Return number of ways
        // to form all of t using all of s

        return  ? ;
    }
};
```

### Skeleton — 1D Space-Optimized DP (C++)

Once you're comfortable with 2D DP, try this version.

```
class Solution {
public:
    int numDistinct(string s, string t) {

        int n = s.size();
        int m = t.size();

        if (n < m) {
            return 0;
        }

        // dp[j] = number of ways
        // to form the first j characters of t
        vector<long long> dp(m + 1, 0);

        // Base case
        dp[0] =  ? ;

        // Process each source character
        for (int i = 1; i <= n; i++) {

            // Iterate BACKWARD to prevent
            // reusing the current source character
            for (int j = m; j >= 1; j--) {

                // If the characters match
                if ( ? ) {

                    // Skip OR use this character

                    dp[j] +=  ? ;
                }
            }
        }

        return  ? ;
    }
};
```

### Core recurrence

Define:

```
dp[i][j] = Number of distinct ways
           first i characters of s
           can form first j characters of t
```

Base cases:

```
dp[i][0] = 1;  // Empty target

dp[0][j] = 0;  // Nonempty target, j > 0
```

If characters match:

```
dp[i][j] =
    dp[i - 1][j] +
    dp[i - 1][j - 1];
```

Otherwise:

```
dp[i][j] = dp[i - 1][j];
```

Final answer:

```
dp[n][m]
```

Or mentally:

```
Given source s and target t
             ↓
Define dp[i][j]
             ↓
Number of ways to form
first j characters of t
from first i characters of s
             ↓
Initialize:
dp[i][0] = 1
             ↓
Process each character of s
             ↓
Does s[i-1] match t[j-1]?
          /       \
        YES        NO
         |          |
    Skip or Use   Must Skip
         |          |
    ADD counts   Keep count
          \        /
           dp[i][j]
              ↓
       Return dp[n][m]
```

### Connection to previous DP problems

| Problem                          | DP meaning                                           | Goal               |
| -------------------------------- | ---------------------------------------------------- | ------------------ |
| 1143. Longest Common Subsequence | Longest common subsequence between prefixes          | Maximize length    |
| 97. Interleaving String          | Whether two prefixes can form a third prefix         | Determine validity |
| 115. Distinct Subsequences       | Number of ways a source prefix forms a target prefix | Count subsequences |
| 518. Coin Change II              | Number of combinations forming an amount             | Count combinations |
| 494. Target Sum                  | Number of subsets forming a target sum               | Count assignments  |

The most useful connections are with LeetCode 97 and LeetCode 494.

In LeetCode 97, you used two string indices to track how prefixes could match. Here, you also track two prefixes, but instead of checking whether a match is possible, you count how many matches exist.

In LeetCode 494, you chose whether to include or exclude each number. Here, you choose whether to include or exclude each source character.

The three most important rules to remember are:

```
Rule 1:
Count subsequences, not just check existence.
→ Use ADDITION for matching choices.

Rule 2:
A source character can be skipped
or used if it matches.
→ dp[i-1][j] + dp[i-1][j-1].

Rule 3:
Each source character can be used once.
→ Iterate BACKWARD in 1D DP.
```

The biggest takeaway: LeetCode 115 combines prefix-based string DP with the include-or-skip counting pattern. Once you understand why matching characters create two possible choices and why those counts must be added, the recurrence becomes straightforward.
*/
class Solution {
public:
    int numDistinct(string s, string t) {

        int n = s.size();
        int m = t.size();

        if(n < m){
            return 0;
        }

        vector<vector<long long>> dp(n + 1, vector<long long>(m + 1, 0));

        for(int i=0; i<=n; i++){
            dp[i][0] = 1;
        }

        for(int i=1; i<=n; i++){
            for(int j=1; j<=m; j++){
                if(s[i-1] == t[j-1]){
                    long long ways = (long long) dp[i-1][j] + dp[i-1][j-1];

                    dp[i][j] = std::min(ways, (long long)INT_MAX);
                }else{
                    dp[i][j] = dp[i-1][j];
                }
            }
        }

        return dp[n][m];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna