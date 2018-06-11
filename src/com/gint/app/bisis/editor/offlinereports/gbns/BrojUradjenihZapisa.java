package com.gint.app.bisis.editor.offlinereports.gbns;

import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import com.gint.app.bisis.common.records.Record;
import com.gint.app.bisis.common.records.Subfield;
import com.gint.app.bisis.editor.offlinereports.OfflineReport;

public class BrojUradjenihZapisa implements OfflineReport {

  public String getFileName() {
    return "BrojUradjenihZapisa.xml";
  }

  public String getReport() {
    return report.toString();
  }

  public void handleRecord(Record rec) {
    if (rec == null)
      return;
    if (rec.getSubfield("000a") == null)
      return;
    String pubType = rec.getSubfieldContent("000a").substring(0, 3);
    Iterator it992b = rec.getSubfields("992b").iterator();
    while (it992b.hasNext()) {
      String cnt = ((Subfield)it992b.next()).getContent();
      if (cnt.length() < 3)
        continue;
      int pos = getDigitPos(cnt);
      if (pos == 0)
        continue;
      String type = cnt.substring(0, 2);
      String libName = "";
      if (pos != -1)
        libName = cnt.substring(2, pos);
      else
        libName = cnt.substring(2);
      Librarian lib = getLibrarian(libName);
      if (type.equals("cr"))
        increment(pubType, lib.initial);
      else if (type.equals("cy"))
        increment(pubType, lib.old);
      else if (type.equals("dp"))
        increment(pubType, lib.enriched);
      else if (type.equals("rd"))
        increment(pubType, lib.finalized);
    }
  }

  private Librarian getLibrarian(String libName) {
    Librarian lib = (Librarian)librarians.get(libName);
    if (lib != null)
      return lib;
    lib = new Librarian();
    lib.name = libName;
    librarians.put(libName, lib);
    return lib;
  }
  
  private void increment(String pubType, PubCount pubCount) {
    if (pubType.equals("001"))
      pubCount.monographs++;
    else if (pubType.equals("004"))
      pubCount.serials++;
    else if (pubType.equals("006"))
      pubCount.articles++;
    else if (pubType.equals("017"))
      pubCount.maps++;
    else if (pubType.equals("027"))
      pubCount.musicals++;
    else if (pubType.equals("007"))
      pubCount.nonbooks++;
    else if (pubType.equals("037"))
      pubCount.electronic++;
    else if (pubType.equals("009"))
      pubCount.doctors++;
  }
  
  private int getDigitPos(String s) {
    for (int i = 0; i < s.length(); i++)
      if (Character.isDigit(s.charAt(i)))
        return i;
    return -1;
  }

  public class Librarian {
    public String name = "";
    public PubCount initial = new PubCount();
    public PubCount old = new PubCount();
    public PubCount enriched = new PubCount();
    public PubCount finalized = new PubCount();
    
    public int getTotal() {
      return initial.getTotal() + old.getTotal() + enriched.getTotal() + 
          finalized.getTotal();
    }
    
    public String toString() {
      StringBuffer buff = new StringBuffer();
      buff.append("  <librarian name=\"");
      buff.append(name);
      buff.append("\">\n");
      buff.append("    <part name=\"Prvi unos\">\n");
      buff.append(initial.toString());
      buff.append("    </part>\n");
      buff.append("    <part name=\"Stari zapisi\">\n");
      buff.append(old.toString());
      buff.append("    </part>\n");
      buff.append("    <part name=\"Dora\u0111eni zapisi\">\n");
      buff.append(enriched.toString());
      buff.append("    </part>\n");
      buff.append("    <part name=\"Redigovani zapisi\">\n");
      buff.append(finalized.toString());
      buff.append("    </part>\n");
      buff.append("  </librarian>\n");
      return buff.toString();
    }
  }
  
  public class PubCount {
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
      buff.append("      <monographs>");
      buff.append(monographs);
      buff.append("</monographs>\n");
      buff.append("      <serials>");
      buff.append(serials);
      buff.append("</serials>\n");
      buff.append("      <articles>");
      buff.append(articles);
      buff.append("</articles>\n");
      buff.append("      <maps>");
      buff.append(maps);
      buff.append("</maps>\n");
      buff.append("      <musicals>");
      buff.append(musicals);
      buff.append("</musicals>\n");
      buff.append("      <nonbooks>");
      buff.append(nonbooks);
      buff.append("</nonbooks>\n");
      buff.append("      <electronic>");
      buff.append(electronic);
      buff.append("</electronic>\n");
      buff.append("      <doctors>");
      buff.append(doctors);
      buff.append("</doctors>\n");
      buff.append("      <total>");
      buff.append(getTotal());
      buff.append("</total>\n");
      return buff.toString();
    }
  }

  public void init(Map params) {
  }

  public void start() {
    report = new StringBuffer();
    librarians = new HashMap();
  }

  public void stop() {
    report.append("<report date=\"");
    report.append(sdf.format(new Date()));
    report.append("\">\n");
    Iterator libs = librarians.values().iterator();
    while (libs.hasNext())
      report.append(libs.next().toString());
    
    report.append("</report>\n");
  }

  private StringBuffer report;
  private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
  private HashMap librarians;
}
