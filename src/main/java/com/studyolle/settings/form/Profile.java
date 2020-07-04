package com.studyolle.settings.form;

import lombok.Data;
import org.hibernate.validator.constraints.Length;

@Data
public class Profile {

    @Length(max = 35)
    private String bio;

    @Length(max = 50)
    private String url;

    @Length(max = 50)
    private String occupation;

    @Length(max = 50)
    private String location;

    //TODO 이미지파일 크면 저장안됨 ㅠㅠ (에러페이지로감,,,)
    private String profileImage;

}