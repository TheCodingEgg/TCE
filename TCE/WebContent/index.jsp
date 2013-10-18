<%@ page language="java" contentType="text/html; charset=UTF-8"
    pageEncoding="UTF-8"%>
<!DOCTYPE html">
<html>
<head>
<meta  charset="UTF-8">
<title>Prototype</title>
<script src="js/jquery-1.10.2.js"></script>
<script src="js/raphael.js"></script>
<script src="js/prototypes.js"></script>


<script src="js/codemirror-3.18/lib/codemirror.js"></script>
<script src="js/codemirror-3.18/mode/clike/clike.js"></script>
<script src="js/active_line.js"></script>
	
<link rel="stylesheet" href="js/codemirror-3.18/lib/codemirror.css">

<link rel="stylesheet" href="css/basic.css" type="text/css" media="screen"/>	
<link rel="stylesheet" href="css/code_mirror.css" type="text/css" media="screen"/>
</head>
<body>




	<p class="large" align="center">The Coding Egg - Editor</p>
	<form id="updateStuff">
		<label for=userIn>Enter tour text</label>
		
		<textarea type="text" id="userIn" name="userIn" rows="15"  cols="51"></textarea>
		
		<input type="button" id="invio" value="invio"/>
		<input type="button" id="run" value="run" />
	</form>
	<p id="displayStuff"/>
	<hr/>
	<script>
    var javaEditor = CodeMirror.fromTextArea(document.getElementById("userIn"), {
	lineNumbers: true,
	matchBrackets: true,
	mode: "text/x-java",
	styleActiveLine: true,
	lineWrapping: true
	}); 
    
  

</script>
	
	
<div id="canvas"></div>
<script type="text/javascript">
$(document).ready(function() {
	
	$("#run").click(function(){
    	alert(CodeMirror.getLine(1));
    });
		
	
});



</script> 

</script>

</body>
</html>

