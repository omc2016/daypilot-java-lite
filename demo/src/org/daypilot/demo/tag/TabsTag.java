/*
Copyright © 2012 Annpoint, s.r.o.

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.

-------------------------------------------------------------------------

NOTE: Reuse requires the following acknowledgement (see also NOTICE):
This product includes DayPilot (http://www.daypilot.org) developed by Annpoint, s.r.o.
*/

package org.daypilot.demo.tag;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

import org.daypilot.json.JSONArray;
import org.daypilot.json.JSONObject;

public class TabsTag extends SimpleTagSupport {
	
	@Override
	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		
		PageContext context = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) context.getRequest();
		String root = request.getRequestURI().substring(0, request.getRequestURI().lastIndexOf('/')) + "/";
		JSONArray tabs = getTabs(context);

		String description = "";
		
		out.print("<div>");
		
		for(int i = 0; i < tabs.length(); i++) {
			JSONObject tab = tabs.getJSONObject(i);
			
			String url = tab.optString("url");
			String title = tab.optString("title");
			
			String full = request.getContextPath() + url;
			
			out.print("<a class='");
			if (root.equals(full)) {
				description = tab.optString("description");
				out.print("tab selected");
			}
			else {
				out.print("tab");
			}
			out.print("' href='");
			out.print(full);
			out.print("'><span style='width: 100px; text-align:center;'>");
			out.print(title);
			out.print("</span></a>");
			
			//<a class='tab' href='${pageContext.request.contextPath}/Calendar/'><span style="width: 100px; text-align:center;">Calendar</span></a>
		}
		out.print("</div>");
		
		out.print("<div class='header'><div class='bg-help'>");
		out.print(description);
		out.print("</div></div>");
		
		
		/*
		 * <div class="header"><div class="bg-help">Description</div></div>
		 */
		
	}
	
	private JSONArray getTabs(PageContext context) {
		InputStream is = null;
		try {
			is = context.getServletContext().getResourceAsStream("/navigation.json");
			String data = readFileAsString(is);
			return new JSONArray(data);
		}
		finally {
			try { is.close(); } catch (Exception e) {}
		}
		
	}
	
    private String readFileAsString(InputStream input) {
    	try {
	        StringBuffer fileData = new StringBuffer(1000);
	        BufferedReader reader = new BufferedReader(new InputStreamReader(input));
	        char[] buf = new char[1024];
	        int numRead = 0;
	        while ((numRead = reader.read(buf)) != -1) {
	            String readData = String.valueOf(buf, 0, numRead);
	            fileData.append(readData);
	            buf = new char[1024];
	        }
	        reader.close();
	        return fileData.toString();
    	}
    	catch (IOException e) {
    		throw new RuntimeException(e);
    	}
    }

}
