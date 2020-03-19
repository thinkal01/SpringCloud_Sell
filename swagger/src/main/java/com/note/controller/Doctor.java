package com.note.controller;

import io.swagger.annotations.ApiModel;
import io.swagger.annotations.ApiModelProperty;
import lombok.Data;

import java.util.List;

@ApiModel(value = "医生对象模型")
@Data
public class Doctor {

    @ApiModelProperty(value = "id", required = true)
    private Integer id;

    @ApiModelProperty(value = "医生姓名", required = true)
    private String name;

    @ApiModelProperty(value = "病人列表", required = true)
    private List<Patient> patientList;

    @Override
    public String toString() {
        return "Doctor [id=" + id + ", name=" + name + "]";
    }

    @ApiModel
    @Data
    private class Patient {
        @ApiModelProperty(value = "病人姓名", required = true, example = "张三")
        private String name;
    }
}
