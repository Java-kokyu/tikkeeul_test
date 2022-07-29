package com.hanghae0705.sbmoney.exception;

public class Constants {
    public enum ExceptionClass {

        ITEM("상품"), CATEGORY("카테고리"), SAVED_ITEM("티끌"), GOAL_ITEM("태산");
        private String exceptionClass;

        ExceptionClass(String exceptionClass) {
            this.exceptionClass = exceptionClass;
        }

        public String getExceptionClass() {
            return exceptionClass;
        }

        @Override
        public String toString() {
            return getExceptionClass() + " 오류 발생. ";
        }

    }
}
