/**
 * Definition for a binary tree node.
 * struct TreeNode {
 *     int val;
 *     TreeNode *left;
 *     TreeNode *right;
 *     TreeNode(int x) : val(x), left(NULL), right(NULL) {}
 * };
 */


// Method 1: Save all node including nullnodes in a perorder traversal
/*
### Hint 1

Serialization must preserve both:

* Node values
* Tree structure

Saving only preorder values is not enough, because different tree shapes can produce the same sequence.

---

### Hint 2

Use a preorder traversal:

```text
node → left → right
```

Whenever you encounter `nullptr`, store a special marker such as:

```text
#
```

Example:

```text
    1
   / \
  2   3
```

could become:

```text
1,2,#,#,3,#,#
```

---

### Hint 3

Your serialization helper can follow this structure:

```cpp
void serializeHelper(TreeNode* node, string& result) {
    if (node == nullptr) {
        // append "#,"
        return;
    }

    // append node->val and ","
    // serialize left
    // serialize right
}
```

The null markers are what let you reconstruct the exact shape.

---

### Hint 4

Deserialization should process the tokens in the same preorder order.

For each token:

* If it is `#`, return `nullptr`.
* Otherwise, create a node.
* Recursively construct its left subtree.
* Recursively construct its right subtree.

```cpp
TreeNode* deserializeHelper( tokens and current position ) {
    if ( current token is "#" ) {
        return nullptr;
    }

    TreeNode* node = new TreeNode( convert token to integer );

    node->left = deserializeHelper(...);
    node->right = deserializeHelper(...);

    return node;
}
```

---

### Hint 5

You need one shared position that advances through the tokens.

You could use:

```cpp
int index;
```

passed by reference:

```cpp
TreeNode* build(vector<string>& tokens, int& index);
```

Make sure each recursive call consumes exactly one token before moving forward.

---

### Hint 6

To split a comma-separated string in C++, `stringstream` is useful:

```cpp
stringstream ss(data);
string token;

while (getline(ss, token, ',')) {
    // store token
}
```

Convert numeric tokens using:

```cpp
stoi(token)
```

This also handles negative node values.

---

### Hint 7

During deserialization, the order matters:

```cpp
string token = tokens[index++];
```

Then decide whether the token is null or a real node. Do not increment the index separately inside every branch unless you carefully track it.

---

### Important edge case

An empty tree should serialize to something like:

```text
#
```

and deserializing it should return `nullptr`.

Both serialization and deserialization take `O(n)` time, where `n` is the number of nodes.
*/ 
class Codec {
private:
    string result = "";
    int index = 0;

    void serializeHelper(TreeNode* node, string& result){
        if(node == nullptr){
            result += "#,";
            return;
        }

        result += std::to_string(node->val) + ",";
        serializeHelper(node->left, result);
        serializeHelper(node->right, result);
    }

    TreeNode* deserializeHelper(vector<string>& tokens, int& index){
        string token = tokens[index++];

        if(token == "#"){
            return nullptr;
        }

        TreeNode* node = new TreeNode(stoi(token));

        node->left = deserializeHelper(tokens, index);
        node->right = deserializeHelper(tokens, index);

        return node;
    }


public:

    // Encodes a tree to a single string.
    string serialize(TreeNode* root) {

        serializeHelper(root, result);

        return result;
    }

    // Decodes your encoded data to tree.
    TreeNode* deserialize(string data) {
        vector<string> tokens;

        stringstream ss(data);
        string token;

        while(getline(ss, token, ',')){
            tokens.push_back(token);
        }

        return deserializeHelper(tokens, index);
    }
};

// Your Codec object will be instantiated and called as such:
// Codec ser, deser;
// TreeNode* ans = deser.deserialize(ser.serialize(root));

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna