class Solution {
    private final List<List<Integer>> combinations = new ArrayList<>();
    private final List<Integer> currentcombi = new ArrayList<>();
    private int totalnums;
    private int combisize;


    public void dfs(int currnum){
        if(currentcombi.size() == combisize){
            combinations.add(new ArrayList<>(currentcombi));
            return;
        }    

        if(currnum > totalnums){
            return;
        }

        currentcombi.add(currnum);
        dfs(currnum + 1);

        currentcombi.remove(currentcombi.size() - 1);
        dfs(currnum + 1);
    }


    public List<List<Integer>> combine(int n, int k) {
        totalnums = n;
        combisize = k;
        dfs(1);

        return combinations;
    }

}




// function dfs(start_index, path):
//   if is_leaf(start_index):
//     report(path)
//     return
//   for edge in get_edges(start_index):
//     path.add(edge)
//     dfs(start_index + 1, path)
//     path.pop()