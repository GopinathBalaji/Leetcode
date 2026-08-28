// Method 1: Checking whether every adjacent pair of words
/*
Think of this as checking whether every adjacent pair of words is in the correct order according to a custom alphabet.

### Hint 1: Build a rank map

You need to know the position of each character in the alien alphabet.

For example:

```cpp
unordered_map<char, int> rank;
```

or even simpler:

```cpp
vector<int> rank(26);
```

Then:

```cpp
rank[order[i] - 'a'] = i;
```

So comparing two alien letters becomes comparing their ranks.

### Hint 2: Only compare adjacent words

You do not need to compare every word with every other word.

Just verify:

```text
words[0] <= words[1]
words[1] <= words[2]
...
```

If every adjacent pair is correctly ordered, the whole list is sorted.

### Hint 3: Compare characters until they differ

For two words like:

```text
"hello"
"leetcode"
```

scan from left to right.

At the first different character:

```cpp
word1[i] != word2[i]
```

compare their alien ranks:

```cpp
rank[word1[i] - 'a'] < rank[word2[i] - 'a']
```

Once you find the first differing character, you can stop comparing that pair.

### Hint 4: Watch the prefix edge case

This is the main trap.

Consider:

```text
"apple"
"app"
```

All characters match until `"app"` ends.

In normal lexicographical ordering, the shorter prefix must come first:

```text
"app" < "apple"
```

So:

```text
["apple", "app"]
```

must return `false`.

A useful condition after comparing common characters is:

```cpp
if (word1.size() > word2.size() &&
    word1.substr(0, word2.size()) == word2) {
    return false;
}
```

You can avoid actually creating substrings if you track whether all compared characters matched.

### Hint 5: Helper structure

You might write:

```cpp
bool inOrder(string& a, string& b, vector<int>& rank) {
    int len = min(a.size(), b.size());

    for (int i = 0; i < len; i++) {
        if (a[i] != b[i]) {
            // compare rank and return
        }
    }

    // all common characters were equal
    // what does that imply about their lengths?
}
```

Then the main function is just:

```cpp
for (int i = 0; i < words.size() - 1; i++) {
    if (!inOrder(words[i], words[i + 1], rank)) {
        return false;
    }
}

return true;
```

The key mental model is:

```text
first differing character decides the order

unless one word is a prefix of the other,
in which case the shorter word must come first
```
*/
class Solution {
private:
    bool inOrder(string& a, string& b, vector<int>& rank){
        int len = std::min(a.size(), b.size());

        for(int i=0; i<len; i++){
            if(a[i] != b[i]){
                return rank[a[i] - 'a'] < rank[b[i] - 'a'];
            }
        }

        // If one is a prefix of the other, the shorter word must come first
        return a.size() <= b.size();
    }

public:
    bool isAlienSorted(vector<string>& words, string order) {
        vector<int> rank(26);

        for(int i=0; i<order.size(); i++){
            rank[order[i] - 'a'] = i;
        }

        for(int i=0; i<words.size() - 1; i++){
            if(!inOrder(words[i], words[i+1], rank)){
                return false;
            }
        }

        return true;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna