//package br.com.rtools.utilitarios;
//
//import com.google.maps.GeoApiContext;
//import com.google.maps.GeocodingApi;
//import com.google.maps.model.GeocodingResult;
//
//public class GoogleMaps {
//
//    public static void main(String args[]) {
//        try {
//
//            // "https://maps.googleapis.com/maps/api/geocode/json?address=Rua%20Marques%20da%20Cruz,%20989%20-%20Ribeir%C3%A3o%20Preto,%20SP&key=AIzaSyAnMYctlIvbcbnFq801DauM02euEaJR9Aw";
//            GeoApiContext geoApiContext = new GeoApiContext();
//            geoApiContext.setApiKey("AIzaSyAnMYctlIvbcbnFq801DauM02euEaJR9Aw");
//            GeocodingResult[] results = GeocodingApi.geocode(geoApiContext, "R MARQUES DA CRUZ 989, RIBEIRÃO PRETO , SP").await();
//            if (results != null && results.length > 0) {
//                System.out.println(results[0].formattedAddress);
//            }
//        } catch (Exception e) {
//
//        }
//        try {
//        } catch (Exception e) {
//
//        }
//    }
//}
