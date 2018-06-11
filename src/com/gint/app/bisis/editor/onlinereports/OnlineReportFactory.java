package com.gint.app.bisis.editor.onlinereports;


import com.gint.util.file.FileUtils;

public class OnlineReportFactory {

  public static OnlineReport getReport(String reportSet, String reportName) {
    try {
      String dirName = "/com/gint/app/bisis/editor/onlinereports/" + reportSet;
      String files[] = FileUtils.listFiles(OnlineReportFactory.class, dirName);
      for (int i = 0; i < files.length; i++) {
        if (files[i].endsWith(".class")) {
          String piece = files[i].substring(1).replace('/', '.');
          String className = piece.substring(0, piece.length() - 6);
          Object o = null;
          try { o = Class.forName(className).newInstance(); } catch (Exception ex) { }
          if (o == null)
            continue;
          if (o instanceof OnlineReport) {
            OnlineReport report = (OnlineReport)o;
            if (report.getName().equals(reportName))
              return report;
          }
        }
      }
    } catch (Exception ex) {
      return null;
    }
    return null;
  }
}
