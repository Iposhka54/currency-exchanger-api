package servlet.exchange;

import com.google.gson.Gson;
import exception.DaoException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ErrorResponse;
import model.dto.CurrencyDto;
import model.dto.CurrencyPairCodesDto;
import model.dto.ExchangeRateDto;
import service.ExchangeRateService;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

@WebServlet("/exchangeRate/*")
public class ExchangeRateServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ErrorResponse databaseError = new ErrorResponse("Something happened with the database");
    private final Integer START_INDEX_FIRST_CURRENCY = 0;
    private final Integer START_INDEX_SECOND_CURRENCY = 3;
    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String exchangeRateParam = req.getPathInfo().replaceFirst("/", "").toUpperCase();

        Gson gson = new Gson();

        try {
            if(exchangeRateParam.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(new ErrorResponse("The currencies code is missing in the address"), resp.getWriter());
                return;
            }

            if(exchangeRateParam.length() != 6){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(new ErrorResponse("The currencies code is incorrect"), resp.getWriter());
                return;
            }

            String baseCurrency = exchangeRateParam.substring(START_INDEX_FIRST_CURRENCY, START_INDEX_SECOND_CURRENCY);
            String targetCurrency = exchangeRateParam.substring(START_INDEX_SECOND_CURRENCY);

            CurrencyDto base = CurrencyDto.builder()
                    .code(baseCurrency)
                    .build();

            CurrencyDto target = CurrencyDto.builder()
                    .code(targetCurrency)
                    .build();

            CurrencyPairCodesDto find = CurrencyPairCodesDto.builder()
                    .base(base)
                    .target(target)
                    .build();

            Optional<ExchangeRateDto> maybeExchangeRate = exchangeRateService.findByCodes(find);
            if(maybeExchangeRate.isEmpty()){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                gson.toJson(new ErrorResponse("Exchange rate not found"), resp.getWriter());
                return;
            }
            ExchangeRateDto result = maybeExchangeRate.get();
            gson.toJson(result, resp.getWriter());
        } catch (DaoException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(new ErrorResponse("Something happened with the database"), resp.getWriter());
        }
    }

    @Override
    protected void doPatch(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String exchangeRateParam = req.getPathInfo().replaceFirst("/", "").toUpperCase();
        BigDecimal rate = null;
        String rateStr = getRate(req.getReader().readLine());
        Gson gson = new Gson();

        try{
            rate = BigDecimal.valueOf(Double.parseDouble(rateStr));
        }catch(NumberFormatException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("Bad parameter rate"), resp.getWriter());
        }

        try {
            if(exchangeRateParam.isBlank()) {
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(new ErrorResponse("The currencies code is missing in the address"), resp.getWriter());
                return;
            }

            if(exchangeRateParam.length() != 6){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(new ErrorResponse("The currencies code is incorrect"), resp.getWriter());
                return;
            }

            String baseCurrency = exchangeRateParam.substring(START_INDEX_FIRST_CURRENCY, START_INDEX_SECOND_CURRENCY);
            String targetCurrency = exchangeRateParam.substring(START_INDEX_SECOND_CURRENCY);

            CurrencyDto base = CurrencyDto.builder()
                    .code(baseCurrency)
                    .build();

            CurrencyDto target = CurrencyDto.builder()
                    .code(targetCurrency)
                    .build();

            ExchangeRateDto exchangeUpdate = ExchangeRateDto.builder()
                    .rate(rate)
                    .targetCurrency(target)
                    .baseCurrency(base)
                    .build();

            Optional<ExchangeRateDto> maybeExchangeRate = exchangeRateService.update(exchangeUpdate);
            if(maybeExchangeRate.isEmpty()){
                resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
                gson.toJson(new ErrorResponse("Exchange rate not found"), resp.getWriter());
                return;
            }
            ExchangeRateDto result = maybeExchangeRate.get();
            gson.toJson(result, resp.getWriter());

        } catch (DaoException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(new ErrorResponse("Something happened with the database"), resp.getWriter());
        }catch (NoSuchElementException e){
            resp.setStatus(HttpServletResponse.SC_NOT_FOUND);
            gson.toJson(new ErrorResponse("Currency not found"), resp.getWriter());
        }
    }

    private static String getRate(String params) {

        if (params == null || params.isEmpty()) {
            throw new IllegalArgumentException("Request body is empty or null.");
        }

        for (String param : params.split("&")) {
            String[] keyValue = param.split("=");
            if (keyValue.length == 2 && "rate".equals(keyValue[0])) {
                return keyValue[1];
            }
        }
        throw new IllegalArgumentException("Rate parameter not found.");
    }
}
