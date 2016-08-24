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


public class Week {
	
    public static DateTime firstDayOfWeek()
    {
        return firstDayOfWeek(DateTime.getToday());
    }
    
    public static DateTime firstDayOfWeek(DateTime day)
    {
        return firstDayOfWeek(day, DayOfWeek.getDefaultWeekStart());
    }
	
	public static DateTime firstDayOfWeek(DateTime day, DayOfWeek weekStarts)
    {
        DateTime d = day;
        while (d.getDayOfWeek() != weekStarts)
        {
            d = d.addDays(-1);
        }

        return d;
    }
	
    public static DateTime firstWorkingDayOfWeek(DateTime day)
    {
        return firstDayOfWeek(day, DayOfWeek.MONDAY);
    }

    public static int weekNrISO8601(DateTime date)
    {
    	
        boolean thursdayFlag = false;
        int dayOfYear = date.getDayOfYear();

        int startWeekDayOfYear = new DateTime(date.getYear(), 1, 1).getDayOfWeekOrdinal();
        int endWeekDayOfYear = new DateTime(date.getYear(), 12, 31).getDayOfWeekOrdinal();

        if (startWeekDayOfYear == 0)
            startWeekDayOfYear = 7;
        if (endWeekDayOfYear == 0)
            endWeekDayOfYear = 7;

        int daysInFirstWeek = 8 - (startWeekDayOfYear);

        if (startWeekDayOfYear == 4 || endWeekDayOfYear == 4)
            thursdayFlag = true;

        int fullWeeks = (int)Math.ceil((dayOfYear - (daysInFirstWeek)) / 7.0);

        int weekNumber = fullWeeks;

        if (daysInFirstWeek >= 4)
            weekNumber = weekNumber + 1;

        if (weekNumber > 52 && !thursdayFlag)
            weekNumber = 1;

        if (weekNumber == 0)
            weekNumber = weekNrISO8601(new DateTime(date.getYear() - 1, 12, 31));
        return weekNumber;
    }

	public static DateTime firstDayOfWeek(DateTime date, Locale locale) {
		Calendar c = Calendar.getInstance(locale);
		DayOfWeek first = DayOfWeek.fromConstant(c.getFirstDayOfWeek());
		
		return firstDayOfWeek(date, first);
	
	}

}
