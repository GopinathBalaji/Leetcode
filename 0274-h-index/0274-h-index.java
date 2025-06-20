// If citations[i] is at least (n - i), then you have at least (n - i) papers with ≥ (n - i) citations, so your h-index is (n - i).
class Solution {
    public int hIndex(int[] citations) {
        Arrays.sort(citations);
        int n = citations.length;
        for(int i=0;i<citations.length;i++){
            if(citations[i] >= n-i){
                return n-i;
            }
        }
        return 0;
    }
}
// ///////////////////////////////////////////

// Method 2: O(n) Counting Sort based solution but with extra space
// class Solution {
//     public int hIndex(int[] citations) {
//         int n = citations.length;

//         int[] bucket = new int[n+1];

//         for(int c:citations){
//             if(c >= n){
//                 bucket[n]++;
//             }else{
//                 bucket[c]++;
//             }
//         }

//         int papersSoFar = 0;
//         for(int h=n;h>=0;h--){
//             papersSoFar += bucket[h];

//             if(papersSoFar >= h){
//                 return h;
//             }
//         }

//         return 0;
//     }
// }

// Why buckets of size n+1?

// The h-index can never exceed the total number of papers n.

// Any paper with more than n citations can only contribute to an h-index of at most n, so we lump all counts > n into the final bucket at index n.

// Building the bucket array

// We make a single pass over citations[].

// If citations[i] >= n, increment bucket[n]. Otherwise increment bucket[citations[i]].

// This costs O(n) time and O(n) extra space.

// Finding the largest valid h

// We want the largest h such that at least h papers have ≥ h citations.

// Starting from the top bucket h = n, maintain a running sum papersSoFar of how many papers have citation‐counts ≥ current h.

// Initially papersSoFar = bucket[n].

// Then for h = n-1 down to 0, we add bucket[h] each time.

// As soon as papersSoFar >= h, we know there are at least h papers whose citation‐counts are all ≥ h. Because we’re scanning from high to low, the first time this is true gives us the maximum valid h.
