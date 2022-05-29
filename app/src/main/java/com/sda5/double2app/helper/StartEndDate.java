package com.sda5.double2app.helper;

public class StartEndDate {
    private long startDate;
    private long endDate;
    private String start;
    private String end;

    public StartEndDate(long startDate, long endDate) {
        this.startDate = startDate;
        this.endDate = endDate;
    }

    public StartEndDate(long startDate, long endDate, String start, String end) {
        this.startDate = startDate;
        this.endDate = endDate;
        this.start = start;
        this.end = end;
    }

    public long getStartDate() {
        return startDate;
    }

    public void setStartDate(long startDate) {
        this.startDate = startDate;
    }

    public String getStart() {
        return start;
    }

    public void setStart(String start) {
        this.start = start;
    }


    public String getEnd() {
        return end;
    }

    public void setEnd(String end) {
        this.end = end;
    }

    public long getEndDate() {
        return endDate;
    }

    public void setEndDate(long endDate) {
        this.endDate = endDate;
    }
}
