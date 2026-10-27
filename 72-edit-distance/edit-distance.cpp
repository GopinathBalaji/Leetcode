// Method 1: 2D DP
/*
# LeetCode 72. Edit Distance — Hints

This is a **Dynamic Programming + 2D Grid DP + String Matching (Minimum Operations)** problem.

The key idea is that instead of trying to transform one string into another greedily, you can calculate the minimum number of operations needed to transform different prefixes of the first string into prefixes of the second string.

This is closely related to:

- **1143. Longest Common Subsequence:** Find the longest subsequence shared by two strings.
- **97. Interleaving String:** Determine whether prefixes of two strings can form a third string.
- **115. Distinct Subsequences:** Count how many ways a source string can form a target string.
- **583. Delete Operation for Two Strings:** Find the minimum number of deletions needed to make two strings equal.

The major difference is that **you can perform three types of operations, and your goal is to minimize the total number of operations**.

We'll derive the 2D DP solution first, then explore how to optimize its space complexity.

---

## Hint 1: Understand the three allowed operations

Suppose:

```cpp
word1 = "horse";
word2 = "ros";
```

You want to transform `word1` into `word2` using the minimum number of operations.

You can perform three operations:

**Operation 1: Insert a character**

For example:

```text
"abc" → "abxc"
```

You inserted `'x'`.

Cost:

```cpp
1
```

**Operation 2: Delete a character**

For example:

```text
"abc" → "ac"
```

You deleted `'b'`.

Cost:

```cpp
1
```

**Operation 3: Replace a character**

For example:

```text
"abc" → "axc"
```

You replaced `'b'` with `'x'`.

Cost:

```cpp
1
```

Every operation costs exactly `1`.

For our example:

```cpp
word1 = "horse";
word2 = "ros";
```

One optimal transformation is:

```text
horse
  ↓ Replace 'h' with 'r'
rorse
  ↓ Delete the second 'r'
rose
  ↓ Delete 'e'
ros
```

Total operations:

```cpp
3
```

Therefore:

```cpp
answer = 3;
```

**Important observation:** You're minimizing the number of operations, not counting how many different transformations are possible.

---

## Hint 2: Why might a greedy approach fail?

You might try comparing characters from left to right.

Whenever two characters don't match, replace one with the other.

But consider:

```cpp
word1 = "abc";
word2 = "yabc";
```

If you greedily replace mismatching characters:

```text
abc
 ↓ Replace 'a' with 'y'
ybc
```

Now the remaining characters don't align correctly.

However, there's a much better solution:

```text
abc
 ↓ Insert 'y' at the beginning
yabc
```

Only one operation is needed.

```cpp
answer = 1;
```

This illustrates why you cannot always determine the best operation by looking at the current characters alone.

Sometimes:

- Inserting is better than replacing.
- Deleting is better than replacing.
- Replacing is better than inserting and deleting.

You need to consider the consequences of each possible operation.

This suggests **Dynamic Programming**.

---

## Hint 3: Think in terms of string prefixes

Recall LeetCode 115. Distinct Subsequences.

You defined:

```cpp
dp[i][j]
```

using prefixes of two strings.

You can use a similar approach here.

Suppose:

```cpp
word1 = "horse";
word2 = "ros";
```

Instead of immediately trying to transform the entire string, consider smaller questions.

```text
How many operations are needed to transform:

""      → ""
"h"     → "r"
"ho"    → "r"
"hor"   → "ro"
"hors"  → "ros"
"horse" → "ros"
```

Each smaller transformation can help solve a larger transformation.

For example, if you already know the minimum operations required to transform:

```text
"hor" → "ro"
```

you may be able to reuse that result when calculating:

```text
"hors" → "ros"
```

The important observation is:

**The optimal transformation of two prefixes can be built using optimal transformations of smaller prefixes.**

---

## Hint 4: Define your DP state

Let:

```cpp
dp[i][j]
```

represent:

```text
The MINIMUM number of operations
required to transform

the first i characters of word1

into

the first j characters of word2.
```

For example:

```cpp
word1 = "horse";
word2 = "ros";
```

Consider:

```cpp
dp[2][1]
```

This asks:

```text
Minimum operations needed to transform:

"ho" → "r"
```

One possible transformation:

```text
ho
 ↓ Replace 'h' with 'r'
ro
 ↓ Delete 'o'
r
```

Total:

```cpp
dp[2][1] = 2;
```

Another example:

```cpp
dp[3][2]
```

This asks:

```text
Minimum operations needed to transform:

"hor" → "ro"
```

One possible transformation:

```text
hor
 ↓ Replace 'h' with 'r'
ror
 ↓ Delete the last 'r'
ro
```

Therefore:

```cpp
dp[3][2] = 2;
```

Unlike LeetCode 115, where `dp[i][j]` represented a number of ways, here it represents a minimum cost.

Compare:

| Problem | Meaning of `dp[i][j]` |
|---|---|
| 1143. Longest Common Subsequence | Maximum length of a common subsequence |
| 97. Interleaving String | Whether two prefixes can form another prefix |
| 115. Distinct Subsequences | Number of ways to form a target prefix |
| **72. Edit Distance** | Minimum operations to transform one prefix into another |

Initialize:

```cpp
int m = word1.size();
int n = word2.size();

vector<vector<int>> dp(
    m + 1,
    vector<int>(n + 1, 0)
);
```

Why `m + 1` and `n + 1`?

Because you also need to represent empty prefixes.

---

## Hint 5: Understand the base cases

This is one of the most important parts of the problem.

Consider:

```cpp
dp[i][0]
```

What does this mean?

```text
Transform the first i characters
of word1 into an empty string.
```

For example:

```text
"abc" → ""
```

How many operations are needed?

You must delete every character.

```text
abc
 ↓ Delete 'c'
ab
 ↓ Delete 'b'
a
 ↓ Delete 'a'
""
```

Total:

```cpp
3
```

Therefore:

```cpp
dp[i][0] = i;
```

Now consider:

```cpp
dp[0][j]
```

This means:

```text
Transform an empty string
into the first j characters of word2.
```

For example:

```text
"" → "abc"
```

You must insert three characters.

Therefore:

```cpp
dp[0][j] = j;
```

Finally:

```cpp
dp[0][0] = 0;
```

because transforming an empty string into another empty string requires zero operations.

### Base case initialization

```cpp
for (int i = 0; i <= m; i++) {
    dp[i][0] = i;
}

for (int j = 0; j <= n; j++) {
    dp[0][j] = j;
}
```

For our example:

```cpp
word1 = "horse";
word2 = "ros";
```

The initialized DP table looks like this:

| `dp[i][j]` | `""` | `"r"` | `"ro"` | `"ros"` |
|---|---:|---:|---:|---:|
| `""` | 0 | 1 | 2 | 3 |
| `"h"` | 1 | ? | ? | ? |
| `"ho"` | 2 | ? | ? | ? |
| `"hor"` | 3 | ? | ? | ? |
| `"hors"` | 4 | ? | ? | ? |
| `"horse"` | 5 | ? | ? | ? |

Notice:

```text
First column:
Deleting characters from word1.

First row:
Inserting characters into word1.
```

These base cases allow you to calculate the remaining cells.

---

## Hint 6: Compare the last characters of the prefixes

Suppose you're calculating:

```cpp
dp[i][j]
```

The last character of the current prefix of `word1` is:

```cpp
word1[i - 1]
```

The last character of the current prefix of `word2` is:

```cpp
word2[j - 1]
```

There are two cases.

**Case 1: The characters match**

```cpp
word1[i - 1] == word2[j - 1]
```

**Case 2: The characters don't match**

```cpp
word1[i - 1] != word2[j - 1]
```

The DP transition depends on which case you're in.

Let's derive each one.

---

## Hint 7: Case 1 — The characters match

Suppose:

```cpp
word1 = "cat";
word2 = "bat";
```

Consider the last characters:

```text
word1: c a t
             ↑

word2: b a t
             ↑
```

Both are `'t'`.

Do you need to perform an operation on these characters?

No.

You can keep the matching `'t'` and focus on transforming:

```text
"ca" → "ba"
```

Therefore, the minimum operations needed for:

```text
"cat" → "bat"
```

is the same as:

```text
"ca" → "ba"
```

In DP terms:

```cpp
dp[i][j] = dp[i - 1][j - 1];
```

This is the first transition.

### Why move diagonally?

Because you've successfully matched one character from each string.

You can remove both matching characters from the subproblem.

```text
dp[i][j]
    ↑
    |
dp[i-1][j-1]
```

No additional operation is required.

So:

```cpp
if (word1[i - 1] == word2[j - 1]) {
    dp[i][j] = dp[i - 1][j - 1];
}
```

**Important observation:** When the last characters match, you don't need to add `1`.

---

## Hint 8: Case 2 — The characters don't match

Now suppose:

```cpp
word1[i - 1] != word2[j - 1]
```

For example:

```cpp
word1 = "cat";
word2 = "car";
```

The last characters are:

```text
't' != 'r'
```

You need to perform an operation.

There are three possibilities:

```text
1. Insert

2. Delete

3. Replace
```

Your task is to determine which operation results in the minimum total cost.

Let's derive each transition separately.

---

## Hint 9: First operation — Delete

Suppose you decide to delete the last character of the current prefix of `word1`.

For example:

```text
word1 = "cats"
word2 = "cat"
```

You can delete `'s'`:

```text
cats
 ↓ Delete 's'
cat
```

This costs one operation.

Now think about the remaining subproblem.

After deleting the last character of the source prefix:

```text
First i-1 characters of word1
```

must be transformed into:

```text
First j characters of word2
```

That subproblem is:

```cpp
dp[i - 1][j]
```

Since deletion costs one operation:

```cpp
deleteCost = 1 + dp[i - 1][j];
```

### Why `i - 1` but not `j - 1`?

Because you're deleting a character from `word1`.

You consume one character from the source without matching or consuming a character from the target.

Mental model:

```text
Delete from word1
        ↓
Remove word1[i-1]
        ↓
Solve smaller source prefix
        ↓
dp[i-1][j]
        ↓
Add deletion cost
        ↓
1 + dp[i-1][j]
```

Remember:

```cpp
Delete → dp[i - 1][j] + 1
```

---

## Hint 10: Second operation — Insert

Suppose:

```cpp
word1 = "cat";
word2 = "cats";
```

You need to insert `'s'`.

```text
cat
 ↓ Insert 's'
cats
```

This costs one operation.

But how does this translate into a DP recurrence?

Think about the final character of `word2`:

```cpp
word2[j - 1]
```

If you insert this character, you can treat it as already matched.

Now the remaining task is to transform:

```text
First i characters of word1
```

into:

```text
First j-1 characters of word2
```

This corresponds to:

```cpp
dp[i][j - 1]
```

Therefore:

```cpp
insertCost = 1 + dp[i][j - 1];
```

### Why `j - 1` but not `i - 1`?

Because inserting a character satisfies one additional character in the target.

You haven't consumed another character from the original `word1`.

Mental model:

```text
Insert word2[j-1]
        ↓
One additional target character
is now satisfied
        ↓
Solve the shorter target prefix
        ↓
dp[i][j-1]
        ↓
Add insertion cost
        ↓
1 + dp[i][j-1]
```

Remember:

```cpp
Insert → dp[i][j - 1] + 1
```

---

## Hint 11: Third operation — Replace

Suppose:

```cpp
word1 = "cat";
word2 = "car";
```

You can replace `'t'` with `'r'`.

```text
cat
 ↓ Replace 't' with 'r'
car
```

This costs one operation.

After replacing the last character, those characters are considered matched.

So you only need to solve:

```text
"ca" → "ca"
```

In general, you need to transform:

```text
First i-1 characters of word1
```

into:

```text
First j-1 characters of word2
```

That corresponds to:

```cpp
dp[i - 1][j - 1]
```

Therefore:

```cpp
replaceCost = 1 + dp[i - 1][j - 1];
```

### Why decrease both indices?

Because replacing a character handles one character from each string.

Mental model:

```text
Replace word1[i-1]
with word2[j-1]
        ↓
Both characters are handled
        ↓
Solve shorter prefixes
        ↓
dp[i-1][j-1]
        ↓
Add replacement cost
        ↓
1 + dp[i-1][j-1]
```

Remember:

```cpp
Replace → dp[i - 1][j - 1] + 1
```

---

## Hint 12: Combine the three operations

Now you have all three possibilities.

**Option 1: Delete**

```cpp
dp[i - 1][j] + 1
```

**Option 2: Insert**

```cpp
dp[i][j - 1] + 1
```

**Option 3: Replace**

```cpp
dp[i - 1][j - 1] + 1
```

Which one should you choose?

Remember that the problem asks for the minimum number of operations.

Therefore, use:

```cpp
min()
```

The recurrence becomes:

```cpp
dp[i][j] = 1 + min({
    dp[i - 1][j],      // Delete
    dp[i][j - 1],      // Insert
    dp[i - 1][j - 1]   // Replace
});
```

This is the most important recurrence in the problem.

### Visualizing the transitions

```text
                    dp[i][j]
                       |
             Characters match?
                /           \
              YES            NO
               |              |
        No operation      Try 3 operations
               |              |
        dp[i-1][j-1]      /     |     \
                         /      |      \
                     Delete   Insert   Replace
                       |        |         |
                  dp[i-1][j] dp[i][j-1] dp[i-1][j-1]
                       \        |         /
                        \       |        /
                         Take MIN + 1
```

The complete logic is:

```cpp
if (word1[i - 1] == word2[j - 1]) {

    // Characters match:
    // No operation needed

    dp[i][j] = dp[i - 1][j - 1];

} else {

    // Characters don't match:
    // Choose the cheapest operation

    dp[i][j] = 1 + min({
        dp[i - 1][j],
        dp[i][j - 1],
        dp[i - 1][j - 1]
    });
}
```

---

## Hint 13: Walk through an example

Consider:

```cpp
word1 = "horse";
word2 = "ros";
```

We know the expected answer is:

```cpp
3
```

Initialize the DP table:

```text
        ""   r   o   s

""       0   1   2   3

h        1   ?   ?   ?

o        2   ?   ?   ?

r        3   ?   ?   ?

s        4   ?   ?   ?

e        5   ?   ?   ?
```

Let's calculate a few cells.

### Step 1: Calculate `dp[1][1]`

You're transforming:

```text
"h" → "r"
```

The last characters don't match:

```cpp
'h' != 'r'
```

Consider the three operations.

Delete:

```cpp
dp[0][1] + 1 = 1 + 1 = 2;
```

Insert:

```cpp
dp[1][0] + 1 = 1 + 1 = 2;
```

Replace:

```cpp
dp[0][0] + 1 = 0 + 1 = 1;
```

Choose the minimum:

```cpp
dp[1][1] = min({2, 2, 1});
         = 1;
```

Replacing `'h'` with `'r'` is optimal.

### Step 2: Calculate `dp[2][2]`

You're transforming:

```text
"ho" → "ro"
```

The last characters match:

```cpp
'o' == 'o'
```

Therefore:

```cpp
dp[2][2] = dp[1][1];
         = 1;
```

No new operation is required for the matching `'o'`.

### Step 3: Calculate `dp[3][3]`

You're transforming:

```text
"hor" → "ros"
```

The last characters don't match:

```cpp
'r' != 's'
```

Calculate:

```cpp
dp[3][3] = 1 + min({
    dp[2][3],
    dp[3][2],
    dp[2][2]
});
```

The relevant previous values are:

```text
dp[2][3] = 2

dp[3][2] = 2

dp[2][2] = 1
```

Therefore:

```cpp
dp[3][3] = 1 + min({2, 2, 1});
         = 2;
```

Continue filling the table.

### Final DP table

| Source prefix | `""` | `"r"` | `"ro"` | `"ros"` |
|---|---:|---:|---:|---:|
| `""` | 0 | 1 | 2 | 3 |
| `"h"` | 1 | 1 | 2 | 3 |
| `"ho"` | 2 | 2 | 1 | 2 |
| `"hor"` | 3 | 2 | 2 | 2 |
| `"hors"` | 4 | 3 | 3 | 2 |
| `"horse"` | 5 | 4 | 4 | **3** |

Notice:

```cpp
dp[5][3] = 3;
```

That means the minimum number of operations to transform:

```text
"horse" → "ros"
```

is:

```cpp
3
```

---

## Hint 14: Why can you fill the DP table row by row?

Look carefully at the recurrence:

```cpp
dp[i][j] = 1 + min({
    dp[i - 1][j],
    dp[i][j - 1],
    dp[i - 1][j - 1]
});
```

Each state depends on three neighboring cells:

```text
       dp[i-1][j-1]      dp[i-1][j]
                    \    /
                     \  /
                    dp[i][j-1] → dp[i][j]
```

More simply:

```text
      DIAGONAL     TOP
          ↘         ↓
           CURRENT ← LEFT
```

So before calculating:

```cpp
dp[i][j]
```

you need:

```cpp
dp[i - 1][j]
```

from the previous row,

```cpp
dp[i][j - 1]
```

from the current row,

and:

```cpp
dp[i - 1][j - 1]
```

from the previous row.

If you process rows from top to bottom and columns from left to right, these dependencies have already been calculated.

Therefore, your loops can look like:

```cpp
for (int i = 1; i <= m; i++) {
    for (int j = 1; j <= n; j++) {

        // Calculate dp[i][j]
    }
}
```

This is a standard **2D Grid DP traversal pattern**.

---

## Hint 15: What is the final answer?

Recall:

```cpp
dp[i][j]
```

represents:

```text
Minimum operations required to transform
the first i characters of word1
into the first j characters of word2.
```

Let:

```cpp
int m = word1.size();
int n = word2.size();
```

You want to transform the entire `word1` into the entire `word2`.

Therefore, the answer is:

```cpp
dp[m][n]
```

You don't need to search through the entire DP table for a minimum.

The final cell already represents the complete transformation.

---

## Hint 16: Can you optimize the space?

Look at the recurrence again:

```cpp
dp[i][j] = 1 + min({
    dp[i - 1][j],
    dp[i][j - 1],
    dp[i - 1][j - 1]
});
```

Notice that you only need:

```text
Previous row

Current row
```

You don't need to retain all the earlier rows once they've been used.

So you can optimize from:

```cpp
vector<vector<int>> dp(
    m + 1,
    vector<int>(n + 1, 0)
);
```

to:

```cpp
vector<int> dp(n + 1);
```

However, there's an important challenge.

In 1D DP, you need to preserve three values:

```text
TOP:
dp[i-1][j]

LEFT:
dp[i][j-1]

DIAGONAL:
dp[i-1][j-1]
```

When processing row `i`:

- `dp[j]` initially represents the **TOP** value.
- `dp[j-1]` has already been updated and represents the **LEFT** value.
- The **DIAGONAL** value must be saved before it gets overwritten.

So you'll need a temporary variable:

```cpp
int prevDiagonal;
```

Before updating `dp[j]`, save its old value so it can become the diagonal value for the next column.

**Important:** Unlike LeetCode 115, where backward iteration allows 1D optimization, Edit Distance typically uses forward iteration with a saved diagonal value.

---

## Hint 17: Analyze the complexity

Let:

```cpp
m = word1.size();
n = word2.size();
```

Your DP table contains:

```cpp
(m + 1) * (n + 1)
```

states.

For each state, you perform a constant amount of work:

```text
Compare two characters.

If they match:
    Copy diagonal value.

Otherwise:
    Find minimum of three values.
```

Therefore:

\[
\boxed{\text{Time: }O(m\times n)}
\]

For the 2D DP:

\[
\boxed{\text{Space: }O(m\times n)}
\]

For the optimized 1D DP:

\[
\boxed{\text{Space: }O(n)}
\]

You can reduce the space complexity further to:

\[
O(\min(m,n))
\]

by choosing the shorter string as the DP array dimension.

---

# Skeleton — 2D DP (C++)

Try filling in the missing expressions yourself.

```cpp
class Solution {
public:
    int minDistance(string word1, string word2) {

        int m = word1.size();
        int n = word2.size();

        // dp[i][j] = Minimum operations
        // required to transform
        // first i characters of word1
        // into first j characters of word2

        vector<vector<int>> dp(
            m + 1,
            vector<int>(n + 1, 0)
        );

        // Base case 1:
        // Transform word1 prefix
        // into an empty string
        for (int i = 0; i <= m; i++) {
            dp[i][0] =  ? ;
        }

        // Base case 2:
        // Transform an empty string
        // into word2 prefix
        for (int j = 0; j <= n; j++) {
            dp[0][j] =  ? ;
        }

        // Fill DP table
        for (int i = 1; i <= m; i++) {
            for (int j = 1; j <= n; j++) {

                // Case 1:
                // Characters match
                if (word1[i - 1] == word2[j - 1]) {

                    // No additional operation needed

                    dp[i][j] =  ? ;

                } else {

                    // Case 2:
                    // Characters don't match

                    // Option 1: Delete
                    int deleteCost =  ? ;

                    // Option 2: Insert
                    int insertCost =  ? ;

                    // Option 3: Replace
                    int replaceCost =  ? ;

                    // Choose minimum number
                    // of operations

                    dp[i][j] =  ? ;
                }
            }
        }

        // Return minimum edit distance
        return  ? ;
    }
};
```

---

# Skeleton — 1D Space-Optimized DP (C++)

Once you're comfortable with the 2D solution, try implementing this version.

```cpp
class Solution {
public:
    int minDistance(string word1, string word2) {

        int m = word1.size();
        int n = word2.size();

        // dp[j] stores the edit distance
        // for the current row as it is updated

        vector<int> dp(n + 1, 0);

        // Initialize first row
        for (int j = 0; j <= n; j++) {
            dp[j] =  ? ;
        }

        for (int i = 1; i <= m; i++) {

            // Save the previous row's
            // diagonal value
            int prevDiagonal = dp[0];

            // First column:
            // Delete all i source characters
            dp[0] =  ? ;

            for (int j = 1; j <= n; j++) {

                // Save previous row's dp[j]
                int oldTop = dp[j];

                if (word1[i - 1] == word2[j - 1]) {

                    // Characters match:
                    // Use previous diagonal

                    dp[j] =  ? ;

                } else {

                    // Delete: oldTop
                    // Insert: dp[j-1]
                    // Replace: prevDiagonal

                    dp[j] =  ? ;
                }

                // Update diagonal for next column
                prevDiagonal =  ? ;
            }
        }

        return  ? ;
    }
};
```

The trickiest part is preserving the diagonal value before overwriting `dp[j]`.

---

# Core recurrence

Define:

```cpp
dp[i][j] = Minimum operations needed
           to transform the first i
           characters of word1
           into the first j
           characters of word2.
```

### Base cases

```cpp
dp[i][0] = i;   // Delete i characters

dp[0][j] = j;   // Insert j characters
```

### Case 1: Characters match

```cpp
if (word1[i - 1] == word2[j - 1]) {
    dp[i][j] = dp[i - 1][j - 1];
}
```

### Case 2: Characters don't match

```cpp
else {
    dp[i][j] = 1 + min({
        dp[i - 1][j],      // Delete
        dp[i][j - 1],      // Insert
        dp[i - 1][j - 1]   // Replace
    });
}
```

### Final answer

```cpp
return dp[m][n];
```

Or mentally:

```text
Given word1 and word2
          ↓
Define dp[i][j]
          ↓
Minimum operations required
to transform first i characters
of word1 into first j characters
of word2
          ↓
Initialize base cases
          ↓
dp[i][0] = i
dp[0][j] = j
          ↓
Process every pair (i, j)
          ↓
Do the last characters match?
          |
     ┌────┴────┐
     │         │
    YES        NO
     │         │
No operation   Try:
needed         Delete
     │         Insert
     │         Replace
     │         │
     │      Take MIN + 1
     │         │
     └────┬────┘
          │
     Store dp[i][j]
          ↓
Return dp[m][n]
```

---

# Connection to Previous DP Problems

| Problem | DP Meaning | Goal |
|---|---|---|
| 1143. Longest Common Subsequence | Maximum common subsequence length between prefixes | Maximize length |
| 97. Interleaving String | Whether two prefixes can form a target prefix | Determine validity |
| 115. Distinct Subsequences | Number of ways a source prefix forms a target prefix | Count subsequences |
| **72. Edit Distance** | Minimum operations to transform one prefix into another | Minimize operations |
| 583. Delete Operation for Two Strings | Minimum deletions required to make two strings equal | Minimize deletions |

The most useful connection is with **LeetCode 115. Distinct Subsequences**.

Both problems use:

```cpp
dp[i][j]
```

to represent a relationship between prefixes of two strings.

But the state transitions are different.

### LeetCode 115 — Distinct Subsequences

When characters match:

```cpp
dp[i][j] =
    dp[i - 1][j] +
    dp[i - 1][j - 1];
```

Why addition?

Because you're counting all possible ways to form the target.

### LeetCode 72 — Edit Distance

When characters don't match:

```cpp
dp[i][j] = 1 + min({
    dp[i - 1][j],
    dp[i][j - 1],
    dp[i - 1][j - 1]
});
```

Why minimum?

Because you're finding the cheapest sequence of operations.

The difference is:

```text
115. Distinct Subsequences

COUNTING DP
→ Add the number of valid choices.
```

```text
72. Edit Distance

OPTIMIZATION DP
→ Minimize the cost of valid choices.
```

---

## Three Important Rules to Remember

```text
Rule 1:

Transform one string into another.
→ Use PREFIX-BASED 2D DP.
```

```text
Rule 2:

Characters match.
→ No additional operation.

dp[i][j] = dp[i-1][j-1]
```

```text
Rule 3:

Characters don't match.
→ Try INSERT, DELETE, REPLACE.
→ Choose the MINIMUM cost.

dp[i][j] = 1 + min(
    dp[i-1][j],
    dp[i][j-1],
    dp[i-1][j-1]
)
```

### One final mental trick

When you see the three neighboring cells:

```text
        Diagonal    Top
             ↘      ↓
              Current
                 ↑
                Left
```

Remember:

| Direction | Operation | Cost |
|---|---|---|
| From top `dp[i-1][j]` | Delete | +1 |
| From left `dp[i][j-1]` | Insert | +1 |
| From diagonal `dp[i-1][j-1]` | Replace | +1 |
| From diagonal when characters match | Keep character | +0 |

**The biggest takeaway:** LeetCode 72 is a classic example of *minimum-cost string transformation DP*. Once you understand why deletion comes from the top, insertion comes from the left, and replacement comes from the diagonal, the recurrence becomes intuitive.

The most important conceptual skill is recognizing that **each DP cell represents the minimum cost of transforming two prefixes, and every operation reduces the problem to an already-solved smaller prefix pair**.
*/
class Solution {
public:
    int minDistance(string word1, string word2) {
        int m = word1.size();
        int n = word2.size();

        vector<vector<int>> dp(m + 1, vector<int>(n + 1, 0));

        for(int i=0; i <= m; i++){
            dp[i][0] = i;
        }

        for(int j = 0; j <= n; j++){
            dp[0][j] = j;
        }

        for(int i=1; i<=m; i++){
            for(int j=1; j<=n; j++){
                if(word1[i - 1] == word2[j - 1]){
                    dp[i][j] = dp[i-1][j-1];
                }else{
                    int deleteCost = dp[i-1][j];

                    int insertCost = dp[i][j-1];

                    int replaceCost = dp[i-1][j-1];

                    dp[i][j] = 1 + std::min({deleteCost, insertCost, replaceCost});
                }
            }
        }

        return dp[m][n];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna