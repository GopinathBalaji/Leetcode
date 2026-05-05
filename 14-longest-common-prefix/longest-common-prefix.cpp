// Method 1: Find shortest and check each of it character with other the same position character in other strings
/*
*/
class Solution {
public:
    string longestCommonPrefix(vector<string>& strs) {
        string s;
        string ans;
        int minLen = INT_MAX;
        for(string str : strs){
            if(str.size() == 0){
                return "";
            }

            if(str.size() < minLen){
                s = str;
                minLen = str.size();
            }
        }

        for(int i=0; i<s.size(); i++){
            char c = s[i];

            for(string str : strs){
                if(str[i] != c){
                    return ans;
                }
            }

            ans += c;
        }

        return ans;
    }
};




// Method 2: Trie Approach
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
// private:
//     struct TrieNode {
//         TrieNode* next[26];
//         int childCount;
//         bool isEnd;

//         TrieNode() {
//             for (int i = 0; i < 26; i++) {
//                 next[i] = nullptr;
//             }

//             childCount = 0;
//             isEnd = false;
//         }
//     };

//     class Trie {
//     private:
//         TrieNode* root;

//     public:
//         Trie() {
//             root = new TrieNode();
//         }

//         void insert(const string& s) {
//             TrieNode* curr = root;

//             for (char c : s) {
//                 int idx = c - 'a';

//                 if (curr->next[idx] == nullptr) {
//                     curr->next[idx] = new TrieNode();
//                     curr->childCount++;
//                 }

//                 curr = curr->next[idx];
//             }

//             curr->isEnd = true;
//         }

//         string lcp() {
//             string result;
//             TrieNode* curr = root;

//             while (!curr->isEnd && curr->childCount == 1) {
//                 int nextIdx = -1;

//                 for (int i = 0; i < 26; i++) {
//                     if (curr->next[i] != nullptr) {
//                         nextIdx = i;
//                         break;
//                     }
//                 }

//                 result.push_back('a' + nextIdx);
//                 curr = curr->next[nextIdx];
//             }

//             return result;
//         }

//         ~Trie() {
//             deleteTrie(root);
//         }

//     private:
//         void deleteTrie(TrieNode* node) {
//             if (node == nullptr) {
//                 return;
//             }

//             for (int i = 0; i < 26; i++) {
//                 deleteTrie(node->next[i]);
//             }

//             delete node;
//         }
//     };

// public:
//     string longestCommonPrefix(vector<string>& strs) {
//         if (strs.empty()) {
//             return "";
//         }

//         for (const string& s : strs) {
//             if (s.empty()) {
//                 return "";
//             }
//         }

//         Trie trie;

//         for (const string& s : strs) {
//             trie.insert(s);
//         }

//         return trie.lcp();
//     }
// };