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

package org.daypilot.ui;

import java.io.BufferedReader;
import java.io.IOException;

import javax.servlet.http.HttpServletRequest;

import org.daypilot.json.JSONException;
import org.daypilot.json.JSONObject;

class JSONParser {
	
	static JSONObject parse(HttpServletRequest request) throws IOException, JSONException {
		BufferedReader r = request.getReader();
		
		StringBuilder sb = new StringBuilder();
		String line = null;
        while ((line = r.readLine()) != null) {
        	if (line.startsWith("JSON")) { // first line
        		line = line.substring(4);
        	}
            sb.append(line + "\n");
        }

		return new JSONObject(sb.toString());

	}
}
