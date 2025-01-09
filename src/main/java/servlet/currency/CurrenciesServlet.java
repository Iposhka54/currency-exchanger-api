package servlet.currency;

import com.google.gson.Gson;
import exception.CurrencyAlreadyExistsException;
import exception.DaoException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ErrorResponse;
import model.dto.CreateCurrencyDto;
import model.dto.CurrencyDto;
import service.CurrencyService;
import util.Validator;

import java.io.IOException;
import java.util.List;

@WebServlet("/currencies")
public class CurrenciesServlet extends HttpServlet {
    private final CurrencyService currencyService = CurrencyService.getInstance();
    private final ErrorResponse databaseError = new ErrorResponse("Something happened with the database");

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Gson gson = new Gson();
        try{
            List<CurrencyDto> currencies = currencyService.findAll();
            gson.toJson(currencies, resp.getWriter());
        }catch(Exception e){
            resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
            gson.toJson(databaseError, resp.getWriter());
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
            String name = req.getParameter("name");
            String code = req.getParameter("code");
            String sign = req.getParameter("sign");

            Gson gson = new Gson();

            if(name == null || name.isBlank()){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(new ErrorResponse("Missing parameter name"), resp.getWriter());
                return;

            }
            else if(code == null || code.isBlank()){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(new ErrorResponse("Missing parameter code"), resp.getWriter());
                return;
            }
            else if(sign == null || sign.isBlank()){
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(new ErrorResponse("Missing parameter sign"), resp.getWriter());
                return;
            }

            CreateCurrencyDto currency = CreateCurrencyDto.builder()
                .name(name)
                .code(code.toUpperCase())
                .sign(sign.toUpperCase())
                .build();

            if(Validator.isValidCurrencyParams(currency)){
                try{
                    CurrencyDto save = currencyService.save(currency);
                    resp.setStatus(HttpServletResponse.SC_CREATED);
                    gson.toJson(save, resp.getWriter());
                }catch (CurrencyAlreadyExistsException e){
                    resp.setStatus(HttpServletResponse.SC_CONFLICT);
                    gson.toJson(new ErrorResponse("Currency already exists"), resp.getWriter());
                }
                catch(DaoException e){
                    resp.setStatus(HttpServletResponse.SC_INTERNAL_SERVER_ERROR);
                    gson.toJson(databaseError, resp.getWriter());
                }
            }
            else{
                resp.setStatus(HttpServletResponse.SC_BAD_REQUEST);
                gson.toJson(new ErrorResponse("Parameters are not correct"), resp.getWriter());
            }
    }
}