<h2>1514. Path with Maximum Probability</h2><h3>Medium</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">You are given an undirected weighted graph of&nbsp;<code style="user-select: auto;">n</code>&nbsp;nodes (0-indexed), represented by an edge list where&nbsp;<code style="user-select: auto;">edges[i] = [a, b]</code>&nbsp;is an undirected edge connecting the nodes&nbsp;<code style="user-select: auto;">a</code>&nbsp;and&nbsp;<code style="user-select: auto;">b</code>&nbsp;with a probability of success of traversing that edge&nbsp;<code style="user-select: auto;">succProb[i]</code>.</p>

<p style="user-select: auto;">Given two nodes&nbsp;<code style="user-select: auto;">start</code>&nbsp;and&nbsp;<code style="user-select: auto;">end</code>, find the path with the maximum probability of success to go from&nbsp;<code style="user-select: auto;">start</code>&nbsp;to&nbsp;<code style="user-select: auto;">end</code>&nbsp;and return its success probability.</p>

<p style="user-select: auto;">If there is no path from&nbsp;<code style="user-select: auto;">start</code>&nbsp;to&nbsp;<code style="user-select: auto;">end</code>, <strong style="user-select: auto;">return&nbsp;0</strong>. Your answer will be accepted if it differs from the correct answer by at most <strong style="user-select: auto;">1e-5</strong>.</p>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:</strong></p>

<p style="user-select: auto;"><strong style="user-select: auto;"><img alt="" src="https://assets.leetcode.com/uploads/2019/09/20/1558_ex1.png" style="width: 187px; height: 186px; user-select: auto;"></strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 3, edges = [[0,1],[1,2],[0,2]], succProb = [0.5,0.5,0.2], start = 0, end = 2
<strong style="user-select: auto;">Output:</strong> 0.25000
<strong style="user-select: auto;">Explanation:</strong>&nbsp;There are two paths from start to end, one having a probability of success = 0.2 and the other has 0.5 * 0.5 = 0.25.
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 2:</strong></p>

<p style="user-select: auto;"><strong style="user-select: auto;"><img alt="" src="https://assets.leetcode.com/uploads/2019/09/20/1558_ex2.png" style="width: 189px; height: 186px; user-select: auto;"></strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 3, edges = [[0,1],[1,2],[0,2]], succProb = [0.5,0.5,0.3], start = 0, end = 2
<strong style="user-select: auto;">Output:</strong> 0.30000
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 3:</strong></p>

<p style="user-select: auto;"><strong style="user-select: auto;"><img alt="" src="https://assets.leetcode.com/uploads/2019/09/20/1558_ex3.png" style="width: 215px; height: 191px; user-select: auto;"></strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 3, edges = [[0,1]], succProb = [0.5], start = 0, end = 2
<strong style="user-select: auto;">Output:</strong> 0.00000
<strong style="user-select: auto;">Explanation:</strong>&nbsp;There is no path between 0 and 2.
</pre>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Constraints:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">2 &lt;= n &lt;= 10^4</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= start, end &lt; n</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">start != end</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= a, b &lt; n</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">a != b</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= succProb.length == edges.length &lt;= 2*10^4</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= succProb[i] &lt;= 1</code></li>
	<li style="user-select: auto;">There is at most one edge between every two nodes.</li>
</ul></div>