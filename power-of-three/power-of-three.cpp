class Solution {
public:
    bool isPowerOfThree(int n) {
        if(n<=0){
            return false;
        }
         n = abs(n);
        float a = log(n) / log(3);
        a = ceil(a);
        if(pow(3,a) == n){
            return true;
        }
        return false;
    }
};