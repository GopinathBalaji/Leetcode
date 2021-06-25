class Solution {
public:
    int removeElement(vector<int>& nums, int val) {
        vector<int>::iterator it;
        while(count(nums.begin(),nums.end(),val)!=0){
         it = find(nums.begin(),nums.end(),val);
            nums.erase(it);
        }
       
        return nums.size();
    }
};

// Method 2: Slow and Fast Pointers
   /*
   public int removeElement(int[] nums, int val) {
    int i = 0;
    for (int j = 0; j < nums.length; j++) {
        if (nums[j] != val) {
            nums[i] = nums[j];
            i++;
        }
    }
    return i;
}
   */

// Method 3: Two Pointer but swapping with last element
   /*
    public int removeElement(int[] nums, int val) {
    int i = 0;
    int n = nums.length;
    while (i < n) {
        if (nums[i] == val) {
            nums[i] = nums[n - 1];
            // reduce array size by one
            n--;
        } else {
            i++;
        }
    }
    return n;
}
   */