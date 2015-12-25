package com.jiacw.d35ohweather.model;

/**
 * Created by Jiacw on 10:56 24/12/2015.
 * Email: 313133710@qq.com
 * Function:实体类
 */
public class Province {
    //3.定义变量
    private int id;
    private String provinceName;
    private String provinceCode;
    //4.get set->City
    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getProvinceName() {
        return provinceName;
    }

    public void setProvinceName(String provinceName) {
        this.provinceName = provinceName;
    }

    public String getProvinceCode() {
        return provinceCode;
    }

    public void setProvinceCode(String provinceCode) {
        this.provinceCode = provinceCode;
    }
}
