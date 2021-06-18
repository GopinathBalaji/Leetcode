class NumArray {
public:
    vector<int> vec;
    NumArray(vector<int>& nums) {
        vec = nums;
    }
    
    int sumRange(int left, int right) {
          int sum = 0;
          while(left<=right){
              sum += vec[left];
              left++;
          }
        return sum;
    }
};

/**
 * Your NumArray object will be instantiated and called as such:
 * NumArray* obj = new NumArray(nums);
 * int param_1 = obj->sumRange(left,right);
 */