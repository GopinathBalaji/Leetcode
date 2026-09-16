// Method 1: Shortest-path BFS
/*
This is a **shortest-path BFS** problem.

Think of every valid word as a node in a graph. Two words are connected if they differ by exactly one character.

### Hint 1: BFS is the right tool

You want the **shortest transformation sequence**, so BFS fits naturally.

Start with:

```cpp
queue<string> q;
q.push(beginWord);
```

Also track the current number of steps / words in the path.

### Hint 2: Put the dictionary in a hash set

Use:

```cpp
unordered_set<string> dict(wordList.begin(), wordList.end());
```

That gives fast checks for whether a generated word is valid.

One important early check:

```cpp
if (!dict.count(endWord)) {
    return 0;
}
```

### Hint 3: Generate neighbors by changing one character

For each position in the current word:

```cpp
for (int i = 0; i < word.size(); i++) {
```

try replacing it with every letter:

```cpp
for (char c = 'a'; c <= 'z'; c++) {
```

For example:

```text
"hit"
```

can generate candidates like:

```text
ait
bit
cit
...
hat
hbt
...
hia
hib
...
```

Only keep candidates that exist in `dict`.

### Hint 4: Remove visited words immediately

Once you enqueue a valid word:

```cpp
q.push(nextWord);
```

remove it from the dictionary:

```cpp
dict.erase(nextWord);
```

This acts as your `visited` set.

Do this **when you enqueue**, not later when you pop, so you don’t insert the same word into the queue multiple times.

### Hint 5: Process BFS by levels

Each BFS level corresponds to one transformation step.

A common pattern:

```cpp
int length = 1;

while (!q.empty()) {
    int size = q.size();

    for (int i = 0; i < size; i++) {
        string word = q.front();
        q.pop();

        // generate all one-letter transformations
    }

    length++;
}
```

If you encounter:

```cpp
word == endWord
```

return `length`.

### Hint 6: Restore the original character

When mutating each position:

```cpp
char original = word[i];

for (char c = 'a'; c <= 'z'; c++) {
    word[i] = c;

    // check candidate
}

word[i] = original;
```

Otherwise the next position starts from a modified word instead of the original one.

### Skeleton

```cpp
int ladderLength(string beginWord,
                 string endWord,
                 vector<string>& wordList) {

    unordered_set<string> dict(wordList.begin(), wordList.end());

    if (!dict.count(endWord)) {
        return 0;
    }

    queue<string> q;
    q.push(beginWord);

    dict.erase(beginWord);

    int length = 1;

    while (!q.empty()) {
        int size = q.size();

        for (int i = 0; i < size; i++) {
            string word = q.front();
            q.pop();

            if (word == endWord) {
                return length;
            }

            for (int pos = 0; pos < word.size(); pos++) {
                char original = word[pos];

                for (char c = 'a'; c <= 'z'; c++) {
                    // mutate
                    // if valid and unvisited:
                    //     enqueue
                    //     erase from dict
                }

                // restore
            }
        }

        length++;
    }

    return 0;
}
```

The main mental model is:

```text
word = graph node
one-letter change = graph edge
shortest transformation = BFS
```

The biggest implementation traps are forgetting to restore the character and forgetting to mark words visited when you enqueue them.
*/
class Solution {
public:
    int ladderLength(string beginWord, string endWord, vector<string>& wordList) {

        unordered_set<string> dict(wordList.begin(), wordList.end());
        if(!dict.count(endWord)){
            return 0;
        }
        
        queue<string> q;
        q.push(beginWord);

        dict.erase(beginWord);

        int length = 1;

        while(!q.empty()){
            int size = q.size();

            for(int i=0; i<size; i++){
                string word = q.front();
                q.pop();

                if(word == endWord){
                    return length;
                }

                for(int pos=0; pos < word.size(); pos++){
                    char original = word[pos];

                    for(char c = 'a'; c <= 'z'; c++){
                        word[pos] = c;

                        if(dict.count(word)){
                            q.push(word);
                            dict.erase(word);
                        }
                    }

                    word[pos] = original;
                }
            }

            length++;
        }

        return 0;
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna