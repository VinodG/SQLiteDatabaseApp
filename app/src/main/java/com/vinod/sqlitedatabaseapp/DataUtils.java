package com.vinod.sqlitedatabaseapp;

import java.util.Vector;

public class DataUtils {
    private static int recordNumber;
    private static boolean isEnable;
    private static int VEC_SIZE =10;

    public static Vector<StudentDO> getStudentDO() {
        Vector<StudentDO> vec = new Vector<StudentDO>();
        for (int i = 0;i<VEC_SIZE;i++) {
            StudentDO e = getDummyStudentObject(i);
            vec.add(e);
        }
        return vec;
    }

    public static Vector<EmployeeDO> getEmployeeDO() {
        Vector<EmployeeDO> vec = new Vector<EmployeeDO>();
        for (int i = 0;i<VEC_SIZE;i++) {
            EmployeeDO e = getDummyEmpObject(i);
            vec.add(e);
        }
        return vec;
    }

    public static Vector<ComplexDO>  getComplexDO() {
        Vector<ComplexDO> vecC = new Vector<ComplexDO>();
        for (int i = 0;i<VEC_SIZE;i++) {
            ComplexDO c =getDummyComplexDOObject(i);
            vecC.add(c);
        }
        return   vecC;
    }

    public static EmployeeDO getDummyEmpObject(int assignNumber) {
        int addLabel ;
        if(assignNumber<1) {
            addLabel = assignNumber;
        }else
        {
            addLabel = recordNumber;
            recordNumber++;
        }
        EmployeeDO e = new EmployeeDO();
        e.id = addLabel;
        e.name = "name" + addLabel;
        e.rollno = addLabel;
        e.salary = addLabel;
        e.isEnable = addLabel % 2 == 0 ? false : true;
        e.column1 = "column "+assignNumber;
        e.column2 = "column "+assignNumber;
        e.column3 = "column "+assignNumber;
        e.column4 = "column "+assignNumber;
        e.column5 = addLabel % 2 == 0 ? false : true;
        e.column6 =  addLabel;
        e.column7 = "column "+assignNumber;
        e.column8 = "column "+assignNumber;
        e.column9 = "column "+assignNumber;
        e.column10 = addLabel;
        return e;
    }
    private static StudentDO getDummyStudentObject(int assignNumber) {
        int addLabel ;
        if(assignNumber<1) {
            addLabel = assignNumber;
        }else
        {
            addLabel = recordNumber;
            recordNumber++;
        }
        StudentDO e = new StudentDO();
        e.id = addLabel;
        e.name = "name" + addLabel;
        e.rollno = addLabel;
        e.salary = addLabel;
        e.isEnable = addLabel % 2 == 0 ? false : true;
        return e;
    }
    private static ComplexDO getDummyComplexDOObject(int assignNumber) {
        int addLabel ;
        if(assignNumber<1) {
            addLabel = assignNumber;
        }else
        {
            addLabel = recordNumber;
        }
        ComplexDO e = new ComplexDO();
        e.employeeDO = getDummyEmpObject(addLabel);
//        e.studentDO = getDummyStudentObject(addLabel);
        return e;
    }
}
