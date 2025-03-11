# Method 1 - Without Hashmap (O(n^2))

# Definition for a binary tree node.
# class TreeNode(object):
#     def __init__(self, val=0, left=None, right=None):
#         self.val = val
#         self.left = left
#         self.right = right
class Solution(object):
    def buildTree(self, preorder, inorder):
        """
        :type preorder: List[int]
        :type inorder: List[int]
        :rtype: Optional[TreeNode]
        """
        preorder = deque(preorder)

        def build(preorder, inorder):
            if inorder:
                idx = inorder.index(preorder.popleft())
                root = TreeNode(inorder[idx])

                root.left = build(preorder, inorder[:idx])
                root.right = build(preorder, inorder[idx+1:])

                return root


        return build(preorder,inorder)


###########################################################################################

# Method 2 - Using HashMap (O(n))

# Definition for a binary tree node.
# class TreeNode(object):
#     def __init__(self, val=0, left=None, right=None):
#         self.val = val
#         self.left = left
#         self.right = right
# class Solution(object):
#     def buildTree(self, preorder, inorder):
#         """
#         :type preorder: List[int]
#         :type inorder: List[int]
#         :rtype: Optional[TreeNode]
#         """

#         mapping = {}

#         for i in range(len(inorder)):
#             mapping[inorder[i]] = i
        
#         preorder = deque(preorder)

#         def build(start, end):
#             if start > end:
#                 return None
            
#             root = TreeNode(preorder.popleft())
#             mid = mapping[root.val]

#             root.left = build(start, mid-1)
#             root.right = build(mid+1, end)

#             return root

#         return build(0, len(preorder)-1)



