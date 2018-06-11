package com.gint.app.bisis.editor.offlinereports.gbns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Map;

import com.gint.app.bisis.common.records.Record;
import com.gint.app.bisis.editor.offlinereports.OfflineReport;

public class BrojZapisa implements OfflineReport {

  public String getFileName() {
    return "BrojZapisa.xml";
  }

  public String getReport() {
    return report.toString();
  }

  public void handleRecord(Record rec) {
    if (rec == null)
      return;
    if (rec.getSubfieldContent("000a") == null)
      return;
    totalRecords++;
    String pubType = rec.getSubfieldContent("000a").substring(0, 3);
    if (pubType.equals("001"))
      monographs++;
    else if (pubType.equals("004"))
      serials++;
    else if (pubType.equals("006"))
      articles++;
    else if (pubType.equals("017"))
      maps++;
    else if (pubType.equals("027"))
      musicals++;
    else if (pubType.equals("007"))
      nonbooks++;
    else if (pubType.equals("037"))
      electronic++;
    else if (pubType.equals("009"))
      doctors++;
  }

  public void init(Map params) {
  }

  public void start() {
    report = new StringBuffer();
  }

  public void stop() {
    report.append("<report date=\"");
    report.append(sdf.format(new Date()));
    report.append("\">\n");
    report.append("  <totalRecords>" + totalRecords + "</totalRecords>\n");
    report.append("  <monographs>" + monographs + "</monographs>\n");
    report.append("  <serials>" + serials + "</serials>\n");
    report.append("  <articles>" + articles + "</articles>\n");
    report.append("  <maps>" + maps + "</maps>\n");
    report.append("  <musicals>" + musicals + "</musicals>\n");
    report.append("  <nonbooks>" + nonbooks + "</nonbooks>\n");
    report.append("  <electronic>" + electronic + "</electronic>\n");
    report.append("  <doctors>" + doctors + "</doctors>\n");
    report.append("</report>\n");
  }
  
  private StringBuffer report;

  int totalRecords = 0;
  int monographs = 0;
  int serials = 0;
  int articles = 0;
  int maps = 0;
  int musicals = 0;
  int nonbooks = 0;
  int electronic = 0;
  int doctors = 0;

  private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
}
