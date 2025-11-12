class Solution {
    public boolean isPossibleDivide(int[] nums, int k) {
        int n = nums.length;
        if (n % k != 0) return false;
        if (k == 1) return true;

        TreeMap<Integer, Integer> map = new TreeMap<>();
        for (int card : nums) {
            map.put(card, map.getOrDefault(card, 0) + 1);
        }

        while (!map.isEmpty()) {
            int start = map.firstKey();      // smallest remaining
            int c = map.get(start);          // how many groups must start here

            // form c groups: [start, start+1, ..., start+groupSize-1]
            for (int v = start; v < start + k; v++) {
                Integer cnt = map.get(v);
                if (cnt == null || cnt < c) return false; // gap or not enough
                if (cnt == c) {
                    map.remove(v);
                } else {
                    map.put(v, cnt - c);
                }
            }
        }

        return true;
    }
}