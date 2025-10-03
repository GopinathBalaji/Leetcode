// Using BFS to find the smallest number of mutations
/*
Why this works:
BFS guarantees the first time you reach endGene is the minimum mutations.
We generate neighbors by changing exactly one character to one of A,C,G,T.
We only enqueue genes that are in the bank and not yet visited.
We mark on enqueue to avoid duplicates.

Quick walkthrough:
start=“AACCGGTT”, end=“AAACGGTA”, bank contains intermediate valid genes.
Start with (AACCGGTT, 0). Generate all one-step valid neighbors in bank, enqueue with 1.
Continue level by level until end is popped → return its distance.
*/
class Solution {

    static class Pair{
        String gene;
        int mutations;
        Pair() {};
        Pair(String gene, int mutations){
            this.gene = gene;
            this.mutations = mutations;
        }
    }

    public int minMutation(String startGene, String endGene, String[] bank) {
        if (startGene.equals(endGene)) return 0;

        Set<String> allowed = new HashSet<>(Arrays.asList(bank));
        if (!allowed.contains(endGene)) return -1; // impossible

        char[] letters = {'A','C','G','T'};

        Deque<Pair> q = new ArrayDeque<>();
        q.offer(new Pair(startGene, 0));

        // mark visited by removing from allowed OR use a separate visited set
        // start may not be in bank; use a dedicated visited set instead:
        Set<String> visited = new HashSet<>();
        visited.add(startGene);

        while (!q.isEmpty()) {
            Pair cur = q.poll();
            String gene = cur.gene;
            int steps = cur.mutations;

            if (gene.equals(endGene)) return steps;

            char[] arr = gene.toCharArray(); // base for neighbors this level
            for (int i = 0; i < arr.length; i++) {
                char original = arr[i];
                for (char c : letters) {
                    if (c == original) continue;
                    arr[i] = c;
                    String next = new String(arr);
                    if (!visited.contains(next) && allowed.contains(next)) {
                        visited.add(next);           // mark on enqueue
                        q.offer(new Pair(next, steps + 1));
                    }
                }
                arr[i] = original; // restore for next position
            }
        }
        return -1;
    }
}