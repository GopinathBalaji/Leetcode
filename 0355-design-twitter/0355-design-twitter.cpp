// Method 1: MaxHeap for getting news feed
/*
### Hint 1

Identify the operations you must support:

```cpp
postTweet(userId, tweetId)
getNewsFeed(userId)
follow(followerId, followeeId)
unfollow(followerId, followeeId)
```

The hardest operation is `getNewsFeed`.

---

### Hint 2

For each user, store the users they follow:

```cpp
unordered_map<int, unordered_set<int>> following;
```

Also treat a user as following themselves when building their feed, so their own tweets appear.

---

### Hint 3

Store each tweet with a timestamp so you can compare recency:

```cpp
struct Tweet {
    int tweetId;
    int time;
};
```

Maintain a global increasing counter:

```cpp
int timestamp = 0;
```

When posting:

```cpp
tweets[userId].push_back({tweetId, timestamp++});
```

---

### Hint 4

For each user, keep their tweets in chronological order:

```cpp
unordered_map<int, vector<Tweet>> tweets;
```

Since new tweets are appended, the most recent tweet is at the end of the vector.

---

### Hint 5

To build the news feed, you need the 10 newest tweets among several already-sorted tweet lists.

This is similar to merging `k` sorted lists.

Use a **max-heap** containing the most recent tweet from each relevant user.

---

### Hint 6

Each heap entry needs enough information to find the next older tweet from the same user:

```cpp
struct Entry {
    int time;
    int tweetId;
    int userId;
    int index;
};
```

Here, `index` is the tweet’s position in that user’s tweet vector.

---

### Hint 7

Initially, push the latest tweet from:

* the requesting user
* every user they follow

Then repeat at most 10 times:

1. Pop the newest tweet.
2. Add its `tweetId` to the result.
3. Push the previous tweet from the same user, if one exists.

---

### Hint 8

The core feed logic looks like:

```cpp
while (!maxHeap.empty() && feed.size() < 10) {
    auto current = maxHeap.top();
    maxHeap.pop();

    feed.push_back(current.tweetId);

    if (current.index > 0) {
        // Push the previous tweet from current.userId
    }
}
```

---

### Hint 9

For following:

```cpp
following[followerId].insert(followeeId);
```

For unfollowing:

```cpp
following[followerId].erase(followeeId);
```

Be careful with self-following. An easy design is to never store self-following and simply include the user separately in `getNewsFeed`.

---

### Suggested class state

```cpp
class Twitter {
private:
    int timestamp;

    unordered_map<int, vector<pair<int, int>>> tweets;
    unordered_map<int, unordered_set<int>> following;
};
```

You could store each tweet as:

```cpp
{timestamp, tweetId}
```

---

### Complexity

Let `f` be the number of followed users.

* `postTweet`: `O(1)`
* `follow`: average `O(1)`
* `unfollow`: average `O(1)`
* `getNewsFeed`: approximately `O((f + 10) log f)`

The key insight is that you should not combine and sort every tweet. Only keep one candidate tweet per user in the heap at a time.
*/
class Twitter {
private:
    unordered_map<int, unordered_set<int>> following;
    struct Tweet{
        int tweetId;
        int time;
    };
    int timestamp = 0;
    unordered_map<int, vector<Tweet>> tweets;
    struct Entry{
        int time;
        int tweetId;
        int userId;
        int index;
    };

    // Makes the entry with the greatest timestamp appear at the top of the priority queue.
    struct Compare{
        bool operator()(const Entry& a, const Entry& b) const {
            return a.time < b.time;
        }
    };

public:
    Twitter() {}
    
    void postTweet(int userId, int tweetId) {
        tweets[userId].push_back({tweetId, timestamp++});
    }
    
    vector<int> getNewsFeed(int userId) {
        vector<int> feed;
        priority_queue<Entry, vector<Entry>, Compare> maxHeap;

        // Adding user's latest tweet
        if(!tweets[userId].empty()){
            int index = static_cast<int>(tweets[userId].size()) - 1;
            Tweet& tweet = tweets[userId][index];

            maxHeap.push({tweet.time, tweet.tweetId, userId, index});
        }

        // Adding the latest tweet from each followed user
        for(int followeeId : following[userId]){
            if(followeeId == userId || tweets[followeeId].empty()){
                continue;
            }

            int index = static_cast<int> (tweets[followeeId].size()) - 1;
            Tweet& tweet = tweets[followeeId][index];

            maxHeap.push({tweet.time, tweet.tweetId, followeeId, index});
        }

        while(!maxHeap.empty() && feed.size() < 10){
            Entry current = maxHeap.top();
            maxHeap.pop();

            feed.push_back(current.tweetId);

            // Push the next older tweet from the same user
            int previousIndex = current.index - 1;

            if(previousIndex >= 0){
                Tweet& previousTweet = tweets[current.userId][previousIndex];

                maxHeap.push({previousTweet.time, previousTweet.tweetId, current.userId, previousIndex});
            }
        }

        return feed;
    }
    
    void follow(int followerId, int followeeId) {
        if(followerId != followeeId){
            following[followerId].insert(followeeId);
        }
    }
    
    void unfollow(int followerId, int followeeId) {
        following[followerId].erase(followeeId);
    }
};

/**
 * Your Twitter object will be instantiated and called as such:
 * Twitter* obj = new Twitter();
 * obj->postTweet(userId,tweetId);
 * vector<int> param_2 = obj->getNewsFeed(userId);
 * obj->follow(followerId,followeeId);
 * obj->unfollow(followerId,followeeId);
 */

// Synced seamlessly with LeetHub Pro
// Pro features: https://bit.ly/leethubpro | Free version: https://bit.ly/leethubv4
// Get it here: https://chromewebstore.google.com/detail/bcilpkkbokcopmabingnndookdogmbna