<h2>1319. Number of Operations to Make Network Connected</h2><h3>Medium</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">There are&nbsp;<code style="user-select: auto;">n</code>&nbsp;computers numbered from&nbsp;<code style="user-select: auto;">0</code>&nbsp;to&nbsp;<code style="user-select: auto;">n-1</code>&nbsp;connected by&nbsp;ethernet cables&nbsp;<code style="user-select: auto;">connections</code>&nbsp;forming a network where&nbsp;<code style="user-select: auto;">connections[i] = [a, b]</code>&nbsp;represents a connection between computers&nbsp;<code style="user-select: auto;">a</code>&nbsp;and&nbsp;<code style="user-select: auto;">b</code>. Any computer&nbsp;can reach any other computer directly or indirectly through the network.</p>

<p style="user-select: auto;">Given an initial computer network <code style="user-select: auto;">connections</code>. You can extract certain cables between two directly connected computers, and place them between any pair of disconnected computers to make them directly connected. Return the <em style="user-select: auto;">minimum number of times</em> you need to do this in order to make all the computers connected. If it's not possible, return -1.&nbsp;</p>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:</strong></p>

<p style="user-select: auto;"><strong style="user-select: auto;"><img alt="" src="https://assets.leetcode.com/uploads/2020/01/02/sample_1_1677.png" style="width: 570px; height: 167px; user-select: auto;"></strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 4, connections = [[0,1],[0,2],[1,2]]
<strong style="user-select: auto;">Output:</strong> 1
<strong style="user-select: auto;">Explanation:</strong> Remove cable between computer 1 and 2 and place between computers 1 and 3.
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 2:</strong></p>

<p style="user-select: auto;"><strong style="user-select: auto;"><img alt="" src="https://assets.leetcode.com/uploads/2020/01/02/sample_2_1677.png" style="width: 660px; height: 167px; user-select: auto;"></strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 6, connections = [[0,1],[0,2],[0,3],[1,2],[1,3]]
<strong style="user-select: auto;">Output:</strong> 2
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 3:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 6, connections = [[0,1],[0,2],[0,3],[1,2]]
<strong style="user-select: auto;">Output:</strong> -1
<strong style="user-select: auto;">Explanation:</strong> There are not enough cables.
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 4:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 5, connections = [[0,1],[0,2],[3,4],[2,3]]
<strong style="user-select: auto;">Output:</strong> 0
</pre>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Constraints:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= n &lt;= 10^5</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= connections.length &lt;= min(n*(n-1)/2, 10^5)</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">connections[i].length == 2</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= connections[i][0], connections[i][1]&nbsp;&lt; n</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">connections[i][0] != connections[i][1]</code></li>
	<li style="user-select: auto;">There are no repeated connections.</li>
	<li style="user-select: auto;">No two computers are connected by more than one cable.</li>
</ul>
</div>