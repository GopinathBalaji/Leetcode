// Method 1: Uses maxHeap to get most recent tweets
/*
# Big picture: what `getNewsFeed` must do

`getNewsFeed(userId)` needs to return up to **10 most recent tweetIds** posted by:

1. `userId` themself
2. everyone `userId` follows

“Most recent” means: newest tweet across all those users, then next newest, etc.

---

# Your data structures (what they store)

## 1) `tweetMap: Map<Integer, List<Pair>>`

For each user, you store a list of their tweets:

```java
Pair(count, tweetId)
```

* `count` is a global timestamp that increases each time any tweet is posted (`count++`).
* Because you always append, each user’s list is automatically in increasing time order:

  * oldest … newest

Example list for user 5:

```
[(0, 101), (7, 300), (12, 44)]
```

## 2) `followMap: Map<Integer, Set<Integer>>`

For each follower, the set of followees.

Example:

```
followMap.get(1) = {2, 3}
```

## 3) `Node` (heap entry)

This is the key to efficiency:

```java
static class Node {
  int time;    // tweet timestamp (Pair.count)
  int tweetId; // tweet id
  int userId;  // which user’s tweet list this came from
  int idx;     // index inside that user's list
}
```

A `Node` is like a “cursor” into one user’s tweet list.

---

# Why a heap is needed

Each user’s tweet list is individually sorted by time, but you need the **global top 10** across multiple lists.

This is exactly the classic problem:

> Merge multiple sorted lists and take the top 10 (like merging k sorted lists).

But instead of merging everything (too slow), you only ever look at the **current best candidate** from each list. The heap gives you that in `O(log F)` time, where `F` is number of sources (followees + self).

---

# `getNewsFeed` explained line-by-line

## Step 1: Max-heap by time

```java
PriorityQueue<Node> maxHeap =
    new PriorityQueue<>((a, b) -> Integer.compare(b.time, a.time));
```

* Highest `time` (most recent) comes out first.

So `poll()` always returns the newest tweet currently available among candidates.

---

## Step 2: Build the “sources” set (self + followees)

```java
Set<Integer> sources = new HashSet<>();
sources.add(userId);
sources.addAll(followMap.getOrDefault(userId, Collections.emptySet()));
```

This ensures:

* Your feed includes your own tweets even if you follow nobody.
* If the user doesn’t exist in `followMap`, it’s fine.

---

## Step 3: Initialize heap with each source’s newest tweet only

```java
for (int u : sources) {
    List<Pair> tweets = tweetMap.get(u);
    if (tweets == null || tweets.isEmpty()) continue;

    int idx = tweets.size() - 1; // newest tweet index
    Pair p = tweets.get(idx);
    maxHeap.offer(new Node(p.count, p.tweetId, u, idx));
}
```

Important insight:

* For each user, the **newest tweet** is at the end of their list.
* You push *only one* tweet per user into the heap initially.
* That means heap size ≤ number of sources.

---

## Step 4: Repeatedly take the newest, then advance within that user’s list

```java
while (ans.size() < 10 && !maxHeap.isEmpty()) {
    Node cur = maxHeap.poll();
    ans.add(cur.tweetId);

    int nextIdx = cur.idx - 1;  // next older tweet from same user
    if (nextIdx >= 0) {
        List<Pair> tweets = tweetMap.get(cur.userId);
        Pair p = tweets.get(nextIdx);
        maxHeap.offer(new Node(p.count, p.tweetId, cur.userId, nextIdx));
    }
}
```

This is the k-way merge logic:

* You pop the newest tweet overall.
* If that tweet came from user U at index `idx`, then the next candidate from user U is their tweet at `idx - 1` (the next older one).
* Push that next older tweet into the heap.

This way, you always keep the heap filled with “current best remaining tweet” from each user.

---

# Thorough example walkthrough (heap contents every step)

Let’s simulate:

### Actions

1. `postTweet(1, 101)`
2. `postTweet(2, 201)`
3. `postTweet(1, 102)`
4. `postTweet(2, 202)`
5. `postTweet(3, 301)`
6. `follow(1, 2)`
7. `follow(1, 3)`
8. `getNewsFeed(1)`

### Timestamps assigned (`count++`)

Assume `count` starts at 0:

* time 0: user1 tweet 101  → Pair(0,101)
* time 1: user2 tweet 201  → Pair(1,201)
* time 2: user1 tweet 102  → Pair(2,102)
* time 3: user2 tweet 202  → Pair(3,202)
* time 4: user3 tweet 301  → Pair(4,301)

So `tweetMap` looks like:

* user 1 list: `[(0,101), (2,102)]`
* user 2 list: `[(1,201), (3,202)]`
* user 3 list: `[(4,301)]`

And `followMap.get(1) = {2, 3}`

---

## Now call: `getNewsFeed(1)`

### Step A: Build sources

`sources = {1, 2, 3}`

### Step B: Push newest tweet from each source into heap

* For user 1: newest idx=1 → (time=2, tweet=102)

  * push Node(2,102,user1,idx1)

* For user 2: newest idx=1 → (time=3, tweet=202)

  * push Node(3,202,user2,idx1)

* For user 3: newest idx=0 → (time=4, tweet=301)

  * push Node(4,301,user3,idx0)

Heap (conceptually ordered by time):

* top: (4,301,u3,i0)
* then: (3,202,u2,i1)
* then: (2,102,u1,i1)

`ans = []`

---

### Iteration 1

Pop heap → (4,301,u3,i0)

* ans = [301]
  Push next older from u3:
* nextIdx = 0-1 = -1 → none

Heap now:

* (3,202,u2,i1)
* (2,102,u1,i1)

---

### Iteration 2

Pop → (3,202,u2,i1)

* ans = [301, 202]
  Push next older from u2:
* nextIdx = 1-1 = 0 → tweetMap[2][0] = (1,201)
* push Node(1,201,u2,i0)

Heap now:

* (2,102,u1,i1)
* (1,201,u2,i0)

---

### Iteration 3

Pop → (2,102,u1,i1)

* ans = [301, 202, 102]
  Push next older from u1:
* nextIdx = 1-1 = 0 → tweetMap[1][0] = (0,101)
* push Node(0,101,u1,i0)

Heap now:

* (1,201,u2,i0)
* (0,101,u1,i0)

---

### Iteration 4

Pop → (1,201,u2,i0)

* ans = [301, 202, 102, 201]
  Push next older from u2:
* nextIdx = 0-1 = -1 → none

Heap now:

* (0,101,u1,i0)

---

### Iteration 5

Pop → (0,101,u1,i0)

* ans = [301, 202, 102, 201, 101]
  Push next older from u1:
* nextIdx = -1 → none

Heap empty → stop.

✅ Final result: `[301, 202, 102, 201, 101]`

That is exactly the 5 tweets in global descending time:
time 4,3,2,1,0

---

# Why this is efficient

Let:

* `F` = number of sources (followees + user)

Initialization pushes ≤ F nodes.

Then you do ≤ 10 iterations:

* each iteration is one `poll()` and at most one `offer()`
* each heap op costs `O(log F)`

So:

* Time for `getNewsFeed`: about `O(F log F + 10 log F)`
  (dominated by initial pushing of at most F tweets)
* Space: `O(F)` heap + output list

This is far better than:

* dumping all tweets from all followees into one list and sorting (could be huge).

---

# A couple of subtle correctness notes

1. **Ties in timestamp**
   Your timestamps are strictly increasing (`count++`), so no ties. Great.

2. **User follows themselves**
   Even if user 1 follows user 1, using a `Set` prevents duplicates.

3. **Order among the returned tweets**
   The returned list is correct ordering by time, but the code does not try to break ties by tweetId (not needed).
*/

