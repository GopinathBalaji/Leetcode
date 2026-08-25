// Method 1: DP + Memoization (Trie + DP method also exists and is better)
/*
This is a **dynamic programming / memoized recursion** problem. A good way to think about it is:

> Starting at index `i`, what is the minimum number of extra characters needed for `s[i...]`?

### Hint 1: Define the state

Let:

```cpp
dp(i)
```

mean:

```text
minimum extra characters needed from s[i...] onward
```

Your base case is:

```cpp
if (i == s.size()) {
    return 0;
}
```

### Hint 2: You always have the option to treat `s[i]` as extra

If you don't match a dictionary word starting at `i`, you can simply count the current character as extra:

```cpp
1 + dp(i + 1)
```

This is a very useful default answer.

So conceptually:

```cpp
int result = 1 + dp(i + 1);
```

### Hint 3: Try every substring starting at `i`

Now see whether you can do better by matching a dictionary word.

Try:

```cpp
for (int j = i; j < s.size(); j++) {
    string word = s.substr(i, j - i + 1);

    if (dict.count(word)) {
        // this substring contributes 0 extra characters
    }
}
```

If `s[i...j]` is a dictionary word, then you can jump directly to:

```cpp
dp(j + 1)
```

because the whole substring was matched and contributes **zero** extra characters.

So you want the minimum between:

```text
1 + dp(i + 1)

and

dp(j + 1)   for every dictionary word s[i...j]
```

### Hint 4: Memoize by index

Without memoization, you'll solve the same suffix repeatedly.

Use something like:

```cpp
vector<int> memo(s.size(), -1);
```

Then:

```cpp
if (memo[i] != -1) {
    return memo[i];
}
```

### Hint 5: Skeleton

```cpp
int dfs(string& s,
        int i,
        unordered_set<string>& dict,
        vector<int>& memo) {

    if (i == s.size()) {
        return 0;
    }

    if (memo[i] != -1) {
        return memo[i];
    }

    // Option 1:
    // treat s[i] as an extra character

    int result = 1 + dfs(s, i + 1, dict, memo);

    // Option 2:
    // try every substring starting at i
    // if substring is in dict:
    //     result = min(result, dfs(...))

    return memo[i] = result;
}
```

For example:

```text
s = "leetscode"
dictionary = ["leet", "code", "leetcode"]
```

One path is:

```text
"leet" → matched
"s"    → extra
"code" → matched
```

so the answer is `1`.

The key recurrence to understand is:

```text
At index i:

either
    count s[i] as extra and move to i + 1

or
    match a dictionary word beginning at i
    and jump past the whole word
```

Since you've just worked with Tries, there is also a **Trie + DP** version that avoids repeatedly creating substrings, but I'd get the `unordered_set + memoization` version working first.
*/
class Solution {
private:
    int dfs(string& s, int i, unordered_set<string>& dict, vector<int>& memo){
        if(i == s.size()){
            return 0;
        }

        if(memo[i] != -1){
            return memo[i];
        }

        int result = 1 + dfs(s, i + 1, dict, memo);

        for(int j=i; j<s.size(); j++){
            string word = s.substr(i, j - i + 1);

            if(dict.count(word)){
                result = std::min(result, dfs(s, j + 1, dict, memo));
            }
        }

        return memo[i] = result;
    }

public:
    int minExtraChar(string s, vector<string>& dictionary) {
        unordered_set<string> dict(dictionary.begin(), dictionary.end());
        vector<int> memo(s.size(), -1);

        return dfs(s, 0, dict, memo);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna