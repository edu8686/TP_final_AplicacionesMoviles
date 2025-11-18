package com.example.tp_final_v1;

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

public class ApiHelper {

    // Declaración métoddo usable por cualquier clase, sin necesidad de crear un objeto. Recibe
    // como parámetro la url de tipo String

    //CAMBIOS CANDE
    public static String getApiData(String urlString) throws Exception {
        java.net.URL url = new java.net.URL(urlString);
        java.net.HttpURLConnection connection = (java.net.HttpURLConnection) url.openConnection();
        connection.setRequestMethod("GET");
        connection.connect();

        java.io.BufferedReader reader = new java.io.BufferedReader(new java.io.InputStreamReader(connection.getInputStream()));
        StringBuilder result = new StringBuilder();
        String line;
        while ((line = reader.readLine()) != null) {
            result.append(line);
        }
        reader.close();
        connection.disconnect();
        return result.toString();
    }






//    cambios edu

//    public static String getApiData(String urlString) throws Exception {
//
//        // Convierte la url en objeto URL que java puede operar
//        URL url = new URL(urlString);
//        // Se abre la conexión con al url
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        // Le indica al servidor que queremos obtener datos
//        connection.setRequestMethod("GET");
//
//        // Lectura de la respuesta
//        BufferedReader reader = new BufferedReader(
//                // Recibe el flujo de datos que envía el servidor
//                new InputStreamReader(connection.getInputStream())
//        );
//
//        // StringBuilder para almacenar respuesta completa
//        StringBuilder result = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            result.append(line);
//        }
//        reader.close();
//
//        return result.toString();
//    }
}
























//package com.example.tp_final_v1;


//
//import java.io.BufferedReader;
//import java.io.InputStreamReader;
//import java.net.HttpURLConnection;
//import java.net.URL;
//
//public class ApiHelper {
//
//    // Declaración métoddo usable por cualquier clase, sin necesidad de crear un objeto. Recibe
//    // como parámetro la url de tipo String
//    public static String getApiData(String urlString) throws Exception {
//
//        // Convierte la url en objeto URL que java puede operar
//        URL url = new URL(urlString);
//        // Se abre la conexión con al url
//        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
//        // Le indica al servidor que queremos obtener datos
//        connection.setRequestMethod("GET");
//
//        // Lectura de la respuesta
//        BufferedReader reader = new BufferedReader(
//                // Recibe el flujo de datos que envía el servidor
//                new InputStreamReader(connection.getInputStream())
//        );
//
//        // StringBuilder para almacenar respuesta completa
//        StringBuilder result = new StringBuilder();
//        String line;
//        while ((line = reader.readLine()) != null) {
//            result.append(line);
//        }
//        reader.close();
//
//        return result.toString();
//    }
//}
