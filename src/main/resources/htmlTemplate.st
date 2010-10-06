<h4>Package: $package$</h4>
<h5>Class: $className$</h5>
$methods: {method|
<table class="plain_text_table">
<tr style="background-color: #E8E8E8;">
	<td>Method</td><td colspan="2">$method.readableName$</td>
</tr>
<tr>
	<td colspan="3">$method.javadoc$</td>
</tr>
<tr>	
	<td>Returns</td><td colspan="2">$method.returns$</td>
</tr>
$if(method.hasParams)$
<tr>	
	<td colspan="3">Parameters</td>
</tr>
$method.params:{p|<tr>
	<td></td>
	<td>$p.name$</td><td>$p.type$</td>
</tr>}$
$endif$
</table>
}$
<hr/>