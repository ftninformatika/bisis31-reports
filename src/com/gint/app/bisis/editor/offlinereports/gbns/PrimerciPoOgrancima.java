package com.gint.app.bisis.editor.offlinereports.gbns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.gint.app.bisis.common.records.Record;
import com.gint.app.bisis.common.records.Subfield;
import com.gint.app.bisis.editor.offlinereports.OfflineReport;

public class PrimerciPoOgrancima implements OfflineReport {

  public void init(Map params) {
  }
  
  public void start() {
    report = new StringBuffer();
    branches = new HashMap();
  }
  
  public void handleRecord(Record rec) {
    if (rec == null)
      return;
    if (rec.getSubfieldContent("000a") == null)
      return;
    String pubType = rec.getSubfieldContent("000a").substring(0, 3);
    List temp = rec.getSubfields("996f");
    temp.addAll(rec.getSubfields("997f"));
    Iterator invs = temp.iterator();
    while (invs.hasNext()) {
      String cnt = ((Subfield)invs.next()).getContent();
      if (cnt == null || cnt.length() < 2)
        continue;
      String ogr = cnt.substring(0, 2);
      Branch branch = getBranch(ogr);
      increment(pubType, branch);
    }
  }
  private Branch getBranch(String sigla) {
    Branch b = (Branch)branches.get(sigla);
    if (b != null)
      return b;
    b = new Branch();
    b.sigla = sigla;
    branches.put(sigla, b);
    return b;
  }
  
  private void increment(String pubType, Branch branch) {
    if (pubType.equals("001"))
      branch.monographs++;
    else if (pubType.equals("004"))
      branch.serials++;
    else if (pubType.equals("006"))
      branch.articles++;
    else if (pubType.equals("017"))
      branch.maps++;
    else if (pubType.equals("027"))
      branch.musicals++;
    else if (pubType.equals("007"))
      branch.nonbooks++;
    else if (pubType.equals("037"))
      branch.electronic++;
    else if (pubType.equals("009"))
      branch.doctors++;
  }

  public class Branch implements Comparable {
    public String sigla = "";
    public int monographs = 0;
    public int serials = 0;
    public int articles = 0;
    public int maps = 0;
    public int musicals = 0;
    public int nonbooks = 0;
    public int electronic = 0;
    public int doctors = 0;
    
    public int getTotal() {
      return monographs+serials+articles+maps+musicals+nonbooks+electronic+doctors;
    }
    
    public String toString() {
      StringBuffer buff = new StringBuffer();
      buff.append("  <branch id=\"");
      buff.append(sigla);
      buff.append("\">\n");
      buff.append("    <monographs>");
      buff.append(monographs);
      buff.append("</monographs>\n");
      buff.append("    <serials>");
      buff.append(serials);
      buff.append("</serials>\n");
      buff.append("    <articles>");
      buff.append(articles);
      buff.append("</articles>\n");
      buff.append("    <maps>");
      buff.append(maps);
      buff.append("</maps>\n");
      buff.append("    <musicals>");
      buff.append(musicals);
      buff.append("</musicals>\n");
      buff.append("    <nonbooks>");
      buff.append(nonbooks);
      buff.append("</nonbooks>\n");
      buff.append("    <electronic>");
      buff.append(electronic);
      buff.append("</electronic>\n");
      buff.append("    <doctors>");
      buff.append(doctors);
      buff.append("</doctors>\n");
      buff.append("    <total>");
      buff.append(getTotal());
      buff.append("</total>\n");
      buff.append("  </branch>\n");
      return buff.toString();
    }
    
    public int compareTo(Object o) {
      if (o instanceof Branch) {
        Branch b = (Branch)o;
        return sigla.compareTo(b.sigla);
      }
      return 0;
    }
  }
  
  public void stop() {
    List branchList = new ArrayList();
    branchList.addAll(branches.values());
    Collections.sort(branchList);
    
    report.append("<report date=\"");
    report.append(sdf.format(new Date()));
    report.append("\">\n");
    Iterator it = branchList.iterator();
    while (it.hasNext())
      report.append(it.next().toString());
    report.append("</report>\n");
  }
  
  public String getReport() {
    return report.toString();
  }
  
  public String getFileName() {
    return "PrimerciPoOgrancima.xml";
  }
  
  private StringBuffer report;
  private HashMap branches;
  private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
}
