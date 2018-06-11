package com.gint.app.bisis.editor.offlinereports.gbns;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import com.gint.app.bisis.common.records.Record;
import com.gint.app.bisis.editor.offlinereports.OfflineReport;
import com.gint.app.bisis.common.records.Field;
import com.gint.app.bisis.common.records.Subfield;
import com.gint.app.bisis.common.records.Subsubfield;

public class NabavkaPoUDK1 implements OfflineReport {

  public String getFileName() {
    return "NabavkaPoUDKZaTekucuGodinu.xml";
  }

  public String getReport() {
    return report.toString();
  }

  public void handleRecord(Record rec) {
    if (rec == null)
      return;
    Subfield sf996d = rec.getSubfield("996d");
    if (sf996d == null) {
      //noSignature++;
      return;
    }
    Subsubfield ssf996du = sf996d.getSubsubfield('u');
    if (ssf996du == null || ssf996du.getContent().equals("")) {
      //noSignature++;
      return;
    }
    String sig = ssf996du.getContent().trim();
    char c = getFirstDigit(sig);
    if (c == ' ') {
      //noSignature++;
      return;
    }
    int type = 1; // odrasli
    if (sig.indexOf("-93") != -1 || sig.indexOf(".053.2") != -1)
      type = 2; // deca
    if (sig.startsWith("087.5"))
      type = 3; // slikovnica
    
    Iterator it = rec.getFields("996").iterator();
    while (it.hasNext()) {
      Field f = (Field)it.next();
      Subfield sfw = f.getSubfield('w');
      if (sfw == null || sfw.getContent().equals("")) {
        //noBranch++;
        continue;
      }
      String sigla = sfw.getContent();
      Subfield sfo = f.getSubfield('o');
      if (sfo == null || sfo.getContent().equals("")) {
        //noInvDate++;
        continue;
      }
      Date invDate = null;
      try {
        invDate = intern.parse(sfo.getContent());
      } catch (Exception ex) {
        //noInvDate++;
        continue;
      }
      if (invDate.compareTo(startDate) < 0 || invDate.compareTo(endDate) > 0)
        continue;
      Branch b = getBranch(sigla);
      if (type == 3) {
        b.slik++;
        continue;
      }
      switch (type) {
        case 1:
          switch (c) {
            case '0':
              b.adult0++;
              break;
            case '1':
              b.adult1++;
              break;
            case '2':
              b.adult2++;
              break;
            case '3':
              b.adult3++;
              break;
            case '4':
              b.adult4++;
              break;
            case '5':
              b.adult5++;
              break;
            case '6':
              b.adult6++;
              break;
            case '7':
              b.adult7++;
              break;
            case '8':
              b.adult8++;
              break;
            case '9':
              b.adult9++;
              break;
          }
          break;
        case 2:
          switch (c) {
            case '0':
              b.child0++;
              break;
            case '1':
              b.child1++;
              break;
            case '2':
              b.child2++;
              break;
            case '3':
              b.child3++;
              break;
            case '4':
              b.child4++;
              break;
            case '5':
              b.child5++;
              break;
            case '6':
              b.child6++;
              break;
            case '7':
              b.child7++;
              break;
            case '8':
              b.child8++;
              break;
            case '9':
              b.child9++;
              break;
          }
          break;
      }
    }
  }
  
  public char getFirstDigit(String s) {
    if (s.length() == 0)
      return ' ';
    int pos = 0;
    if (s.charAt(0) == '(') {
      pos = s.indexOf(')') + 1;
      if (pos == 0 || pos == s.length())
        pos = 1;
    }
    while (pos < s.length() && !Character.isDigit(s.charAt(pos)))
      pos++;
    if (pos == s.length())
      return ' ';
    return s.charAt(pos);
  }

  public void init(Map params) {
  }

  public void start() {
    report = new StringBuffer();
    Date now = new Date();
    Calendar cal = Calendar.getInstance();
    cal.setTime(now);
    currentYear = cal.get(Calendar.YEAR);
    cal.clear();
    cal.set(Calendar.DAY_OF_MONTH, 1);
    cal.set(Calendar.MONTH, Calendar.JANUARY);
    cal.set(Calendar.YEAR, currentYear);
    startDate = cal.getTime();
    cal.clear();
    cal.set(Calendar.DAY_OF_MONTH, 31);
    cal.set(Calendar.MONTH, Calendar.DECEMBER);
    cal.set(Calendar.YEAR, currentYear);
    endDate = cal.getTime();
  }

  public void stop() {
    List branchList = new ArrayList();
    branchList.addAll(branches.values());
    Collections.sort(branchList);
    addTotal(branchList);
    
    report.append("<report today=\"");
    report.append(sdf.format(new Date()));
    report.append("\" dateRange=\"");
    report.append(currentYear);
    report.append("\">\n");
    Iterator it = branchList.iterator();
    while (it.hasNext())
      report.append(it.next().toString());
    
    report.append("\n</report>\n");
  }

