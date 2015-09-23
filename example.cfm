<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
<title>SWFParser Component example</title>
</head>

<body>
<cfparam name="thefile" default="http://www.elgalpon.cl/index.swf"/>
<cfset swf_parser=CreateObject("component","swfparser").init().read(thefile)>
<cfset info=swf_parser.getInfo()>
<cfdump var="#info#"/>
<!---<cfset all=swf_parser.getElements()>
<cfdump var="#all#"/>--->
<cfset texts=swf_parser.extractTexts()>
<cfset qimages=swf_parser.queryImages()>
<cfset videos=swf_parser.extractVideos(withsound=false)>
<cfset sounds=swf_parser.extractSounds()>
<cfdump var="#texts#"/>
<cfdump var="#qimages#"/>
<cfdump var="#videos#"/>
<cfdump var="#sounds#"/>
</body>
</html>