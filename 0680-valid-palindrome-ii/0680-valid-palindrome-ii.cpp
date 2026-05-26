// Method 1: Two-pointer approach
/*
## Hint 1: Start like a normal palindrome check

Use two pointers:

```cpp
left = 0;
right = s.size() - 1;
```

Then compare:

```cpp
s[left] == s[right]
```

If they match, move inward:

```cpp
left++;
right--;
```

---

## Hint 2: The only problem is one mismatch

Example:

```cpp
s = "abca"
```

Start:

```cpp
left = 0  -> 'a'
right = 3 -> 'a'
```

They match.

Move inward:

```cpp
left = 1  -> 'b'
right = 2 -> 'c'
```

Now they do not match.

Since you are allowed to delete **at most one character**, you have two choices:

```cpp
delete s[left]
```

or

```cpp
delete s[right]
```

---

## Hint 3: After mismatch, check both possibilities

When you find:

```cpp
s[left] != s[right]
```

You should check:

```cpp
isPalindrome(left + 1, right)
```

or:

```cpp
isPalindrome(left, right - 1)
```

If either one is true, then the answer is true.


---

## Example

```cpp
s = "abca"
```

Mismatch happens at:

```cpp
'b' and 'c'
```

Try deleting `'b'`:

```cpp
"aca"
```

This is a palindrome.

So return:

```cpp
true
```

---

## Core idea

```cpp
Use two pointers.
When the first mismatch happens, try skipping either the left character or the right character.
After that, no more deletions are allowed.
```

Time complexity:

```cpp
O(n)
```

Space complexity:

```cpp
O(1)
```
*/
class Solution {
public:
    bool validPalindrome(string s) {
        int left = 0;
        int right = s.size() - 1;

        while(left < right){
            if(s[left] == s[right]){
                left++;
                right--;
            }else{
                return isPalindrome(s, left+1, right) || isPalindrome(s, left, right-1);
            }
        }

        return true;
    }

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
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/leethub-v4/bcilpkkbokcopmabingnndookdogmbna