// Method 1: Binary Search in a closed interval
/*
Use a hash map where each key stores its full history.

For one key, think of the data like:

```text
"foo" -> [(1, "bar"), (4, "bar2"), (10, "bar3")]
```

## Main idea

### `set(key, value, timestamp)`

Store:

```cpp
(timestamp, value)
```

in a list for that key.

The problem guarantees that, for the same key, timestamps passed to `set` are strictly increasing. So you can simply append to the vector.

```cpp
mp[key].push_back({timestamp, value});
```

### `get(key, timestamp)`

You need the value with the **largest stored timestamp that is less than or equal to** the requested timestamp.

For example:

```text
history for "foo": [(1,"bar"), (4,"bar2"), (10,"bar3")]

get("foo", 7)  -> "bar2"
get("foo", 10) -> "bar3"
get("foo", 0)  -> ""
```

That is a binary-search problem.

## Binary-search condition

For a candidate history entry:

```cpp
history[mid].first <= timestamp
```

then it is a valid possible answer.

* Save its value or index.
* Continue searching right, because there could be a later valid timestamp.

Otherwise:

```cpp
history[mid].first > timestamp
```

search left.

## Helpful structure

```cpp
unordered_map<string, vector<pair<int, string>>> mp;
```


## Important edge cases

* The key does not exist → return `""`.
* Requested timestamp is earlier than every stored timestamp → return `""`.
* Requested timestamp exactly matches a stored timestamp → return that value.
* Requested timestamp falls between two stored timestamps → return the earlier one.

## Complexity

* `set`: `O(1)` amortized, because you append.
* `get`: `O(log n)` for that key’s timestamp history.

The core phrase to remember is:

> Find the **rightmost timestamp `<=` query timestamp**.
*/
class TimeMap {
private:
    unordered_map<string, vector<pair<int, string>>> mp;

public:
    TimeMap() {
        
    }
    
    void set(string key, string value, int timestamp) {
        mp[key].push_back({timestamp, value});
    }
    
    string get(string key, int timestamp) {
        const vector<pair<int, string>>& history = mp[key];

        int left = 0;
        int right = history.size() - 1;
        int ans = -1;

        while(left <= right){
            int mid = left + (right - left) / 2;

            if(history[mid].first <= timestamp){
                ans = mid;
                left = mid + 1;
            }else{
                right = mid - 1;
            }
        }

        return ans == -1 ? "" : history[ans].second;
    }
};

/**
 * Your TimeMap object will be instantiated and called as such:
 * TimeMap* obj = new TimeMap();
 * obj->set(key,value,timestamp);
 * string param_2 = obj->get(key,timestamp);
 */

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna