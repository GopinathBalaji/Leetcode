// Same as Course Schedule 1 but we only PostOrder add nodes and return reverse list
/*
Why This Works
Post-order DFS ensures all dependencies of a course (neighbors) are handled before the course itself is added.

So postOrder gives a valid topological ordering if there's no cycle.
*/
class Solution {
    public List<Integer> postOrder = new ArrayList<>();

    public int[] findOrder(int numCourses, int[][] prerequisites) {
        HashMap<Integer, List<Integer>> graph = new HashMap<>();
        for(int i=0; i<prerequisites.length; i++){
            int from = prerequisites[i][1];
            int to = prerequisites[i][0];

            graph.putIfAbsent(from, new ArrayList<>());
            graph.get(from).add(to);
        }

        for(int i=0; i<numCourses; i++){
            graph.putIfAbsent(i, new ArrayList<>());
        }

        if(hasCycle(graph)){
            return new int[0];
        }

        Collections.reverse(postOrder);
        int[] result = new int[postOrder.size()];

        for(int i=0; i<postOrder.size(); i++){
            result[i] = postOrder.get(i);
        }

        return result;
    }

    public boolean hasCycle(HashMap<Integer, List<Integer>> graph){
        Set<Integer> visited = new HashSet<>();
        Set<Integer> recStack = new HashSet<>();

        for(int node: graph.keySet()){
            if(!visited.contains(node)){
                if(dfs(graph, node, visited, recStack)){
                    return true;
                }
            }
        }

        return false;
    }

    public boolean dfs(HashMap<Integer, List<Integer>> graph, int node, Set<Integer> visited, Set<Integer> recStack){
        visited.add(node);
        recStack.add(node);

        for(int neighbor: graph.getOrDefault(node, new ArrayList<>())){
            if(!visited.contains(neighbor)){
                if(dfs(graph, neighbor, visited, recStack)){
                    return true;
                }
            }else if(recStack.contains(neighbor)){
                return true;
            }
        }

        recStack.remove(node);
        postOrder.add(node);

        return false;
    }
}