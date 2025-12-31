// Method 1: Without Sorting using HashMap
/*
### WHAT I WAS DOING WRONG

You only verify: **every character in `s` has the same count in `t`**.
But you never verify the reverse: **that `t` doesn’t contain additional characters**.

Example that breaks it:

* `s = "a"`
* `t = "ab"`

Your code:

* For `'a'`, counts match → passes loop
* returns `true` ❌ (but they’re not anagrams)

### Another issue

You also never check lengths. Different lengths can’t be anagrams, and skipping that causes the same bug pattern.

### Minor Java detail

`mapT.get(c) != valS` compares `Integer` objects via unboxing here, so it behaves like value compare, but it’s better style to compare primitives or use `.equals`.

---

## Minimal fix ideas

1. Early length check:

```java
if (s.length() != t.length()) return false;
```

2. And/or ensure maps are identical:

* Compare `mapS.equals(mapT)`, or
* Iterate over one map’s entries and also ensure sizes match, or
* Use one frequency map and decrement with `t`.

If you want, I can show the cleanest one-map O(n) solution (or the array[26] version for lowercase letters).

*/
class Solution {
    public boolean isAnagram(String s, String t) {

        if (s.length() != t.length()) return false;

        Map<Character, Integer> mapS = new HashMap<>();
        Map<Character, Integer> mapT = new HashMap<>();

        for(char c: s.toCharArray()){
            mapS.put(c, mapS.getOrDefault(c, 0) + 1);
        }

        for(char c: t.toCharArray()){
            mapT.put(c, mapT.getOrDefault(c, 0) + 1);
        }

        for(char c: s.toCharArray()){
            int valS = mapS.get(c);

            if(!mapT.containsKey(c) || mapT.get(c) != valS){
                return false;
            }
        }

        return true;
    }
}





// Method 1.5: Similar using inbuilt function .equals() in HashMap
/*
*/
// class Solution {
//     public boolean isAnagram(String s, String t) {

//         if(s.length() != t.length()){
//             return false;
//         }

//         Map<Character, Integer> mapS = new HashMap<>();
//         Map<Character, Integer> mapT = new HashMap<>();

//         for(char c : s.toCharArray()){
//             mapS.put(c, mapS.getOrDefault(c , 0) + 1);
//         }

//         for(char c : t.toCharArray()){
//             mapT.put(c, mapT.getOrDefault(c , 0) + 1);
//         }        

//         return mapS.equals(mapT);
//     }
// }




// Method 1.5.2: More efficient
/*

*/

// class Solution {
//     public boolean isAnagram(String s, String t) {
//         if (s.length() != t.length()) return false;

//         Map<Character, Integer> freq = new HashMap<>();

//         // Count characters in s
//         for (char c : s.toCharArray()) {
//             freq.put(c, freq.getOrDefault(c, 0) + 1);
//         }

//         // Subtract characters using t
//         for (char c : t.toCharArray()) {
//             Integer count = freq.get(c);
//             if (count == null) return false;          // char not in s
//             if (count == 1) freq.remove(c);           // drop to 0
//             else freq.put(c, count - 1);
//         }

//         // If all counts balanced out, it's an anagram
//         return freq.isEmpty();
//     }
// }






// Method 2: Using Sorting
/*
*/

// class Solution {
//     public boolean isAnagram(String s, String t) {
//         if (s.length() != t.length()) return false;

//         char[] a = s.toCharArray();
//         char[] b = t.toCharArray();

//         Arrays.sort(a);
//         Arrays.sort(b);

//         return Arrays.equals(a, b);
//     }
// }
