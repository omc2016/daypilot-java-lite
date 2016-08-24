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

import java.util.ArrayList;

import org.daypilot.json.JSONArray;
import org.daypilot.json.JSONObject;

public class ColumnCollection extends ArrayList<Column> {

	private static final long serialVersionUID = 2855600073458457731L;

	public JSONArray getJSON() {
		JSONArray array = new JSONArray();
		for(Column c : this) {
			array.put(c.getJSON());
		}
		return array;
	}

	public void restoreFromJSON(JSONArray input) {
		try {
			clear();
			
			if (input == null) {
				return;
			}
			
			for (int i = 0; i < input.length(); i++) {
				JSONObject c = input.getJSONObject(i);
				add(Column.fromJSON(c));
			}
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
		
	}

}