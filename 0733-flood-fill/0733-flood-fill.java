// BFS solution
class Solution {
    public int[][] floodFill(int[][] image, int sr, int sc, int color) {
        int m = image.length;
        int n = image[0].length;
        
        int origColor = image[sr][sc];
        if(origColor == color){
            return image;
        }

        boolean[][] visited = new boolean[image.length][image[0].length];
        visited[sr][sc] = true;
        image[sr][sc] = color;

        Queue<int[]> queue = new LinkedList<>();
        queue.offer(new int[]{sr, sc});

        int[][] dirs = {{-1,0},{1,0},{0,-1},{0,1}};

        while(!queue.isEmpty()){
            int[] cur = queue.poll();
            int r = cur[0];
            int c = cur[1];

            for(int[] d : dirs){
                
                int nr = r + d[0];
                int nc = c + d[1];

                if(nr >= 0 && nr < m && nc >= 0 && nc < n && !visited[nr][nc] && image[nr][nc] == origColor){
                    visited[nr][nc] = true;
                    image[nr][nc] = color;
                    queue.offer(new int[]{nr, nc});
                }
            }
        }

        return image;
    }
}


    // DFS Solution
// class Solution {
//     public int[][] floodFill(int[][] image, int sr, int sc, int newColor) {
//         int m = image.length, n = image[0].length;
//         int origColor = image[sr][sc];
//         // If the starting pixel is already the target color, nothing to do
//         if (origColor == newColor) return image;
//         dfs(image, sr, sc, origColor, newColor, m, n);
//         return image;
//     }

//     private void dfs(int[][] img, int r, int c, int orig, int target, int m, int n) {
//         // 1) bounds check
//         if (r < 0 || r >= m || c < 0 || c >= n) return;
//         // 2) only proceed on pixels matching the original color
//         if (img[r][c] != orig) return;
        
//         // 3) “visit” by recoloring
//         img[r][c] = target;
        
//         // 4) recurse in 4 directions
//         dfs(img, r - 1, c, orig, target, m, n);
//         dfs(img, r + 1, c, orig, target, m, n);
//         dfs(img, r, c - 1, orig, target, m, n);
//         dfs(img, r, c + 1, orig, target, m, n);
//     }
// }
