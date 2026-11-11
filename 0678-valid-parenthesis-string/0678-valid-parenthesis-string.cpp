// Method 1: Greedy
/*
## LeetCode 678: Valid Parenthesis String (C++)

Approach: Greedy | Difficulty: Medium

### Hint 1: Think Greedy

The string contains three types of characters:

- `'('` → Opening parenthesis
- `')'` → Closing parenthesis
- `'*'` → Can represent `'('`, `')'`, or an empty string

Key idea: Track the minimum and maximum possible number of unmatched opening parentheses.

### Hint 2: Track Two Variables

```
int low = 0;
int high = 0;
```

- `low`: Minimum possible unmatched `'('`.
- `high`: Maximum possible unmatched `'('`.

### Hint 3: Update Based on Character

```
if (c == '(') {
    low++;
    high++;
}
else if (c == ')') {
    low--;
    high--;
}
else {
    low--;
    high++;
}
```

Why? For `'*'`, treating it as `')'` decreases the minimum, while treating it as `'('` increases the maximum.

### Hint 4: Handle Invalid States

If `high < 0`, too many closing parentheses have appeared.

Also, `low` cannot be negative because `'*'` can represent an empty string.

```
if (high < 0)
    return false;

low = max(0, low);
```

### Hint 5: Final Validation

At the end, the string is valid if the minimum unmatched opening parentheses is zero.

```
return low == 0;
```

### Complete C++ Solution

```
class Solution {
public:
    bool checkValidString(string s) {
        int low = 0, high = 0;

        for (char c : s) {
            if (c == '(') {
                low++;
                high++;
            }
            else if (c == ')') {
                low--;
                high--;
            }
            else {
                low--;
                high++;
            }

            if (high < 0)
                return false;

            low = max(0, low);
        }

        return low == 0;
    }
};
```

### Complexity

- Time: O(n) — Single traversal.
- Space: O(1) — Only two variables.

### Example

```
s = "(*))"
```

| Character | low | high |
| --------- | --- | ---- |
| `(`       | 1   | 1    |
| `*`       | 0   | 2    |
| `)`       | 0   | 1    |
| `)`       | 0   | 0    |

Answer: `true`

We can interpret `'*'` as `'('`, producing `"(())"`.

### Key Intuition

Greedy + Range Tracking = O(n)

Instead of trying all three possibilities for every `'*'`, maintain a range of possible open-parenthesis counts.

Interview tip: When a problem involves wildcard characters with multiple possible interpretations, consider tracking the minimum and maximum possible states instead of exploring every possibility.
*/
class Solution {
public:
    bool checkValidString(string s) {
        int low = 0;
        int high = 0;

        for(char c : s){
            if(c == '('){
                low++;
                high++;
            }else if(c == ')'){
                low--;
                high--;
            }else{
                low--;
                high++;
            }

            if(high < 0){
                return false;
            }

            low = std::max(0, low);
        }

        return low == 0;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna