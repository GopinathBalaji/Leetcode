// Method 1: DFS + Trie for early stopping or pruning
/*
This is essentially **LeetCode 79: Word Search + Trie**.

The big idea is: instead of searching the board separately for every word, put all words into a Trie and search the board once while following Trie paths.

### Hint 1: Build a Trie from `words`

Each Trie node needs children, and it helps to store the complete word when a word ends there:

```cpp
struct TrieNode {
    unordered_map<char, TrieNode*> children;
    string word;
};
```

Why store the whole word instead of just `isEnd`?

Because when DFS reaches that node, you can immediately add:

```cpp
result.push_back(node->word);
```

without reconstructing the path.

### Hint 2: Start DFS from every board cell

Like Word Search:

```cpp
for (int r = 0; r < rows; r++) {
    for (int c = 0; c < cols; c++) {
        dfs(board, r, c, root, result);
    }
}
```

But now DFS should stop immediately if the current board character is not a child of the current Trie node.

Conceptually:

```cpp
char c = board[row][col];

if (!node->children.count(c)) {
    return;
}

node = node->children[c];
```

This is the main optimization.

If your current path spells:

```text
"zx"
```

and no dictionary word starts with `"zx"`, stop exploring immediately.

### Hint 3: Detect when you found a word

After moving into the Trie child:

```cpp
if (!node->word.empty()) {
    result.push_back(node->word);
}
```

But think about duplicates.

The same word might be found through multiple board paths.

A neat trick is:

```cpp
result.push_back(node->word);
node->word = "";
```

After finding it once, clear it so you don't add it again.

### Hint 4: Mark cells as visited

Same as LeetCode 79:

```cpp
char original = board[row][col];
board[row][col] = '#';

// explore neighbors

board[row][col] = original;
```

You can't reuse the same board cell within one word path.

### Hint 5: Explore four directions

From `(row, col)`:

```cpp
dfs(row + 1, col, ...)
dfs(row - 1, col, ...)
dfs(row, col + 1, ...)
dfs(row, col - 1, ...)
```

Notice that you're passing the **next Trie node** into those calls.

### Hint 6: DFS skeleton

```cpp
void dfs(vector<vector<char>>& board,
         int row,
         int col,
         TrieNode* node,
         vector<string>& result) {

    // bounds check

    char c = board[row][col];

    // visited or no matching Trie child?
    // return

    node = node->children[c];

    // did we finish a dictionary word?

    // mark board cell visited

    // search four directions

    // restore board cell
}
```

### Hint 7: Why Trie matters

A naive approach would do:

```text
for every word:
    run Word Search
```

That repeats a huge amount of work.

With the Trie:

```text
board path = "oat..."
```

If no word begins with `"oat"`, you stop immediately.

So the Trie gives you **prefix pruning**.

The mental model is:

```text
board DFS
+
Trie prefix matching
+
backtracking for visited cells
```

If you already understand 79 and 208, this problem is basically those two combined.
*/
class Solution {
private:
    struct TrieNode{
        unordered_map<char, TrieNode*> children;
        string word;
    };


    TrieNode* buildTrie(vector<string>& words){
        TrieNode* root = new TrieNode();

        for(string& word : words){
            TrieNode* current = root;

            for(char c: word){
                if(!current->children.count(c)){
                    current->children[c] = new TrieNode();
                }

                current = current->children[c];
            }

            current->word = word;
        }

        return root;
    }


    void dfs(vector<vector<char>>& board, int row, int col, TrieNode* node, vector<string>& result){
        if(row < 0 || row >= board.size() || col < 0 || col >= board[0].size()){
            return;
        }

        char c = board[row][col];

        if(!node->children.count(c)){
            return;
        }

        node = node->children[c];

        if(!node->word.empty()){
            result.push_back(node->word);
            node->word = "";
        }

        char original = board[row][col];
        board[row][col] = '#';

        dfs(board, row + 1, col, node, result);
        dfs(board, row - 1, col, node, result);
        dfs(board, row, col + 1, node, result);
        dfs(board, row, col - 1, node, result);

        board[row][col] = original;
    }


public:
    vector<string> findWords(vector<vector<char>>& board, vector<string>& words) {
        int rows = board.size();
        int cols = board[0].size();
        
        vector<string> result;

        TrieNode* root = buildTrie(words);

        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){
                dfs(board, r, c, root, result);
            }
        }

        return result;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna