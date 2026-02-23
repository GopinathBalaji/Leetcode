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