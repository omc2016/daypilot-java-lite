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

import java.beans.BeanInfo;
import java.beans.IntrospectionException;
import java.beans.Introspector;
import java.beans.PropertyDescriptor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Hashtable;
import java.util.Map;

import org.daypilot.date.DateTime;

class EventBean {
	private Object bean = null;
	private Hashtable<String, Method> getters = new Hashtable<String, Method>();
	
	private Map<String, ?> map = null;
	
	public EventBean(Object object) throws IntrospectionException {
		
		if (object == null) {
			return;
		}
		
		if (object instanceof Map) {
			map = (Map<String, ?>) object;
		}
		else {
			bean = object;

			BeanInfo beanInfo = Introspector.getBeanInfo(bean.getClass());
			PropertyDescriptor[] properties = beanInfo.getPropertyDescriptors();

			for(PropertyDescriptor property: properties) {
				Method getter = property.getReadMethod();
				getters.put(property.getName(), getter);
			}

		}
	}
	
	private Object getObject(String property) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		if (map != null) {
			return getObjectFromMap(property);
		}
		else if (bean != null) {
			return getObjectFromBean(property);
		}
		else {
			return null;
		}
	}
	
	private Object getObjectFromMap(String property) {
		if (!map.containsKey(property)) {
			throw new RuntimeException("Key '" + property + "' not found in the supplied Map.");
		}
		return map.get(property);
	}
	
	private Object getObjectFromBean(String property) throws IllegalArgumentException, IllegalAccessException, InvocationTargetException {
		Method getter = getters.get(property);
		
		if (getter == null) {
			throw new RuntimeException("Property '" + property + "' not found in the supplied JavaBean.");
		}

		return getters.get(property).invoke(bean, new Object[] {});	
	}
	
	public Object get(String path) {
		if (path == null) {
			throw new IllegalArgumentException("property argument cannot be null");
		}
		
		String property = path;
		String next = "";
		if (path.contains(".")) {
			int point = path.indexOf('.');
			property = path.substring(0, point);
			next = path.substring(point + 1);
		}
		
		try {
			Object resolved = getObject(property); 
				
			if ("".equals(next)) {
				return resolved;
			}
			EventBean bean = new EventBean(resolved);
			return bean.get(next);
		}
		catch (Exception e) {
			throw new RuntimeException(e);
		}
	}
	
	public String getString(String property) {
		Object value = get(property);
		if (value == null) {
			return null;
		}
		return value.toString();
	}
	
	public DateTime getDateTime(String property) {
		return DateTime.parseAsLocal(get(property));
	}
	
	public boolean isNull(String property) {
		return get(property) == null;
	}
	
}
