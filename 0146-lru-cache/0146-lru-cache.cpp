// Method 1: Using raw pointers with destructor
/*
# Core linked list operations

You only need two helper functions:
1) Remove a node from the list
2) Insert a node right after head
*/
struct Node{
    int key;
    int value;
    Node* next = nullptr;
    Node* prev = nullptr;
    Node(int k, int v) : key(k) , value(v) {}
};

class LRUCache {
private:
    int capacity;
    unordered_map<int, Node*> map;

    Node* head;
    Node* tail;

public:
    LRUCache(int capacity) : capacity(capacity) {
        head = new Node(-1, -1);
        tail = new Node(-1, -1);

        head->next = tail;
        tail->prev = head;
    }

    ~LRUCache() {
        Node* curr = head;

        while(curr != nullptr){
            Node* nextNode = curr->next;
            delete curr;
            curr = nextNode;
        }
    }
    
    int get(int key) {
        if(map.find(key) == map.end()){
            return -1;
        }

        Node* node = map[key];
        removeNode(node);
        insertAfterHead(node);

        return node->value;
    }
    
    void put(int key, int value) {
        if(map.find(key) != map.end()){
            Node* node = map[key];
            node->value = value;

            removeNode(node);
            insertAfterHead(node);

            return;
        }

        Node* node = new Node(key, value);
        map[key] = node;
        insertAfterHead(node);
        
        if(map.size() > capacity){
            Node* lru = tail->prev;

            removeNode(lru);
            map.erase(lru->key);

            delete lru;
        }
    }

    // Double Linked List Helpers: 1) Remove and 2) Inserting after Head
    void removeNode(Node* node){
        Node* before = node->prev;
        Node* after = node->next;

        before->next = after;
        after->prev = before;
    }

    void insertAfterHead(Node* node){
        Node* first = head->next;

        head->next = node;
        node->prev = head;

        node->next = first;
        first->prev = node;
    }
};






// Method 2: Using built-in Doubly Linked List class (std::list)
/*
*/
// class LRUCache {
// private:
//     int capacity;

//     // front = most recently used
//     // back  = least recently used
//     list<pair<int, int>> cache;

//     // key -> iterator pointing to node in list
//     unordered_map<int, list<pair<int, int>>::iterator> map;

// public:
//     LRUCache(int capacity) : capacity(capacity) {}

//     int get(int key) {
//         if (map.find(key) == map.end()) {
//             return -1;
//         }

//         auto it = map[key];
//         int value = it->second;

//         // Move this node to the front because it was recently used
//         cache.splice(cache.begin(), cache, it);

//         return value;
//     }

//     void put(int key, int value) {
//         if (map.find(key) != map.end()) {
//             auto it = map[key];

//             // Update value
//             it->second = value;

//             // Move to front
//             cache.splice(cache.begin(), cache, it);

//             return;
//         }

//         if (cache.size() == capacity) {
//             auto lru = cache.back();
//             int lruKey = lru.first;

//             map.erase(lruKey);
//             cache.pop_back();
//         }

//         cache.push_front({key, value});
//         map[key] = cache.begin();
//     }
// };




/**
 * Your LRUCache object will be instantiated and called as such:
 * LRUCache* obj = new LRUCache(capacity);
 * int param_1 = obj->get(key);
 * obj->put(key,value);
 */