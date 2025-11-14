// Method 1: My Greedy approach with two pointer
/*
### Hint 1 — What info do you need to cut safely?

For each character, note the **last index** where it appears in the string.
(If a char shows up later, your current partition can’t end before that point.)

---

### Hint 2 — Sweep with a moving “deadline”

Walk left → right, keeping:

* `start` = where the current partition began,
* `end` = the **furthest last-index** of any character you’ve seen in this partition so far.

At each `i`, update:

```
end = max(end, lastIndex[s[i]])
```

---

### Hint 3 — When to cut

Whenever `i == end`, you’ve reached a point where **every character seen since `start` ends by here**.
→ Record the partition length `end - start + 1`, then set `start = i + 1` to begin the next partition.

---

### Hint 4 — Why this greedy cut is optimal

You always extend the current partition just enough to include the last occurrence of every letter inside it—no more.
Cutting earlier would split a character across partitions; cutting later would be unnecessary.

---

### Hint 5 — Mini walkthrough (classic example)

`s = "ababcbacadefegdehijhklij"`

* Precompute last indices (e.g., last(a)=8, last(b)=5, last(c)=7, …)
* Sweep:

  * i=0 (‘a’): end=max(0,8)=8
  * i=1 (‘b’): end=max(8,5)=8
  * i=2 (‘a’): end=max(8,8)=8
  * …
  * i=8 ⇒ i==end → cut length 9 → partitions: `[9]`; start=9
  * Continue to i=15 ⇒ cut length 7 → `[9,7]`; start=16
  * Continue to i=23 ⇒ cut length 8 → `[9,7,8]`

---

### Hint 6 — Edge checks

* All same char: one big partition.
* All distinct: each char is its own partition.
* Implementation detail: a fixed array of size 26 (lowercase letters) for last indices is simplest.
*/
class Solution {
    public List<Integer> partitionLabels(String s) {
        List<Integer> ans = new ArrayList<>();
        HashMap<Character, Integer> map = new HashMap<>();
        
        for(int i=0; i<s.length(); i++){
            map.put(s.charAt(i), i);
        }

        int start = 0;
        int end = 0;
        for(int i=0; i<s.length(); i++){
            end = Math.max(end, map.get(s.charAt(i)));
            if(i == end){
                ans.add(end - start + 1);
                start = i + 1;
            }
        }

        return ans;
    }
}




// Method 2: Same approach (greedy, two pointer) but using arrays instead of hashmap
// class Solution {
//     public List<Integer> partitionLabels(String s) {
//         List<Integer> ans = new ArrayList<>();
//         int[] last = new int[26];
//         for (int i = 0; i < s.length(); i++) last[s.charAt(i) - 'a'] = i;

//         int start = 0, end = 0;
//         for (int i = 0; i < s.length(); i++) {
//             end = Math.max(end, last[s.charAt(i) - 'a']);
//             if (i == end) {
//                 ans.add(end - start + 1);
//                 start = i + 1;
//             }
//         }
//         return ans;
//     }
// }
