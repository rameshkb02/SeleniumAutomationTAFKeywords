package com.wipro.hps.common;

import com.ibm.db2.jcc.am.SqlException;
import com.wipro.taf.selenium.*;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.By;
import org.openqa.selenium.JavascriptExecutor;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.WebElement;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

public class generalutils {
    SeleniumAutomationManager mgr;
    SeleniumAutomationBrowser browser;
    SeleniumAutomationConfManager conf;
    SeleniumAutomationUtils utils;
    SeleniumAutomationLogger log;
    String keywordName;
    String xPath;
    private String parameter;
    Connection connection;
    private WebDriver webDriver;

    public generalutils(SeleniumAutomationBrowser browser_, SeleniumAutomationUtils utils_, SeleniumAutomationLogger log_, WebDriver webDriver_, SeleniumAutomationConfManager conf_, String action, String property, String param) {
        this.log = log_;
        this.browser = browser_;
        this.conf = conf_;
        this.webDriver = webDriver_;
        this.utils = utils_;

        this.keywordName = action;
        this.xPath = property;
        this.parameter = param;
    }

    public static String removeNull(String txt) {
        String nullString = "";
        if (txt != null) {
            return txt;
        }

        return nullString;
    }


    public static String toSqlDate(String dateMask, String dateValue, String requireddatemask) {
        String dateString = null;
        int MaskChoice = 0;
        String month, date, year;
        if (dateMask.equals("mm/dd/yyyy"))
            MaskChoice = 1;
        if (dateMask.equals("dd/mm/yyyy"))
            MaskChoice = 2;
        if (dateMask.equals("yyyy/mm/dd"))
            MaskChoice = 3;
        if (dateMask.equals("mm/dd/yy"))
            MaskChoice = 4;
        if (dateMask.equals("dd/mm/yy"))
            MaskChoice = 5;
        if (dateMask.equals("mm-dd-yyyy"))
            MaskChoice = 1;
        if (dateMask.equals("dd-mm-yyyy"))
            MaskChoice = 2;
        if ((dateMask.equals("yyyy-mm-dd")) || (dateMask.equals("yyyy-MM-dd")))
            MaskChoice = 3;
        if (dateMask.equals("mm-dd-yy"))
            MaskChoice = 4;
        if (dateMask.equals("dd-mm-yy"))
            MaskChoice = 5;
        switch (MaskChoice) {
            case 1:
                month = dateValue.substring(0, 2);
                date = dateValue.substring(3, 5);
                year = dateValue.substring(6, 10);

                if (requireddatemask.equals("yyyy-mm-dd")) {
                    dateString = year + "-" + month + "-" + date;
                }
                if (requireddatemask.equals("mm-dd-yyyy")) {
                    dateString = month + "-" + date + "-" + year;
                }
                if (requireddatemask.equals("mm/dd/yyyy")) {
                    dateString = month + "/" + date + "/" + year;
                }
                break;
            case 2:
                date = dateValue.substring(0, 2);
                month = dateValue.substring(3, 5);
                year = dateValue.substring(6, 10);

                if (requireddatemask.equals("yyyy-mm-dd")) {
                    dateString = year + "-" + month + "-" + date;
                }
                if (requireddatemask.equals("mm-dd-yyyy")) {
                    dateString = month + "-" + date + "-" + year;
                }
                if (requireddatemask.equals("mm/dd/yyyy")) {
                    dateString = month + "/" + date + "/" + year;
                }
                break;
            case 3:
                year = dateValue.substring(0, 4);
                month = dateValue.substring(5, 7);
                date = dateValue.substring(8, 10);

                if (requireddatemask.equals("yyyy-mm-dd")) {
                    dateString = year + "-" + month + "-" + date;
                }
                if (requireddatemask.equals("mm-dd-yyyy")) {
                    dateString = month + "-" + date + "-" + year;
                }
                if (requireddatemask.equals("mm/dd/yyyy")) {
                    dateString = month + "/" + date + "/" + year;
                }
                break;
            case 4:
                month = dateValue.substring(0, 2);
                date = dateValue.substring(3, 5);
                year = dateValue.substring(6, 8);

                if (requireddatemask.equals("yyyy-mm-dd")) {
                    dateString = "20" + year + "-" + month + "-" + date;
                }
                if (requireddatemask.equals("mm-dd-yyyy")) {
                    dateString = month + "-" + date + "-20" + year;
                }
                if (requireddatemask.equals("mm/dd/yyyy")) {
                    dateString = month + "/" + date + "/" + year;
                }
                break;
            case 5:
                date = dateValue.substring(0, 2);
                month = dateValue.substring(3, 5);
                year = dateValue.substring(6, 8);

                if (requireddatemask.equals("yyyy-mm-dd")) {
                    dateString = "20" + year + "-" + month + "-" + date;
                }
                if (requireddatemask.equals("mm-dd-yyyy")) {
                    dateString = month + "-" + date + "-20" + year;
                }
                if (requireddatemask.equals("mm/dd/yyyy")) {
                    dateString = month + "/" + date + "/" + year;
                }
                break;
        }
        return dateString;
    }

    public static String getMethodName() {
        return "INSIDE THE METHOD---> " + java.lang.Thread.currentThread().getStackTrace()[2].getMethodName();
    }

