// Method 1: Backtracking + Memoization
/*
This is a **backtracking + memoization** problem. The main challenge is generating all valid sentences without recomputing the same suffix over and over.

### Hint 1: Think in terms of splitting the string

Suppose:

```text
s = "catsanddog"
```

Starting at index `0`, try prefixes:

```text
"c"
"ca"
"cat"
"cats"
...
```

Whenever a prefix is in `wordDict`, recurse on the remaining suffix.

A useful recursive state is:

```cpp
backtrack(start)
```

where `start` is the index where the remaining string begins.

### Hint 2: Use a hash set for dictionary lookup

Instead of repeatedly searching a vector:

```cpp
unordered_set<string> dict(wordDict.begin(), wordDict.end());
```

Then checking:

```cpp
if (dict.count(word))
```

is efficient.

### Hint 3: Try every possible ending position

From `start`:

```cpp
for (int end = start; end < s.size(); end++) {
    string word = s.substr(start, end - start + 1);

    if (!dict.count(word)) {
        continue;
    }

    // word is valid
    // recurse on end + 1
}
```

For `"catsanddog"`:

```text
"cat"  → recurse on "sanddog"
"cats" → recurse on "anddog"
```

Both paths can eventually produce valid sentences.

### Hint 4: What should the recursion return?

A nice approach is for:

```cpp
backtrack(start)
```

to return **all valid sentences that can be formed from `s[start...]`**.

For example, if the remaining suffix is:

```text
"dog"
```

the recursive result might be:

```text
["dog"]
```

Then if your current word is `"sand"`, combine them into:

```text
"sand dog"
```

### Hint 5: The base case is slightly tricky

When:

```cpp
start == s.size()
```

you've successfully consumed the whole string.

A useful trick is to return:

```cpp
{""}
```

instead of an empty vector.

Why?

Because then your caller can combine the final word cleanly.

Conceptually:

```cpp
for (string sentence : suffixSentences) {
    if (sentence.empty()) {
        result.push_back(word);
    } else {
        result.push_back(word + " " + sentence);
    }
}
```

### Hint 6: You need memoization

Without memoization, you'll solve the same suffix many times.

For example:

```text
catsanddog
   ↓
sanddog
```

might be reached through multiple recursive paths in larger inputs.

Memoize by `start`:

```cpp
unordered_map<int, vector<string>> memo;
```

Then:

```cpp
if (memo.count(start)) {
    return memo[start];
}
```

### Skeleton

```cpp
vector<string> backtrack(string& s,
                         int start,
                         unordered_set<string>& dict,
                         unordered_map<int, vector<string>>& memo) {

    if (start == s.size()) {
        return {""};
    }

    if (memo.count(start)) {
        return memo[start];
    }

    vector<string> result;

    for (int end = start; end < s.size(); end++) {

        string word = s.substr(start, end - start + 1);

        if (!dict.count(word)) {
            continue;
        }

        vector<string> suffixes =
            backtrack(s, end + 1, dict, memo);

        // combine word with each suffix
    }

    return memo[start] = result;
}
```

The core mental model is:

```text
choose a valid prefix
→ solve the remaining suffix
→ prepend the prefix to every returned sentence
→ memoize by starting index
```

The biggest difference from many earlier backtracking problems is that here it’s especially useful for the recursive function to **return a list of solutions for a suffix**, rather than mutating one global `current` path.
*/
class Solution {
private:
    vector<string> backtrack(string& s, int start, unordered_set<string>& dict, unordered_map<int, vector<string>>& memo){
        if(start == s.size()){
            return {""};
        }

        if(memo.count(start)){
            return memo[start];
        }

        vector<string> result;

        for(int end=start; end<s.size(); end++){
            string word = s.substr(start, end - start + 1);

            if(!dict.count(word)){
                continue;
            }

            vector<string> suffixSentences = backtrack(s, end + 1, dict, memo);

            for(string sentence: suffixSentences){
                if(sentence.empty()){
                    result.push_back(word);
                }else{
                    result.push_back(word + " " + sentence);
                }
            }
        }

        return memo[start] = result;
    }

public:
    vector<string> wordBreak(string s, vector<string>& wordDict) {
        unordered_set<string> dict(wordDict.begin(), wordDict.end());
        unordered_map<int, vector<string>> memo;
        

        return backtrack(s, 0, dict, memo);
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna