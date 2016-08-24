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

package org.daypilot.ui.enums;

public enum ViewType {
	DAY,
	WORKWEEK,
	WEEK,
	DAYS;

	
	/**
	 * 
	 * @param input
	 * @return
	 */
	public static ViewType parse(String input) {
		if (input == null) {
			throw new IllegalArgumentException("Can't parse a null value.");
		}
		
		String upper = input.toUpperCase();
		
		return ViewType.valueOf(upper);
	}
	
	/** Parses a String. If it's not possible to parse a correct value, returns null.
	 * @param input Input String to be parsed.
	 * @return ViewTypeEnum value or null
	 */
	public static ViewType parseSoft(String input) {
		try {
			return parse(input);
		}
		catch (Exception e) {
			return null;
		}
	}
	
	public String toString() {
		switch (this) {
			case DAYS:
				return "Days";
			case DAY:
				return "Day";
			case WEEK:
				return "Week";
			case WORKWEEK:
				return "WorkWeek";
			default:
				throw new RuntimeException("Unexpected value.");
		}
	}
}
