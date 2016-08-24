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

package org.daypilot.ui;

import java.io.IOException;
import java.util.Calendar;
import java.util.Collection;
import java.util.Locale;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.daypilot.data.Column;
import org.daypilot.data.ColumnCollection;
import org.daypilot.date.DateTime;
import org.daypilot.date.DayOfWeek;
import org.daypilot.date.Week;
import org.daypilot.json.JSONArray;
import org.daypilot.json.JSONException;
import org.daypilot.json.JSONObject;
import org.daypilot.ui.args.calendar.CommandArgs;
import org.daypilot.ui.args.calendar.EventClickArgs;
import org.daypilot.ui.args.calendar.EventMoveArgs;
import org.daypilot.ui.args.calendar.EventResizeArgs;
import org.daypilot.ui.args.calendar.TimeRangeSelectedArgs;
import org.daypilot.ui.enums.HeightSpec;
import org.daypilot.ui.enums.TimeFormat;
import org.daypilot.ui.enums.UpdateType;
import org.daypilot.ui.enums.ViewType;
import org.daypilot.util.LocaleParser;
import org.daypilot.util.Str;
import org.daypilot.util.Xml;

public class DayPilotCalendar {

	// general
	private HttpServletRequest request;
	private HttpServletResponse response;
	
	private String dataIdField = "id";
	private String dataTextField = "text";
	private String dataStartField = "start";
	private String dataEndField = "end";
	
	private Collection<?> events;

	private UpdateType updateType = UpdateType.NONE;
	
	// properties
	private DateTime startDate;
	private int days;
	private String id;
	
	private boolean eventHeaderVisible;
	private TimeFormat timeFormat = TimeFormat.AUTO;
	
	private HeightSpec heightSpec;
	private int businessBeginsHour;
	private int businessEndsHour;
	private ViewType viewType = ViewType.DAYS;
	private String backColor;
	private String headerDateFormat = "EEE, d MMMM yyyy";
	private String hourFontFamily;
	private String hourFontSize;
	private String hourFontColor;
	private Locale locale = Locale.getDefault();

