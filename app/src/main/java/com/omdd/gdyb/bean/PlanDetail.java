package com.omdd.gdyb.bean;

import java.io.Serializable;

/**
 * Created by hxhuang on 2016/6/15 0015.
 */
public class PlanDetail implements Serializable {

    public String planNo;
    public String schemeNo;
    public String projectName;
    public String planStartTime;
    public String planEndTime;
    public String devName;
    public String model;
    public String testProject;
    public String majorMan;
    public String majorPhone;
    public int pileNum;//桩数

    public PlanDetail(String planNo, String schemeNo, String projectName, String planStartTime, String planEndTime, String devName, String model, String testProject, String majorMan, String majorPhone, int pileNum) {
        this.planNo = planNo;
        this.schemeNo = schemeNo;
        this.projectName = projectName;
        this.planStartTime = planStartTime;
        this.planEndTime = planEndTime;
        this.devName = devName;
        this.model = model;
        this.testProject = testProject;
        this.majorMan = majorMan;
        this.majorPhone = majorPhone;
        this.pileNum = pileNum;
    }
}
