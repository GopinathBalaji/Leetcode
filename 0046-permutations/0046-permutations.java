class Solution {

    public void dfs(Integer startIndex, List<List<Integer>> res, int[] nums, List<Integer> path, boolean[] used){
        if(nums.length == path.size()){
            List<Integer> li = new ArrayList<>(path);
            res.add(li);
            return;
        }

        for(int i=0;i<nums.length;i++){
            if(used[i]){
                continue;
            }

            path.add(nums[i]);
            used[i] = true;
            dfs(startIndex + 1, res, nums, path, used);
            path.remove(path.size() - 1);
            used[i] = false;
        }
    }

    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> res = new ArrayList<>();
        dfs(0, res, nums, new ArrayList<>(), new boolean[nums.length]);
        return res;
    }
}