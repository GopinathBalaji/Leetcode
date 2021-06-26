class Solution {
public:
    int mySqrt(int x) {
//      Method 1: The sqr root will lie within 1 to x. Search efficiently for this
//        number using binary search
    
        int ans = sqrtSearch(0,x,x);
        return ans;        
    }
    
   int sqrtSearch(int low, int high, int N)
{
 
    if (low <= high) {
        long int mid = (low + high) / 2;

        if ((mid * mid <= N)
            && ((mid + 1) * (mid + 1) > N)) {
            return mid;
        }
        else if (mid * mid < N) {
            return sqrtSearch(mid + 1, high, N);
        }
        else {
            return sqrtSearch(low, mid - 1, N);
        }
    }
    return low;
}
};

// Method 2: Binary Search without recursion. 
 // We can find a value between 1 and x such that value x value is just greater than x
//The result will be value-1
   /*
   
   long int getpow(long int mid){
        
        return mid*mid;
    }
    
    int mySqrt(long int x) {
        
        long int lo=1;
        long int hi=x;
        long int mid;
        
        if (x==1){
            return 1;
        }
        
        while(hi>lo){
            mid=lo+(hi-lo)/2;
            if(getpow(mid)>x){
                hi=mid;
            }
            else{
                lo=mid+1;
            }
        }
        return lo-1;
        
    }
   */