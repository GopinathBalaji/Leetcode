<h2>997. Find the Town Judge</h2><h3>Easy</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">In a town, there are <code style="user-select: auto;">n</code> people labelled from&nbsp;<code style="user-select: auto;">1</code> to <code style="user-select: auto;">n</code>.&nbsp; There is a rumor that one of these people is secretly the town judge.</p>

<p style="user-select: auto;">If the&nbsp;town judge exists, then:</p>

<ol style="user-select: auto;">
	<li style="user-select: auto;">The town judge trusts nobody.</li>
	<li style="user-select: auto;">Everybody (except for the town judge) trusts the town judge.</li>
	<li style="user-select: auto;">There is exactly one person that satisfies properties 1 and 2.</li>
</ol>

<p style="user-select: auto;">You are given <code style="user-select: auto;">trust</code>, an array of pairs <code style="user-select: auto;">trust[i] = [a, b]</code> representing that the person labelled <code style="user-select: auto;">a</code> trusts the person labelled <code style="user-select: auto;">b</code>.</p>

<p style="user-select: auto;">If the town judge exists and can be identified, return the label of the town judge.&nbsp; Otherwise, return <code style="user-select: auto;">-1</code>.</p>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 2, trust = [[1,2]]
<strong style="user-select: auto;">Output:</strong> 2
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 2:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 3, trust = [[1,3],[2,3]]
<strong style="user-select: auto;">Output:</strong> 3
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 3:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 3, trust = [[1,3],[2,3],[3,1]]
<strong style="user-select: auto;">Output:</strong> -1
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 4:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 3, trust = [[1,2],[2,3]]
<strong style="user-select: auto;">Output:</strong> -1
</pre>

<p style="user-select: auto;"><strong style="user-select: auto;">Example 5:</strong></p>

<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> n = 4, trust = [[1,3],[1,4],[2,3],[2,4],[4,3]]
<strong style="user-select: auto;">Output:</strong> 3
</pre>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Constraints:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= n &lt;= 1000</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= trust.length &lt;= 10<sup style="user-select: auto;">4</sup></code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">trust[i].length == 2</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">trust[i]</code> are all different</li>
	<li style="user-select: auto;"><code style="user-select: auto;">trust[i][0] != trust[i][1]</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">1 &lt;= trust[i][0], trust[i][1] &lt;= n</code></li>
</ul>
</div>