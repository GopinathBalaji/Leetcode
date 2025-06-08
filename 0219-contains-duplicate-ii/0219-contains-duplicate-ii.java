class Solution {
    public boolean containsNearbyDuplicate(int[] nums, int k) {
        Map<Integer, List<Integer>> map = new HashMap<>();

        for(int i=0;i<nums.length;i++){
            map.putIfAbsent(nums[i], new ArrayList<>());

            map.get(nums[i]).add(i);

            if(map.get(nums[i]).size() > 1){
                List<Integer> list = map.get(nums[i]);
                int size = list.size();
                if(Math.abs(list.get(size - 1) - list.get(size - 2)) <= k){
                    return true;
                }
            }
        }

        return false;
    }
}

// Better version, check only current index with the last seen index
// Map<Integer, Integer> map = new HashMap<>();
// for (int i = 0; i < nums.length; i++) {
//     if (map.containsKey(nums[i]) && i - map.get(nums[i]) <= k) {
//         return true;
//     }
//     map.put(nums[i], i);
// }
// return false;
