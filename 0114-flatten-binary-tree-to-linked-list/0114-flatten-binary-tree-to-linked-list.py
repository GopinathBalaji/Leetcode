# Definition for a binary tree node.
# class TreeNode(object):
#     def __init__(self, val=0, left=None, right=None):
#         self.val = val
#         self.left = left
#         self.right = right
class Solution(object):
    def __init__(self):
        self.prev = None

    def flatten(self, root):
        """
        :type root: Optional[TreeNode]
        :rtype: None Do not return anything, modify root in-place instead.
        """
        if not root:
            return


        self.flatten(root.right)
        self.flatten(root.left)

        root.right = self.prev
        root.left = None

        self.prev = root

        
####################################################################

# Method 2 : Iterative O(1)

# def flatten(self, root: TreeNode) -> None:
#     curr = root

#     while curr:
#         if curr.left != None:
#             p = curr.left
#             while p.right != None:
#                 p = p.right

#             p.right = curr.right

#             curr.right = curr.left
#             curr.left = None

#         curr = curr.right
        