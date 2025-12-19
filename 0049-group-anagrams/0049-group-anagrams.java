// Method 1: Sorting characters within each string
/*
### Idea

Two strings are anagrams if their letters sorted are identical.

* `"eat" → "aet"`
* `"tea" → "aet"`
* `"tan" → "ant"`

Use the sorted string as the hashmap key.

### Complexity

* For each string of length `k`, sorting costs `O(k log k)`
* Total: `O(N * k log k)` (where `k` is average length)

### Example walkthrough

Input:

```text
["eat","tea","tan","ate","nat","bat"]
```

Process each:

* `"eat"` → sort `"aet"` → map["aet"] = ["eat"]
* `"tea"` → sort `"aet"` → map["aet"] = ["eat","tea"]
* `"tan"` → sort `"ant"` → map["ant"] = ["tan"]
* `"ate"` → sort `"aet"` → map["aet"] = ["eat","tea","ate"]
* `"nat"` → sort `"ant"` → map["ant"] = ["tan","nat"]
* `"bat"` → sort `"abt"` → map["abt"] = ["bat"]

Return values:
`[["eat","tea","ate"], ["tan","nat"], ["bat"]]` (order may vary)
*/
class Solution {
    public List<List<String>> groupAnagrams(String[] strs) {
        
        List<List<String>> ans = new ArrayList<>();
        
        HashMap<String, List<String>> map = new HashMap<>();
        for(String str: strs){
            char[] charArray = str.toCharArray();
            Arrays.sort(charArray);
            String sorted = new String(charArray);

            map.computeIfAbsent(sorted, k -> new ArrayList<>()).add(str);
        }

        for(Map.Entry<String, List<String>> entry : map.entrySet()){
            ans.add(entry.getValue());
        }

        return ans;
    }
}





// Method 2: Frequency-count key (O(k) per string)
/*

### Idea

Two strings are anagrams if they have the same **counts of each letter**.

For lowercase English letters, build a 26-length count array.

Example:

* `"eat"` → a:1, e:1, t:1 (others 0)
* `"tea"` → same counts → same key

We turn the 26 counts into a unique key string like:
`"1#0#0#0#1#...#1#..."`

(Using separators is important to avoid ambiguity.)

### Why it’s often faster

Instead of sorting `O(k log k)`, counting is `O(k)`.
So total: `O(N * k)` — usually faster for longer strings.

### Example walkthrough (same input)

Input:

```text
["eat","tea","tan","ate","nat","bat"]
```

Compute keys (showing only relevant letters):

* `"eat"` counts: a1 e1 t1 → key like `"1#0#0#0#1#...#1#..."` → group1
* `"tea"` counts: a1 e1 t1 → same key → group1
* `"tan"` counts: a1 n1 t1 → different key → group2
* `"ate"` counts: a1 e1 t1 → group1
* `"nat"` counts: a1 n1 t1 → group2
* `"bat"` counts: a1 b1 t1 → group3

Same grouping, but key building avoids sorting.
*/

// class Solution {
//     public List<List<String>> groupAnagrams(String[] strs) {
//         Map<String, List<String>> map = new HashMap<>();

//         for (String s : strs) {
//             int[] cnt = new int[26];
//             for (int i = 0; i < s.length(); i++) {
//                 cnt[s.charAt(i) - 'a']++;
//             }

//             // Build a unique key
//             StringBuilder sb = new StringBuilder();
//             for (int c : cnt) {
//                 sb.append(c).append('#');
//             }
//             String key = sb.toString();

//             map.computeIfAbsent(key, k -> new ArrayList<>()).add(s);
//         }

//         return new ArrayList<>(map.values());
//     }
// }
