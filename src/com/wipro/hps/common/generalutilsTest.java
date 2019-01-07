package com.wipro.hps.common;

import com.ibm.db2.jcc.am.SqlException;
import org.junit.Test;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

public class generalutilsTest {

    @Test
    public  void testthis() {

        String sql = "select\n" +
                " D2_M2.CASE_NUM, 'DTLCASE'               as POLICY_TYPE,\n" +
                "  \n" +
                "  substr('--JanFebMarAprMayJunJulAugSepOctNovDec', 3 * month(D2_M2.BILL_PERIOD), 3) || '-' ||\n" +
                "  year(D2_M2.BILL_PERIOD) as BILL_PER,\n" +
                "  CHAR(D2_M2.CYCLE_END_DT,USA) as DED_CYCLE,\n" +
                "  D2_M2.BILLED,\n" +
                "  E2.GENL_ADJ,\n" +
                "  ''                      as BILL_ADJ,\n" +
                "  G2.CVRG_PREM,\n" +
                "  ''                      as DIFF_BILL_COVER,\n" +
                "  I2.WO_ADJ,\n" +
                "  J2.FEES,\n" +
                "  K2.INT_ADJ,\n" +
                "  ''                      as BILL_TOT,\n" +
                "  D2_M2.PAID,\n" +
                "  ''                      as PAY_DUE\n" +
                "from (\n" +
                "       SELECT\n" +
                "         CDST.CASE_NUM,\n" +
                "         CDST.CYCLE_BEG_DT,\n" +
                "         CDST.CYCLE_END_DT,\n" +
                "         CDST.BILL_PERIOD,\n" +
                "         SUM(CDST.BILLED_AMOUNT)    AS BILLED,\n" +
                "         SUM(CDST.COLLECTED_AMOUNT) AS PAID\n" +
                "       FROM QUA.CASH_DISTRIBUTION CDST\n" +
                "       WHERE CDST.CASE_NUM = 'T695M2'\n" +
                "             AND CDST.PREM_FEE_IND = 'P'\n" +
                "             AND NOT CDST.PREM_FEE_TYPE IN ('SB', 'CR')\n" +
                "       GROUP BY CDST.CASE_NUM\n" +
                "         , CDST.CYCLE_BEG_DT\n" +
                "         , CDST.CYCLE_END_DT\n" +
                "         , CDST.BILL_PERIOD\n" +
                "     ) D2_M2\n" +
                "\n" +
                "  left outer join\n" +
                "  (SELECT\n" +
                "     CDST.CASE_NUM,\n" +
                "     CDST.CYCLE_BEG_DT,\n" +
                "     CDST.CYCLE_END_DT,\n" +
                "     CDST.BILL_PERIOD,\n" +
                "     COALESCE(SUM(PYCS.CASH_AMOUNT), 0)\n" +
                "       AS GENL_ADJ\n" +
                "   FROM QUA.CASH_DISTRIBUTION CDST\n" +
                "     INNER JOIN QUA.PYMTCASH PYCS\n" +
                "       ON (PYCS.CASE_NUMBER = CDST.CASE_NUM\n" +
                "           AND PYCS.BATCH_TYPE = '2'\n" +
                "           AND PYCS.PERIOD_APPLIED = CDST.CYCLE_BEG_DT\n" +
                "           AND NOT PYCS.BYPASS_INDICATOR = '1'\n" +
                "           AND NOT PYCS.ADJUSTMENT_CODE IN\n" +
                "                   ('(3', '3(', '2R', 'R2', '2S', 'S2'\n" +
                "                     , '(4', '4(', '2O', 'O2'\n" +
                "                     , '(7', '7(', '(5', '5('\n" +
                "                     , '2(', '(2'\n" +
                "                     , ')3', '3)', '(8', '8(')\n" +
                "       )\n" +
                "   WHERE CDST.CASE_NUM = 'T695M2'\n" +
                "         AND CDST.PREM_FEE_IND = 'P'\n" +
                "         AND NOT CDST.PREM_FEE_TYPE IN ('SB', 'CR')\n" +
                "         AND NOT CDST.ADJUSTMENT_AMOUNT = 0\n" +
                "   GROUP BY CDST.CASE_NUM\n" +
                "     , CDST.CYCLE_BEG_DT\n" +
                "     , CDST.CYCLE_END_DT\n" +
                "     , CDST.BILL_PERIOD\n" +
                "  )\n" +
                "  E2 on D2_M2.CASE_NUM = E2.CASE_NUM and D2_M2.BILL_PERIOD = E2.BILL_PERIOD\n" +
                "        and D2_M2.CYCLE_END_DT = E2.CYCLE_END_DT\n" +
                "\n" +
                "  left outer join\n" +
                "  (SELECT DISTINCT\n" +
                "     CDST.CASE_NUM,\n" +
                "     CDST.CYCLE_BEG_DT,\n" +
                "     CDST.CYCLE_END_DT,\n" +
                "     CDST.BILL_PERIOD,\n" +
                "     SUM(DEC(CH.COV_VALUE, 9, 2)) AS CVRG_PREM\n" +
                "   FROM QUA.CASH_DISTRIBUTION CDST, QUA.COVERAGE_HISTORY CH\n" +
                "   WHERE CDST.CASE_NUM = 'T695M2'/*'N793V4'*/\n" +
                "         AND CDST.PREM_FEE_IND = 'P'\n" +
                "         AND NOT CDST.PREM_FEE_TYPE IN ('SB', 'CR')\n" +
                "         AND CH.CASE_NUM = CDST.CASE_NUM\n" +
                "         AND CH.EMP_NUM = 1\n" +
                "         AND CH.DEPENDENT_NUM = 0\n" +
                "         AND CH.STATUS IN ('C', 'H')\n" +
                "         AND CH.EFFECTIVE_DATE <= CDST.CYCLE_BEG_DT\n" +
                "         AND (CH.TERM_DATE IS NULL\n" +
                "              OR CH.TERM_DATE >= CDST.CYCLE_END_DT)\n" +
                "         AND CH.COV_QUALIFIER = 'PREMIUM'\n" +
                "         AND NOT CH.COV_ENTITY LIKE 'SUB%'\n" +
                "   GROUP BY CDST.CASE_NUM\n" +
                "     , CDST.CYCLE_BEG_DT\n" +
                "     , CDST.CYCLE_END_DT\n" +
                "     , CDST.BILL_PERIOD) G2\n" +
                "    on D2_M2.CASE_NUM = G2.CASE_NUM and D2_M2.BILL_PERIOD = G2.BILL_PERIOD\n" +
                "       and D2_M2.CYCLE_END_DT = G2.CYCLE_END_DT\n" +
                "\n" +
                "  left outer join (SELECT\n" +
                "                     CDST.CASE_NUM,\n" +
                "                     CDST.CYCLE_BEG_DT,\n" +
                "                     CDST.CYCLE_END_DT,\n" +
                "                     CDST.BILL_PERIOD,\n" +
                "                     COALESCE(SUM(PYCS.CASH_AMOUNT), 0)\n" +
                "                       AS WO_ADJ\n" +
                "                   FROM QUA.CASH_DISTRIBUTION CDST\n" +
                "                     INNER JOIN QUA.PYMTCASH PYCS\n" +
                "                       ON (PYCS.CASE_NUMBER = CDST.CASE_NUM\n" +
                "                           AND PYCS.BATCH_TYPE = '2'\n" +
                "                           AND PYCS.PERIOD_APPLIED = CDST.CYCLE_BEG_DT\n" +
                "                           AND NOT PYCS.BYPASS_INDICATOR = '1'\n" +
                "                           AND PYCS.ADJUSTMENT_CODE IN\n" +
                "                               ('2(', '(2')\n" +
                "                       )\n" +
                "                   WHERE CDST.CASE_NUM = 'T695M2'\n" +
                "                         AND CDST.PREM_FEE_IND = 'P'\n" +
                "                         AND NOT CDST.PREM_FEE_TYPE IN ('SB', 'CR')\n" +
                "                         AND NOT CDST.ADJUSTMENT_AMOUNT = 0\n" +
                "                   GROUP BY CDST.CASE_NUM\n" +
                "                     , CDST.CYCLE_BEG_DT\n" +
                "                     , CDST.CYCLE_END_DT\n" +
                "                     , CDST.BILL_PERIOD) I2\n" +
                "\n" +
                "    on D2_M2.CASE_NUM = I2.CASE_NUM and D2_M2.BILL_PERIOD = I2.BILL_PERIOD\n" +
                "       and D2_M2.CYCLE_END_DT = I2.CYCLE_END_DT\n" +
                "\n" +
                "  left outer join (SELECT\n" +
                "                     CDST.CASE_NUM,\n" +
                "                     CDST.CYCLE_BEG_DT,\n" +
                "                     CDST.CYCLE_END_DT,\n" +
                "                     CDST.BILL_PERIOD,\n" +
                "                     SUM(CDST.BILLED_AMOUNT) +\n" +
                "                     SUM(CDST.ADJUSTMENT_AMOUNT) AS FEES,\n" +
                "                     SUM(CDST.COLLECTED_AMOUNT)  AS PAID\n" +
                "                   FROM QUA.CASH_DISTRIBUTION CDST\n" +
                "                   WHERE CDST.CASE_NUM = 'T695M2'\n" +
                "                         AND CDST.PREM_FEE_IND = 'F'\n" +
                "                         AND NOT CDST.PREM_FEE_TYPE IN ('SB', 'CR')\n" +
                "                   GROUP BY CDST.CASE_NUM\n" +
                "                     , CDST.CYCLE_BEG_DT\n" +
                "                     , CDST.CYCLE_END_DT\n" +
                "                     , CDST.BILL_PERIOD) J2\n" +
                "\n" +
                "    on D2_M2.CASE_NUM = J2.CASE_NUM and D2_M2.BILL_PERIOD = J2.BILL_PERIOD\n" +
                "       and D2_M2.CYCLE_END_DT = J2.CYCLE_END_DT\n" +
                "\n" +
                "  left outer join (SELECT\n" +
                "                     CDST.CASE_NUM,\n" +
                "                     CDST.CYCLE_BEG_DT,\n" +
                "                     CDST.CYCLE_END_DT,\n" +
                "                     CDST.BILL_PERIOD,\n" +
                "                     COALESCE(SUM(PYCS.CASH_AMOUNT), 0)\n" +
                "                       AS INT_ADJ\n" +
                "                   FROM QUA.CASH_DISTRIBUTION CDST\n" +
                "                     INNER JOIN QUA.PYMTCASH PYCS\n" +
                "                       ON (PYCS.CASE_NUMBER = CDST.CASE_NUM\n" +
                "                           AND PYCS.BATCH_TYPE = '2'\n" +
                "                           AND PYCS.PERIOD_APPLIED = CDST.CYCLE_BEG_DT\n" +
                "                           AND NOT PYCS.BYPASS_INDICATOR = '1'\n" +
                "                           AND PYCS.ADJUSTMENT_CODE IN\n" +
                "                               ('3)', ')3')\n" +
                "                       )\n" +
                "                   WHERE CDST.CASE_NUM = 'T695M2'\n" +
                "                         AND CDST.PREM_FEE_IND = 'P'\n" +
                "                         AND NOT CDST.PREM_FEE_TYPE IN ('SB', 'CR')\n" +
                "                         AND NOT CDST.ADJUSTMENT_AMOUNT = 0\n" +
                "                   GROUP BY CDST.CASE_NUM\n" +
                "                     , CDST.CYCLE_BEG_DT\n" +
                "                     , CDST.CYCLE_END_DT\n" +
                "                     , CDST.BILL_PERIOD) K2\n" +
                "    on D2_M2.CASE_NUM = K2.CASE_NUM and D2_M2.BILL_PERIOD = K2.BILL_PERIOD\n" +
                "       and D2_M2.CYCLE_END_DT = K2.CYCLE_END_DT\n" +
                "\n" +
                "\n" +
                "order by 1, D2_M2.CYCLE_END_DT desc\n" +
                " for fetch only;";
        List<List<String>> resultArray = new ArrayList<>();
        try {

            String DB_Url = "jdbc:db2://PSI4MVS.HEALTHPLAN.COM:818/DB2J:retrieveMessagesFromServerOnGetMessages=true;emulateParameterMetaDataForZCalls=1;";
            String userName = "vkavula";
            String passWord = "test4545";
            String dbClass = "com.ibm.db2.jcc.DB2Driver";
            Class.forName(dbClass).newInstance();
            Connection con = DriverManager.getConnection(DB_Url, userName, passWord);


//            System.out.println("Query:" + sql);
            Statement stmt = con.createStatement();
            ResultSet res;
            if (stmt.execute(sql)) {
                res = stmt.getResultSet();
                System.out.println("Query Executed");


                int loopi = 0;

                while (res.next()) {
                    loopi += 1;
                    if (loopi > 10) {
                        break;
                    }
                    List<String> temp = new ArrayList<>();
                    for (int i = 1; i < res.getMetaData().getColumnCount(); i++) {
                        temp.add(res.getString(i));
                    }
                    resultArray.add(temp);
                }
            }else {
                System.out.println("Query failed");
            }
            if (resultArray.size() < 0) {

            } else {
                System.out.println(resultArray.size());
                System.out.println(resultArray.toString());
            }

        } catch (SqlException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();

        }

    }


    @Test
    public void testdateformat(){
        SimpleDateFormat sdfmt1 = new SimpleDateFormat("MMM-yyyy");
        SimpleDateFormat sdfmt2= new SimpleDateFormat("dd-MMM-yyyy");
        java.util.Date dDate = null;
        try {
            dDate = sdfmt1.parse( "Feb-2018" );
        } catch (ParseException e) {
            e.printStackTrace();
        }
        System.out.println(dDate.toString());
    }

    @Test
    public void testtemp(){
       System.out.println("Showing 1 to 10 of 50 entries".replace("entries","").substring("Showing 1 to 10 of 50 entries".indexOf("of")+2).trim());
    }
}