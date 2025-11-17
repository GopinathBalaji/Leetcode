// Method 1: Backtracking
/*
To Depulicate:
Establish order and only use candidate numbers whose 
index in the array is >= last used number's index. This ensures 
you don’t recombine in different orders.

In the code below: Why i, not i + 1?
Because you're allowed to use the same number multiple times in the 
combination (per problem rules), you pass the same i again.
*/
class Solution {
    public List<List<Integer>> combinationSum(int[] candidates, int target) {
        List<Integer> candi = new ArrayList<>();
        List<List<Integer>> ans = new ArrayList<>();

        backtrack(candidates, target, candi, ans, 0, 0);
        
        return ans;
    }

    public void backtrack(int[] candidates, int target, List<Integer> candi, List<List<Integer>> ans, int sum, int idx){
        if(sum > target || idx >= candidates.length){
            return;
        }
        if(sum == target && idx < candidates.length){
            ans.add(new ArrayList<>(candi));
            return;
        }


        for(int i=idx; i<candidates.length; i++){
            candi.add(candidates[i]);
            sum += candidates[i];

            backtrack(candidates, target, candi, ans, sum, i);

            candi.remove(candi.size() - 1);
            sum -= candidates[i];
        }

        return;
    }
}