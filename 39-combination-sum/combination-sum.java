class Solution {
    public void dfs(List<List<Integer>> res, List<Integer> path, int currsum, int startidx, int[] candidates, int target){
        if(currsum == target){
            res.add(new ArrayList<>(path));
            return;
        }

        if(currsum < target){
            for(int i=startidx;i<candidates.length;i++){
                currsum += candidates[i];
                if(currsum > target){
                    break;
                }
                path.add(candidates[i]);
                dfs(res,path,currsum,i,candidates,target);
                path.remove(path.size() - 1);
                currsum -= candidates[i];
            }
        }
    }

    public List<List<Integer>> combinationSum(int[] candidates, int target){
        List<List<Integer>> res = new ArrayList<>();
        Arrays.sort(candidates);
        dfs(res, new ArrayList<>(), 0, 0, candidates, target);
        return res;
    }
}