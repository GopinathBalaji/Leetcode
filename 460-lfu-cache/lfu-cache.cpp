// Method 1: Using Doubly Linked List
/*
For **LFU Cache using your own doubly linked list**, think of the design as:

```text
key -> Node*
freq -> doubly linked list of Nodes
```

Each frequency has its own doubly linked list.

---

## Hint 1: Each node needs more than key and value

For LRU, your node had:

```cpp
key, value
```

For LFU, each node also needs:

```cpp
freq
```

So the node should look like:

```cpp
struct Node {
    int key;
    int value;
    int freq;

    Node* prev;
    Node* next;

    Node(int k, int v) {
        key = k;
        value = v;
        freq = 1;
        prev = nullptr;
        next = nullptr;
    }
};
```

The `freq` tells you which frequency bucket this node currently belongs to.

---

## Hint 2: You need one doubly linked list per frequency

Instead of one global list like LRU, LFU needs many lists:

```text
freq = 1:  most recent <-> ... <-> least recent
freq = 2:  most recent <-> ... <-> least recent
freq = 3:  most recent <-> ... <-> least recent
```

So you can have:

```cpp
unordered_map<int, DoublyLinkedList*> freqMap;
```

Meaning:

```text
frequency -> list of nodes with that frequency
```

---

## Hint 3: You still need fast key lookup

You also need:

```cpp
unordered_map<int, Node*> keyMap;
```

Meaning:

```text
key -> actual node
```

This lets `get(key)` find the node in O(1).

---

## Hint 4: Your doubly linked list class should support three operations

Each frequency bucket should be a small LRU list.

You need:

```cpp
addToFront(Node* node)
removeNode(Node* node)
removeLast()
```

The list order should be:

```text
front = most recently used inside that frequency
back  = least recently used inside that frequency
```

So `removeLast()` removes the LRU node among all nodes with that same frequency.

---

## Hint 5: Use dummy head and dummy tail for each list

Each frequency list can look like this:

```text
head <-> real nodes <-> tail
```

The list class can be designed like:

```cpp
class DoublyLinkedList {
public:
    Node* head;
    Node* tail;
    int size;

    DoublyLinkedList() {
        head = new Node(-1, -1);
        tail = new Node(-1, -1);

        head->next = tail;
        tail->prev = head;

        size = 0;
    }

    void addToFront(Node* node) {
        Node* first = head->next;

        head->next = node;
        node->prev = head;

        node->next = first;
        first->prev = node;

        size++;
    }

    void removeNode(Node* node) {
        Node* before = node->prev;
        Node* after = node->next;

        before->next = after;
        after->prev = before;

        size--;
    }

    Node* removeLast() {
        if (size == 0) {
            return nullptr;
        }

        Node* last = tail->prev;
        removeNode(last);
        return last;
    }
};
```

This list is used for each frequency.

---

## Hint 6: Track the minimum frequency

You need:

```cpp
int minFreq;
```

This tells you which frequency bucket to evict from.

Example:

```text
freq = 1: empty
freq = 2: [7, 4]
freq = 3: [9]
```

Here:

```cpp
minFreq = 2;
```

When the cache is full, evict from:

```cpp
freqMap[minFreq]
```

Specifically, remove from the **back** of that list.

---

## Hint 7: The main helper is `increaseFreq(Node* node)`

Whenever a key is used, you move it from frequency `f` to frequency `f + 1`.

The helper should do this:

```text
oldFreq = node->freq

remove node from freqMap[oldFreq]

if oldFreq list becomes empty and minFreq == oldFreq:
    minFreq++

node->freq++

add node to front of freqMap[node->freq]
```

In C++ shape:

```cpp
void increaseFreq(Node* node) {
    int oldFreq = node->freq;

    freqMap[oldFreq]->removeNode(node);

    if (freqMap[oldFreq]->size == 0 && minFreq == oldFreq) {
        minFreq++;
    }

    node->freq++;

    int newFreq = node->freq;

    if (freqMap.find(newFreq) == freqMap.end()) {
        freqMap[newFreq] = new DoublyLinkedList();
    }

    freqMap[newFreq]->addToFront(node);
}
```

---

## Hint 8: `get(key)` becomes simple

For `get(key)`:

```text
If key does not exist:
    return -1

Otherwise:
    find the node
    increase its frequency
    return its value
```

Shape:

```cpp
int get(int key) {
    if (keyMap.find(key) == keyMap.end()) {
        return -1;
    }

    Node* node = keyMap[key];
    increaseFreq(node);

    return node->value;
}
```

---

## Hint 9: `put(key, value)` has three cases

### Case 1: Capacity is zero

```cpp
if (capacity == 0) {
    return;
}
```

---

### Case 2: Key already exists

Update the value and increase frequency:

```cpp
if (keyMap.find(key) != keyMap.end()) {
    Node* node = keyMap[key];
    node->value = value;
    increaseFreq(node);
    return;
}
```

The `return` is important.

---

### Case 3: New key

If cache is full, evict from the `minFreq` list:

```cpp
if (size == capacity) {
    Node* victim = freqMap[minFreq]->removeLast();
    keyMap.erase(victim->key);
    delete victim;
    size--;
}
```

Then insert the new node with frequency `1`:

```cpp
Node* node = new Node(key, value);

if (freqMap.find(1) == freqMap.end()) {
    freqMap[1] = new DoublyLinkedList();
}

freqMap[1]->addToFront(node);
keyMap[key] = node;

minFreq = 1;
size++;
```

---

## Overall design

Your class should roughly have:

```cpp
class LFUCache {
private:
    int capacity;
    int size;
    int minFreq;

    unordered_map<int, Node*> keyMap;
    unordered_map<int, DoublyLinkedList*> freqMap;

public:
    LFUCache(int capacity) {
        this->capacity = capacity;
        this->size = 0;
        this->minFreq = 0;
    }

    int get(int key) {
        // use keyMap
        // call increaseFreq
    }

    void put(int key, int value) {
        // handle capacity 0
        // handle existing key
        // evict if full
        // insert new node with freq 1
    }

private:
    void increaseFreq(Node* node) {
        // move node from freq f to freq f + 1
    }
};
```

The main mental model is:

```text
keyMap:  key -> node

freqMap:
1 -> DLL of all nodes used once
2 -> DLL of all nodes used twice
3 -> DLL of all nodes used three times

minFreq -> lowest frequency currently present
```

When evicting:

```text
Go to freqMap[minFreq]
Remove from the back
```

That satisfies:

```text
Least frequently used first
If tied, least recently used among that frequency
```
*/

