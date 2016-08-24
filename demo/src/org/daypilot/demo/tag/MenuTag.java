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
import java.io.File;
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

public class MenuTag extends SimpleTagSupport {

	@Override
	public void doTag() throws JspException, IOException {
		JspWriter out = getJspContext().getOut();
		
		PageContext context = (PageContext) getJspContext();
		HttpServletRequest request = (HttpServletRequest) context.getRequest();
		
		String root = request.getRequestURI().substring(0, request.getRequestURI().lastIndexOf('/')) + "/";
		
		// don't print menu on the front page
		if (root.equals(request.getContextPath() + "/")) {
			return;
		}
		
		String realRoot = context.getServletContext().getRealPath("/");
		
		
		JSONArray items = getMenuItems(context);
		for(int i = 0; items != null && i < items.length(); i++) {
			JSONObject tab = items.getJSONObject(i);
			
			String url = tab.optString("url");
			String title = tab.optString("title");
			
			if ("".equals(url)) {
				out.print("<div class='header'>");
				out.print(title);
				out.print("</div>");
			}
			else {
				out.print("<div><a href='");
				if (url.toLowerCase().equals("default.jsp") || url.toLowerCase().equals("index.jsp")) {
					out.print("./");
				}
				else {
					out.print(url);
				}
				out.print("'");
				
				String realAbsolute = context.getServletContext().getRealPath(request.getServletPath());
				String realRelative = realAbsolute.substring(realRoot.length());
				
				if (realRelative.toUpperCase().endsWith(File.separator + url.toUpperCase())) {
					out.print(" class='selected'");
				}
				out.print("><span>");
				out.print(title);
				out.print("</span></a></div>");
			}
		}
		
	}

	private JSONArray getMenuItems(PageContext context) {
		InputStream is = null;
		try {
			HttpServletRequest request = (HttpServletRequest) context.getRequest();
			String root = request.getServletPath().substring(0, request.getServletPath().lastIndexOf('/')) + "/";
	
			is = context.getServletContext().getResourceAsStream(root + "navigation.json");
			String data = readFileAsString(is);
	
			if (data == null) {
				data = "[]";
			}
			return new JSONArray(data);
		}
		finally {
			try { is.close(); } catch (Exception e) {}
		}
	}

	
    private String readFileAsString(InputStream input) {
    	try {
	    	if (input == null) {
	    		return null;
	    	}
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

	
    public class Menu {
    	private String description;
    	private JSONArray items;
    	
    	public Menu(String input) {
   			this(new JSONObject(input));
    	}
    	
    	public Menu(JSONObject input) {
    		description = input.optString("Description");
    		items = input.optJSONArray("Items");
    	}

		public String getDescription() {
			return description;
		}

		public JSONArray getItems() {
			return items;
		}
    	
    }
}
