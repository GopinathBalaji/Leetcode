class Solution {
    private int rows;
    private int cols;
    private int[][] dirs = {{1, 0}, {-1,0}, {0, 1}, {0, -1}};

    public void solve(char[][] board) {
        if (board == null || board.length == 0) return;

        rows = board.length;
        cols = board[0].length;
        boolean[][] visited = new boolean[rows][cols];

        // Top and Bottom
        for (int j = 0; j < cols; j++) {
            if (board[0][j] == 'O') dfs(board, 0, j, visited);
            if (board[rows - 1][j] == 'O') dfs(board, rows - 1, j, visited);
        }

        // Left and Right
        for (int i = 0; i < rows; i++) {
            if (board[i][0] == 'O') dfs(board, i, 0, visited);
            if (board[i][cols - 1] == 'O') dfs(board, i, cols - 1, visited);
        }

        // Final transform
        for (int i = 0; i < rows; i++) {
            for (int j = 0; j < cols; j++) {
                if (board[i][j] == 'O') board[i][j] = 'X';
                if (board[i][j] == '#') board[i][j] = 'O';
            }
        }
    }

    private void dfs(char[][] board, int r, int c, boolean[][] visited) {
        if (r < 0 || c < 0 || r >= rows || c >= cols) return;
        if (board[r][c] != 'O' || visited[r][c]) return;

        visited[r][c] = true;
        board[r][c] = '#';

        for (int[] d : dirs) {
            dfs(board, r + d[0], c + d[1], visited);
        }
    }
}
