// Method 1: 2D DP
/*
# LeetCode 10. Regular Expression Matching — Hints

This is a **Dynamic Programming + 2D Grid DP + String Matching (Pattern Matching)** problem.

The key idea is that instead of trying to match the entire string against the entire pattern at once, you can determine whether different prefixes of the string match different prefixes of the pattern.

This is closely related to:

- **44. Wildcard Matching:** Determine whether a string matches a pattern containing `?` and `*`.
- **72. Edit Distance:** Use 2D DP to compare prefixes of two strings.
- **97. Interleaving String:** Determine whether prefixes of two strings can form another string.
- **115. Distinct Subsequences:** Count how many ways a source string can form a target subsequence.
- **1143. Longest Common Subsequence:** Find the longest common subsequence of two strings.

The major difference is that **the pattern contains special characters (`.` and `*`) that change how matching works**.

In particular, `*` makes this problem tricky because it can match **zero or more occurrences of the preceding element**.

We'll derive the 2D DP solution first, then explore top-down memoization and space optimization.

---

## Hint 1: Understand the two special pattern characters

You're given two strings:

```cpp
string s;
string p;
```

Your goal is to determine whether the pattern `p` matches the **entire string `s`**.

The pattern supports two special characters:

```text
.
*
```

Let's understand them separately.

### Special character 1: `.`

A dot matches exactly **one arbitrary character**.

For example:

```cpp
s = "cat";
p = "c.t";
```

The pattern:

```text
c . t
```

can match:

```text
c a t
```

because:

```text
c matches c

. matches a

t matches t
```

Therefore:

```cpp
answer = true;
```

Another example:

```cpp
s = "dog";
p = "d.g";
```

The dot matches `'o'`.

```cpp
answer = true;
```

However, consider:

```cpp
s = "cat";
p = "c.";
```

The pattern can match:

```text
c a
```

but there is still a `'t'` remaining.

Therefore:

```cpp
answer = false;
```

**Important observation:** `.` matches exactly one character, not zero or multiple characters.

---

### Special character 2: `*`

An asterisk means:

> Match zero or more occurrences of the immediately preceding element.

For example:

```cpp
p = "a*";
```

This can match:

```text
""

"a"

"aa"

"aaa"

"aaaa"

...
```

Why?

Because `a*` means:

```text
Zero or more occurrences of 'a'.
```

Consider:

```cpp
s = "aaa";
p = "a*";
```

The pattern `a*` can consume all three `'a'` characters.

Therefore:

```cpp
answer = true;
```

Now consider:

```cpp
s = "";
p = "a*";
```

Since `a*` can represent zero occurrences of `'a'`:

```cpp
answer = true;
```

Another example:

```cpp
s = "b";
p = "a*b";
```

The pattern is:

```text
a* b
```

We can interpret `a*` as an empty string:

```text
a* → ""
```

Leaving:

```text
b
```

which matches the string.

Therefore:

```cpp
answer = true;
```

### Important distinction

```text
.   → Matches exactly ONE arbitrary character.

*   → Matches ZERO OR MORE occurrences
      of the PRECEDING element.
```

Notice that `*` does not independently match arbitrary characters.

For example:

```cpp
p = "a*";
```

cannot match:

```cpp
s = "bbb";
```

because `a*` can only match repetitions of `'a'`.

However:

```cpp
p = ".*";
```

can match:

```cpp
s = "bbb";
```

because:

```text
. → Any single character

.* → Zero or more arbitrary characters
```

This distinction is essential for deriving the DP transitions.

---

## Hint 2: Understand that the pattern must match the ENTIRE string

This is not substring matching.

Consider:

```cpp
s = "abc";
p = "a";
```

The first character matches:

```text
s: a b c
   ↑

p: a
   ↑
```

But the remaining characters:

```text
b c
```

have not been matched.

Therefore:

```cpp
answer = false;
```

Now consider:

```cpp
s = "abc";
p = "a.*";
```

The pattern can be interpreted as:

```text
a     → Matches 'a'

.*    → Matches 'bc'
```

Therefore:

```cpp
answer = true;
```

Another example:

```cpp
s = "ab";
p = ".*";
```

Since `.*` can match any sequence of characters:

```cpp
answer = true;
```

The important observation is:

**Both the entire string and the entire pattern must be consumed for a successful match.**

This will help us define the DP state.

---

## Hint 3: Why might a greedy approach fail?

Suppose:

```cpp
s = "aaa";
p = "a*a";
```

You might try to make `a*` match as many `'a'` characters as possible.

That would consume:

```text
aaa
```

But then the pattern still contains:

```text
a
```

and there are no characters remaining in the string.

The match would fail.

However, there is a valid matching strategy.

Let `a*` match only two characters:

```text
String:    a a a
           └─┘ │
            a*  a
```

The matching becomes:

```text
a* → "aa"

a  → "a"
```

Therefore:

```cpp
answer = true;
```

This demonstrates why blindly consuming as many characters as possible is insufficient.

Sometimes `*` should match:

- Zero occurrences.
- One occurrence.
- Multiple occurrences.

You need to consider different possibilities.

### Another example

```cpp
s = "aab";
p = "c*a*b";
```

At first glance, the pattern looks different from the string.

But consider:

```text
Pattern:  c*   a*   b

String:       aa   b
```

We can interpret:

```text
c* → ""

a* → "aa"

b  → "b"
```

The entire string is matched.

Therefore:

```cpp
answer = true;
```

**Key observation:** When you encounter `*`, you may need to explore multiple matching choices.

This suggests **Dynamic Programming**.

---

## Hint 4: Think in terms of string prefixes

Recall LeetCode 72. Edit Distance.

You defined:

```cpp
dp[i][j]
```

using prefixes of two strings.

You can use a similar approach here.

Suppose:

```cpp
s = "aab";
p = "c*a*b";
```

Instead of immediately asking whether:

```text
"aab" matches "c*a*b"
```

consider smaller questions:

```text
Does ""    match ""?

Does ""    match "c*"?

Does "a"   match "c*a"?

Does "a"   match "c*a*"?

Does "aa"  match "c*a*"?

Does "aab" match "c*a*b"?
```

Each smaller matching problem can help solve a larger matching problem.

For example:

If you already know whether:

```text
"aa" matches "c*a*"
```

you can reuse that result when determining whether:

```text
"aab" matches "c*a*b"
```

because the final characters are both `'b'`.

The important observation is:

**Whether two prefixes match can often be determined using the matching results of smaller prefixes.**

This is the foundation of our DP solution.

---

## Hint 5: Define your DP state

Let:

```cpp
dp[i][j]
```

represent:

```text
Whether the first i characters of s
can be COMPLETELY matched by
the first j characters of p.
```

The result is a boolean:

```cpp
true
```

or:

```cpp
false
```

For example:

```cpp
s = "aab";
p = "c*a*b";
```

Consider:

```cpp
dp[1][3]
```

The first character of `s` is:

```text
"a"
```

The first three characters of `p` are:

```text
"c*a"
```

This asks:

```text
Does "a" match "c*a"?
```

Yes.

We can interpret:

```text
c* → ""

a  → "a"
```

Therefore:

```cpp
dp[1][3] = true;
```

Another example:

```cpp
dp[2][4]
```

This asks:

```text
Does "aa" match "c*a*"?
```

We can interpret:

```text
c* → ""

a* → "aa"
```

Therefore:

```cpp
dp[2][4] = true;
```

One more example:

```cpp
dp[1][1]
```

This asks:

```text
Does "a" match "c"?
```

No.

Therefore:

```cpp
dp[1][1] = false;
```

### Compare with previous DP problems

| Problem | Meaning of `dp[i][j]` |
|---|---|
| 1143. Longest Common Subsequence | Length of the longest common subsequence between two prefixes |
| 72. Edit Distance | Minimum operations needed to transform one prefix into another |
| 97. Interleaving String | Whether two prefixes can form a target prefix |
| 115. Distinct Subsequences | Number of ways a source prefix forms a target prefix |
| **10. Regular Expression Matching** | Whether a string prefix completely matches a pattern prefix |

### DP table initialization

Let:

```cpp
int m = s.size();
int n = p.size();
```

We need a table of size:

```cpp
(m + 1) * (n + 1)
```

In C++:

```cpp
vector<vector<bool>> dp(
    m + 1,
    vector<bool>(n + 1, false)
);
```

Why `m + 1` and `n + 1`?

Because we also need to represent empty prefixes.

For example:

```cpp
dp[0][0]
```

represents:

```text
Does an empty string match an empty pattern?
```

And:

```cpp
dp[0][j]
```

represents:

```text
Does an empty string match
the first j characters of the pattern?
```

These empty-prefix cases are especially important because of `*`.

---

## Hint 6: Understand the base cases

This is one of the most important parts of the problem.

Let's consider the different possibilities.

### Base case 1: Both string and pattern are empty

Consider:

```cpp
s = "";
p = "";
```

An empty pattern matches an empty string.

Therefore:

```cpp
dp[0][0] = true;
```

This is our starting point.

### Base case 2: The pattern is empty, but the string is not

Consider:

```cpp
s = "abc";
p = "";
```

Can an empty pattern match a nonempty string?

No.

The pattern has no characters that can consume any part of the string.

Therefore:

```cpp
dp[i][0] = false;
```

for every:

```cpp
i > 0
```

For example:

```cpp
dp[1][0] = false;

dp[2][0] = false;

dp[3][0] = false;
```

### Base case 3: The string is empty, but the pattern is not

This case is more interesting.

Consider:

```cpp
s = "";
p = "a*";
```

Can `a*` match an empty string?

Yes.

Because `a*` can represent zero occurrences of `'a'`.

Therefore:

```cpp
dp[0][2] = true;
```

Now consider:

```cpp
s = "";
p = "a*b*";
```

We can interpret:

```text
a* → ""

b* → ""
```

Therefore:

```cpp
dp[0][4] = true;
```

Another example:

```cpp
s = "";
p = "a*b*c*";
```

Every `x*` group can match zero characters.

Therefore:

```cpp
dp[0][6] = true;
```

However, consider:

```cpp
s = "";
p = "a*b";
```

We can skip:

```text
a*
```

But we still have:

```text
b
```

which must match exactly one character.

Since the string is empty:

```cpp
answer = false;
```

### Important observation

An empty string can match a nonempty pattern **only when the entire pattern can represent an empty string**.

For valid patterns, this happens when the pattern consists entirely of zero-or-more groups such as:

```text
a*

a*b*

a*b*c*

.*a*
```

### How do we initialize the first row?

Suppose:

```cpp
p = "a*b*";
```

The pattern indices are:

```text
Index:     0   1   2   3

Pattern:   a   *   b   *
```

The empty-prefix matching states are:

| Pattern prefix | Matches `""`? |
|---|---|
| `""` | true |
| `"a"` | false |
| `"a*"` | true |
| `"a*b"` | false |
| `"a*b*"` | true |

Notice the pattern.

When the current pattern character is:

```cpp
'*'
```

we can ignore the entire preceding `x*` group.

That means removing two characters from the pattern prefix.

So:

```cpp
dp[0][j] = dp[0][j - 2];
```

when:

```cpp
p[j - 1] == '*'
```

and `j >= 2`.

### Base case initialization

```cpp
dp[0][0] = true;

for (int j = 2; j <= n; j++) {

    if (p[j - 1] == '*') {

        dp[0][j] = dp[0][j - 2];
    }
}
```

Why start at `j = 2`?

Because `*` always applies to the preceding element, and the problem guarantees that the pattern is valid.

Therefore, `*` cannot be the first character.

### Example

Suppose:

```cpp
s = "aab";
p = "c*a*b";
```

Initially, the first row looks like:

| Pattern prefix | `""` | `"c"` | `"c*"` | `"c*a"` | `"c*a*"` | `"c*a*b"` |
|---|---|---|---|---|---|---|
| `s = ""` | T | F | T | F | T | F |

Here:

```cpp
dp[0][0] = true;

dp[0][2] = true;

dp[0][4] = true;
```

because:

```text
c*   → ""

c*a* → ""
```

But:

```cpp
dp[0][5] = false;
```

because the final `'b'` cannot be ignored.

**Important:** Unlike Edit Distance, you cannot initialize the first row and column using simple character counts. The `*` operator changes which pattern prefixes can match an empty string.

---

## Hint 7: Compare the last characters of the prefixes

Suppose you're calculating:

```cpp
dp[i][j]
```

The last character of the current prefix of `s` is:

```cpp
s[i - 1]
```

The last character of the current prefix of `p` is:

```cpp
p[j - 1]
```

There are two major cases.

### Case 1: The pattern character is NOT `*`

```cpp
p[j - 1] != '*'
```

This includes:

- Ordinary letters such as `'a'`, `'b'`, and `'c'`.
- The wildcard character `'.'`.

### Case 2: The pattern character IS `*`

```cpp
p[j - 1] == '*'
```

This requires special handling because `*` operates on the preceding pattern character.

Let's derive the transitions for each case.

---

## Hint 8: Case 1 — The current pattern character is NOT `*`

Suppose:

```cpp
p[j - 1] != '*'
```

The pattern character must match exactly one character from the string.

There are two ways this can happen.

### Situation 1: The characters are equal

Suppose:

```cpp
s = "cat";
p = "cat";
```

Consider the last characters:

```text
String:   c a t
              ↑

Pattern:  c a t
              ↑
```

Both characters are:

```cpp
't'
```

Do we need to do anything else to match these two characters?

No.

They already match.

Therefore, the remaining question is:

```text
Does "ca" match "ca"?
```

In DP terms:

```cpp
dp[i - 1][j - 1]
```

So:

```cpp
dp[i][j] = dp[i - 1][j - 1];
```

provided:

```cpp
s[i - 1] == p[j - 1]
```

### Why move diagonally?

Because we've successfully matched one character from each string.

```text
String:    c a t
               ↑
             Matched

Pattern:   c a t
               ↑
             Matched
```

We can remove the matching characters from both prefixes.

Therefore:

```cpp
dp[i][j] = dp[i - 1][j - 1];
```

---

### Situation 2: The pattern character is `.`

Suppose:

```cpp
s = "cat";
p = "ca.";
```

Consider the last characters:

```text
String:    c a t
               ↑

Pattern:   c a .
               ↑
```

The characters are not equal:

```cpp
't' != '.'
```

But `.` matches any single character.

Therefore, the characters are considered compatible.

We can again reduce the problem to:

```text
Does "ca" match "ca"?
```

So:

```cpp
dp[i][j] = dp[i - 1][j - 1];
```

The same transition applies.

### Combine both situations

The current characters match if:

```cpp
s[i - 1] == p[j - 1]
```

OR:

```cpp
p[j - 1] == '.'
```

Therefore:

```cpp
bool match =
    (s[i - 1] == p[j - 1]) ||
    (p[j - 1] == '.');
```

Then:

```cpp
dp[i][j] = match && dp[i - 1][j - 1];
```

### Complete transition for non-`*` characters

```cpp
if (p[j - 1] != '*') {

    bool match =
        (s[i - 1] == p[j - 1]) ||
        (p[j - 1] == '.');

    dp[i][j] =
        match && dp[i - 1][j - 1];
}
```

**Important observation:** When the current pattern element matches exactly one string character, we consume one character from both the string and the pattern.

---

## Hint 9: Case 2 — The current pattern character IS `*`

Now we reach the most important part of the problem.

Suppose:

```cpp
p[j - 1] == '*'
```

For example:

```cpp
s = "aaa";
p = "a*";
```

Remember:

```text
a* → Zero or more occurrences of 'a'.
```

There are two main possibilities.

**Option 1: Match ZERO occurrences of the preceding element.**

**Option 2: Match ONE OR MORE occurrences of the preceding element.**

We must consider both possibilities.

Why?

Because either one might lead to a valid complete match.

Let's derive them separately.

---

## Hint 10: Option 1 — Let `*` match ZERO occurrences

Suppose:

```cpp
s = "b";
p = "a*b";
```

The pattern is:

```text
a* b
```

We can choose to let:

```text
a* → ""
```

Then the pattern becomes:

```text
b
```

which matches the string.

The important idea is:

**When `*` matches zero occurrences, we skip both the `*` and the preceding pattern element.**

For example:

```text
Pattern:   a * b
           └─┘
           Skip
```

In DP terms, suppose you're calculating:

```cpp
dp[i][j]
```

and:

```cpp
p[j - 1] == '*'
```

The preceding pattern character is:

```cpp
p[j - 2]
```

If we skip the entire `x*` group, the remaining pattern prefix has length:

```cpp
j - 2
```

The string prefix remains unchanged.

Therefore, the smaller subproblem is:

```cpp
dp[i][j - 2]
```

So the transition is:

```cpp
dp[i][j] = dp[i][j - 2];
```

### Why decrease `j` by 2?

Because we're removing:

```text
The preceding element

+

The '*' character
```

For example:

```text
Pattern:

c * a * b
    ↑
Suppose we are processing the first *

Remove:

c *

Remaining:

a * b
```

For the prefix ending at that `*`, we can ignore the `c*` group entirely.

### Mental model

```text
Current pattern ends with *
            ↓
Choose ZERO occurrences
            ↓
Ignore preceding element and *
            ↓
Pattern becomes shorter by 2
            ↓
String remains unchanged
            ↓
dp[i][j-2]
```

Remember:

```cpp
// '*' matches zero occurrences.

zeroOccurrences = dp[i][j - 2];
```

**Important:** This option is always available whenever the current pattern character is `*`.

Even if the preceding element matches the current string character, skipping the entire group may still be necessary.

---

## Hint 11: Option 2 — Let `*` match ONE OR MORE occurrences

Now suppose:

```cpp
s = "aaa";
p = "a*";
```

We want:

```text
a* → "aaa"
```

How can we express this using smaller DP states?

Remember that:

```cpp
p[j - 1] == '*'
```

and the preceding pattern element is:

```cpp
p[j - 2]
```

First, we need to check whether the preceding pattern element can match the current string character.

There are two possibilities.

### Situation 1: The preceding pattern character equals the string character

For example:

```cpp
s[i - 1] = 'a';
p[j - 2] = 'a';
```

They match.

Therefore, `a*` can consume the current `'a'`.

### Situation 2: The preceding pattern character is `.`

For example:

```cpp
s[i - 1] = 'b';
p[j - 2] = '.';
```

Since `.` matches any single character, `.*` can consume `'b'`.

So the matching condition is:

```cpp
bool match =
    (s[i - 1] == p[j - 2]) ||
    (p[j - 2] == '.');
```

If this condition is true, we can use `*` to consume the current string character.

But what happens to our indices?

This is the tricky part.

### Why do we decrease `i` but keep `j` unchanged?

Suppose:

```cpp
s = "aaa";
p = "a*";
```

We want `a*` to match three `'a'` characters.

Consider consuming the last `'a'` first while reasoning backward:

```text
String:   a a a
              ↑
           Consume

Pattern:  a *
           └─┘
         Still usable
```

After consuming one `'a'`, the remaining string prefix is:

```text
"aa"
```

But the pattern is still:

```text
"a*"
```

Why?

Because `a*` can match additional `'a'` characters.

We cannot discard the pattern group yet.

Therefore, the smaller subproblem is:

```cpp
dp[i - 1][j]
```

Notice:

```text
i decreases by 1.

j remains unchanged.
```

This gives:

```cpp
dp[i][j] = dp[i - 1][j];
```

provided the preceding pattern element matches the current string character.

### Step-by-step example

Consider:

```cpp
s = "aaa";
p = "a*";
```

We're calculating:

```cpp
dp[3][2]
```

The last character of the string is:

```cpp
'a'
```

The element preceding `*` is:

```cpp
'a'
```

They match.

So:

```cpp
dp[3][2] = dp[2][2];
```

Now:

```cpp
dp[2][2]
```

asks:

```text
Does "aa" match "a*"?
```

Again, the last string character matches the preceding pattern element.

Therefore:

```cpp
dp[2][2] = dp[1][2];
```

Similarly:

```cpp
dp[1][2] = dp[0][2];
```

We already know:

```cpp
dp[0][2] = true;
```

because `a*` can match an empty string.

Therefore:

```cpp
dp[1][2] = true;

dp[2][2] = true;

dp[3][2] = true;
```

This allows `a*` to match any number of `'a'` characters.

### Mental model

```text
Current pattern ends with *
            ↓
Can preceding element match
current string character?
            |
       ┌────┴────┐
       |         |
      YES        NO
       |         |
       v         v
Consume one    Cannot use
character      this option
       |
       v
Decrease i by 1
       |
       v
Keep j unchanged
       |
       v
The same x* group
can match more characters
       |
       v
dp[i-1][j]
```

Remember:

```cpp
// '*' matches one or more occurrences.

oneOrMore = dp[i - 1][j];
```

But only if:

```cpp
s[i - 1] == p[j - 2]
```

or:

```cpp
p[j - 2] == '.'
```

---

## Hint 12: Combine both `*` possibilities

Now we have both possible interpretations.

When:

```cpp
p[j - 1] == '*'
```

we can either:

**Option 1: Skip the entire `x*` group.**

```cpp
dp[i][j - 2]
```

**Option 2: Use `x*` to consume one more string character.**

```cpp
dp[i - 1][j]
```

The second option is available only when the preceding pattern element matches the current string character.

Since we're trying to determine whether **at least one valid matching strategy exists**, we combine the possibilities using logical OR.

### Step 1: Try zero occurrences

```cpp
dp[i][j] = dp[i][j - 2];
```

This skips the preceding element and `*`.

### Step 2: Check whether the preceding element matches

```cpp
bool match =
    (s[i - 1] == p[j - 2]) ||
    (p[j - 2] == '.');
```

### Step 3: Try consuming one more character

If the characters match:

```cpp
if (match) {

    dp[i][j] =
        dp[i][j] || dp[i - 1][j];
}
```

### Complete transition

```cpp
if (p[j - 1] == '*') {

    // Option 1:
    // Match zero occurrences of x.

    dp[i][j] = dp[i][j - 2];

    // Option 2:
    // Match one or more occurrences of x.

    bool match =
        (s[i - 1] == p[j - 2]) ||
        (p[j - 2] == '.');

    if (match) {

        dp[i][j] =
            dp[i][j] || dp[i - 1][j];
    }
}
```

This is the most important transition in the problem.

### Why OR instead of addition?

Because we're not counting the number of valid ways to match the pattern.

We're only determining whether a match is possible.

If either interpretation works:

```cpp
dp[i][j] = true;
```

Otherwise:

```cpp
dp[i][j] = false;
```

### Visualizing the transition

```text
                    dp[i][j]
                        |
              Does p[j-1] == '*'?
                        |
                       YES
                        |
            ┌───────────┴────────────┐
            |                        |
            v                        v
       ZERO occurrences        ONE OR MORE
            |                  occurrences
            |                        |
            v                        v
     Skip entire x*          Does x match
           group             s[i-1]?
            |                        |
            v                 ┌──────┴─────┐
      dp[i][j-2]              |            |
                            YES           NO
                             |            |
                             v            v
                       Consume one      Cannot
                       character        use this
                             |          option
                             v
                       dp[i-1][j]
                             |
            ┌────────────────┘
            |
            v
     Combine valid options
           using OR
            |
            v
       Store dp[i][j]
```

The complete logic for both cases is:

```cpp
if (p[j - 1] != '*') {

    // Ordinary character or '.'

    bool match =
        (s[i - 1] == p[j - 1]) ||
        (p[j - 1] == '.');

    dp[i][j] =
        match && dp[i - 1][j - 1];

} else {

    // '*' matches zero occurrences.

    dp[i][j] = dp[i][j - 2];

    // '*' matches one or more occurrences.

    bool match =
        (s[i - 1] == p[j - 2]) ||
        (p[j - 2] == '.');

    if (match) {

        dp[i][j] =
            dp[i][j] || dp[i - 1][j];
    }
}
```

**The biggest conceptual distinction:**

```text
No '*':
    Match ONE character.
    Move diagonally.

With '*':
    Either skip x*,
    or consume one character
    while retaining x*.
```

---

## Hint 13: Walk through an example

Consider:

```cpp
s = "aab";
p = "c*a*b";
```

We know the expected answer is:

```cpp
true
```

Let's understand exactly how the DP table arrives at that answer.

### Step 1: Identify the string and pattern prefixes

The string prefixes are:

```text
""
"a"
"aa"
"aab"
```

The pattern prefixes are:

```text
""
"c"
"c*"
"c*a"
"c*a*"
"c*a*b"
```

Our DP table will have:

```cpp
m + 1 = 4
```

rows and:

```cpp
n + 1 = 6
```

columns.

### Step 2: Initialize the base cases

We know:

```cpp
dp[0][0] = true;
```

An empty pattern cannot match a nonempty string.

Therefore:

```cpp
dp[1][0] = false;
dp[2][0] = false;
dp[3][0] = false;
```

Now initialize the first row.

The pattern:

```text
c*
```

can match an empty string.

Therefore:

```cpp
dp[0][2] = true;
```

Similarly:

```text
c*a*
```

can match an empty string.

Therefore:

```cpp
dp[0][4] = true;
```

The initialized table looks like:

| String prefix | `""` | `"c"` | `"c*"` | `"c*a"` | `"c*a*"` | `"c*a*b"` |
|---|---|---|---|---|---|---|
| `""` | T | F | T | F | T | F |
| `"a"` | F | ? | ? | ? | ? | ? |
| `"aa"` | F | ? | ? | ? | ? | ? |
| `"aab"` | F | ? | ? | ? | ? | ? |

Now let's calculate some important cells.

---

### Step 3: Calculate `dp[1][3]`

This asks:

```text
Does "a" match "c*a"?
```

The current string character is:

```cpp
s[0] = 'a';
```

The current pattern character is:

```cpp
p[2] = 'a';
```

They match.

Also:

```cpp
p[2] != '*';
```

Therefore, use the normal character transition:

```cpp
dp[1][3] = dp[0][2];
```

We already know:

```cpp
dp[0][2] = true;
```

Therefore:

```cpp
dp[1][3] = true;
```

Why?

Because:

```text
c* → ""

a  → "a"
```

So the pattern matches the string.

---

### Step 4: Calculate `dp[1][4]`

This asks:

```text
Does "a" match "c*a*"?
```

The current pattern character is:

```cpp
p[3] = '*';
```

The preceding pattern element is:

```cpp
p[2] = 'a';
```

We have two possibilities.

**Option 1: Let `a*` match zero occurrences.**

```cpp
dp[1][4] = dp[1][2];
```

But:

```cpp
dp[1][2] = false;
```

because `"a"` cannot match `"c*"`.

So this option fails.

**Option 2: Let `a*` consume the current `'a'`.**

The current characters match:

```cpp
s[0] == p[2];
```

Therefore, consider:

```cpp
dp[0][4];
```

We already know:

```cpp
dp[0][4] = true;
```

because `"c*a*"` can match an empty string.

So:

```cpp
dp[1][4] =
    dp[1][2] || dp[0][4];
```

Substitute:

```cpp
dp[1][4] = false || true;
```

Therefore:

```cpp
dp[1][4] = true;
```

The matching interpretation is:

```text
c* → ""

a* → "a"
```

---

### Step 5: Calculate `dp[2][4]`

This asks:

```text
Does "aa" match "c*a*"?
```

Again, the pattern ends in:

```text
a*
```

Try both possibilities.

**Option 1: Zero occurrences**

```cpp
dp[2][2]
```

This asks whether:

```text
"aa" matches "c*"
```

The answer is:

```cpp
false
```

**Option 2: One or more occurrences**

The last string character is:

```cpp
s[1] = 'a';
```

The preceding pattern element is:

```cpp
p[2] = 'a';
```

They match.

Therefore, we can consume the current `'a'` and reuse the same `a*` group:

```cpp
dp[1][4]
```

We already calculated:

```cpp
dp[1][4] = true;
```

Therefore:

```cpp
dp[2][4] =
    dp[2][2] || dp[1][4];
```

Substitute:

```cpp
dp[2][4] = false || true;
```

So:

```cpp
dp[2][4] = true;
```

The matching interpretation is:

```text
c* → ""

a* → "aa"
```

Notice how:

```cpp
dp[i - 1][j]
```

allows us to match multiple occurrences of `'a'`.

---

### Step 6: Calculate `dp[3][5]`

This is our final answer.

It asks:

```text
Does "aab" match "c*a*b"?
```

Compare the last characters:

```cpp
s[2] = 'b';
p[4] = 'b';
```

They match.

The pattern character is not `*`.

Therefore:

```cpp
dp[3][5] = dp[2][4];
```

We already know:

```cpp
dp[2][4] = true;
```

Therefore:

```cpp
dp[3][5] = true;
```

This means the entire pattern matches the entire string.

### Final DP table

| String prefix | `""` | `"c"` | `"c*"` | `"c*a"` | `"c*a*"` | `"c*a*b"` |
|---|---|---|---|---|---|---|
| `""` | T | F | T | F | T | F |
| `"a"` | F | F | F | T | T | F |
| `"aa"` | F | F | F | F | T | F |
| `"aab"` | F | F | F | F | F | **T** |

Notice:

```cpp
dp[3][5] = true;
```

Therefore:

```cpp
answer = true;
```

The complete matching interpretation is:

```text
Pattern:   c*   a*   b
           |    |    |
           v    v    v
String:    ""   aa   b
```

---

## Hint 14: Why can you fill the DP table row by row?

Look carefully at the transitions.

### When the pattern character is not `*`

```cpp
dp[i][j] =
    match && dp[i - 1][j - 1];
```

You need:

```cpp
dp[i - 1][j - 1]
```

This is the diagonal cell from the previous row.

### When the pattern character is `*`

You need:

```cpp
dp[i][j - 2]
```

for zero occurrences.

And possibly:

```cpp
dp[i - 1][j]
```

for one or more occurrences.

These states correspond to:

```text
                    dp[i-1][j]
                         |
                         v
dp[i][j-2] --------> dp[i][j]
```

And for ordinary characters:

```text
dp[i-1][j-1]
       |
       v
    dp[i][j]
```

So each cell depends on:

1. A cell from the previous row and previous column.
2. A cell from the previous row and same column.
3. A cell two positions to the left in the current row.

If we process rows from top to bottom and columns from left to right, all these dependencies will already be calculated.

Therefore, our loops can look like:

```cpp
for (int i = 1; i <= m; i++) {

    for (int j = 1; j <= n; j++) {

        // Calculate dp[i][j].
    }
}
```

This is a standard **2D Grid DP traversal**.

Notice that we don't need interval DP like LeetCode 312. Burst Balloons.

In this problem, matching decisions reduce the lengths of string and pattern prefixes.

Therefore, prefix-based DP is a natural fit.

---

## Hint 15: What is the final answer?

Recall:

```cpp
dp[i][j]
```

represents:

```text
Whether the first i characters of s
can be completely matched by
the first j characters of p.
```

Let:

```cpp
int m = s.size();
int n = p.size();
```

We want to know whether the entire string matches the entire pattern.

Therefore:

```cpp
dp[m][n]
```

contains our answer.

In C++:

```cpp
return dp[m][n];
```

You don't need to search through the DP table.

The bottom-right cell already represents the full matching problem.

---

## Hint 16: Can you solve this using top-down DP?

Yes.

Just like many other DP problems, you can solve Regular Expression Matching using:

**Recursion + Memoization.**

Define:

```cpp
solve(i, j)
```

as:

```text
Whether the first i characters of s
match the first j characters of p.
```

Now consider the cases.

### Case 1: The pattern is empty

If:

```cpp
j == 0
```

then the pattern can match only if the string is also empty.

```cpp
return i == 0;
```

### Case 2: The current pattern character is not `*`

You need the current string character to match the current pattern character.

Then recursively check:

```cpp
solve(i - 1, j - 1)
```

### Case 3: The current pattern character is `*`

Try skipping the entire preceding `x*` group:

```cpp
solve(i, j - 2)
```

Or, if the preceding element matches the current string character, try consuming that character:

```cpp
solve(i - 1, j)
```

Combine these possibilities using OR.

### Recursive structure

```text
                   solve(i, j)
                        |
                 Check p[j-1]
                        |
            ┌───────────┴────────────┐
            |                        |
         Not '*'                    '*'
            |                        |
            v                        v
       Characters              Two choices
         match?                     |
            |              ┌────────┴────────┐
            v              |                 |
   solve(i-1, j-1)      Skip x*        Consume one
                           |           character
                           v                 |
                     solve(i,j-2)            v
                                      solve(i-1,j)
```

Without memoization, the recursion can repeat the same states many times.

For example:

```cpp
solve(3, 4)
```

might be reached through multiple different matching choices.

Instead of recalculating it, store the result.

Use:

```cpp
vector<vector<int>> memo(
    m + 1,
    vector<int>(n + 1, -1)
);
```

Interpret the values as:

```text
-1 → Not calculated.

 0 → False.

 1 → True.
```

The memoization table ensures each state is computed at most once.

**Important observation:** Top-down recursion is often easier to derive because it directly follows the matching decisions. Bottom-up DP makes the evaluation order explicit.

---

## Hint 17: Can you optimize the space?

Look at the recurrence again.

For an ordinary character:

```cpp
dp[i][j] =
    match && dp[i - 1][j - 1];
```

For `*`:

```cpp
dp[i][j] =
    dp[i][j - 2];
```

and possibly:

```cpp
dp[i][j] =
    dp[i][j] || dp[i - 1][j];
```

Notice that you only need:

```text
Previous row

Current row
```

You don't need all the earlier rows.

Therefore, you can optimize the space from:

```cpp
vector<vector<bool>> dp(
    m + 1,
    vector<bool>(n + 1, false)
);
```

to:

```cpp
vector<bool> dp(n + 1, false);
```

However, there's an important challenge.

### Understand what each value represents

Suppose you're processing row `i`.

Before updating:

```cpp
dp[j]
```

it represents:

```cpp
dp[i - 1][j]
```

This is the value from the previous row.

After updating:

```cpp
dp[j]
```

it represents:

```cpp
dp[i][j]
```

This is the value from the current row.

Let's examine the required dependencies.

### Dependency 1: Previous diagonal

For ordinary characters, you need:

```cpp
dp[i - 1][j - 1]
```

But:

```cpp
dp[j - 1]
```

has already been updated for the current row.

Therefore, the previous diagonal value would be lost unless you save it.

You need a temporary variable:

```cpp
bool prevDiagonal;
```

### Dependency 2: Previous row, same column

When `*` consumes a character, you need:

```cpp
dp[i - 1][j]
```

Before updating `dp[j]`, its old value contains exactly that state.

Save it:

```cpp
bool oldTop = dp[j];
```

### Dependency 3: Current row, two columns to the left

For the zero-occurrence case, you need:

```cpp
dp[i][j - 2]
```

Because you're processing the row from left to right, this value has already been updated.

Therefore, it is available as:

```cpp
dp[j - 2]
```

### Summary

Before updating `dp[j]`:

| Required 2D state | 1D representation |
|---|---|
| `dp[i-1][j-1]` | `prevDiagonal` |
| `dp[i-1][j]` | `oldTop` |
| `dp[i][j-2]` | `dp[j-2]` |

After updating:

```cpp
dp[j]
```

you must update the saved diagonal for the next column.

```cpp
prevDiagonal = oldTop;
```

This is similar to the space optimization used in Edit Distance.

**Important:** Unlike bottom-up interval DP, this prefix DP can be compressed to one dimension because its dependencies are limited to the current and previous rows.

---

## Hint 18: Analyze the complexity

Let:

```cpp
m = s.size();
n = p.size();
```

### Time complexity

The DP table contains:

```cpp
(m + 1) * (n + 1)
```

states.

For every state, we perform a constant amount of work.

For an ordinary character:

```text
Compare characters.

Check diagonal state.
```

For `*`:

```text
Check zero-occurrence state.

Check whether preceding element matches.

Possibly check one-or-more state.
```

Each operation takes constant time.

Therefore:

\[
\boxed{\text{Time: }O(m\times n)}
\]

### Space complexity

For the standard 2D DP:

\[
\boxed{\text{Space: }O(m\times n)}
\]

For the space-optimized 1D DP:

\[
\boxed{\text{Space: }O(n)}
\]

For top-down memoization, the memo table uses `O(m × n)` space, with an additional recursion stack of at most `O(m + n)`.

The overall asymptotic space complexity remains:

\[
O(m\times n)
\]

---

# Skeleton — 2D DP (C++)

Try filling in the missing expressions yourself.

```cpp
class Solution {
public:
    bool isMatch(string s, string p) {

        int m = s.size();
        int n = p.size();

        // dp[i][j] = Whether first i
        // characters of s match first j
        // characters of p.

        vector<vector<bool>> dp(
            m + 1,
            vector<bool>(n + 1, false)
        );

        // Base case 1:
        // Empty string matches empty pattern.

        dp[0][0] =  ? ;

        // Base case 2:
        // Empty string can match patterns
        // such as a*, a*b*, and .*.

        for (int j = 2; j <= n; j++) {

            if (p[j - 1] == '*') {

                dp[0][j] =  ? ;
            }
        }

        // Process all nonempty string
        // and pattern prefixes.

        for (int i = 1; i <= m; i++) {

            for (int j = 1; j <= n; j++) {

                // Case 1:
                // Current pattern character
                // is NOT '*'.

                if (p[j - 1] != '*') {

                    // Check whether current
                    // characters match.

                    bool match =  ? ;

                    // If matched, move
                    // diagonally.

                    dp[i][j] =  ? ;

                } else {

                    // Case 2:
                    // Current pattern character
                    // IS '*'.

                    // Option 1:
                    // Match zero occurrences
                    // of the preceding element.

                    dp[i][j] =  ? ;

                    // Option 2:
                    // Match one or more
                    // occurrences.

                    bool match =  ? ;

                    if (match) {

                        // Consume one character
                        // but keep the x* group.

                        dp[i][j] =
                            dp[i][j] ||  ? ;
                    }
                }
            }
        }

        // Does entire string
        // match entire pattern?

        return  ? ;
    }
};
```

The most important expressions to derive are:

```cpp
// Ordinary character or '.'

bool match =  ? ;

dp[i][j] =  ? ;
```

and:

```cpp
// '*' matches zero occurrences.

dp[i][j] =  ? ;

// '*' matches one or more occurrences.

dp[i][j] = dp[i][j] ||  ? ;
```

If you understand why the second transition keeps the pattern index unchanged, you've understood the hardest part of this problem.

---

# Skeleton — Top-Down DP + Memoization (C++)

Once you're comfortable with the 2D solution, try implementing the recursive version.

```cpp
class Solution {
private:

    string s;
    string p;

    vector<vector<int>> memo;

    // solve(i, j) determines whether
    // first i characters of s match
    // first j characters of p.

    bool solve(int i, int j) {

        // Base case:
        // Pattern is empty.

        if (j == 0) {

            return ? ;
        }

        // Already calculated?

        if (memo[i][j] != -1) {

            return  ? ;
        }

        bool ans = false;

        // Case 1:
        // Current pattern character
        // is NOT '*'.

        if (p[j - 1] != '*') {

            // String must be nonempty
            // to match one character.

            if (i > 0) {

                bool match =  ? ;

                if (match) {

                    ans =  ? ;
                }
            }

        } else {

            // Case 2:
            // Current pattern character
            // IS '*'.

            // Option 1:
            // Match zero occurrences.

            ans =  ? ;

            // Option 2:
            // Match one or more occurrences.

            if (i > 0) {

                bool match =  ? ;

                if (match) {

                    ans = ans ||  ? ;
                }
            }
        }

        // Save result.

        memo[i][j] =  ? ;

        return ans;
    }

public:

    bool isMatch(string s, string p) {

        this->s = s;
        this->p = p;

        int m = s.size();
        int n = p.size();

        // -1 = not calculated
        //  0 = false
        //  1 = true

        memo.assign(
            m + 1,
            vector<int>(n + 1, -1)
        );

        // Match entire string
        // against entire pattern.

        return solve( ? ,  ? );
    }
};
```

The trickiest part is handling the `*` case when the string prefix is empty.

Even if:

```cpp
i == 0
```

the pattern may still match because an `x*` group can represent zero occurrences.

For example:

```cpp
s = "";
p = "a*b*";
```

Therefore, don't immediately return `false` whenever `i == 0`.

Instead, let the zero-occurrence transition handle that situation.

---

# Skeleton — 1D Space-Optimized DP (C++)

Once you understand the 2D version, try compressing it to one dimension.

```cpp
class Solution {
public:
    bool isMatch(string s, string p) {

        int m = s.size();
        int n = p.size();

        // dp[j] stores matching results
        // for pattern prefix of length j.

        vector<bool> dp(n + 1, false);

        // Empty string matches
        // empty pattern.

        dp[0] = true;

        // Initialize first row:
        // Match empty string with
        // pattern prefixes.

        for (int j = 2; j <= n; j++) {

            if (p[j - 1] == '*') {

                dp[j] =  ? ;
            }
        }

        // Process string characters.

        for (int i = 1; i <= m; i++) {

            // Save previous row's
            // diagonal value.

            bool prevDiagonal = dp[0];

            // Nonempty string cannot
            // match empty pattern.

            dp[0] =  ? ;

            for (int j = 1; j <= n; j++) {

                // Save old dp[j],
                // which represents dp[i-1][j].

                bool oldTop = dp[j];

                if (p[j - 1] != '*') {

                    // Ordinary character or '.'

                    bool match =  ? ;

                    // Use previous diagonal.

                    dp[j] =  ? ;

                } else {

                    // Option 1:
                    // Zero occurrences of x.

                    bool zeroOccurrences =  ? ;

                    // Option 2:
                    // One or more occurrences.

                    bool oneOrMore = false;

                    bool match =  ? ;

                    if (match) {

                        // oldTop represents
                        // dp[i-1][j].

                        oneOrMore =  ? ;
                    }

                    // Either possibility
                    // can produce a match.

                    dp[j] =  ? ;
                }

                // Update diagonal for
                // next column.

                prevDiagonal =  ? ;
            }
        }

        return  ? ;
    }
};
```

The trickiest part is preserving the previous diagonal value before overwriting `dp[j]`.

---

# Core Recurrence

Define:

```cpp
dp[i][j]
```

as:

```text
Whether the first i characters of s
can be completely matched by
the first j characters of p.
```

## Base cases

An empty string matches an empty pattern:

```cpp
dp[0][0] = true;
```

A nonempty string cannot match an empty pattern:

```cpp
dp[i][0] = false;   // i > 0
```

An empty string can match a pattern prefix ending in `*` if removing the final `x*` group leaves another pattern prefix that matches the empty string:

```cpp
if (p[j - 1] == '*') {

    dp[0][j] = dp[0][j - 2];
}
```

## Case 1: The pattern character is not `*`

```cpp
if (p[j - 1] != '*') {

    bool match =
        (s[i - 1] == p[j - 1]) ||
        (p[j - 1] == '.');

    dp[i][j] =
        match && dp[i - 1][j - 1];
}
```

## Case 2: The pattern character is `*`

```cpp
else {

    // Zero occurrences.

    dp[i][j] = dp[i][j - 2];

    // One or more occurrences.

    bool match =
        (s[i - 1] == p[j - 2]) ||
        (p[j - 2] == '.');

    if (match) {

        dp[i][j] =
            dp[i][j] || dp[i - 1][j];
    }
}
```

These transitions are evaluated for `i >= 1` and `j >= 1`. In valid patterns, a `*` always has a preceding element, so `j >= 2` whenever the `*` branch is used.

## Final answer

```cpp
return dp[m][n];
```

### Mathematical representation

Let:

\[
M(i,j) =
(s[i-1]=p[j-1])
\lor
(p[j-1]='.')
\]

Then, when the current pattern character is not `*`:

\[
\boxed{
dp[i][j] =
M(i,j)\land dp[i-1][j-1]
}
\]

When the current pattern character is `*`, define:

\[
M^*(i,j) =
(s[i-1]=p[j-2])
\lor
(p[j-2]='.')
\]

Then:

\[
\boxed{
dp[i][j] =
dp[i][j-2]
\lor
\left(
M^*(i,j)\land dp[i-1][j]
\right)
}
\]

The second term is used only when `i > 0`.

The first part:

\[
dp[i][j-2]
\]

represents matching zero occurrences.

The second part:

\[
M^*(i,j)\land dp[i-1][j]
\]

represents matching one or more occurrences.

---

# Connection to Previous DP Problems

The most useful connection is with **LeetCode 44. Wildcard Matching**.

Both problems involve matching a string against a pattern containing special characters.

However, the meaning of `*` is different.

## LeetCode 44 — Wildcard Matching

The pattern supports:

```text
? → Matches exactly one arbitrary character.

* → Matches any sequence of characters,
    including an empty sequence.
```

For example:

```cpp
s = "abc";
p = "a*";
```

In Wildcard Matching:

```text
a → "a"

* → "bc"
```

Therefore:

```cpp
answer = true;
```

Here, `*` independently matches arbitrary characters.

---

## LeetCode 10 — Regular Expression Matching

The pattern supports:

```text
. → Matches exactly one arbitrary character.

* → Matches zero or more occurrences
    of the preceding element.
```

For example:

```cpp
s = "abc";
p = "a*";
```

In Regular Expression Matching, `a*` can only match repetitions of `'a'`.

It cannot match:

```text
"abc"
```

Therefore:

```cpp
answer = false;
```

However:

```cpp
s = "abc";
p = ".*";
```

returns:

```cpp
true
```

because `.*` can match any sequence of characters.

### Comparison

| Feature | 44. Wildcard Matching | 10. Regular Expression Matching |
|---|---|---|
| Single-character wildcard | `?` | `.` |
| Meaning of `*` | Matches any sequence | Repeats preceding element zero or more times |
| Can `*` match empty? | Yes | Yes, as part of `x*` |
| Does `*` depend on preceding character? | No | Yes |
| Main DP type | Prefix-based 2D DP | Prefix-based 2D DP |
| Goal | Determine complete match | Determine complete match |

### Why does this distinction matter?

In Wildcard Matching, when `*` consumes a character, you don't need to check whether that character matches a preceding pattern element.

In Regular Expression Matching, you must check:

```cpp
s[i - 1] == p[j - 2]
```

or:

```cpp
p[j - 2] == '.'
```

before letting `*` consume the current string character.

---

# Common Mistakes to Avoid

## Mistake 1: Thinking `*` matches any sequence

This is incorrect:

```text
* → Any sequence
```

That is the behavior in LeetCode 44.

In LeetCode 10:

```text
* → Zero or more occurrences
    of the preceding element
```

For example:

```cpp
s = "bbb";
p = "a*";
```

The answer is:

```cpp
false
```

But:

```cpp
s = "bbb";
p = ".*";
```

returns:

```cpp
true
```

---

## Mistake 2: Forgetting the zero-occurrence option

Consider:

```cpp
s = "b";
p = "a*b";
```

If you always require `a*` to consume at least one character, the match would fail.

But:

```text
a* → ""
```

is valid.

Therefore:

```cpp
dp[i][j - 2]
```

must always be considered when the current pattern character is `*`.

---

## Mistake 3: Decreasing both indices when `*` consumes a character

A common incorrect transition is:

```cpp
dp[i - 1][j - 1]
```

Why is this wrong?

Because `j - 1` points to the pattern prefix without the `*`, leaving the preceding element as an ordinary single character.

It doesn't preserve the ability of `x*` to match additional characters.

Instead, when `*` consumes one character:

```cpp
dp[i - 1][j]
```

is the correct subproblem.

The pattern remains available for matching more characters.

---

## Mistake 4: Accessing `s[i - 1]` when `i == 0`

Consider:

```cpp
s = "";
p = "a*";
```

If you attempt:

```cpp
s[i - 1]
```

when:

```cpp
i == 0
```

you access an invalid index.

The bottom-up solution avoids this by initializing the empty-string row separately.

The top-down solution must check:

```cpp
i > 0
```

before accessing the current string character.

---

## Mistake 5: Forgetting that the entire string must match

Consider:

```cpp
s = "abc";
p = "ab";
```

The pattern matches the first two characters.

But the last character remains unmatched.

Therefore:

```cpp
answer = false;
```

You must return:

```cpp
dp[m][n]
```

not the result of matching some shorter prefix.

---

# Three Important Rules to Remember

### Rule 1: Ordinary characters and `.` consume exactly one character

```text
Pattern character is NOT '*'
            ↓
Check whether characters match
            ↓
If compatible, consume one
character from each prefix
            ↓
dp[i-1][j-1]
```

In C++:

```cpp
dp[i][j] =
    match && dp[i - 1][j - 1];
```

### Rule 2: `*` can match zero occurrences

```text
Current pattern character is '*'
            ↓
Choose ZERO occurrences
            ↓
Ignore preceding element and '*'
            ↓
dp[i][j-2]
```

In C++:

```cpp
dp[i][j] = dp[i][j - 2];
```

### Rule 3: `*` can match one or more occurrences

```text
Current pattern character is '*'
            ↓
Does preceding element match
current string character?
            ↓
           YES
            ↓
Consume one string character
            ↓
Keep pattern group unchanged
            ↓
dp[i-1][j]
```

In C++:

```cpp
if (s[i - 1] == p[j - 2] ||
    p[j - 2] == '.') {

    dp[i][j] =
        dp[i][j] || dp[i - 1][j];
}
```

---

## One Final Mental Trick

Whenever you encounter:

```text
x*
```

think of it as having two choices:

```text
                  x*
                   |
          ┌────────┴────────┐
          |                 |
          v                 v
     SKIP ENTIRE         USE x* TO
        GROUP            MATCH MORE
          |                 |
          v                 v
   Zero occurrences     One or more
          |             occurrences
          |                 |
          v                 v
   Remove x and *       Consume one
   from the pattern     string character
          |                 |
          v                 v
   dp[i][j-2]          dp[i-1][j]
                            |
                            v
                    Keep x* available
                    for more matching
```

This is the single most important concept in LeetCode 10.

### Final mental model

```text
           Given s and p
                 |
                 v
         Define dp[i][j]
                 |
                 v
      Does first i chars of s
      match first j chars of p?
                 |
                 v
         Initialize dp[0][0]
                 |
                 v
       Initialize empty-string
          matching cases
                 |
                 v
       Process every (i, j)
                 |
                 v
         Is p[j-1] == '*'?
                 |
          ┌──────┴──────┐
          |             |
         NO            YES
          |             |
          v             v
    Match normal    Two options:
    character or .       |
          |        ┌────┴─────┐
          |        |          |
          v        v          v
     Match?      Skip x*    Consume
          |        |        one char
          v        v          |
    dp[i-1][j-1] dp[i][j-2]   v
                          dp[i-1][j]
                              |
                ┌─────────────┘
                |
                v
          Combine using OR
                |
                v
          Store dp[i][j]
                |
                v
          Return dp[m][n]
```

**The biggest takeaway:** LeetCode 10 is a classic example of *boolean string-matching DP with branching pattern operations*.

Once you understand why ordinary characters move diagonally, why skipping `x*` moves two positions left, and why consuming a character with `x*` moves up while retaining the same pattern prefix, the recurrence becomes intuitive.

\[
\boxed{
dp[i][j] =
dp[i][j-2]
\lor
\left(
match\land dp[i-1][j]
\right)
}
\]

For the `*` case, this is the recurrence to remember, with `match` comparing the current string character to the element preceding `*`.

**Time:** \(O(m\times n)\)  
**Space:** \(O(m\times n)\), optimized to \(O(n)\).

**The most important conceptual skill is recognizing that `*` creates two possible decisions: skip the entire repeated group or consume one matching character while keeping that group available for further matching.**
*/
class Solution {
public:
    bool isMatch(string s, string p) {
        int m = s.size();
        int n = p.size();

        vector<vector<bool>> dp(m + 1, vector<bool>(n + 1, false));
        dp[0][0] = true;

        for(int j = 2; j <= n; j++){
            if(p[j - 1] == '*'){
                dp[0][j] = dp[0][j-2];
            }
        }

        for(int i=1; i<=m; i++){
            for(int j=1; j<=n; j++){
                if(p[j-1] != '*'){
                    bool match = (s[i-1] == p[j-1]) || (p[j-1] == '.');
                    dp[i][j] = match && dp[i-1][j-1];

                }else{
                    dp[i][j] = dp[i][j-2];

                    bool match = (s[i-1] == p[j-2]) || (p[j-2] == '.');

                    if(match){
                        dp[i][j] = dp[i][j] || dp[i-1][j];
                    }
                }
            }
        }

        return dp[m][n];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna