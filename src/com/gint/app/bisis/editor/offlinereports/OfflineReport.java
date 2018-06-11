package com.gint.app.bisis.editor.offlinereports;

import java.util.Map;

import com.gint.app.bisis.common.records.Record;

public interface OfflineReport {

  public void init(Map params);
  
  public void start();
  
  public void handleRecord(Record rec);
  
  public void stop();
  
  public String getReport();
  
  public String getFileName();

}
