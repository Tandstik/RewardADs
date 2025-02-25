package it.nathanub.rewardads.Bungee.Tools.Logs;

import it.nathanub.rewardads.Bungee.Tools.Api.Api;

import java.util.Base64;

public class Error {
    public static void send(String code, Exception e) {
        String error = "TYPE: MINECRAFT\nCLASS: " + e.getCause() + "\nERROR: " + e.getMessage() + "\nCAUSE: " + e.getCause();
        String encoded = Base64.getEncoder().encodeToString(error.getBytes());
        Api.handle("senderror/" + code + "/" + encoded);
    }
}