struct Node {
    int key;
    int value;
    int freq;

    Node* prev;
    Node* next;

    Node(int k, int v) : key(k), value(v), freq(1), prev(nullptr), next(nullptr)  {}
};

// Double Linked List Helpers: 1) Remove , 2) Inserting after Head , 3) Remove Last (removing LRU)
class DoublyLinkedList{
public:
    Node* head;
    Node* tail;
    int size;

    DoublyLinkedList() {
        head = new Node(-1, -1);
        tail = new Node(-1, -1);

        head->next = tail;
        tail->prev = head;

        size = 0;
    }

    ~DoublyLinkedList() {
        Node* curr = head;

        while(curr != nullptr){
            Node* nextNode = curr->next;
            delete curr;
            curr = nextNode;
        }
    }

    void addToFront(Node* node) {
        Node* first = head->next;

        head->next = node;
        node->prev = head;

        node->next = first;
        first->prev = node;

        size++;
    }

    void removeNode(Node* node) {
        Node* before = node->prev;
        Node* after = node->next;

        before->next = after;
        after->prev = before;

        size--;
    }

    Node* removeLast() {
        if(size == 0){
            return nullptr;
        }

        Node* last = tail->prev;
        removeNode(last);
        return last;
    }
};


class LFUCache {
private:
    int capacity;
    int size;
    int minFreq;

    unordered_map<int, Node*> keyMap;
    unordered_map<int, DoublyLinkedList*> freqMap;

public:
    LFUCache(int capacity) : capacity(capacity), size(0), minFreq(0)  {}

    ~LFUCache() {
        for(auto& [freq, list] : freqMap){
            delete list;
        }
    }
    
