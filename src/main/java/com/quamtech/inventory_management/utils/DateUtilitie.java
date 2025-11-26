package com.quamtech.inventory_management.utils;

import java.time.LocalDate;

public class DateUtilitie {

    public static LocalDate getDayDate(){
        var year = LocalDate.now().getYear();
        var month = LocalDate.now().getMonth();
        var day = LocalDate.now().getDayOfMonth();
        return LocalDate.of(year,month,day);
    }

    public static LocalDate parseStringToDate(String dateFormat){
        var  year = Integer.parseInt(dateFormat.split("-")[0]);
        var  month = Integer.parseInt(dateFormat.split("-")[1]);
        var  day = Integer.parseInt(dateFormat.split("-")[2]);
        //la date doit toujours etre bien formater
        return LocalDate.of(year,month,day);
    }

    public static boolean validateDate(String date){
        if(date.split("-").length==3){
            if(date.split("-")[0].length()==4){
                if(date.split("-")[1].length()==2|date.split("-")[1].length()==2){
                    if(Integer.parseInt(date.split("-")[1])<=13|Integer.parseInt(date.split("-")[2])<=32){
                        return true;
                    }else {
                        return false;
                    }
                }else{
                    return false;
                }
            }else {
                return false;
            }
        }else {
            return false;
        }
    }

    public static LocalDate handleDate(String date) throws Exception {
        if(!validateDate(date)){
            throw new Exception("Invalid date format. the god format is : yyyy-mm-dd");
        }
        return parseStringToDate(date);
    }
}

