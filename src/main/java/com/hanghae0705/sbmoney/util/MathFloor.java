package com.hanghae0705.sbmoney.util;

public class MathFloor {
    public static double PercentTenths(double decimal){
        return (Math.round(decimal * 10000 ) / 10000.0) * 100;
    }
}