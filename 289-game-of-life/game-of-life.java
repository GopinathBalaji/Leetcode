class Solution {
    public void gameOfLife(int[][] board) {

        int[][] copy = Arrays.stream(board).map(int[]::clone).toArray(int[][]::new);

        int vertLimit = board.length;
        int horiLimit = board[0].length;

        for (int i = 0; i < vertLimit; i++) {
            for (int j = 0; j < horiLimit; j++) {
                int liveCount = 0;

                // Iterate over all 8 directions
                for (int x = -1; x <= 1; x++) {
                    for (int y = -1; y <= 1; y++) {
                        if (x == 0 && y == 0) continue; // skip the current cell itself

                        int ni = i + x;
                        int nj = j + y;

                        if (ni >= 0 && ni < vertLimit && nj >= 0 && nj < horiLimit) {
                            if (copy[ni][nj] == 1) {
                                liveCount++;
                            }
                        }
                    }
                }

                // Apply Game of Life rules
                if (copy[i][j] == 1) {
                    if (liveCount < 2 || liveCount > 3) {
                        board[i][j] = 0;
                    } else {
                        board[i][j] = 1;
                    }
                } else {
                    if (liveCount == 3) {
                        board[i][j] = 1;
                    }
                }
            }
        }
    }
}
