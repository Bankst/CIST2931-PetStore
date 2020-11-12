package com.cist2931.petstore.application.merchandise;

import com.cist2931.petstore.StringUtils;
import com.cist2931.petstore.application.AuthenticationService;
import com.cist2931.petstore.application.customer.Customer;
import com.cist2931.petstore.application.customer.CustomerController;
import com.cist2931.petstore.logging.Logger;
import io.javalin.http.Context;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;

import java.sql.Connection;

public class MerchandiseController {

    private static final Logger logger = new Logger(MerchandiseController.class);

    private final MerchandiseService merchandiseService;

    public MerchandiseController(Connection dbConnection){merchandiseService = new MerchandiseService(dbConnection);}

    public void doCreate(Context ctx) {
        String merchIDRaw = ctx.queryParam("merchID");
        String merchName = ctx.queryParam("merchName");
        String priceRaw = ctx.queryParam("price");
        String category = ctx.queryParam("category");
        String description = ctx.queryParam("description");
        String quantityRaw = ctx.queryParam("quantity");

        if (!StringUtils.hasValue(merchIDRaw, merchName, priceRaw, category, description, quantityRaw)) {
            ctx.status(HttpStatus.BAD_REQUEST_400);
            return;
        }

        //noinspection ConstantConditions
        int merchID = Integer.parseInt(merchIDRaw);
        double price = Double.parseDouble(priceRaw);
        int quantity = Integer.parseInt(quantityRaw);

        Merchandise merchandise = new Merchandise(merchID, merchName, price, category, description, quantity);

        int respCode = merchandiseService.create(merchandise);

        ctx.status(respCode);
    }

    public void getMerchandise(Context ctx) {
        String merchIDRaw = ctx.queryParam("merchID");

        int merchID = Integer.parseInt(merchIDRaw);

        Pair<Integer, Merchandise> getResponse = merchandiseService.getByID(merchID);

        ctx.json(getResponse.getRight());
        ctx.status(getResponse.getLeft());
    }

    public void doUpdateInfo(Context ctx) {
        String merchIDRaw = ctx.queryParam("merchID");
        String merchName = ctx.queryParam("merchName");
        String priceRaw = ctx.queryParam("price");
        String category = ctx.queryParam("category");
        String description = ctx.queryParam("description");
        String quantityRaw = ctx.queryParam("quantity");

        int merchID = Integer.parseInt(merchIDRaw);
        double price = Double.parseDouble(priceRaw);
        int quantity = Integer.parseInt(quantityRaw);

       int respCode = merchandiseService.updateInfo(merchID, merchName, price, category, description, quantity);

       ctx.status(respCode);
    }
}
