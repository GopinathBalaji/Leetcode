// Method 1: Two Pointer my approach (Using extra space)
/*
*/
class Solution {
    public boolean isPalindrome(String s) {
        StringBuilder sb = new StringBuilder();
        
        for(char c: s.toCharArray()){
            if(Character.isLetterOrDigit(c)){
                if(Character.isLetter(c) && Character.isUpperCase(c)){
                    c = Character.toLowerCase(c);
                }
                sb.append(c);
            }
        }

        int start = 0;
        int end = sb.length() - 1;

        while(start < end){
            if(sb.charAt(start) != sb.charAt(end)){
                return false;
            }
            start++;
            end--;
        }

        return true;
    }
}





// Method 1.5: Same as above more efficient
/*
*/
// class Solution {
//     public boolean isPalindrome(String s) {
//         StringBuilder sb = new StringBuilder();

//         for (char c : s.toCharArray()) {
//             if (Character.isLetterOrDigit(c)) {
//                 sb.append(Character.toLowerCase(c));
//             }
//         }

//         int start = 0, end = sb.length() - 1;
//         while (start < end) {
//             if (sb.charAt(start) != sb.charAt(end)) return false;
//             start++;
//             end--;
//         }
//         return true;
//     }
// }






// Method 2: No extra space two-pointer approach
/*
## Detailed explanation (O(1) extra space, two pointers)

### What the problem asks

A string is a valid palindrome if, after:

* removing all **non-alphanumeric** characters
* treating uppercase/lowercase as the same

…it reads the same forwards and backwards.

### Key idea

Use two pointers:

* `left` starts at the beginning
* `right` starts at the end

Move them inward while:

1. skipping characters that don’t matter (not letters/digits)
2. comparing the next valid characters case-insensitively

This avoids building a cleaned string (so **O(1)** extra space).

### Why skipping works

If a character is not a letter or digit, it’s irrelevant to the palindrome check. So we just move past it.

### Why lowercase conversion works

We compare `Character.toLowerCase(...)` so `'A'` matches `'a'`, etc.

---

## Thorough example walkthrough

Example:
`s = "A man, a plan, a canal: Panama"`

Indices (roughly):

* left starts at `'A'`
* right starts at `'a'` (the last char)

### Step-by-step pointer moves (key steps)

**Start:** `left=0 ('A')`, `right=... ('a')`

* both alphanumeric
* compare lower: `'a'` vs `'a'` ✅
* move inward: `left++`, `right--`

**Next:** left points to `' '` (space)

* not alphanumeric → skip left until `'m'`
  Left lands on `'m'`

Right might point to `'m'` (or may skip punctuation/spaces similarly)

* compare `'m'` vs `'m'` ✅
* move inward

Then you repeatedly do:

* skip commas/spaces/colon
* compare letters/digits

You’ll compare these pairs in order (conceptually):

* `a` ↔ `a`
* `m` ↔ `m`
* `a` ↔ `a`
* `n` ↔ `n`
* `p` ↔ `p`
* `l` ↔ `l`
* `a` ↔ `a`
* `n` ↔ `n`
* `a` ↔ `a`
* `c` ↔ `c`
* `a` ↔ `a`
* `n` ↔ `n`
* `a` ↔ `a`
* `l` ↔ `l`
* `p` ↔ `p`
* `a` ↔ `a`
* `n` ↔ `n`
* `a` ↔ `a`
* `m` ↔ `m`
* `a` ↔ `a`

Pointers cross → return **true**.

### Quick failing example

`s = "race a car"`

Cleaned is `"raceacar"` which is not palindrome.

Two-pointer view:

* compare `r` ↔ `r` ✅
* compare `a` ↔ `a` ✅
* compare `c` ↔ `c` ✅
* compare `e` ↔ `a` ❌ → return false

---

## Complexity

* **Time:** O(n) (each pointer only moves forward/backward)
* **Space:** O(1)
*/
// class Solution {
//     public boolean isPalindrome(String s) {
//         int left = 0, right = s.length() - 1;

//         while (left < right) {
//             // Move left until it's alphanumeric (or crosses right)
//             while (left < right && !Character.isLetterOrDigit(s.charAt(left))) {
//                 left++;
//             }

//             // Move right until it's alphanumeric (or crosses left)
//             while (left < right && !Character.isLetterOrDigit(s.charAt(right))) {
//                 right--;
//             }

//             // Compare lowercase versions
//             char cl = Character.toLowerCase(s.charAt(left));
//             char cr = Character.toLowerCase(s.charAt(right));
//             if (cl != cr) return false;

//             left++;
//             right--;
//         }

//         return true;
//     }
// }
