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

import javax.servlet.jsp.JspException;
import javax.servlet.jsp.JspWriter;
import javax.servlet.jsp.PageContext;
import javax.servlet.jsp.tagext.SimpleTagSupport;

public class DownloadTag extends SimpleTagSupport {
	
	@Override
	public void doTag() throws JspException, IOException {
		InputStream is = null;
		try {
			JspWriter out = getJspContext().getOut();
			
			PageContext context = (PageContext) getJspContext();
			//HttpServletRequest request = (HttpServletRequest) context.getRequest();
			is = context.getServletContext().getResourceAsStream("/WEB-INF/build.number");
			String data = readFileAsString(is);
			
			if (data == null) {
				data = "X.X.X";
			}
		
			out.print("daypilot-java-trial-" + data + ".zip");
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

}
