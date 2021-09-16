<h2>93. Restore IP Addresses</h2><h3>Medium</h3><hr><div style="user-select: auto;"><p style="user-select: auto;">Given a string <code style="user-select: auto;">s</code> containing only digits, return all possible valid IP addresses that can be obtained from <code style="user-select: auto;">s</code>. You can return them in <strong style="user-select: auto;">any</strong> order.</p>

<p style="user-select: auto;">A <strong style="user-select: auto;">valid IP address</strong> consists of exactly four integers, each integer is between <code style="user-select: auto;">0</code> and <code style="user-select: auto;">255</code>, separated by single dots and cannot have leading zeros. For example, "0.1.2.201" and "192.168.1.1" are <strong style="user-select: auto;">valid</strong> IP addresses and "0.011.255.245", "192.168.1.312" and "192.168@1.1" are <strong style="user-select: auto;">invalid</strong> IP addresses.&nbsp;</p>

<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Example 1:</strong></p>
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> s = "25525511135"
<strong style="user-select: auto;">Output:</strong> ["255.255.11.135","255.255.111.35"]
</pre><p style="user-select: auto;"><strong style="user-select: auto;">Example 2:</strong></p>
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> s = "0000"
<strong style="user-select: auto;">Output:</strong> ["0.0.0.0"]
</pre><p style="user-select: auto;"><strong style="user-select: auto;">Example 3:</strong></p>
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> s = "1111"
<strong style="user-select: auto;">Output:</strong> ["1.1.1.1"]
</pre><p style="user-select: auto;"><strong style="user-select: auto;">Example 4:</strong></p>
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> s = "010010"
<strong style="user-select: auto;">Output:</strong> ["0.10.0.10","0.100.1.0"]
</pre><p style="user-select: auto;"><strong style="user-select: auto;">Example 5:</strong></p>
<pre style="user-select: auto;"><strong style="user-select: auto;">Input:</strong> s = "101023"
<strong style="user-select: auto;">Output:</strong> ["1.0.10.23","1.0.102.3","10.1.0.23","10.10.2.3","101.0.2.3"]
</pre>
<p style="user-select: auto;">&nbsp;</p>
<p style="user-select: auto;"><strong style="user-select: auto;">Constraints:</strong></p>

<ul style="user-select: auto;">
	<li style="user-select: auto;"><code style="user-select: auto;">0 &lt;= s.length &lt;= 3000</code></li>
	<li style="user-select: auto;"><code style="user-select: auto;">s</code> consists of digits only.</li>
</ul>
</div>