    int get(int key) {
        if(keyMap.find(key) == keyMap.end()){
            return -1;
        }

        Node* node = keyMap[key];
        increaseFreq(node);

        return node->value;
    }
    
    void put(int key, int value) {
        if(capacity == 0){
            return;
        }

        if(keyMap.find(key) != keyMap.end()) {
            Node* node = keyMap[key];
            node->value = value;
            
            increaseFreq(node);
            return;
        }


        if(size == capacity){
            Node* victim = freqMap[minFreq]->removeLast();
            keyMap.erase(victim->key);
            delete victim;
            size--;
        }

        Node* node = new Node(key, value);

        if(freqMap.find(1) == freqMap.end()){
            freqMap[1] = new DoublyLinkedList();
        }

        freqMap[1]->addToFront(node);
        keyMap[key] = node;

        minFreq = 1;
        size++;
    }

private:
    void increaseFreq(Node* node){
        int oldFreq = node->freq;
        freqMap[oldFreq]->removeNode(node);

        if(freqMap[oldFreq]->size == 0 && minFreq == oldFreq){
            minFreq++;
        }

        node->freq++;

        int newFreq = node->freq;

        if(freqMap.find(newFreq) == freqMap.end()){
            freqMap[newFreq] = new DoublyLinkedList();
        }

        freqMap[newFreq]->addToFront(node);
    }
};





// Method 2: Using built-in Doubly Linked List class (std::list)
/*
*/
// class LFUCache {
// private:
//     struct Node {
//         int key;
//         int value;
//         int freq;

//         Node(int k, int v) : key(k), value(v), freq(1) {}
//     };

//     int capacity;
//     int size;
//     int minFreq;

//     // freq -> list of nodes with that frequency
//     // front = most recently used within that frequency
//     // back  = least recently used within that frequency
//     unordered_map<int, list<Node>> freqMap;

//     // key -> iterator pointing to the node inside freqMap[node.freq]
//     unordered_map<int, list<Node>::iterator> keyMap;

// private:
//     void increaseFreq(int key) {
//         auto it = keyMap[key];

//         int oldFreq = it->freq;
//         int value = it->value;

//         // Remove node from old frequency list
//         freqMap[oldFreq].erase(it);

//         // If old frequency list becomes empty, update minFreq if needed
//         if (freqMap[oldFreq].empty()) {
//             freqMap.erase(oldFreq);

//             if (minFreq == oldFreq) {
//                 minFreq++;
//             }
//         }

//         // Insert node into new frequency list
//         int newFreq = oldFreq + 1;
//         freqMap[newFreq].push_front(Node(key, value));
//         freqMap[newFreq].front().freq = newFreq;

//         // Update keyMap to point to new location
//         keyMap[key] = freqMap[newFreq].begin();
//     }

// public:
//     LFUCache(int capacity) : capacity(capacity), size(0), minFreq(0) {}

//     int get(int key) {
//         if (keyMap.find(key) == keyMap.end()) {
//             return -1;
//         }

//         int value = keyMap[key]->value;

//         increaseFreq(key);

//         return value;
//     }

//     void put(int key, int value) {
//         if (capacity == 0) {
//             return;
//         }

//         // Case 1: key already exists
//         if (keyMap.find(key) != keyMap.end()) {
//             keyMap[key]->value = value;
//             increaseFreq(key);
//             return;
//         }

//         // Case 2: cache is full, evict LFU + LRU
//         if (size == capacity) {
//             auto& minFreqList = freqMap[minFreq];

//             Node victim = minFreqList.back();
//             minFreqList.pop_back();

//             keyMap.erase(victim.key);

//             if (minFreqList.empty()) {
//                 freqMap.erase(minFreq);
//             }

//             size--;
//         }

//         // Case 3: insert new node with freq = 1
//         freqMap[1].push_front(Node(key, value));
//         keyMap[key] = freqMap[1].begin();

//         minFreq = 1;
//         size++;
//     }
// };



/**
 * Your LFUCache object will be instantiated and called as such:
 * LFUCache* obj = new LFUCache(capacity);
 * int param_1 = obj->get(key);
 * obj->put(key,value);
 */