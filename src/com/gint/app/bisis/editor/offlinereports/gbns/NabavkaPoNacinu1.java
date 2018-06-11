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
import com.gint.app.bisis.common.records.Field;
import com.gint.app.bisis.common.records.Subfield;
import com.gint.app.bisis.common.records.Subsubfield;
import com.gint.app.bisis.editor.offlinereports.OfflineReport;

public class NabavkaPoNacinu1 implements OfflineReport {

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
      Subfield sfv = f.getSubfield('v');
      if (sfv == null || sfv.getContent().equals("")) {
        //noAcquisitionType++;
        continue;
      }
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
      char t = sfv.getContent().toLowerCase().charAt(0);
      Branch b = getBranch(sigla);
      switch (t) {
        case 'a': // kupovina
        case 'k':
          switch (type) {
            case 1:
              b.cBoughtAdult++;
              break;
            case 2:
              b.cBoughtChild++;
              break;
            case 3:
              b.cBoughtSlik++;
              break;
          }
          break;
        case 'c': // poklon
        case 'p':
          switch (type) {
            case 1:
              b.cGiftAdult++;
              break;
            case 2:
              b.cGiftChild++;
              break;
            case 3:
              b.cGiftSlik++;
              break;
          }
          break;
        case 'o': // otkup
          switch (type) {
            case 1:
              b.cReboughtAdult++;
              break;
            case 2:
            case 3:
              b.cReboughtChild++;
              break;
          }
          break;
        case 'b': // razmena
          switch (type) {
            case 1:
              b.cExchangedAdult++;
              break;
            case 2:
            case 3:
              b.cExchangedChild++;
              break;
          }
          break;
        case 'f': // samostalno izdanje
        case 's':
          switch (type) {
            case 1:
              b.cSelfPrintAdult++;
              break;
            case 2:
            case 3:
              b.cSelfPrintChild++;
              break;
          }
          break;
      }
    }
  }
  
  private void addTotal(List list) {
    Branch t = new Branch();
    t.sigla = "UKUPNO";
    Iterator it = list.iterator();
    while (it.hasNext()) {
      Branch b = (Branch)it.next();
      t.cBoughtAdult += b.cBoughtAdult;
      t.cBoughtChild += b.cBoughtChild;
      t.cBoughtSlik += b.cBoughtSlik;
      t.cExchangedAdult += b.cExchangedAdult;
      t.cExchangedChild += b.cExchangedChild;
      t.cGiftAdult += b.cGiftAdult;
      t.cGiftChild += b.cGiftChild;
      t.cGiftSlik += b.cGiftSlik;
      t.cReboughtAdult += b.cReboughtAdult;
      t.cReboughtChild += b.cReboughtChild;
      t.cSelfPrintAdult += b.cSelfPrintAdult;
      t.cSelfPrintChild += b.cSelfPrintChild;
    }
    list.add(t);
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
  
  public class Branch implements Comparable {
    public String sigla = "";
    public int cBoughtAdult = 0;
    public int cBoughtChild = 0;
    public int cBoughtSlik = 0;
    public int cGiftAdult = 0;
    public int cGiftChild = 0;
    public int cGiftSlik = 0;
    public int cReboughtAdult = 0;
    public int cReboughtChild = 0;
    public int cSelfPrintAdult = 0;
    public int cSelfPrintChild = 0;
    public int cExchangedAdult = 0;
    public int cExchangedChild = 0;
    
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
      buf.append("\">\n    <total>");
      buf.append(total());
      buf.append("</total>\n    <totalBooks>");
      buf.append(totalBooks());
      buf.append("</totalBooks>\n    <adultBooks>");
      buf.append(adultBooks());
      buf.append("</adultBooks>\n    <childBooks>");
      buf.append(childBooks());
      buf.append("</childBooks>\n    <boughtSlik>");
      buf.append(cBoughtSlik);
      buf.append("</boughtSlik>\n    <boughtBooks>");
      buf.append(boughtBooks());
      buf.append("</boughtBooks>\n    <totalBought>");
      buf.append(totalBought());
      buf.append("</totalBought>\n    <giftBooks>");
      buf.append(giftBooks());
      buf.append("</giftBooks>\n    <giftSlik>");
      buf.append(cGiftSlik);
      buf.append("</giftSlik>\n    <otkup>");
      buf.append(cReboughtAdult+cReboughtChild);
      buf.append("</otkup>\n    <samizd>");
      buf.append(cSelfPrintAdult+cSelfPrintChild);
      buf.append("</samizd>\n    <exchange>");
      buf.append(cExchangedAdult+cExchangedChild);
      buf.append("</exchange>\n  </branch>");
      return buf.toString();
    }
    
    public int total() {
      return totalBooks() + totalSlik();
    }
    
    public int totalBooks() {
      return adultBooks() + childBooks();
    }
    
    public int totalSlik() {
      return cBoughtSlik + cGiftSlik;
    }
    
    public int adultBooks() {
      return cBoughtAdult+cGiftAdult+cReboughtAdult+cSelfPrintAdult+cExchangedAdult;
    }
    
    public int childBooks() {
      return cBoughtChild+cGiftChild+cReboughtChild+cSelfPrintChild+cExchangedChild;
    }
    
    public int boughtBooks() {
      return cBoughtAdult + cBoughtChild;
    }
    
    public int totalBought() {
      return boughtBooks() + cBoughtSlik;
    }
    
    public int giftBooks() {
      return cGiftAdult + cGiftChild;
    }
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
  
  public String getFileName() {
    return "NabavkaPoNacinuTekucaGodina.xml";
  }

  public String getReport() {
    return report.toString();
  }

  public void init(Map params) {
  }

  private StringBuffer report;
  private int currentYear;
  HashMap branches = new HashMap();
  private Date startDate;
  private Date endDate;
  SimpleDateFormat sdf = new SimpleDateFormat("dd.MM.yyyy.");
  SimpleDateFormat intern = new SimpleDateFormat("yyyyMMdd");
}
