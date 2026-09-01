/*
// Definition for a Node.
class Node {
public:
    int val;
    vector<Node*> neighbors;
    Node() {
        val = 0;
        neighbors = vector<Node*>();
    }
    Node(int _val) {
        val = _val;
        neighbors = vector<Node*>();
    }
    Node(int _val, vector<Node*> _neighbors) {
        val = _val;
        neighbors = _neighbors;
    }
};
*/


// Method 1: DFS + HashMap
/*
This is a **graph DFS/BFS + hashmap** problem. The tricky part is that graphs can contain cycles, so you need to remember which nodes you've already cloned.

### Hint 1: You need a mapping from original → clone

Use:

```cpp
unordered_map<Node*, Node*> clones;
```

This means:

```text
original node → cloned node
```

For example:

```text
Node 1 (original) → Node 1 (copy)
Node 2 (original) → Node 2 (copy)
```

This map serves two purposes:

* prevents infinite recursion from cycles
* lets you reuse an already-created clone

### Hint 2: Let DFS return the cloned node

A useful recursive function is:

```cpp
Node* dfs(Node* node)
```

Meaning:

> Give me the cloned version of this original node.

Base case:

```cpp
if (node == nullptr) {
    return nullptr;
}
```

### Hint 3: Before creating a clone, check the hashmap

This is the most important part:

```cpp
if (clones.count(node)) {
    return clones[node];
}
```

Why?

Imagine:

```text
1 -- 2
|    |
4 -- 3
```

Eventually your DFS can travel:

```text
1 → 2 → 3 → 4 → 1
```

Without the hashmap, you'd keep cloning forever.

### Hint 4: Create the clone before exploring neighbors

Once you know it hasn't been cloned:

```cpp
Node* copy = new Node(node->val);
```

Immediately store it:

```cpp
clones[node] = copy;
```

**Do this before recursively exploring neighbors.**

That way, if a cycle leads back to this node, the recursive call finds it in `clones`.

### Hint 5: Clone every neighbor

The original node has:

```cpp
node->neighbors
```

For each neighbor:

```cpp
for (Node* neighbor : node->neighbors) {
    // recursively clone neighbor
    // add cloned neighbor to copy->neighbors
}
```

Think about:

```cpp
copy->neighbors.push_back(
    dfs(neighbor)
);
```

The recursive call returns the correct cloned node whether it is new or was already cloned.

### Hint 6: Skeleton

```cpp
class Solution {
private:
    unordered_map<Node*, Node*> clones;

    Node* dfs(Node* node) {
        if (node == nullptr) {
            return nullptr;
        }

        if (clones.count(node)) {
            return clones[node];
        }

        // Create clone

        // Store original → clone BEFORE recursion

        for (Node* neighbor : node->neighbors) {
            // clone neighbor
            // add to clone's neighbors
        }

        // return clone
    }

public:
    Node* cloneGraph(Node* node) {
        return dfs(node);
    }
};
```

The core pattern to remember is:

```text
Have I cloned this node?
    yes → return existing clone

    no  → create clone
          save it in hashmap
          recursively clone neighbors
          return clone
```

It's very similar to DFS on an island, except instead of a `visited` set, your hashmap is both **visited tracking and the actual cloned-node lookup**.
*/
class Solution {
private:
    unordered_map<Node*, Node*> clones;

    Node* dfs(Node* node){
        if(node == nullptr){
            return nullptr;
        }

        if(clones.count(node)){
            return clones[node];
        }

        Node* copy = new Node(node->val);
        clones[node] = copy;

        for(Node* neighbor : node->neighbors){
            copy->neighbors.push_back(dfs(neighbor));
        }

        return copy;
    }

public:
    Node* cloneGraph(Node* node) {
        return dfs(node);        
    }
};

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna