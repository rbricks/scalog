package org.slf4j.impl;

import org.slf4j.Logger;
import org.slf4j.ILoggerFactory;

public interface LoggerFactoryInterface {
  public Logger getNewLogger(String name);
}
