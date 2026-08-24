// Method 1: Trie using Hashmap approach
/*
Using a **hashmap-based Trie** is a good fit here, especially because `'.'` can match any character.

### Hint 1: Trie node with a hashmap

Instead of a fixed array of 26 children, use:

```cpp
struct TrieNode {
    unordered_map<char, TrieNode*> children;
    bool isEnd = false;
};
```

This means each node only stores characters that actually exist.

---

### Hint 2: `addWord` is just normal Trie insertion

Start from `root`.

For each character:

```cpp
if (!current->children.count(c)) {
    current->children[c] = new TrieNode();
}

current = current->children[c];
```

After the whole word:

```cpp
current->isEnd = true;
```

So far, almost identical to LeetCode 208.

---

### Hint 3: `search` is where the problem changes

If the current character is a normal letter like:

```text
'a'
```

you only have one possible path:

```cpp
if (!node->children.count(word[index])) {
    return false;
}
```

Then recurse into that child.

But if the current character is:

```text
'.'
```

you don't know which child to take.

So you must try **every child**.

---

### Hint 4: Make `search` recursive

A helpful recursive state is:

```cpp
dfs(word, index, node)
```

Meaning:

> Can I match `word[index...]` starting from this Trie node?

Skeleton:

```cpp
bool dfs(string& word, int index, TrieNode* node) {

    if (index == word.size()) {
        // what should we check here?
    }

    char c = word[index];

    if (c == '.') {
        // try every child
    } else {
        // follow only child c
    }
}
```

---

### Hint 5: Handling `'.'`

With an `unordered_map`, you can iterate over all children:

```cpp
for (auto& [ch, child] : node->children) {
    // try recursively matching from child
}
```

If **any** recursive call succeeds:

```cpp
return true;
```

If none succeeds:

```cpp
return false;
```

---

### Hint 6: Base case

When:

```cpp
index == word.size()
```

you have consumed every search character.

But don't automatically return `true`.

You need to make sure you ended at a complete word:

```cpp
return node->isEnd;
```

For example, if you added:

```text
"apple"
```

then:

```text
search("app")
```

should still be `false`.

---

### Skeleton

```cpp
class WordDictionary {
private:
    struct TrieNode {
        unordered_map<char, TrieNode*> children;
        bool isEnd = false;
    };

    TrieNode* root;

    bool dfs(string& word, int index, TrieNode* node) {

        if (index == word.size()) {
            return node->isEnd;
        }

        char c = word[index];

        if (c == '.') {

            // Try every child.
            // If any recursive call returns true,
            // return true.

        } else {

            // If this character doesn't exist,
            // return false.

            // Otherwise recurse into that child.
        }

        return false;
    }

public:
    WordDictionary() {
        root = new TrieNode();
    }

    void addWord(string word) {
        // normal Trie insertion
    }

    bool search(string word) {
        return dfs(word, 0, root);
    }
};
```

The main idea is:

```text
normal character:
    follow exactly one hashmap entry

'.':
    branch into every hashmap entry
```

So LeetCode 211 is essentially **LeetCode 208 + backtracking when you encounter `'.'`**.
*/
class WordDictionary {
private:
    struct TrieNode {
        unordered_map<char, TrieNode*> children;
        bool isEnd;

        TrieNode(): children{}, isEnd(false) {

        }
    };

    TrieNode* root;

    bool dfs(string& word, int index, TrieNode* node){
        if(index == word.size()){
            return node->isEnd;
        }

        char c = word[index];

        if(c == '.'){
            for(auto& [ch, child] : node->children){
                if(dfs(word, index + 1, child)){
                    return true;
                }
            }

            return false;

        }else{
            if(!node->children.count(word[index])){
                return false;
            }

            return dfs(word, index + 1, node->children[word[index]]);
        }

        return false;
    }

public:
    WordDictionary() {
        root = new TrieNode();
    }
    
    void addWord(string word) {
        TrieNode* current = root;

        for(char c: word){
            if(!current->children.count(c)){
                current->children[c] = new TrieNode();
            }

            current = current->children[c];
        }

        current->isEnd = true;
    }
    
    bool search(string word) {
        return dfs(word, 0, root);
    }
};

/**
 * Your WordDictionary object will be instantiated and called as such:
 * WordDictionary* obj = new WordDictionary();
 * obj->addWord(word);
 * bool param_2 = obj->search(word);
 */

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna