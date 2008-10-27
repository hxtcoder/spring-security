/* Copyright 2004, 2005, 2006 Acegi Technology Pty Limited
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package org.springframework.security.intercept.method;

import java.lang.reflect.Method;
import java.util.List;

import org.springframework.security.ConfigAttribute;
import org.springframework.security.intercept.ObjectDefinitionSource;


/**
 * Interface for <code>ObjectDefinitionSource</code> implementations
 * that are designed to perform lookups keyed on <code>Method</code>s.
 *
 * @author Ben Alex
 * @version $Id$
 */
public interface MethodDefinitionSource extends ObjectDefinitionSource {
    public List<ConfigAttribute> getAttributes(Method method, Class targetClass);
}
