// Backtracking with additional states (maintaining visited to prevent adding the same number again in the same permutation)
class Solution {
    public List<List<Integer>> permute(int[] nums) {
        List<List<Integer>> ans = new ArrayList<>();
        HashSet<Integer> visited = new HashSet<>();

        backtrack(nums, 0, visited, new ArrayList<Integer>(), ans);

        return ans;
    }

    private void backtrack(int[] nums, int index, HashSet<Integer> visited, List<Integer> permutations, List<List<Integer>> ans){
        if(permutations.size() == nums.length){
            ans.add(new ArrayList<>(permutations));

            return;
        }


        for(int i=0; i<nums.length; i++){
            if(visited.contains(nums[i])){
                continue;
            }

            permutations.add(nums[i]);
            visited.add(nums[i]);

            backtrack(nums, index + 1, visited, permutations, ans);

            permutations.remove(permutations.size() - 1);
            visited.remove(nums[i]);
        }
    }
}