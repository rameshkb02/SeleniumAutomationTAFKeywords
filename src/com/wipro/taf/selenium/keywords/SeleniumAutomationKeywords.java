package com.wipro.taf.selenium.keywords;

import com.ibm.db2.jcc.am.SqlException;
import com.wipro.hps.businesslogic.*;
import com.wipro.hps.common.generalutils;
import com.wipro.taf.selenium.*;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.HttpClientBuilder;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.xssf.usermodel.XSSFCell;
import org.apache.poi.xssf.usermodel.XSSFRow;
import org.apache.poi.xssf.usermodel.XSSFSheet;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.openqa.selenium.*;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.interactions.Actions;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.Select;
import org.openqa.selenium.support.ui.WebDriverWait;
import org.w3c.dom.CharacterData;
import org.w3c.dom.Element;
import org.w3c.dom.Node;

import java.awt.*;
import java.awt.event.KeyEvent;
import java.io.*;
import java.sql.*;
import java.text.DateFormat;
import java.text.NumberFormat;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.Period;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.Date;
import java.util.List;
import java.util.NoSuchElementException;

public class SeleniumAutomationKeywords {

    public static int DateofBirth = 0;
    public static int IndvCommPrem = 0;
    public static int Smoker = 0;
    String keywordName;
    String xPath;
    String parameter;
    String carrier_path;
    SeleniumAutomationManager mgr;
    SeleniumAutomationBrowser browser;
    SeleniumAutomationConfManager conf;
    SeleniumAutomationUtils utils;
    SeleniumAutomationLogger log;
    Enrollment e_obj;
    Connection connection;

    ExchangeLink exchange_obj;
    SalesLink sales_obj;
    TagLine tag_obj;
    ServiceLink service_obj;
    generalutils general_obj;

    private WebDriver webDriver;

    public SeleniumAutomationKeywords(SeleniumAutomationManager aMgr) {
        mgr = aMgr;
        e_obj = new Enrollment();
    }

    public static int updateTestData() throws IOException {

        File masterfile = new File("C:\\Assure_Nxt\\Master Sheet.xlsx");
        File testfile = new File("C:\\Assure_Nxt\\smoke test\\testdata\\Internal Smoke test.xlsx");

        FileInputStream fis = new FileInputStream(masterfile);
        FileInputStream testfis = new FileInputStream(testfile);

        XSSFWorkbook MW = new XSSFWorkbook(fis);
        XSSFWorkbook testWB = new XSSFWorkbook(testfis);

        XSSFSheet worksheet = MW.getSheet("New Run");
        XSSFSheet testSheet = testWB.getSheet("test");

        String[] data_split = null;
        String[] carrier_split = null;
        String[] portal_split = null;
        String Region;
        String carrier;
        String portal = "";
        int row;
        int colcount = 0;
        int col;

        XSSFRow[] test_row = new XSSFRow[testSheet.getPhysicalNumberOfRows()];
        XSSFCell[][] test_cell = new XSSFCell[test_row.length][25];

        for (row = 1; row < test_row.length; row++) {
            testSheet.getRow(row).getCell(0).setCellValue("N");
        }
        for (row = 1; row < test_row.length; row++) {
            test_row[row] = testSheet.getRow(row);
            colcount = 10;

            for (col = 0; col < colcount; col++) {
                test_cell[row][col] = test_row[row].getCell(col);
            }
        }

        System.out.println("Total Row in Master sheet : " + worksheet.getPhysicalNumberOfRows());
        for (int i = 1; i < worksheet.getPhysicalNumberOfRows(); i++) {

            if (worksheet.getRow(i).getCell(2).getStringCellValue().equalsIgnoreCase("Y")) {

                portal_split = null;
                carrier_split = null;
                Region = worksheet.getRow(i).getCell(1).toString();
                portal_split = worksheet.getRow(i).getCell(3).toString().split("\\+");
                carrier_split = worksheet.getRow(i).getCell(0).toString().split("\\+");
                for (int r = 0; r < portal_split.length; r++) {
                    portal = portal_split[r].toLowerCase();
                    for (int j = 0; j < carrier_split.length; j++) {
                        carrier = carrier_split[j].toLowerCase();
                        System.out.println(Region + "  \t" + "  \t" + portal + "  \t" + carrier);

                        for (row = 1; row < testSheet.getPhysicalNumberOfRows(); row++) {

                            // System.out.println(test_cell[row][1].getStringCellValue());

                            if (test_cell[row][2].getStringCellValue().toLowerCase().contains(portal)
                                    && test_cell[row][2].getStringCellValue().toLowerCase().contains(carrier)) {
                                test_cell[row][0].setCellValue("Y");

                                for (col = 1; col < colcount; col++) {
                                    String cell_data = test_cell[row][col].getStringCellValue();
                                    // System.out.println(cell_data);
                                    if (cell_data.contains("UAT") || cell_data.contains("UFT")
                                            || cell_data.contains("QUA")) {
                                        if (!cell_data.contains(Region)) {
                                            String[] newvalue = cell_data.split(":");
                                            newvalue[1] = Region;
                                            test_cell[row][col].setCellValue(newvalue[0] + ":" + Region);
                                        }

                                    }

                                }
                            }
                        }

                    }
                }
            }
        }
        testfis.close();
        FileOutputStream testout = new FileOutputStream(testfile);
        testWB.write(testout);
        testWB.close();
        MW.close();
        return 0;

    }

    public static String getCharacterDataFromElement(Element e) {
        Node child = e.getFirstChild();
        if (child instanceof CharacterData) {
            CharacterData cd = (CharacterData) child;
            return cd.getData();
        }
        return "?";
    }

    public static boolean isWorkingDay(Calendar cal) {
        int dayOfWeek = cal.get(Calendar.DAY_OF_WEEK);
        if (dayOfWeek == Calendar.SUNDAY || dayOfWeek == Calendar.SATURDAY)
            return false;

        return true;
    }

    public static String dateFormat(String effectiveDate) {
        String formatedDate = null;
        try {
            SimpleDateFormat originalFormat = new SimpleDateFormat("yyyyMMdd");
            Date date = originalFormat.parse(effectiveDate.toString());
            SimpleDateFormat newFormat = new SimpleDateFormat("MM/dd/yyyy");
            formatedDate = newFormat.format(date);
            formatedDate = "'" + formatedDate;
            System.out.println(formatedDate);
            return formatedDate;
        } catch (Exception e) {
            return formatedDate;
        }

    }

    /**
     * @param
     * @throws Exception
     */
    public int executeKeyword(String action, String property, String param) throws Exception {
        // TODO Auto-generated method stub

        browser = mgr.getAutBrowser();
        utils = mgr.getUtils();
        log = mgr.getLog();
        webDriver = browser.getWebDriver();
        conf = mgr.getConf();

        keywordName = action;
        xPath = property;
        parameter = param;

        exchange_obj = new ExchangeLink(browser, utils, log, webDriver, conf, action, property, param);
        sales_obj = new SalesLink(browser, utils, log, webDriver, conf, action, property, param);
        tag_obj = new TagLine(browser, utils, log, webDriver, conf, action, property, param);
        service_obj = new ServiceLink(browser, utils, log, webDriver, conf, action, property, param);
        general_obj = new generalutils(browser, utils, log, webDriver, conf, action, property, param);
       
        int resultCode = 1;
        switch (keywordName.toUpperCase()) {

            case "SAMPLE":
                resultCode = sampleFunction(parameter);
                break;
            case "REMOVEDEPENDENT":
                resultCode = checkAndRemoveDependentFromTable(parameter);
                break;
            case "ROWCOUNT":
                resultCode = rownos(parameter);
                break;

            case "NAVIGATE":
                resultCode = navigation(parameter);
                break;

            case "BROKENLINKCHECK":
                resultCode = brokenLinkCheck();
                break;

            case "ASSERTPAGEBYURL":
                resultCode = assertPageByUrl(parameter);
                break;

            case "ASSERTPAGETITLE":
                resultCode = assertPageByTitle(parameter);
                break;

            case "ENTERCURRENTDATE":
                resultCode = sales_obj.enterCurrentDate(parameter);
                break;

            case "ISELEMENTPRESENT":
                resultCode = IsElementPresent(parameter);
                break;
            case "ELEMENTPRESENCECHECK":
                resultCode = elementPresent();
                break;

            case "VERIFYDROPDOWN":
                resultCode = validateDropDownForNull(parameter);
                break;

            case "COMPARETEXT":
                resultCode = compareTextwithUI(parameter);
                break;

            case "COMPARESTR":
                resultCode = compareTextwithSL(parameter);
                break;

            case "GETTEXT":
                resultCode = getText(parameter);
                break;

            case "CAPTURE_ILBCIM":
                resultCode = getIlbcimFromMainFramReply(parameter);
                break;

            case "CAPTURE_DTLCIM":
                resultCode = getDetailCimFromMainFramReply(parameter);
                break;

            case "SERVICELINKVALUES":
                resultCode = serviceLinkValues(parameter);
                break;

            case "GETVALUE_DB":
                resultCode = retriveValueFromDB(parameter);
                break;

            case "GET_INPUT":
                resultCode = get_input(parameter);
                break;

            case "DB2CONNECTS3":
                resultCode = DBConnectionServiceLinkS3(parameter);
                break;

            case "SLPCOMPARISONWITHDB2":
                resultCode = slpComparisonWithDB2(parameter);
                break;

            case "ISDATEEMPTY":
                resultCode = datefieldempty(parameter);
                break;

            case "TRIMINPUT":
                resultCode = trimInput(parameter);
                break;

            case "ISRADIOBTNSELECTED":
                resultCode = isRadioBtnClicked();
                break;

            case "VALIDATE":
                resultCode = validate(parameter);
                break;

            case "WINDOWHANDLE":
                resultCode = windowHandle(parameter);
                break;

            case "SCROLLVIEW":
                resultCode = scrollView();
                break;

            case "PRINTESCAPE":
                resultCode = printescape();
                break;

            case "VALIDATE_INPUT":
                resultCode = validateValue(parameter);
                break;

            case "BILL_HISTORY_TABLE":
                resultCode = getDynamicBillHistoryvalues(parameter);
                break;

            case "BILLHISTORY_AETNATABLE":
                resultCode = getDynamicBillHistoryAetnavalues(parameter);
                break;

            case "KEYSTAB":
                resultCode = KeysTab();
                break;

            case "SET_STARTDATE":
                resultCode = tempInsuranceStartDate(parameter);
                break;

            case "SELECT_A_STARTDATE":
                resultCode = selectStartDateA(parameter);
                break;

            case "SELECT_B_STARTDATE":
                resultCode = selectStartDateB(parameter);
                break;

            case "CAPTURECIMILB":
                resultCode = captureCimIlb(parameter);
                break;

            case "CAPTURECIMDTL":
                resultCode = captureCimDtl(parameter);
                break;

            case "GETDROPDOWNLISTCOUNT":
                resultCode = checkAccountCreatedornot(parameter);
                break;

            case "DTLCASECHECK":
                resultCode = trimDTLCaseNum(parameter);
                break;

            case "DATEBETWEEN":
                resultCode = DateBetweenInTable(parameter);
                break;

            case "BILLRERUN":
                resultCode = billRerun();
                break;

            case "RANDOMSELECT":
                resultCode = randomSelectOptions();
                break;

            case "VALIDATE_SELECT":
                resultCode = validateSelect(parameter);
                break;

            case "DEPENDENTDBVALIDATION":
                resultCode = dependentFunc(parameter);
                break;

            case "TABLECELLVALUE":
                resultCode = cellValueFromTbl(parameter);
                break;

            case "RETURNRTVALUE":
                resultCode = returnRuntimeValue(parameter);
                break;

            case "SPLITANDASSIGN":
                resultCode = splitAndAssign(parameter);
                break;

            case "VERIFYCOVERAGESPECIFICVALUE":
                resultCode = verifyCoverageSpecificValues();
                break;

            case "VERIFYTABLEHEADERS":
                resultCode = VerifyTableHeader(parameter);
                break;

            case "COMPARELISTSIZE":
                resultCode = compareListSize(parameter);
                break;

            case "COMPARELISTVALUE":
                resultCode = compareListValue(parameter);
                break;

            case "COMPARECASENAMEDB2":
                resultCode = slpCompareWithCasenameDB2(parameter);
                break;

            case "ISELEMENTEXIST":
                resultCode = IsElementExist(parameter);
                break;

            case "DBCONNECT":
                resultCode = DBConnection();
                break;

            case "RETRIEVEVALUEFROMDB":
                resultCode = retriveValueFromDB(parameter);
                break;

            case "ISDROPDOWNNULL":
                resultCode = validateDropDownForNull(parameter);
                break;

            case "GETCASEID":
                carrier_path = conf.getConfigValue("CARRIER");
                resultCode = getcaseid(parameter);
                break;

            case "GETLOGINDETAILS":
                carrier_path = conf.getConfigValue("CARRIER");
                resultCode = getcasedetails(parameter);
                break;

            case "CHECKVALUEINTABLE":
                resultCode = checkvalueintable(parameter);
                break;

            case "GETCONFNO":
                resultCode = getconfirmationno();
                break;

            case "TRIMINPUTCASE":
                resultCode = trimInputcasenum(parameter);
                break;

            case "SELECTDROPDOWNMATCH":
                resultCode = selectTheDropDownMatch(parameter);
                break;

            case "CALCULATEPERC":
                resultCode = calculatepercentage();
                break;

            case "SELECTEFTORCC":
                resultCode = selecEFTorCC(parameter);
                break;

            case "RETURNFAIL":
                resultCode = 1;
                break;

            case "ISELEMENTPRE":
                resultCode = isElementPresence(parameter);
                break;

            case "ISELEMENTENABLED":
                resultCode = isElementEnabled(parameter);
                break;

            case "SAVETHEDETAILS":
                carrier_path = conf.getConfigValue("CARRIER");
                resultCode = saveTheDetails(parameter);
                break;

            case "SAVETHEDETAILSMEMBER":
                carrier_path = conf.getConfigValue("CARRIER");
                resultCode = saveTheDetails_member(parameter);
                break;

            case "GETDETAILFROMXL":
                resultCode = getdetailfromxl(parameter);
                break;

            case "VALIDATEANDSAVETOXL":
                carrier_path = conf.getConfigValue("CARRIER");
                resultCode = validateAndSaveToXL(parameter);
                break;

            case "VALIDATEANDSAVETOXLMEMBER":
                carrier_path = conf.getConfigValue("CARRIER");
                resultCode = validateAndSaveToXL_member(parameter);
                break;

            case "SCROLL":
                resultCode = scroll(parameter);
                break;

            case "MAILTOQUOTEVALIDATION":
                resultCode = mailToQuoteValidation(parameter);
                break;

            case "DEMEANOR":
                resultCode = demeanorSelection(parameter);
                break;

            case "ETLOGDESC":
                resultCode = etlogNotes(parameter);
                break;

            case "CHANGEPHYSICALADDRESS":
                resultCode = changePhysicalAddress(parameter);
                break;

            case "CONTAINS":
                resultCode = contains(parameter);
                break;

            case "GETCOUNTYAREAFROMDB":
                resultCode = getCountyAreaFromDB(parameter);
                break;

            case "CHECKRATINGON":
                resultCode = checkRatingON(parameter);
                break;

            case "VERIFYRATE":
                resultCode = verifyRate(parameter);
                break;

            case "COMPARESTRING":
                resultCode = compareText(parameter);
                break;

            case "PAGERELOAD":
                resultCode = Pagereload();
                break;

            case "WAITTILLDISAPPEAR":
                resultCode = waittilldisappear(parameter);
                break;

            case "FAIL_TEST":
                resultCode = failtest(parameter);
                break;

            case "SELECT_REASON":
                resultCode = ScenarioReason(parameter);
                break;

            case "ERRORMSG":
                resultCode = errorMsglist(parameter);
                break;

            case "UPDATETESTDATA":
                resultCode = updateTestData();
                break;

            case "KEYTAB":
                resultCode = KeyTab();
                break;

            case "GETETLOG":
                resultCode = getETLog(parameter);
                break;

            case "GETIFCONDITION":
                resultCode = getifcondition(parameter);
                break;
            case "CHECKDEPENDENTTABLE":
                resultCode = checkDependentInTable(parameter);
                break;
            case "NAMEGENERATE":
                resultCode = NameGeneration();

                break;

            /*
             * General Utils Keyword
             *
             */

            /*
             * Exchange Link Scenarios Keyword
             *
             */
            case "VALIDATE_APTC_ENROLLEMENT":
                resultCode = exchange_obj.validateAptcEnrollement(parameter);
                break;

            case "VALIDATE_ADDRESS_CHANGE":
                resultCode = exchange_obj.validateAddress(parameter);
                break;

            case "VALIDATE_BROKER_CHANGE":
                resultCode = exchange_obj.validateBroker(parameter);
                break;

            case "VALIDATE_DEPENDENT":
                resultCode = exchange_obj.validateAddDependent(parameter);
                break;

            case "VALIDATE_CASE_TERMINATION_EC":
                resultCode = exchange_obj.validateCaseTerminationExistingCase(parameter);
                break;

            case "VALIDATE_ADD_DEPENDENT_ONLY":
                resultCode = exchange_obj.validateAddDependentOnly(parameter);
                break;

            case "VALIDATE_PLAN_CHANGE":
                resultCode = exchange_obj.validatePlanChange(parameter);
                break;

            case "VALIDATE_RENEWAL":
                resultCode = exchange_obj.validateRenewal(parameter);
                break;

            case "VALIDATE_TERM_DEPENDENT":
                resultCode = exchange_obj.validateTermDependent(parameter);
                break;

            case "VALIDATE_HIXID_CHANGE":
                resultCode = exchange_obj.validateHixIDChange(parameter);
                break;

            case "VALIDATE_REINSTATEMENT":
                resultCode = exchange_obj.validateReinstatement(parameter);
                break;

            case "VALIDATE_HIOSCHANGE":
                resultCode = exchange_obj.validateHiosChange(parameter);
                break;

            case "GET_ROWCOUNT":
                resultCode = exchange_obj.getDynamicWebtableRowCount(parameter);
                break;

            case "DESELECT_CHECK":
                resultCode = sales_obj.deselect_CheckBox(parameter);
                break;

            case "COMPARE_PRODUCT":
                resultCode = sales_obj.compareProduct(parameter);
                break;

            case "REPLACE_PRODUCT":
                resultCode = sales_obj.replaceProduct(parameter);
                break;

            case "VALIDATE_ADDTOCART":
                resultCode = sales_obj.validateAddtoCart(parameter);
                break;

            case "CLICK_SAVE":
                // resultCode = service_obj.clicksave(parameter);
                break;

            case "ADD_TO_COMPARE":
                resultCode = sales_obj.addToCompareScreen(parameter);
                break;

            case "VALIDATE_SORTBY_PREMIUM":
                resultCode = sales_obj.validateSortByPremium();
                break;

            case "VALIDATE_SORTBY_CARRIER":
                resultCode = sales_obj.validateSortByCarrier();
                break;

            case "VALIDATE_INSURANCE_FILTER":
                resultCode = sales_obj.validateInsuranceCompanyFilter(parameter);
                break;

            case "VALIDATE_RANGE_FILTER":
                resultCode = sales_obj.validateRangeFilter(parameter);
                break;

            case "VALIDATE_PLANTYPE_FILTER":
                resultCode = sales_obj.validatePlanTypeFilter(parameter);
                break;

            case "VALIDATE_DEDUCTIBLE_FILTER":
                resultCode = sales_obj.validateDeductibleFilter(parameter);
                break;

            case "READ_PLAN":
                resultCode = sales_obj.readPlan(parameter);
                break;
            /*
             * TagLine
             */

            case "VALIDATE_LANGUAGE_CONTACTUSLINK":
                resultCode = tag_obj.validateContactUsLink(parameter);
                break;

            case "SELECTSSOTARGET":
                resultCode = selectssotarget();
                break;

            /*
             * ServiceLink
             */
            case "OPEN_EZPAY_BUTTON":
                resultCode = service_obj.open_EZPAYButton(parameter);
                break;

            case "CHECK_EZPAY_BUTTON_STATE_CIGNA":
                resultCode = service_obj.checkEZPAYButtonStateCigna(parameter);
                break;

            case "CHECK_EFTACCOUNT_INTABLE":
                resultCode = service_obj.checkEFTAccountInTable(parameter);
                break;

            case "CHECK_EFTACCOUNT_STATE_INTABLE":
                resultCode = service_obj.checkEFTAccountStatusInTable(parameter);
                break;

            case "CHECK_EFT_AFTERADD":
                resultCode = service_obj.check_EFT_AfterAdd(parameter);
                break;

            case "CHECK_EFT_AFTERDELETE":
                resultCode = service_obj.check_EFT_AfterDelete(parameter);
                break;

            case "DELETE_EFTACCOUNT":
                resultCode = service_obj.delete_EFTAccount(parameter);
                break;

            case "ACTIVATE_EFTACCOUNT":
                resultCode = service_obj.activate_eft_Account(parameter);
                break;

            case "CHECK_EFT_STATUS_AFTERACTIVATE":
                resultCode = service_obj.check_EFT_Status_AfterActivate(parameter);
                break;

            case "CASE_NOTES":
                resultCode = casenotes(parameter);
                break;

            /*
             * Sales Link
             */
            case "EMAILIDFROMTEMPAPP":
                resultCode = sales_obj.emailidfromtempapp(parameter);
                break;

            case "ALERTHANDLE1":
                resultCode = sales_obj.alerthandle1();
                break;

            case "SALESLINK_ADMINLOGIN":
                resultCode = this.sales_obj.saleslink_Adminlogin2(this.parameter);
                break;

            case "SALESLINK_ADMINLOGIN11":
                resultCode = sales_obj.saleslink_Adminlogin(parameter);
                break;

            case "SALESLINK_ADMINLOGIN22":
                resultCode = sales_obj.saleslink_Adminlogin1(parameter);
                break;

            case "GETENROLLEMENTNUMBERFROMUIPASTEINAWS":
                resultCode = sales_obj.getenrollementnumberfromUIpasteinAWS(parameter);
                break;

            case "GETUSERNAMEFROMMAILPASTEINUI":
                resultCode = sales_obj.getusernamefrommailpasteinUI(parameter);
                break;

            case "GETPASSWORDFROMMAILPASTEINUI":
                resultCode = sales_obj.getpasswordfrommailpasteinUI(parameter);
                break;

            case "DBCHECK_DEPENDENT":
                resultCode = sales_obj.tpaDBCheckDependent(parameter);
                break;

            case "VALIDATE_LOGOCOLOR_SETUP_A_TEMP":
                resultCode = this.sales_obj.Validate_LogoColor_Setup_A_Temp();
                break;

            case "VAL_A_CROSSELL":
                resultCode = sales_obj.Validate_crossell_A();
                break;

            case "VAL_B_CROSSELL":
                resultCode = sales_obj.Validate_crossell_B();
                break;

            case "CONFIGPE_SUBSIDY_YES":
                resultCode = this.sales_obj.PE_Subsidy_Yes_A();
                break;

            case "CONFIGPE_SUBSIDY_NO":
                resultCode = this.sales_obj.PE_Subsidy_No_A();
                break;

            case "CONFIGPE_SUBSIDY_B_YES":
                resultCode = this.sales_obj.PE_Subsidy_Yes_B();
                break;

            case "VALIDATE_A_YES_WBE":
                resultCode = this.sales_obj.PE_WBE_YES_A();
                break;

            case "VALIDATE_A_NO_WBE":
                resultCode = this.sales_obj.PE_WBE_No_A();
                break;

            case "VALIDATE_B_YES_WBE":
                resultCode = this.sales_obj.PE_WBE_YES_B();
                break;

            case "VALIDATE_B_NO_WBE":
                resultCode = this.sales_obj.PE_WBE_No_B();
                break;

            case "CONFIGPE_A_ELIG_YES":
                resultCode = this.sales_obj.CheckElig_A_PE_Yes();
                break;

            case "CONFIGPE_A_ELIG_NO":
                resultCode = this.sales_obj.CheckElig_A_PE_No();
                break;

            case "CONFIGPE_B_ELIG_YES":
                resultCode = this.sales_obj.CheckElig_B_PE_Yes();
                break;

            case "CONFIGPE_B_ELIG_NO":
                resultCode = this.sales_obj.CheckElig_B_PE_No();
                break;

            case "VALIDATE_A_PREFCARRIER":
                resultCode = this.sales_obj.Validate_PrefCarrier_A();
                break;

            case "CONFIGPE_A_WBELIG_YES":
                resultCode = this.sales_obj.PE_WBEElig_Quote_Yes_A();
                break;
            case "CONFIGPE_A_WBELIG_NO":
                resultCode = this.sales_obj.PE_WBEElig_Quote_No_A();
                break;

            case "CONFIGPE_B_WBELIG_YES":
                resultCode = this.sales_obj.PE_WBEElig_Quote_Yes_B();
                break;
            case "CONFIGPE_B_WBELIG_NO":
                resultCode = this.sales_obj.PE_WBEElig_Quote_No_B();
                break;

            case "DBCHECK_CONFIG_PE":
                resultCode = sales_obj.tpaDBCheckConfig(parameter);
                break;

            case "TPADBCHECK":
                resultCode = sales_obj.tpaDBCheck(parameter);
                break;

            case "VALIDATE_LOGOCOLOR_SETUP_B_TEMP":
                resultCode = this.sales_obj.Validate_LogoColor_Setup_B_Temp();
                break;

            case "BEFOREPEDB":
                resultCode = sales_obj.beforePEDemoDB();
                break;

            case "AFTERPEDB":
                resultCode = sales_obj.afterPEDemoDB();
                break;

            case "GETFROMTESTMAIL":
                resultCode = sales_obj.getfrommail(parameter);
                break;

            case "DYNAMICCELLCLICK":
                resultCode = sales_obj.dynamiccellclick(parameter);
                break;

            case "COMPARE_DEFAULT":
                resultCode = this.sales_obj.compareDefaultSelect(parameter);
                break;

            case "ERRORNOTFOUND":
                resultCode = errorNotFound(parameter);
                break;
            case "CHECKALLBOX":
                resultCode = checkallbox();
                break;
            case "WARNINGNOTFOUND":
                resultCode = warningNotFound();
                break;
            default:
                println("Unknown Action :" + keywordName);
                resultCode = 1;
        }
        return resultCode;
    }

    public int NameGeneration() {
        // TODO Auto-generated method stub
        String name1 = "AU";
        String name2;
        DateFormat df = new SimpleDateFormat("ddMM_HH_mm_ss");
        Date dateobj = new Date();
        name1 = name1 + df.format(dateobj);
        name2 = "TE" + df.format(dateobj);
        println("User Name :" + name1);
        println("Password :" + name2);
        conf.addRuntimeData("USERNAME", name1);
        // String uname="USERNAME";
        conf.addRuntimeData("PASSWORD", name2);
        // conf.testDataRows.put(uname, name1);
        return 0;
    }

