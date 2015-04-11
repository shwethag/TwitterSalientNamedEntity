<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Strict//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-strict.dtd">
<!--
Design by TEMPLATED
http://templated.co
Released for free under the Creative Commons Attribution License

Name       : Amaryllis 
Description: A two-column, fixed-width design with dark color scheme.
Version    : 1.0
Released   : 20140131

-->

<html xmlns="http://www.w3.org/1999/xhtml">
<%@ page import="java.util.ArrayList" %>
<%@ page import="com.iiit.SNE_UI" %>
<head>
<meta http-equiv="Content-Type" content="text/html; charset=utf-8" />
<title></title>
<meta name="keywords" content="" />
<meta name="description" content="" />
<link href="http://fonts.googleapis.com/css?family=Source+Sans+Pro:200,300,400,600,700,900|Quicksand:400,700|Questrial" rel="stylesheet" />
<link href="default.css" rel="stylesheet" type="text/css" media="all" />
<link href="fonts.css" rel="stylesheet" type="text/css" media="all" />

<!--[if IE 6]><link href="default_ie6.css" rel="stylesheet" type="text/css" /><![endif]-->

</head>
<body>
<div id="header-wrapper">
	<div id="header" class="container">
		<div id="logo">
			<h1><a href="#">IDENTIFYING SALIENT NAMED ENTITIES</a></h1>
		</div>
		<div id="menu">
			<ul>
				<li class="active"><a href="index.jsp" accesskey="1" title="">Homepage</a></li>
				<li><a href="http://www.iiit.ac.in/" accesskey="2" title="">IIIT Hyderabad</a></li>
				<li><a href="#" accesskey="3" title=""></a></li>
				<li><a href="#" accesskey="4" title=""></a></li>
				<li><a href="#" accesskey="5" title=""></a></li>
			</ul>
		</div>
	</div>
</div>
<div class="wrapper">
	<div id="welcome" class="container">
		<div class="title">
			<h2>Welcome</h2>
		</div>
		<p>This is <strong>Identifying named entity</strong>, We propose a system that determines the salience of entities within web documents.
Many recent advances in commercial search engines leverage the identification of entities
in web pages. However, for many pages, only a small subset of entities are important, or
central, to the document, which can lead to degraded relevance for entity triggered experiences.
We address this problem by devising a system that scores each entity on a web page
according to its centrality to the page content</p>
	        <form action="SNE_UI" method="post">
		    <h2>Enter Named Entity Here</h2>
		    <input type=text id="txtbox" name="textbox" size="60" required/>
		    <input name="" type="submit" class="button" value="GO" />
		   
		</form>
	</div>
	<div id="three-column" class="container">
	<!-- Process the elements here -->
		<div id="tbox1">
			<div class="title">
				<h2>IDENTIFIED SNE</h2>
			</div>
			<p><input type="text" class="paddingtop" value=""/></p>
			<a href="#" class="button">Result 1</a> </div>
		<div id="tbox2">
			<div class="title">
				<h2>IDENTIFIED SNE</h2>
			</div>
			<p><input type="text" class="paddingtop" value=""/></p>
			<a href="#" class="button">Result 2</a> </div>
		<div id="tbox3">
			<div class="title">
				<h2>IDENTIFIED SNE</h2>
			</div>
			<p><input type="text" class="paddingtop" value=""/></p>
			<a href="#" class="button">Result 3</a> </div>
	</div>
</div>
<div id="copyright">
	<p>&copy; Untitled. All rights reserved. | Photos by <a href="http://iiit.ac.in/">IIIT Hyderabad</a> | Design by <a href="http://templated.co" rel="nofollow">TEMPLATED</a>.</p>
	<ul class="contact">
		<li><a href="#" class="icon icon-twitter"><span>Twitter</span></a></li>
		<li><a href="#" class="icon icon-facebook"><span></span></a></li>
		<li><a href="#" class="icon icon-dribbble"><span>Pinterest</span></a></li>
		<li><a href="#" class="icon icon-tumblr"><span>Google+</span></a></li>
		<li><a href="#" class="icon icon-rss"><span>Pinterest</span></a></li>
	</ul>
</div>
</body>
</html>
