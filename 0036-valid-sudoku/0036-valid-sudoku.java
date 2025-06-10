class Solution {
    public boolean isValidSudoku(char[][] board) {
        Map<Character, List<List<Integer>>> map = new HashMap<>();
        Map<String, Set<Character>> boxCheck = new HashMap<>();

        for(int i=0;i<board.length;i++){
            for(int j=0;j<board[0].length;j++){
                char key = board[i][j];

                if(key == '.'){
                    continue;
                }

                int boxRow = i / 3;
                int boxCol = j / 3;
                String boxId = boxRow + "-" + boxCol;

                boxCheck.putIfAbsent(boxId, new HashSet<>());

                if(boxCheck.get(boxId).contains(key)){
                    return false;
                }

                boxCheck.get(boxId).add(key);

                if(map.containsKey(key)){
                    List<List<Integer>> positions= map.get(key);
                    for(List<Integer> pos: positions){
                        if(pos.get(0) == i){
                            return false;
                        }
                        if(pos.get(1) == j){
                            return false;
                        }
                    }
                }
                
                

                List<Integer> position = new ArrayList<>();
                position.add(i);
                position.add(j);

                map.putIfAbsent(key, new ArrayList<>());
                map.get(key).add(position);
            }
        }

        return true;
    }
}