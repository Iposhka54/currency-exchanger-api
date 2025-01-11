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
import model.dto.ExchangeResponseDto;
import service.ExchangeRateService;
import util.Validator;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.NoSuchElementException;
import java.util.Optional;

@WebServlet("/exchange")
public class ExchangeServlet extends HttpServlet {
    private final ExchangeRateService exchangeRateService = ExchangeRateService.getInstance();
    private final ErrorResponse databaseError = new ErrorResponse("Something happened with the database");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        try{
            String baseCode = req.getParameter("from").toUpperCase();
            String targetCode = req.getParameter("to").toUpperCase();
            BigDecimal amount = BigDecimal.valueOf(Double.parseDouble(req.getParameter("amount")));
            if(!Validator.isValidCurrencyCode(baseCode) || !Validator.isValidCurrencyCode(targetCode)){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(new ErrorResponse("Missing any currency"), resp.getWriter());
                return;
            }
            CurrencyDto baseDto = CurrencyDto.builder()
                    .code(baseCode)
                    .build();
            CurrencyDto targetDto = CurrencyDto.builder()
                    .code(targetCode)
                    .build();
            CurrencyPairCodesDto pairCodes = CurrencyPairCodesDto.builder()
                    .base(baseDto)
                    .target(targetDto)
                    .amount(amount)
                    .build();

            ExchangeResponseDto exchange = exchangeRateService.exchange(pairCodes).orElseThrow();

            gson.toJson(exchange, resp.getWriter());
        }catch (NumberFormatException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("Missing parameter amount"), resp.getWriter());
        }
        catch (NoSuchElementException e){
            resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            gson.toJson(new ErrorResponse("No such exchange rate"), resp.getWriter());
        }
        catch (DaoException e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(databaseError, resp.getWriter());
        }catch (Exception e) {
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(new ErrorResponse("An unexpected error occurred"), resp.getWriter());
        }
    }
}
