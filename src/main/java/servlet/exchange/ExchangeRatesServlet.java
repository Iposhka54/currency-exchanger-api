package servlet.exchange;

import com.google.gson.Gson;
import exception.DaoException;
import exception.ExchangeRateAlreadyExistsException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ErrorResponse;
import model.dto.CreateExchangeRateDto;
import model.dto.ExchangeRateDto;
import service.CurrencyService;
import service.ExchangeRateService;
import util.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.util.List;

@WebServlet("/exchangeRates")
public class ExchangeRatesServlet extends HttpServlet {
    private final ExchangeRateService exchangeService = ExchangeRateService.getInstance();
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ErrorResponse databaseError = new ErrorResponse("Something happened with the database");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        try{
            List<ExchangeRateDto> exchangeRates = exchangeService.findAll();
            gson.toJson(exchangeRates, resp.getWriter());
        }catch(Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(databaseError, resp.getWriter());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String base = req.getParameter("baseCurrencyCode");
        String target = req.getParameter("targetCurrencyCode");
        String rateStr = req.getParameter("rate");


        Gson gson = new Gson();

        if(rateStr == null || rateStr.isBlank()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("Missing parameter name"), resp.getWriter());
            return;

        }
        else if(base == null || base.isBlank()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("Missing parameter baseCurrencyCode"), resp.getWriter());
            return;
        }
        else if(target == null || target.isBlank()){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("Missing parameter targetCurrencyCode"), resp.getWriter());
            return;
        }

        if(!Validator.isValidCurrencyCode(base)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("Invalid baseCurrencyCode"), resp.getWriter());
            return;
        }

        if(!Validator.isValidCurrencyCode(target)){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("Invalid targetCurrencyCode"), resp.getWriter());
            return;
        }

        BigDecimal rate = null;
        try{
            rate = BigDecimal.valueOf(Double.parseDouble(rateStr)).setScale(6, RoundingMode.HALF_UP);
        }catch(NumberFormatException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("Invalid rate"), resp.getWriter());
        }

        try{
            CreateExchangeRateDto createExchangeRate = CreateExchangeRateDto.of(
                    currencyService.findByCode(base).orElseThrow(),
                    currencyService.findByCode(target).orElseThrow(),
                    rate
            );
            ExchangeRateDto save = exchangeService.save(createExchangeRate);
            gson.toJson(save, resp.getWriter());
        }catch (DaoException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(databaseError, resp.getWriter());
        }catch (ExchangeRateAlreadyExistsException e){
            resp.setStatus(HttpServletResponse.SC_CONFLICT);
            gson.toJson(new ErrorResponse("Exchange rate already exists"), resp.getWriter());
        }

    }
}
