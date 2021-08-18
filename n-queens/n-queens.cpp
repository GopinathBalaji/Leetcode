class Solution {
public:
//     Method 1: Follow Backtracking template by Lynn Zheng
    
    vector<vector<string>> solveNQueens(int n) {
       vector<vector<string>> res;
        vector<string> v(n, string(n, '.'));
        search(0,n,res,v);
        
        return res;
    }
    
    void search(int col, int n, vector<vector<string>> &res, vector<string> &v){
        if(isValidState(col,n) == true){
            res.push_back(v);
            return;
        }
        
        for(int row=0;row<n;row++){
            if(isSafeCandidate(row,col,n,v) == true){
                v[row][col] = 'Q';
                search(col+1,n,res,v);
                v[row][col] = '.';
            }
        }
    }
    
    bool isSafeCandidate(int row, int col, int n, vector<string> &v){
        for(int i=0;i<col;i++){
            if(v[row][i] == 'Q'){
                return false;
            }
        }
        for(int i=row,j=col; i>=0 and j>=0 ;i--,j--){  // check for queen in upper left diagonal
            if(v[i][j]=='Q'){
                return false;
            }
        }
        
        for(int i=row,j=col; i<n and j>=0; i++,j--){
            if(v[i][j]=='Q'){
                return false;
            }
        }
        
        return true;
    }
    
    bool isValidState(int col, int n){
        return col == n;
    }
};

// Template
   /*
    def is_valid_state(state):
    # check if it is a valid solution
    return True

def get_candidates(state):
    return []

def search(state, solutions):
    if is_valid_state(state):
        solutions.append(state.copy())
        # return

    for candidate in get_candidates(state):
        state.add(candidate)
        search(state, solutions)
        state.remove(candidate)

def solve():
    solutions = []
    state = set()
    search(state, solutions)
    return solutions
   */

// Method 2: Similar Approach in Python
   /*
    class Solution:
    """
    example on the left: [1, 3, 0, 2]
    example on the right: [2, 0, 3, 1]
    """
    def solveNQueens(self, n: int) -> List[List[str]]:
        solutions = []
        state = []
        self.search(state, solutions, n)
        return solutions
        
    def is_valid_state(self, state, n):
        # check if it is a valid solution
        return len(state) == n

    def get_candidates(self, state, n):
        if not state:
            return range(n)
        
        # find the next position in the state to populate
        position = len(state)
        candidates = set(range(n))
        # prune down candidates that place the queen into attacks
        for row, col in enumerate(state):
            # discard the column index if it's occupied by a queen
            candidates.discard(col)
            dist = position - row
            # discard diagonals
            candidates.discard(col + dist)
            candidates.discard(col - dist)
        return candidates

    def search(self, state, solutions, n):
        if self.is_valid_state(state, n):
            state_string = self.state_to_string(state, n)
            solutions.append(state_string)
            return

        for candidate in self.get_candidates(state, n):
            # recurse
            state.append(candidate)
            self.search(state, solutions, n)
            state.pop()
    
    def state_to_string(self, state, n):
        # ex. [1, 3, 0, 2]
        # output: [".Q..","...Q","Q...","..Q."]
        ret = []
        for i in state:
            string = '.' * i + 'Q' + '.' * (n - i - 1)
            ret.append(string)
        return ret
   */