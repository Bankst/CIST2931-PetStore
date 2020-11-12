package com.cist2931.petstore.application.merchandise;

import com.cist2931.petstore.application.customer.Customer;
import com.cist2931.petstore.application.customer.CustomerSQL;
import com.cist2931.petstore.application.customer.CustomerService;
import com.cist2931.petstore.logging.Logger;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.Optional;

public class MerchandiseService {
    private static final Logger logger = new Logger(CustomerService.class);

    private final Connection conn;

    public MerchandiseService(Connection conn) {
        this.conn = conn;
    }

    public int create(Merchandise merchandise) {
        int responseCode;

        Optional<Merchandise> merchandiseOptional = MerchandiseSQL.getMerchandiseById(conn, merchandise.getMerchID());
        if (merchandiseOptional.isPresent()) {
            responseCode = HttpStatus.CONFLICT_409; // Merchandise with id already exists
        } else {
            try {
                if (merchandise.insert(conn)) {
                    responseCode = HttpStatus.OK_200;
                } else {
                    logger.error("Unknown failure to insert new merchandise(" + merchandise.getMerchID() + ")");
                    responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
                }
            } catch (SQLException ex) {
                logger.error("Failed to insert new merchandise(" + merchandise.getMerchID() + ")", ex);
                responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
        }

        return responseCode;
    }

    public Pair<Integer, Merchandise> getByID(int merchID) {
        int responseCode;
        Merchandise merchandise = null;

        Optional<Merchandise> merchandiseOptional = MerchandiseSQL.getMerchandiseById(conn, merchID);
        if (merchandiseOptional.isPresent()) {
            merchandise = merchandiseOptional.get();
            responseCode = HttpStatus.OK_200;
            logger.info("Handled info request for merchandise(" + merchandise.getMerchID() + ")");
        } else {
            responseCode = HttpStatus.FORBIDDEN_403;
        }

        return Pair.of(responseCode, merchandise);
    }

    public int updateInfo(int merchID, String merchName, double price, String category, String description, int quantity) {
        int responseCode;
        Optional<Merchandise> merchandiseOptional = MerchandiseSQL.getMerchandiseById(conn, merchID);
        if(merchandiseOptional.isPresent()) {
            Merchandise merchandise = merchandiseOptional.get();

            merchandise.setMerchName(merchName);
            merchandise.setPrice(price);
            merchandise.setCategory(category);
            merchandise.setDescription(description);
            merchandise.setQuantity(quantity);

            try {
                merchandise.update(conn);
                logger.info("Updated info for merchandise(" + merchandise.getMerchID() + ")");

                responseCode = HttpStatus.OK_200;
            } catch (SQLException ex) {
                logger.error("Failed to update merchandise(" + merchandise.getMerchID() + ") row for info!", ex);
                responseCode = HttpStatus.INTERNAL_SERVER_ERROR_500;
            }
        } else responseCode = HttpStatus.FORBIDDEN_403;
        return responseCode;
    }
}