  private void addTotal(List list) {
    Branch t = new Branch();
    t.sigla = "UKUPNO";
    Iterator it = list.iterator();
    while (it.hasNext()) {
      Branch b = (Branch)it.next();
      t.adult0 += b.adult0;
      t.adult1 += b.adult1;
      t.adult2 += b.adult2;
      t.adult3 += b.adult3;
      t.adult4 += b.adult4;
      t.adult5 += b.adult5;
      t.adult6 += b.adult6;
      t.adult7 += b.adult7;
      t.adult8 += b.adult8;
      t.adult9 += b.adult9;
      t.child0 += b.child0;
      t.child1 += b.child1;
      t.child2 += b.child2;
      t.child3 += b.child3;
      t.child4 += b.child4;
      t.child5 += b.child5;
      t.child6 += b.child6;
      t.child7 += b.child7;
      t.child8 += b.child8;
      t.child9 += b.child9;
      t.slik += b.slik;
    }
    list.add(t);
  }
  
  public class Branch implements Comparable {
    public String sigla = "";
    public int adult0 = 0;
    public int adult1 = 0;
    public int adult2 = 0;
    public int adult3 = 0;
    public int adult4 = 0;
    public int adult5 = 0;
    public int adult6 = 0;
    public int adult7 = 0;
    public int adult8 = 0;
    public int adult9 = 0;
    public int child0 = 0;
    public int child1 = 0;
    public int child2 = 0;
    public int child3 = 0;
    public int child4 = 0;
    public int child5 = 0;
    public int child6 = 0;
    public int child7 = 0;
    public int child8 = 0;
    public int child9 = 0;
    public int slik = 0;
    
    public int compareTo(Object o) {
      if (o instanceof Branch) {
        Branch b = (Branch)o;
        return sigla.compareTo(b.sigla);
      }
      return 0;
    }
    
    public String toString() {
      StringBuffer buf = new StringBuffer();
      buf.append("\n  <branch id=\"");
      buf.append(sigla);
      buf.append("\">\n    <adult0>");
      buf.append(adult0);
      buf.append("</adult0>\n    <adult1>");
      buf.append(adult1);
      buf.append("</adult1>\n    <adult2>");
      buf.append(adult2);
      buf.append("</adult2>\n    <adult3>");
      buf.append(adult3);
      buf.append("</adult3>\n    <adult4>");
      buf.append(adult4);
      buf.append("</adult4>\n    <adult5>");
      buf.append(adult5);
      buf.append("</adult5>\n    <adult6>");
      buf.append(adult6);
      buf.append("</adult6>\n    <adult7>");
      buf.append(adult7);
      buf.append("</adult7>\n    <adult8>");
      buf.append(adult8);
      buf.append("</adult8>\n    <adult9>");
      buf.append(adult9);
      buf.append("</adult9>\n    <totalAdult>");
      buf.append(totalAdult());
      buf.append("</totalAdult>\n    <child0>");
      buf.append(child0);
      buf.append("</child0>\n    <child1>");
      buf.append(child1);
      buf.append("</child1>\n    <child2>");
      buf.append(child2);
      buf.append("</child2>\n    <child3>");
      buf.append(child3);
      buf.append("</child3>\n    <child4>");
      buf.append(child4);
      buf.append("</child4>\n    <child5>");
      buf.append(child5);
      buf.append("</child5>\n    <child6>");
      buf.append(child6);
      buf.append("</child6>\n    <child7>");
      buf.append(child7);
      buf.append("</child7>\n    <child8>");
      buf.append(child8);
      buf.append("</child8>\n    <child9>");
      buf.append(child9);
      buf.append("</child9>\n    <slik>");
      buf.append(slik);
      buf.append("</slik>\n    <totalChild>");
      buf.append(totalChild());
      buf.append("</totalChild>\n    <total>");
      buf.append(total());
      buf.append("</total>\n  </branch>");
      return buf.toString();
    }
    
    public int total() {
      return totalAdult() + totalChild();
    }
    
    public int totalAdult() {
      return adult0+adult1+adult2+adult3+adult4+adult5+adult6+adult7+adult8+adult9;
    }
    
    public int totalChild() {
      return child0+child1+child2+child3+child4+child5+child6+child7+child8+child9+slik;
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
  
  private StringBuffer report;
  private int currentYear;
  private Date startDate;
  private Date endDate;
  private SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
  private SimpleDateFormat intern = new SimpleDateFormat("yyyyMMdd");
  HashMap branches = new HashMap();
}
