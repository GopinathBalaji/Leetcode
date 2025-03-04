class Solution(object):
    def searchRange(self, nums, target):
        """
        :type nums: List[int]
        :type target: int
        :rtype: List[int]
        """
        
        def binary_search(nums, target, search_left):
            left = 0
            right = len(nums) - 1
            idx = -1

            while left <= right:
                mid = (right+left) >> 1

                if nums[mid] > target:
                    right = mid - 1
                elif nums[mid] < target:
                    left = mid + 1
                else:
                    idx = mid
                    if search_left:
                        right = mid - 1
                    else:
                        left = mid + 1
            return idx

        left = binary_search(nums,target,True)
        right = binary_search(nums,target,False)

        return [left,right]


