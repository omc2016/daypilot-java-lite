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

import java.util.Locale;

import org.daypilot.ui.enums.TimeFormat;

public class TimeFormatter
{
    public static String getHour(DateTime time, TimeFormat clock, String format, Locale locale)
    {
        return getHour(time.getHour(), clock, format, locale);
    }

    public static String getHour(int hour, TimeFormat clock, String format, Locale locale)
    {
    	clock = clock.resolve(locale);
    	
        boolean am = (hour / 12) == 0;
        if (clock == TimeFormat.CLOCK_12_HOURS)
        {
            hour = hour % 12;
            if (hour == 0)
                hour = 12;
        }

        String suffix = "";
        if (clock == TimeFormat.CLOCK_12_HOURS)
        {
            if (am)
            {
                suffix = "AM";
            }
            else
            {
                suffix = "PM";
            }
        }

        if (format == null || "".equals(format))
        {
            format = "%d %s";
        }

        return String.format(format, hour, suffix);
    }
    
    public static String getHourMinutes(DateTime date, TimeFormat clock, Locale locale)
    {
    	return getHourMinutes(date.getTimeOfDay(), clock, locale);
    }

	public static String getHourMinutes(TimeSpan time, TimeFormat clock,
			Locale locale) {
        TimeFormat resolvedClock = clock.resolve(locale);

        int hour = (int) time.getHours();
        boolean am = (hour / 12) == 0;
        if (resolvedClock == TimeFormat.CLOCK_12_HOURS)
        {
            hour = (int) (time.getHours() % 12);
            if (hour == 0)
                hour = 12;
        }

        String suffix = "";
        if (resolvedClock == TimeFormat.CLOCK_12_HOURS)
        {
            if (am)
            {
                suffix = "AM";
            }
            else
            {
                suffix = "PM";
            }
        }

    	String minute = time.getMinutes() < 10 ? "0" + time.getMinutes() : "" + time.getMinutes(); 
        if (suffix == null || "".equals(suffix))
        {
            return String.format("%d:%2s", hour, minute);
        }
        else
        {
            return String.format("%d:%s %s", hour, minute, suffix);
        }
	}

}

