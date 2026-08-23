// Method 1 
/*
Think of a Trie as a tree where each edge represents a character.

For lowercase English letters, each node can have up to 26 children.

### Hint 1: Design a Trie node

Each node needs to know:

```cpp
children
isEnd
```

`children` tells you where the next letters go.

`isEnd` tells you whether a complete word ends at that node.

A possible structure:

```cpp
struct TrieNode {
    TrieNode* children[26];
    bool isEnd;
};
```

You’ll also need to initialize all child pointers to `nullptr`.

### Hint 2: Why is `isEnd` necessary?

Suppose you've inserted:

```text
apple
```

Then the path:

```text
a → p → p → l → e
```

exists.

But `"app"` should **not** count as a word unless it was separately inserted.

So:

```text
search("app") → false
startsWith("app") → true
```

That distinction is exactly why you need `isEnd`.

### Hint 3: Convert a character into an array index

Since all letters are lowercase:

```cpp
int index = c - 'a';
```

So:

```text
'a' → 0
'b' → 1
...
'z' → 25
```

### Hint 4: `insert`

Start at the root.

For every character:

```cpp
int index = c - 'a';

if (current->children[index] == nullptr) {
    // create a new node
}

current = current->children[index];
```

After processing the entire word:

```cpp
current->isEnd = true;
```

### Hint 5: `search`

Again, start at the root and follow each character.

If at any point:

```cpp
current->children[index] == nullptr
```

then the word does not exist.

But after reaching the final character, don't immediately return `true`.

Ask:

```cpp
return current->isEnd;
```

because the path might only be a prefix of a longer word.

### Hint 6: `startsWith`

This is almost identical to `search`.

The difference is at the end.

For `startsWith`, if you successfully follow the entire prefix, that's enough:

```cpp
return true;
```

You do **not** care about `isEnd`.

### Skeleton

```cpp
class Trie {
private:
    struct TrieNode {
        TrieNode* children[26];
        bool isEnd;

        TrieNode() {
            // initialize children
            // initialize isEnd
        }
    };

    TrieNode* root;

public:
    Trie() {
        root = new TrieNode();
    }

    void insert(string word) {
        TrieNode* current = root;

        // walk through characters
        // create missing nodes
        // mark final node as end
    }

    bool search(string word) {
        TrieNode* current = root;

        // follow characters
        // if missing → false

        // return whether this is a complete word
    }

    bool startsWith(string prefix) {
        TrieNode* current = root;

        // follow characters
        // if missing → false

        // if whole prefix exists → true
    }
};
```

The key distinction to remember is:

```text
search("app")      → path must exist AND isEnd must be true
startsWith("app")  → path only needs to exist
```

Once that clicks, the problem is mostly just pointer traversal.
*/
class Trie {
private:
    struct TrieNode {
        TrieNode* children[26];
        bool isEnd;

        TrieNode() : children{}, isEnd(false) {
        }
    };

    TrieNode* root;

public:
    Trie() {
        root = new TrieNode();
    }

    void insert(string word) {
        TrieNode* current = root;

        for (char c : word) {
            int index = c - 'a';

            if (current->children[index] == nullptr) {
                current->children[index] = new TrieNode();
            }

            current = current->children[index];
        }

        current->isEnd = true;
    }

    bool search(string word) {
        TrieNode* current = root;

        for (char c : word) {
            int index = c - 'a';

            if (current->children[index] == nullptr) {
                return false;
            }

            current = current->children[index];
        }

        return current->isEnd;
    }

    bool startsWith(string prefix) {
        TrieNode* current = root;

        for (char c : prefix) {
            int index = c - 'a';

            if (current->children[index] == nullptr) {
                return false;
            }

            current = current->children[index];
        }

        return true;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna