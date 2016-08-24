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

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.SimpleTimeZone;
import java.util.TimeZone;

/**
 * A helper class for date/time calculations.
 */
public class DateTime {
	private static final SimpleDateFormat iso = new SimpleDateFormat("yyyy'-'MM'-'dd'T'HH':'mm':'ss");
	public static final TimeZone UTC = new SimpleTimeZone(0, "UTC");

	protected long ticks;
	private TimeZone timeZone = UTC;
	
	public static final DateTime EMPTY = new DateTime(0);
	public static final DateTime MAX = new DateTime("9999-01-01T00:00:00");
	public static final DateTime MIN = new DateTime("0001-01-01T00:00:00");

	/**
	 * Creates a new DateTime representing current date and time (now).
	 * 
	 * @param date
	 */
	public DateTime() {
		ticks = new Date().getTime();
	}
	
	public DateTime(long time) {
		ticks = time;
	}
	
	
	protected Calendar calendar() {
		Calendar c = Calendar.getInstance(timeZone);
		c.setTimeInMillis(ticks);
		return c;
	}

	/**
	 * Creates a new DateTime object from an existing java.sql.Timestamp object.
	 * 
	 * @param date
	 */
	public DateTime(Timestamp timestamp) {
		this(timestamp, false);
	}
	
	public DateTime(Timestamp timestamp, boolean isLocal) {
		this((Date)timestamp, isLocal);
	}

	/**
	 * Creates a new DateTime object from an existing java.util.Date object.
	 * 
	 * @param date
	 */
	public DateTime(Date date, boolean isLocal) {
		this();

		if (date == null) {
			return;
		}
		
		if (isLocal) {
			DateTime utc = new DateTime(date.getTime());
			
			//ticks = utc.addMinutes(-date.getTimezoneOffset()).getTicks();
			ticks = utc.addMinutes(getTimeZoneOffset(date)).getTicks();
		}
		else {
			ticks = date.getTime();
		}
	}

	/**
	 * As defined in javadoc for Date.getTimeZoneOffset(), WITHOUT the minus sign.
	 * @param d
	 * @return
	 */
	private static int getTimeZoneOffset(Date d) {
		Calendar c = Calendar.getInstance();
		c.setTime(d);
		int offset = (c.get(Calendar.ZONE_OFFSET) + c.get(Calendar.DST_OFFSET)) / (60 * 1000);
		return offset;
	}

	public DateTime(Date date) {
		this(date, false);
	}
	
	/**
	 * Creates a new DateTime object from a string in "2009-01-01T00:00:00"
	 * or "2009-01-01" format.
	 * 
	 * @param date
	 */
	public DateTime(String str) {
		fromString(str);
	}
	
	private void fromString(String str) {
		try {
			iso.setTimeZone(timeZone);

			if (str.length() == 10) {
				ticks = iso.parse(str + "T00:00:00").getTime();
			}
			else {
				ticks = iso.parse(str).getTime();
			}
		} catch (ParseException e) {
			throw new RuntimeException(e);
		}		
	}
	
	public static DateTime fromDateLocal(Date date) {
		return new DateTime(date, true);
	}
	
	
	public DateTime(int year, int month, int day) {
		Calendar calendar = calendar();
		calendar.clear();
		calendar.set(year, month - 1, day);
		
		ticks = calendar.getTimeInMillis();
	}

	public DateTime(long ticks, TimeZone tz) {
		this.ticks = ticks;
		if (tz != null) {
			this.timeZone = tz;
		}
	}

	/**
	 * Converts DateTime to a sortable string format. Uses toStringIso8601().
	 * 
	 * @param date
	 */
	public String toStringSortable() {
		return toStringIso8601();
	}
	
	/**
	 * Converts DateTime to a String in ISO 8601 format ("2009-01-01T00:00:00").
	 * 
	 * @param date
	 */
	public String toStringIso8601() {
		iso.setTimeZone(timeZone);
		String str = iso.format(new Date(ticks));
		return str;
	}


	/**
	 * Converts DateTime to a String in "2009-01-01T00:00:00" format.
	 * 
	 * @param date
	 */
	public String toString() {
		return toStringIso8601();
	}
	
	/**
	 * Formats the date using the specified pattern (see http://java.sun.com/javase/6/docs/api/index.html?java/text/SimpleDateFormat.html). Uses the default locale (Locale.getDefault()).
	 * @param pattern
	 * @return Formatted String.
	 */
	public String toString(String pattern) {
		return toString(pattern, Locale.getDefault());
	}
	
	public String toDateString(Locale locale) {
		DateFormat sdf = DateFormat.getDateInstance(java.text.DateFormat.DEFAULT, locale);
		sdf.setTimeZone(timeZone);
		return sdf.format(calendar().getTime());
	}
	
	public String toDateTimeString(Locale locale) {
		DateFormat sdf = DateFormat.getDateTimeInstance(DateFormat.DEFAULT, DateFormat.DEFAULT, locale);
		sdf.setTimeZone(timeZone);
		return sdf.format(calendar().getTime());
	}
	
	/**
	 * Formats the date using the specified pattern (see http://java.sun.com/javase/6/docs/api/index.html?java/text/SimpleDateFormat.html) and locale.
	 * @param pattern
	 * @param locale
	 * @return Formatted String.
	 */
	public String toString(String pattern, Locale locale) {
		SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
		sdf.setTimeZone(timeZone);
		String str = sdf.format(new Date(ticks));
		return str;
//		SimpleDateFormat sdf = new SimpleDateFormat(pattern, locale);
//		return sdf.format(calendar().getTime());
	}

	/**
	 * Converts DateTime to a java.sql.Timestamp object.
	 * 
	 * @param date
	 */
	public Timestamp toTimeStamp() {
		Timestamp ts = new Timestamp(ticks);
		return ts;
	}
	
	/**
	 * Returns a new DateTime object, increased by the specified number of days
	 * (accepts negative values).
	 * 
	 * @param days
	 * @return
	 */
	public DateTime addDays(int days) {
		return add(TimeSpan.fromDays(days));
	}

	/**
	 * Returns a new DateTime object, increased by the specified number of
	 * minutes (accepts negative values).
	 * 
	 * @param days
	 * @return
	 */
	public DateTime addMinutes(int minutes) {
		DateTime t = add(TimeSpan.fromMinutes(minutes));
		return t;
	}
	
	public static DateTime parseAsLocal(Object o) {
		return parse(o, true);
	}
	
	public static DateTime parseString(String str) {
		DateTime dt = new DateTime();
		dt.fromString(str);
		
		return dt;
		//return parse(str, false);
	}

	private static DateTime parse(Object o, boolean isLocal) {
		if (o == null) {
			return null;
		}
		if (o instanceof DateTime) {
			return (DateTime) o;
		}
		if (o instanceof java.util.Date) {
			return new DateTime((Date) o, isLocal);
		}
		if (o instanceof java.sql.Timestamp) {
			return new DateTime((Timestamp) o, isLocal);
		}
		if (o instanceof String) {
			String str = (String) o;
			if (str.length() == 0) {
				return null;
			}
			return new DateTime(str);
		}
		
		throw new RuntimeException("Unable to convert from " + o.getClass() + " to org.daypilot.date.DateTime");
	}

	public Date toDate() {
		return calendar().getTime();
	}
	
	public int getYear() {
		Calendar calendar = calendar();
		return calendar.get(Calendar.YEAR);
	}
	
	public int getMonth() {
		Calendar calendar = calendar();
		return calendar.get(Calendar.MONTH) + 1;
	}
	
	public int getDay() {
		Calendar calendar = calendar();
		return calendar.get(Calendar.DATE);
	}

	public DateTime getDatePart() {
		Calendar calendar = calendar();
		calendar.clear();
		calendar.set(getYear(), getMonth() - 1, getDay());
		return new DateTime(calendar.getTime().getTime(), timeZone);
	}

	public DateTime add(TimeSpan offset) {
		return new DateTime(ticks + offset.getTotalMilliseconds(), timeZone);
	}

	public int getHour() {
		Calendar calendar = calendar();
		return calendar.get(Calendar.HOUR_OF_DAY);
	}

	public DayOfWeek getDayOfWeek() {
		Calendar calendar = calendar();
		//calendar.setTime(dateTime);
		return DayOfWeek.fromConstant(calendar.get(Calendar.DAY_OF_WEEK));
	}
	
	
	/**
	 * @return Sunday = 0, Monday = 1, etc.
	 */
	public int getDayOfWeekOrdinal() {
		Calendar calendar = calendar();
		int dow = calendar.get(Calendar.DAY_OF_WEEK);
		return dow - 1;
	}

	public String toDateString() {
		return toStringSortable().substring(0, 10);
	}

	public static DateTime getToday() {
		return new DateTime().getDatePart();
	}
	
	public static DateTime today() {
		return new DateTime().getDatePart();
	}
	
	public int getDayOfYear() {
		Calendar calendar = calendar();
		return calendar.get(Calendar.DAY_OF_YEAR);
	}

	public int getMinute() {
		Calendar calendar = calendar();
		return calendar.get(Calendar.MINUTE);
	}

	public TimeSpan getTimeOfDay() {
		return new TimeSpan(ticks - getDatePart().getTicks());
	}
	
	public long getTicks() {
		return ticks;
	}

	public boolean before(DateTime another) {
		return ticks < another.ticks;
	}

	public DateTime addHours(int hours) {
		Calendar calendar = calendar();
		calendar.add(Calendar.HOUR, hours);

		return new DateTime(calendar.getTime().getTime(), timeZone);
	}

