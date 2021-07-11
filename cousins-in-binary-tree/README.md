<h2>993. Cousins in Binary Tree</h2><h3>Easy</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">In a binary tree, the root node is at depth <code style="user-select: auto;">0</code>, and children of each depth <code style="user-select: auto;">k</code> node are at depth <code style="user-select: auto;">k+1</code>.</p>

<p style="user-select: auto;">Two nodes of a binary tree are <em style="user-select: auto;">cousins</em> if they have the same depth, but have <strong style="user-select: auto;">different parents</strong>.</p>

<p style="user-select: auto;">We are given the <code style="user-select: auto;">root</code> of a binary tree with unique values, and the values <code style="user-select: auto;">x</code>&nbsp;and <code style="user-select: auto;">y</code>&nbsp;of two different nodes in the tree.</p>

<p style="user-select: auto;">Return&nbsp;<code style="user-select: auto;">true</code>&nbsp;if and only if the nodes corresponding to the values <code style="user-select: auto;">x</code> and <code style="user-select: auto;">y</code> are cousins.</p>

<p style="user-select: auto;">&nbsp;</p>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:<br style="user-select: auto;">
<img alt="" src="https://assets.leetcode.com/uploads/2019/02/12/q1248-01.png" style="width: 180px; height: 160px; user-select: auto;"></strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input: </strong>root = <span id="example-input-1-1" style="user-select: auto;">[1,2,3,4]</span>, x = <span id="example-input-1-2" style="user-select: auto;">4</span>, y = <span id="example-input-1-3" style="user-select: auto;">3</span>
<strong style="user-select: auto;">Output: </strong><span id="example-output-1" style="user-select: auto;">false</span>
</pre>

<div style="user-select: auto;">
<p style="user-select: auto;"><strong style="user-select: auto;">Example 2:<br style="user-select: auto;">
<img alt="" src="https://assets.leetcode.com/uploads/2019/02/12/q1248-02.png" style="width: 201px; height: 160px; user-select: auto;"></strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input: </strong>root = <span id="example-input-2-1" style="user-select: auto;">[1,2,3,null,4,null,5]</span>, x = <span id="example-input-2-2" style="user-select: auto;">5</span>, y = <span id="example-input-2-3" style="user-select: auto;">4</span>
<strong style="user-select: auto;">Output: </strong><span id="example-output-2" style="user-select: auto;">true</span>
</pre>

<div style="user-select: auto;">
<p style="user-select: auto;"><strong style="user-select: auto;">Example 3:</strong></p>

<p style="user-select: auto;"><strong style="user-select: auto;"><img alt="" src="https://assets.leetcode.com/uploads/2019/02/13/q1248-03.png" style="width: 156px; height: 160px; user-select: auto;"></strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input: </strong>root = <span id="example-input-3-1" style="user-select: auto;">[1,2,3,null,4]</span>, x = 2, y = 3
<strong style="user-select: auto;">Output: </strong><span id="example-output-3" style="user-select: auto;">false</span>
</pre>
</div>
</div>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Constraints:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;">The number of nodes in the tree will be between <code style="user-select: auto;">2</code> and <code style="user-select: auto;">100</code>.</li>
	<li style="user-select: auto;">Each node has a unique integer value from <code style="user-select: auto;">1</code> to <code style="user-select: auto;">100</code>.</li>
</ul>
</div>