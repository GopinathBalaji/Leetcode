class Solution {
public:
    int singleNumber(vector<int>& nums) {
//         Method 1: Hash Map (Linear runtime but not constant space)
        if(nums.size()==1){
            return nums[0];
        }
        unordered_map<int,int> m1;
         for(int i=0;i<nums.size();i++){
           if(!m1[nums[i]]){
               m1[nums[i]] = 1;
           }else{
               m1.erase(nums[i]);
           }
         }
        
        return m1.begin()->first;
    }
};

// Method 2: Math
//  2∗(a+b+c)−(a+a+b+b+c)=c
// Store values in set, sum all values in set and sum all values in original array.
// 2*(sum of set) - (sum of array) = answer
    /*
      public int singleNumber(int[] nums) {
    int sumOfSet = 0, sumOfNums = 0;
    Set<Integer> set = new HashSet();

    for (int num : nums) {
      if (!set.contains(num)) {
        set.add(num);
        sumOfSet += num;
      }
      sumOfNums += num;
    }
    return 2 * sumOfSet - sumOfNums;
  }
    */

// Method 3: Bit Manipulation using XOR
   // If we take XOR of zero and some bit, it will return that bit
   // a⊕0=a
   // If we take XOR of two same bits, it will return 0
   // a⊕a=0
   // a⊕b⊕a=(a⊕a)⊕b=0⊕b=b
   // So we can XOR all bits together to find the unique number.
   /*
   public int singleNumber(int[] nums) {
    int a = 0;
    for (int i : nums) {
      a ^= i;
    }
    return a;
  }
   */