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

package org.daypilot.util;

import java.util.Iterator;
import java.util.List;

public class Str {
	public static boolean isNullOrEmpty(String input) {
		if (input == null) {
			return true;
		}
		
		if (input.equals("")) {
			return true;
		}
		
		return false;
	}
	
	public static String toStringOrNull(Object object) {
		if (object == null) {
			return null;
		}
		
		return object.toString();
	}
	
	public static String join(List<?> list, String separator) {
		StringBuilder sb = new StringBuilder();
		
		for (Iterator<?> iterator = list.iterator(); iterator.hasNext();) {
			String string = iterator.next().toString();
			sb.append(string);
			if (iterator.hasNext()) {
				sb.append(separator);
			}
		}
		return sb.toString();
	}
}
