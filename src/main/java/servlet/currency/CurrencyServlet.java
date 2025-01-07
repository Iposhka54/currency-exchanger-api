package servlet.currency;

import com.google.gson.Gson;
import exception.DaoException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ErrorResponse;
import model.dto.CurrencyDto;
import service.CurrencyService;

import java.io.IOException;
import java.util.Optional;

@WebServlet("/currency/*")
public class CurrencyServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private static final ErrorResponse ERROR = new ErrorResponse("Валюта не найдена");
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String code = req.getPathInfo().replaceFirst("/", "").toUpperCase();
        Gson gson = new Gson();
            try {
                if(code.isEmpty()){
                    resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                    gson.toJson(ERROR, resp.getWriter());
                    return;
                }
                Optional<CurrencyDto> maybeCurrency = currencyService.findByCode(code);
                if(maybeCurrency.isEmpty()){
                    resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                    gson.toJson(ERROR, resp.getWriter());
                    return;
                }
                CurrencyDto currency = maybeCurrency.get();
                gson.toJson(currency, resp.getWriter());
            } catch (DaoException e) {
                resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                gson.toJson(ERROR, resp.getWriter());
            }
    }

    private static boolean isCodeValid(String code) {
        if(code.length() == 3){
            for (char c : code.toCharArray()) {
                if(Character.UnicodeBlock.of(c) == Character.UnicodeBlock.BASIC_LATIN
                && Character.isLetter(c)){
                    return true;
                }
            }
        }
        return false;
    }
}
