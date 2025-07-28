// Use Backtracking with deduplication 
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
        List<List<Integer>> ans = new ArrayList<>();

        backtrack(candidates, target, 0, new ArrayList<>(), ans, 0);

        return ans;
    }

    public void backtrack(int[] candidates, int target, int sum, List<Integer> values, List<List<Integer>> ans, int startIndex){
        if(sum == target){
            ans.add(new ArrayList<>(values));

            return;
        }


        for(int i=startIndex; i<candidates.length; i++){
            if(sum + candidates[i] <= target){
                values.add(candidates[i]);
                backtrack(candidates, target, sum + candidates[i], values, ans, i);
                values.remove(values.size() - 1);
            }else{
                continue;
            }
        }
    }
}



// Method 2: Same backtracking with depulication but a different method

// class Solution {
//     public void dfs(List<List<Integer>> res, List<Integer> path, int currsum, int startidx, int[] candidates, int target){
//         if(currsum == target){
//             res.add(new ArrayList<>(path));
//             return;
//         }

//         if(currsum < target){
//             for(int i=startidx;i<candidates.length;i++){
//                 currsum += candidates[i];
//                 if(currsum > target){
//                     break;
//                 }
//                 path.add(candidates[i]);
//                 dfs(res,path,currsum,i,candidates,target);
//                 path.remove(path.size() - 1);
//                 currsum -= candidates[i];
//             }
//         }
//     }

//     public List<List<Integer>> combinationSum(int[] candidates, int target){
//         List<List<Integer>> res = new ArrayList<>();
//         Arrays.sort(candidates);
//         dfs(res, new ArrayList<>(), 0, 0, candidates, target);
//         return res;
//     }
// }