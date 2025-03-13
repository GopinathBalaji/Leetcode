"""
# Definition for a Node.
class Node(object):
    def __init__(self, val=0, left=None, right=None, next=None):
        self.val = val
        self.left = left
        self.right = right
        self.next = next
"""

class Solution(object):
    def connect(self, root):
        """
        :type root: Node
        :rtype: Node
        """
        if not root:
            return

        queue = deque([root,None])

        while queue:
            node  = queue.popleft()

            if node is None:
                if queue:
                    queue.append(None)
                continue
            

            currHead = queue[0]

            if currHead is None:
                node.next = None
            else:
                node.next = currHead

            if node.left:
                queue.append(node.left)
            if node.right:
                queue.append(node.right)

            
        
        return root


        