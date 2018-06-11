package com.gint.app.bisis.editor.offlinereports;

import java.io.File;
import java.io.RandomAccessFile;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import com.gint.app.bisis.common.records.Record;
import com.gint.app.bisis.common.records.RecordFactory;
import com.gint.util.file.FileUtils;
import com.gint.util.file.INIFile;

public class ReportRunner {
  
  public static void main(String[] args) {
    if (args.length < 1) {
      System.err.println(
          "Usage: ReportRunner <config-file>");
      return;
    }
    INIFile iniFile = new INIFile(args[0]);
    reportSet = iniFile.getString("main", "REPORT_SET");
    recordFile = iniFile.getString("main", "RECORD_FILE");
    destinationDir = iniFile.getString("main", "DESTINATION_DIR");
    logFile = iniFile.getString("log", "LOG_FILE");
    
    if (!loadReports())
      return;

    RandomAccessFile records = null;
    try {
      records = new RandomAccessFile(recordFile, "r");
    } catch (Exception ex) {
      log.fatal("Cannot open records file: " + recordFile);
      return;
    }
    
    int count = 0;
    for (int i = 0; i < reports.size(); i++)
      getReport(i).start();
    log.info("Starting report generation...");
    try {
      String line = "";
      while ((line = records.readLine()) != null)  {
        count++;
        line = line.trim();
        if ("".equals(line))
          continue;
        Record rec = RecordFactory.fromUNIMARC(0, line);
        for (int i = 0; i < reports.size(); i++) {
          getReport(i).handleRecord(rec);
        }
      }
      for (int i = 0; i < reports.size(); i++) {
        getReport(i).stop();
        String report = getReport(i).getReport();
        String fileName = getReport(i).getFileName();
        org.apache.commons.io.FileUtils.writeStringToFile(
            new File(destinationDir + "/" + fileName), report, "UTF8");
      }
      log.info("Report generation finished. ");
    } catch (Exception ex) {
      ex.printStackTrace();
      log.fatal("Error running reports, line count: " + count + ". " + ex.getMessage());
    }
    
  }
  
  private static boolean loadReports() {
    reports = new ArrayList();
    try {
      String dirName = "/com/gint/app/bisis/editor/offlinereports/" + reportSet;
      String files[] = FileUtils.listFiles(ReportRunner.class, dirName);
      for (int i = 0; i < files.length; i++) {
        if (files[i].endsWith(".class")) {
          String piece = files[i].substring(1).replace('/', '.');
          String className = piece.substring(0, piece.length() - 6);
          Object o = null;
          try { o = Class.forName(className).newInstance(); } catch (Exception ex) { }
          if (o == null)
            continue;
          if (o instanceof OfflineReport) {
            OfflineReport report = (OfflineReport)o;
            reports.add(report);
            log.info("Report " + report.getFileName() + " loaded.");
          }
        }
      }
    } catch (Exception ex) {
      log.fatal("Error loading reports: " + ex.getMessage());
      return false;
    }
    return true;
  }
  
  private static OfflineReport getReport(int index) {
    return (OfflineReport)reports.get(index);
  }

  private static String reportSet;
  private static String recordFile;
  private static String destinationDir;
  private static String logFile;
  
  private static Log log = LogFactory.getLog(ReportRunner.class);
  private static List reports;
  
  private static SimpleDateFormat sdf = new SimpleDateFormat("hh:mm:ss");
}
