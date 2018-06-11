package com.gint.app.bisis.editor.reportservlets;

import java.io.File;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.commons.io.FileUtils;

public class OfflineReportServlet extends HttpServlet {

  public void init(ServletConfig cfg) throws ServletException {
    super.init(cfg);
    reportDir = cfg.getInitParameter("reportDir");
  }

  public void doGet(HttpServletRequest request, HttpServletResponse response)
      throws ServletException, IOException {
    response.setContentType("text/xml; charset=utf-8");
    request.setCharacterEncoding(response.getCharacterEncoding());
    String file = request.getParameter("reportFile");
    if (file == null) {
      errorPage(request, response);
      return;
    }
    String reportFile = reportDir + "/" + file;
    System.out.println("request for file: " + reportFile);
    String report = FileUtils.readFileToString(
        new File(reportDir + "/" + file), "UTF8");
    response.getWriter().println(report);
    response.getWriter().close();
  }

  private void errorPage(HttpServletRequest request,
      HttpServletResponse response) throws ServletException, IOException {
    PrintWriter out = response.getWriter();
    out.println("<html><body>Invalid call to OfflineReport: "
        + request.getParameter("reportFile") + " </body></html>");
    out.close();
  }

  private String reportDir;
}
