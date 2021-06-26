class Solution {
public:
    vector<int> plusOne(vector<int>& digits) {
        int right = digits.size()-1;
        int left = 0;
        while(left<=right){
            if(digits[right]==9){
                digits[right]=0;
                right--;
            }else{
                digits[right]++;
                return digits;
            }
        }
        digits[0]=1;
        digits.push_back(0);
        return digits;
    }
};