class Solution {
public:
    bool isPerfectSquare(int num) {
        long int lo=1,high =num;
        long int mid;
        
        while(lo<=high){
            mid = lo-(lo-high)/2;
            if(mid*mid==num){
                return true;
            }
           else if(mid*mid<num){
               lo = mid+1;
           }else{
               high = mid - 1;
           }
        }
        
        return false;
    }
};