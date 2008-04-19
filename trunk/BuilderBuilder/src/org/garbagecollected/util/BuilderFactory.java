/**
 * Copyright (C) 2007 Robbie Vanbrabant
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.garbagecollected.util;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.HashMap;
import java.util.Map;

/**
 * Creates objects that fake a builder object for a given {@link Builder} type
 * based interface. Expects that build "reader" and "writer" methods have the
 * same name.
 * 
 * @author Robbie Vanbrabant (robbie.vanbrabant@gmail.com)
 */
public class BuilderFactory {
  @SuppressWarnings("serial")
  private static Map<Class<?>, ?> PRIMITIVE_DEFAULTS = 
    new HashMap<Class<?>, Object>() {{
      put(int.class, 0);
      put(long.class, 0L);
      put(boolean.class, false);
      put(byte.class, 0);
      put(short.class, 0);
      put(float.class, 0.0F);
      put(double.class, 0.0D);
      put(char.class, '\u0000');
    }};

  @SuppressWarnings("unchecked") // java.lang.reflect.Proxy is not generic
  public static <T extends Builder<V>, V> T make(Class<T> spec,
      BuilderCallback<T, V> callback) {
    return (T) Proxy.newProxyInstance(spec.getClassLoader(),
        new Class[] { spec },
        new BuilderInvocationHandler<T, V>(spec, callback));
  }

  private static class BuilderInvocationHandler<T extends Builder<V>, V>
      implements InvocationHandler {
    private Class<T> spec;
    private BuilderCallback<T, V> callback;
    private Map<String, Object> methodsToValues = new HashMap<String, Object>();

    private BuilderInvocationHandler(Class<T> spec,
        BuilderCallback<T, V> callback) {
      this.spec = spec;
      this.callback = callback;
    }

    @SuppressWarnings("unchecked") // java.lang.reflect.Proxy is not generic
    public Object invoke(Object proxy, Method method, Object[] args)
        throws Throwable {
      if (isWriter(method, args)) {
        methodsToValues.put(method.getName(), args[0]);
        return proxy;
      } else if (isReader(method, args)) {
        if (isBuildReader(method)) {
          return callback.call((T) proxy);
        } else {
          Object value = methodsToValues.get(method.getName());
          if (value == null && method.getReturnType().isPrimitive()) {
            return PRIMITIVE_DEFAULTS.get(method.getReturnType());
          }
          return value;
        }

      } else {
        throw new IllegalStateException(String.format(
            "method '%s' is not a getter or a single argument setter",
                    method.getName()));
      }
    }

    private boolean isWriter(Method method, Object[] args) {
      return (args != null && args.length == 1 &&
              spec.isAssignableFrom(method.getReturnType()));
    }

    private boolean isReader(Method method, Object[] args) {
      return args == null
          && !Void.TYPE.isAssignableFrom(method.getReturnType());
    }

    private boolean isBuildReader(Method method) {
      return method.equals(Builder.class.getMethods()[0]);
    }
  }
}