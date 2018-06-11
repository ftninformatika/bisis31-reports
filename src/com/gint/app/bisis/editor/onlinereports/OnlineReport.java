package com.gint.app.bisis.editor.onlinereports;

import java.util.Map;

public interface OnlineReport {

  public String getName();
  
  public String run(String recordsFileName, Map params);
}
