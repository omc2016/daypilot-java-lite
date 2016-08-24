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

package org.daypilot.data;

import org.daypilot.date.DateTime;
import org.daypilot.json.JSONObject;

public class Column {
    private String value;
    private String name;
    private DateTime date = DateTime.EMPTY;
    private String toolTip;

    public Column() {}
    public Column(String name, String value) {
    	this.value = value;
    	this.name = name;
    }
    
	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public DateTime getDate() {
		return date;
	}

	public void setDate(DateTime date) {
		this.date = date;
	}

	public String getToolTip() {
		return toolTip;
	}

	public void setToolTip(String toolTip) {
		this.toolTip = toolTip;
	}

	public JSONObject getJSON() {
		try {
			JSONObject result = new JSONObject();

			result.put("Value", value);
			result.put("Name", name);
			result.put("Date", date.toStringSortable());
			result.put("ToolTip", toolTip);

			return result;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	public static Column fromJSON(JSONObject input) {
		try {
			
			Column r = new Column();
			r.setName(input.getString("Name"));
			r.setToolTip(input.optString("ToolTip"));
			r.setValue(input.optString("Value"));
			r.setDate(DateTime.parseString(input.optString("Date")));

			return r;
			
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
    
    
}
