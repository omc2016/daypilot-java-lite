<?xml version="1.0" encoding="UTF-8" ?>
<%@ page language="java" contentType="text/html; charset=UTF-8" pageEncoding="UTF-8"%>
<%@ taglib prefix="demo" tagdir="/WEB-INF/tags"%>
<!DOCTYPE html PUBLIC "-//W3C//DTD XHTML 1.0 Transitional//EN" "http://www.w3.org/TR/xhtml1/DTD/xhtml1-transitional.dtd">
<html xmlns="http://www.w3.org/1999/xhtml">
<head>
	<meta http-equiv="Content-Type" content="text/html; charset=UTF-8" />
	<demo:title>Week View (Calendar)</demo:title>
	<demo:scripts />
	<demo:css />
	<demo:head />
</head>
<body>

<demo:header />

<table style="width:100%">
        <tr>
            <td valign="top">
				<div id="dpc"></div>
            </td>
		</tr>
</table>
            

<script type="text/javascript">

var dpc = new DayPilot.Calendar("dpc");
dpc.backendUrl = '../dpc';
dpc.ajaxError = function(req) { new DayPilot.Modal().showHtml(req.responseText); };
dpc.borderColor = "#a0a0a0";
dpc.viewType = "Week";
dpc.heightSpec = 'BusinessHours';
dpc.timeRangeSelectedHandling = 'CallBack';
dpc.eventMoveHandling = 'CallBack';
dpc.eventResizeHandling = 'CallBack';
dpc.initScrollPos = 9 * 40 + 1; 
dpc.Init();

</script>

<demo:footer />

</body>
</html>