	public JSONObject callback(JSONObject input) {
		try {
			String action = input.getString("action");
			JSONObject params = input.optJSONObject("parameters");
			JSONObject data = input.optJSONObject("data");
			JSONObject header = input.getJSONObject("header");
			
			backColor = header.optString("backColor");
			businessBeginsHour = header.optInt("businessBeginsHour");
			businessEndsHour = header.optInt("businessEndsHour");
			days = header.getInt("days"); 
			eventHeaderVisible = header.optBoolean("eventHeaderVisible");
			heightSpec = HeightSpec.parseSoft(header.optString("heightSpec"));
			hourFontFamily = header.optString("hourFontFamily");
			hourFontSize = header.optString("hourFontSize");
			hourFontColor = header.optString("hourFontColor");
			id = header.getString("id");
			locale = LocaleParser.isValid(header.optString("locale")) ? LocaleParser.parse(header.getString("locale")) : locale; 
			startDate = DateTime.parseString(header.getString("startDate"));
			timeFormat = TimeFormat.isValid(header.optString("timeFormat")) ? TimeFormat.parse(header.getString("timeFormat")) : timeFormat;
			viewType = ViewType.parseSoft(header.optString("viewType"));

			sanityCheck();
			
			onPrepare();

			if (action.equals("Command")) {
				CommandArgs ea = CommandArgs.fromJSON(params, data);
				onCommand(ea);
			}
			else if (action.equals("EventClick")) {
				EventClickArgs ea = EventClickArgs.fromJSON(params, data);
				onEventClick(ea);
			}
			else if (action.equals("EventMove")) {
				EventMoveArgs ea = EventMoveArgs.fromJSON(params, data);
				onEventMove(ea);
			}
			else if (action.equals("EventResize")) {
				EventResizeArgs ea = EventResizeArgs.fromJSON(params, data);
				onEventResize(ea);
			}
			else if (action.equals("Init")) {
				onInit();
			}
			else if (action.equals("TimeRangeSelected")) {
				TimeRangeSelectedArgs ea = TimeRangeSelectedArgs.fromJSON(params, data);
				onTimeRangeSelected(ea);
			}
			else {
				throw new RuntimeException("Unknown callback type.");
			}

			onFinish();

			sanityCheck();

			JSONObject result = new JSONObject();
			
			// always
			result.put("UpdateType", getUpdateType());
			
			// send for EVENTS and FULL
			if (getUpdateType() != UpdateType.NONE) {
				result.put("Events", getEventsJSON());
			}

			
			// send for FULL
			if (getUpdateType() == UpdateType.FULL) {
				// generated collections
				result.put("Columns", getJsonColumns());

				// properties
				result.put("Days", getDays());
				result.put("StartDate", getStartDate());
				result.put("HeightSpec", heightSpec);
				result.put("BusinessBeginsHour", businessBeginsHour);
				result.put("BusinessEndsHour", businessEndsHour);
				result.put("ViewType", viewType);
				result.put("BackColor", backColor);
				result.put("EventHeaderVisible", eventHeaderVisible);
				result.put("TimeFormat", timeFormat);
				result.put("Locale", Str.toStringOrNull(locale));
			}
			
			return result;
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private void sanityCheck() {
		Ensure.inRange("businessBeginsHour", businessBeginsHour, 0, 23);
		Ensure.inRange("businessEndsHour", businessEndsHour, 0, 24);
		Ensure.inRange("days", days, 1, 100);
	}

	public void process(HttpServletRequest request, HttpServletResponse response) {
		try {
			this.request = request;
			this.response = response;
			
			JSONObject input = JSONParser.parse(request);
			JSONObject result = callback(input);
			response.getWriter().print(result);
			response.getWriter().close();
		}
		catch (JSONException e) {
			throw new RuntimeException(e);
		} catch (IOException e) {
			throw new RuntimeException(e);
		}
	}

	public String getDataValueField() {
		return dataIdField;
	}

	public void setDataValueField(String dataValueField) {
		this.dataIdField = dataValueField;
	}

	public String getDataTextField() {
		return dataTextField;
	}

	public void setDataTextField(String dataTextField) {
		this.dataTextField = dataTextField;
	}

	public String getDataStartField() {
		return dataStartField;
	}

	public void setDataStartField(String dataStartField) {
		this.dataStartField = dataStartField;
	}

	public String getDataEndField() {
		return dataEndField;
	}

	public void setDataEndField(String dataEndField) {
		this.dataEndField = dataEndField;
	}

	@SuppressWarnings("rawtypes")
	public Collection getEvents() {
		return events;
	}

	public void setEvents(Collection<?> events) {
		this.events = events;
	}

	public void update(UpdateType updateType) {
		this.updateType = updateType;
	}

	/**
	 * Updates the client (using CallBackUpdateType.EVENTS_ONLY).
	 */
	public void update() {
		updateType = UpdateType.EVENTS_ONLY;
	}

	public HttpServletRequest getRequest() {
		return request;
	}

	public HttpServletResponse getResponse() {
		return response;
	}

	public UpdateType getUpdateType() {
		return updateType;
	}

	public void setUpdateType(UpdateType updateType) {
		this.updateType = updateType;
	}

	
	private JSONArray getEventsJSON() {

		try {

			JSONArray array = new JSONArray();
			if (events == null) {
				return array;
			}

			if (events.size() == 0) {
				return array;
			}

			for (Object o : events) {

				JSONObject e = new JSONObject();

				EventBean map = new EventBean(o);

				e.put("Value", map.getString(dataIdField));
				e.put("Text", map.getString(dataTextField));
				e.put("Start", map.getDateTime(dataStartField));
				e.put("End", map.getDateTime(dataEndField));

				array.put(e);
			}

			return array;
		} catch (RuntimeException e) {
			throw e;
		} catch (Exception e) {
			throw new RuntimeException(e);
		}
	}

	private JSONObject getHeaderJson(Column c) {

		JSONObject result = new JSONObject();
		result.put("Name", c.getName());
		result.put("Value", c.getValue());
		result.put("Start", c.getDate());
		result.put("InnerHTML", Xml.escape(c.getName()));
    	
		return result;
	}
	
	private JSONArray getJsonColumnCollection(ColumnCollection collection) {
		JSONArray result = new JSONArray();
        for (Column column : collection)
        {
        	result.put(getHeaderJson(column));
        }
        return result;
	}
	
	
	private JSONArray getJsonColumns() {
		return getJsonColumnCollection(getColumnsResolved());
	}
	
    private ColumnCollection getColumnsResolved() {
		ColumnCollection result = new ColumnCollection();
        for (int i = 0; i < getDays(); i++)
        {
            DateTime date = getStartDate().addDays(i);

            Column col = new Column(date.toString(headerDateFormat, locale), null);
            col.setDate(date);
            
            result.add(col);

        }
        return result;
    }

    
	public void onInit() throws Exception {
	}

	public void onEventClick(EventClickArgs ea) throws Exception {
	}

	public void onEventMove(EventMoveArgs ea) throws Exception {
	}
	
	public void onEventResize(EventResizeArgs ea) throws Exception {
	}
	
	public void onCommand(CommandArgs ea) throws Exception {
	}
	
	public void onTimeRangeSelected(TimeRangeSelectedArgs ea) throws Exception {
	}
	
	public void onFinish() throws Exception {
	}

	public void onPrepare() throws Exception {
	}

	public DateTime getVisibleStart() {
		return startDate.addHours(0);
	}
	
	public DateTime getVisibleEnd() {
		DateTime last = getStartDate().addDays(getDays());
		return last;
	}

	public DateTime getStartDate() {
		switch (viewType) {
			case WORKWEEK:
				return Week.firstWorkingDayOfWeek(startDate);
			case WEEK:
				return Week.firstDayOfWeek(startDate, getFirstDayOfWeek());
		}

		return startDate;
	}

	public void setStartDate(DateTime startDate) {
		this.startDate = startDate;
	}

	public int getDays() {
		switch (viewType) {
			case DAY:
				return 1;
			case WORKWEEK:
				return 5;
			case WEEK:
				return  7;
		}

		return days;
	}

	public void setDays(int days) {
		this.days = days;
	}

	public HeightSpec getHeightSpec() {
		return heightSpec;
	}

	public void setHeightSpec(HeightSpec heightSpec) {
		this.heightSpec = heightSpec;
	}

	public int getBusinessBeginsHour() {
		return businessBeginsHour;
	}

	public void setBusinessBeginsHour(int businessBeginsHour) {
		this.businessBeginsHour = businessBeginsHour;
	}

	public int getBusinessEndsHour() {
		return businessEndsHour;
	}

	public void setBusinessEndsHour(int businessEndsHour) {
		this.businessEndsHour = businessEndsHour;
	}

	public ViewType getViewType() {
		return viewType;
	}

	public void setViewType(ViewType viewType) {
		this.viewType = viewType;
	}

	public String getBackColor() {
		return backColor;
	}

	public void setBackColor(String backColor) {
		this.backColor = backColor;
	}

	public String getHeaderDateFormat() {
		return headerDateFormat;
	}

	public void setHeaderDateFormat(String headerDateFormat) {
		this.headerDateFormat = headerDateFormat;
	}

	public Locale getLocale() {
		return locale;
	}

	public void setLocale(Locale locale) {
		this.locale = locale;
	}

	public String getId() {
		return id;
	}

	public DayOfWeek getFirstDayOfWeek() {
		return DayOfWeek.fromConstant(Calendar.getInstance(getLocale()).getFirstDayOfWeek());
	}

	public boolean isEventHeaderVisible() {
		return eventHeaderVisible;
	}

	public void setEventHeaderVisible(boolean eventHeaderVisible) {
		this.eventHeaderVisible = eventHeaderVisible;
	}

	public TimeFormat getTimeFormat() {
		return timeFormat;
	}

	public void setTimeFormat(TimeFormat timeFormat) {
		this.timeFormat = timeFormat;
	}

	public String getHourFontFamily() {
		return hourFontFamily;
	}

	public void setHourFontFamily(String hourFontFamily) {
		this.hourFontFamily = hourFontFamily;
	}

	public String getHourFontSize() {
		return hourFontSize;
	}

	public void setHourFontSize(String hourFontSize) {
		this.hourFontSize = hourFontSize;
	}

	public String getHourFontColor() {
		return hourFontColor;
	}

	public void setHourFontColor(String hourFontColor) {
		this.hourFontColor = hourFontColor;
	}
}
