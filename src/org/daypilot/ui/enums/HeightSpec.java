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

public enum HeightSpec {
	FULL,
	BUSINESS_HOURS;
	
	@Override
	public String toString() {
		switch (this) {
			case FULL:
				return "Full";
			case BUSINESS_HOURS:
				return "BusinessHours";
			default:
				throw new RuntimeException("Unexpected HeightSpec value");
		}
	}

	public static HeightSpec parse(String input) {
		if (input == null) {
			throw new RuntimeException("Can't parse a null value.");
		}
		
		String upper = input.toUpperCase();
		if (upper.equals("FULL")) {
			return FULL;
		}
		if (upper.equals("BUSINESSHOURS")) {
			return BUSINESS_HOURS;
		}
		
		throw new RuntimeException("Unrecognized input string (" + input + ").");
	}
	
	public static HeightSpec parseSoft(String input) {
		try {
			return parse(input);
		}
		catch (Exception e) {
			return null;
		}
	}
}
