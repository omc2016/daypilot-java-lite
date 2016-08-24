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

package org.daypilot.ui.args.calendar;

import org.daypilot.date.DateTime;
import org.daypilot.json.JSONObject;

public class EventActionArgs {
	private String value;
	private String text;
	private DateTime start;
	private DateTime end;
	private JSONObject data;
	
	protected EventActionArgs() {
	}
	
	public void loadEvent(JSONObject event) {
		value = event.getString("value");
		text = event.getString("text");
		start = event.getDateTime("start");
		end = event.getDateTime("end");
	}

	public String getValue() {
		return value;
	}
	public String getText() {
		return text;
	}
	public DateTime getStart() {
		return start;
	}
	public DateTime getEnd() {
		return end;
	}

	protected void setData(JSONObject data) {
		this.data = data;
	}

	public JSONObject getData() {
		return data;
	}
	
}
