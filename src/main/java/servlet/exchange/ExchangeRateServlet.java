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
import util.Validator;

import java.io.IOException;
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
                gson.toJson(new ErrorResponse("The currency code is missing in the address"), resp.getWriter());
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
                gson.toJson(new ErrorResponse("Currency not found"), resp.getWriter());
                return;
            }
            ExchangeRateDto currency = maybeExchangeRate.get();
            gson.toJson(currency, resp.getWriter());
        } catch (DaoException e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(new ErrorResponse("Something happened with the database"), resp.getWriter());
        }
    }
}
