/** 
 * Forward declaration of guess API.
 * @param  num   your guess
 * @return 	     -1 if num is lower than the guess number
 *			      1 if num is higher than the guess number
 *               otherwise return 0
 * int guess(int num);
 */

class Solution {
public:
    int guessNumber(int n) {
       long int lo=1,high=n;
        long int mid;
        while(lo<=high){
            mid = lo-(lo-high)/2;
            if(!guess(mid)){
                return mid;
            }else if(guess(mid)==-1){
                high = mid-1;
            }else{
                lo = mid+1; 
            }
        }
        return 0;
    }
};