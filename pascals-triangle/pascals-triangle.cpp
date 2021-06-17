class Solution {
public:
    vector<vector<int>> generate(int numRows) {
//         BOTTOM-UP APPROACH
       vector<vector<int>> ans;
        vector<int> a;
        a.push_back(1);
        ans.push_back(a);
        for(int i=1; i<numRows; i++) {
            vector<int> p;
            p.push_back(1);
            for(int j=1; j<=i-1; j++) {
                p.push_back(ans[i-1][j-1]+ans[i-1][j]);
            }
            p.push_back(1);
            ans.push_back(p);
        }
        return ans;
    }
};


/*
EXPLANATION OF TOP-DOWN AND BOTTOM-UP  (JAVA)

The first thing to realize is that this is a recursive problem because there is a recurrence relation. So we should first try to solve the sub-problem, which is to find the i-th row and j-th column of the triangle. We begin by finding the base case and recursive case, then work our way through to see if this problem is optimizable.

Base Case
We can begin by defining a function f(i, j) which returns the i-th row and j-th column of the pascal's triangle. We see from the triangle's pattern that the beginning and end of the triangle always has values of 1. So it follows that f(i, j) = 1 when j = 1 or j = i.

Recursive Case
We also know from the description that the current i-th row and j-th column is the sum of the two values above in the previous row. This gives: f(i, j) = f(i - 1, j - 1) + f(i - 1, j)

Top-Down
Given these two cases, it is now easy to write a top-down solution to solve the sub-problem of finding f(i, j).

public int generateNum(int i, int j) {
	if (j == 0 || j == i) {  // base case
		return 1;
	} else { // recursive case
		int a = generateNum(i - 1, j - 1);
		int b = generateNum(i - 1, j);
		return a + b;
	}
}
If we try this on a few examples, we can see that this will be highly inefficient because we start from scrach and recursiveley calculate all the two summed values above the triangle for each f(i, j), resulting in many repeated calculations. Instead of working our way from the top of the array to the bottom, we can work in reverse. In terms of the triangle, this would be working our way down (sorry if this is confusing).

We have to realize that the previous rows can be stored and used to calculate the next row, rather than recalculating them every time we want a new f(i, j). We will assume that we have already calculated and stored each of the previous row values already in our triangle so we don't have to recalculate them each time. Essentially, we are going up the list by calculating the values in the current row through adding the values in the previous row. This is the bottom-up approach:

Bottom-Up
public int generateNum(List<List<Integer>> triangle, int i, int j) {
	if (j == 0 || j == i || i < 2) { 
		return 1;
	} else {
		int a = triangle.get(i - 1).get(j - 1);
		int b = triangle.get(i - 1).get(j);
		return a + b;
	}
}
Now that we have solved the sub-problem, we can easily use this method to solve the main problem.

Recursive Solution
We can apply the same way of finding the recursive and base case to create a recursive solution that solves and adds each row to the triangle.

public List<List<Integer>> generate(int numRows) {
	List<List<Integer>> triangle = new ArrayList<>();
	generateRow(0, numRows, triangle);
	return triangle;
}

public void generateRow(int i, int numRows, List<List<Integer>> triangle) {
	if (i >= numRows) {
		return;
	} else {
		List<Integer> row = new ArrayList<>();
		for (int j = 0; j <= i; j++) {
			row.add(generateNum(triangle, i, j));
		}
		triangle.add(row);
		generateRow(i + 1, numRows, triangle);
	}
}

public int generateNum(List<List<Integer>> triangle, int i, int j) {
	if (j == 0 || j == i || i < 2) { 
		return 1;
	} else {
		int a = triangle.get(i - 1).get(j - 1);
		int b = triangle.get(i - 1).get(j);
		return a + b;
	}
}
Iterative Solution
We can also easily, change this into an iterative solution by using a loop. This solution is asymptotically the same, but does not have a recursion stack. The recursive solution might be a bit easier to read.

public List<List<Integer>> generate(int numRows) {
	List<List<Integer>> triangle = new ArrayList<>();
	for (int i = 0; i < numRows; i++) {
		List<Integer> row = new ArrayList<>();
		for (int j = 0; j <= i; j++) {
			row.add(generateNum(triangle, i, j));
		}
		triangle.add(row);
	}
	return triangle;
}
    
public int generateNum(List<List<Integer>> triangle, int i, int j) {
	if (j == 0 || j == i || i < 2) { 
		return 1;
	} else {
		int a = triangle.get(i - 1).get(j - 1);
		int b = triangle.get(i - 1).get(j);
		return a + b;
	}
}
Time and Space Analysis
We should analyze the time based on the input numRows, so we will use n as numRows.

Top down solution:
We can see that the recurrence is approximately T(n) = 2T(n - 1) + 1 because there are 2 recursive calls where it goes up the row by one on each call until n = 1 where T(1) = 1.
As we know, this means it is exponential time or approximatley θ(2^n). Working out for anyone interested for. (c is a constant):

T(n) = 2T(n - 1) + c
T(n) = 2(2T(n−2)+c)+c
T(n) = 2(2(2T(n−2)+c)+c)+c
...
T(n) = (2^n)T(0) + c(2^n-c)
= θ(2^n)

Recursive and Iterative Solutions
We see that each it generates each row in the triangle through sums of two values in the previous row. It does not need to recalculate it's results unlike the top down approach. Each row requires about c(n - 1) time because there are n - 1 values in the previous row. There are n rows in the triangle.

= θ(n^2).

Space Complexity: θ(n^2)
There are n - 1 + n - 2 + n - 3 ... + 1 values stored.
With Gauss Sum:
n (n + 1) / 2
= θ(n^2)
*/