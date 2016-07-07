package com.omdd.gdyb.bean;

import java.io.Serializable;

/**
 * Created by Administrator on 2016/6/15 0015.
 * desc:工程信息
 */
public class ProjectInfo implements Serializable {

    /*
    "planNo": "140048008",
    "projectName": "LG化学（广州）工程塑料有限公司研发车间工程",
    "schemeNo": "140048",
    "testProject": "抽芯"
    */

    public String planNo;
    public String projectName;
    public String schemeNo;
    public String testProject;

    public ProjectInfo(String planNo, String projectName, String schemeNo, String testProject){
        this.planNo = planNo;
        this.projectName = projectName;
        this.schemeNo = schemeNo;
        this.testProject = testProject;
    }

}
