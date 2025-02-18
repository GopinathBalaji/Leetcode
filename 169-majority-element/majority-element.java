class Solution {
    public int majorityElement(int[] nums) {
    //     HashMap<Integer,Integer> map = new HashMap<>();
    //     for(int num: nums){
    //         if(map.containsKey(num)){
    //             map.put(num, map.get(num)+1);
    //         }
    //         else{
    //             map.put(num, 1);
    //         }
    //     }

    //     int maximum = 0;
    //     int maxele = nums[0];

    //    for(int num: nums){
    //     if (map.get(num) > maximum) {
    //             maxele = num;
    //             maximum = map.get(num);
    //         }
    //    }

    //    return maxele; 



    // Moore Voting Algorithm
    int count = 0;
    int candidate = 0;

    for(int num: nums){
        if(count == 0){
            candidate = num;
        }

        if(num == candidate){
            count++;
        }
        else{
            count--;
        }
    }

    return candidate;

    }
}