class Twitter {

    static class Pair{
        int count;
        int tweetId;
        Pair(int count, int tweetId){
            this.count = count;
            this.tweetId = tweetId;
        }
    }

    static class Node{
        int time;
        int tweetId;
        int userId;
        int idx;

        Node(int time, int tweetId, int userId, int idx){
            this.time = time;
            this.tweetId = tweetId;
            this.userId = userId;
            this.idx = idx;
        }
    }
    
    Map<Integer, Set<Integer>> followMap;
    Map<Integer, List<Pair>> tweetMap;
    int count;

    public Twitter() {
        followMap = new HashMap<>();
        tweetMap = new HashMap<>();
        count = 0;
    }
    
    public void postTweet(int userId, int tweetId) {
        tweetMap.computeIfAbsent(userId, k -> new ArrayList<>()).add(new Pair(count++, tweetId));
    }
    
    public List<Integer> getNewsFeed(int userId) {

        // Max-heap by timestamp (most recent first)
        PriorityQueue<Node> maxHeap = new PriorityQueue<>((a , b) -> Integer.compare(b.time, a.time));

        // sources = userId + everyone they follow
        Set<Integer> sources = new HashSet<>();
        sources.add(userId);
        sources.addAll(followMap.getOrDefault(userId, Collections.emptySet()));

        // Push each source's most recent tweet into heap
        for(int u: sources){
            List<Pair> tweets = tweetMap.get(u);
            if(tweets == null || tweets.isEmpty()){
                continue;
            }

            int idx = tweets.size() - 1;
            Pair p = tweets.get(idx);
            maxHeap.offer(new Node(p.count, p.tweetId, u, idx));
        }

        // Pull up to 10 most recent tweets
        List<Integer> ans = new ArrayList<>(10);
        while(ans.size() < 10 && !maxHeap.isEmpty()){
            Node cur = maxHeap.poll();
            ans.add(cur.tweetId);


            // Push next older tweet from the same user (if any)
            int prevIdx = cur.idx - 1;
            if(prevIdx >= 0){
                List<Pair> tweets = tweetMap.get(cur.userId);
                Pair p = tweets.get(prevIdx);
                maxHeap.offer(new Node(p.count, p.tweetId, cur.userId, prevIdx));
            }
        }

        return ans;
    }
    
    public void follow(int followerId, int followeeId) {
        followMap.computeIfAbsent(followerId, k -> new HashSet<>()).add(followeeId);
    }
    
    public void unfollow(int followerId, int followeeId) {
        // followMap.getOrDefault(followerId, Collections.emptySet()).remove(followeeId);
        // followMap.computeIfPresent(followerId, (k, set) -> {
        //     set.remove(followeeId);
        //     return set; 
        // });

        followMap.computeIfPresent(followerId, (k, set) -> set.remove(followeeId) ? set : set);
    }
}

/**
 * Your Twitter object will be instantiated and called as such:
 * Twitter obj = new Twitter();
 * obj.postTweet(userId,tweetId);
 * List<Integer> param_2 = obj.getNewsFeed(userId);
 * obj.follow(followerId,followeeId);
 * obj.unfollow(followerId,followeeId);
 */