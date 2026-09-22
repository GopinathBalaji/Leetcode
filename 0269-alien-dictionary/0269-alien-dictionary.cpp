// Method 1: Kahn's topological sort
/*
This is a **topological sort** problem.

The key idea is:

> Compare adjacent words to infer ordering rules between characters, then topologically sort those characters.

### Hint 1: Build a graph of characters

Suppose:

```text
["wrt", "wrf"]
```

Compare the two words from left to right.

The first differing characters are:

```text
t vs f
```

Since `"wrt"` comes before `"wrf"`, you learn:

```text
t -> f
```

meaning `t` must come before `f` in the alien alphabet.

### Hint 2: Only the first difference matters

For:

```text
"abc"
"ade"
```

the first difference is:

```text
b vs d
```

so add:

```text
b -> d
```

Do **not** keep comparing `c` and `e`.

Once lexicographical order is determined by the first mismatch, later characters tell you nothing.

### Hint 3: Watch the invalid prefix case

This is the biggest trap.

Consider:

```text
["abc", "ab"]
```

This can never be a valid dictionary ordering because a longer word appears before its exact prefix.

So if all shared characters match and:

```cpp
word1.size() > word2.size()
```

then return:

```cpp
""
```

### Hint 4: Include every character

Even a character with no edges must appear in the final answer.

For example:

```text
["z"]
```

should return:

```text
"z"
```

So initialize your graph / indegree structure using **every character in every word**, not just characters that appear in relationships.

### Hint 5: Use Kahn's topological sort

This is similar to Course Schedule II.

Track:

```cpp
unordered_map<char, vector<char>> graph;
unordered_map<char, int> indegree;
```

For every inferred edge:

```text
a -> b
```

do:

```cpp
graph[a].push_back(b);
indegree[b]++;
```

Then put all characters with:

```cpp
indegree[c] == 0
```

into a queue.

### Hint 6: Avoid duplicate edges

This is subtle.

Suppose different word comparisons infer:

```text
a -> b
```

more than once.

If you blindly do:

```cpp
indegree[b]++;
```

multiple times, your indegree becomes incorrect.

Using something like:

```cpp
unordered_map<char, unordered_set<char>> graph;
```

makes duplicate edges easier to avoid.

Then:

```cpp
if (!graph[a].count(b)) {
    graph[a].insert(b);
    indegree[b]++;
}
```

### Hint 7: Perform the topological sort

Same pattern as Course Schedule II:

```cpp
while (!q.empty()) {
    char c = q.front();
    q.pop();

    result += c;

    for (char next : graph[c]) {
        indegree[next]--;

        if (indegree[next] == 0) {
            q.push(next);
        }
    }
}
```

### Hint 8: How do you detect a cycle?

After topological sort:

```cpp
result.size()
```

should equal the total number of unique characters.

If not, there's a cycle such as:

```text
a -> b
b -> a
```

and no valid alphabet exists.

Return:

```cpp
""
```

### Overall skeleton

```cpp
string foreignDictionary(vector<string>& words) {

    // 1. Add every character to graph / indegree

    // 2. Compare adjacent words
    //    - detect invalid prefix
    //    - find first differing character
    //    - add directed edge

    // 3. Add all indegree-0 chars to queue

    // 4. Topological sort

    // 5. If result doesn't contain every character:
    //        cycle -> ""

    return result;
}
```

The mental model is:

```text
adjacent words
→ infer character ordering rules
→ build directed graph
→ topological sort
```

So this is essentially **Course Schedule II, but the nodes are characters and the edges come from comparing neighboring words**.
*/
class Solution {
public:
    string foreignDictionary(vector<string>& words) {
        int n = words.size();

        unordered_map<char, unordered_set<char>> graph;
        unordered_map<char, int> indegree;

        for(string& word : words){
            for(char c : word){
                indegree[c] = 0;
            }
        }

        for(int i=0; i<n-1; ++i){
            string word1 = words[i];
            string word2 = words[i+1];

            int len = std::min(word1.size(), word2.size());
            int j = 0;

            while(j < len && word1[j] == word2[j]){
                j++;
            }

            if(j == len){
                if(word1.size() > word2.size()){
                    return "";
                }

                continue;
            }

            char from = word1[j];
            char to = word2[j];

            if(!graph[from].count(to)){
                graph[from].insert(to);
                indegree[to]++;
            }
        }

        queue<char> q;

        for(const auto& [node, degree] : indegree){
            if(degree == 0){
                q.push(node);
            }
        }

        string result;

        while(!q.empty()){
            char c = q.front();
            q.pop();

            result += c;

            for(char next : graph[c]){
                indegree[next]--;

                if(indegree[next] == 0){
                    q.push(next);
                }
            }
        }

        if(result.size() != indegree.size()){
            return "";
        }

        return result;
    }
};
