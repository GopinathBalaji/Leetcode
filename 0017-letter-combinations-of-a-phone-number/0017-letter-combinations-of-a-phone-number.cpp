// Method 1: Backtracking
/*
This is another **backtracking** problem, but here each digit gives you a small set of letter choices.

### Hint 1: Map each digit to its letters

For example:

```cpp
vector<string> mapping = {
    "",     // 0
    "",     // 1
    "abc",  // 2
    "def",  // 3
    "ghi",  // 4
    "jkl",  // 5
    "mno",  // 6
    "pqrs", // 7
    "tuv",  // 8
    "wxyz"  // 9
};
```

Then for a digit like `'2'`, convert it to an integer index with:

```cpp
digits[index] - '0'
```

### Hint 2: Your recursive state can be very small

You only really need:

```cpp
index
current
result
```

`index` tells you which digit you're currently processing.

### Hint 3: At each digit, loop over all possible letters

Suppose:

```text
digits = "23"
```

At index `0`, digit `2` gives:

```text
a
b
c
```

For each one, recurse to the next digit.

Conceptually:

```cpp
string letters = mapping[digits[index] - '0'];

for (char c : letters) {
    // choose c
    // recurse to index + 1
    // undo
}
```

### Hint 4: Base case

A combination is finished when you've chosen one letter for every digit:

```cpp
if (index == digits.size()) {
    result.push_back(current);
    return;
}
```

### Hint 5: Standard backtracking pattern

```cpp
current.push_back(c);

backtrack( index + 1 );

current.pop_back();
```

So your skeleton could look like:

```cpp
void backtrack(string& digits,
               int index,
               vector<string>& mapping,
               string& current,
               vector<string>& result) {

    if (index == digits.size()) {
        // save current
        return;
    }

    string letters = mapping[digits[index] - '0'];

    for (char c : letters) {

        // choose c

        // recurse to next digit

        // undo
    }
}
```

For `"23"`, think of the recursion tree as:

```text
""
├── a
│   ├── d → "ad"
│   ├── e → "ae"
│   └── f → "af"
├── b
│   ├── d → "bd"
│   ├── e → "be"
│   └── f → "bf"
└── c
    ├── d → "cd"
    ├── e → "ce"
    └── f → "cf"
```

One important edge case: if `digits` is empty, the expected answer is an empty vector, not a vector containing `""`.
*/
class Solution {
private:
    void backtrack(string& digits, vector<string>& mapping, int index, string& current, vector<string>& result){
        if(index == digits.size()){
            result.push_back(current);
            return;
        }

        string letters = mapping[digits[index] - '0'];

        for(char c: letters){
            current.push_back(c);

            backtrack(digits, mapping, index + 1, current, result);

            current.pop_back();
        }

    }


public:
    vector<string> letterCombinations(string digits) {
        vector<string> mapping = {"", "", "abc", "def", "ghi", "jkl", "mno", "pqrs", "tuv", "wxyz"};

        string current = "";
        vector<string> result;

        backtrack(digits, mapping, 0, current, result);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna