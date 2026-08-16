// Method 1: Backtracking
/*
This is another **backtracking** problem, but instead of choosing individual numbers, you choose **substrings**.

### Hint 1: Think in terms of where to cut

Suppose:

```text
s = "aab"
```

Starting at index `0`, you could try:

```text
"a"
"aa"
"aab"
```

But you should only recurse on a substring if that substring is a palindrome.

So a useful recursive state is:

```cpp
start
currentPartition
```

where `start` is the first character you haven't partitioned yet.

### Hint 2: Try every possible ending position

From `start`, loop over all possible substring endings:

```cpp
for (int end = start; end < s.size(); end++) {
    // substring = s[start...end]
}
```

For each substring, ask:

```text
Is s[start...end] a palindrome?
```

If not, skip it.

If yes, add it to your current partition and recurse starting from:

```cpp
end + 1
```

### Hint 3: Base case

When:

```cpp
start == s.size()
```

you have successfully partitioned the entire string.

So:

```cpp
result.push_back(current);
```

For `"aab"`, one successful path would be:

```text
[]
→ ["a"]
→ ["a", "a"]
→ ["a", "a", "b"]
```

Another is:

```text
[]
→ ["aa"]
→ ["aa", "b"]
```

### Hint 4: You'll want a palindrome helper

Something like:

```cpp
bool isPalindrome(string& s, int left, int right) {
    while (left < right) {
        if (s[left] != s[right]) {
            return false;
        }

        left++;
        right--;
    }

    return true;
}
```

### Hint 5: Backtracking pattern

Inside your loop:

```cpp
if (isPalindrome(s, start, end)) {

    current.push_back(s.substr(start, end - start + 1));

    // recurse from end + 1

    current.pop_back();
}
```

That `pop_back()` lets you try a different cut afterward.

### Skeleton

```cpp
void backtrack(string& s,
               int start,
               vector<string>& current,
               vector<vector<string>>& result) {

    if (start == s.size()) {
        // save current partition
        return;
    }

    for (int end = start; end < s.size(); end++) {

        if (!isPalindrome(s, start, end)) {
            continue;
        }

        // choose substring s[start...end]

        // recurse from end + 1

        // undo choice
    }
}
```

The main idea is:

```text
Choose a palindrome substring
→ recurse on the rest of the string
→ undo
→ try a longer substring
```

So unlike Subsets, where you decide whether to include an element, here you're deciding **where the next partition boundary should be**.
*/
class Solution {
private:
    bool isPalindrome(string& s, int left, int right){
        while(left < right){
            if(s[left] != s[right]){
                return false;
            }

            left++;
            right--;
        }

        return true;
    }

    void backtrack(string& s, int start, vector<string>& current, vector<vector<string>>& result){
        if(start == s.size()){
            result.push_back(current);
            return;
        }

        for(int end = start; end < s.size(); end++){
            if(!isPalindrome(s, start, end)){
                continue;
            }

            current.push_back(s.substr(start, end - start + 1));

            backtrack(s, end + 1, current, result);

            current.pop_back();
        }
    }

public:
    vector<vector<string>> partition(string s) {
        vector<string> current;
        vector<vector<string>> result;

        backtrack(s, 0, current, result);

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna