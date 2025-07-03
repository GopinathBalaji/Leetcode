// Using Graph BFS and extra space to store visited information
class Solution {
    public int numIslands(char[][] grid) {
        int islands = 0;
        int rows = grid.length;
        int cols = grid[0].length;

        boolean[][] visited = new boolean[rows][cols];

        Deque<int[]> queue = new ArrayDeque<>();
        
        int[][] dirs = {{1,0}, {-1,0}, {0,1}, {0,-1}};

        for(int r=0; r<rows; r++){
            for(int c=0; c<cols; c++){

                if(grid[r][c] == '1' && !visited[r][c]){
                    islands++;

                    visited[r][c] = true;
                    queue.offer(new int[]{r,c});

                    while(!queue.isEmpty()){
                        int[] cur = queue.poll();
                        int cr = cur[0];
                        int cc = cur[1];

                        for(int[] d : dirs){
                            int nr = cr + d[0];
                            int nc = cc + d[1];

                            if(nr >=0 && nr < rows && nc >=0 && nc < cols && grid[nr][nc] == '1' && !visited[nr][nc]){
                                visited[nr][nc] = true;
                                queue.offer(new int[]{nr, nc});
                            }
                        }
                    }
                }
            }
        }

        return islands;
    }
}

// Using Graph BFS + no extra visited matrix. Changing 1s to 0s as we traverse the grid
// class Solution {
//     public int numIslands(char[][] grid) {
//         if (grid == null || grid.length == 0) return 0;
//         int rows = grid.length, cols = grid[0].length;
//         int islands = 0;
//         Deque<int[]> queue = new ArrayDeque<>();
//         // Four directions: down, up, right, left
//         int[][] dirs = {{1,0},{-1,0},{0,1},{0,-1}};
        
//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 // When you find an unvisited '1', that's a new island
//                 if (grid[r][c] == '1') {
//                     islands++;
//                     // Mark it as visited by mutating the grid
//                     grid[r][c] = '0';
//                     queue.offer(new int[]{r, c});
                    
//                     // BFS to sink the entire island
//                     while (!queue.isEmpty()) {
//                         int[] cur = queue.poll();
//                         int cr = cur[0], cc = cur[1];
//                         for (int[] d : dirs) {
//                             int nr = cr + d[0], nc = cc + d[1];
//                             if (nr >= 0 && nr < rows
//                              && nc >= 0 && nc < cols
//                              && grid[nr][nc] == '1') {
//                                 grid[nr][nc] = '0';
//                                 queue.offer(new int[]{nr, nc});
//                             }
//                         }
//                     }
//                 }
//             }
//         }
        
//         return islands;
//     }
// }


// Graph DFS code
// class Solution {
//     private int rows, cols;
//     // Four-direction vectors: down, up, right, left
//     private final int[][] DIRS = {{1,0},{-1,0},{0,1},{0,-1}};
    
//     public int numIslands(char[][] grid) {
//         if (grid == null || grid.length == 0) return 0;
        
//         rows = grid.length;
//         cols = grid[0].length;
//         int islands = 0;
        
//         for (int r = 0; r < rows; r++) {
//             for (int c = 0; c < cols; c++) {
//                 if (grid[r][c] == '1') {
//                     // Found an unvisited island cell
//                     islands++;
//                     // Flood‐fill (sink) the entire island
//                     dfs(grid, r, c);
//                 }
//             }
//         }
        
//         return islands;
//     }
    
//     private void dfs(char[][] grid, int r, int c) {
//         // Boundary or water checks
//         if (r < 0 || r >= rows || c < 0 || c >= cols || grid[r][c] != '1') {
//             return;
//         }
//         // Mark this cell as visited by sinking it
//         grid[r][c] = '0';
//         // Recurse in all four directions
//         for (int[] d : DIRS) {
//             dfs(grid, r + d[0], c + d[1]);
//         }
//     }
// }
