package com.cist2931.petstore.application.merchandise;

import com.cist2931.petstore.StringUtils;
import com.cist2931.petstore.application.customer.CustomerService;
import com.cist2931.petstore.logging.Logger;
import me.xdrop.fuzzywuzzy.FuzzySearch;
import me.xdrop.fuzzywuzzy.model.BoundExtractedResult;
import org.apache.commons.lang3.tuple.Pair;
import org.eclipse.jetty.http.HttpStatus;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class MerchandiseService {
    private static final Logger logger = new Logger(MerchandiseService.class);

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
            responseCode = HttpStatus.NOT_FOUND_404;
        }

        return Pair.of(responseCode, merchandise);
    }

    public Pair<Integer, List<Merchandise>> getAll() {
        int responseCode;
        List<Merchandise> merchList = null;

        Optional<List<Merchandise>> merchandiseListOptional = MerchandiseSQL.getAllMerchandise(conn);
        if (merchandiseListOptional.isPresent()) {
            merchList = merchandiseListOptional.get();
            responseCode = HttpStatus.OK_200;
        } else {
            responseCode = HttpStatus.NOT_FOUND_404;
        }
        return Pair.of(responseCode, merchList);
    }

    public Pair<Integer, List<Merchandise>> getCategory(String category) {
        int responseCode;
        List<Merchandise> merchList = null;

        switch (category) {
            case "cats":
                category = "Cat";
                break;
            case "dogs":
                category = "Dog";
                break;
            case "fish":
                category = "Fish";
                break;
            case "birds":
                category = "Bird";
                break;
        }

        Optional<List<Merchandise>> merchandiseListOptional = MerchandiseSQL.getMerchandiseByCategory(conn, category);
        if (merchandiseListOptional.isPresent()) {
            merchList = merchandiseListOptional.get();
            responseCode = HttpStatus.OK_200;
        } else {
            responseCode = HttpStatus.NOT_FOUND_404;
        }
        return Pair.of(responseCode, merchList);
    }

    public Pair<Integer, List<Merchandise>> getSearchMerch(String searchQuery) {
        int responseCode;
        List<Merchandise> merchList = new ArrayList<>();

        Pair<Integer, List<Merchandise>> allMerchResponse = getAll();

        if (StringUtils.hasValue(searchQuery)) {

            List<BoundExtractedResult<Merchandise>> searchResults = FuzzySearch.extractAll(searchQuery, allMerchResponse.getValue(), Merchandise::getMerchName, 90);

            for(BoundExtractedResult<Merchandise> merchResult : searchResults) {
                merchList.add(merchResult.getReferent());
            }

            responseCode = HttpStatus.OK_200;
        } else {
            responseCode = allMerchResponse.getLeft();
            merchList = allMerchResponse.getRight();
        }

        return Pair.of(responseCode, merchList);
    }
}
