// Method 1: 1D DP + String Segmentation
/*
This is a **Dynamic Programming + String Segmentation** problem.

The key idea is to ask:

```text
Can the prefix ending here be formed using words from wordDict?
```

If some earlier prefix is already valid, and the remaining substring is a dictionary word, then the current prefix is valid too.

### Hint 1: Define your DP state

Let:

```cpp
dp[i]
```

mean:

```text
whether the first i characters of s
can be segmented into dictionary words
```

So:

```cpp
dp[n]
```

is your final answer.

---

### Hint 2: The base case is `dp[0] = true`

Before using any characters:

```text
""
```

the empty prefix is considered valid.

So:

```cpp
dp[0] = true;
```

This gives you a starting point for building valid prefixes.

For example, if:

```cpp
s = "leetcode"
```

and `"leet"` is in the dictionary, then:

```text
dp[0] == true
+
s[0...3] == "leet"
```

allows:

```cpp
dp[4] = true;
```

---

### Hint 3: Try every possible split point

Suppose you're trying to determine:

```cpp
dp[i]
```

Try splitting the prefix at every earlier position `j`:

```text
0 ... j-1 | j ... i-1
```

You want both conditions to be true:

```text
the prefix before j is valid
AND
the substring from j to i-1 is a dictionary word
```

So conceptually:

```cpp
if (dp[j] && wordExists(s.substr(j, i - j))) {
    dp[i] = true;
}
```

---

### Hint 4: The recurrence

For every:

```cpp
i = 1 ... n
```

try:

```cpp
j = 0 ... i - 1
```

and check:

```cpp
dp[j]
```

plus:

```cpp
s.substr(j, i - j)
```

If both work:

```cpp
dp[i] = true;
```

Mental model:

```text
valid prefix
+
valid dictionary word
=
larger valid prefix
```

---

### Hint 5: Example with `"leetcode"`

Suppose:

```cpp
s = "leetcode";
wordDict = {"leet", "code"};
```

Initially:

```text
dp[0] = true
```

When:

```cpp
i = 4
```

try:

```cpp
j = 0
```

Then:

```text
dp[0] == true
s.substr(0, 4) == "leet"
```

and `"leet"` is in the dictionary.

So:

```cpp
dp[4] = true;
```

Later, when:

```cpp
i = 8
```

try:

```cpp
j = 4
```

Then:

```text
dp[4] == true
s.substr(4, 4) == "code"
```

So:

```cpp
dp[8] = true;
```

Therefore the answer is:

```cpp
true
```

---

### Hint 6: Put the dictionary into a hash set

Instead of repeatedly searching through:

```cpp
wordDict
```

use:

```cpp
unordered_set<string> words(
    wordDict.begin(),
    wordDict.end()
);
```

Then you can check:

```cpp
words.count(substring)
```

efficiently.

---

### Hint 7: Skip useless split points

If:

```cpp
dp[j] == false
```

then there's no reason to check:

```cpp
s.substr(j, i - j)
```

because the prefix before that split is already impossible.

So:

```cpp
if (!dp[j]) {
    continue;
}
```

can save some work.

---

### Hint 8: Stop once you find one valid split

You only need to know whether:

```cpp
dp[i]
```

is possible.

So once you find:

```cpp
dp[j] == true
```

and:

```cpp
s.substr(j, i - j)
```

in the dictionary, do:

```cpp
dp[i] = true;
break;
```

No need to keep checking other split points.

---

### Hint 9: Example where greedy fails

Consider:

```cpp
s = "cars";
wordDict = {"car", "ca", "rs"};
```

If you greedily choose:

```text
"car"
```

you're left with:

```text
"s"
```

which fails.

But:

```text
"ca" + "rs"
```

works.

So you cannot simply take the longest matching word each time.

DP keeps track of all reachable prefix boundaries.

---

### Hint 10: Think of `dp` as reachable positions

Another useful mental model:

```text
dp[i] = can I reach index i?
```

Starting from:

```text
position 0
```

if `dp[j]` is true and:

```cpp
s[j...i-1]
```

is a word, then you can jump:

```text
j → i
```

So the problem becomes:

```text
Can I reach position n?
```

---

### Skeleton

```cpp
class Solution {
public:
    bool wordBreak(
        string s,
        vector<string>& wordDict
    ) {
        int n = s.size();

        unordered_set<string> words(
            wordDict.begin(),
            wordDict.end()
        );

        vector<bool> dp(n + 1, false);

        dp[0] = true;

        for (int i = 1; i <= n; i++) {

            for (int j = 0; j < i; j++) {

                if (!dp[j]) {
                    continue;
                }

                string current =
                    s.substr(j, i - j);

                if ( current is in dictionary ) {
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[n];
    }
};
```

The core recurrence is:

```text
dp[i] = true
if there exists some j < i such that:

dp[j] == true
and
s[j ... i-1] is in wordDict
```

The trickiest part is usually getting the meaning of `dp[i]` right: it represents whether the **first `i` characters** can be segmented, not whether character `i` itself is valid.
*/
class Solution {
public:
    bool wordBreak(string s, vector<string>& wordDict) {
        int n = s.size();

        unordered_set<string> words(wordDict.begin(), wordDict.end());

        vector<bool> dp(n+1, false);
        dp[0] = true;

        for(int i=1; i<=n; i++){
            for(int j=0; j<i; j++){
                if(!dp[j]){
                    continue;
                }

                string current = s.substr(j, i-j);

                if(words.count(current)){
                    dp[i] = true;
                    break;
                }
            }
        }

        return dp[n];
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna