# Definition for a binary tree node.
# class TreeNode(object):
#     def __init__(self, val=0, left=None, right=None):
#         self.val = val
#         self.left = left
#         self.right = right
class Solution(object):
    def buildTree(self, inorder, postorder):
        """
        :type inorder: List[int]
        :type postorder: List[int]
        :rtype: Optional[TreeNode]
        """

        def build(inorder, postorder):
            if inorder:
                idx = inorder.index(postorder.pop())
                root  = TreeNode(inorder[idx])

                root.right = build(inorder[idx+1:], postorder)
                root.left = build(inorder[:idx], postorder)
                

                return root

        return build(inorder, postorder)
        