    public int getFutureDateFromCurrentDate(String Parameter) {
        try {
            println("");
            println(getMethodName());
            println("");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.add(6, 5);
            String date = dateFormat.format(cal.getTime());
            System.out.println(date);
            String newdate = toSqlDate("yyyy-MM-dd", date, "mm/dd/yyyy");
            this.conf.addRuntimeData("futureDate", newdate);
            this.parameter = newdate;
            println("future date: " + this.parameter);
            println("Method Pass");
            return 0;
        } catch (Exception e) {
            return 1;
        }

    }


    public int getCurrentDate(String Parameter) {
        try {
            println("");
            println(getMethodName());
            println("");
            DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd");
            Calendar cal = Calendar.getInstance();
            cal.add(6, 0);
            String date = dateFormat.format(cal.getTime());
            System.out.println(date);
            String newdate = toSqlDate("yyyy-MM-dd", date, "mm/dd/yyyy");
            this.conf.addRuntimeData("currentDate", newdate);
            this.parameter = newdate;
            println("currentDate: " + this.parameter);
            println("Method Pass");
            println("Method Pass");
            return 0;
        } catch (Exception e) {
            return 1;
        }

    }

    /**
     * function to get data from DB2
     *
     * @param sql
     * @param rowCountLimit
     * @return a list of items from the resultset starting @ 1
     */
    public List<List<String>> getResultListFromDB(String sql, int rowCountLimit) {
        List<List<String>> resultArray = new ArrayList<>();
        try {
            println("Inside Keyword");
            String DB_Url = "jdbc:db2://PSI4MVS.HEALTHPLAN.COM:818/DB2J:retrieveMessagesFromServerOnGetMessages=true;emulateParameterMetaDataForZCalls=1;";
            /*conf.getParameterValue("DB_URL");*/
            String userName = conf.getParameterValue("USERNAME");
            String passWord = conf.getParameterValue("PASSWORD");
            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, passWord);

            println("Connection established");
            log.deb("Query:" + sql);
            Statement stmt = con.createStatement();
            ResultSet res;
            if (stmt.execute(sql)) {
                log.deb("Query executed");
                res = stmt.getResultSet();
                int loopi = 0;

                while (res.next()) {
                    loopi += 1;
                    if (loopi > rowCountLimit) {
                        break;
                    }
                    List<String> temp = new ArrayList<>();
                    for (int i = 1; i < res.getMetaData().getColumnCount(); i++) {
                        temp.add(res.getString(i));
                    }
                    resultArray.add(temp);
                }
            }
            if (resultArray.size() < 0) {
                println("No record found");
            } else {
                println(resultArray.toString());
            }

        } catch (SqlException e) {
            browser.strErrorInfo = "Connection not established due to following exception " + e;
            println("Connection not established due to following exception " + e);
        } catch (Exception e) {
            browser.strErrorInfo = "Exception during dbconnection " + e;
            println("Exception during dbconnection " + e);

        }
        return resultArray;
    }

    void println(String strLog) {
        this.log.deb(strLog);
    }


    public int checkTextInElement(String xPath,String text){
        try {
            WebElement webElement = webDriver.findElement(By.xpath(xPath));
            log.deb("Text to be compared " + webElement.getText() + "|");
            if (webElement.getText().trim().contains(text)){
                log.logExecutionResults("``", "Verify text ", "", "Passed", "Found " + text, 0, "check", "");
                return 0;
            }else{
                log.logExecutionResults("``", "Verify text ", "", "Failed", "Not Found " + text, 0, "check", "");
                return  1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return  1;
        }
    }

    public void scrolltoelement(String xPath) {
        try {
            WebElement we = webDriver.findElement(By.xpath(xPath));
            JavascriptExecutor jse = (JavascriptExecutor) webDriver;
            jse.executeScript("arguments[0].scrollIntoView()", we);
        } catch (Exception e) {
            browser.strErrorInfo = "Exception - " + e;
            log.deb("Exception - " + e);
            log.systemLog("Exception e\n" + e);
        }
    }

    public static void updateTestDataFile(String testDataPath,String assureNxtIterationName,int columnNum, String Data) {
        try {
            FileInputStream fis2 = new FileInputStream(testDataPath);
            XSSFWorkbook workbook = new XSSFWorkbook(fis2);
            XSSFSheet sh = workbook.getSheet("test");
            String scriptname;
            String iterationname;
            int lastRowNum = sh.getLastRowNum();

            for (int i = 1; i <= lastRowNum; i++) {
                scriptname = sh.getRow(i).getCell(1).getStringCellValue();
                iterationname = sh.getRow(i).getCell(2).getStringCellValue();
                System.out.println("Script " + scriptname);

                if (iterationname.contains(assureNxtIterationName)) {
                    System.out.println("Before:" + sh.getRow(i).getCell(columnNum).getStringCellValue());

                    String envChangeBefore = sh.getRow(i).getCell(columnNum).getStringCellValue();
                    String envChanged = envChangeBefore.substring(0,envChangeBefore.indexOf("="));
                    String finalEnv = envChanged + "=" + Data;
                    sh.getRow(i).getCell(columnNum).setCellValue(finalEnv);
                    System.out.println("After:" + sh.getRow(i).getCell(columnNum).getStringCellValue());
                    FileOutputStream output_file = new FileOutputStream(
                            new File(testDataPath));
                    workbook.write(output_file);
                    output_file.close();
                    break;
                }
            }
            fis2.close();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


}