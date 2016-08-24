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

package org.daypilot.date;

import java.util.Calendar;
import java.util.Locale;

public enum DayOfWeek {
	SUNDAY,
	MONDAY,
	TUESDAY,
	WEDNESDAY,
	THURSDAY,
	FRIDAY,
	SATURDAY;
	
	public static DayOfWeek getDefaultWeekStart() {
		return getDefaultWeekStart(Locale.getDefault());
	}
	
	public static DayOfWeek getDefaultWeekStart(Locale locale) {
		Calendar calendar = Calendar.getInstance(locale);
		return fromConstant(calendar.getFirstDayOfWeek());
	}
	
	public int toOrdinal() {
		switch (this) {
			case MONDAY:
				return 1;
			case TUESDAY:
				return 2;
			case WEDNESDAY:
				return 3;
			case THURSDAY:
				return 4;
			case FRIDAY:
				return 5;
			case SATURDAY:
				return 6;
			case SUNDAY:
				return 0;
			default:
				throw new RuntimeException("Unrecognized day constant value.");
		}
	}
	
	public static DayOfWeek fromOrdinal(int i) {
		switch (i) {
			case 0:
				return SUNDAY;
			case 1:
				return MONDAY;
			case 2:
				return TUESDAY;
			case 3:
				return WEDNESDAY;
			case 4:
				return THURSDAY;
			case 5:
				return FRIDAY;
			case 6:
				return SATURDAY;
			default:
				throw new RuntimeException("Unrecognized day constant value.");
		}
	}
	
	public static DayOfWeek fromConstant(int constant) {
		switch (constant) {
			case Calendar.MONDAY:
				return MONDAY;
			case Calendar.TUESDAY:
				return TUESDAY;
			case Calendar.WEDNESDAY:
				return WEDNESDAY;
			case Calendar.THURSDAY:
				return THURSDAY;
			case Calendar.FRIDAY:
				return FRIDAY;
			case Calendar.SATURDAY:
				return SATURDAY;
			case Calendar.SUNDAY:
				return SUNDAY;
			default:
				throw new RuntimeException("Unrecognized day constant value.");
		}
	}
}
