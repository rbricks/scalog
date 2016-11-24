package org.slf4j.impl;

import org.slf4j.spi.MDCAdapter;

import io.rbricks.scalog.mdc.ScalogMDCAdapter;

public class StaticMDCBinder {
  public static final StaticMDCBinder SINGLETON = new StaticMDCBinder();

  private StaticMDCBinder() { }

  public MDCAdapter getMDCA() {
    return new ScalogMDCAdapter();
  }

  public String getMDCAdapterClassStr() {
    return ScalogMDCAdapter.class.getName();
  }
}
