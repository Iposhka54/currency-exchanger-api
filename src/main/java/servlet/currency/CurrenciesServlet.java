package servlet.currency;

import com.google.gson.Gson;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ErrorResponse;
import model.dto.CurrencyDto;
import service.CurrencyService;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    CurrencyService currencyService = CurrencyService.getInstance();
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        try{
            List<CurrencyDto> currencies = currencyService.findAll();
            gson.toJson(currencies, resp.getWriter());
        }catch(Exception e){
            resp.setStatus(500);
            gson.toJson(new ErrorResponse("База данных недоступна!"), resp.getWriter());
        }
    }
}