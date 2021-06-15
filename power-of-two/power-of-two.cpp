class Solution {
public:
    bool isPowerOfTwo(int n) {
        if(n<=0){
            return false;
        }
        n = abs(n);
        int a = log(n)/log(2);
        if(pow(2,a)==n){
            return true;
        }
        return false;
    }
};