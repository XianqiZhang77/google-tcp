package com.concordia.entity;

import com.concordia.utils.DateUtils;

import java.text.ParseException;
import java.util.Date;

public class TestSuite {

    public static TestSuite parseTestSuite(String data) throws ParseException {

        String[] fields = data.split(",");
        String directory = fields[0];
        Integer requestNumber = Integer.valueOf(fields[1]);
        String stage = fields[2];
        String status = fields[3];
        Date launchTime = DateUtils.dateFormatter(fields[4]);
        Double executionTime = Double.valueOf(fields[5]);
        String size = fields[6];
        Integer shard_number = Integer.valueOf(fields[7]);
        Integer run_number = Integer.valueOf(fields[8]);
        String language = fields[9];


        return new TestSuite(directory, requestNumber, stage, status, launchTime, executionTime, size, shard_number, run_number, language);
    }

    private String directory;

    private Integer requestNumber;

    private String stage;

    private String status;

    private Date launchTime;

    private Double executionTime;

    private String size;

    private Integer shard_number;

    private Integer run_number;

    private String language;

    private Integer Priority;

    public Integer getPriority() {
        return Priority;
    }

    public void setPriority(Integer priority) {
        Priority = priority;
    }

    public TestSuite() {
    }

    public TestSuite(String directory, Integer requestNumber, String stage, String status, Date launchTime, Double executionTime, String size, Integer shard_number, Integer run_number, String language) {
        this.directory = directory;
        this.requestNumber = requestNumber;
        this.stage = stage;
        this.status = status;
        this.launchTime = launchTime;
        this.executionTime = executionTime;
        this.size = size;
        this.shard_number = shard_number;
        this.run_number = run_number;
        this.language = language;
    }

    public String getDirectory() {
        return directory;
    }

    public void setDirectory(String directory) {
        this.directory = directory;
    }

    public Integer getRequestNumber() {
        return requestNumber;
    }

    public void setRequestNumber(Integer requestNumber) {
        this.requestNumber = requestNumber;
    }

    public String getStage() {
        return stage;
    }

    public void setStage(String stage) {
        this.stage = stage;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public Date getLaunchTime() {
        return launchTime;
    }

    public void setLaunchTime(Date launchTime) {
        this.launchTime = launchTime;
    }

    public Double getExecutionTime() {
        return executionTime;
    }

    public void setExecutionTime(Double executionTime) {
        this.executionTime = executionTime;
    }

    public String getSize() {
        return size;
    }

    public void setSize(String size) {
        this.size = size;
    }

    public Integer getShard_number() {
        return shard_number;
    }

    public void setShard_number(Integer shard_number) {
        this.shard_number = shard_number;
    }

    public Integer getRun_number() {
        return run_number;
    }

    public void setRun_number(Integer run_number) {
        this.run_number = run_number;
    }

    public String getLanguage() {
        return language;
    }

    public void setLanguage(String language) {
        this.language = language;
    }

    @Override
    public String toString() {
        return directory + "," +requestNumber + "," + stage + "," + status + "," + DateUtils.dateFormatter(launchTime) + "," + executionTime + "," + size + "," + shard_number + "," + run_number + "," + language;
    }
}
