class Solution {
public:
    bool isPowerOfFour(int n) {
        if(n<=0){
            return false;
        }
        int a = log(n)/log(4);
        a = ceil(a);
        if(pow(4,a)==n){
            return true;
        }
        return false;
    }
};