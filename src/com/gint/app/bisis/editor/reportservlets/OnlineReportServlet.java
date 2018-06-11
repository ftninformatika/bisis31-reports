package com.gint.app.bisis.editor.reportservlets;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;


import com.gint.app.bisis.editor.onlinereports.OnlineReport;
import com.gint.app.bisis.editor.onlinereports.OnlineReportFactory;

public class OnlineReportServlet extends HttpServlet {

  public void init(ServletConfig cfg) throws ServletException {
    super.init(cfg);
    reportSet = cfg.getInitParameter("reportSet");
    recordsFile = cfg.getInitParameter("recordsFile");
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/xml; charset=utf-8");
    request.setCharacterEncoding(response.getCharacterEncoding());
    String onlineReport = request.getParameter("onlineReport");
    if (onlineReport == null) {
      errorPage(request, response);
      return;
    }
    OnlineReport rep = OnlineReportFactory.getReport(reportSet, onlineReport);
    if (rep == null) {
      errorPage(request, response);
      return;
    }
    String sStartDate = request.getParameter("startDate");
    String sEndDate = request.getParameter("endDate");
    HashMap params = new HashMap();
    params.put("startDate", sStartDate);
    params.put("endDate", sEndDate);
    String xml = rep.run(recordsFile, params);
    response.getWriter().println(xml);
    response.getWriter().close();
  }
  
  private void errorPage(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    PrintWriter out = response.getWriter();
    out.println("<html><body>Invalid call to OnlineReport. </body></html>");
    out.close();
  }


  private String reportSet;
  private String recordsFile;
}
