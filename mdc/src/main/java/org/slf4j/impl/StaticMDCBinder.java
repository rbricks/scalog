package org.slf4j.impl;

import org.slf4j.spi.MDCAdapter;

import io.rbricks.slog.mdc.SlogMDCAdapter;

public class StaticMDCBinder {
  public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

  private StaticMDCBinder() { }

  public MDCAdapter getMDCA() {
    return new SlogMDCAdapter();
  }

  public String getMDCAdapterClassStr() {
    return SlogMDCAdapter.class.getName();
  }
}
