/*
// Definition for a Node.
class Node {
    public int val;
    public Node left;
    public Node right;
    public Node next;

    public Node() {}
    
    public Node(int _val) {
        val = _val;
    }

    public Node(int _val, Node _left, Node _right, Node _next) {
        val = _val;
        left = _left;
        right = _right;
        next = _next;
    }
};
*/

// BFS with extra space using Queue
// class Solution {
//     public Node connect(Node root) {
//         if(root == null){
//             return root;
//         }

//         Queue<Node> queue = new LinkedList<>();

//         queue.offer(root);

//         while(!queue.isEmpty()){
//             int size = queue.size();

//             Node prev = null;
//             for(int i=0; i<size; i++){
//                 Node curr = queue.poll();
//                 if(prev != null){
//                     prev.next = curr;
//                 }
//                 prev = curr;

//                 if(curr.left != null){
//                     queue.offer(curr.left);
//                 }

//                 if(curr.right != null){
//                     queue.offer(curr.right);
//                 }
//             }
//         }

//         return root;
//     }
// }



// BFS without extra space for Queue (using pointers)
// Use three pointers: head, prev, curr
// head is the first child, prev is the last node and
// curr is the current node.
/*
Step-by-Step Process
Start at root (this is the first level).

Initialize head = null and prev = null for the next level.

Traverse the current level using curr = curr.next:

For each child (curr.left then curr.right):

If head is null, this child is the first node of the next level → set head = child.

If prev exists, connect prev.next = child.

Move prev to this child.

When the current level is done (curr == null):

Move curr = head (go to the next level).

Reset head = null and prev = null.

Repeat until curr is null.
*/
class Solution {
    public Node connect(Node root) {
        if (root == null) return null;

        Node curr = root;  // Current level pointer

        while (curr != null) {
            Node head = null; // First node of the next level
            Node prev = null; // The last connected node in the next level

            // Traverse the current level using next pointers
            while (curr != null) {
                // Process the left child
                if (curr.left != null) {
                    if (head == null) head = curr.left; // First node of next level
                    if (prev != null) prev.next = curr.left;
                    prev = curr.left;
                }

                // Process the right child
                if (curr.right != null) {
                    if (head == null) head = curr.right; // First node of next level
                    if (prev != null) prev.next = curr.right;
                    prev = curr.right;
                }

                // Move horizontally in the current level
                curr = curr.next;
            }

            // Move to the next level
            curr = head;
        }

        return root;
    }
}


// Recursive DFS Approach
/*
DFS Idea
DFS visits nodes top-down.

For each node:

Connect its left child to its right child if both exist.
If the right child is missing, connect left child to the first available child in the node’s next chain.
Similarly, if left is missing, process right to find its next neighbor.
Recurse into right subtree first, then left subtree.

Why Right First?
We need the next pointers of the current level to be fully established before connecting children of the next level.
Processing the right first ensures that next pointers on the right side are ready when left children try to connect.
*/
// class Solution {
//     public Node connect(Node root) {
//         if (root == null) return null;
//         dfs(root);
//         return root;
//     }

//     private void dfs(Node node) {
//         if (node == null) return;

//         // Connect children
//         if (node.left != null) {
//             // If right child exists, connect left -> right
//             if (node.right != null) {
//                 node.left.next = node.right;
//             } else {
//                 // Otherwise, connect left -> first child in node.next chain
//                 node.left.next = findNext(node.next);
//             }
//         }

//         if (node.right != null) {
//             // Connect right -> first child in node.next chain
//             node.right.next = findNext(node.next);
//         }

//         // DFS right first to ensure next pointers are ready for left
//         dfs(node.right);
//         dfs(node.left);
//     }

//     // Find the next available child for a given node's next chain
//     private Node findNext(Node node) {
//         while (node != null) {
//             if (node.left != null) return node.left;
//             if (node.right != null) return node.right;
//             node = node.next;
//         }
//         return null;
//     }
// }