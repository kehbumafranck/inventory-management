package com.quamtech.inventory_management.utils;

import java.math.BigInteger;

import org.springframework.stereotype.Component;
@Component
public class  CameroonianPhoneNumberValidator {
    private static final String CAMEROON_COUNTRY_CODE = "237";
    private static final String INVALID1_NUMBER = "23761";
    private static final String INVALID2_NUMBER = "23763";
    private static final String INVALID3_NUMBER = "23764";

    public static boolean isValidCameroonianPhoneNumber(String phoneNumber) {
        if (phoneNumber == null || phoneNumber.isEmpty()) {
            return false;
        }
        phoneNumber = phoneNumber.trim();
        // Vérifie si le numéro de téléphone commence par l'indicatif du Cameroun.
        if (!phoneNumber.startsWith(CAMEROON_COUNTRY_CODE)) {
            return false;
        }
        // Vérifie si le numéro de téléphone contient 13 chiffres.
        if (phoneNumber.length() != 12) {
            return false;
        }
        // Vérifie si le numero de téléphone est invalide.
        if (phoneNumber.startsWith(INVALID1_NUMBER)||phoneNumber.startsWith(INVALID2_NUMBER)||phoneNumber.startsWith(INVALID3_NUMBER)){
            System.out.println("invalid");
            System.out.println("valid");
        }
        try {
            new BigInteger(phoneNumber);
            return true;
        } catch (NumberFormatException nfe) {
            return false;
        }
    }

    public static void handlerNumber(String phoneNumber){
        if(isValidCameroonianPhoneNumber(phoneNumber)) {
            throw new RuntimeException("Le format de numerotation est invalide et doit avoir 12 chiffre: \"+ INVALID1_NUMBER+\", \"+INVALID2_NUMBER+\", \"+INVALID3_NUMBER");
        }
    }
}
