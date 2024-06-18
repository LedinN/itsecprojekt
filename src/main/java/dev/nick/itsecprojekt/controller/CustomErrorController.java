package dev.nick.itsecprojekt.controller;

import jakarta.servlet.RequestDispatcher;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.boot.web.error.ErrorAttributeOptions;
import org.springframework.boot.web.servlet.error.ErrorAttributes;
import org.springframework.boot.web.servlet.error.ErrorController;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.context.request.WebRequest;


import java.util.Map;
/* En controller för errors. Implementeras från den inbyggda gränssnittet ErrorController */
@Controller
public class CustomErrorController implements ErrorController {

    /* instansering */
    private final ErrorAttributes errorAttributes;

    /* konstruktor */
    public CustomErrorController(ErrorAttributes errorAttributes) {
        this.errorAttributes = errorAttributes;
    }

   /* Hanterar felkoder genom att hämta statuskoden för felet och lägga till felattribut i modellen.
    Beroende på vilken felkod som har dykt upp returnerar den ett specifikt felmeddelande / felmeddelandesida.
*/

    @RequestMapping("/error")
    public String handleError(HttpServletRequest request, Model model) {
        Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
        if (status != null) {
            Integer statusCode = Integer.valueOf(status.toString());
            model.addAllAttributes(getErrorAttributes(request));
            if (statusCode == 401) {
                return "401";
            } else if (statusCode == 403) {
                return "403";
            } else if (statusCode == 404) {
                return "404";
            }
        }
        return "error";
    }

    /* Denna metod returnerar en map där nycklarna är strings och värdena är objekt.
    Metoden getErrorAttributes tar emot en parameter (request) av typen HttpServletRequest.
    errorAttributes.getErrorAttributes hämtar felattributen från  förfrågan och
    ErrorAttributeOptions.defaults() anger att standardalternativ ska användas för att hämta dessa attribut.
*/
    private Map<String, Object> getErrorAttributes(HttpServletRequest request) {
        return errorAttributes.getErrorAttributes((WebRequest) request, ErrorAttributeOptions.defaults());
    }
}
