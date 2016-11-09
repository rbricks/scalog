/**
 * Copyright (c) 2004-2011 QOS.ch, buildo s.r.l.s.
 * All rights reserved.
 *
 * Permission is hereby granted, free  of charge, to any person obtaining
 * a  copy  of this  software  and  associated  documentation files  (the
 * "Software"), to  deal in  the Software without  restriction, including
 * without limitation  the rights to  use, copy, modify,  merge, publish,
 * distribute,  sublicense, and/or sell  copies of  the Software,  and to
 * permit persons to whom the Software  is furnished to do so, subject to
 * the following conditions:
 *
 * The  above  copyright  notice  and  this permission  notice  shall  be
 * included in all copies or substantial portions of the Software.
 *
 * THE  SOFTWARE IS  PROVIDED  "AS  IS", WITHOUT  WARRANTY  OF ANY  KIND,
 * EXPRESS OR  IMPLIED, INCLUDING  BUT NOT LIMITED  TO THE  WARRANTIES OF
 * MERCHANTABILITY,    FITNESS    FOR    A   PARTICULAR    PURPOSE    AND
 * NONINFRINGEMENT. IN NO EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE
 * LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER IN AN ACTION
 * OF CONTRACT, TORT OR OTHERWISE,  ARISING FROM, OUT OF OR IN CONNECTION
 * WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE SOFTWARE.
 *
 */
package org.slf4j.impl;

import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;

import org.slf4j.Logger;
import org.slf4j.ILoggerFactory;

public class SimpleLoggerFactory implements ILoggerFactory {

  ConcurrentMap<String, Logger> loggerMap;

  public SimpleLoggerFactory() {
    loggerMap = new ConcurrentHashMap<String, Logger>();
  }

  private static LoggerFactoryInterface factory = null;

  public static void setLoggerFactoryInterface(LoggerFactoryInterface instance) {
    if (factory != null) {
      System.out.println("WARNING Re-setting LoggerFactoryInterface (this is ok in tests)");
    }
    factory = instance;
  }

  /**
   * Return an appropriate {@link SimpleLogger} instance by name.
   */
  public Logger getLogger(String name) {
    if (factory == null) {
      throw new RuntimeException("LoggerFactory instance not set");
    }
    Logger simpleLogger = loggerMap.get(name);
    if (simpleLogger != null) {
      return simpleLogger;
    } else {
      Logger newInstance = factory.getNewLogger(name);
      Logger oldInstance = loggerMap.putIfAbsent(name, newInstance);
      return oldInstance == null ? newInstance : oldInstance;
    }
  }

  /**
   * Clear the internal logger cache.
   *
   * This method is intended to be called by classes (in the same package) for
   * testing purposes. This method is internal. It can be modified, renamed or
   * removed at any time without notice.
   *
   * You are strongly discouraged from calling this method in production code.
   */
  void reset() {
    loggerMap.clear();
  }
}
