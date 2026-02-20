// Method 1: Using maxHeap / PriorityQueue
/*
*/
class Solution {
    public int lastStoneWeight(int[] stones) {
        PriorityQueue<Integer> maxHeap = new PriorityQueue<>(Comparator.reverseOrder());

        for(int stone: stones){
            maxHeap.add(stone);
        }

        while(maxHeap.size() > 1){
            int largest = maxHeap.poll();
            int secondLargest = maxHeap.poll();

            if(largest - secondLargest != 0){
                maxHeap.add(largest - secondLargest);
            }
        }


        return maxHeap.size() == 0 ? 0 : maxHeap.poll();
    }
}