	public DateTime addMonths(int months) {
		Calendar calendar = calendar();
		calendar.add(Calendar.MONTH, months);

		return new DateTime(calendar.getTime().getTime(), timeZone);
	}

	public TimeSpan minus(DateTime another) {
		return new TimeSpan(this.getTicks() - another.getTicks());
	}

	public DateTime addYears(int years) {
		Calendar calendar = calendar();
		calendar.add(Calendar.YEAR, years);

		return new DateTime(calendar.getTime().getTime(), timeZone);
	}

	public boolean after(DateTime another) {
		return this.getTicks() > another.getTicks();
	}
	
	@Override
	public boolean equals(Object another) {
		if (another == null) {
			return false;
		}
		if (!(another instanceof DateTime)) {
			return false;
		}
		return this.getTicks() == ((DateTime)another).getTicks();
	}

	public DateTime addSeconds(int seconds) {
		Calendar calendar = calendar();
		calendar.add(Calendar.SECOND, seconds);

		return new DateTime(calendar.getTime().getTime(), timeZone);
	}

	public boolean beforeOrEqual(DateTime another) {
		return before(another) || equals(another);
	}

	public boolean afterOrEqual(DateTime another) {
		return after(another) || equals(another);
	}

	/**
	 * Return true if the string can be parsed using parse().
	 * @param text
	 * @return
	 */
	public static boolean canParse(String text) {
		try {
			parse(text, false);
		}
		catch (Exception e) {
			return false;
		}
		return true;
	}

	public boolean isWeekend() {
        if (getDayOfWeek() == DayOfWeek.SATURDAY)
        {
            return true;
        }
        if (getDayOfWeek() == DayOfWeek.SUNDAY)
        {
            return true;
        }
        return false;
	}
	
	// using the locale
	public String getMonthName(DateTime date, Locale locale) {
		return date.toString("MMMM", locale);
	}

	public DateTime firstDayOfMonth() {
		return new DateTime(this.getYear(), this.getMonth(), 1);
	}

	public int getDaysOfMonth() {
		DateTime start = this.firstDayOfMonth();
		DateTime end = start.addMonths(1);
		return (int) end.minus(start).getTotalDays();
	}
	
	public int getDaysOfYear() {
		DateTime end = firstDayOfYear().addYears(1);
		return (int) end.minus(firstDayOfYear()).getTotalDays();
	}
	
	public DateTime firstDayOfYear() {
		return new DateTime(this.getYear(), 1, 1);
	}

	public int compareTo(DateTime another) {
		return calendar().compareTo(another.calendar());
	}

	public DateTime smaller(DateTime another) {
		return this.before(another) ? this : another;
	}
	
	public DateTime bigger(DateTime another) {
		return this.before(another) ? another : this;
	}

	/**
	 * Returns a new DateTime instance with timezone set to tz. TimeZone affects formatting methods (e.g. toString()) and info methods (e.g. getDay()). 
	 * It doesn't affect the actual time (milliseconds since 1970 UTC).
	 * @param tz
	 * @return
	 */
	public DateTime tz(TimeZone tz) {
		return new DateTime(ticks, tz);
	}
	
	/**
	 * Returns a new DateTime instance with timezone set to UTC. TimeZone affects formatting methods (e.g. toString()) and info methods (e.g. getDay()). 
	 * It doesn't affect the actual time (milliseconds since 1970 UTC).
	 * @return
	 */
	public DateTime utc() {
		return tz(UTC);
	}

	public TimeZone getTimezone() {
		return timeZone;
	}

	public void setTimezone(TimeZone timezone) {
		if (timezone == null) {
			throw new RuntimeException("Timezone cannot be null.");
		}
		this.timeZone = timezone;
	}
	
	public DateTime addMilliSeconds(int ms) {
		return new DateTime(ticks + ms, timeZone);
	}

	/**
	 * Returns beginning of this day in the given time zone. The timezone of the returned class is set to tz.
	 * @param tz
	 * @return
	 */
	public static DateTime getToday(TimeZone tz) {
		DateTime today = getToday();
		int offset = safeTimeZone(tz).getOffset(today.getTicks());
		return today.addMilliSeconds(-offset).tz(tz);
	}
	
	private static TimeZone safeTimeZone(TimeZone tz) {
		if (tz == null) {
			return UTC;
		}
		
		return tz;
	}

	public DateTime inZoneByName(TimeZone newZone) {
		if (newZone == null) {
			throw new RuntimeException("newZone parameter cannot be null");
		}
		
		int offset = timeZone.getOffset(getTicks()); // get offset to utc
		DateTime normalized = addMilliSeconds(offset); // maybe plus here ??
		
		offset = newZone.getOffset(getTicks());
		return normalized.addMilliSeconds(-offset).tz(newZone);
		
	}

}
