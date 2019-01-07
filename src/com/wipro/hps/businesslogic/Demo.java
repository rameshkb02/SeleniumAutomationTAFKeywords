package com.wipro.hps.businesslogic;

import java.text.NumberFormat;

public class Demo {

    public static void main(String[] args) {
        // TODO Auto-generated method stub

        String val = "200";
        int foo = Integer.parseInt(val);
        NumberFormat fmt = NumberFormat.getCurrencyInstance();
        System.out.println(fmt.format(foo));

    }

}
