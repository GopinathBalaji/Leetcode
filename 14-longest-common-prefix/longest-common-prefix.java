// Method 1: Find shortest and check each of it character with other the same position
// character in other strings
class Solution {
    public String longestCommonPrefix(String[] strs) {
        if (strs == null || strs.length == 0) return "";

        // find the shortest string
        String shortest = strs[0];
        for (String s : strs) {
            if (s.length() < shortest.length()) shortest = s;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < shortest.length(); i++) {
            char c = shortest.charAt(i);
            for (String s : strs) {
                if (s.charAt(i) != c) {
                    return sb.toString();
                }
            }
            sb.append(c);
        }
        return sb.toString();
    }
}


// Method 2: Trie
/*
Using a trie (prefix tree) for Longest Common Prefix (LCP) is very natural:

    Insert all strings into the trie.
    Then walk from the root while the current node has exactly one child and no word ends at this node.
    The characters you traverse form the LCP.
    Stop as soon as you hit a branch (≥2 children) or a terminal node (some string ends here).

Why that works: a path is common to all strings iff every node on it has one outgoing edge and no string stopped earlier; the first branch/terminal is where some strings diverge/stop.


Complexity:

Build trie: O(total characters) across all strings.
LCP walk: O(length of LCP).
Space: O(total characters) for nodes.



Walkthrough on ["flower","flow","flight"]
Insert all three:
root
 └─ f
     └─ l
         ├─ o
         │   └─ w
         │       └─ e
         │           └─ r (end)
         └─ i
             └─ g
                 └─ h
                     └─ t (end)

Traverse for LCP:
At root: one child f (not end) → append f.
At f: one child l (not end) → append l.
At l: two children o and i → stop.
LCP = "fl".


Common pitfalls (and how this avoids them):
Stopping too late: The isEnd check ensures you stop when a shorter string ends (e.g., ["ab","a"] → "a").
Branching: childCount == 1 (or kids.size()==1) guarantees all strings share that next char.
Empty input/empty string: handled up front.
*/
// class Solution {
//     public String longestCommonPrefix(String[] strs) {
//         if (strs == null || strs.length == 0) return "";

//         // If any string is empty, LCP is empty
//         for (String s : strs) if (s.length() == 0) return "";

//         Trie trie = new Trie();
//         for (String s : strs) trie.insert(s);
//         return trie.lcp();
//     }

//     // ----- Trie implementation -----
//     static class TrieNode {
//         TrieNode[] next = new TrieNode[26];
//         int childCount = 0;    // number of non-null children
//         boolean isEnd = false; // a word ends here
//     }

//     static class Trie {
//         TrieNode root = new TrieNode();

//         void insert(String s) {
//             TrieNode cur = root;
//             for (int i = 0; i < s.length(); i++) {
//                 char c = s.charAt(i);
//                 int idx = c - 'a';                // assumes lowercase a-z
//                 if (cur.next[idx] == null) {
//                     cur.next[idx] = new TrieNode();
//                     cur.childCount++;
//                 }
//                 cur = cur.next[idx];
//             }
//             cur.isEnd = true;
//         }

//         String lcp() {
//             StringBuilder sb = new StringBuilder();
//             TrieNode cur = root;

//             while (!cur.isEnd && cur.childCount == 1) {
//                 int nextIdx = -1;
//                 for (int i = 0; i < 26; i++) {
//                     if (cur.next[i] != null) { nextIdx = i; break; }
//                 }
//                 sb.append((char) ('a' + nextIdx));
//                 cur = cur.next[nextIdx];
//             }
//             return sb.toString();
//         }
//     }
// }