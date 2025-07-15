// // BFS (Level order Graph traversal) (Same as Snakes and Ladders question)
class Solution {
    public int minMutation(String startGene, String endGene, String[] bank) {
        HashSet<String> geneBank = new HashSet<>(Arrays.asList(bank));
        if (!geneBank.contains(endGene)) return -1;  // Optimization

        Queue<String> queue = new LinkedList<>();
        HashSet<String> visited = new HashSet<>();

        char[] characters = {'A', 'T', 'G', 'C'};
        int count = 0;
        queue.add(startGene);
        visited.add(startGene);

        while (!queue.isEmpty()) {
            int size = queue.size();

            // Level-order BFS traversal
            for (int i = 0; i < size; i++) {
                String gene = queue.poll();

                for (int j = 0; j < gene.length(); j++) {
                    for (char letter : characters) {
                        if (letter == gene.charAt(j)) continue;  //  skip redundant mutation

                        StringBuilder sb = new StringBuilder(gene);
                        sb.setCharAt(j, letter);
                        String mutated = sb.toString();

                        if (mutated.equals(endGene)) {
                            return count + 1;
                        }

                        if (geneBank.contains(mutated) && !visited.contains(mutated)) {
                            queue.add(mutated);
                            visited.add(mutated);
                        }
                    }
                }
            }

            count++;
        }

        return -1;
    }
}
