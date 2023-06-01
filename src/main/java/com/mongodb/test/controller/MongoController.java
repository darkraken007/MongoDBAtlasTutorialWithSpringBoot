package com.mongodb.test.controller;

import com.mongodb.test.model.ListingsAndReviews;
import com.mongodb.test.model.PagedData;
import com.mongodb.test.model.SearchData;
import com.mongodb.test.service.ListingAndReviewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RestController
public class MongoController {

    @Autowired
    ListingAndReviewsService listingAndReviewsService;


    @GetMapping("/listingsAndReviews/{id}")
    public ListingsAndReviews getSomething(@PathVariable String id) {
        return listingAndReviewsService.getById(id);
    }

    @GetMapping("/listingsAndReviews")
    public List<ListingsAndReviews> getByPriceAndOccupancylimit(@RequestParam float price,
                                                                @RequestParam int accommodates) {
        return listingAndReviewsService.getByPriceLessThanAndAccommodatesGreaterThan(price, accommodates);
    }

    @GetMapping("/listingsAndReviewsMatchAndSort")
    public List<ListingsAndReviews> getByPriceAndOccupancylimitAndSort(@RequestParam float price,
                                                                       @RequestParam int accommodates,
                                                                       @RequestParam String sortField) {
        return listingAndReviewsService.filterByPriceAndAccommodatesAndSortByField(price, accommodates, sortField);
    }

    @GetMapping("/listingsAndReviewsMatchSortAndProject")
    public List<ListingsAndReviews> matchSortAndProject(@RequestParam float price, @RequestParam int accommodates,
                                                        @RequestParam String sortField) {
        return listingAndReviewsService.filterByPriceAndAccommodatesAndSortByFieldWithOnlyRequiredFields(price,
                accommodates,sortField);
    }

    @GetMapping("/listingsAndReviewsMatchSortAndProjectWithPaging")
    public List<PagedData> matchSortAndProjectWithPaging(@RequestParam float price,
                                                   @RequestParam int accommodates,
                                                   @RequestParam String sortField, @RequestParam int pageSize,
                                                   @RequestParam int pageNumber) {
        return listingAndReviewsService.filterByPriceAndAccommodatesAndSortByFieldWithOnlyRequiredFieldsWithPaging(price
                ,accommodates, sortField, pageSize, pageNumber);
    }

    @GetMapping("/searching")
    public List<PagedData> search(@RequestBody List<SearchData> searchDatas, @RequestParam int pageSize,
                                  @RequestParam int pageNumber) {
        return listingAndReviewsService.getSearchResults(searchDatas, pageSize,pageNumber);
    }


    @GetMapping("/autocomplete")
    public List<String> autoComplete(@RequestParam String query, @RequestParam String path) {
        return listingAndReviewsService.autoComplete(query,path);
    }


}
