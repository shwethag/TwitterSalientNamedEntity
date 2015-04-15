<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">


<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page import="java.util.ArrayList"%>
<%@ page import="com.iiit.SNE_UI"%>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link
	href="http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600,700,900|Quicksand:400,700|Questrial"
	rel="stylesheet" />
<link href="default.css" rel="stylesheet" type="text/css" media="all" />
<link href="fonts.css" rel="stylesheet" type="text/css" media="all" />
 <script src="http://code.jquery.com/jquery-latest.min.js"></script>
<script>
	
	$(document).ready(function() {
	    $('#go').click(function() {
		 var content = document.getElementById("txtbox").value;
		 $('#sne1').val('processing...');
		     $.ajax({
	            url : 'SNE_UI',
	            type:'post',
	            data : {tweet:content},
	            success : function(responseText) {
	            	var resp = responseText;
	            	var sneArr = resp.split(',');
	            	for(var i=0;i<sneArr.length;i++){
	            		var id = '#sne'+(i+1);
	            		$(id).val(sneArr[i]);
	            	}
	            },
	            
	            error: function(err){
	            	alert(err);
	            }
	        });
	    });
	 }); 
	

</script>

<!--[if IE 6]><link href="default_ie6.css" rel="stylesheet" type="text/css" /><![endif]-->

</head>
<body>
	<div id="header-wrapper">
		<div id="header">
			<div id="logo">
				<h1>
					<a href="#">IDENTIFYING SALIENT NAMED ENTITIES</a>
				</h1>

			</div>
		</div>
		<div class="wrapper">
			<div id="welcome" class="container">


				<!--  form action="SNE_UI" method="post">-->
					<h2>Enter tweet</h2>
					<textarea  id="txtbox" name="textbox"  required ></textarea>
					<br></br>
					<input name="" id="go" type="submit" class="button" value="GO" />

				<!--  /form>-->
			</div>
			<div id="three-column" class="container">
				<!-- Process the elements here -->
				<div class="title">
					<h2>IDENTIFIED SNE</h2>
				</div>
				
				<div id="tbox">
					<input type="text" name="sne1" id="sne1" value=''/><br></br>
					<input type="text" name="sne1" id="sne2" value=""/><br></br>
					<input type="text" name="sne1" id="sne3" value=""/>
				</div>
			</div>
		</div>
		<div id="copyright">
			<p>
				&copy; SNE Identification 
			</p>
			
		</div>
</body>
</html>
