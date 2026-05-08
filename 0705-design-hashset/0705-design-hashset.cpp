// Method 1: Hashing with buckets
/*
## Hint 1: What does `HashSet` need to support?

LeetCode 705 asks you to implement:

```cpp
void add(int key);
void remove(int key);
bool contains(int key);
```

A set stores only **keys**, not key-value pairs.

So unlike `HashMap`, you only care whether a key exists.

---

## Hint 2: Simplest possible approach

Since LeetCode says:

```text
0 <= key <= 10^6
```

One very simple solution is to use a boolean array:

```cpp
vector<bool> present;
```

Then:

```cpp
present[key] = true;   // add
present[key] = false;  // remove
return present[key];   // contains
```

This is accepted and very easy.

But it uses space proportional to the largest possible key.

---

## Hint 3: More “real hash set” approach

A more educational approach is to use **hashing with buckets**.

You create an array of buckets:

```cpp
vector<list<int>> buckets;
```

Each bucket stores keys that hash to the same index.

---

## Hint 4: Hash function

Use a fixed bucket size, for example:

```cpp
int bucketCount = 1000;
```

Then compute:

```cpp
int index = key % bucketCount;
```

So key `1005` goes to bucket:

```cpp
1005 % 1000 = 5
```

---

## Hint 5: Why buckets are needed

Different keys can map to the same bucket.

Example:

```cpp
5 % 1000 = 5
1005 % 1000 = 5
2005 % 1000 = 5
```

This is called a **collision**.

So each bucket needs to hold multiple keys.

That is why we can use:

```cpp
vector<list<int>> buckets;
```

---

## Hint 6: `add(key)`

To add a key:

```text
1. Find bucket index using key % bucketCount
2. Search that bucket
3. If key already exists, do nothing
4. Otherwise insert key into that bucket
```

Do not insert duplicates.

---

## Hint 7: `remove(key)`

To remove a key:

```text
1. Find bucket index
2. Search the bucket
3. If key is found, erase it
4. If not found, do nothing
```

---

## Hint 8: `contains(key)`

To check if a key exists:

```text
1. Find bucket index
2. Search the bucket
3. Return true if found
4. Otherwise return false
```

---

## Hint 9: Helper function

You can write a helper:

```cpp
int hash(int key) {
    return key % bucketCount;
}
```

And another helper to search inside a bucket:

```cpp
auto findKey(int key)
```

But for LeetCode, simple loops inside each method are also fine.


---

## Main idea

A hash set is basically:

```text
array of buckets
```

where each key goes to:

```cpp
key % bucketCount
```

If multiple keys go to the same bucket, store them together and search only inside that bucket.

*/
class MyHashSet {
private:
    vector<list<int>> buckets;
    static const int bucketCount = 1000;
public:
    MyHashSet() {
        buckets.resize(bucketCount);
    }
    
    void add(int key) {
        if(contains(key)){
            return;
        }

        int bucketIndex = key % bucketCount;
        buckets[bucketIndex].push_back(key);
    }
    
    // list::remove(key) is safe even if the key is not present, so no need to check if key is present
    void remove(int key) {
        int bucketIndex = key % bucketCount;
        buckets[bucketIndex].remove(key);
    }
    
    bool contains(int key) {
        int bucketIndex = key % bucketCount;
        for(const auto& num: buckets[bucketIndex]){
            if(num == key){
                return true;
            }
        }

        return false;
    }
};

/**
 * Your MyHashSet object will be instantiated and called as such:
 * MyHashSet* obj = new MyHashSet();
 * obj->add(key);
 * obj->remove(key);
 * bool param_3 = obj->contains(key);
 */