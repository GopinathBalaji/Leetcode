// Method 1: Hashing with buckets
/*
*/
class MyHashMap {
private:
    static const int bucketCount = 1000;
    vector<list<pair<int, int>>> buckets;

public:
    MyHashMap() {
        buckets.resize(bucketCount);
    }
    
    void put(int key, int value) {
        int bucketIndex = key % bucketCount;

        for(auto& [k, v] : buckets[bucketIndex]){
            if(k == key){
                v = value;
                return;
            }
        }

        buckets[bucketIndex].push_back({key, value});
    }
    
    int get(int key) {
        int bucketIndex = key % bucketCount;

        for(const auto& [k, v] : buckets[bucketIndex]){
            if(k == key){
                return v;
            }
        }

        return -1;
    }
    
    void remove(int key) {
        int bucketIndex = key % bucketCount;
        for(auto it = buckets[bucketIndex].begin(); it != buckets[bucketIndex].end(); it++){
            if(it->first == key){
                buckets[bucketIndex].erase(it);
                return;
            }
        }
    }
};

/**
 * Your MyHashMap object will be instantiated and called as such:
 * MyHashMap* obj = new MyHashMap();
 * obj->put(key,value);
 * int param_2 = obj->get(key);
 * obj->remove(key);
 */