package com.example.myapplication;

import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Example local unit test, which will execute on the development machine (host).
 *
 * @see <a href="http://d.android.com/tools/testing">Testing documentation</a>
 */
public class ExampleUnitTest {
    @Test
    public void addition_isCorrect() {
        printBin(0.625);
    }

    public String printBin(double num) {
        StringBuilder builder=new StringBuilder();
        builder.append("0.");
        while(builder.length()<32&&num!=0){
            double temp=num*2.0;
            builder.append(String.valueOf((int)(temp/1.0)));
            num=temp%1;
        }
        if(builder.length()>=32||num!=0){
            return "ERROR";
        }
        return builder.toString();
    }
}