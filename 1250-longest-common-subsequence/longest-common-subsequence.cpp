// Method 1:2D DP
/*
This is a Dynamic Programming / 2D String DP problem.

The key idea is to find the longest sequence of characters that appears in both strings in the same relative order, but not necessarily consecutively.

For each pair of positions, you have two possibilities:

```
characters match
→ extend a common subsequence
```

or:

```
characters don't match
→ skip a character from one of the strings
```

This leads to a classic 2D DP recurrence.

### Hint 1: Understand what a subsequence means

Suppose:

```
text1 = "abcde";
text2 = "ace";
```

One common subsequence is:

```
a → c → e
```

Its length is:

```
3
```

Notice that in `text1`, the characters are not consecutive.

That is allowed because this is a subsequence, not a substring.

Another example:

```
text1 = "abc";
text2 = "def";
```

There are no matching characters, so the answer is:

```
0
```

### Hint 2: Define your DP state

Let:

```
dp[i][j]
```

mean:

```
the length of the longest common subsequence
between:

the first i characters of text1

and

the first j characters of text2
```

Your goal is:

```
dp[m][n]
```

where:

```
int m = text1.size();
int n = text2.size();
```

The important detail is that `i` and `j` represent prefix lengths, not direct string indices.

So the characters you're currently considering are:

```
text1[i - 1]
text2[j - 1]
```

### Hint 3: What is the base case?

Suppose one of the strings is empty:

```
text1 = ""
text2 = "abc"
```

There cannot be any common subsequence with a positive length.

Therefore:

```
dp[0][j] = 0;
dp[i][0] = 0;
```

You can initialize the entire DP table with zeros:

```
vector<vector<int>> dp(
    m + 1,
    vector<int>(n + 1, 0)
);
```

The extra row and column represent empty prefixes.

### Hint 4: Case 1 — the characters match

Suppose you're calculating:

```
dp[i][j]
```

and:

```
text1[i - 1] == text2[j - 1]
```

For example:

```
text1 = "abc"
text2 = "ac"

Current characters:

c == c
```

Since both characters match, you can extend the longest common subsequence of the previous prefixes.

The previous prefixes are represented by:

```
dp[i - 1][j - 1]
```

So:

```
dp[i][j] = 1 + dp[i - 1][j - 1];
```

Why `+1`?

Because you've found one more matching character.

Mental model:

```
characters match
        ↓
take the best common subsequence
before both characters
        ↓
add 1
```

### Hint 5: Case 2 — the characters don't match

Now suppose:

```
text1[i - 1] != text2[j - 1]
```

For example:

```
text1 = "abc"
text2 = "abd"

Current characters:

c != d
```

You cannot use both of these final characters together as a new matching pair.

So you have two choices.

Option 1: Skip the current character from `text1`

Use:

```
dp[i - 1][j]
```

Option 2: Skip the current character from `text2`

Use:

```
dp[i][j - 1]
```

Since you want the longest possible subsequence, take the maximum:

```
dp[i][j] = max(
    dp[i - 1][j],
    dp[i][j - 1]
);
```

Mental model:

```
characters don't match
        ↓
skip character from text1
OR
skip character from text2
        ↓
take the better result
```

### Hint 6: Put the two cases together

Your recurrence becomes:

```
if (text1[i - 1] == text2[j - 1]) {

    dp[i][j] = 1 + dp[i - 1][j - 1];

} else {

    dp[i][j] = max(
        dp[i - 1][j],
        dp[i][j - 1]
    );
}
```

This is the central idea of the entire problem.

A useful visual:

Dependencies for dp[i][j]

Match: diagonal + 1. Mismatch: maximum of above and left.

### Hint 7: Walk through an example

Suppose:

```
text1 = "abcde";
text2 = "ace";
```

Your DP table will have:

```
6 rows
4 columns
```

because you include an extra row and column for empty prefixes.

LCS DP walkthrough

Step 1 of 6

∅

a

c

e

∅

0

0

0

0

a

0

–

–

–

b

0

–

–

–

c

0

–

–

–

d

0

–

–

–

e

0

–

–

–

Initialize empty prefixes

An empty prefix has LCS length 0.

&#x20;PreviousNext&#x20;

The completed DP table is:

```
       ""  a  c  e
   ""   0  0  0  0
    a   0  1  1  1
    b   0  1  1  1
    c   0  1  2  2
    d   0  1  2  2
    e   0  1  2  3
```

So:

```
dp[5][3] = 3;
```

The longest common subsequence is:

```
"ace"
```

with length `3`.

### Hint 8: Build the DP table from top-left to bottom-right

Your recurrence uses three previously computed states:

```
dp[i - 1][j - 1]  // diagonal
dp[i - 1][j]      // above
dp[i][j - 1]      // left
```

So process the table row by row:

```
for (int i = 1; i <= m; i++) {
    for (int j = 1; j <= n; j++) {
        // Calculate dp[i][j]
    }
}
```

Why start at `1`?

Because row `0` and column `0` already represent empty prefixes and are initialized to zero.

### Hint 9: Watch out for off-by-one errors

This is probably the most common implementation mistake.

Your DP table has dimensions:

```
(m + 1) x (n + 1)
```

But your strings use zero-based indices.

Therefore, when computing:

```
dp[i][j]
```

you compare:

```
text1[i - 1]
text2[j - 1]
```

Not:

```
text1[i]
text2[j]
```

For example:

```
i = 1;
j = 1;
```

corresponds to comparing:

```
text1[0]
text2[0]
```

These are the first characters of the two strings.

### Hint 10: Why doesn't greedy work?

Suppose:

```
text1 = "abc";
text2 = "bac";
```

You might greedily match the first character of `text1`:

```
a
```

But there are multiple possibilities:

```
"ac"
"bc"
```

Both have length `2`.

You need to consider whether skipping a character in either string leads to a better future subsequence.

That is why the mismatch recurrence uses:

```
max(
    dp[i - 1][j],
    dp[i][j - 1]
)
```

rather than making one fixed greedy choice.

### Hint 11: Complexity

Let:

```
m = text1.size();
n = text2.size();
```

You calculate every entry in a DP table with:

```
(m + 1) × (n + 1)
```

cells.

Each entry takes constant time.

Therefore:

```
Time:  O(m × n)
Space: O(m × n)
```

You can optimize space to:

```
O(min(m, n))
```

by storing only one row and carefully preserving the previous diagonal value.

However, I recommend implementing the 2D version first because it's easier to understand.

### Skeleton — 2D DP

```
class Solution {
public:
    int longestCommonSubsequence(
        string text1,
        string text2
    ) {
        int m = text1.size();
        int n = text2.size();

        vector<vector<int>> dp(
            m + 1,
            vector<int>(n + 1, 0)
        );

        // dp[i][j] represents the LCS length
        // between the first i characters of text1
        // and the first j characters of text2

        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {

                if (text1[i - 1] == text2[j - 1]) {

                    // Characters match
                    // Extend the LCS from diagonal

                    dp[i][j] =  ? ;

                } else {

                    // Characters don't match
                    // Skip a character from
                    // either text1 or text2

                    dp[i][j] = max(
                         ? ,
                         ? 
                    );
                }
            }
        }

        return dp[m][n];
    }
};
```

### Core recurrence

\\[ dp[i][j] = \begin{cases} 1+dp[i-1][j-1] & \text{if characters match}\\\ \max(dp[i-1][j],dp[i][j-1]) & \text{otherwise} \end{cases} \\]

Or mentally:

```
two strings
      ↓
create 2D DP table
      ↓
compare characters at (i-1, j-1)
      ↓
do they match?
    /       \
   yes       no
    ↓         ↓
diagonal+1  max(above, left)
    \         /
     ↓       ↓
    store dp[i][j]
          ↓
      return dp[m][n]
```

### Connection to previous DP problems

| Problem                             | DP meaning                                   | Transition                                      |
| ----------------------------------- | -------------------------------------------- | ----------------------------------------------- |
| 300. Longest Increasing Subsequence | Longest increasing subsequence ending at `i` | Extend from smaller previous values             |
| 62. Unique Paths                    | Number of paths to a grid cell               | Above + Left                                    |
| 64. Minimum Path Sum                | Minimum cost to reach a grid cell            | Cost + Min(Above, Left)                         |
| 1143. Longest Common Subsequence    | Longest common subsequence of two prefixes   | Match: Diagonal + 1; Mismatch: Max(Above, Left) |

The trickiest conceptual part is understanding why matching characters use the diagonal cell, while mismatching characters use the maximum of the top and left cells.

When characters match, both can be included in the same subsequence. When they don't, at least one must be skipped.

Once you recognize that distinction, this becomes a straightforward 2D DP problem.
*/
class Solution {
public:
    int longestCommonSubsequence(string text1, string text2) {
        int m = text1.length();
        int n = text2.length();

        vector<vector<int>> dp(m+1, vector<int>(n+1, 0));

        for(int i=1; i<=m; i++){
            for(int j=1; j<=n; j++){
                if(text1[i-1] == text2[j-1]){
                    dp[i][j] = 1 + dp[i-1][j-1];
                }else{
                    dp[i][j] = std::max(dp[i-1][j], dp[i][j-1]);
                }
            }
        }

        return dp[m][n];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna