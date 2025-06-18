class Solution {
    public int minimumJumps(int[] forbidden, int a, int b, int x) {

        int maxForbidden = Arrays.stream(forbidden).max().getAsInt();
        int limit = Math.max(a + b + maxForbidden, x + b);

        // For O(1) lookup if value is forbidden or not
        boolean[] isForbidden = new boolean[limit + 1];
        for(int f : forbidden){
            if(f <= limit){
                isForbidden[f] = true;
            }
        }

        // 3) visited[pos][back] — back=1 if last move was backward
        boolean[][] visited = new boolean[limit + 1][2];
        Queue<int[]> q = new LinkedList<>();

        q.offer(new int[]{0, 0});
        visited[0][0] = true;

        int steps = 0;
        while(!q.isEmpty()){
            int sz = q.size();
            for(int i=0;i<sz;i++){
                int[] cur = q.poll();
                int pos = cur[0];
                int back = cur[1];

                if(pos == x){
                    return steps;
                }

                int fwd = pos + a;
                if(fwd <= limit && !isForbidden[fwd] && !visited[fwd][0]){
                    visited[fwd][0] = true;
                    q.offer(new int[]{fwd, 0});
                }

                // ← Backward jump (only if last move wasn’t backward)
                int bwd = pos - b;
                if(back == 0 && bwd >= 0 && !isForbidden[bwd] && !visited[bwd][1]){
                    visited[bwd][1] = true;
                    q.offer(new int[]{bwd, 1});
                }
            }
            steps++;
        }

        return -1;
    }
}