    public int checkDependentInTable(String parameter) {
        println("");
        println(generalutils.getMethodName());
        println("");
        println("parameter--->" + conf.getParameterValue(parameter));
        int depcount = 0;
        try {

            WebElement mytable = webDriver
                    .findElement(By.xpath(conf.getObjectProperties("CaseDetails_Dependent", "DependentDetails_Table")));
            // WebElement mytable =
            // webDriver.findElement(By.xpath("//table[@id='account_vault_accounts_table']"));

            List<WebElement> tbody_table = mytable.findElements(By.tagName("tbody"));

            int tbody_count = tbody_table.size();

            for (int tbody = 0; tbody < tbody_count; tbody++) {

                List<WebElement> tbody_row = tbody_table.get(tbody).findElements(By.tagName("tr"));
                int rows_count = tbody_row.size();
                for (int row = 0; row < 1; row++) {
                    depcount = 0;
                    List<WebElement> Columns_row = tbody_row.get(row).findElements(By.tagName("td"));

                    int columns_count = Columns_row.size();

                    for (int column = 0; column < 1; column++) {

                        String celtext = Columns_row.get(column).getText();

                        println("Value--->" + celtext);

                        if (column == 0) {
                            if (celtext.equals(parameter)) {
                                depcount = depcount + 1;
                                println("Value--->" + celtext);
                                break;
                            }
                        }

                    }
                    if (depcount == 1) {
                        break;
                    }

                }

                if (depcount == 1) {
                    break;
                }
            }
            if (depcount == 1) {
                println("EFT Acount Name Exists in table");
                return 0;
            } else {
                println("EFT Acount Name Not Exists in table");
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

    }

    /*
     * @Description This validates the row number in a table to be =<1
     *
     * @return Boolean
     *
     * @author Ramyashree
     */
    /*
     * public int rownos() { String strElement = xPath; int returnValue = 0;
     * List<WebElement> rownos = webDriver.findElements(By.xpath(strElement));
     *
     * println("the number of rows in the table is" + rownos.size()); if
     * (rownos.size() > 1) { println("This is not  a issue bill"); returnValue = 1;
     * println("return value is" + returnValue);
     *
     * } else if (rownos.size() == 1) { println("This is a issue bill"); returnValue
     * = 0; println("return value is" + returnValue);
     *
     * } else { println("no bill details found for the account"); returnValue = 1;
     * println("return value is" + returnValue); } println("return value is" +
     * returnValue); return returnValue; }
     */
    public int rownos(String parameter) {
        String strElement = xPath;
        int returnValue = 0;
        try {

            WebElement we = webDriver.findElement(By.xpath(strElement));

            /***** GET ALL THE TRs INSIDE THE WEB ELEMENT - TBODY *****/
            List<WebElement> tableRows = we.findElements(By.tagName("tr"));
            String dbQuery;
            int tblSize = tableRows.size();
            String rownos = Integer.toString(tblSize);
            println("tblSize: " + rownos);
            conf.addRuntimeData(parameter, rownos);
            return returnValue;
        } catch (Exception e) {
            returnValue = 1;
            browser.strErrorInfo = "" + e;
            return returnValue;
        }

    }

    /*
     * @Description To select Target in SSO page by URL
     *
     * @return Boolean
     *
     * @author Nandha
     */

    /*
     * @Description This navigates the page backward, forward and refreshes based on
     * the input parameter
     *
     * @Param Back/Forward/Refresh
     *
     * @return Boolean
     *
     * @author Ramyashree
     */
    public int navigation(String parameter) {
        int returnValue;
        try {
            returnValue = 0;
            if (parameter.equalsIgnoreCase("Refresh")) {
                webDriver.navigate().refresh();
                println("Refreshed the page");
                returnValue = 0;
            } else if (parameter.equalsIgnoreCase("Back")) {
                webDriver.navigate().back();
                println("Navigated back");
                returnValue = 0;
            } else if (parameter.equalsIgnoreCase("Forward")) {
                webDriver.navigate().forward();
                println("Navigated forward");
                returnValue = 0;
            } else if (parameter.contains("TO")) {
                String[] to = parameter.split("-");
                webDriver.navigate().to(to[1]);
                returnValue = 0;
            } else {
                println("No input found");
                returnValue = 1;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }

        return returnValue;
    }

    /*
     * @Description This validates the presence of an element in the web page
     *
     * @Param Boolean variable
     *
     * @return Boolean
     *
     * @author Ramyashree, Sheela
     */

    /*
     * @Description This validates if the link present on the page is active or
     * broken
     *
     * @return Boolean
     *
     * @author Ramyashree
     */
    public int brokenLinkCheck() {
        String strElement = xPath;
        int returnValue = 1;
        WebElement element = browser.getWebDriverElement(strElement, "xpath");
        HttpClient client = HttpClientBuilder.create().build();
        HttpGet request = new HttpGet(element.getAttribute("href"));
        try {
            HttpResponse response = client.execute(request);

            if (response.getStatusLine().getStatusCode() != 200) {
                println("The link is broken");
                println("The status code is" + response.getStatusLine().getStatusCode());
                return returnValue;
            } else {
                println("The link is working fine");
                returnValue = 0;
                println("The status code is" + response.getStatusLine().getStatusCode());
                return returnValue;

            }

        } catch (Exception e) {
            e.printStackTrace();
            return returnValue;
        }

    }

    /*
     * @Description This validates whether the element is present or not
     *
     * @return Boolean
     *
     * @author Ramyashree,Sheela
     */

    /*
     * @Description This validates the page title of expected versus actual
     *
     * @Param Expected page title
     *
     * @return Boolean
     *
     * @author Padma
     */
    public int assertPageByTitle(String parameter) {

        try {
            int returnValue = 1;
            String pageTitle = webDriver.getTitle();
            // String pageTitle = "Title";
            if (pageTitle.contains(parameter)) {
                returnValue = 0;
                return 0;

            } else {
                returnValue = 1;
                browser.strErrorInfo = "Page Title mismatch";
                this.browser.close();
                return 1;
            }

        } catch (Exception e) {
            browser.strErrorInfo = "" + e;
            this.browser.close();

            return 0;
            // TODO Auto-generated catch block

        }

    }

    public int selectssotarget() {
        // TODO Auto-generated method stub
        try {
            String url = webDriver.getCurrentUrl();
            String region = "";
            String value;
            String selectoption = xPath + "/option";

            if (url.contains("qua"))
                region = "qua.";
            if (url.contains("uat"))
                region = "uat.";
            if (url.contains("uft"))
                region = "uft.";
            println("Current URL: " + url + "-- Taken Region value is : " + region);

            List<WebElement> option = webDriver.findElements(By.xpath(selectoption));
            Select st = new Select(webDriver.findElement(By.xpath(xPath)));

            for (WebElement op : option) {
                if (op.getText().contains(region)) {
                    value = op.getAttribute("value");
                    st.selectByValue(value);
                    println("Selected value for index is : " + value);
                    break;
                }
            }
            return 0;
        } catch (Exception e) {
            println("Exception Thrown: " + e.toString());
            browser.strErrorInfo = "" + e;
            return 1;
        }
    }

    public int IsElementPresent(String parameter) {
        String strElement = xPath;
        println("Keyword");
        try {
            if (webDriver.findElement(By.xpath(strElement)).isDisplayed()) {
                println("Element present");
                conf.addRuntimeData(parameter, "True");
                println(parameter);
            }

            return 0;
        } catch (Exception e) {
            println("Element not present");
            conf.addRuntimeData(parameter, "False");
            return 0;
        }
    }

    public int elementPresent() {
        String strElement = xPath;
        int returnValue = 0;
        try {
            String textValue = webDriver.findElement(By.xpath(strElement)).getText();

            println(textValue);
            if (textValue.equalsIgnoreCase(null)) {
                println("Element not present");
                browser.strErrorInfo = " Element not Present in UI";
                this.browser.close();
                returnValue = 1;

            } else {
                println("Element present");

                returnValue = 0;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            println("Element not present");
            browser.strErrorInfo = " " + e;

            returnValue = 1;
            e.printStackTrace();
            this.browser.close();
        }
        return returnValue;

    }

    /*
     * @Description This checks if the dropdown is null or it has a value and
     * assigns it to a Boolean variable
     *
     * @return Boolean
     *
     * @param Boolean variable
     *
     * @author Ramyashree
     */
    public int validateDropDownForNull(String parameter) {
        try {
            String strElement = xPath;
            int returnValue = 1;
            int listSize = 0;

            Select options = new Select(webDriver.findElement(By.xpath(strElement)));

            List<WebElement> allOptions = options.getOptions();

            listSize = allOptions.size();

            println("The dropdown size:" + listSize);

            if (listSize == 1) {

                conf.addRuntimeData(parameter, "True");
                println("The dropdown has no value:" + parameter);

                returnValue = 0;
            } else if (listSize > 1) {
                println("The dropdown has values");
                conf.addRuntimeData(parameter, "False");
                println("The dropdown has values:" + parameter);
                returnValue = 0;
            }
            return returnValue;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            browser.strErrorInfo = " " + e;
            e.printStackTrace();
            this.browser.close();
            return 1;

        }
    }

    public int SelectDropDown() {
        try {

            int iCnt = 0;

            WebElement drpDwnList = webDriver.findElement(By.xpath(xPath));
            Select objSel = new Select(drpDwnList);
            List<WebElement> weblist = objSel.getOptions();

            // Taking the count of items
            iCnt = weblist.size();
            // Using Random class to generate random values
            Random num = new Random();
            int iSelect = num.nextInt(iCnt);
            // Selecting value from DropDownList
            objSel.selectByIndex(iSelect - 1);
            // Selected Value
            // System.out.println(drpDwnList.getAttribute("value"));
            println("Total List of Options : " + iSelect);
            println(drpDwnList.getAttribute("value"));
            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }

    }

    /*
     * @Description This validates the expected value against the actual value
     *
     * @return Expected value
     *
     * @param Boolean variable
     *
     * @author Ramyashree
     */
    public int compareTextwithUI(String parameter) {

        try {

            String expected = conf.getParameterValue(parameter);
            String Str1 = xPath + "[contains(text(),'" + expected + "')]";
            println("Expected Value: " + expected);
            println("Passing Value: " + parameter);
            println("Xpath :" + Str1);

            if (webDriver.findElement(By.xpath(Str1)).isDisplayed()) {
                println("Value " + expected + " Present in this table.");
                return 0;
            } else {
                println("Value " + expected + "not Present in this table.");
                browser.strErrorInfo = " value " + expected + "is not present in UI";
                this.browser.close();
                return 1;
            }

        } catch (Exception e) {
            browser.strErrorInfo = " " + e;
            println("Value is not present in the table.");
            this.browser.close();
            return 1;
        }
    }

    public int compareTextwithSL(String parameter) {

        try {
            parameter = conf.getParameterValue(parameter);
            String text = webDriver.findElement(By.xpath(xPath)).getText();
            println("Parameter Text : " + parameter);
            println("Actual Text : " + text);

            if (parameter.equalsIgnoreCase(text)) {
                println("\n");
                println("Text : " + parameter + " Present in this UI.");
                return 0;
            } else {
                println(parameter);
                println(text);
                browser.strErrorInfo = "Text : " + parameter + " not present in this UI.";
                this.browser.close();
                return 1;
            }

        } catch (Exception e) {
            println("Text is not present in this UI");
            browser.strErrorInfo = " " + e;
            this.browser.close();
            return 1;
        }
    }

    /*
     * @Description This captures the CimIlb value from text area and assignes it to
     * input variable
     *
     * @Specific Exchange Link
     *
     * @return Boolean
     *
     * @param CimIlb variable
     *
     * @author Ramyashree
     */

    public int compareText(String parameter) {

        try {
            parameter = conf.getParameterValue(parameter);
            String text = webDriver.findElement(By.xpath(xPath)).getText();
            println("Parameter Text : " + parameter);
            println("Actual Text : " + text);

            parameter = parameter.trim();
            text = text.trim();
            if (parameter.equalsIgnoreCase(text)) {
                println("Text : " + parameter + " Present in this UI.");
                return 0;
            } else {
                println(parameter);
                println(text);
                browser.strErrorInfo = "Text : " + parameter + " Present in this UI.";
                this.browser.close();

                return 1;
            }

        } catch (Exception e) {
            println("Text is not present in this UI");
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            return 1;
        }
    }

    /*
     * @Description This captures the Ilb Cim value from MainFram Reply text area in
     * Transaction Log and assignes it to input variable
     *
     * @Specific Exchange Link
     *
     * @return Boolean
     *
     * @param CimDtl variable
     *
     * @author Ramyashree
     */
    /*
     * public int getIlbcimFromMainFramReply(String parameter) {
     *
     * try { println("INSIDE  getIlbcimMainFramReply method"); boolean value = true;
     * TreeSet<String> set_obj = new TreeSet<String>(); String textArea =
     * webDriver.findElement(By.xpath(xPath)).getText();
     *
     * DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
     * DocumentBuilder db = dbf.newDocumentBuilder(); InputSource is = new
     * InputSource(); is.setCharacterStream(new StringReader(textArea));
     *
     * Document doc = db.parse(is); NodeList nodes =
     * doc.getElementsByTagName("issueStatus");
     *
     * // iterate the employees for (int i = 0; i < nodes.getLength(); i++) {
     * Element element = (Element) nodes.item(i);
     *
     * NodeList name = element.getElementsByTagName("cim"); Element line = (Element)
     * name.item(0); String val = getCharacterDataFromElement(line);
     * set_obj.add(val);
     *
     * if (set_obj.size() >= 2) { println("Size of Set" + set_obj.size()); // String
     * ilb=set_obj. println("ILBCIMVALUE==================>" + set_obj.first());
     * conf.addRuntimeData("ILBCIMVALUE", set_obj.first()); parameter =
     * set_obj.first();
     *
     * } else if (set_obj.size() == 0) {
     *
     * value = false; }
     *
     * }
     *
     * if (value = false) { println("getIlbcimMainFramReply method Failed"); return
     * 1; } else { println("END of  getIlbcimMainFramReply method"); return 0; }
     *
     * } catch (Exception e) { println("getIlbcimMainFramReply method Failed");
     * e.printStackTrace(); return 1; }
     *
     * }
     */

    /*
     * @Description This captures the Detail Cim value from MainFram Reply text area
     * in Transaction Log and assignes it to input variable
     *
     * @Specific Exchange Link
     *
     * @return Boolean
     *
     * @param CimDtl variable
     *
     * @author Ramyashree
     */
    /*
     * public int getDetailCimFromMainFramReply(String parameter) {
     *
     * try { println("INSIDE  getDetailCimMainFramReply method"); boolean value =
     * true; TreeSet<String> set_obj = new TreeSet<String>(); String textArea =
     * webDriver.findElement(By.xpath(xPath)).getText();
     * println("textArea-->"+textArea); DocumentBuilderFactory dbf =
     * DocumentBuilderFactory.newInstance(); DocumentBuilder db =
     * dbf.newDocumentBuilder(); InputSource is = new InputSource();
     * is.setCharacterStream(new StringReader(textArea));
     *
     * Document doc = db.parse(is); NodeList nodes =
     * doc.getElementsByTagName("issueStatus");
     *
     * // iterate the employees for (int i = 0; i < nodes.getLength(); i++) {
     * println("textArea-->"+textArea); Element element = (Element) nodes.item(i);
     *
     * NodeList name = element.getElementsByTagName("cim"); Element line = (Element)
     * name.item(0); String val = getCharacterDataFromElement(line);
     * set_obj.add(val);
     *
     * if (set_obj.size() >= 2) { System.out.println(set_obj.size());
     *
     * String detailsim = String.valueOf(Integer.parseInt(set_obj.first()) + 1);
     * println("DTLCIMVALUE==================>" + detailsim);
     * conf.addRuntimeData("DTLCIMVALUE", detailsim); parameter = detailsim; } else
     * if (set_obj.size() == 0) {
     *
     * value = false; }
     *
     * }
     *
     * if (value = false) { println("getDetailCimMainFramReply method Failed");
     * return 1; } else { println(" END of method getDetailCimMainFramReply");
     * return 0; }
     *
     * } catch (Exception e) { println("getDetailCimMainFramReply method Failed" );
     * println(""+e); e.printStackTrace(); return 1; }
     *
     * }
     */

    /*
     * @Description This gets the text value and assigns it to the variable passed
     *
     * @return Boolean
     *
     * @param Variable to which text will be assigned
     *
     * @author Chiranjeevi
     */
    public int getText(String parameter) {
        try {
            String text = webDriver.findElement(By.xpath(xPath)).getText();
            println("Text :" + text);

            conf.addRuntimeData(parameter, text);
            println("Value " + text + " Assigned to: " + parameter);
            // parameter=text;

            return 0;
        } catch (Exception e) {
            println("Issue in asigning value.");
            browser.strErrorInfo = "" + e;
            this.browser.close();
            return 1;
        }

    }

    /*
     * @Description This captures the CimDtl value from text area and assignes it to
     * input variable
     *
     * @Specific Exchange Link
     *
     * @return Boolean
     *
     * @param CimDtl variable
     *
     * @author Ramyashree
     */

    public int get_input(String parameter) {

        String text = "";
        try {
            text = webDriver.findElement(By.xpath(xPath)).getAttribute("value");
            conf.addRuntimeData(parameter, text);
            println("Value " + text + " Assigned to: " + parameter);
            return 0;
        } catch (Exception e) {
            println("Issue in asigning value.");
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            return 1;
        }
    }

    public int getIlbcimFromMainFramReply(String parameter) {

        try {

            String textArea = webDriver.findElement(By.xpath(xPath)).getText();
            println("cimDtlValue=================================>" + textArea);
            String[] testAreaList = textArea.split("cim");
            String[] cimIlbList = testAreaList[1].split(">");
            String cimIlb = cimIlbList[1];
            String cimIlbValue = cimIlb.substring(0, 8);
            println(cimIlbValue);
            conf.addRuntimeData("ILBCIMVALUE", cimIlbValue);
            parameter = cimIlbValue;
            println(parameter);

            return 0;
        } catch (Exception e) { // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }
    }

    public int getDetailCimFromMainFramReply(String parameter) {

        try {
            String textArea = webDriver.findElement(By.xpath(xPath)).getText();
            println("cimDtlValue=================================>" + textArea);
            String[] testAreaList = textArea.split("cim");
            String[] cimDtlList = testAreaList[3].split(">");
            String cimDtl = cimDtlList[1];
            String cimDtlValue = cimDtl.substring(0, 8);
            println(cimDtlValue);

            conf.addRuntimeData("DTLCIMVALUE", cimDtlValue);
            parameter = cimDtlValue;
            println(parameter);
            return 0;
        } catch (Exception e) {
        } // TODO
        return 1;
    }

    /*
     * @Description This captures the resultset form DB
     *
     * @Specific Service Link
     *
     * @return Boolean
     *
     * @param Variable to store resultset
     *
     * @author Ramyashree
     */

    /*
     * @Description This captures the CimIlb value from text area and assignes it to
     * input variable
     *
     * @Specific Exchange Link
     *
     * @return Boolean
     *
     * @param CASENUM/DOB/EFFDATE/NAME/PLAN/PREMIUM/SSN
     *
     * @author Ramyashree
     */
    public int serviceLinkValues(String parameter) {

        String text = webDriver.findElement(By.xpath(xPath)).getText();

        try {

            if (parameter.equalsIgnoreCase("CASENUM")) {
                conf.addRuntimeData("CASENUM", text);
            } else if (parameter.equalsIgnoreCase("DOB")) {
                conf.addRuntimeData("DOB", text);
            } else if (parameter.equalsIgnoreCase("EFFDATE")) {
                conf.addRuntimeData("EFFDATE", text);
            } else if (parameter.equalsIgnoreCase("NAME")) {
                conf.addRuntimeData("NAME", text);
            } else if (parameter.equalsIgnoreCase("PLAN")) {
                conf.addRuntimeData("PLAN", text);
            } else if (parameter.equalsIgnoreCase("PREMIUM")) {
                conf.addRuntimeData("PREMIUM", text);
            } else if (parameter.equalsIgnoreCase("SSN")) {
                conf.addRuntimeData("SSN", text);
            }

            println("Value " + text + " Assigned to: " + parameter);

            return 0;
        } catch (Exception e) {
            println("Issue in asigning value.");
            return 1;
        }

    }

    /*
     * @Description This queries the DB and assigns value to a variable 'dbValue'
     *
     * @return Boolean
     *
     * @param DB Query
     *
     * @author Ramyashree
     */
    public int retriveValueFromDB(String parameter) {

        int i = 0;
        try {
            println("Inside Keyword");
            String DB_Url = conf.getParameterValue("DB_URL");
            String userName = conf.getParameterValue("USERNAME");
            String passWord = conf.getParameterValue("PASSWORD");

            println("Query-->" + parameter);
            println("DB_Url+USDRNAME+PASSWORD------>" + DB_Url + " " + userName + " " + passWord);

            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, passWord);
            log.deb("Query:" + parameter);
            println("Connection established");
            String data = conf.getParameterValue(parameter);
            println(data);
            String val[] = data.split("\\+");
            String query = val[0];
            String dbValue = val[1];
            log.deb("Query:" + query);
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery(query);
            log.deb("Query Executed");
            log.deb("");
            while (res.next()) {
                i = i + 2;
                println(res.getString(1));
                conf.addRuntimeData(dbValue, res.getString(1));
                println("The value updated" + parameter);
            }
            println("Query:" + query + "  " + val[1] + "assigned to " + "dbValue");
            if (i > 0) {
                return 0;
            } else {
                browser.strErrorInfo = "No Record Found";
                this.browser.close();
                return 1;
            }

        } catch (SqlException e) {
            browser.strErrorInfo = "Connection not established due to following exception " + e;
            this.browser.close();
            return 1;
        } catch (Exception e) {
            browser.strErrorInfo = "Exception during dbconnection " + e;
            this.browser.close();
            return 1;
        }
    }

    /*
     * @Description This validates the date field with the given parameter
     *
     * @return Boolean
     *
     * @author Dhivya
     */

    public int DBConnectionServiceLinkS3(String parameter) {
        log.defaultExpected = "Click the given link.";
        try {
            if (webDriver == null) {
                println("Browser instance is null. Application not launched properly");
                return 1;
            }

            String DB_Url = "jdbc:db2://PSI4MVS.HEALTHPLAN.COM:818/DB2J:retrieveMessagesFromServerOnGetMessages=true;emulateParameterMetaDataForZCalls=1;";
            String userName = "dbalakr";
            String password = "hello124";
            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, password);
            log.defaultExpected = "Connection Established";

            Statement stmt = con.createStatement();

            String query = "SELECT SM.LIST_BILL_CIM " + "FROM UAT.GRP_LISTBILL_SUMM SM " + ",UAT.CASE_MASTER CM "
                    + "WHERE SM.LIST_BILL_CIM > '40000000' " + "AND SM.BILL_PERIOD > '2017-02-28' "
                    + "AND SM.LIST_BILL_CIM = CM.CASENAME#CIM " + "AND CM.ACTIVE_CODE IN ( " + "' G' " + ",'G' " + ") "
                    + "AND CM.OWNING_CARRIER = 'X2' " + " AND CM.PROD_LVL_OWNING_CARRIER = 'X2' " + " AND NOT EXISTS ( "
                    + " SELECT 'Y' " + " FROM UAT.GRP_LISTBILL_SUMM SM2 "
                    + "WHERE SM2.LIST_BILL_CIM = SM.LIST_BILL_CIM " + "AND SM2.BILL_PERIOD < '2017-02-28' " + ") "
                    + "GROUP BY SM.LIST_BILL_CIM " + "HAVING COUNT(*) < 2;";

            ResultSet res = stmt.executeQuery(query);
            while (res.next()) {
                println(res.getString(1));
                String cimValue = res.getString(1);
                conf.addRuntimeData(parameter, cimValue);
                println("The value updated" + parameter);
            }

            return 0;

        } catch (Exception e) {
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }

    }

    /*
     * @Description This gets the value from UI based on the parameter
     *
     * @Specific Service Link
     *
     * @return Boolean
     *
     * @param Variable to store resultset
     *
     * @author Ramyashree,Dhivya
     */

    /*
     * @Description This compares the slp values against DB values
     *
     * @Specific Service Link
     *
     * @return Boolean
     *
     * @param Slp values separated by comma
     *
     * @author Ramyashree
     */
    public int slpComparisonWithDB2(String parameter) {

        try {

            if (webDriver == null) {
                println("Browser instance is null. Application not launched properly");
                return 1;
            }

            String DB_Url = conf.getParameterValue("DB_URL");
            String userName = conf.getParameterValue("USERNAME");
            String passWord = conf.getParameterValue("PASSWORD");

            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, passWord);

            println("Connection established");
            println(parameter);

            String val[] = parameter.split(",");
            String str1 = conf.getParameterValue(parameter);

            String cim = conf.getParameterValue(val[0]);
            String plan = conf.getParameterValue(val[1]);
            println("Str1:" + str1 + ",CIM:" + cim + ",PLAN: " + plan);

            Statement stmt = con.createStatement();
            String text = null;

            // String[] val = parameter.split(",");

            println("Before Select:" + parameter);

            String query = "SELECT COV_VALUE FROM SIT.COVERAGE_HISTORY WHERE cim IN ('" + cim
                    + "') and status='C' and COV_ENTITY='HEALTH' and COV_QUALIFIER IN ('PLAN');";

            println("After Select");

            println(query);
            ResultSet res = stmt.executeQuery(query);

            if (res.next()) {
                text = res.getString(1);
            }

            if (plan.equalsIgnoreCase(text)) {
                println("SSN matched between SLP:" + plan + " and DB2:" + res.getString(1));
                return 0;
            } else {
                println(((res.getString(1))));
                println("SSN not matched between SLP:" + plan + " and DB2:" + res.getString(1));
                return 1;
            }

        } catch (Exception e) {
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }
    }

    /*
     * @Description - Get ETlog value
     *
     * @Specific Service Link
     *
     * @param Variable to store to Text
     *
     */

    public int datefieldempty(String parameter) {

        try {
            String parameterValue = conf.getParameterValue(parameter);
            println("Parameter Value:" + parameterValue);
            String uivalue = webDriver.findElement(By.xpath(xPath)).getAttribute("placeholder");
            println("UI Value:" + uivalue);
            if (parameterValue.equalsIgnoreCase(uivalue)) {
                return 0;
            } else {
                browser.strErrorInfo = "Value mismatch";
                this.browser.close();
                return 1;
            }
        } catch (Exception e) {
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            return 1;
        }

    }

    /*
     * @Description Checks whether the radio button is selected or not
     *
     * @Specific Service Link
     *
     * @return Boolean
     *
     * @param Variable to store resultset
     *
     * @author Padma
     */

    public int trimInput(String parameter) {

        String text = webDriver.findElement(By.xpath(xPath)).getText();

        try {

            if (parameter.equalsIgnoreCase("SSN")) {
                String inputSSN = text;

                println("Before Trim:" + inputSSN);

                String finalSSN = inputSSN.substring(7);

                println("After Trim:" + finalSSN);

                conf.addRuntimeData("SSN", finalSSN);

                println("Value " + finalSSN + " Assigned to: " + parameter);

                return 0;

            } else if (parameter.equalsIgnoreCase("DOB")) {

                String inputDate = text;

                println("Before Trim:" + inputDate);

                String result1[] = inputDate.split("/");

                String finalDate = result1[0] + result1[1] + result1[2];

                println("After Trim:" + finalDate);

                conf.addRuntimeData("DOB", finalDate);

                println("Value " + finalDate + " Assigned to: " + parameter);

                return 0;

            } else if (parameter.equalsIgnoreCase("ZIP")) {
                String input = text;
                println("Before Trim:" + input);
                String val[] = input.split(",");

                String inputAddress = conf.getParameterValue(val[0]);
                String inputZip = conf.getParameterValue(val[1]);

                String result1[] = inputZip.split("\\s+");
                String finalZip = result1[1];

                println("After trim:" + finalZip);

                conf.addRuntimeData("ZIP", finalZip);

                println("Value " + finalZip + " Assigned to: " + parameter);

                return 0;

            } else if (parameter.equalsIgnoreCase("CASENUM")) {
                String input = text;
                println("Before Trim:" + input);
                String val[] = input.split("\\s+");

                String CaseNum = conf.getParameterValue(val[3]);

                println("After trim:" + CaseNum);

                conf.addRuntimeData(parameter, CaseNum);

                println("Value " + CaseNum + " Assigned to: " + parameter);

                return 0;

            } else if (parameter.equalsIgnoreCase("ETLog_NO")) {
                String value = this.webDriver.findElement(By.xpath(this.xPath)).getText().replaceAll("\\D+", "")
                        .toString();
                this.conf.addRuntimeData(parameter, value);
                println("Value " + value + " Assigned to: " + parameter);
                return 0;
            } else if ((parameter.equalsIgnoreCase("CONFNO")) || (parameter.equalsIgnoreCase("ILB_Email"))) {
                String input[] = text.split(":");
                String conf_value = input[1].trim();
                println("Confirmation Number after Payment : " + conf_value);

                if ((parameter.equalsIgnoreCase("CONFNO"))) {
                    conf.addRuntimeData("CONFNO", conf_value);
                    return 0;
                } else if ((parameter.equalsIgnoreCase("ILB_Email"))) {
                    conf.addRuntimeData("ILB_Email", conf_value);
                    return 0;
                }
                return 0;
            } else if (parameter.equalsIgnoreCase("ILB_Type")) {
                String input[] = text.split(":");
                String conf_value = input[1].trim();
                println("Confirmation Number after Payment : " + conf_value);
                conf.addRuntimeData("ILB_Type", conf_value);
                return 0;
            } else if (parameter.equalsIgnoreCase("ILB_EffectiveDate")) {
                String input[] = text.split(":");
                String conf_value = input[1].trim();
                println("Confirmation Number after Payment : " + conf_value);
                conf.addRuntimeData("ILB_EffectiveDate", conf_value);
                return 0;
            } else if (parameter.equalsIgnoreCase("ILB_Hierarchy")) {
                String input[] = text.split(":");
                String conf_value = input[1].trim();
                println("Confirmation Number after Payment : " + conf_value);
                conf.addRuntimeData("ILB_Hierarchy", conf_value);
                return 0;
            } else if (parameter.equalsIgnoreCase("ILB_Application_ID")) {
                String input[] = text.split(":");
                String conf_value = input[1].trim();
                println("Confirmation Number after Payment : " + conf_value);
                conf.addRuntimeData("ILB_Application_ID", conf_value);
                return 0;
            } else if (parameter.equalsIgnoreCase("ILB_HIX")) {
                String input[] = text.split(":");
                String conf_value = input[1].trim();
                println("Confirmation Number after Payment : " + conf_value);
                conf.addRuntimeData("ILB_HIX", conf_value);
                return 0;
            } else if (parameter.equalsIgnoreCase("ILB_CIM")) {
                String input[] = text.split(":");
                String conf_value = input[1].trim();
                println("Confirmation Number after Payment : " + conf_value);
                conf.addRuntimeData("ILB_CIM", conf_value);
                return 0;
            } else if (parameter.equalsIgnoreCase("ILB_Carrier")) {
                String input[] = text.split(":");
                String conf_value = input[1].trim();
                println("Confirmation Number after Payment : " + conf_value);
                conf.addRuntimeData("ILB_Carrier", conf_value);
                return 0;
            } else if (parameter.equalsIgnoreCase("SMOKER_INDICATOR")) {
                String input[] = text.split(":");
                String conf_value = input[1].trim();
                println("Confirmation Number after Payment : " + conf_value);
                conf.addRuntimeData("SMOKER_INDICATOR", conf_value);
                return 0;
            } else if (parameter.equalsIgnoreCase("Agent_id")) {
                String input[] = text.split(":");
                String conf_value = input[1].trim();
                println("Confirmation Number after Payment : " + conf_value);
                conf.addRuntimeData("Agent_id", conf_value);
                return 0;

            } else if (parameter.equalsIgnoreCase("CIM")) {
                String input[] = text.split(":");
                String cim = input[1].trim();
                println("Confirmation Number after Payment : " + cim);
                conf.addRuntimeData("CIM", cim);
                return 0;
            } else if (parameter.equalsIgnoreCase("AMOUNT")) {
                println("Amount : " + text);

                int length = text.length();
                println("Length : " + length);
                String[] dot = text.split("\\.");
                println("Decimal Value :" + dot[1].length());
                println("first index :" + text.charAt(0));
                if ((dot[1].length() == 2) && ((text.charAt(0) == '$') || (text.charAt(0) == '-'))) {
                    return 0;

                } else {
                    browser.strErrorInfo = "Format of amount is incorrect";
                    this.browser.close();
                    return 1;

                }
            } else if (parameter.equalsIgnoreCase("DELIVERY")) {
                println("Delivery Mode :" + text);
                if ((text.equalsIgnoreCase("paper")) || (text.equalsIgnoreCase("webportal"))
                        || (text.equalsIgnoreCase("finbill"))) {

                    return 0;
                } else {
                    browser.strErrorInfo = "Delivery mode is not paper or webportal or finbill";
                    this.browser.close();
                    return 1;
                }
            } else if (parameter.equalsIgnoreCase("OUTOFPACKET")) {
                int returnValue = 1;
                try {

                    List<WebElement> webElements = webDriver.findElements(By.xpath(xPath));
                    int listSize = webElements.size();

                    for (int i = 0; i < listSize; i++) {
                        String uiValue = webElements.get(i).getText();
                        if (uiValue.equalsIgnoreCase("NOT APPLICABLE")) {
                            println("Ui Value " + uiValue);
                            returnValue = 0;
                        } else {
                            String[] Amount = uiValue.split("\\s+");
                            println("Amount 1 " + Amount[1]);
                            println("Amount 2 " + Amount[3]);

                            if (Amount[1].matches("\\$?[0-9]+\\,*[0-9]*")
                                    && Amount[3].matches("\\$?[0-9]+\\,*[0-9]*")) {
                                println("In proper format");
                                returnValue = 0;
                            }

                        }
                    }

                } catch (Exception e) {
                    println("Exception :" + e.toString());
                    browser.strErrorInfo = "Exception - " + e;
                    this.browser.close();
                    returnValue = 1;

                }
                return returnValue;
            } else if (parameter.equalsIgnoreCase("DEDUCTIBLE")) {
                int returnValue = 1;
                try {

                    List<WebElement> webElements = webDriver.findElements(By.xpath(xPath));
                    int listSize = webElements.size();

                    for (int i = 0; i < listSize; i++) {
                        String uiValue = webElements.get(i).getText();
                        if (uiValue.equalsIgnoreCase("NOT APPLICABLE")) {
                            returnValue = 0;
                        } else {
                            String[] Amount = uiValue.split("\\s+");
                            println("Amount 1 " + Amount[1]);
                            println("Amount 2 " + Amount[3]);

                            if ((Amount[1].matches("\\$?[0-9]+\\,*[0-9]*") && Amount[3].matches("\\$?[0-9]+\\,*[0-9]*"))
                                    || (Amount[1].contains("No") && Amount[4].contains("No"))) {
                                println("In proper format");
                                returnValue = 0;
                            }

                        }
                    }

                } catch (Exception e) {

                    println("Exception :" + e.toString());
                    browser.strErrorInfo = "Exception - " + e;
                    this.browser.close();
                    returnValue = 1;

                }
                return returnValue;
            } else if (parameter.equalsIgnoreCase("APTC")) {
                println("APTC Amount : " + text);

                NumberFormat format = NumberFormat.getCurrencyInstance();
                Number number = format.parse(text);
                String newaptc = number.toString();
                println("NEW APTC Amount : " + newaptc);
                conf.addRuntimeData(parameter, newaptc);
                return 0;
            } else if (parameter.equalsIgnoreCase("MOREAPTC")) {
                println("APTC Amount : " + text);

                NumberFormat format = NumberFormat.getCurrencyInstance();
                Number number = format.parse(text);
                number = number.intValue() + 5;
                String newaptc = number.toString();
                println("NEW APTC Amount : " + newaptc);
                conf.addRuntimeData(parameter, newaptc);
                return 0;
            } else if (parameter.equalsIgnoreCase("BILLPERIOD")) {
                println("BillPeriod in BillingHistory : " + text);

                String[] dteformat = text.split("/");
                String newformat = dteformat[2] + "-" + dteformat[0] + "-" + dteformat[1];
                conf.addRuntimeData(parameter, newformat);
                if (newformat.isEmpty()) {
                    return 1;
                } else {
                    return 0;
                }
            } else if (parameter.equalsIgnoreCase("CASENUM")) {
                String input = text;
                println("Before Trim:" + input);
                String val[] = input.split("\\s+");

                String CaseNum = conf.getParameterValue(val[3]);


                println("After trim:" + CaseNum);

                conf.addRuntimeData(parameter, CaseNum);

                println("Value " + CaseNum + " Assigned to: " + parameter);

                return 0;

            } else {
                println("Given input parameter is wrong! Please give DOB or SSN or ZIP to use this keayword");
                return 1;
            }

        } catch (Exception e) {
            println("Issue in asigning value.");
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            return 1;
        }

    }

    public int getETLog(String parameter) {
        // xPath="("+xPath+")[1]";
        int output = 0;
        try {
            webDriver.findElement(By.xpath("//a[@title='Case Info']")).click();
            String text = webDriver.findElement(By.xpath(xPath)).getText();
            println(" ETLog Text : " + text);
            String etlog = text.substring(7, 15);
            conf.addRuntimeData(parameter, etlog);
            println("Trimmed ETLog No -" + etlog);

        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            output = 1;
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
        }
        return output;
    }

    /*
     * @Description Handles window events like "Escape", "Close","Tab"
     *
     * @Specific Online Service Link
     *
     * @return Boolean
     *
     * @author Hari
     */

    public int isRadioBtnClicked() {
        String radioBtn = xPath;
        int output = 0;
        println("Inside isRadioBtnClicked()");
        try {
            if (webDriver.findElement(By.xpath(radioBtn)).isSelected())
                ;
            {
                println("Billing radio button is selected");
                output = 0;
            }
        } catch (Exception e) {
            output = 1;

        }
        return output;
    }

    /*
     * @Description This validates UI content with parameter using contains
     *
     * @return Boolean
     *
     * @author Dhivya
     *
     */
    public int validate(String parameter) {
        int returnValue = 0;
        try {
            String parameterValue = conf.getParameterValue(parameter);
            println("Parameter Value:" + parameterValue);
            String uivalue = webDriver.findElement(By.xpath(xPath)).getText();
            println("UI Value:" + uivalue);

            if (webDriver.findElement(By.xpath(xPath)).getText().contains(parameterValue)) {
                returnValue = 0;
            } else {
                browser.strErrorInfo = "xpath value is not same as parameter value";
                this.browser.close();
                returnValue = 1;
            }
        } catch (Exception e) {
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            returnValue = 1;
        }
        return returnValue;
    }

    public int windowHandle(String parameter) {
        int returnValue = 0;
        Robot r;
        if (parameter.equalsIgnoreCase("escape")) {
            try {
                r = new Robot();
                r.keyPress(KeyEvent.VK_ESCAPE);
                r.keyRelease(KeyEvent.VK_ESCAPE);
                returnValue = 0;
            } catch (AWTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (parameter.equalsIgnoreCase("close")) {
            try {
                System.out.println("Inside");
                r = new Robot();
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_W);
                r.keyRelease(KeyEvent.VK_W);
                r.keyRelease(KeyEvent.VK_CONTROL);
                System.out.println("Final");
                returnValue = 0;
            } catch (AWTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else if (parameter.equalsIgnoreCase("tab")) {
            try {
                System.out.println("Inside");
                r = new Robot();
                r.keyPress(KeyEvent.VK_CONTROL);
                r.keyPress(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_TAB);
                r.keyRelease(KeyEvent.VK_CONTROL);
                System.out.println("Final");
                returnValue = 0;

            } catch (AWTException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        } else {
            println("No input found");
            returnValue = 1;
        }

        return returnValue;
    }
    /*
     * @Description Scrolls the view to the given webelement
     *
     * @Specific Online Service Link
     *
     * @return Boolean
     *
     * @author Padma
     */

    public int printescape() {
        int output = 0;
        try {
            WebDriver driver = null;
            WebDriverWait wait = new WebDriverWait(driver, 10);
            wait.until(ExpectedConditions.elementToBeClickable(By.xpath("//*[@id='print-header']/div/button[2]")))
                    .click();

            // click print button
            WebElement cancelButton = driver.findElement(By.xpath("//*[@id='print-header']/div/button[2]"));
            cancelButton.click();
            output = 0;
        } catch (Exception e) {
            output = 1;
        }

        return output;
    }
    /*
     * @Description validate the ui value in input field with the parameter value
     *
     * @Specific Online Service Link
     *
     * @return Boolean
     *
     * @author Dhivya
     */

    public int Pagereload() {
        int output = 0;
        try {
            webDriver.navigate().refresh();
            output = 0;
        } catch (Exception e) {
            output = 1;
        }

        return output;
    }

    public int scrollView() {
        int output = 0;
        String webElement = xPath;
        println("Inside scrollView method");
        try {
            println("In try block");
            WebElement we = webDriver.findElement(By.xpath(webElement));
            JavascriptExecutor jse = (JavascriptExecutor) webDriver;
            jse.executeScript("arguments[0].scrollIntoView()", we);
            output = 0;
        } catch (Exception e) {
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            output = 1;
        }

        return output;
    }

    public int validateValue(String parameter) {
        int returnValue = 0;
        try {
            String parameterValue = conf.getParameterValue(parameter);
            println("Parameter Value:" + parameterValue);
            String uivalue = webDriver.findElement(By.xpath(xPath)).getAttribute("value");
            println("UI Value:" + uivalue);
            if (uivalue.contains("X")) {
                String value1 = uivalue.substring(5);
                println("Value " + value1);
                String value2 = parameterValue.substring(5);
                if (value1.equalsIgnoreCase(value2)) {
                    returnValue = 0;
                } else {
                    browser.strErrorInfo = "UI and paramete value mismatch";
                    this.browser.close();
                    returnValue = 1;

                }
            } else if (parameterValue.equalsIgnoreCase(uivalue)) {
                returnValue = 0;
            } else {
                browser.strErrorInfo = "UI and paramete value mismatch";
                this.browser.close();
                returnValue = 1;
            }
        } catch (Exception e) {
            returnValue = 1;
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
        }
        return returnValue;
    }

    /*
     * @Description This will select scenario reason from storm stool
     *
     * @Specific exchange Link
     *
     * @param Variable to provide scenario ID
     *
     * @author Ramesh
     */
    public int ScenarioReason(String parameter) {
        int returnValue = 1;
        try {
            String parameterValue = conf.getParameterValue(parameter);
            println("Parameter Value:" + parameterValue);
            String uivalue = xPath + "[@data-value='" + parameterValue + "']";
            println("Xpath : " + uivalue);
            WebElement we = webDriver.findElement(By.xpath(uivalue));
            we.click();
            returnValue = 0;

        } catch (Exception e) {
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            returnValue = 1;

        }
        return returnValue;
    }

    public int captureCoverageDetails(String parameter) {

        try {
            List<WebElement> elements = webDriver.findElements(By.xpath(xPath));
            if (elements.isEmpty()) {
                throw new RuntimeException("No text area found");
            }

            String value = elements.get(0).getAttribute("value");
            // System.out.println(value);

            // Read FirstName
            String[] Fname = value.split("<FirstName>");
            String[] FnameIlbList = Fname[1].split("</FirstName>");
            String FnameIlb = FnameIlbList[0];
            println("First name : " + FnameIlb);

            return 0;
        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }

    }

    /*
     * @Description This captures the resultset form DB with Fixed row
     *
     * @Specific Service Link
     *
     * @return Boolean
     *
     * @param Variable to store resultset
     *
     * @author Ramyashree
     */
    public int retriveValuesWithFixedRowFromDB(String parameter) {

        try {

            String DB_Url = conf.getParameterValue("DB_URL");
            String userName = conf.getParameterValue("USERNAME");
            String passWord = conf.getParameterValue("PASSWORD");
            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, passWord);

            println("Connection established");
            String data = conf.getParameterValue(parameter);
            println(data);
            String[] val = data.split("\\+");
            String query = val[0];
            println("Query is:" + val[0]);
            Statement stmt = con.createStatement();
            ResultSet res = stmt.executeQuery(query);
            int count = 1;

            while (res.next()) {

                println("List length is" + val.length);
                for (int i = count; i < val.length; i++) {
                    println(count + " Column name is" + val[count]);
                    println(count + " Column value is" + res.getString(val[count]));
                    conf.addRuntimeData(val[count], res.getString(val[count]));
                    println("The value" + res.getString(val[count]) + "assigned to" + val[count]);
                    count++;

                }

                // println("Query:" +query+" "+val[1]+"assigned to "+"CIM");

            }
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    /*
     * @Description This captures the resultset form DB with Fixed row
     *
     * @Specific Service Link
     *
     * @return Boolean
     *
     * @param Variable to store resultset
     *
     * @author Ramyashree
     */
    public int generateTenDigitRandomNumber(String parameter) {
        try {

            if (parameter.equalsIgnoreCase("AccountNumber")) {
                long tLen = (long) Math.pow(10, 10 - 1) * 9;
                long number = (long) (Math.random() * tLen) + (long) Math.pow(10, 10 - 1) * 1;
                String tVal = number + "";
                conf.addRuntimeData("AccountNumber", tVal);
            } else if (parameter.equalsIgnoreCase("PhoneNumber")) {
                long tLen = (long) Math.pow(10, 10 - 1) * 9;
                long number = (long) (Math.random() * tLen) + (long) Math.pow(10, 10 - 1) * 1;
                String tVal = number + "";
                conf.addRuntimeData("PhoneNumber", tVal);
            }

            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }
    }

    void println(String strLog) {
        log.deb(keywordName + ":" + strLog);
    }

    public int sampleFunction(String parameter) {
        int resultCode = 0;// success

        log.defaultExpected = "Click the given link.";
        try {
            if (webDriver == null) {
                println("Browser instance is null. Application not launched properly");
                return 1;
            }

            // Get the xpath of the object from the repository excel sheet
            // providing the screen name and object name
            String strElement = xPath;

            // To get the data from test data file. Provide the parameter name
            // to the getParameterValue function
            String strValue = conf.getParameterValue("UserName");

            browser.delay(3000);

            // Checks whether the xpath element is present in the application
            // and get the handle of the element
            WebElement element = browser.getWebDriverElement(strElement, "xpath");
            if (element != null) {
                // Can use any Selenium property on the element
                element.click();
                browser.delay(3000);

                // Report true/false based on the execution results
                println("Successfully clicked the object [" + strElement + "].");
                log.stepActual = "Successfully clicked the object [" + strElement + "].";

                // To add additional results to HTML report
                log.logExecutionResults("Step Name", "Description", "Expected", "Passed",

                        "Actual", 10, "", "SampleFunction");

                // To add runtime data
                conf.addRuntimeData("UserName", strValue);

                return resultCode;
            } else {
                browser.strErrorInfo = "Failed to find the element:" + strElement;
                return 1;
            }

        } catch (Exception e) {
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }
    }

    public ArrayList<String> getEnrollmentAPTCValueFromDB(String cimnumber, String cimname) {
        ArrayList<String> db_values = new ArrayList<>();

        System.out.println(generalutils.getMethodName());

        String employeeQuery = "Select  CN.CIM,CN.LAST_NAME,CN.FIRST_NAME,CN.MIDDLE_NAME,CN.PREFIX,CN.SUFFIX1,CN.SUFFIX2,CM.CONTACT_NAME,"
                + "CN.ADDRESS1,CN.ADDRESS2,CN.CITY,CN.STATE,CN.ZIP,CN.ZIP_PLUS4,CN.COUNTY_CODE,CN.AREA_CODE,CN.PHONE,CN.EMAIL,"
                + "CN.PHONE_EXTENSION,CN.SSN,CN.SEX,CN.BIRTH_DATE,CM.EXCHANGE_SUBSCRIBER_ID,CM.CASE_NUM from QUA.CASENAME AS CN ,QUA.CASE_MASTER AS CM "
                + "where CIM = '" + cimnumber + "' AND CM.CASENAME#CIM = '" + cimnumber + "' ";

        String coverageProductsQuery = "SELECT CH.CIM,CM.CASE_NUM, CH.COV_VALUE,CH.EFFECTIVE_DATE, CH.COV_ENTITY ,CM.RATE_INCREASE,"
                + " CM.INCEPTION_DATE,CM.ACTIVE_CODE,EM.SMOKER_QUESTION"
                + " FROM  QUA.COVERAGE_HISTORY AS CH,QUA.CASE_MASTER AS CM ,QUA.CASENAME AS CN,QUA.EMPLOYEE as EM "
                + " WHERE  CH.COV_QUALIFIER='PLAN'" + " AND CH.CIM='" + cimnumber
                + "' AND CH.EMP_NUM='1' AND CH.DEPENDENT_NUM='0' " + "AND CM.CASENAME#CIM='" + cimnumber
                + "' AND CN.CIM='" + cimnumber + "' AND EM.CASENAME#CIM = '" + cimnumber + "' AND CH.STATUS='C' ";
        try {

            String DB_Url = "jdbc:db2://PSI4MVS.HEALTHPLAN.COM:818/DB2J:retrieveMessagesFromServerOnGetMessages=true;emulateParameterMetaDataForZCalls=1;";
            String userName = "kmuruga";
            String password = "florida1";
            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, password);
            println("Connection establishment");

            Statement ilbstmt = con.createStatement();
            Statement dtlstmt = con.createStatement();
            ResultSet dtlres = dtlstmt.executeQuery(coverageProductsQuery);
            ResultSet ilbres = ilbstmt.executeQuery(employeeQuery);

            Thread.sleep(20 * 1000);
            println("QUERY EXECUTED-->" + coverageProductsQuery);
            println("QUERY EXECUTED-->" + employeeQuery);
            if (cimname == "DTL") {

                Thread.sleep(6000);

                int count = 1;
                if (dtlres.next()) {
                    db_values = new ArrayList<>();
                    db_values.add(generalutils.removeNull(dtlres.getString("CIM")).trim()); // 0
                    db_values.add(generalutils.removeNull(dtlres.getString("CASE_NUM")).trim()); // 1
                    db_values.add(generalutils.removeNull(dtlres.getString("COV_VALUE")).trim());// 2
                    db_values.add(generalutils.toSqlDate("yyyy-mm-dd",
                            generalutils.removeNull(dtlres.getString("EFFECTIVE_DATE")).trim(), "mm/dd/yyyy"));// 3
                    db_values.add(generalutils.removeNull(dtlres.getString("COV_ENTITY")).trim()); // 4
                    db_values.add(generalutils.toSqlDate("yyyy-mm-dd",
                            generalutils.removeNull(dtlres.getString("RATE_INCREASE")).trim(), "mm/dd/yyyy"));// 5
                    db_values.add(generalutils.toSqlDate("yyyy-mm-dd",
                            generalutils.removeNull(dtlres.getString("INCEPTION_DATE")).trim(), "mm/dd/yyyy"));// 6
                    db_values.add(generalutils.removeNull(dtlres.getString("ACTIVE_CODE"))); // 7
                    db_values.add(generalutils.removeNull(dtlres.getString("SMOKER_QUESTION"))); // 8
                } else {
                    println("FAIL");
                    println("No Records found with ilb cim : query-->" + coverageProductsQuery);
                    db_values.add("1");
                }

            } else {

                if (ilbres.next()) {
                    db_values = new ArrayList<>();

                    // concat name
                    String lastname = generalutils.removeNull(ilbres.getString("LAST_NAME")).trim();
                    String firstname = generalutils.removeNull(ilbres.getString("FIRST_NAME")).trim();
                    String middlename = generalutils.removeNull(ilbres.getString("MIDDLE_NAME")).trim();
                    String prefix = generalutils.removeNull(ilbres.getString("PREFIX")).trim();
                    String suffix1 = generalutils.removeNull(ilbres.getString("SUFFIX1")).trim();
                    String suffix2 = generalutils.removeNull(ilbres.getString("SUFFIX2")).trim();

                    String concatname = firstname + " " + lastname;

                    db_values.add(generalutils.removeNull(ilbres.getString("CIM")).trim()); // 0
                    db_values.add(concatname); // 1
                    db_values.add(generalutils.removeNull(ilbres.getString("ADDRESS1")).trim()); // 2
                    db_values.add(generalutils.removeNull(ilbres.getString("ADDRESS2")).trim());// 3
                    db_values.add(generalutils.removeNull(ilbres.getString("CITY")).trim());// 4
                    db_values.add(generalutils.removeNull(ilbres.getString("STATE")).trim());// 5
                    db_values.add(generalutils.removeNull(ilbres.getString("ZIP")).trim());// 6
                    db_values.add(generalutils.removeNull(ilbres.getString("ZIP_PLUS4")).trim());// 7
                    db_values.add(generalutils.toSqlDate("yyyy-mm-dd",
                            generalutils.removeNull(ilbres.getString("BIRTH_DATE")).trim(), "mm/dd/yyyy"));// 8
                    db_values.add(generalutils.removeNull(ilbres.getString("PHONE")).trim()); // 9
                    db_values.add(generalutils.removeNull(ilbres.getString("SSN")).trim()); // 10
                    db_values.add(generalutils.removeNull(ilbres.getString("EMAIL")).trim()); // 11
                    db_values.add(generalutils.removeNull(ilbres.getString("CONTACT_NAME")).trim()); // 12
                    db_values.add(generalutils.removeNull(ilbres.getString("EXCHANGE_SUBSCRIBER_ID")).trim()); // 13

                } else {
                    System.out.println(db_values);
                    println("No Records found with ilb cim : query-->" + employeeQuery);
                    System.out.println("No Records found with ilb cim : query-->" + employeeQuery);
                    db_values.add("1");
                }
            }
            System.out.println(db_values);

        } catch (Exception e) {
            System.out.println(e);
            println(e.toString());
            db_values.add("1");
        }
        return db_values;
    }

    public int validate_ilb_details(ArrayList<String> get_slp_ilbdetails, ArrayList<String> get_db_ilbdetails) {
        System.out.println(generalutils.getMethodName());
        int ilb = 0;
        // ILB VALIDATION
        // CIM check
        if ((get_db_ilbdetails.get(0)).equals(get_slp_ilbdetails.get(0))) {
            println("CIM MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(0));
            println("DB : " + get_db_ilbdetails.get(0));
            println("-------------------------------");
        } else {
            println("CIM MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(0));
            println("DB : " + get_db_ilbdetails.get(0));
            println("-------------------------------");
            ilb++;
        }

        // NAME check
        if ((get_db_ilbdetails.get(1)).equals(get_slp_ilbdetails.get(1))) {
            println("NAME MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(1));
            println("DB : " + get_db_ilbdetails.get(1));
            println("-------------------------------");
        } else {
            println("NAME MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(1));
            println("DB : " + get_db_ilbdetails.get(1));
            println("-------------------------------");
            ilb++;
        }

        // ADDRESS Check
        if ((get_db_ilbdetails.get(2).equals(get_slp_ilbdetails.get(2)))
                && (get_db_ilbdetails.get(3).equals(get_slp_ilbdetails.get(3)))
                && (get_db_ilbdetails.get(4).equals(get_slp_ilbdetails.get(4)))
                && (get_db_ilbdetails.get(5).equals(get_slp_ilbdetails.get(5)))
                && (get_db_ilbdetails.get(6).equals(get_slp_ilbdetails.get(6)))
                && (get_db_ilbdetails.get(7).equals(get_slp_ilbdetails.get(7)))) {
            println("SLP ADDRESS ===>" + get_slp_ilbdetails.get(2) + "," + get_slp_ilbdetails.get(3) + ","
                    + get_slp_ilbdetails.get(4) + "," + get_slp_ilbdetails.get(5) + "," + get_slp_ilbdetails.get(6)
                    + "," + get_slp_ilbdetails.get(7));
            println("DB ADDRESS ===>" + get_db_ilbdetails.get(2) + "," + get_db_ilbdetails.get(3) + ","
                    + get_db_ilbdetails.get(4) + "," + get_db_ilbdetails.get(5) + "," + get_db_ilbdetails.get(6) + ","
                    + get_db_ilbdetails.get(7));
            println("ADDRESS MATCHED");
        } else {
            println("SLP ADDRESS ===>" + get_slp_ilbdetails.get(2) + "," + get_slp_ilbdetails.get(3) + ","
                    + get_slp_ilbdetails.get(4) + "," + get_slp_ilbdetails.get(5) + "," + get_slp_ilbdetails.get(6)
                    + "," + get_slp_ilbdetails.get(7));
            println("DB ADDRESS ===>" + get_db_ilbdetails.get(2) + "," + get_db_ilbdetails.get(3) + ","
                    + get_db_ilbdetails.get(4) + "," + get_db_ilbdetails.get(5) + "," + get_db_ilbdetails.get(6) + ","
                    + get_db_ilbdetails.get(7));
            println("ADDRESS MISMATCHED");
            ilb++;
        }

        // DOB
        if ((get_db_ilbdetails.get(8)).equals(get_slp_ilbdetails.get(8))) {
            println("DOB MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(8));
            println("DB : " + get_db_ilbdetails.get(8));
            println("-------------------------------");
        } else {
            println("DOB MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(8));
            println("DB : " + get_db_ilbdetails.get(8));
            println("-------------------------------");
            ilb++;
        }

        String phonenumber = get_slp_ilbdetails.get(9).substring(6, 14).replaceAll("-", "");
        // PHONENUMBER Check
        if ((get_db_ilbdetails.get(9)).equals(phonenumber)) {
            println("PHONENUMBER MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(9));
            println("DB : " + get_db_ilbdetails.get(9));
            println("-------------------------------");
        } else {
            println("PHONENUMBER MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(9));
            println("DB : " + get_db_ilbdetails.get(9));
            println("-------------------------------");
            ilb++;
        }

        // SSN Check
        String ssn = get_slp_ilbdetails.get(10).replaceAll("-", "");
        if ((get_db_ilbdetails.get(10)).equals(ssn)) {
            println("SSN MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(10));
            println("DB : " + get_db_ilbdetails.get(10));
            println("-------------------------------");
        } else {
            println("SSN MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(10));
            println("DB : " + get_db_ilbdetails.get(10));
            println("-------------------------------");
            ilb++;
        }
        // EMAIL Check
        String email = "";
        if (get_slp_ilbdetails.get(11).equals("N/A")) {
            email = "";
        } else {
            email = get_slp_ilbdetails.get(11);
        }

        if ((get_db_ilbdetails.get(11)).equals(email)) {
            println("EMAIL MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(11));
            println("DB : " + get_db_ilbdetails.get(11));
            println("-------------------------------");
        } else {
            println("EMAIL MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(11));
            println("DB : " + get_db_ilbdetails.get(11));
            println("-------------------------------");
            ilb++;
        }

        // CONTACT_NAME Check
        if ((get_db_ilbdetails.get(12)).equals(get_slp_ilbdetails.get(13))) {
            println("CONTACT_NAME MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(13));
            println("DB : " + get_db_ilbdetails.get(12));
            println("-------------------------------");
        } else {
            println("CONTACT_NAME MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(13));
            println("DB : " + get_db_ilbdetails.get(12));
            println("-------------------------------");
            ilb++;
        }

        // EXCHANGE_SUBSCRIBER_ID Check
        if ((get_db_ilbdetails.get(13)).equals(get_slp_ilbdetails.get(15))) {
            println("EXCHANGE_SUBSCRIBER_ID MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(15));
            println("DB : " + get_db_ilbdetails.get(13));
            println("-------------------------------");
        } else {
            println("EXCHANGE_SUBSCRIBER_ID MISMATCHED");
            ilb++;
            println("-------------------------------");
            println("SLP: " + get_slp_ilbdetails.get(15));
            println("DB : " + get_db_ilbdetails.get(13));
            println("-------------------------------");
        }

        if (ilb == 0) {
            return 0;
        } else {
            return 1;
        }

    }

    public int validate_dtl_details(ArrayList<String> get_slp_dtldetails, ArrayList<String> get_db_dtldetails,
                                    String enrollmentstatus) {

        System.out.println(generalutils.getMethodName());
        int dtl = 0;

        // DETAILVALIDATION

        // CIM Check
        if ((get_db_dtldetails.get(0)).equals(get_slp_dtldetails.get(0))) {
            println("CIM MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(0));
            println("DB : " + get_db_dtldetails.get(0));
            println("-------------------------------");
        } else {
            println("CIM MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(0));
            println("DB : " + get_db_dtldetails.get(0));
            println("-------------------------------");
            dtl++;
        }

        // CASE_NUM Check
        if (enrollmentstatus.equals("F")) {
            if ((get_slp_dtldetails.get(1)).contains(get_db_dtldetails.get(1))) {
                println("CASE_NUM MATCHED");
                println("-------------------------------");
                println("SLP: " + get_slp_dtldetails.get(1));
                println("DB : " + get_db_dtldetails.get(1));
                println("-------------------------------");
            } else {
                println("CASE_NUM MISMATCHED");
                println("-------------------------------");
                println("SLP: " + get_slp_dtldetails.get(1));
                println("DB : " + get_db_dtldetails.get(1));
                println("-------------------------------");
                dtl++;
            }
        } else if (enrollmentstatus.equals("P")) {
            if ((get_db_dtldetails.get(1).contentEquals("000000")) && (get_slp_dtldetails.get(1).contains("000000"))) {
                println("CASE_NUM MATCHED");
                println("-------------------------------");
                println("SLP: " + get_slp_dtldetails.get(1).substring(0, 6));
                println("DB : " + get_db_dtldetails.get(1));
                println("-------------------------------");
            } else {
                println("CASE_NUM MISMATCHED");
                println("-------------------------------");
                println("SLP: " + get_slp_dtldetails.get(1));
                println("DB : " + get_db_dtldetails.get(1));
                println("-------------------------------");
                dtl++;
            }
        }
        // PLANID Check
        if ((get_db_dtldetails.get(2)).equals(get_slp_dtldetails.get(2))) {
            println("PLANID MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(2));
            println("DB : " + get_db_dtldetails.get(2));
            println("-------------------------------");
        } else {
            println("PLANID MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(2));
            println("DB : " + get_db_dtldetails.get(2));
            println("-------------------------------");
            dtl++;
        }

        // EFFECTIVE_DATE Check
        if ((get_db_dtldetails.get(3)).equals(get_slp_dtldetails.get(3))) {
            println("EFFECTIVE_DATE MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(3));
            println("DB : " + get_db_dtldetails.get(3));
            println("-------------------------------");
        } else {
            println("EFFECTIVE_DATE MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(3));
            println("DB : " + get_db_dtldetails.get(3));
            println("-------------------------------");
            dtl++;
        }

        // PRODUCT_TYPE Check
        if ((get_db_dtldetails.get(4)).equals(get_slp_dtldetails.get(4))) {
            println("PRODUCT_TYPE MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(4));
            println("DB : " + get_db_dtldetails.get(4));
            println("-------------------------------");
        } else {
            println("PRODUCT_TYPE MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(4));
            println("DB : " + get_db_dtldetails.get(4));
            println("-------------------------------");
            dtl++;
        }

        // RATE_INCREASE_DATE Check
        if ((get_db_dtldetails.get(5)).equals(get_slp_dtldetails.get(5))) {
            println("RATE_INCREASE_DATE MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(5));
            println("DB : " + get_db_dtldetails.get(5));
            println("-------------------------------");
        } else {
            println("RATE_INCREASE_DATE MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(5));
            println("DB : " + get_db_dtldetails.get(5));
            println("-------------------------------");
            dtl++;
        }

        // INCEPTION_DATE Check
        if ((get_db_dtldetails.get(6)).equals(get_slp_dtldetails.get(6))) {
            println("INCEPTION_DATE MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(6));
            println("DB : " + get_db_dtldetails.get(6));
            println("-------------------------------");
        } else {
            println("INCEPTION_DATE MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(6));
            println("DB : " + get_db_dtldetails.get(6));
            println("-------------------------------");
            dtl++;
        }

        // RECORD_STATUS Check

        if (enrollmentstatus.equals("F"))

        {
            if ((get_db_dtldetails.get(7).contentEquals("CM")) && (get_slp_dtldetails.get(7).contentEquals("ISSUED"))) {
                println("RECORD_STATUS MATCHED");
                println("-------------------------------");
                println("SLP: " + get_slp_dtldetails.get(7));
                println("DB : " + get_db_dtldetails.get(7));
                println("-------------------------------");
            } else {
                println("RECORD_STATUS MISMATCHED");
                println("-------------------------------");
                println("SLP: " + get_slp_dtldetails.get(7));
                println("DB : " + get_db_dtldetails.get(7));
                println("-------------------------------");
                dtl++;
            }
        } else if (enrollmentstatus.equals("P")) {

            if ((get_db_dtldetails.get(7).contentEquals("  ")) && (get_slp_dtldetails.get(7).contentEquals("ACTIVE"))) {
                println("RECORD_STATUS MATCHED");
                println("-------------------------------");
                println("SLP: " + get_slp_dtldetails.get(7));
                println("DB : " + get_db_dtldetails.get(7));
                println("-------------------------------");
            } else {
                println("RECORD_STATUS MISMATCHED");
                println("-------------------------------");
                println("SLP: " + get_slp_dtldetails.get(7));
                println("DB : " + get_db_dtldetails.get(7));
                println("-------------------------------");
                dtl++;
            }
        }

        // SMOKER_INDICATOR Check
        if ((get_db_dtldetails.get(8).contentEquals("Y")) && (get_slp_dtldetails.get(8).contentEquals("Yes"))) {
            println("SMOKER_INDICATOR MATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(8));
            println("DB : " + get_db_dtldetails.get(8));
            println("-------------------------------");
        } else {
            println("SMOKER_INDICATOR MISMATCHED");
            println("-------------------------------");
            println("SLP: " + get_slp_dtldetails.get(8));
            println("DB : " + get_db_dtldetails.get(8));
            println("-------------------------------");
            dtl++;
        }

        if (dtl == 0) {
            return 0;
        } else {
            return 1;
        }

    }

    public int validateAddress(String Parameter) {

        System.out.println(generalutils.getMethodName());

        String address1SLP = webDriver
                .findElement(By.xpath(conf.getObjectProperties("ServiceLink - Policy_Dashboard", "Address_input1")))
                .getAttribute("value");
        String address2SLP = webDriver
                .findElement(By.xpath(conf.getObjectProperties("ServiceLink - Policy_Dashboard", "Address_input2")))
                .getAttribute("value");
        String citySLP = webDriver
                .findElement(By.xpath(conf.getObjectProperties("ServiceLink - Policy_Dashboard", "City_input")))
                .getAttribute("value");
        String stateSLP = webDriver
                .findElement(By.xpath(conf.getObjectProperties("ServiceLink - Policy_Dashboard", "State_input")))
                .getAttribute("value");
        String zipSLP = webDriver
                .findElement(By.xpath(conf.getObjectProperties("ServiceLink - Policy_Dashboard", "Zip_input")))
                .getAttribute("value");
        String zipplusSLP = webDriver
                .findElement(By.xpath(conf.getObjectProperties("ServiceLink - Policy_Dashboard", "Plus_input")))
                .getAttribute("value");

        String cimnumber = conf.getParameterValue(parameter);
        println("CIM Number :" + cimnumber);

        String addressquery = "Select  CN.CIM,CN.ADDRESS1,CN.ADDRESS2,CN.CITY,CN.STATE,CN.ZIP,CN.ZIP_PLUS4 "
                + " from QUA.CASENAME AS CN " + " where CIM = '" + cimnumber + "'";

        String address_audittable = "select NEW_VALUE  " + " from QUA.AUDIT_TRAIL "
                + " where COLUMN_NAME IN ('ADDRESS1','ADDRESS2','CITY','STATE','ZIP') and TABLE_NAME = 'CASENAME' "
                + " and CIM = '" + cimnumber + "'";

        try {

            String DB_Url = "jdbc:db2://PSI4MVS.HEALTHPLAN.COM:818/DB2J:retrieveMessagesFromServerOnGetMessages=true;emulateParameterMetaDataForZCalls=1;";
            String userName = "kmuruga";
            String password = "florida1";
            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, password);
            println("Connection establishment");

            Statement ilbstmt = con.createStatement();
            Statement dtlstmt = con.createStatement();
            ResultSet casename_add = dtlstmt.executeQuery(addressquery);
            ResultSet audit_add = ilbstmt.executeQuery(address_audittable);

            Thread.sleep(20 * 1000);
            println("QUERY EXECUTED-->" + addressquery);
            println("QUERY EXECUTED-->" + address_audittable);

            String db_address1 = "", db_address2 = "", db_city = "", db_state = "", db_zip = "", db_zip_plus4 = "";

            if (casename_add.next()) {
                db_address1 = generalutils.removeNull(casename_add.getString("ADDRESS1")).trim();
                db_address2 = generalutils.removeNull(casename_add.getString("ADDRESS2")).trim();
                db_city = generalutils.removeNull(casename_add.getString("CITY")).trim().trim();
                db_state = generalutils.removeNull(casename_add.getString("STATE")).trim();
                db_zip = generalutils.removeNull(casename_add.getString("ZIP")).trim();
                db_zip_plus4 = generalutils.removeNull(casename_add.getString("ZIP_PLUS4")).trim();
            }

            int count = 0;
            if ((address1SLP.equals(db_address1)) && (address2SLP.equals(db_address2)) && (citySLP.equals(db_city))
                    && (stateSLP.equals(db_state)) && (zipSLP.equals(db_zip)) && (zipplusSLP.equals(db_zip_plus4))) {
                println("SLP ADDRESS ===>" + address1SLP + "," + address2SLP + "," + citySLP + "," + stateSLP + ","
                        + zipSLP + "," + zipplusSLP);
                println("DB ADDRESS ===>" + db_address1 + "," + db_address2 + "," + db_city + "," + db_state + ","
                        + db_zip + "," + db_zip_plus4);
                println("ADDRESS MATCHED");
            } else {
                println("SLP ADDRESS ===>" + address1SLP + "," + address2SLP + "," + citySLP + "," + stateSLP + ","
                        + zipSLP + "," + zipplusSLP);
                println("DB ADDRESS ===>" + db_address1 + "," + db_address2 + "," + db_city + "," + db_state + ","
                        + db_zip + "," + db_zip_plus4);
                println("ADDRESS MISMATCHED");
                count++;
            }

            ArrayList<String> audit_table_address = new ArrayList<>();
            while (audit_add.next()) {
                audit_table_address.add(audit_add.getString("NEW_VALUE"));
                System.out.println(audit_add.getString("NEW_VALUE"));
            }

            /*
             * int count_1 = 0; for(int i =0; i <audit_table_address.size();i++) {
             * if(slpAddress.contains(audit_table_address.get(i))) {
             * println(audit_table_address.get(i)); println("ADDRESS MATCHED"); } else {
             * println(audit_table_address.get(i)); println( "ADDRESS MISMATCHED");
             * count_1++; } }
             */
            if ((count == 0)) {
                return 0;
            } else {
                return 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int validateBroker(String Parameter) {

        System.out.println(generalutils.getMethodName());

        String delims = ",";
        String[] getValues = Parameter.split(delims);

        String cimnumber = conf.getParameterValue(getValues[0]);
        String slpAgentID = conf.getParameterValue(getValues[1]);

        println("SLP AgentID :" + slpAgentID);
        println("CIM Number :" + cimnumber);

        String agentIDquery = "select AGENT_ID " + " from QUA.CASE_BROKER " + " where CASENAME#CIM = '" + cimnumber
                + "'";

        try {

            String DB_Url = "jdbc:db2://PSI4MVS.HEALTHPLAN.COM:818/DB2J:retrieveMessagesFromServerOnGetMessages=true;emulateParameterMetaDataForZCalls=1;";
            String userName = "kmuruga";
            String password = "florida1";
            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, password);
            println("Connection establishment");

            Statement stmt = con.createStatement();
            ResultSet result = stmt.executeQuery(agentIDquery);

            Thread.sleep(20 * 1000);
            println("QUERY EXECUTED-->" + agentIDquery);

            String db_agentID = "";
            if (result.next()) {
                db_agentID = generalutils.removeNull(result.getString("AGENT_ID")).trim();
            }

            int count = 0;
            if (slpAgentID.equals(db_agentID)) {
                println("Agent_ID MATCHED");
                println("-------------------------------");
                println("SLP: " + slpAgentID);
                println("DB : " + db_agentID);
                println("-------------------------------");
            } else {
                println("Agent_ID MISMATCHED");
                println("-------------------------------");
                println("SLP: " + slpAgentID);
                println("DB : " + db_agentID);
                println("-------------------------------");
                count++;
            }

            if (count == 0) {
                return 0;
            } else {
                return 1;
            }

        } catch (Exception e) {
            e.printStackTrace();
            return 1;
        }
    }

    public int validateDependent(String Parameter) {
        println(generalutils.getMethodName());
        int returnValue = 0;
        ArrayList<String> db_value_array_obj = new ArrayList<>();
        TreeMap<String, ArrayList<String>> db_value_map_obj = new TreeMap<>();
        try {
            // String cimnumber = "49588496";
            // String cimnumber = "49588716";
            String cimnumber = conf.getParameterValue(Parameter);
            String deptableXpath = conf.getObjectProperties("ServiceLink - Coverage", "Dependents");
            println("Cimnumber-->" + cimnumber);
            println("deptableXpath-->" + deptableXpath);

            String dependentsCountQuery = "SELECT Count(*)  FROM QUA.DEPENDENT  WHERE CASENAME#CIM = '" + cimnumber
                    + "'";
            String dependentsDetailsQuery = "select CASENAME#CIM	,CASE_NUM,	EMP_NUM,	DEPENDENT_NUM,	LAST_NAME,	FIRST_NAME,	MIDDLE_INITIAL,	GENDER,	RELATIONSHIP,	DATE_OF_BIRTH,	"
                    + "EFFECTIVE_DATE,	TERM_DATE,	EXCHANGE_ENROLLEE_ID,SMOKER_IND ,SPOUSE_SSN	 FROM QUA.DEPENDENT WHERE CASENAME#CIM = '"
                    + cimnumber + "'";
            Connection con = OpenDBConnection();
            Statement stmt2 = con.createStatement();

            ResultSet rs2 = stmt2.executeQuery(dependentsDetailsQuery);
            println("dependentsDetailsQuery-->" + dependentsDetailsQuery);

            int dependenttablecount_db = getDbtableRowCount(dependentsCountQuery);
            int dependenttablecount_sip = getDynamicWebtableRowCount(deptableXpath) - 1;

            println("dependenttablecount_db ---->" + dependenttablecount_db);
            println("dependenttablecount_sip---->" + dependenttablecount_sip);

            // max = (a > b) ? a : b;

            if (dependenttablecount_db == dependenttablecount_sip) {

                if ((dependenttablecount_db == 0) & (dependenttablecount_sip == 0)) {
                    println("FAIL");
                    println("No Records found : query-->" + dependentsCountQuery);
                    println("No Records found : query-->" + dependentsDetailsQuery);
                    returnValue = 1;

                } else {
                    while (rs2.next()) {
                        db_value_array_obj = new ArrayList<>();
                        String fullname = "";
                        String dep_lastname = generalutils.removeNull(rs2.getString("LAST_NAME")).trim();
                        String dep_firstname = generalutils.removeNull(rs2.getString("FIRST_NAME")).trim();
                        String dep_middlename = generalutils.removeNull(rs2.getString("MIDDLE_INITIAL")).trim();

                        if (dep_middlename.equals("")) {
                            fullname = dep_firstname + " " + dep_lastname;

                        } else {
                            fullname = dep_firstname + " " + dep_middlename + " " + dep_lastname;

                        }
                        db_value_array_obj.add(fullname.toLowerCase());// 0
                        db_value_array_obj.add(generalutils.removeNull(rs2.getString("RELATIONSHIP")).trim()); // 1
                        db_value_array_obj.add(generalutils.removeNull(rs2.getString("GENDER")).trim()); // 2
                        db_value_array_obj.add(generalutils.toSqlDate("yyyy-mm-dd",
                                generalutils.removeNull(rs2.getString("DATE_OF_BIRTH")).trim(), "mm/dd/yyyy")); // 3
                        db_value_array_obj.add(generalutils.removeNull(rs2.getString("SPOUSE_SSN")).trim()); // 4
                        db_value_array_obj.add(generalutils.removeNull(rs2.getString("SMOKER_IND")).trim()); // 5
                        db_value_array_obj.add(generalutils.toSqlDate("yyyy-mm-dd",
                                generalutils.removeNull(rs2.getString("EFFECTIVE_DATE")).trim(), "mm/dd/yyyy"));// 6
                        db_value_array_obj.add(generalutils.removeNull(rs2.getString("EXCHANGE_ENROLLEE_ID")).trim()); // 7
                        db_value_map_obj.put(fullname.toLowerCase(), db_value_array_obj);

                    }

                    Map<String, ArrayList<String>> web_table_map_obj = getValuesFromDependentWebtable(1, 1, 1,
                            deptableXpath);
                    println("db_values ------>" + db_value_map_obj);
                    println("sip_values------>" + web_table_map_obj);

                    boolean dep_check = db_value_map_obj.equals(web_table_map_obj);

                    if (dep_check == true) {
                        returnValue = 0;
                    } else {
                        returnValue = 1;
                    }
                    println("boolean value--->" + dep_check);

                }
            } else {
                println("FAIL");
                println("Records not get matched : query-->" + dependentsCountQuery);
                println("Records not get matched : query-->" + dependentsDetailsQuery);
                returnValue = 1;

            }

        } catch (Exception e) {
            println("FAIL");
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int getDynamicWebtableRowCount(String xpath) throws Exception {

        // To locate table.
        // WebElement mytable =
        // webDriver.findElement(By.xpath(".//*[@id='post-body-8228718889842861683']/div[1]/table/tbody"));

        WebElement mytable = webDriver.findElement(By.xpath(xpath));
        // To locate rows of table.
        List<WebElement> rows_table = mytable.findElements(By.tagName("tr"));
        // To calculate no of rows In table.

        int rows_count = rows_table.size();
        return rows_count;

    }

    public int getDbtableRowCount(String query) throws SQLException {
        Connection con = OpenDBConnection();
        Statement stmt1 = con.createStatement();

        ResultSet rs1 = stmt1.executeQuery(query);
        println("dependentsCountQuery-->" + query);

        rs1.next();
        int dependenttablecount = rs1.getInt(1);
        return dependenttablecount;
    }

    public Map<String, ArrayList<String>> getValuesFromDependentWebtable(int startrow, int startcol, int keycolumn,
                                                                         String xpath) {
        ArrayList<String> sip_value_array_obj = new ArrayList<>();
        TreeMap<String, ArrayList<String>> sip_value_map_obj = new TreeMap<>();
        String keyvalue = "";

        println("--------------------------------------------------");
        WebElement mytable = webDriver.findElement(By.xpath(xpath));

        List<WebElement> rows_table = mytable.findElements(By.tagName("tr"));

        int rows_count = rows_table.size();

        for (int row = startrow; row < rows_count; row++) {

            List<WebElement> Columns_row = rows_table.get(row).findElements(By.tagName("td"));

            int columns_count = Columns_row.size();
            println("Number of cells In Row " + row + " are " + columns_count);

            for (int column = startcol; column < columns_count; column++) {

                String celtext = Columns_row.get(column).getText();
                if (column == 1) {
                    keyvalue = "";
                    keyvalue = celtext.toLowerCase();
                    celtext = celtext.toLowerCase();
                }
                if (column == 2) {
                    if (celtext.equals("Daughter")) {
                        celtext = "D";
                    } else if (celtext.equals("Wife")) {
                        celtext = "W";
                    } else if (celtext.equals("Son")) {
                        celtext = "S";
                    }

                }
                if (column == 3) {
                    if (celtext.equals("Female")) {
                        celtext = "F";
                    } else if (celtext.equals("Male")) {
                        celtext = "M";
                    }

                }
                if (column == 5) {
                    if (!celtext.equals("")) {
                        celtext = celtext.replace("-", "");
                    }
                }
                if (column == 6) {
                    if (celtext.equals("Yes")) {
                        celtext = "Y";
                    } else if (celtext.equals("No")) {
                        celtext = "N";
                    }
                }

                if ((column != 8) && (column != 9)) {
                    sip_value_array_obj.add(celtext);
                }

            }
            sip_value_map_obj.put(keyvalue, sip_value_array_obj);
            sip_value_array_obj = new ArrayList<>();
            println("--------------------------------------------------");
        }

        return sip_value_map_obj;
    }

    public Connection OpenDBConnection() {
        String DB_Url = "jdbc:db2://PSI4MVS.HEALTHPLAN.COM:818/DB2J:retrieveMessagesFromServerOnGetMessages=true;emulateParameterMetaDataForZCalls=1;";
        String userName = "kmuruga";
        String password = "florida1";
        String driverstring = "com.ibm.db2.jcc.DB2Driver";

        try {
            Class.forName(driverstring).newInstance();
            connection = DriverManager.getConnection(DB_Url, userName, password);
            println("Connection establishment");
        } catch (SQLException se) {
            println("Exception" + se);
            se.printStackTrace();
        } catch (Exception se) {
            println("Exception" + se);
            se.printStackTrace();
        }
        return connection;
    }

    public int validateTermDate(String Parameter) {
        println(generalutils.getMethodName());
        int returnValue = 0;
        String delims = "@";
        String Status = "I";
        try {
            String deptableXpath = conf.getObjectProperties("ServiceLink - Coverage History", "CoverageHistory_Table");

            String[] getValues = Parameter.split(delims);

            String detailCimvalue = conf.getParameterValue(getValues[0]);
            String status1 = conf.getParameterValue(getValues[1]);
            String status2 = conf.getParameterValue(getValues[2]);
            String termdate = conf.getParameterValue(getValues[3]);

            println("Status from Policy Dashboard-->" + status1);
            println("Status from CaseDetails-->" + status2);
            println("Termdate from CaseDetails-->" + termdate);

            ArrayList<String> get_coverage_history_slp_list = getValuesFromCoverageHistoryWebtable(0, 0, deptableXpath);
            ArrayList<String> get_coverage_history_db_list = new ArrayList<>();

            if (get_coverage_history_slp_list.size() == 0) {
                println("Execution Failed Since Not found any Term Applicant detail in Service link portail for the Cim -->"
                        + detailCimvalue);
                returnValue = 1;
            } else {
                String termdetailsQuery = "SELECT TERM_DATE,COV_ENTITY FROM QUA.COVERAGE_HISTORY WHERE CIM  = '"
                        + detailCimvalue + "' " + "AND TERM_DATE='" + get_coverage_history_slp_list.get(0)
                        + "' AND COV_ENTITY='" + get_coverage_history_slp_list.get(1) + "' AND STATUS = '" + Status
                        + "' ";

                String terminationCountQuery = "SELECT Count(*)  FROM QUA.COVERAGE_HISTORY  WHERE CIM  = '"
                        + detailCimvalue + "' " + "AND TERM_DATE='" + get_coverage_history_slp_list.get(0)
                        + "' AND COV_ENTITY='" + get_coverage_history_slp_list.get(1) + "' AND STATUS = '" + Status
                        + "' ";

                println("QUERY1--->" + terminationCountQuery);
                println("QUERY2--->" + termdetailsQuery);
                Connection con = OpenDBConnection();
                Statement stmt2 = con.createStatement();
                int termtablecount_db = getDbtableRowCount(terminationCountQuery);

                ResultSet rs2 = stmt2.executeQuery(termdetailsQuery);

                if (termtablecount_db == 1) {
                    while (rs2.next()) {
                        get_coverage_history_db_list.add(generalutils.removeNull(rs2.getString("TERM_DATE")).trim()); // 1
                        get_coverage_history_db_list.add(generalutils.removeNull(rs2.getString("COV_ENTITY")).trim()); // 1
                    }

                    if ((get_coverage_history_slp_list.get(0)).equals(get_coverage_history_db_list.get(0))) {

                        println("TERM DATE MATCHED");
                        println("-------------------------------");
                        println("SLP: " + get_coverage_history_slp_list.get(0));
                        println("DB : " + get_coverage_history_db_list.get(0));
                        println("-------------------------------");

                    } else {
                        println("TERM DATE NOT MATCHED");
                        println("-------------------------------");
                        println("SLP: " + get_coverage_history_slp_list.get(0));
                        println("DB : " + get_coverage_history_db_list.get(0));
                        println("-------------------------------");
                        returnValue = 1;
                    }

                    if ((get_coverage_history_slp_list.get(1)).equals(get_coverage_history_db_list.get(1))) {
                        println("ENTITY MATCHED");
                        println("-------------------------------");
                        println("SLP: " + get_coverage_history_slp_list.get(1));
                        println("DB : " + get_coverage_history_db_list.get(1));
                        println("-------------------------------");

                    } else {
                        println("ENTITY NOT MATCHED");
                        println("-------------------------------");
                        println("SLP: " + get_coverage_history_slp_list.get(1));
                        println("DB : " + get_coverage_history_db_list.get(1));
                        println("-------------------------------");
                        returnValue = 1;
                    }

                } else if (termtablecount_db == 0) {
                    println("Execution Failed Since  found no term Applicant details in the database for the query -->"
                            + termdetailsQuery);
                    returnValue = 1;
                } else if (termtablecount_db > 1) {
                    println("Execution Failed Since  found more than one term Applicant details in the database for the query -->"
                            + termdetailsQuery);
                    returnValue = 1;
                }
            }

        } catch (Exception e) {
            println("FAIL");
            println(e.toString());
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public ArrayList<String> getValuesFromCoverageHistoryWebtable(int startrow, int startcol, String xpath) {
        ArrayList<String> cov_history_list = new ArrayList<>();
        TreeMap<String, ArrayList<String>> sip_value_map_obj = new TreeMap<>();
        String keyvalue = "";

        println("--------------------------------------------------");
        WebElement mytable = webDriver.findElement(By.xpath(xpath));

        List<WebElement> rows_table = mytable.findElements(By.tagName("tr"));

        int rows_count = rows_table.size();

        for (int row = startrow; row < rows_count; row++) {

            List<WebElement> Columns_row = rows_table.get(row).findElements(By.tagName("td"));

            String termdate = Columns_row.get(10).getText();
            String Entity = Columns_row.get(11).getText();
            println("termdate-----------" + termdate);
            println("Entity-------------" + Entity);

            if ((!termdate.equals("")) && (Entity.equals("FUTURE TRM"))) {

                println("termdate-----------" + termdate);
                println("Entity-------------" + Entity);

                String str[] = termdate.split("/");
                String month = str[0];
                String day = str[1];
                String year = str[2];

                println("day-----------" + day);
                println("month-----------" + month);
                println("year-----------" + year);

                if (month.length() == 1) {
                    month = "0" + month;
                }

                if (day.length() == 1) {
                    day = "0" + day;
                }

                termdate = month + "/" + day + "/" + year;

                cov_history_list.add(generalutils.toSqlDate("mm/dd/yyyy", termdate, "yyyy-mm-dd"));// 3
                cov_history_list.add(Entity);
                break;
            }
        }

        println("cov_history_list---->" + cov_history_list);
        println("--------------------------------------------------");
        return cov_history_list;
    }

    public void closeConnection() throws SQLException {
        connection.close();
    }

    public int getDynamictableColumnCount(String xpath) throws Exception {

        // To locate table.
        // WebElement mytable =
        // webDriver.findElement(By.xpath(".//*[@id='post-body-8228718889842861683']/div[1]/table/tbody"));
        int columns_count = 0;
        WebElement mytable = webDriver.findElement(By.xpath(xpath));
        // To locate rows of table.
        List<WebElement> rows_table = mytable.findElements(By.tagName("tr"));
        // To calculate no of rows In table.

        int rows_count = rows_table.size();
        for (int row = 0; row < rows_count; row++) {

            List<WebElement> Columns_row = rows_table.get(row).findElements(By.tagName("td"));

            columns_count = Columns_row.size();

        }
        return columns_count;
    }

    public ArrayList<StringBuffer> getValuesFromWebtable(int startrow, int startcol, String xpath) {
        ArrayList<StringBuffer> array_obj = new ArrayList<>();

        String keyvalue = "";
        StringBuffer str_obj = new StringBuffer();

        println("--------------------------------------------------");
        WebElement mytable = webDriver.findElement(By.xpath(xpath));

        List<WebElement> rows_table = mytable.findElements(By.tagName("tr"));

        int rows_count = rows_table.size();

        for (int row = startrow; row < rows_count; row++) {

            List<WebElement> Columns_row = rows_table.get(row).findElements(By.tagName("td"));

            int columns_count = Columns_row.size();

            for (int column = startcol; column < columns_count; column++) {

                String celtext = Columns_row.get(column).getText();
                str_obj.append(celtext);
                str_obj.append(" ");
                array_obj.add(str_obj);
                str_obj = new StringBuffer();
            }
            println("" + array_obj);
            array_obj = new ArrayList<>();
        }

        return array_obj;
    }

    // <Ramesh> Desc - Used to print Table values from Billing details page
    public int getDynamicBillHistoryvalues(String X) throws Exception {

        try {

            String billsumXpath = conf.getObjectProperties("Billing Details", "Billing_deatils_BillSummary_table");
            String payrcvdXpath = conf.getObjectProperties("Billing Details", "Billing_deatils_PymtRecieved_table");
            String billadjustXpath = conf.getObjectProperties("Billing Details", "Billing_deatils_BillAdjust_table");
            String billfeeXpath = conf.getObjectProperties("Billing Details", "Billing_deatils_Billfee_table");
            String covsumXpath = conf.getObjectProperties("Billing Details", "Billing_deatils_CovSummary_table");

            // BillSummary Table

            int row_count1 = getDynamicWebtableRowCount(billsumXpath);
            int col_count1 = getDynamictableColumnCount(billsumXpath);
            if ((row_count1 == 1) && (col_count1 == 1)) {
                println("Currently No data found in Billing Summary Table");
            } else {
                println("Billing Summary Table Values : ");
                getValuesFromWebtable(0, 0, billsumXpath);
            }

            // Payment received Table
            int row_count2 = getDynamicWebtableRowCount(payrcvdXpath);
            int col_count2 = getDynamictableColumnCount(payrcvdXpath);
            if ((row_count2 == 1) && (col_count2 == 1)) {
                println("Currently No data found in Payment received Table");
            } else {
                println("Payment recived Table Values : ");
                getValuesFromWebtable(0, 0, payrcvdXpath);
            }

            // Bill Adjustment Table
            int row_count3 = getDynamicWebtableRowCount(billadjustXpath);
            int col_count3 = getDynamictableColumnCount(billadjustXpath);
            if ((row_count3 == 1) && (col_count3 == 1)) {
                println("Currently No data found in Bill Adjustment Table");
            } else {
                println("Bill Adjustment Table Values : ");
                getValuesFromWebtable(0, 0, billadjustXpath);
            }

            // Billing Fees Table
            int row_count4 = getDynamicWebtableRowCount(billfeeXpath);
            int col_count4 = getDynamictableColumnCount(billfeeXpath);
            if ((row_count4 == 1) && (col_count4 == 1)) {
                println("Currently No data found in Billing Fees Table");
            } else {
                println("Billing Fees Table Values : ");
                getValuesFromWebtable(0, 0, billfeeXpath);
            }

            // Coverage Summary Table
            int row_count5 = getDynamicWebtableRowCount(covsumXpath);
            int col_count5 = getDynamictableColumnCount(covsumXpath);
            if ((row_count5 == 1) && (col_count5 == 1)) {
                println("Currently No data found in Coverage Summary Table");
            } else {
                println("Coverage Summary Table Values : ");
                getValuesFromWebtable(0, 0, covsumXpath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            browser.strErrorInfo = "" + e;
            this.browser.close();

        }
        return 0;
    }

    // <Ramesh> Desc - Used to print Table values from Aetna Billing details
    // page
    public int getDynamicBillHistoryAetnavalues(String X) throws Exception {

        try {

            String billsumXpath = conf.getObjectProperties("ServiceLink - Billing Details",
                    "Billing_deatils_BillSummary_table");
            String payrcvdXpath = conf.getObjectProperties("ServiceLink - Billing Details",
                    "Billing_deatils_PymtRecieved_table");
            String billadjustXpath = conf.getObjectProperties("ServiceLink - Billing Details",
                    "Billing_deatils_BillAdjust_table");

            // BillSummary Table

            int row_count1 = getDynamicWebtableRowCount(billsumXpath);
            int col_count1 = getDynamictableColumnCount(billsumXpath);
            if ((row_count1 == 1) && (col_count1 == 1)) {
                println("Currently No data found in Billing Summary Table");
            } else {
                println("Billing Summary Table Values : ");
                getValuesFromWebtable(0, 0, billsumXpath);
            }

            // Payment received Table
            int row_count2 = getDynamicWebtableRowCount(payrcvdXpath);
            int col_count2 = getDynamictableColumnCount(payrcvdXpath);
            if ((row_count2 == 1) && (col_count2 == 1)) {
                println("Currently No data found in Payment received Table");
            } else {
                println("Payment recived Table Values : ");
                getValuesFromWebtable(0, 0, payrcvdXpath);
            }

            // Bill Adjustment Table
            int row_count3 = getDynamicWebtableRowCount(billadjustXpath);
            int col_count3 = getDynamictableColumnCount(billadjustXpath);
            if ((row_count3 == 1) && (col_count3 == 1)) {
                println("Currently No data found in Bill Adjustment Table");
            } else {
                println("Bill Adjustment Table Values : ");
                getValuesFromWebtable(0, 0, billadjustXpath);
            }

        } catch (Exception e) {
            e.printStackTrace();
            println(" " + e);

        }
        return 0;
    }

    /*
     * //---------------------------Ramyashree
     * S---------------------------------------------
     */

    public int KeysTab() {
        int returnValue = 0;
        String strElement = xPath;
        try {

            WebElement xyz = webDriver.findElement(By.id(strElement));
            xyz.sendKeys(Keys.TAB);
        } catch (Exception e) {
            // TODO Auto-generated catch block
            println("Element not present");
            returnValue = 1;
            e.printStackTrace();
        }
        return returnValue;

    }

    // <Ramesh> Desc - apply date logic (Two days from current day, Excluding
    // Weekend) for Temp Insurance

    public int KeyTab() {
        int returnValue = 0;
        String strElement = xPath;
        try {
            // webDriver.findElement(By.xpath(strElement)).sendKeys(parameter);
            webDriver.findElement(By.xpath(strElement)).sendKeys(Keys.TAB);

        } catch (Exception e) {
            println("Element not present");
            return 1;
        }
        return returnValue;
    }

    public int captureCimIlb(String parameter) {

        try {
            String textArea = webDriver.findElement(By.xpath(xPath)).getText();
            String[] testAreaList = textArea.split("cim");
            String[] cimIlbList = testAreaList[1].split(">");
            String cimIlb = cimIlbList[1];
            String cimIlbValue = cimIlb.substring(0, 8);
            println(cimIlbValue);
            conf.addRuntimeData("ILBCIMVALUE", cimIlbValue);
            parameter = cimIlbValue;
            println(parameter);

            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;

        }
    }

    // <Ramesh> Desc - apply date logic (Two days from current day, Excluding
    // Weekend) for Temp Insurance (Template B)

    public int tempInsuranceStartDate(String parameter) {

        int returnValue = 0;
        try {

            Calendar cal = new GregorianCalendar();
            // cal now contains current date
            // System.out.println(cal.getTime());

            // add the working days
            int workingDaysToAdd = 2;
            for (int i = 0; i < workingDaysToAdd; i++)
                do {
                    cal.add(Calendar.DAY_OF_MONTH, 1);
                } while (!isWorkingDay(cal));

            SimpleDateFormat sm = new SimpleDateFormat("dd");
            String strDate = sm.format(cal.getTime());
            conf.addRuntimeData("START_DATE", strDate);
            println("strdate:-------------------->" + strDate);
            println("START DATE" + conf.getParameterValue("START_DATE"));

        } catch (Exception e) {
            e.toString();
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int selectStartDateB(String parameter) {

        int returnValue = 1;
        try {

            String start_date = conf.getParameterValue(parameter);
            println("START_DATE: " + conf.getParameterValue(parameter));
            String today = null;
            // webDriver.findElement(By.xpath(conf.getObjectProperties(sName,
            // oName)))
            webDriver.findElement(By.xpath("//input[@name='tempInsuranceStartDate']")).click();
            WebElement dateWidgetFrom = webDriver.findElement(By.xpath("(//table[@class='pika-table']/tbody)[2]"));
            List<WebElement> rows = dateWidgetFrom.findElements(By.tagName("tr"));
            List<WebElement> columns = dateWidgetFrom.findElements(By.tagName("td"));

            for (WebElement cell : columns) {
                println("inside for");
                println(" Text : " + cell.getText());
                if (cell.getText().equals(start_date)) {

                    println("inside if");
                    cell.click();
                    println("date selected");
                    break;

                }
                returnValue = 0;
            }
        } catch (Exception e) {
            e.toString();
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    public int selectStartDateA(String parameter) {

        int returnValue = 1;
        try {

            String start_date = conf.getParameterValue(parameter);
            println("START_DATE: " + conf.getParameterValue(parameter));
            String today = null;
            // webDriver.findElement(By.xpath(conf.getObjectProperties(sName,
            // oName)))
            webDriver.findElement(By.xpath("//input[@name='tempInsuranceStartDate']")).click();
            WebElement dateWidgetFrom = webDriver.findElement(By.xpath("(//table[@class='pika-table']/tbody)"));
            List<WebElement> rows = dateWidgetFrom.findElements(By.tagName("tr"));
            List<WebElement> columns = dateWidgetFrom.findElements(By.tagName("td"));

            for (WebElement cell : columns) {
                println("inside for");
                println(" Text : " + cell.getText());
                if (cell.getText().equals(start_date)) {

                    println("inside if");
                    cell.click();
                    println("date selected");
                    break;

                }
                returnValue = 0;
            }
        } catch (Exception e) {
            e.toString();
            e.printStackTrace();
            returnValue = 1;
        }
        return returnValue;
    }

    /*
     * //---------------------------Ramyashree
     * S---------------------------------------------
     */
    public int captureCimDtl(String parameter) {

        try {
            String textArea = webDriver.findElement(By.xpath(xPath)).getText();
            String[] testAreaList = textArea.split("cim");
            String[] cimDtlList = testAreaList[3].split(">");
            String cimDtl = cimDtlList[1];
            String cimDtlValue = cimDtl.substring(0, 8);
            println(cimDtlValue);

            conf.addRuntimeData("DTLCIMVALUE", cimDtlValue);
            parameter = cimDtlValue;
            println(parameter);
            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 1;
    }

    /*
     * //---------------------------Dhivya
     * B---------------------------------------------
     */
    public int checkAccountCreatedornot(String parameter) {
        try {
            String strElement = xPath;
            int returnValue = 1;
            int listSize = 0;

            Select options = new Select(webDriver.findElement(By.xpath(strElement)));

            List<WebElement> allOptions = options.getOptions();

            listSize = allOptions.size();
            listSize = listSize - 1;

            String listLength = Integer.toString(listSize);

            if (listSize == 1) {
                conf.addRuntimeData(parameter, listLength);
                println("The dropdown size:" + listSize);
                returnValue = 0;
            } else {
                conf.addRuntimeData(parameter, listLength);
                println("The dropdown size:" + listSize);
                returnValue = 0;
            }

            return returnValue;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }
    }

    /*
     * //---------------------------Dhivya
     * B---------------------------------------------
     */
    public int trimDTLCaseNum(String parameter) {
        String text = webDriver.findElement(By.xpath(xPath)).getText();
        String casenum[] = text.split("-");
        text = casenum[0];
        try {
            if (parameter.equalsIgnoreCase("SLPDTLCASENUM")) {
                println("DTL Casenumber Before trim:" + text);
                String caseNum = text.trim();

                if (caseNum.equalsIgnoreCase("000000")) {
                    println("DTL Casenumber not created " + caseNum);

                    return 1;
                } else {
                    conf.addRuntimeData("SLPDTLCASENUM", caseNum);
                    println("DTL Casenumber created:" + caseNum);
                    return 0;
                }
            } else {
                println("Please give the input parameter as SLPDTLCASENUM");
            }
            return 1;
        } catch (Exception e) {
            println("Issue in asigning value.");
            return 1;
        }

    }

    /*
     * //---------------------------Dhivya
     * B---------------------------------------------
     */
    public int DateBetweenInTable(String parameter) {
        int returnValue = 1;
        String parameterValue = conf.getParameterValue(parameter);
        println("Parameter Value:" + parameterValue);
        String date[] = parameterValue.split("\\s+");
        println("From : " + date[1]);
        println("To : " + date[3]);

        SimpleDateFormat sdf = new SimpleDateFormat("MM/dd/yyyy");
        try {
            println("inside try");
            Date date3 = sdf.parse(date[1]);

            println("Date3 " + date3);

            Date date4 = sdf.parse(date[3]);

            println("Date4 " + date4);

            while (date4.after(date3) || (date4.equals(date3))) {
                date[1] = sdf.format(date4);
                println("Date after incrementing " + date[1]);
                String Str1 = xPath + "[contains(text(),'" + date[1] + "')]";

                if (webDriver.findElement(By.xpath(Str1)).isDisplayed()) {
                    println("Inside if");
                    returnValue = 0;
                    println("Value " + date[1] + " Present in this table.");

                } else {
                    returnValue = 1;
                    browser.strErrorInfo = "Value not present in the table";
                    this.browser.close();

                }
                Calendar calc = Calendar.getInstance();
                calc.setTime(date4);

                calc.add(Calendar.MONTH, -1);
                println("After Incrementing" + calc.getTime());
                date4 = calc.getTime();
            }
        } catch (Exception e) {
            println("Inside Exception");
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            returnValue = 1;
        }
        return returnValue;
    }

    /*
     * //---------------------------Dhivya
     * B---------------------------------------------
     */
    public int billRerun() {
        int returnValue = 0;
        // GIVE THE XPATH TILL TBODY OF THE ELEMENT
        String webElement = xPath;
        try {
            WebElement we = webDriver.findElement(By.xpath(webElement));
            List<WebElement> tableRows = we.findElements(By.tagName("tr"));
            int tblSize = tableRows.size();
            println("tblSize: " + tblSize);
            for (int i = 1; i <= tblSize; i++) {
                String rerunxpath = webElement + "/tr[" + i + "]/td[3]";
                println("rerun xpath :" + rerunxpath);
                String datexpath = webElement + "/tr[" + i + "]/td[2]";
                String value = webDriver.findElement(By.xpath(rerunxpath)).getText().toString();

                int rerunValue = Integer.parseInt(value);
                println("rerun Value :" + rerunValue);
                if (rerunValue > 1) {
                    returnValue = 1;
                    String dateValue = webDriver.findElement(By.xpath(datexpath)).getText();
                    println("Date :" + dateValue);
                    String toggle = webElement + "/tr[" + i + "]/td[1]/span";
                    WebElement we1 = webDriver.findElement(By.xpath(toggle));
                    JavascriptExecutor jse = (JavascriptExecutor) webDriver;
                    jse.executeScript("arguments[0].scrollIntoView()", we1);
                    webDriver.findElement(By.xpath(toggle)).click();
                    String rerunDate = webElement + "/tr[" + i + "]/following-sibling::tr/td[2]";
                    println("Date Xpath :" + rerunDate);
                    String dateValue2 = webDriver.findElement(By.xpath(rerunDate)).getText();
                    println("Date 2 " + dateValue2);
                    if (dateValue.equals(dateValue2)) {
                        returnValue = 0;

                    }
                    i = i + rerunValue - 1;
                }

            }

        } catch (Exception e) {
            println("Inside Exception");
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            returnValue = 1;
        }

        return returnValue;

    }

    public int randomSelectOptions() {
        try {
            String strElement = xPath;
            Select sel = new Select(webDriver.findElement(By.xpath(strElement)));
            List<WebElement> allOptions = sel.getOptions();
            int numberOfOptionsAvailable = allOptions.size();
            int randomNumber = randomNumberGenrator(numberOfOptionsAvailable);

            sel.selectByIndex(randomNumber);
            return 0;
        } catch (Exception e) {
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            return 1;
        }

    }

    /*
     * @Description Selecting the description option for Quick Notes Randomly
     *
     * @Specific Online Service Link
     *
     * @return Boolean
     *
     * @author Padma
     */
    public int randomNumberGenrator(int numberOfOptionsAvailable) {
        Random r = new Random();
        int randomNumber = r.nextInt((numberOfOptionsAvailable - 1)) + 1;
        return randomNumber;
    }

    /*
     * //---------------------------Dhivya
     * B---------------------------------------------
     */
    public int validateSelect(String Parameter) {
        int returnValue = 0;
        try {
            String parameterValue = conf.getParameterValue(parameter);
            println("Parameter Value:" + parameterValue);
            WebElement element = webDriver.findElement(By.xpath(xPath));
            Select select = new Select(element);
            List<WebElement> uivalue = select.getAllSelectedOptions();
            int length = uivalue.size();
            println("Length : " + length);
            String value = uivalue.get((length - 1)).getText();
            println("UI Value:" + value);
            if (parameterValue.equalsIgnoreCase(value)) {
                returnValue = 0;
            } else {
                browser.strErrorInfo = "UI and parameter value mismatch";
                this.browser.close();
                returnValue = 1;
            }
        } catch (Exception e) {
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            returnValue = 1;
        }
        return returnValue;
    }

    public int cellValueFromTbl(String parameter) {

        /* GIVE THE XPATH TILL TBODY OF THE ELEMENT */
        String webElement = xPath;
        int output = 0;
        String REGION;
        try {
            WebElement we = webDriver.findElement(By.xpath(webElement));

            /***** GET ALL THE TRs INSIDE THE WEB ELEMENT - TBODY *****/
            List<WebElement> tableRows = we.findElements(By.tagName("tr"));
            String dbQuery;
            int tblSize = tableRows.size();
            println("tblSize: " + tblSize);

            if (parameter.equalsIgnoreCase("PREMIUM")) {

                /*
                 * RECONSTRUCTING THE XPATH WITH THE REQUIRED ROW AND COLUMN VALUES
                 */
                String cellValueXpath1 = webElement + "/tr[" + tblSize + "]/td[@class='money']";
                println("cellValueXpath1: " + cellValueXpath1);

                /* GETTING THE VALUE FROM THE RECONTRUCTED XPATH */
                String value = webDriver.findElement(By.xpath(cellValueXpath1)).getText().toString();
                println("value: " + value);

                /* ASSIGNING THE OBTAINED VALUES TO PARAMETER */
                conf.addRuntimeData(parameter, value);
                println("The value" + value + "has been assigned to the parameter" + parameter);
                output = 0;
            } else if (parameter.equalsIgnoreCase("AGE")) {
                String cellValueXpath2 = webElement + "/tr[" + tblSize + "]/td[5]";
                println("cellValueXpath2: " + cellValueXpath2);

                String value = webDriver.findElement(By.xpath(cellValueXpath2)).getText().toString();
                println("value: " + value);

                String a[] = value.split("/");
                int month = Integer.parseInt(a[0]);
                int date = Integer.parseInt(a[1]);
                int year = Integer.parseInt(a[2]);

                /*
                 * String rateRefDateSLP_Xpath = conf.getObjectProperties(
                 * "ServiceLink - Dependent", "Rate_ReferenceDate"); String rateRefDateSLP =
                 * webDriver.findElement(By.xpath(rateRefDateSLP_Xpath)).getText ().toString();
                 * println("Rate Reference Date from SLP: " +rateRefDateSLP);
                 */

                // CHANGE STARTS - REFERENCE DATE FROM DB
                String caseNumSLP_Xpath = conf.getObjectProperties("CaseDetails_Dependent", "SLP_CaseNum");
                String[] CASENUM = webDriver.findElement(By.xpath(caseNumSLP_Xpath)).getText().toString().split("\\s+");
                String Casenum = CASENUM[3].trim().toUpperCase();
                println("Detail case number from SLP: " + Casenum);
                String rateRefDateDB = "";

                try {
                    File file = new File("C:\\Assure_NXT\\AutoExecution.properties");
                    FileInputStream fileInput = new FileInputStream(file);
                    Properties properties = new Properties();
                    properties.load(fileInput);
                    fileInput.close();
                    String DB_Url = properties.getProperty("db_url");
                    String userName = properties.getProperty("db_username");
                    String passWord = properties.getProperty("db_password");
                    REGION = properties.getProperty("REGION");

                    /*
                     * String DB_Url = conf.getParameterValue("DB_URL"); String userName =
                     * conf.getParameterValue("USERNAME"); String passWord =
                     * conf.getParameterValue("PASSWORD");
                     */

                    String dbClass = "com.ibm.db2.jcc.DB2Driver";
                    Class.forName(dbClass).newInstance();
                    Connection con = DriverManager.getConnection(DB_Url, userName, passWord);
                    println("Connection established");
                    Statement stmt = con.createStatement();

                    dbQuery = "SELECT RATE_REFERENCE_DTE FROM " + REGION + ".CASE_MASTER WHERE CASE_NUM='"
                            + Casenum.toUpperCase() + "';";
                    println("dbQuery: " + dbQuery);
                    ResultSet rs = stmt.executeQuery(dbQuery);

                    while (rs.next()) {
                        println("In rs while loop" + rs.getString(1));
                        rateRefDateDB = rs.getString(1);
                        println("Rate Reference Date from DB: " + rateRefDateDB);
                    }

                    // CHANGE ENDS - REFERENCE DATE FROM DB

                    String b[] = rateRefDateDB.split("-");
                    int year1 = Integer.parseInt(b[0]);
                    int month1 = Integer.parseInt(b[1]);
                    int date1 = Integer.parseInt(b[2]);
                    println("Year " + year1);
                    println("month1 " + month1);
                    println("date1 " + date1);

                    // LocalDate today = LocalDate.now();
                    // System.out.println("Today: "+today);
                    LocalDate DOB = LocalDate.of(year, month, date);
                    System.out.println("dob" + DOB);
                    LocalDate rateRefDate = LocalDate.of(year1, month1, date1);
                    System.out.println("rateRefDate" + rateRefDate);

                    Period p = Period.between(DOB, rateRefDate);
                    String age = String.valueOf(p.getYears());
                    System.out.println("AGE: " + age);
                    conf.addRuntimeData(parameter, age);
                    println("The value" + age + "has been assigned to the parameter" + parameter);
                    output = 0;
                } catch (Exception e) {
                    browser.strErrorInfo = "Exception - " + e;
                    output = 1;
                }
            } else if (parameter.equalsIgnoreCase("SMOKER")) {
                String cellValueXpath3 = webElement + "/tr[" + tblSize + "]/td[7]";
                println("cellValueXpath3: " + cellValueXpath3);

                String value = webDriver.findElement(By.xpath(cellValueXpath3)).getText().trim();
                println("value: " + value);
                conf.addRuntimeData(parameter, value);
                println("The value" + value + "has been assigned to the parameter" + parameter);
                output = 0;
            }

        } catch (Exception e) {
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            output = 1;
        }

        return output;
    }

    public int dependentFunc(String parameter) {
        int output = 0;
        String SLPValues[] = parameter.split(",");

        String CASENUM = conf.getParameterValue(SLPValues[0]);
        String PLAN_ID = conf.getParameterValue(SLPValues[1]);

        String PLAN_ID1 = PLAN_ID.substring(PLAN_ID.length() - 2);
        println("Last 2 character of PLAN_ID1 is: " + PLAN_ID1);
        if (PLAN_ID1.equals("OE") || (PLAN_ID1.equals("01"))) {
            PLAN_ID1 = PLAN_ID;
            println("PLAN_ID1 is: " + PLAN_ID1);
        } else if (!(PLAN_ID1.equals("01"))) {
            PLAN_ID1 = PLAN_ID.substring(0, PLAN_ID.length() - 2) + "01";
            println("PLAN_ID1 is: " + PLAN_ID1);
        }

        String PREMIUM = conf.getParameterValue(SLPValues[2]);

        String AGE = conf.getParameterValue(SLPValues[3]);
        int age = Integer.parseInt(AGE);
        if (age <= 20) {
            AGE = "0";
            println("AGE is: " + age);
        } else if (age >= 65) {
            AGE = "65";
            println("AGE is: " + age);
        } else {
            // age = age-1;
            AGE = String.valueOf(age);
            println("AGE is: " + age);
        }

        String SMOKER = conf.getParameterValue(SLPValues[4]);
        char smoker = SMOKER.charAt(0);
        println("Char of smoker: " + smoker);

        String PROD_TYPE = conf.getParameterValue(SLPValues[5]);
        String EFF_DATE = conf.getParameterValue(SLPValues[6]);
        String OVERALL_PREMIUM = conf.getParameterValue(SLPValues[7]);
        String REGION;

        println("CASENUM: " + CASENUM);
        println("PLAN_ID: " + PLAN_ID);
        println("PREMIUM: " + PREMIUM);
        println("AGE: " + AGE);
        println("SMOKER: " + SMOKER);
        println("PROD_TYPE: " + PROD_TYPE);
        println("EFF_DATE: " + EFF_DATE);
        println("OVERALL_PREMIUM: " + OVERALL_PREMIUM);

        String dbQuery1 = "";
        String dbQuery2 = "";
        String dbQuery3 = "";
        String County = "";
        String ZIP = "";
        String RETURN_INFO = "";
        String premiumFromDB = "";
        String CIM = "";

        try {
            File file = new File("C:\\Assure_NXT\\AutoExecution.properties");
            FileInputStream fileInput = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(fileInput);
            fileInput.close();
            String DB_Url = properties.getProperty("db_url");
            String userName = properties.getProperty("db_username");
            String passWord = properties.getProperty("db_password");
            REGION = properties.getProperty("REGION");

            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, passWord);
            println("Connection established");
            Statement stmt = con.createStatement();
            String cimQuery = "select CASENAME#CIM from " + REGION + ".Case_Master where CASE_NUM='" + CASENUM + "';";
            println("cimQuery: " + cimQuery);
            ResultSet res = stmt.executeQuery(cimQuery);
            while (res.next()) {
                println("In res while loop" + res.getString(1));
                CIM = res.getString(1);
                println("CIM: " + CIM);
            }
            dbQuery1 = "SELECT COUNTY_CODE,ZIP FROM " + REGION + ".CASENAME WHERE CIM='" + CIM + "';";
            println("dbQuery1: " + dbQuery1);
            ResultSet res1 = stmt.executeQuery(dbQuery1);

            while (res1.next()) {
                println("In res while loop" + res1.getString(1) + " " + res1.getString(2));
                County = res1.getString(1).substring(2, 5).trim();
                println("County: " + County);
                ZIP = res1.getString(2);
                ZIP = ZIP.trim();
                println("zip: " + ZIP);
            }

            if (County.length() == 0) {
                println("County_length: " + County.length());
                browser.strErrorInfo = "County field is empty in DB for checking premium";
                this.browser.close();
                output = 1;
            } else if (ZIP.length() == 0) {
                println("Zip_length: " + ZIP.length());
                // println("Inside if");
                browser.strErrorInfo = "ZIP field is empty in DB for checking premium";
                this.browser.close();
                output = 1;
            } else {
                // dbQuery2 = "SELECT RETURN_INFO FROM UAT.ZIPCODE_RATING WHERE
                // COV_VALUE='" + PLAN_ID + "' AND COUNTY_CODE='"
                // + County + "' AND FROM_ZIPCODE='" + ZIP + "';";
                dbQuery2 = "SELECT RETURN_INFO FROM " + REGION + ".ZIPCODE_RATING WHERE COV_VALUE='" + PLAN_ID
                        + "' AND COUNTY_CODE='" + County + "'Fetch first 1 row only;";
                println("dbQuery2: " + dbQuery2);
                ResultSet res2 = stmt.executeQuery(dbQuery2);

                while (res2.next()) {
                    RETURN_INFO = res2.getString(1).trim();
                    println("RETURN_INFO: " + RETURN_INFO);
                }
                if (RETURN_INFO.length() == 0) {
                    browser.strErrorInfo = "RETURN_INFO value is null";
                    this.browser.close();
                    output = 1;
                } else {
                    dbQuery3 = "SELECT RATING_VALUE FROM " + REGION + ".COVERAGE_RATING WHERE AREA='" + RETURN_INFO
                            + "' AND EFFECTIVE_DATE='" + EFF_DATE + "' AND PROD_TYPE='" + PROD_TYPE
                            + "' AND COV_VALUE='" + PLAN_ID1 + "' AND START_AGE ='" + AGE + "' AND SMOKER_CODE='"
                            + smoker + "' AND TIER='S' AND COVERAGE='S' Fetch first 1 row only;";
                    println("dbQuery3: " + dbQuery3);
                    ResultSet res3 = stmt.executeQuery(dbQuery3);

                    while (res3.next()) {
                        premiumFromDB = res3.getString(1);
                        println("Premium value fetched from DB: " + premiumFromDB);
                    }

                    int decimalIndex = premiumFromDB.indexOf(".");
                    println("Decimal index in the Premium from DB: " + decimalIndex);
                    String s1 = "";
                    String s2 = "";

                    switch (decimalIndex) {

                        case 1:
                            s1 = PREMIUM.substring(1, 5);
                            s2 = premiumFromDB.substring(0, 4);
                            println("Premium from SLP: " + s1 + "Premium from DB: " + s2);
                            break;

                        case 2:
                            s1 = PREMIUM.substring(1, 6);
                            s2 = premiumFromDB.substring(0, 5);
                            println("Premium from SLP: " + s1 + "Premium from DB: " + s2);
                            break;

                        case 3:
                            s1 = PREMIUM.substring(1, 7);
                            s2 = premiumFromDB.substring(0, 6);
                            println("Premium from SLP: " + s1 + "Premium from DB: " + s2);
                            break;

                        case 4:
                            s1 = PREMIUM.substring(1, 8);
                            s2 = premiumFromDB.substring(0, 7);
                            println("Premium from SLP: " + s1 + "Premium from DB: " + s2);
                            break;

                        case 5:
                            s1 = PREMIUM.substring(1, 9);
                            s2 = premiumFromDB.substring(0, 8);
                            println("Premium from SLP: " + s1 + "Premium from DB: " + s2);
                            break;
                        default:
                            browser.strErrorInfo = "Unable to fetch DB value";
                            this.browser.close();
                            output = 1;

                    }

                    if (s1.equals(s2)) {
                        output = 0;
                    } else {
                        browser.strErrorInfo = "Value mismatch";
                        this.browser.close();
                        output = 1;
                    }
                }
            }
        } catch (Exception e) {
            // File file = new File("C:/Exception.txt");
            // e.printStackTrace(new PrintStream(file));
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            output = 1;
        }

        return output;
    }

    /*
     * //---------------------------Hari----------------------------------------
     * -----
     */
    public int returnRuntimeValue(String parameter) {
        String str = conf.getParameterValue(parameter);
        String[] a = str.split(":");
        println("Before Splitting the parameter : " + a[0] + "=" + a[1]);
        println("Assiging the Runtime variable value of - " + a[0] + " - " + a[1]);
        conf.addRuntimeData(a[0], a[1]);
        return 0;
    }

    /*
     * //---------------------------Hari----------------------------------------
     * -----
     */
    public int splitAndAssign(String parameter) {
        try {
            String str = conf.getParameterValue(parameter);
            String[] a = str.split(",");
            for (String string : a) {
                String[] strs = string.split(":");
                println("After Splitting the parameter : " + strs[0] + "=" + strs[1]);
                println("Assiging the Runtime variable value of - " + strs[0] + " - " + strs[1]);
                conf.addRuntimeData(strs[0], strs[1]);

            }
            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }
    }

    /*
     * //---------------------------Hari----------------------------------------
     * -----
     */
    public int verifyCoverageSpecificValues() {
        try {
            List<WebElement> listOfCoverage = webDriver
                    .findElements(By.xpath("//table[@class='coverage-products']//tr[starts-with(@id,'cim_num')]"));
            int coverageCount = listOfCoverage.size();

            println("Total Coverage : " + coverageCount);
            List<WebElement> listOfCases = webDriver.findElements(By.className("display"));
            int caseCount = listOfCases.size();
            println("Case Count : " + caseCount + "Case Name : " + listOfCases);
            for (int j = 0; j < caseCount; j++) {
                listOfCases.get(j).click();
                String caseName = listOfCases.get(j).getText();
                println("Case Name : " + caseName);
                List<WebElement> temp = webDriver.findElements(By
                        .xpath("//div[@case_num='" + caseName + "']/table[@class='coverage-details']/tbody/tr[1]/td"));
                int k = 1;
                for (WebElement ele : temp) {
                    String planNameXpath = conf.getObjectProperties("ServiceLink_CoverageDetails",
                            "CaseDetails_Plan_Content");
                    String planCoverageXpath = conf.getObjectProperties("ServiceLink_CoverageDetails",
                            "CaseDetails_Coverage_Content");
                    String planEffectiveDateXpath = conf.getObjectProperties("ServiceLink_CoverageDetails",
                            "CaseDetails_EffectiveDate_Content");
                    String planTermDateXpath = conf.getObjectProperties("ServiceLink_CoverageDetails",
                            "CaseDetails_TermDate_Content");
                    String planPremiumAmountXpath = conf.getObjectProperties("ServiceLink_CoverageDetails",
                            "CaseDetails_Premium_Content");
                    String planIDXpath = conf.getObjectProperties("ServiceLink_CoverageDetails",
                            "CaseDetails_Plan_ID_Content");
                    String planCustomerNameXpath = conf.getObjectProperties("ServiceLink_CoverageDetails",
                            "CaseDetails_CustomerName");

                    println(ele.getText().toString());
                    List<WebElement> planName = webDriver.findElements(By.xpath(planNameXpath));
                    List<WebElement> planCoverage = webDriver.findElements(By.xpath(planCoverageXpath));
                    List<WebElement> planEffectiveDate = webDriver.findElements(By.xpath(planEffectiveDateXpath));
                    List<WebElement> planTermDate = webDriver.findElements(By.xpath(planTermDateXpath));
                    List<WebElement> planPremiumAmount = webDriver.findElements(By.xpath(planPremiumAmountXpath));
                    List<WebElement> planID = webDriver.findElements(By.xpath(planIDXpath));
                    List<WebElement> planCustomerNameID = webDriver.findElements(By.xpath(planCustomerNameXpath));

                    verifyElement(ele, planName, planName.size());
                    verifyElement(ele, planCoverage, planCoverage.size());
                    verifyElement(ele, planEffectiveDate, planEffectiveDate.size());
                    verifyElement(ele, planTermDate, planTermDate.size());
                    verifyElement(ele, planPremiumAmount, planPremiumAmount.size());
                    verifyElement(ele, planID, planID.size());
                    verifyElement(ele, planCustomerNameID, planCustomerNameID.size());
                }
                return 0;
            }
        } catch (Exception e) {
            return 1;
        }
        return 0;
    }

    private void verifyElement(WebElement ele, List<WebElement> planType, int planSize) {

        for (int kk = 0; kk < planSize; kk++) {
            String outerCov = planType.get(kk).getText();
            if (ele.getText().toString().equalsIgnoreCase(outerCov)) {
                println("Print : " + kk + " ----->" + outerCov);
                break;
            }
        }

    }

    /*
     * //---------------------------Hari----------------------------------------
     * -----
     */
    private int VerifyTableHeader(String parameter) {
        try {
            String strElement = xPath;
            String parameters = conf.getParameterValue(parameter);
            String[] testAreaList = parameters.split(",");
            List<WebElement> tableHeader = webDriver.findElements(By.xpath(strElement));
            int tableHeaderSize = tableHeader.size();
            println("Size :" + tableHeaderSize);
            for (int i = 0; i < tableHeaderSize - 1; i++) {
                println(testAreaList[i]);
                for (WebElement el : tableHeader) {
                    if (el.getText().toString().equalsIgnoreCase(testAreaList[i])) {
                        println("");
                    }
                }
            }
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    /*
     * //---------------------------Hari----------------------------------------
     * -----
     */
    public int DBConnection() {
        try {

            String DB_Url = "jdbc:db2://PSI4MVS.HEALTHPLAN.COM:818/DB2J:retrieveMessagesFromServerOnGetMessages=true;emulateParameterMetaDataForZCalls=1;";
            String userName = "DBalakr";
            String passWord = "hello123";

            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            println(userName + "," + passWord + "," + DB_Url);
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, passWord);
            log.defaultExpected = "Connection Established";
            println("Connection is established");

            Statement stmt = con.createStatement(ResultSet.TYPE_SCROLL_SENSITIVE, ResultSet.CONCUR_UPDATABLE);

            String query = "select CASENAME#CIM from UAT.case_master where carrier_code ='X5' Fetch first 1 rows only;";

            ResultSet res = stmt.executeQuery(query);

            while (res.next()) {
                println("CaseNumber:" + res.getString(1));
            }

        } catch (Exception e) {
            if (!browser.isAllowedException(e))
                mgr.logException(e);

        }

        return 0;

    }

    /*
     * //---------------------------Hari----------------------------------------
     * -----
     */
    public int IsElementExist(String parameter) {
        String strElement = xPath;
        println("Keyword");
        try {
            if (webDriver.findElement(By.xpath(strElement)).isDisplayed()) {
                println("Element present");
                return 0;
            } else {
                browser.strErrorInfo = "Element not present";
                this.browser.close();
                return 1;
            }
        } catch (Exception e) {
            println("Element not present");
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            return 1;
        }
    }

    // ********************************Hari**********************************comparing
    // values with DB*********************
    public int slpCompareWithCasenameDB2(String parameter) {

        try {
            if (webDriver == null) {
                println("Browser instance is null. Application not launched properly");
                return 1;
            }

            String DB_Url = conf.getParameterValue("DB_URL");
            String userName = conf.getParameterValue("USERNAME");
            String passWord = conf.getParameterValue("PASSWORD");

            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, passWord);

            println("Connection established");
            println(parameter);

            String val[] = parameter.split(",");

            if (val.length != 4) {
                println("Wrong parameters");
                return 1;
            }

            String str1 = conf.getParameterValue(parameter);

            String cim = conf.getParameterValue(val[0]);
            String name = conf.getParameterValue(val[1]);
            String ssn = conf.getParameterValue(val[2]);
            String dob = conf.getParameterValue(val[3]);

            String nameArray[] = name.split("\\s+");
            String ssnArray[] = ssn.split("-");
            String ssn_ui = (ssnArray[0] + ssnArray[1] + ssnArray[2]).trim();

            /*
             * SimpleDateFormat sd = new SimpleDateFormat("yyyy-mm-dd"); String dob_ui =
             * sd.format(dob);
             */

            String firstName = nameArray[0].trim();
            String lastName = nameArray[1].trim();

            println("Str1:" + str1 + ",CIM:" + cim + ",FirstNAME:" + firstName + ",LastName:" + lastName + ", SSN:"
                    + ssn + ", DOB:" + dob);

            Statement stmt = con.createStatement();

            String FName = null;
            String LName = null;
            String SSNDB2 = null;
            String DOBDB2 = null;
            String FullName = null;

            println("Before Select:" + parameter);

            // String query="SELECT COV_VALUE FROM SIT.COVERAGE_HISTORY WHERE
            // cim IN ('"+cim+"') and status='C' and COV_ENTITY='HEALTH' and
            // COV_QUALIFIER IN ('PLAN');";

            String query = "SELECT FIRST_NAME,LAST_NAME,SSN,BIRTH_DATE FROM SIT.CASENAME WHERE cim IN ('" + cim
                    + "') ;";

            ResultSet res = stmt.executeQuery(query);

            if (res.next()) {
                FName = res.getString(1).trim();
                LName = res.getString(2).trim();
                SSNDB2 = res.getString(3).trim();
                DOBDB2 = res.getString(4).trim();
            }

            FullName = FName + LName;

            if ((firstName.equalsIgnoreCase(FName)) && (lastName.equalsIgnoreCase(LName))
                    && (ssn_ui.equalsIgnoreCase(SSNDB2))) {
                println("Name matched between SLP:" + name + " and DB2:" + FullName);
                println("SSN matched between SLP:" + ssn_ui + " and DB2:" + SSNDB2);
                return 0;

            } else {
                if ((firstName.equalsIgnoreCase(FName)) && (lastName.equalsIgnoreCase(LName)))
                    println("Name matched between SLP:" + name + " and DB2:" + FullName);
                else
                    println("Name not matched between SLP:" + name + " and DB2:" + FullName);

                if ((ssn_ui.equalsIgnoreCase(SSNDB2)))
                    println("SSN matched between SLP:" + ssn_ui + " and DB2:" + SSNDB2);
                else
                    println("SSN Not matched between SLP:" + ssn_ui + " and DB2:" + SSNDB2);

                return 1;

            }

            /*
             * if(ssn.equalsIgnoreCase(SSNDB2)) { println(
             * "SSN matched between SLP:"+ssn+" and DB2:"+SSNDB2); } else {
             * println("SSN not matched between SLP:"+ssn+" and DB2:"+SSNDB2); }
             */

        } catch (Exception e) {
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }
    }

    /*
     * //---------------------------Hari----------------------------------------
     * -----
     */
    public int compareListSize(String parameter) {
        try {
            String strElement = xPath;
            int returnValue = 1;
            int listSize = 0;

            Select options = new Select(webDriver.findElement(By.xpath(strElement)));

            List<WebElement> allOptions = options.getOptions();

            listSize = allOptions.size();
            listSize = listSize - 1;

            String val[] = parameter.split(",");

            String BeforeCount = conf.getParameterValue(val[0]);
            String ResultStore = conf.getParameterValue(val[1]);

            int beforeCount = Integer.parseInt(BeforeCount);

            String listLength = Integer.toString(listSize);

            if (listSize > beforeCount) {
                conf.addRuntimeData("ResultStore", "True");
                println("The dropdown size:" + listSize);
                returnValue = 0;
            } else {
                conf.addRuntimeData("ResultStore", "False");
                println("The dropdown size:" + listSize);
                returnValue = 1;
            }

            return returnValue;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }
    }

    /*
     * //---------------------------Hari----------------------------------------
     * -----
     */
    public int compareListValue(String parameter) {
        try {
            String strElement = xPath;
            int returnValue = 1;
            int j = 0;
            String para = conf.getParameterValue(parameter);

            Select dropdownOptions = new Select(webDriver.findElement(By.xpath(strElement)));
            String parameterValue[] = para.split(",");
            HashMap<Integer, String> ui_parameter_value = new HashMap<Integer, String>();

            for (int i = 0; i < parameterValue.length; i++) {
                ui_parameter_value.put(i, parameterValue[i].toUpperCase());
            }

            HashMap<Integer, String> tempHashMap = new HashMap<Integer, String>();

            List<WebElement> allOptions = dropdownOptions.getOptions();

            for (WebElement ele : allOptions) {
                tempHashMap.put(j, ele.getText().toUpperCase());
                j++;
            }

            if (ui_parameter_value.equals(tempHashMap)) {
                returnValue = 0;
                conf.addRuntimeData("RESULTSTORE", "TRUE");
            } else {
                conf.addRuntimeData("RESULTSTORE", "FAIL");
                returnValue = 1;
            }

            return returnValue;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int getcaseid(String parameter) {
        int resultCode = 0;
        String CIM = null;
        int caseID_column_index = 0, status_column_index = 1, status_last_row = 9;
        File file = null;
        FileInputStream FIS;
        FileOutputStream FOS;
        XSSFWorkbook wb;
        String sheetName = conf.getParameterValue(parameter);

        println("GETCASEID: Inalizing the variables");
        try {
            String iteration_name = conf.strIterationName;
            String file_ref = iteration_name.trim().toLowerCase();
            // String file_ref=sheetName.trim().toLowerCase();

            if ((file_ref.contains("int"))) {
                file = new File("../../../" + carrier_path + "/Scenario-9_1/testdata/TestData_CaseID_Internal.xlsx");
            } else if ((file_ref.contains("ext"))) {
                file = new File("../../../" + carrier_path + "/Scenario-9_1/testdata/TestData_CaseID_External.xlsx");
            }
            println("File path is:" + file.getPath().toString());

            if (file.exists()) {

                FIS = new FileInputStream(file);
                println("defining the workbook");
                wb = new XSSFWorkbook(FIS);
                println("Sheet Name :" + sheetName);
                XSSFSheet sh = wb.getSheet(sheetName);
                println("Defining the Sheet");

                int firstRowNum = sh.getFirstRowNum();
                int lastCellNum = sh.getRow(sh.getFirstRowNum()).getLastCellNum();

                int last_row_num = (int) sh.getRow(firstRowNum).getCell(lastCellNum - 1).getNumericCellValue();
                println("Getting the last row which doesn't have done status " + last_row_num);

                XSSFRow row = sh.getRow(last_row_num);

                if (row.getCell(caseID_column_index).getCellType() == Cell.CELL_TYPE_NUMERIC) {
                    // String status_row =
                    // row.getCell(status_column_index).getStringCellValue();
                    int caseID;
                    caseID = (int) row.getCell(caseID_column_index).getNumericCellValue();
                    println("Retrived Case ID:" + caseID);

                    CIM = Integer.toString(caseID);
                    println("Sending Case ID:" + CIM);
                    resultCode = 0;
                } else if (row.getCell(caseID_column_index).getCellType() == Cell.CELL_TYPE_STRING) {
                    String caseID;
                    caseID = row.getCell(caseID_column_index).getStringCellValue();
                    println("Retrived Case ID:" + caseID);

                    CIM = caseID;
                    println("Sending Case ID:" + CIM);
                    resultCode = 0;
                }
                // Writing to the file after fetch the CIM number
                try {

                    XSSFCell cell = row.createCell(status_column_index);
                    cell.setCellValue("Done");
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    println("Current date and time is writing to excel Cell: " + dtf.format(now));
                    XSSFCell cell1 = row.createCell(status_column_index + 1);
                    cell1.setCellValue(dtf.format(now));
                    sh.getRow(firstRowNum).getCell(status_last_row).setCellValue(last_row_num + 1);

                    FOS = new FileOutputStream(file);
                    wb.write(FOS);
                    println("Writing the Status of Case ID");

                    conf.addRuntimeData("CIM", CIM);

                    FOS.close();
                } catch (Exception e) {
                    println("Some exception occured in writing the file!");
                    println(e.toString());
                    resultCode = 1;
                    return 1;
                }

                FIS.close();

            } else {
                println(file.getPath() + " " + file.getName() + "File not found");
                resultCode = 1;
                return 1;
            }

        } catch (Exception e) {
            println("Some exception occured in accessing the file!");
            println(e.toString());
            resultCode = 1;
            println("**************************Exception**************************");
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }
        log.logExecutionResults("GETCASEID", "getcaseid", "Access the Case ID repo and return case CIM no.", "Passed",
                "Returned the CIM", 10, "", "getcaseid");

        return resultCode;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int getcasedetails(String parameter) {
        int resultCode = 0;
        String CIM = null;
        int caseID_column_index = 0, status_column_index = 4, time_stamp = 5, status_last_row = 9;
        File file = null;
        FileInputStream FIS;
        FileOutputStream FOS;
        XSSFWorkbook wb;
        String sheetName = conf.getParameterValue(parameter);

        println("GETCASEDETAILS: Inalizing the variables");
        try {
            String file_ref = (conf.strIterationName).trim().toLowerCase();
            if ((file_ref.contains("member"))) {
                file = new File("../../../" + carrier_path + "/Scenario-9_1/testdata/TestData_CaseDetails_Member.xlsx");
                // file=new
                // File("C:/HPS_Projects/testdata/TestData_CaseID.xlsx");
            } else if ((file_ref.contains("binder"))) {
                file = new File("../../../" + carrier_path + "/Scenario-9_1/testdata/TestData_Binder.xlsx");
            }
            System.out.println("File path is:" + file.getPath().toString());

            if (file.exists()) {

                FIS = new FileInputStream(file);
                System.out.println("defining the workbook");
                wb = new XSSFWorkbook(FIS);
                System.out.println("Sheet Name :" + sheetName);
                XSSFSheet sh = wb.getSheet(sheetName);
                System.out.println("Defining the Sheet");

                int firstRowNum = sh.getFirstRowNum();
                int lastCellNum = sh.getRow(sh.getFirstRowNum()).getLastCellNum();

                System.out.println("firstRowNum" + firstRowNum);
                System.out.println("lastCellNum" + lastCellNum);

                int last_row_num = (int) sh.getRow(firstRowNum).getCell(lastCellNum - 2).getNumericCellValue();
                int no_of_col = (int) sh.getRow(firstRowNum).getCell(lastCellNum - 1).getNumericCellValue();
                System.out.println("no_of_col" + no_of_col);
                System.out.println("Getting the last row which doesn't have done status " + last_row_num);

                XSSFRow first_row = sh.getRow(firstRowNum);
                XSSFRow data_row = sh.getRow(last_row_num);
                String temp_str = "", temp_val = "";
                for (int i = 0; i <= no_of_col; i++) {
                    int cell_type = data_row.getCell(i).getCellType();
                    if (cell_type == Cell.CELL_TYPE_STRING) {
                        temp_str = first_row.getCell(i).getStringCellValue();
                        temp_val = data_row.getCell(i).getStringCellValue();

                    } else if (cell_type == Cell.CELL_TYPE_NUMERIC) {
                        temp_str = first_row.getCell(i).getStringCellValue();
                        temp_val = Integer.toString((int) data_row.getCell(i).getNumericCellValue());

                    } else if (cell_type == Cell.CELL_TYPE_BLANK) {
                        temp_str = "BLANK";
                        temp_val = "BLANK";
                    } else if (cell_type == Cell.CELL_TYPE_ERROR) {
                        temp_str = "ERROR";
                        temp_val = "ERROR";
                    }

                    System.out.println("The parameter ---" + temp_str + "--- as value ---" + temp_val + "---");
                    conf.addRuntimeData(temp_str, temp_val);
                    resultCode = 0;
                    temp_str = "";
                    temp_val = "";
                    // Writing to the file after fetch the CIM number
                }
                System.out.println("Loop exit");
                try {

                    XSSFCell cell = data_row.createCell(no_of_col + 1);
                    cell.setCellValue("Done");
                    DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy/MM/dd HH:mm:ss");
                    LocalDateTime now = LocalDateTime.now();
                    System.out.println("Current date and time is writing to excel Cell: " + dtf.format(now));
                    XSSFCell cell1 = data_row.createCell(no_of_col + 2);
                    cell1.setCellValue(dtf.format(now));
                    sh.getRow(firstRowNum).getCell(lastCellNum - 2).setCellValue(last_row_num + 1);

                    FOS = new FileOutputStream(file);
                    wb.write(FOS);
                    System.out.println("Writing the values");
                    FOS.close();
                } catch (Exception e) {
                    System.out.println("Some exception occured in writing the file!");
                    System.out.println(e.toString());
                    resultCode = 1;
                    return 1;
                }
                FIS.close();

            } else {
                println(file.getPath() + " " + file.getName() + "File not found");
                resultCode = 1;
                return 1;
            }
        } catch (Exception e) {
            println("Some exception occured in accessing the file!");
            println(e.toString());
            resultCode = 1;
            println("**************************Exception**************************");
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;

        }
        log.logExecutionResults("GETCASEID", "getcaseid", "Access the Case ID repo and return case CIM no.", "Passed",
                "Returned the CIM", 10, "", "getcaseid");

        return resultCode;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int checkvalueintable(String parameter) {
        int returnCode = 1;
        String value = parameter.trim();
        String strValue = conf.getParameterValue("CONFNO");
        int loop_count = 5;

        if (webDriver == null) {
            println("Browser instance is null. Application not launched properly");
            return 1;
        }

        GOO:
        try {
            // xpath = "//table[@id="table-ajax-scheduled"]/tbody"
            // Checking the table is present, if not present the browser will be
            // delayed for 5s and refresh for 'loop_count' times
            if (webDriver.findElement(By.xpath(xPath)).isDisplayed()) {
                WebElement table = webDriver.findElement(By.xpath(xPath));
                List<WebElement> tabletr = table.findElements(By.xpath("./tr")); // By.xpath("./tr[starts-with(@id,"cim_num")]");
                println("Assiging the ./tr tags to webelements ");
                println("No of the rows: " + tabletr.size());

                for (WebElement row : tabletr) {
                    List<WebElement> td = row.findElements(By.xpath("./td/a"));
                    println("Assiging the ./td tags to webelements ");

                    for (WebElement columns : td) {
                        String recivedStr = columns.getText().trim();
                        println("if(" + recivedStr + "=" + strValue + ")");

                        if (recivedStr.equalsIgnoreCase(strValue)) {
                            returnCode = 0;
                            println(recivedStr + "=" + strValue + " is matched");
                            conf.addRuntimeData("CONFNOPRES", "TRUE");
                            loop_count = -1;
                            break;
                        }

                    }
                }

                log.logExecutionResults("CHECKVALUEINTABLE", "Check Value in table",
                        "Value should present in the table", "Passed", "Value is present in the table", 10, "",
                        "checkvalueintable");
            } else {
                browser.delay(5000);
                webDriver.navigate().refresh();
                loop_count--;
                Label GOO;
            }

        } catch (NoSuchElementException e) {
            conf.addRuntimeData("CONFNOPRES", "FALSE");
            browser.delay(5000);
            webDriver.navigate().refresh();
            loop_count--;
            Label GOO;
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        } catch (Exception e) {
            println(e.toString());
            e.printStackTrace();
            println("**************************Exception**************************");
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }

        if (returnCode == 1) {
            conf.addRuntimeData("CONFNOPRES", "FALSE");
        }
        return 0;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int getconfirmationno() {

        int resultCode = 0;// sucesss
        String strPath = xPath;

        try {
            // text =" Confirmation No. 7974178 "
            String text = webDriver.findElement(By.xpath(xPath)).getText().trim();
            println("Raw text:" + text);
            String edited_text = text.substring(text.indexOf(".") + 1, text.length()).trim();
            println("After edited :" + edited_text);

            // Assigning the edited value to run time variable
            conf.addRuntimeData("CONFNO", edited_text);

            resultCode = 0;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            println(e.toString());
            e.printStackTrace();
            println("**************************Exception**************************");
            resultCode = 1;
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }

        log.logExecutionResults("Step Name", "Description", "Expected", "Passed", "Actual", 10, "", "SampleFunction");

        return resultCode;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int trimInputcasenum(String parameter) {
        // text=($600.00)

        try {
            String text = webDriver.findElement(By.xpath(xPath)).getText();
            String editedTxt = "";
            println("Checking the value for : " + parameter);

            int resultCode = 0;
            String in = text;
            println("Before trim: " + in);
            try {
                // text=($600.00)

                // Pay Total Amount ($207.35) Pay Other Amount $
                /*
                 * Pay Total Amount (($1.00)) Pay Past Due ($0.00) Pay Other Amount $
                 */
                String edit1 = in.replace("\n", " ");
                if (parameter.equalsIgnoreCase("TOT")) {
                    editedTxt = edit1.trim().substring(edit1.indexOf("$") + 1, edit1.indexOf(")"));
                } else if (parameter.equalsIgnoreCase("DUE")) {
                    String edit2 = edit1.trim().substring(edit1.indexOf("Due") + 1, edit1.length() - 1);
                    editedTxt = edit2.trim().substring(edit2.indexOf("$") + 1, edit2.indexOf(")"));
                }
                println("After trimmed" + editedTxt);

                conf.addRuntimeData("CASENUM", editedTxt.trim());
                resultCode = 0;

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                browser.strErrorInfo = "Exception - " + e;
                this.browser.close();
                resultCode = 1;
            }

            return resultCode;

        } catch (Exception e) {
            println("Issue in asigning value.");
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            return 1;
        }

    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int selectTheDropDownMatch(String parameter) {

        int resultCode = 0;// sucesss
        String strPath = xPath;
        try {
            String strElement = xPath;
            int returnValue = 1;
            int listSize = 0;

            Select options = new Select(webDriver.findElement(By.xpath(strElement)));
            List<WebElement> allOptions = options.getOptions();
            listSize = allOptions.size();
            println("The dropdown size:" + listSize);

            for (WebElement op : allOptions) {
                String check = op.getText().toLowerCase();
                String check_class = op.getAttribute("class");
                println("Parameter value is: " + parameter);
                println("Option: " + check);
                if (check.contains(parameter)) {
                    options.selectByVisibleText(op.getText());
                    println("Option matched: " + check);
                } else {
                    println("Dropdown not contain the value parameterized");
                }
            }
            resultCode = 0;
        } catch (Exception e) {
            println("**************************Exception**************************");
            println(e.toString());
            e.printStackTrace();
            println("**************************Exception**************************");
            resultCode = 1;
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }

        log.logExecutionResults("Step Name", "Description", "Expected", "Passed", "Actual", 10, "", "SampleFunction");

        return resultCode;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int calculatepercentage() {
        int returnvalue = 0;
        String CALCULATED_VAL;
        Float temp, temp1;
        String parameter = conf.getParameterValue("PERC");
        conf.addRuntimeData("PERC", parameter);
        println("Before changing the percentage value: " + parameter);
        try {
            int perc = Integer.parseInt(parameter);
            println("After changing the percentage value: " + perc);
            String value_temp = conf.getParameterValue("CASENUM").replace(",", "");
            Float value = Float.parseFloat(value_temp);

            println("Parsed float value is: " + value);

            if (perc == 0) {

                println("Percentage value is 0 or NULL, so calculation is not possible");
                returnvalue = 0;
            } else if (perc > 0) {

                temp = (value / 100) * perc;
                temp1 = (float) temp.floatValue();
                CALCULATED_VAL = temp1.toString();
                conf.addRuntimeData("CALCULATED_VAL", CALCULATED_VAL);

                println("Percentage of the provided value(String) is: " + CALCULATED_VAL);
                returnvalue = 0;
            } else if (perc == 100) {
                conf.addRuntimeData("CALCULATED_VAL", value.toString());
                println("Percentage of the provided value(String) is: " + value.toString());
                returnvalue = 0;
            }

            returnvalue = 0;

        } catch (Exception e) {
            // TODO Auto-generated catch block
            println("**************************Exception Start**************************");
            println(e.toString());
            e.printStackTrace();
            println("**************************Exception End**************************");
            returnvalue = 1;
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            return 1;
        }

        log.logExecutionResults("Step Name", "Description", "Expected", "Passed", "Actual", 10, "", "SampleFunction");
        return returnvalue;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int selecEFTorCC(String parameter) {

        int resultCode = 0;// sucesss
        String strPath = xPath;
        String decision = parameter; // EFT or CC
        int index = 0;
        boolean selected = false;
        String result = null;
        try {
            String strElement = xPath;
            int returnIndex = -1;
            int listSize = 0;

            Select options = new Select(webDriver.findElement(By.xpath(strElement)));
            List<WebElement> allOptions = options.getOptions();

            listSize = allOptions.size() - 1;
            println("The dropdown size:" + listSize);
            println("Received Parameter is: " + decision);

            /*
             * 0=Select the value 1=Pal eft *class=EFT 2=Pal CC *class=CC
             */
            if (listSize > 0) {

                switch (decision) {

                    case "EFT":
                        for (WebElement op : allOptions) {
                            String check = op.getText().toLowerCase().trim();
                            println("Option: " + check);
                            if (!check.contains("select")) {
                                String check_class = op.getAttribute("class").trim(); // retrived
                                // the
                                // class
                                // attr
                                // is
                                // EFT
                                // or
                                // CC
                                if (check_class.equalsIgnoreCase("EFT")) // EFT==EFT
                                {
                                    options.selectByVisibleText(op.getText());
                                    println("Drop down Option is selected as : " + op.getText());
                                    selected = true;
                                    result = "TRUE";
                                    break;
                                }
                            }
                        }
                        break;
                    case "CC":
                        for (WebElement op : allOptions) {
                            String check = op.getText().toLowerCase().trim();
                            println("Option: " + check);
                            if (!check.contains("select")) {
                                String check_class = op.getAttribute("class").trim(); // retrived
                                // the
                                // class
                                // attr
                                // is
                                // EFT
                                // or
                                // CC
                                if (check_class.equalsIgnoreCase("CC")) // EFT==EFT
                                {
                                    options.selectByVisibleText(op.getText());
                                    println("Drop down Option is selected as : " + op.getText());
                                    selected = true;
                                    result = "TRUE";
                                    break;
                                }
                            }
                        }
                        break;
                }
            }

            if (selected == false) {
                result = "FALSE";
                println("Match not found: So assiging the run time data - DROPRESULT as : FALSE");
            }

            if (listSize == 0) {
                result = "FALSE";
                println("List not have any data, so assiging the run time data - DROPRESULT as : FALSE");
            }

            conf.addRuntimeData("DROPRESULT", result);
            println("DROPRESULT as : " + result);
            resultCode = 0;

        } catch (Exception e) {
            println("**************************Exception**************************");
            println(e.toString());
            e.printStackTrace();
            println("**************************Exception**************************");
            resultCode = 1;
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }

        log.logExecutionResults("Step Name", "Description", "Expected", "Passed", "Actual", 10, "", "SampleFunction");

        return resultCode;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int isElementPresence(String para) {
        String strElement = xPath;
        int returnValue = 0;
        try {
            if (webDriver.findElement(By.xpath(strElement)).isDisplayed()) {
                println("Element present");
                conf.addRuntimeData(para, "TRUE");
                return 0;

            } else {
                println("Element not present");
                conf.addRuntimeData(para, "FALSE");
                return 0;

            }
        } catch (Exception e) {
            conf.addRuntimeData(para, "FALSE");
            println("**************************Exception**************************");
            println(e.toString());
            e.printStackTrace();
            println("**************************Exception**************************");
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            browser.strErrorInfo = "Exception - " + e;
            // this.browser.close();
            return 0;
        }
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int isElementEnabled(String para) {
        String strElement = xPath;
        int returnValue = 0;
        try {
            if (webDriver.findElement(By.xpath(strElement)).isEnabled()) {
                println("Element present and enabled");
                conf.addRuntimeData(para, "TRUE");

            } else {
                println("Element not present, but not enabled");
                conf.addRuntimeData(para, "FALSE");

            }
        } catch (Exception e) {
            conf.addRuntimeData(para, "FALSE");
            println("**************************Exception**************************");
            println(e.toString());
            e.printStackTrace();
            println("**************************Exception**************************");
            if (!browser.isAllowedException(e)) {
                mgr.logException(e);
            }
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            return 0;
        }

        return returnValue;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int saveTheDetails(String parameter) {

        int resultCode = 0;
        int caseID;
        String CIM = conf.getParameterValue("CIM");
        String pay_thru_date = conf.getParameterValue("PAYTHRUDATE");
        String perc = conf.getParameterValue("PERC");
        String conf_no = conf.getParameterValue("CONFNO");
        int Iteration_column_index = 0, cim_column_index = 1, paythru_index = 2, conf_no_index = 3, perc_index = 4,
                status_last_row = 9;
        File file = null;
        FileInputStream FIS;
        FileOutputStream FOS;
        XSSFWorkbook wb;
        String sheetName = "details";

        println("SAVETHEDETAILS: Inalizing the variables");
        try {

            String iteration_name = conf.strIterationName;
            String file_ref = iteration_name.trim().toLowerCase();
            // String file_ref=sheetName.trim().toLowerCase();

            if ((file_ref.contains("int"))) {
                file = new File(
                        "../../../" + carrier_path + "/Scenario-9_1/testdata/TestData_Batch_Proc_Detail_Internal.xlsx");
            } else if ((file_ref.contains("ext"))) {
                file = new File(
                        "../../../" + carrier_path + "/Scenario-9_1/testdata/TestData_Batch_Proc_Detail_External.xlsx");
            }

            println("File path is:" + file.getPath().toString());

            // file=new
            // File("C:/HPS_Projects/testdata/TestData_Batch_Proc_Detail.xlsx");

            if (file.exists()) {

                FIS = new FileInputStream(file);
                println("defining the workbook");
                wb = new XSSFWorkbook(FIS);
                println("Sheet Name :" + sheetName);
                XSSFSheet sh = wb.getSheet(sheetName);
                println("Defining the Sheet");

                int firstRowNum = sh.getFirstRowNum();
                println("Firdt Row Number : " + firstRowNum);
                int lastRowNum = sh.getLastRowNum() + 1;
                println("Last Row Number : " + lastRowNum);
                int lastCellNum = sh.getRow(sh.getFirstRowNum()).getLastCellNum();

                // int last_row_num=(int)
                // sh.getRow(firstRowNum).getCell(lastCellNum-1).getNumericCellValue();
                println("Getting the last row which doesn't have any data " + lastRowNum);

                // Writing to the file after fetch the CIM number
                try {
                    // Creating new row
                    XSSFRow row = sh.createRow(lastRowNum);
                    // Creating new cell above row
                    XSSFCell cell = row.createCell(Iteration_column_index);
                    cell.setCellValue(iteration_name);
                    println("iternation_name:" + iteration_name);

                    XSSFCell cell1 = row.createCell(cim_column_index);
                    cell1.setCellValue(CIM);
                    println("CIM:" + CIM);

                    XSSFCell cell2 = row.createCell(paythru_index);
                    cell2.setCellValue(pay_thru_date);
                    println("Pay thru date:" + pay_thru_date);

                    XSSFCell cell3 = row.createCell(conf_no_index);
                    cell3.setCellValue(conf_no);
                    println("Confimation Number:" + conf_no);

                    XSSFCell cell4 = row.createCell(perc_index);
                    cell4.setCellValue(perc);
                    println("Percentage :" + perc);

                    FOS = new FileOutputStream(file);
                    wb.write(FOS);
                    println("Writing the Iteration name and Case ID to excel file");
                    FOS.close();
                } catch (Exception e) {
                    println("Some exception occured in writing the file!");
                    println(e.toString());
                    resultCode = 1;
                    if (!browser.isAllowedException(e))
                        mgr.logException(e);
                    return 1;
                }
                FIS.close();

            } else {
                println(file.getPath() + " " + file.getName() + "File not found");
                resultCode = 1;
            }
        } catch (Exception e) {
            println("Some exception occured in accessing the file!");
            println(e.toString());
            resultCode = 1;
            println("**************************Exception**************************");
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }
        log.logExecutionResults("SAVETHEDETAILS", "SAVETHEDETAILS", "Iteration and CIM number is saved successfully",
                "Passed", "Saved Successfully", 10, "", "SAVETHEDETAILS");

        return resultCode;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int saveTheDetails_member(String parameter) {

        int resultCode = 0;
        int caseID;
        String CIM = conf.getParameterValue("CIM");
        String pay_thru_date = conf.getParameterValue("PAYTHRUDATE");
        String perc = conf.getParameterValue("PERC");
        String conf_no = conf.getParameterValue("CONFNO");
        int Iteration_column_index = 4, cim_column_index = 0, paythru_index = 2, conf_no_index = 3, perc_index = 4,
                status_last_row = 9;
        File file = null;
        FileInputStream FIS;
        FileOutputStream FOS;
        XSSFWorkbook wb;
        String sheetName = conf.getParameterValue("sheetname");
        String carrier = conf.getParameterValue("CARRIER");

        println("SAVETHEDETAILS: Inalizing the variables");
        try {

            String iteration_name = conf.strIterationName;
            String file_ref = iteration_name.trim().toLowerCase();
            // String file_ref=sheetName.trim().toLowerCase();

            if ((file_ref.contains("member"))) {
                file = new File(
                        "../../../" + carrier_path + "/Scenario-9_1/testdata/TestData_Batch_Proc_Detail_Member.xlsx");
            }
            println("File path is:" + file.getPath().toString());

            // file=new
            // File("C:/HPS_Projects/testdata/TestData_Batch_Proc_Detail.xlsx");

            if (file.exists()) {

                FIS = new FileInputStream(file);
                println("defining the workbook");
                wb = new XSSFWorkbook(FIS);
                println("Sheet Name :" + sheetName);
                XSSFSheet sh = wb.getSheet(sheetName);
                println("Defining the Sheet");

                int firstRowNum = sh.getFirstRowNum();
                println("Firdt Row Number : " + firstRowNum);
                int lastRowNum = sh.getLastRowNum() + 1;
                println("Last Row Number : " + lastRowNum);
                int lastCellNum = sh.getRow(sh.getFirstRowNum()).getLastCellNum();

                // int last_row_num=(int)
                // sh.getRow(firstRowNum).getCell(lastCellNum-1).getNumericCellValue();
                println("Getting the last row which doesn't have any data " + lastRowNum);

                // Writing to the file after fetch the CIM number
                try {
                    if (carrier.equalsIgnoreCase("AETNA")) {
                        XSSFRow row = sh.createRow(lastRowNum);
                        XSSFCell cell1 = row.createCell(cim_column_index);
                        cell1.setCellValue(CIM);
                        println("CIM:" + CIM);
                        XSSFCell cell2 = row.createCell(cim_column_index + 1);
                        cell2.setCellValue(conf.getParameterValue("Aetna_WID_Value"));
                        println("Aetna_WID_Value :" + conf.getParameterValue("Aetna_WID_Value"));
                        XSSFCell cell3 = row.createCell(cim_column_index + 2);
                        cell3.setCellValue(conf.getParameterValue("Aetna_IID_Value"));
                        println("Aetna_IID_Value :" + conf.getParameterValue("Aetna_IID_Value"));
                        XSSFCell cell4 = row.createCell(cim_column_index + 3);
                        cell4.setCellValue(conf.getParameterValue("Aetna_DoB_Value"));
                        println("Aetna_DoB_Value :" + conf.getParameterValue("Aetna_DoB_Value"));
                        XSSFCell cell5 = row.createCell(cim_column_index + 4);
                        cell5.setCellValue(iteration_name);
                        println("iternation_name:" + iteration_name);
                        XSSFCell cell6 = row.createCell(cim_column_index + 5);
                        cell6.setCellValue(pay_thru_date);
                        println("Pay thru date:" + pay_thru_date);
                        XSSFCell cell7 = row.createCell(cim_column_index + 6);
                        cell7.setCellValue(conf_no);
                        println("Confimation Number:" + conf_no);
                        XSSFCell cell8 = row.createCell(cim_column_index + 7);
                        cell8.setCellValue(perc);
                        println("Percentage :" + perc);
                    } else if (carrier.equalsIgnoreCase("BSC")) {
                        XSSFRow row = sh.createRow(lastRowNum);
                        XSSFCell cell1 = row.createCell(cim_column_index);
                        cell1.setCellValue(CIM);
                        println("CIM:" + CIM);
                        XSSFCell cell2 = row.createCell(cim_column_index + 1);
                        cell2.setCellValue(conf.getParameterValue("BSC_MemberProfileID_Value"));
                        println("BSC_MemberProfileID_Value :" + conf.getParameterValue("BSC_MemberProfileID_Value"));
                        XSSFCell cell3 = row.createCell(cim_column_index + 2);
                        cell3.setCellValue(conf.getParameterValue("BSC_DoB_Value"));
                        println("BSC_DoB_Value :" + conf.getParameterValue("BSC_DoB_Value"));
                        XSSFCell cell4 = row.createCell(cim_column_index + 3);
                        cell4.setCellValue(conf.getParameterValue("BSC_LastName_Value"));
                        println("BSC_LastName_Value :" + conf.getParameterValue("BSC_LastName_Value"));
                        XSSFCell cell4a = row.createCell(cim_column_index + 3);
                        cell4a.setCellValue(conf.getParameterValue("BSC_Index"));
                        println("BSC_Index :" + conf.getParameterValue("BSC_Index"));
                        XSSFCell cell5 = row.createCell(cim_column_index + 4);
                        cell5.setCellValue(iteration_name);
                        println("iternation_name:" + iteration_name);
                        XSSFCell cell6 = row.createCell(cim_column_index + 5);
                        cell6.setCellValue(pay_thru_date);
                        println("Pay thru date:" + pay_thru_date);
                        XSSFCell cell7 = row.createCell(cim_column_index + 6);
                        cell7.setCellValue(conf_no);
                        println("Confimation Number:" + conf_no);
                        XSSFCell cell8 = row.createCell(cim_column_index + 7);
                        cell8.setCellValue(perc);
                        println("Percentage :" + perc);
                    } else if (carrier.equalsIgnoreCase("COVENTRY")) {
                        XSSFRow row = sh.createRow(lastRowNum);
                        XSSFCell cell1 = row.createCell(cim_column_index);
                        cell1.setCellValue(CIM);
                        println("CIM:" + CIM);
                        XSSFCell cell2 = row.createCell(cim_column_index + 1);
                        cell2.setCellValue(conf.getParameterValue("CV_MemberId_Value"));
                        println("CV_MemberId_Value :" + conf.getParameterValue("CV_MemberId_Value"));
                        XSSFCell cell3 = row.createCell(cim_column_index + 2);
                        cell3.setCellValue(conf.getParameterValue("CV_DTL_Case_Num_Value"));
                        println("CV_DTL_Case_Num_Value :" + conf.getParameterValue("CV_DTL_Case_Num_Value"));
                        XSSFCell cell4 = row.createCell(cim_column_index + 3);
                        cell4.setCellValue(conf.getParameterValue("CV_DoB_Value"));
                        println("CV_DoB_Value :" + conf.getParameterValue("CV_DoB_Value"));
                        XSSFCell cell5 = row.createCell(cim_column_index + 4);
                        cell5.setCellValue(iteration_name);
                        println("iternation_name:" + iteration_name);
                        XSSFCell cell6 = row.createCell(cim_column_index + 5);
                        cell6.setCellValue(pay_thru_date);
                        println("Pay thru date:" + pay_thru_date);
                        XSSFCell cell7 = row.createCell(cim_column_index + 6);
                        cell7.setCellValue(conf_no);
                        println("Confimation Number:" + conf_no);
                        XSSFCell cell8 = row.createCell(cim_column_index + 7);
                        cell8.setCellValue(perc);
                        println("Percentage :" + perc);
                    } else if (carrier.equalsIgnoreCase("HCSC")) {
                        XSSFRow row = sh.createRow(lastRowNum);
                        XSSFCell cell1 = row.createCell(cim_column_index);
                        cell1.setCellValue(CIM);
                        println("CIM:" + CIM);
                        XSSFCell cell2 = row.createCell(cim_column_index + 1);
                        cell2.setCellValue(conf.getParameterValue("HCSC_MemberProfile_Value"));
                        println("HCSC_MemberProfile_Value :" + conf.getParameterValue("HCSC_MemberProfile_Value"));
                        XSSFCell cell3 = row.createCell(cim_column_index + 2);
                        cell3.setCellValue(conf.getParameterValue("HCSC_DoB_Value"));
                        println("HCSC_DoB_Value :" + conf.getParameterValue("HCSC_DoB_Value"));
                        XSSFCell cell4 = row.createCell(cim_column_index + 3);
                        cell4.setCellValue(conf.getParameterValue("HCSC_Zip_Value"));
                        println("HCSC_Zip_Value :" + conf.getParameterValue("HCSC_Zip_Value"));
                        XSSFCell cell5 = row.createCell(cim_column_index + 4);
                        cell5.setCellValue(iteration_name);
                        println("iternation_name:" + iteration_name);
                        XSSFCell cell6 = row.createCell(cim_column_index + 5);
                        cell6.setCellValue(pay_thru_date);
                        println("Pay thru date:" + pay_thru_date);
                        XSSFCell cell7 = row.createCell(cim_column_index + 6);
                        cell7.setCellValue(conf_no);
                        println("Confimation Number:" + conf_no);
                        XSSFCell cell8 = row.createCell(cim_column_index + 7);
                        cell8.setCellValue(perc);
                        println("Percentage :" + perc);
                    } else if (carrier.equalsIgnoreCase("KAISER")) {
                        XSSFRow row = sh.createRow(lastRowNum);
                        XSSFCell cell1 = row.createCell(cim_column_index);
                        cell1.setCellValue(CIM);
                        println("CIM:" + CIM);
                        XSSFCell cell2 = row.createCell(cim_column_index + 1);
                        cell2.setCellValue(conf.getParameterValue("Kaiser_MRN_Number_Value"));
                        println("Kaiser_MRN_Number_Value :" + conf.getParameterValue("Kaiser_MRN_Number_Value"));
                        XSSFCell cell3 = row.createCell(cim_column_index + 2);
                        cell3.setCellValue(conf.getParameterValue("Kaiser_DoB_Value"));
                        println("Kaiser_DoB_Value :" + conf.getParameterValue("Kaiser_DoB_Value"));
                        XSSFCell cell4 = row.createCell(cim_column_index + 3);
                        cell4.setCellValue(conf.getParameterValue("Kaiser_Region_Value"));
                        println("Kaiser_Region_Value :" + conf.getParameterValue("Kaiser_Region_Value"));
                        XSSFCell cell5 = row.createCell(cim_column_index + 4);
                        cell5.setCellValue(iteration_name);
                        println("iternation_name:" + iteration_name);
                        XSSFCell cell6 = row.createCell(cim_column_index + 5);
                        cell6.setCellValue(pay_thru_date);
                        println("Pay thru date:" + pay_thru_date);
                        XSSFCell cell7 = row.createCell(cim_column_index + 6);
                        cell7.setCellValue(conf_no);
                        println("Confimation Number:" + conf_no);
                        XSSFCell cell8 = row.createCell(cim_column_index + 7);
                        cell8.setCellValue(perc);
                        println("Percentage :" + perc);
                    } else if (carrier.equalsIgnoreCase("WELLMARK")) {
                        XSSFRow row = sh.createRow(lastRowNum);
                        XSSFCell cell1 = row.createCell(cim_column_index);
                        cell1.setCellValue(CIM);
                        println("CIM:" + CIM);
                        XSSFCell cell2 = row.createCell(cim_column_index + 1);
                        cell2.setCellValue(conf.getParameterValue("WELLMARK_MemberID_Value"));
                        println("WELLMARK_MemberID_Value :" + conf.getParameterValue("WELLMARK_MemberID_Value"));
                        XSSFCell cell3 = row.createCell(cim_column_index + 2);
                        cell3.setCellValue(conf.getParameterValue("WELLMARK_DoB_Value"));
                        println("WELLMARK_DoB_Value :" + conf.getParameterValue("WELLMARK_DoB_Value"));
                        XSSFCell cell4 = row.createCell(cim_column_index + 3);
                        cell4.setCellValue(conf.getParameterValue("WELLMARK_Zip_Value"));
                        println("WELLMARK_Zip_Value :" + conf.getParameterValue("WELLMARK_Zip_Value"));
                        XSSFCell cell4a = row.createCell(cim_column_index + 3);
                        cell4a.setCellValue(conf.getParameterValue("WELLMARK_Index"));
                        println("WELLMARK_Index :" + conf.getParameterValue("WELLMARK_Index"));
                        XSSFCell cell5 = row.createCell(cim_column_index + 4);
                        cell5.setCellValue(iteration_name);
                        println("iternation_name:" + iteration_name);
                        XSSFCell cell6 = row.createCell(cim_column_index + 5);
                        cell6.setCellValue(pay_thru_date);
                        println("Pay thru date:" + pay_thru_date);
                        XSSFCell cell7 = row.createCell(cim_column_index + 6);
                        cell7.setCellValue(conf_no);
                        println("Confimation Number:" + conf_no);
                        XSSFCell cell8 = row.createCell(cim_column_index + 7);
                        cell8.setCellValue(perc);
                        println("Percentage :" + perc);
                    } else if (carrier.equalsIgnoreCase("CIGNA")) {
                        XSSFRow row = sh.createRow(lastRowNum);
                        XSSFCell cell1 = row.createCell(cim_column_index);
                        cell1.setCellValue(CIM);
                        println("CIM:" + CIM);
                        XSSFCell cell2 = row.createCell(cim_column_index + 1);
                        cell2.setCellValue(conf.getParameterValue("CIGNA_SSN_Value"));
                        println("CIGNA_SSN_Value :" + conf.getParameterValue("CIGNA_SSN_Value"));
                        XSSFCell cell3 = row.createCell(cim_column_index + 2);
                        cell3.setCellValue(conf.getParameterValue("CIGNA_DoB_Value"));
                        println("CIGNA_DoB_Value :" + conf.getParameterValue("CIGNA_DoB_Value"));
                        XSSFCell cell4 = row.createCell(cim_column_index + 3);
                        cell4.setCellValue(conf.getParameterValue("CIGNA_Index"));
                        println("CIGNA_Index :" + conf.getParameterValue("CIGNA_Index"));
                        XSSFCell cell5 = row.createCell(cim_column_index + 4);
                        cell5.setCellValue(iteration_name);
                        println("iternation_name:" + iteration_name);
                        XSSFCell cell6 = row.createCell(cim_column_index + 5);
                        cell6.setCellValue(pay_thru_date);
                        println("Pay thru date:" + pay_thru_date);
                        XSSFCell cell7 = row.createCell(cim_column_index + 6);
                        cell7.setCellValue(conf_no);
                        println("Confimation Number:" + conf_no);
                        XSSFCell cell8 = row.createCell(cim_column_index + 7);
                        cell8.setCellValue(perc);
                        println("Percentage :" + perc);
                    }

                    FOS = new FileOutputStream(file);
                    wb.write(FOS);
                    println("Writing the Iteration name and Case ID to excel file");
                    FOS.close();
                } catch (Exception e) {
                    println("Some exception occured in writing the file!");
                    println(e.toString());
                    resultCode = 1;
                    if (!browser.isAllowedException(e))
                        mgr.logException(e);
                    return 1;
                }
                FIS.close();

            } else {
                println(file.getPath() + " " + file.getName() + "File not found");
                resultCode = 1;
            }
        } catch (Exception e) {
            println("Some exception occured in accessing the file!");
            println(e.toString());
            resultCode = 1;
            println("**************************Exception**************************");
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }
        log.logExecutionResults("SAVETHEDETAILS", "SAVETHEDETAILS", "Iteration and CIM number is saved successfully",
                "Passed", "Saved Successfully", 10, "", "SAVETHEDETAILS");

        return resultCode;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int validateAndSaveToXL(String parameter) {

        int resultCode = 0;
        int caseID;
        String CIM = conf.getParameterValue("CIM"); // TRUE or FALSE
        String pay_thru_date = conf.getParameterValue("PAYTHRUDATE"); // TRUE or
        // FALSE
        // String Conf_presence=conf.getParameterValue("CONFNOPRES");
        String Conf_presence = conf.getParameterValue("CONFNOPRES"); // TRUE or
        // FALSE
        int Iteration_column_index = 0, cim_column_index = 1, paythru_index = 2, conf_no_index = 3, perc_col_index = 4,
                done_col_index = 8, status_last_row = 9;
        File file;
        FileInputStream FIS;
        FileOutputStream FOS;
        XSSFWorkbook wb;
        String sheetName = "details";

        println("SAVETHEDETAILS: Inalizing the variables");
        try {
            file = new File("../../../" + carrier_path + "/Scenario-9_1/testdata/TestData_Batch_Proc_Detail.xlsx");
            // file=new
            // File("C:/HPS_Projects/testdata/TestData_Batch_Proc_Detail.xlsx");
            println("File path is:" + file.getPath().toString());
            String iteration_name = conf.strIterationName;
            println("Iteration Name received as " + iteration_name);
            if (file.exists()) {

                FIS = new FileInputStream(file);
                println("defining the workbook");
                wb = new XSSFWorkbook(FIS);
                println("Sheet Name :" + sheetName);
                XSSFSheet sh = wb.getSheet(sheetName);
                println("Defining the Sheet");

                int firstRowNum = sh.getFirstRowNum();
                println("Firdt Row Number : " + firstRowNum);
                int lastRowNum = sh.getLastRowNum() + 1;
                println("Last Row Number : " + lastRowNum);
                int lastCellNum = sh.getRow(sh.getFirstRowNum()).getLastCellNum();

                int last_row_num = (int) sh.getRow(firstRowNum).getCell(lastCellNum - 1).getNumericCellValue() - 1;
                println("Getting the last row which doesn't have any data " + last_row_num);

                XSSFRow row = sh.getRow(last_row_num);

                // Writing to the file after fetch the CIM number
                try {
                    String paythrudate_xl = row.getCell(paythru_index).getStringCellValue();
                    int perc = Integer.parseInt(row.getCell(perc_col_index).getStringCellValue());
                    println("Percentage from Excel: " + perc);
                    println("Paythrudate from Excel: " + paythrudate_xl);
                    println("pay_thru_date from GUI: " + pay_thru_date);
                    if (perc >= 95) {
                        if (!paythrudate_xl.equalsIgnoreCase(pay_thru_date)) {
                            XSSFCell cell = row.createCell(perc_col_index + 1);
                            cell.setCellValue("TRUE");
                            println("pay_thru_date:" + "TRUE");
                            println("Conf_presence:" + Conf_presence);
                            if (Conf_presence.equalsIgnoreCase("TRUE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("TRUE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PASSED");
                                println("Overall Status: " + "PASSED");
                            } else if (Conf_presence.equalsIgnoreCase("FALSE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("FALSE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PARTIAL PASSED");
                                println("Overall Status: " + "PARTIAL PASSED");
                            }

                        } else {
                            XSSFCell cell = row.createCell(perc_col_index + 1);
                            cell.setCellValue("FALSE");
                            println("pay_thru_date:" + "FALSE");
                            println("Conf_presence:" + Conf_presence);
                            if (Conf_presence.equalsIgnoreCase("TRUE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("TRUE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PARTIAL PASSED");
                                println("Overall Status: " + "PARTIAL PASSED");
                            } else if (Conf_presence.equalsIgnoreCase("FALSE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("FALSE");
                                println("Conf number presence:" + "FALSE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("FAILED");
                                println("Overall Status: " + "FAILED");
                            }

                        }
                    } else if (perc <= 90) {
                        if (paythrudate_xl.equalsIgnoreCase(pay_thru_date)) {
                            XSSFCell cell = row.createCell(perc_col_index + 1);
                            cell.setCellValue("TRUE");
                            println("pay_thru_date:" + "TRUE");

                            if (Conf_presence.equalsIgnoreCase("TRUE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("TRUE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PASSED");
                                println("Overall Status: " + "PASSED");
                            } else if (Conf_presence.equalsIgnoreCase("FALSE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("FALSE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PARTIAL PASSED");
                                println("Overall Status: " + "PARTIAL PASSED");
                            }

                        } else {
                            XSSFCell cell = row.createCell(perc_col_index + 1);
                            cell.setCellValue("FALSE");
                            println("pay_thru_date:" + "FALSE");

                            if (Conf_presence.equalsIgnoreCase("TRUE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("TRUE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PARTIAL PASSED");
                                println("Overall Status: " + "PARTIAL PASSED");
                            } else if (Conf_presence.equalsIgnoreCase("FALSE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("FALSE");
                                println("Conf number presence:" + "FALSE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("FAILED");
                                println("Overall Status: " + "FAILED");
                            }
                        }
                    }

                    FOS = new FileOutputStream(file);
                    wb.write(FOS);
                    println("Writing the Iteration name and Case ID to excel file");
                    FOS.close();
                    resultCode = 0;
                } catch (Exception e) {
                    println("Some exception occured in writing the file!");
                    println(e.toString());
                    resultCode = 1;
                }
                FIS.close();

            } else {
                println(file.getPath() + " " + file.getName() + "File not found");
                resultCode = 1;
            }
        } catch (Exception e) {
            resultCode = 1;
            println("Some exception occured in accessing the file!");
            println(e.toString());
            println("**************************Exception**************************");
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }
        log.logExecutionResults("SAVETHEDETAILS", "SAVETHEDETAILS", "Iteration and CIM number is saved successfully",
                "Passed", "Saved Successfully", 10, "", "SAVETHEDETAILS");

        return resultCode;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int validateAndSaveToXL_member(String parameter) {

        int resultCode = 0;
        int caseID;
        String CIM = conf.getParameterValue("CIM"); // TRUE or FALSE
        String pay_thru_date = conf.getParameterValue("PAYTHRUDATE"); // TRUE or
        // FALSE
        // String Conf_presence=conf.getParameterValue("CONFNOPRES");
        String Conf_presence = conf.getParameterValue("CONFNOPRES");
        String carrier = conf.getParameterValue("CARRIER");// TRUE or FALSE
        int Iteration_column_index = 4, cim_column_index = 0, paythru_index = 5, conf_no_index = 6, perc_col_index = 7,
                done_col_index = 11, status_last_row = 10;
        if (carrier.equalsIgnoreCase("BSC") || carrier.equalsIgnoreCase("WELLMARK")) {
            Iteration_column_index = 5;
            cim_column_index = 0;
            paythru_index = 6;
            conf_no_index = 7;
            perc_col_index = 8;
            done_col_index = 12;
            status_last_row = 11;
        } else {
            Iteration_column_index = 4;
            cim_column_index = 0;
            paythru_index = 5;
            conf_no_index = 6;
            perc_col_index = 7;
            done_col_index = 11;
            status_last_row = 10;
        }

        File file;
        FileInputStream FIS;
        FileOutputStream FOS;
        XSSFWorkbook wb;
        String sheetName = conf.getParameterValue("sheetname");

        println("SAVETHEDETAILS: Inalizing the variables");
        try {
            file = new File(
                    "../../../" + carrier_path + "/Scenario-9_1/testdata/TestData_Batch_Proc_Detail_Member.xlsx");
            // file=new
            // File("C:/HPS_Projects/testdata/TestData_Batch_Proc_Detail.xlsx");
            println("File path is:" + file.getPath().toString());
            String iteration_name = conf.strIterationName;
            println("Iteration Name received as " + iteration_name);
            if (file.exists()) {

                FIS = new FileInputStream(file);
                println("defining the workbook");
                wb = new XSSFWorkbook(FIS);
                println("Sheet Name :" + sheetName);
                XSSFSheet sh = wb.getSheet(sheetName);
                println("Defining the Sheet");

                int firstRowNum = sh.getFirstRowNum();
                println("First Row Number : " + firstRowNum);
                int lastRowNum = sh.getLastRowNum() + 1;
                println("Last Row Number : " + lastRowNum);
                int lastCellNum = sh.getRow(sh.getFirstRowNum()).getLastCellNum();

                int last_row_num = (int) sh.getRow(firstRowNum).getCell(lastCellNum - 2).getNumericCellValue() - 1;
                println("Getting the last row which doesn't have any data " + last_row_num);

                XSSFRow row = sh.getRow(last_row_num);

                // Writing to the file after fetch the CIM number
                try {
                    String paythrudate_xl = row.getCell(paythru_index).getStringCellValue();
                    int perc = Integer.parseInt(row.getCell(perc_col_index).getStringCellValue());
                    println("Percentage from Excel: " + perc);
                    println("Paythrudate from Excel: " + paythrudate_xl);
                    println("pay_thru_date from GUI: " + pay_thru_date);
                    if (perc >= 95) {
                        if (!paythrudate_xl.equalsIgnoreCase(pay_thru_date)) {
                            XSSFCell cell = row.createCell(perc_col_index + 1);
                            cell.setCellValue("TRUE");
                            println("pay_thru_date:" + "TRUE");
                            println("Conf_presence:" + Conf_presence);
                            if (Conf_presence.equalsIgnoreCase("TRUE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("TRUE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PASSED");
                                println("Overall Status: " + "PASSED");
                            } else if (Conf_presence.equalsIgnoreCase("FALSE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("FALSE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PARTIAL PASSED");
                                println("Overall Status: " + "PARTIAL PASSED");
                            }

                        } else {
                            XSSFCell cell = row.createCell(perc_col_index + 1);
                            cell.setCellValue("FALSE");
                            println("pay_thru_date:" + "FALSE");
                            println("Conf_presence:" + Conf_presence);
                            if (Conf_presence.equalsIgnoreCase("TRUE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("TRUE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PARTIAL PASSED");
                                println("Overall Status: " + "PARTIAL PASSED");
                            } else if (Conf_presence.equalsIgnoreCase("FALSE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("FALSE");
                                println("Conf number presence:" + "FALSE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("FAILED");
                                println("Overall Status: " + "FAILED");
                            }

                        }
                    } else if (perc <= 90) {
                        if (paythrudate_xl.equalsIgnoreCase(pay_thru_date)) {
                            XSSFCell cell = row.createCell(perc_col_index + 1);
                            cell.setCellValue("TRUE");
                            println("pay_thru_date:" + "TRUE");

                            if (Conf_presence.equalsIgnoreCase("TRUE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("TRUE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PASSED");
                                println("Overall Status: " + "PASSED");
                            } else if (Conf_presence.equalsIgnoreCase("FALSE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("FALSE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PARTIAL PASSED");
                                println("Overall Status: " + "PARTIAL PASSED");
                            }

                        } else {
                            XSSFCell cell = row.createCell(perc_col_index + 1);
                            cell.setCellValue("FALSE");
                            println("pay_thru_date:" + "FALSE");

                            if (Conf_presence.equalsIgnoreCase("TRUE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("TRUE");
                                println("Conf number presence:" + "TRUE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("PARTIAL PASSED");
                                println("Overall Status: " + "PARTIAL PASSED");
                            } else if (Conf_presence.equalsIgnoreCase("FALSE")) {
                                XSSFCell cell1 = row.createCell(perc_col_index + 2);
                                cell1.setCellValue("FALSE");
                                println("Conf number presence:" + "FALSE");
                                XSSFCell cell2 = row.createCell(perc_col_index + 3);
                                cell2.setCellValue("FAILED");
                                println("Overall Status: " + "FAILED");
                            }
                        }
                    }

                    FOS = new FileOutputStream(file);
                    wb.write(FOS);
                    println("Writing the Iteration name and Case ID to excel file");
                    FOS.close();
                    resultCode = 0;
                } catch (Exception e) {
                    println("Some exception occured in writing the file!");
                    println(e.toString());
                    resultCode = 1;
                }
                FIS.close();

            } else {
                println(file.getPath() + " " + file.getName() + "File not found");
                resultCode = 1;
            }
        } catch (Exception e) {
            resultCode = 1;
            println("Some exception occured in accessing the file!");
            println(e.toString());
            println("**************************Exception**************************");
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }
        log.logExecutionResults("SAVETHEDETAILS", "SAVETHEDETAILS", "Iteration and CIM number is saved successfully",
                "Passed", "Saved Successfully", 10, "", "SAVETHEDETAILS");

        return resultCode;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int getdetailfromxl(String parameter) {
        int resultCode = 0;
        String caseID;
        String CIM = null, CONFNO;
        int caseID_column_index = 1, conf_no_index = 3, status_column_index = 8, status_last_row = 9;
        File file = null;
        FileInputStream FIS;
        FileOutputStream FOS;
        XSSFWorkbook wb;
        String sheetName = conf.getParameterValue(parameter);

        println("Getdetailfromxl: Inalizing the variables");
        try {
            String iteration_name = conf.strIterationName;
            String file_ref = iteration_name.trim().toLowerCase();

            if ((file_ref.contains("int"))) {
                file = new File(
                        "../../../" + carrier_path + "/Scenario-9_1/testdata/TestData_Batch_Proc_Detail_Internal.xlsx");
                println("File path is:" + file.getPath().toString());
            } else if ((file_ref.contains("ext"))) {
                file = new File(
                        "../../../" + carrier_path + "/Scenario-9_1/testdata/TestData_Batch_Proc_Detail_External.xlsx");
            }

            if (file.exists()) {

                FIS = new FileInputStream(file);
                println("defining the workbook");
                wb = new XSSFWorkbook(FIS);
                println("Sheet Name :" + sheetName);
                XSSFSheet sh = wb.getSheet(sheetName);
                println("Defining the Sheet");

                int firstRowNum = sh.getFirstRowNum();
                int lastCellNum = sh.getRow(sh.getFirstRowNum()).getLastCellNum();

                int last_row_num = (int) sh.getRow(firstRowNum).getCell(lastCellNum - 1).getNumericCellValue();
                println("Getting the last row which doesn't have done status " + last_row_num);

                XSSFRow row = sh.getRow(last_row_num);

                if (row.getCell(caseID_column_index).getStringCellValue() != null) {
                    // String status_row =
                    // row.getCell(status_column_index).getStringCellValue();
                    caseID = row.getCell(caseID_column_index).getStringCellValue();
                    // CIM = Integer.toString(caseID);
                    println("Sending Case ID:" + caseID);

                    CONFNO = row.getCell(conf_no_index).getStringCellValue();
                    println("Sending Case ID:" + CONFNO);

                    conf.addRuntimeData("CIM", caseID);
                    conf.addRuntimeData("CONFNO", CONFNO);
                    resultCode = 0;
                    // Writing to the file after fetch the CIM number
                    try {

                        XSSFCell cell = row.createCell(status_column_index);
                        cell.setCellValue("Done");
                        sh.getRow(firstRowNum).getCell(status_last_row).setCellValue(last_row_num + 1);

                        FOS = new FileOutputStream(file);
                        wb.write(FOS);
                        println("Writing the Status of Case ID and Confirmation no");

                        FOS.close();
                    } catch (Exception e) {
                        println("Some exception occured in writing the file!");
                        println(e.toString());
                        resultCode = 1;
                    }
                }
                FIS.close();
            } else {
                println(file.getPath() + " " + file.getName() + "File not found");
                resultCode = 1;
            }

        } catch (Exception e) {
            println("Getdetailfromxl:Some exception occured in accessing the file!");
            println(e.toString());
            resultCode = 1;
            println("**************************Exception**************************");
            if (!browser.isAllowedException(e))
                mgr.logException(e);
            return 1;
        }
        log.logExecutionResults("GETDETAILFROMXL", "Getdetailfromxl", "Access the Case ID repo and return case CIM no.",
                "Passed", "Returned the CIM", 10, "", "getcaseid");

        return resultCode;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int scroll(String parameter) {

        try {
            JavascriptExecutor jsx = (JavascriptExecutor) webDriver;

            if (parameter.equalsIgnoreCase("DOWN")) {
                jsx.executeScript("window.scrollBy(0,450)", "");
                println("Scroll down by 450 Pixel");
            } else if (parameter.equalsIgnoreCase("UP")) {
                jsx.executeScript("window.scrollBy(0,0)", "");
                println("Scroll up by 450 Pixel");
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            return 1;
        }

        return 0;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int mailToQuoteValidation(String parameter) {
        // webDriver=this.webDriver;
        Set<String> win_set = webDriver.getWindowHandles();
        String current_win = webDriver.getWindowHandle();
        for (String win : win_set) {

            if (!win.equalsIgnoreCase(current_win)) {
                webDriver.switchTo().window(win);
                println(webDriver.getCurrentUrl());
            }
            try {

                String Health = conf.getParameterValue("HEALTH");
                String Dental = conf.getParameterValue("DENTAL");
                String Vision = conf.getParameterValue("VISION");
                String Medigap = conf.getParameterValue("MEDIGAP");
                if (parameter.equalsIgnoreCase("A")) {
                    if (Health.equalsIgnoreCase("TRUE")) {
                        String Health_Header_Xpath = conf.getObjectProperties("MailCart_FromMail",
                                "MailCart_page_first_insurance_Header_Health");
                        String Health_Header = conf.getParameterValue("HEALTH_HEADER");
                        String Health_Header_Mail = webDriver.findElement(By.xpath(Health_Header_Xpath)).getText();
                        if (Health_Header.equalsIgnoreCase(Health_Header_Mail)) {
                            println("The Value of Health -" + Health_Header + "-is matched with-" + Health_Header_Mail);
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                    if (Dental.equalsIgnoreCase("TRUE")) {
                        String Dental_Header_Xpath = conf.getObjectProperties("MailCart_FromMail",
                                "MailCart_page_first_insurance_Header_Dental");
                        String Dental_Header = conf.getParameterValue("DENTAL_HEADER");
                        String Dental_Header_Mail = webDriver.findElement(By.xpath(Dental_Header_Xpath)).getText();
                        if (Dental_Header.equalsIgnoreCase(Dental_Header_Mail)) {
                            println("The Value of Dental -" + Dental_Header + "-is matched with-" + Dental_Header_Mail);
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                    if (Vision.equalsIgnoreCase("TRUE")) {
                        String Vision_Header_Xpath = conf.getObjectProperties("MailCart_FromMail",
                                "MailCart_page_first_insurance_Header_Vision");
                        String Vision_Header = conf.getParameterValue("VISION_HEADER");
                        String Vision_Header_Mail = webDriver.findElement(By.xpath(Vision_Header_Xpath)).getText();
                        if (Vision_Header.equalsIgnoreCase(Vision_Header_Mail)) {
                            println("The Value of Vision -" + Vision_Header + "-is matched with-" + Vision_Header_Mail);
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                    if (Medigap.equalsIgnoreCase("TRUE")) {
                        String Medigap_Header_Xpath = conf.getObjectProperties("MailCart_FromMail",
                                "MailCart_page_first_insurance_Header_Medigap");
                        String Medigap_Header = conf.getParameterValue("MEDIGAP_HEADER");
                        String Medigap_Header_Mail = webDriver.findElement(By.xpath(Medigap_Header_Xpath)).getText();
                        if (Medigap_Header.equalsIgnoreCase(Medigap_Header_Mail)) {
                            println("The Value of Medigap -" + Medigap_Header + "-is matched with-"
                                    + Medigap_Header_Mail);
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                } else if (parameter.equalsIgnoreCase("B")) {
                    if (Health.equalsIgnoreCase("TRUE")) {
                        String Health_Header_Xpath = conf.getObjectProperties("MailCart_FromMail",
                                "Template_B_MailCart_page_first_insurance_Header_Health");
                        String Health_Header = conf.getParameterValue("HEALTH_HEADER");
                        String Health_Header_Mail = webDriver.findElement(By.xpath(Health_Header_Xpath)).getText();
                        if (Health_Header.equalsIgnoreCase(Health_Header_Mail)) {
                            println("The Value of Health -" + Health_Header + "-is matched with-" + Health_Header_Mail);
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                    if (Dental.equalsIgnoreCase("TRUE")) {
                        String Dental_Header_Xpath = conf.getObjectProperties("MailCart_FromMail",
                                "Template_B_MailCart_page_first_insurance_Header_Dental");
                        String Dental_Header = conf.getParameterValue("DENTAL_HEADER");
                        String Dental_Header_Mail = webDriver.findElement(By.xpath(Dental_Header_Xpath)).getText();
                        if (Dental_Header.equalsIgnoreCase(Dental_Header_Mail)) {
                            println("The Value of Dental -" + Dental_Header + "-is matched with-" + Dental_Header_Mail);
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                    if (Vision.equalsIgnoreCase("TRUE")) {
                        String Vision_Header_Xpath = conf.getObjectProperties("MailCart_FromMail",
                                "Template_B_MailCart_page_first_insurance_Header_Vision");
                        String Vision_Header = conf.getParameterValue("VISION_HEADER");
                        String Vision_Header_Mail = webDriver.findElement(By.xpath(Vision_Header_Xpath)).getText();
                        if (Vision_Header.equalsIgnoreCase(Vision_Header_Mail)) {
                            println("The Value of Vision -" + Vision_Header + "-is matched with-" + Vision_Header_Mail);
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                    if (Medigap.equalsIgnoreCase("TRUE")) {
                        String Medigap_Header_Xpath = conf.getObjectProperties("MailCart_FromMail",
                                "Template_B_MailCart_page_first_insurance_Header_Medigap");
                        String Medigap_Header = conf.getParameterValue("MEDIGAP_HEADER");
                        String Medigap_Header_Mail = webDriver.findElement(By.xpath(Medigap_Header_Xpath)).getText();
                        if (Medigap_Header.equalsIgnoreCase(Medigap_Header_Mail)) {
                            println("The Value of Medigap -" + Medigap_Header + "-is matched with-"
                                    + Medigap_Header_Mail);
                            return 0;
                        } else {
                            return 1;
                        }
                    }
                }

            } catch (Exception e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
                return 1;
            }
        }
        return 0;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int demeanorSelection(String parameter) {
        int output = 0;
        String happy = conf.getObjectProperties("ETLog", "Happy");
        String content = conf.getObjectProperties("ETLog", "Content");
        String upset = conf.getObjectProperties("ETLog", "Upset");
        String angry = conf.getObjectProperties("ETLog", "Angry");
        String no_contact = conf.getObjectProperties("ETLog", "No-contact");

        try {
            String reaction = conf.getParameterValue(parameter);
            println("Reaction is: " + reaction);

            if (reaction.equalsIgnoreCase("HAPPY")) {
                webDriver.findElement(By.xpath(happy)).click();
                output = 0;
            } else if (reaction.equalsIgnoreCase("CONTENT")) {
                webDriver.findElement(By.xpath(content)).click();
                output = 0;
            } else if (reaction.equalsIgnoreCase("UPSET")) {
                webDriver.findElement(By.xpath(upset)).click();
                output = 0;
            } else if (reaction.equalsIgnoreCase("ANGRY")) {
                webDriver.findElement(By.xpath(angry)).click();
                output = 0;
            } else if (reaction.equalsIgnoreCase("NO-CONTACT")) {
                webDriver.findElement(By.xpath(no_contact)).click();
                output = 0;
            } else {
                browser.strErrorInfo = "No Demeanor is received as input";
                println("No Demeanor is received as input");

                output = 1;
            }
        } catch (Exception e) {
            // TODO Auto-generated catch block
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            e.printStackTrace();
            output = 1;
        }

        return output;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int etlogNotes(String parameter) {
        int output = 0;

        String textAreaValue = conf.getObjectProperties("ETLog", "ShortcutDescription");
        String category = conf.getParameterValue("CATEGORY").trim();
        println("Category value: " + category);

        String quickNotesCategoryXpath = conf.getObjectProperties("ETLog", "QuickNotes");
        WebElement we = webDriver.findElement(By.xpath(quickNotesCategoryXpath));
        Select select = new Select(we);

        String quickNotesXpath = conf.getObjectProperties("ETLog", "QuickNotes_Description");
        String quickNotesDescXpath = "";
        println("quickNotesXpath" + quickNotesXpath);

        try {
            if (category.equalsIgnoreCase("Billing")) {
                select.selectByVisibleText("Billing");
                int randomNum = randomNumGenerator(1, 8);
                println("Random No is: " + randomNum);
                quickNotesDescXpath = quickNotesXpath + "/li[" + randomNum + "]";
                println("quickNotesDescXpath value: " + quickNotesDescXpath);

            } else if (category.equalsIgnoreCase("Enrollment")) {
                println("Entering into IF else- Enrollment");
                select.selectByVisibleText("Case");
                int randomNum = randomNumGenerator(1, 3);
                println("Random No is: " + randomNum);
                quickNotesDescXpath = quickNotesXpath + "/li[" + randomNum + "]";
                println("quickNotesDescXpath value: " + quickNotesDescXpath);

            } else {
                select.selectByVisibleText("Filter by Category");
                println("Entering into IF else- Category");
                int randomNum = randomNumGenerator(1, 11);
                println("Random No is: " + randomNum);
                quickNotesDescXpath = quickNotesXpath + "/li[" + randomNum + "]";
                println("quickNotesDescXpath value: " + quickNotesDescXpath);
            }
            webDriver.findElement(By.xpath(quickNotesDescXpath)).click();
            Thread.sleep(200);
            String desc = webDriver.findElement(By.xpath(textAreaValue)).getText().trim();
            println("Quick Notes Description: " + desc);
            conf.addRuntimeData(parameter, desc);
            output = 0;

        } catch (Exception e) {
            output = 1;
            println("exception");
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            e.printStackTrace();
        }
        return output;
    }

    /*
     * //---------------------------Mani----------------------------------------
     * -----
     */
    public int randomNumGenerator(int min, int max) {
        Random rn = new Random();
        int randomNum = rn.nextInt(max - min + 1) + min;
        System.out.println(randomNum);
        return randomNum;
    }

    /*
     * //---------------------------Uma-----------------------------------------
     * ----
     */
    public int changePhysicalAddress(String Parameter) {
        try {
            // println("Address");
            String parameterValue[] = Parameter.split(",");
            String COUNTY = conf.getParameterValue(parameterValue[0]);
            String CITY = conf.getParameterValue(parameterValue[1]).trim();
            String STATE = conf.getParameterValue(parameterValue[2]);
            // String Mailing_County = conf.getParameterValue(parameter);
            //
            println(COUNTY);
            println(CITY);
            println(STATE);
            // String Address_search = Mailing_County + " " + "County shop
            // address";
            // println(Address_search);

            System.setProperty("webdriver.chrome.driver",
                    "C:\\SVN\\lib\\Selenium\\utils\\chrome\\chromedriver_win.exe");
            WebDriver d = new ChromeDriver();
            d.get("https://www.Spokeo.com");

            Thread.sleep(3000);
            // d.findElement(By.xpath("//input[@id='lst-ib']")).sendKeys(Address_search+
            // "\n");
            // Thread.sleep(5000);
            // String Address;
            // if
            // (d.findElement(By.xpath("//div[@id='rso']/div[1]/div/div/div[2]/div/div[3]/div[1]/div[1]/div/div/a[1]/div/div")).isDisplayed())
            // {
            d.findElement(By.xpath("//a[contains(text(),'Address')]")).click();
            Thread.sleep(3000);
            // String tempState = conf.getParameterValue(parameterValue[2]);
            String xpath = "//a[contains(text(),'" + STATE + "')]";
            println(xpath);
            d.findElement(By.xpath("//a[contains(text(),'" + STATE + "')]")).click();
            /*
             * WebElement webelement =
             * d.findElement(By.xpath("//a[contains(text(),'"+STATE+"')]"));
             * JavascriptExecutor jse = (JavascriptExecutor)d;
             * jse.executeScript("arguments[0].scrollIntoView()", webelement);
             * webelement.click();
             *
             * /* if (d.findElement(By.xpath("//div[@id='hdtb-msb-vis']/div[2]/a")).
             * isDisplayed()) {
             * d.findElement(By.xpath("//div[@id='hdtb-msb-vis']/div[2]/a")). click();
             * //d.findElement(By.xpath(
             * "//div[@id='pane']/div/div[2]/div/div/div[2]/div[1]/div[1]/div[1]/div[1]/h3/span"
             * )).click();
             */
            Thread.sleep(3000);
            // d.findElement(By.xpath("//a[contains(text(), STATE)]")).click();
            // Thread.sleep(3000);
            d.findElement(By.xpath("//a[contains(text(),'" + COUNTY + "')]")).click();
            Thread.sleep(4000);
            String xpath1 = "//a[contains(text(),'" + CITY + "')]";
            println(xpath1);
            d.findElement(By.xpath("//a[contains(text(),'" + CITY + "')]")).click();
            Thread.sleep(3000);
            d.findElement(By.xpath("//ul[@class='directory_items']/div[1]/li[@class='directory_item'][1]/a")).click();
            Thread.sleep(3000);
            String Address = d.findElement(By.xpath("//input[@value='SEARCH']/following::a[1]")).getText();
            // getText();
            println(Address);
            // String Address1 =
            // d.findElement(By.xpath("//input[@value='SEARCH']/following::a[1]")).getText();
            // String address1 = val[0] + " "+ "#";
            String val[] = Address.split("\n");
            // println(Address1);
            println(val[0]);
            // println(val[2]);
            println(val[1]);
            String City[] = val[1].split(",");
            println(City[0]);
            String State[] = City[1].split(" ");
            println(State[1]);
            println(State[2]);
            conf.addRuntimeData("ADDRESS1", val[0]);
            conf.addRuntimeData("ADDRESS2", City[0]);
            conf.addRuntimeData("ADDRESS3", State[1]);
            conf.addRuntimeData("ADDRESS4", State[2]);
            d.close();

            /*
             * if (d.findElement(By.xpath(
             * "//div[@id='pane']/div/div[2]/div/div/div[3]/div[1]/div[1]/div[1]/div[1]/h3/span"
             * )).isDisplayed()) { d.findElement(By.xpath(
             * "//div[@id='pane']/div/div[2]/div/div/div[3]/div[1]/div[1]/div[1]/div[1]/h3/span"
             * )).click(); Thread.sleep(3000); Address = d.findElement(By.xpath(
             * "//div[@id='pane']/div/div[2]/div/div/div[4]/div[1]/span[3]/span[1]/span"
             * )).getText(); println(Address); String val[] = Address.split(","); // String
             * address1 = val[0] + " "+ "#"; println(val[0]); println(val[1]);
             * println(val[2]); String zip[] = val[2].split(" "); println(zip[2]);
             * conf.addRuntimeData("ADDRESS1", val[0]); conf.addRuntimeData("ADDRESS2",
             * val[1]); conf.addRuntimeData("ADDRESS3", zip[2]); d.close(); } /* } else {
             *
             * d.findElement(By.xpath(
             * "//div[@id='rso']/div/div/div[1]/div/div/h3/a")).click(); Thread.sleep(3000);
             * Address = d.findElement(By.xpath(
             * "//div[3]/div[1]/div[1]/div[4]/div[1]/div/div/div[1]/ul/li[2]/div[3]"
             * )).getText(); println(Address);
             *
             * String val[] = Address.split(","); //String address1 = val[0] + "#";
             * println(val[0]); conf.addRuntimeData(parameter, val[0]); d.close(); }
             */

            return 0;
        } catch (Exception e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
            browser.strErrorInfo = "Exception - " + e;
            this.browser.close();
            return 1;
        }
    }

    /*
     * //---------------------------Uma-----------------------------------------
     * ----
     */
    public int contains(String parameter) {
        int returnValue = 0;
        try {
            String parameterValue = conf.getParameterValue(parameter).toUpperCase();
            println("Parameter Value:" + parameterValue);
            String uivalue = webDriver.findElement(By.xpath(xPath)).getAttribute("value");
            println("UI Value:" + uivalue);

            if (webDriver.findElement(By.xpath(xPath)).getAttribute("value").contains(parameterValue)) {

                returnValue = 0;
            } else {
                returnValue = 1;
            }
        } catch (Exception e) {
            returnValue = 1;
        }
        return returnValue;
    }

    /*
     * //---------------------------Uma-----------------------------------------
     * ----
     */
    public int getCountyAreaFromDB(String parameter) {
        try {
            String County = " ";
            String County_Code = " ";
            String Region = conf.getConfigValue("REGION");
            println(Region);
            String parameterValue[] = parameter.split(",");
            String PLANID = conf.getParameterValue(parameterValue[0]);
            String CASE_NUM = conf.getParameterValue(parameterValue[1]);
            println(PLANID);
            println(CASE_NUM);
            // String parameterValue= conf.getParameterValue(parameter);
            String DB_Url = conf.getParameterValue("DB_URL");
            String userName = conf.getParameterValue("USERNAME");
            String passWord = conf.getParameterValue("PASSWORD");
            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, passWord);

            println("Connection established");

            Statement stmt = con.createStatement();

            String query = "select description, county_code, return_info from " + Region + "."
                    + "zipcode_rating where cov_value = " + "'" + PLANID + "'" + " AND CURRENT_IND=" + "'" + "H" + "'"
                    + " fetch first row only;";
            println(query);

            ResultSet res = stmt.executeQuery(query);
            println("wel");
            while (res.next()) {
                println("Welcome");
                // println(res.getString(10));
                County = res.getString(1);
                County_Code = res.getString(2);
                String Area = res.getString(3);
                conf.addRuntimeData(parameterValue[2], County);
                // conf.addRuntimeData(parameterValue[2], County_Code);
                conf.addRuntimeData(parameterValue[3], Area);
                println("The value updated " + conf.getParameterValue(parameterValue[2]));
                println("The value updated " + conf.getParameterValue(parameterValue[3]));
                // println("The value updated " +
                // conf.getParameterValue(parameterValue[3]));
            }
            String query2 = "select  City_Name, State_CD from " + Region + "." + "State_County_Zip where County_Number "
                    + "=" + "'" + County_Code + "'" + " AND County=" + "'" + County + "'" + " fetch first row only;";
            println(query2);
            ResultSet res1 = stmt.executeQuery(query2);
            while (res1.next()) {
                String City = res1.getString(1);
                String State = res1.getString(2);
                conf.addRuntimeData(parameterValue[4], City);
                conf.addRuntimeData(parameterValue[5], State);
                println("The value updated " + conf.getParameterValue(parameterValue[4]));
                println("The value updated " + conf.getParameterValue(parameterValue[5]));
            }
            String query3 = "select RATE_REFERENCE_DTE from " + Region + "." + "Case_Master where CASE_NUM=" + "'"
                    + CASE_NUM + "';";
            println(query3);
            ResultSet res2 = stmt.executeQuery(query3);
            while (res2.next()) {
                String RATE_REFERENCE_DATE = res2.getString(1);
                conf.addRuntimeData(parameterValue[6], RATE_REFERENCE_DATE);
                println("The value updated for RATE_REFERENCE_DATE is: " + conf.getParameterValue(parameterValue[6]));

            }
            return 0;
        } catch (Exception e) {
            return 1;
        }
    }

    /*
     * //---------------------------Uma-----------------------------------------
     * ----
     */
    public int checkRatingON(String parameter) {
        try {
            String Region = conf.getConfigValue("REGION");
            println(Region);
            String parameterValue[] = parameter.split(",");
            String PLANID = conf.getParameterValue(parameterValue[0]);
            String CARRIERNAME = conf.getParameterValue(parameterValue[1]);
            // String RATINGON = conf.getParameterValue(parameterValue[2]);
            println(PLANID);
            println(CARRIERNAME);
            String DB_Url = conf.getParameterValue("DB_URL");
            String userName = conf.getParameterValue("USERNAME");
            String passWord = conf.getParameterValue("PASSWORD");
            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, passWord);

            println("Connection established");

            Statement stmt = con.createStatement();
            if (CARRIERNAME.equals("Aetna")) {
                String CarrierCode = "X5";

                String query = "SELECT RATING_ALGORITHM_INFORCE FROM " + Region + "."
                        + "BENEFIT_SOLUTIONS_CATALOG WHERE HPS_PLAN_CODE = " + "'" + PLANID + "'"
                        + " AND DETAIL_CARRIER = " + "'" + CarrierCode + "'" + "AND STATUS = " + "'H';";
                println(query);

                ResultSet res = stmt.executeQuery(query);
                println("wel");
                while (res.next()) {
                    println("Welcome");
                    // println(res.getString(10));
                    String RatingON = res.getString(1);
                    println(RatingON);
                    if ((RatingON.equals("RTE")) || (RatingON.equals("RTC")) || (RatingON.equals("RTR"))
                            || (RatingON.equals("RTECR"))) {
                        conf.addRuntimeData(parameterValue[2], "True");
                    }
                    println("The value updated " + conf.getParameterValue(parameterValue[2]));
                }

            }

            if (CARRIERNAME.equals("CIGNA II - Individual")) {
                String CarrierCode = "JT";

                String query = "SELECT RATING_ALGORITHM_INFORCE FROM " + Region + "."
                        + "BENEFIT_SOLUTIONS_CATALOG WHERE HPS_PLAN_CODE = " + "'" + PLANID + "'"
                        + " AND DETAIL_CARRIER = " + "'" + CarrierCode + "'" + "AND STATUS = " + "'H';";
                println(query);

                ResultSet res = stmt.executeQuery(query);
                println("wel");
                while (res.next()) {
                    println("Welcome");
                    // println(res.getString(10));
                    String RatingON = res.getString(1);
                    println(RatingON);
                    if ((RatingON.equals("RTE")) || (RatingON.equals("RTC")) || (RatingON.equals("RTR"))
                            || (RatingON.equals("RTECR"))) {
                        conf.addRuntimeData(parameterValue[2], "True");
                    }
                    println("The value updated " + conf.getParameterValue(parameterValue[2]));
                }

            }
            if (CARRIERNAME.equals("Coventry")) {
                String CarrierCode = "CV";

                String query = "SELECT RATING_ALGORITHM_INFORCE FROM " + Region + "."
                        + "BENEFIT_SOLUTIONS_CATALOG WHERE HPS_PLAN_CODE = " + "'" + PLANID + "'"
                        + " AND DETAIL_CARRIER = " + "'" + CarrierCode + "'" + "AND STATUS = " + "'H';";
                println(query);

                ResultSet res = stmt.executeQuery(query);
                println("wel");
                while (res.next()) {
                    println("Welcome");
                    // println(res.getString(10));
                    String RatingON = res.getString(1);
                    println(RatingON);
                    if ((RatingON.equals("RTE")) || (RatingON.equals("RTC")) || (RatingON.equals("RTR"))
                            || (RatingON.equals("RTECR"))) {
                        conf.addRuntimeData(parameterValue[2], "True");
                    }
                    println("The value updated " + conf.getParameterValue(parameterValue[2]));
                }

            }
            if (CARRIERNAME.equals("Blue Shield of California")) {
                String CarrierCode = "X2";

                String query = "SELECT RATING_ALGORITHM_INFORCE FROM " + Region + "."
                        + "BENEFIT_SOLUTIONS_CATALOG WHERE HPS_PLAN_CODE = " + "'" + PLANID + "'"
                        + " AND DETAIL_CARRIER = " + "'" + CarrierCode + "'" + "AND STATUS = " + "'H';";
                println(query);

                ResultSet res = stmt.executeQuery(query);
                println("wel");
                while (res.next()) {
                    println("Welcome");
                    // println(res.getString(10));
                    String RatingON = res.getString(1);
                    println(RatingON);
                    if ((RatingON.equals("RTE")) || (RatingON.equals("RTC")) || (RatingON.equals("RTR"))
                            || (RatingON.equals("RTECR"))) {
                        conf.addRuntimeData(parameterValue[2], "True");
                    }
                    println("The value updated " + conf.getParameterValue(parameterValue[2]));
                }

            }
            if (CARRIERNAME.equals("Health Care Service Corporation (HCSC)")) {
                String CarrierCode = "X6";

                String query = "SELECT RATING_ALGORITHM_INFORCE FROM " + Region + "."
                        + "BENEFIT_SOLUTIONS_CATALOG WHERE HPS_PLAN_CODE = " + "'" + PLANID + "'"
                        + " AND DETAIL_CARRIER = " + "'" + CarrierCode + "'" + "AND STATUS = " + "'H';";
                println(query);

                ResultSet res = stmt.executeQuery(query);
                println("wel");
                while (res.next()) {
                    println("Welcome");
                    // println(res.getString(10));
                    String RatingON = res.getString(1);
                    println(RatingON);
                    if ((RatingON.equals("RTE")) || (RatingON.equals("RTC")) || (RatingON.equals("RTR"))
                            || (RatingON.equals("RTECR"))) {
                        conf.addRuntimeData(parameterValue[2], "True");
                    }
                    println("The value updated " + conf.getParameterValue(parameterValue[2]));
                }

            }
            if (CARRIERNAME.equals("Kaiser Permanente")) {
                String CarrierCode = "XB";

                String query = "SELECT RATING_ALGORITHM_INFORCE FROM " + Region + "."
                        + "BENEFIT_SOLUTIONS_CATALOG WHERE HPS_PLAN_CODE = " + "'" + PLANID + "'"
                        + " AND DETAIL_CARRIER = " + "'" + CarrierCode + "'" + "AND STATUS = " + "'H';";
                println(query);

                ResultSet res = stmt.executeQuery(query);
                println("wel");
                while (res.next()) {
                    println("Welcome");
                    // println(res.getString(10));
                    String RatingON = res.getString(1);
                    println(RatingON);
                    if ((RatingON.equals("RTE")) || (RatingON.equals("RTC")) || (RatingON.equals("RTR"))
                            || (RatingON.equals("RTECR"))) {
                        conf.addRuntimeData(parameterValue[2], "True");
                    }
                    println("The value updated " + conf.getParameterValue(parameterValue[2]));
                }

            }
            if (CARRIERNAME.equals("Wellmark")) {
                String CarrierCode = "X7";

                String query = "SELECT RATING_ALGORITHM_INFORCE FROM " + Region + "."
                        + "BENEFIT_SOLUTIONS_CATALOG WHERE HPS_PLAN_CODE = " + "'" + PLANID + "'"
                        + " AND DETAIL_CARRIER = " + "'" + CarrierCode + "'" + "AND STATUS = " + "'H';";
                println(query);

                ResultSet res = stmt.executeQuery(query);
                println("wel");
                while (res.next()) {
                    println("Welcome");
                    // println(res.getString(10));
                    String RatingON = res.getString(1);
                    println(RatingON);
                    if ((RatingON.equals("RTE")) || (RatingON.equals("RTC")) || (RatingON.equals("RTR"))
                            || (RatingON.equals("RTECR"))) {
                        conf.addRuntimeData(parameterValue[2], "True");
                    }
                    println("The value updated " + conf.getParameterValue(parameterValue[2]));
                }

            }
            return 0;
        } catch (Exception e) {
            println(e.toString());
            browser.strErrorInfo = "" + e;
            return 1;
        }
    }

    /*
     * //---------------------------Uma-----------------------------------------
     * ----
     */
    public int verifyRate(String parameter)

    {
        try {
            int output = 0;
            String parameterValue[] = parameter.split(",");
            String PLANID = conf.getParameterValue(parameterValue[0]);

            String PLANID1 = PLANID.substring(PLANID.length() - 2);
            println("Last 2 character of PLANID1 is: " + PLANID1);
            if (PLANID1.equals("OE") || (PLANID1.equals("01"))) {
                PLANID1 = PLANID;
                println("PLANID1 is: " + PLANID1);
            } else if (!(PLANID1.equals("01"))) {
                PLANID1 = PLANID.substring(0, PLANID.length() - 2) + "01";
                println("PLANID1 is: " + PLANID1);
            }

            String AREA = conf.getParameterValue(parameterValue[1]).trim();
            String DETAIL_CASE_NUM = conf.getParameterValue(parameterValue[2]);
            String OVERALL_PREMIUM = conf.getParameterValue(parameterValue[3]);

            String RATE_REFERENCE_DATE = conf.getParameterValue(parameterValue[4]);

            // println("PLANID: "+PLANID);
            println("AREA: " + AREA);
            println("DETAIL_CASE_NUM: " + DETAIL_CASE_NUM);
            println("OVERALL_PREMIUM: " + OVERALL_PREMIUM);
            println("RATE_REFERENCE_DATE: " + RATE_REFERENCE_DATE);

            String REGION = conf.getConfigValue("REGION");
            println("REGION: " + REGION);

            /*
             * List<WebElement> rows = webDriver.findElements(By.xpath(
             * "//table[@class='coverage-details']/tbody/tr[2]/td[2]/table/tbody/tr" )); int
             * rowCount= rows.size(); println("rowCount: "+rowCount);
             */

            Statement stmt = null;

            ArrayList<ArrayList<String>> getdepvalue_list = getValuesFromDependentWebtable(webDriver);

            /*
             * for(ArrayList<String> sub_list:getdepvalue_list){ // iterate -list by list
             */
            for (int i = 0; i < getdepvalue_list.size(); i++) {

                String DOB = getdepvalue_list.get(i).get(0);
                String SMOKER = getdepvalue_list.get(i).get(1);
                String INDV_COMM_PREM = getdepvalue_list.get(i).get(2);

                // println("//table[@class='coverage-details']/tbody/tr[2]/td[2]/table/tbody/tr["+row+"]/td[5]");
                // String DOB =
                // webDriver.findElement(By.xpath("//table[@class='coverage-details']/tbody/tr[2]/td[2]/table/tbody/tr["+row+"]/td[5]")).getText().toString();
                println("dob:" + DOB);
                String a[] = DOB.split("/");
                int month = Integer.parseInt(a[0]);
                println("month: " + month);
                int date = Integer.parseInt(a[1]);
                println("date: " + date);
                int year = Integer.parseInt(a[2]);
                println("year: " + year);

                String b[] = RATE_REFERENCE_DATE.split("-");
                int month1 = Integer.parseInt(b[2]);
                println("month1: " + month1);
                int date1 = Integer.parseInt(b[1]);
                println("date1: " + date1);
                int year1 = Integer.parseInt(b[0]);
                println("year1: " + year1);

                // LocalDate today = LocalDate.now();
                // println("Today: "+today);
                LocalDate DOB1 = LocalDate.of(year, month, date);
                println("dob: " + DOB1);
                LocalDate rateRefDate = LocalDate.of(year1, month1, date1);
                println("rateRefDate: " + rateRefDate);

                Period p = Period.between(DOB1, rateRefDate);
                // Period p = Period.between(DOB1, today);
                String age = String.valueOf(p.getYears());
                println("AGE: " + age);

                int age1 = Integer.parseInt(age);
                if (age1 <= 20) {
                    age = "0";
                    println("AGE is: " + age1);
                } else if (age1 >= 65) {
                    age = "65";
                    println("AGE is: " + age1);
                } else {
                    // age = age-1;
                    age = String.valueOf(age1);
                    println("AGE is: " + age1);
                }

                // String SMOKER =
                // webDriver.findElement(By.xpath("//table[@class='coverage-details']/tbody/tr[2]/td[2]/table/tbody/tr["+row+"]/td[7]")).getText().toString();

                char smoker = SMOKER.charAt(0);
                println("Char of smoker: " + smoker);

                String premiumFromDB = "";

                String DB_Url = conf.getParameterValue("DB_URL");
                String userName = conf.getParameterValue("USERNAME");
                String passWord = conf.getParameterValue("PASSWORD");
                String dbClass = "com.ibm.db2.jcc.DB2Driver";
                Class.forName(dbClass).newInstance();
                Connection con = DriverManager.getConnection(DB_Url, userName, passWord);
                println("Connection established");
                stmt = con.createStatement();

                String dbQuery = "SELECT RATING_VALUE FROM " + REGION + ".COVERAGE_RATING WHERE AREA='" + AREA
                        + "'AND COV_VALUE='" + PLANID1 + "' AND START_AGE ='" + age + "' AND SMOKER_CODE='" + smoker
                        + "' AND TIER='S' AND COVERAGE='S';";
                println("dbQuery: " + dbQuery);
                ResultSet res3 = stmt.executeQuery(dbQuery);

                while (res3.next()) {
                    premiumFromDB = res3.getString(1);
                    println("Premium value fetched from DB: " + premiumFromDB);
                }

                // String INDV_COMM_PREM =
                // webDriver.findElement(By.xpath("//table[@class='coverage-details']/tbody/tr[2]/td[2]/table/tbody/tr["+row+"]/td[10]")).getText().toString();

                /*
                 * NumberFormat format = NumberFormat.getCurrencyInstance(); Number number =
                 * format.parse(INDV_COMM_PREM); String INDV_COMM_PREM1 = number.toString();
                 */
                String INDV_COMM_PREM1 = INDV_COMM_PREM.replace(",", "");

                println("INDV_COMM_PREM1: " + INDV_COMM_PREM1);
                int decimalIndex = premiumFromDB.indexOf(".");
                println("Decimal index in the Premium from DB: " + decimalIndex);
                String s1 = "";
                String s2 = "";

                switch (decimalIndex) {

                    case 1:
                        s1 = INDV_COMM_PREM1.substring(1, 5);
                        s2 = premiumFromDB.substring(0, 4);
                        println("Premium from SLP: " + s1 + "Premium from DB: " + s2);
                        break;

                    case 2:
                        s1 = INDV_COMM_PREM1.substring(1, 6);
                        s2 = premiumFromDB.substring(0, 5);
                        println("Premium from SLP: " + s1 + "Premium from DB: " + s2);
                        break;

                    case 3:
                        s1 = INDV_COMM_PREM1.substring(1, 7);
                        s2 = premiumFromDB.substring(0, 6);
                        println("Premium from SLP: " + s1 + "Premium from DB: " + s2);
                        break;

                    case 4:
                        s1 = INDV_COMM_PREM1.substring(1, 8);
                        s2 = premiumFromDB.substring(0, 7);
                        println("Premium from SLP: " + s1 + "Premium from DB: " + s2);
                        break;

                    case 5:
                        s1 = INDV_COMM_PREM1.substring(1, 9);
                        s2 = premiumFromDB.substring(0, 8);
                        println("Premium from SLP: " + s1 + "Premium from DB: " + s2);
                        break;
                }

                if (s1.equals(s2)) {
                    output = 0;

                } else {
                    output = 1;

                }

            }

            String dbQuery1 = "SELECT COV_VALUE FROM " + REGION + ".COVERAGE_HISTORY WHERE CASE_NUM IN ('"
                    + DETAIL_CASE_NUM + "') AND COV_ENTITY='HEALTH' AND COV_QUALIFIER='PREMIUM' AND STATUS='C';";

            println("dbQuery1: " + dbQuery1);
            ResultSet res4 = stmt.executeQuery(dbQuery1);
            String premiumFromDB1 = "";
            String s3 = "";
            String s4 = "";
            NumberFormat format1 = NumberFormat.getCurrencyInstance();
            Number number1 = format1.parse(OVERALL_PREMIUM);
            s3 = number1.toString();
            println("S3: " + s3);
            int len = s3.length();
            println("len1: " + len);

            while (res4.next()) {
                premiumFromDB1 = res4.getString(1).trim();
                println("Premium value fetched from DB: " + premiumFromDB1);
            }
            int l1 = premiumFromDB1.length();
            println("l1: " + l1);
            s4 = premiumFromDB1.substring(l1 - len);
            println("S4: " + s4);

            if (s3.equals(s4)) {
                output = 0;
            } else {
                output = 1;
            }

            return output;

        } catch (Exception e) {
            return 1;
        }
    }

    // Authour - Nandha - Wait till Alert message disappear

    /*
     * This Method will return Arrraylist of Arrraylist
     * Example:[[DOB,SMOKER,PREMIUM][DOB,SMOKER,PREMIUM][DOB,SMOKER,PREMIUM]]
     *
     */
    public ArrayList<ArrayList<String>> getValuesFromDependentWebtable(WebDriver webDriver) throws Exception {
        ArrayList<String> sip_value_sub_array = new ArrayList<>();
        ArrayList<ArrayList<String>> sip_value_main_array = new ArrayList<>();
        String keyvalue = "";
        String headerpath = "/thead/tr";
        String bodypath = "/tbody";
        int depcount = 0;
        String deptableXpath = "";

        println("");

        String deptable = "//*/table";
        List<WebElement> tables = webDriver.findElements(By.xpath(deptable));

        // ServiceLink - Coverage Dependents
        // //*[@class="coverage-details"]/tbody/tr[2]/td[2]/table
        // ServiceLink - Coverage Dependents_Table //*[@id="dependent-details"]

        for (int i = 0; i <= tables.size() - 1; i++) {
            String s = tables.get(i).getAttribute("class");

            if (s.equals("dependent-details")) {
                deptableXpath = conf.getObjectProperties("ServiceLink - Coverage", "Dependents_Table");
                println("XAPTH------>" + deptableXpath);
            } else {
                deptableXpath = conf.getObjectProperties("ServiceLink - Coverage", "Dependents");
                println("XAPTH------>" + deptableXpath);

            }

        }

        int headerstatus = getDependentWebtableHeaderList(deptableXpath + "" + headerpath);
        WebElement mytable = webDriver.findElement(By.xpath(deptableXpath + "" + bodypath));

        println("VALUES--" + deptableXpath + "" + bodypath);
        println("HEADER-" + deptableXpath + "" + headerpath);
        List<WebElement> rows_table = mytable.findElements(By.tagName("tr"));

        int rows_count = rows_table.size();

        for (int row = 0; row < rows_count; row++) {
            depcount = 0;
            List<WebElement> Columns_row = rows_table.get(row).findElements(By.tagName("td"));

            int columns_count = Columns_row.size();

            for (int column = 0; column < columns_count; column++) {

                String celtext = Columns_row.get(column).getText();

                if ((column == DateofBirth) || (column == Smoker) || (column == IndvCommPrem)) {
                    sip_value_sub_array.add(celtext);
                }

            }
            if (depcount == 0) {
                sip_value_main_array.add(sip_value_sub_array);
            }
            sip_value_sub_array = new ArrayList<>();
            println("sip_value_sub_array--------------------------------------------------" + sip_value_main_array);
            println("--------------------------------------------------");
        }

        return sip_value_main_array;
    }

    public int waittilldisappear(String parameter) {
        String strElement = xPath;
        int time = Integer.parseInt(parameter);

        try {
            WebDriverWait wait = new WebDriverWait(webDriver, time);
            wait.until(ExpectedConditions.invisibilityOfElementLocated(By.xpath(strElement)));
            println("waited till " + parameter + " seconds for invisiblity of element: ");
            String actual = "waited for " + parameter + " seconds";
            // log.logExecutionResults("Step Name", "Description", "Expected",
            // "Passed", actual, 10, "", "SampleFunction");

            return 0;
        } catch (Exception e) {

            browser.strErrorInfo = "" + e;
            String exception = e.toString();
            println(exception);
            return 0;
        }
    }

    // Author - Nandha
    public int failtest(String parameter) {
        browser.strErrorInfo = parameter;
        println("parameter");

        return 1;
    }

    /*
     * @Description This validates the page url of expected versus actual
     *
     * @Param Expected url
     *
     * @return Boolean
     *
     */
    public int assertPageByUrl(String parameter) {

        int returnValue = 1;
        try {
            String currentUrl = webDriver.getCurrentUrl();
            println(currentUrl);
            println(parameter);
            if (currentUrl.contains(parameter)) {
                returnValue = 0;

            } else {
                browser.strErrorInfo = " URL mismatch";
                this.browser.close();
                returnValue = 1;
            }
        } catch (Exception e) {
            returnValue = 1;
            browser.strErrorInfo = " " + e;
            this.browser.close();
        }
        return returnValue;

    }

    /*
     * @Description This gets the Error text values and assigns it to the variable
     *
     * @return Boolean
     *
     * @param Variable to which text will be assigned
     *
     * @author Nandha
     */
    public int errorMsglist(String parameter) {
        String errtext = "";
        int returnvalue = 1;

        try {
            List<WebElement> errorelement = webDriver.findElements(By.xpath(xPath));
            if (!errorelement.isEmpty()) {
                for (WebElement err : errorelement) {
                    errtext = errtext + err.getAttribute("innerHTML") + " ; ";
                }
                String enabled = conf.getParameterValue(parameter);
                println(parameter + " with value: " + enabled);
                if (enabled.equalsIgnoreCase("true"))
                    returnvalue = 0;
                println("ERROR MESSAGE :  " + errtext);
                conf.addRuntimeData("ERROR", errtext);
                browser.strErrorInfo = " " + errtext;
            } else {
                errtext = "No error message";
                println("No error message");
                conf.addRuntimeData(parameter, "false");
                conf.addRuntimeData("ERROR", "No Error Message");
                println(parameter + " changed to false");
                returnvalue = 0;
            }
            return returnvalue;
        } catch (Exception e) {
            println("exception");
            println(e.toString());
            browser.strErrorInfo = " " + e;
            return 1;
        }
    }

    public int getDependentWebtableHeaderList(String xpath) throws Exception {
        ArrayList<String> header_list = new ArrayList<>();
        WebElement mytable = webDriver.findElement(By.xpath(xpath));
        int headcount = 0;

        List<WebElement> table_header = mytable.findElements(By.tagName("th"));

        int header_count = table_header.size();
        for (int header = 0; header < header_count; header++) {
            String celtext = table_header.get(header).getText();
            header_list.add(celtext);

            if (celtext.equals("Date of Birth")) {
                DateofBirth = header;
                println("DateofBirth  col value -----------------------------------------" + DateofBirth);
                headcount++;
            } else if (celtext.equals("Smoker Indicator") || (celtext.equals("Smoker"))) {
                Smoker = header;
                println("Smoker Indicator  col value -----------------------------------------" + Smoker);
                headcount++;
            } else if (celtext.equals("Indv Comm Prem") || (celtext.equals("Premium"))) {
                IndvCommPrem = header;
                println("IndvCommPrem  col value -----------------------------------------" + IndvCommPrem);
                headcount++;
            }

        }

        if (headcount == 2) {
            // true all the 3 column in table
            return 0;
        } else {
            // false some column missing
            return 1;
        }

    }

    public int getifcondition(String parameter) {

        String cond = "";
        try {
            String value = conf.getParameterValue(parameter);

            if (value.equalsIgnoreCase("True")) {
                conf.addRuntimeData(parameter, "True");
                cond = conf.getParameterValue(parameter);
                println(parameter + " has data so: " + cond);
            } else {
                conf.addRuntimeData(parameter, "False");
                cond = conf.getParameterValue(parameter);
                println(parameter + " has no data so : " + cond);
                return 0;
            }

        } catch (Exception e) {
            conf.addRuntimeData(parameter, "False");
            cond = conf.getParameterValue(parameter);
            println(parameter + " has exception so:  " + cond);
        }
        return 0;

    }

    public int casenotes(String parameter) {

        try {
            boolean casenotesdisplayed = webDriver.findElement(By.xpath("//div[@class='widget-case-note opened']"))
                    .isDisplayed();
            println("The casenotesdisplayed" + casenotesdisplayed);
            if (casenotesdisplayed == true) {

                webDriver.findElement(By.xpath("//span[@class='notes-label number']")).click();
            } else {
                println("Case notes is" + casenotesdisplayed);

            }

            return 0;

        } catch (Exception e) { // TODO Auto-generated catch block
            // e.printStackTrace();
            /*
             * browser.strErrorInfo=""+e; this.browser.close();
             */
            println("Case notes not found");
            return 0;
        }
    }

    public int errorNotFound(String parameter) {

        try {

            if (webDriver.findElements(By.xpath(xPath)).isEmpty()) {
                println("Error Not found");
                return 0;

            } else {
                String errormsg = webDriver.findElement(By.xpath(xPath)).getAttribute("innerHTML");
                println("Error Found : " + errormsg);
                browser.strErrorInfo = "APPLICATION ERROR: " + errormsg;
                // this.browser.close();
                return 1;
            }
        } catch (Exception e) {
            println("Exception in Keyworkd : " + e.toString());
            browser.strErrorInfo = "APPLICATION ERROR FOUND" + e;
            // this.browser.close();
            return 0;
        }

    }

    public int warningNotFound() {

        WebDriverWait wait = new WebDriverWait(webDriver, 1);
        try {
            String warning = wait.until(ExpectedConditions.presenceOfElementLocated(By.xpath(xPath)))
                    .getAttribute("innerHTML");
            browser.strErrorInfo = warning;
            println(warning);
            return 1;
        } catch (NoSuchElementException e) {
            browser.strErrorInfo = "No Warning Message Found";
            println("No Warning Message Found");
            return 0;
        } catch (Exception e) {
            browser.strErrorInfo = "" + e;
            println("Exception occured" + e);
            return 0;
        }
    }

    public int checkAndRemoveDependentFromTable(String parameter) {
        println("");
        println(generalutils.getMethodName());

        int depcount = 0;
        String firstname = conf.getParameterValue("FIRSTNAME");
        String lastname = conf.getParameterValue("LASTNAME");
        String termdate_raw = conf.getParameterValue("TERMDATE");
        String dob_raw = conf.getParameterValue("DOB");
        String termdate = termdate_raw.substring(1, termdate_raw.length());
        String dob = dob_raw.substring(1, dob_raw.length());
        // dob=dateFormat(dob);
        println("==============");
        println("FIRSTNAME--->" + firstname);
        println("FIRSTNAME--->" + lastname);
        println("TERMDATE--->" + termdate);
        println("DOB--->" + dob);
        println("==============");
        WebDriverWait wait = new WebDriverWait(webDriver, 20);
        // wait.until(ExpectedConditions.visibilityOfElementLocated(By.xpath("//*[@id='print-header']/div/button[2]")));

        // To add additional results to HTML report
        log.logExecutionResults("REMOVEDEPENDENT",
                "Start>>>>Remove the dependent using First, Last Name and DOB combination", "Firstname", "Passed",

                firstname, 10, "", "REMOVEDEPENDENT");
        log.logExecutionResults("REMOVEDEPENDENT",
                "Start>>>>Remove the dependent using First, Last Name and DOB combination", "Lastname", "Passed",

                lastname, 10, "", "REMOVEDEPENDENT");
        log.logExecutionResults("REMOVEDEPENDENT",
                "Start>>>>Remove the dependent using First, Last Name and DOB combination", "Termdate", "Passed",

                termdate, 10, "", "REMOVEDEPENDENT");
        log.logExecutionResults("REMOVEDEPENDENT",
                "Start>>>>Remove the dependent using First, Last Name and DOB combination", "DOB", "Passed",

                dob, 10, "", "REMOVEDEPENDENT");

        try {
            String mainXpath = conf.getObjectProperties("CaseDetails_Dependent", "DependentDetails_Table");
            int rowCountBefore = rowcount(mainXpath);
            // WebElement mytable =
            // webDriver.findElement(By.xpath(conf.getObjectProperties("CaseDetails_Dependent",
            // "DependentDetails_Table")));
            // wait.until(ExpectedConditions.visibilityOf(mytable));
            // WebElement mytable = webDriver.findElement(By.xpath(xPath));
            // WebElement mytable =
            // webDriver.findElement(By.xpath("//table[@id='account_vault_accounts_table']"));

            // List<WebElement> tbody_table = mytable.findElements(By.xpath("//tbody"));

            // int tbody_count = tbody_table.size();
            // println("tbody Count - " + tbody_count);

            //// table[contains(@class,'coverage-details')]//tbody//tr[contains(@class,'tree-child
            //// tree-1')]//table//tbody
            // tr[td[contains(text(),'Steveb')] and td[contains(text(),'Bhalleb')] and
            //// td[contains(@class,'DependentNameField')] and
            //// td[contains(@class,'DependentDOBField') and
            //// contains(text(),'07/24/2013')]]//td//form//span[contains(@title,'Double')]
            // following::span//input[contains(@id,'dep_term_date')]

            String xpathFormed = mainXpath
                    + "//tr[td[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),translate('"
                    + firstname
                    + "','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))] and td[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),translate('"
                    + lastname
                    + "','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))] and td[contains(@class,'DependentNameField')] and td[contains(@class,'DependentDOBField') and contains(text(),'"
                    + dob + "')]]";
            //// tr[@class='tree-child
            //// tree-1']/td[@class='triangle-up']/table/tbody//tr[td[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),translate('"+firstname+"','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))]
            //// and
            //// td[contains(translate(text(),'ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'),translate('"+lastname+"','ABCDEFGHIJKLMNOPQRSTUVWXYZ','abcdefghijklmnopqrstuvwxyz'))]
            //// and td[contains(@class,'DependentNameField')] and
            //// td[contains(@class,'DependentDOBField') and contains(text(),'"+ dob +"')]];

            List<WebElement> tbody_row = null;
            WebElement termdate2 = null;
            WebElement termdate2Save = null;
            try {
                println("Trying to Find xpath td");
                tbody_row = webDriver.findElements(By.xpath(xpathFormed));
                // wait.until(ExpectedConditions.visibilityOfAllElements(tbody_row));
            } catch (NoSuchElementException e1) {
                // TODO Auto-generated catch block
                println(e1.toString());
                log.logExecutionResults("REMOVEDEPENDENT", "Exception", "Exception", "Failed", e1.toString(), 10, "",
                        "REMOVEDEPENDENT");
                return 1;
            }

            int rows_count = tbody_row.size();
            println("Row Count - " + rows_count);
            if (rows_count > 0) {
                /*
                 * for (int row = 0; row < rows_count; row++) {
                 */

                List<WebElement> Columns_row;
                WebElement pencilIcon = null;
                try {
                    pencilIcon = webDriver
                            .findElement(By.xpath(xpathFormed + "//td//form[contains(@id,'dep-terminate-form')]"));

                    // wait.until(ExpectedConditions.visibilityOf(pencilIcon));
                } catch (NoSuchElementException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                    println(e.toString());
                    log.logExecutionResults("REMOVEDEPENDENT", "Exception", "Exception", "Failed", e.toString(), 10, "",
                            "REMOVEDEPENDENT");
                    return 1;
                }

                if (pencilIcon.isDisplayed()) {
                    log.logExecutionResults("REMOVEDEPENDENT", "Dependent Display", "Display", "Passed",
                            "Dependent details is displayed", 10, "", "REMOVEDEPENDENT");
                    println("Pencil icon is displayed");
                    Actions act = new Actions(webDriver);
                    act.doubleClick(pencilIcon).build().perform();
                    // Thread.sleep(5000);
                    println("Double click triggered");
                    Thread.sleep(3000);
                    termdate2 = webDriver.findElement(By.xpath(xpathFormed
                            + "//td//form[contains(@id,'dep-terminate-form')]//span//input[contains(@id,'dep_term_date')]"));
                    // wait.until(ExpectedConditions.visibilityOf(termdate2));
                    if (termdate2.isDisplayed()) {
                        termdate2.sendKeys(termdate);
                        String finalXpath = xpathFormed
                                + "//td//form[contains(@id,'dep-terminate-form')]//span//span//button[contains(@type,'submit')]";
                        webDriver.findElement(By.xpath(xpathFormed + "//td[contains(@class,'DependentNameField')]"))
                                .click();
                        Thread.sleep(2000);
                        termdate2Save = webDriver.findElement(By.xpath(finalXpath));
                        termdate2Save.click();
                        return 0;
                    } else {
                        println("Dependent details is not displayed");
                        log.logExecutionResults("REMOVEDEPENDENT", "Dependent Display", "Display", "Failed",
                                "Dependent details is not displayed", 10, "", "REMOVEDEPENDENT");
                    }
                    String Submit_btn_xpsth = conf.getObjectProperties("CaseDetails_Dependent", "Confirmation_Submit1");
                    WebElement submi_btn = webDriver.findElement(By.xpath(Submit_btn_xpsth));
                    wait.until(ExpectedConditions.elementToBeClickable(submi_btn));
                    /*
                     * if(submi_btn.isEnabled()) { println("Submit button is enabled");
                     * //submi_btn.click(); println("Submit button is clicked"); }else {
                     * println("Submit button is not enabled"); Thread.sleep(5000);
                     * println("Refreshed"); if(submi_btn.isEnabled()) {
                     * println("Submit button is enabled"); submi_btn.click();
                     * println("Submit button is clicked"); }else { return 1; } }
                     */
                    /*
                     * WebElement mainDepTable = webDriver.findElement(By.xpath(mainXpath));
                     * wait.until(ExpectedConditions.visibilityOf(mainDepTable));
                     * //CaseDetails_Dependent DependentDetails_Table
                     */
                    /*
                     * try { int rowCountAfter=rowcount(mainXpath); println("rowCountAfter:" +
                     * rowCountAfter);
                     *
                     * if(rowCountBefore!=rowCountAfter) {
                     *
                     * println("Table is updated; - Removed"); return 0; } else {
                     * println("Table is not updated; - not Removed"); return 1; } } catch
                     * (Exception e) { // TODO Auto-generated catch block
                     * println("Table is not updated");
                     *
                     * e.printStackTrace(); return 0; }
                     */

                } else {
                    println("Pencil icon is not displayed");
                    log.logExecutionResults("REMOVEDEPENDENT", "Pencil icon check", "Pencil icon needs to be displayed",
                            "Failed", "Pencil icon is not displayed", 10, "", "REMOVEDEPENDENT");
                    return 1;

                }

            } else {
                return 1;
            }
        } catch (Exception e) {
            e.printStackTrace();
            println(e.toString());
            log.logExecutionResults("REMOVEDEPENDENT", "Exception", "Exception", "Failed", e.toString(), 10, "",
                    "REMOVEDEPENDENT");
            return 1;
        }

        return 0;
    }

    public int rowcount(String xPath) {
        String strElement = xPath;
        try {

            WebElement we = webDriver.findElement(By.xpath(strElement));

            /***** GET ALL THE TRs INSIDE THE WEB ELEMENT - TBODY *****/
            List<WebElement> tableRows = we.findElements(By.tagName("tr"));
            String dbQuery;
            int tblSize = tableRows.size();
            String rownos = Integer.toString(tblSize);
            println("tblSize: " + rownos);
            // conf.addRuntimeData(parameter, rownos);
            return tblSize;
        } catch (Exception e) {
            println(e.toString());
            browser.strErrorInfo = "" + e;
            return 0;
        }

    }

    public int checkallbox() {

        try {
            String idvalue = "";
            List<WebElement> checkbox = webDriver.findElements(By.xpath(xPath));
            for (WebElement box : checkbox) {
                box.click();
                idvalue = box.getAttribute("id");
                println(idvalue + " Check box clicked");
            }
            return 0;
        } catch (Exception e) {
            println("Exception in Keyworkd : " + e.toString());
            browser.strErrorInfo = "" + e;
            return 1;
        }

    }

    public int resultDetails(String paramter) {
        try {
            String strPath = "";
            String casenum = "";
            String cim = "";
            String casenumvalue = "";
            String para = "";
            DateFormat dateFormat = new SimpleDateFormat("MM/dd/yyyy");
            Date date = new Date();
            String finalDate = dateFormat.format(date).toString();
            System.out.println("Final :" + finalDate);

            para = this.conf.getParameterValue(paramter);

            strPath = this.log.getResultsFolder();
            String strTestName = this.mgr.getTestName();
            String strIterationName = this.conf.strIterationName;
            try {
                String filename = "C:\\CASA\\OLS\\Scenarios\\CasaScenarios\\testdata\\resultdetails.csv";
                String line = finalDate + "," + strTestName + "," + strIterationName + "," + para + "," + strPath
                        + "\n";
                FileWriter fw = new FileWriter(filename, true);
                fw.write(line);
                fw.close();
                this.log.logExecutionResults("RESULTDETAILS", "Storing the result as CSV", line, "Passed", line, 10L,
                        "", "RESULTDETAILS");
            } catch (IOException ioe) {
                System.err.println("IOException: " + ioe.getMessage());
                return 0;
            }
            return 0;
        } catch (Exception e) {
            if (!this.browser.isAllowedException(e))
                this.mgr.logException(e);
        }
        return 1;
    }

}// end
