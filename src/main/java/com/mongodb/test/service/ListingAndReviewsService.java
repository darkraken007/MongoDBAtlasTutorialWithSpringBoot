package com.mongodb.test.service;

import com.mongodb.client.model.Aggregates;
import com.mongodb.client.model.search.FieldSearchPath;
import com.mongodb.client.model.search.SearchOperator;
import com.mongodb.client.model.search.SearchOptions;
import com.mongodb.client.model.search.SearchPath;
import com.mongodb.test.model.ListingsAndReviews;
import com.mongodb.test.model.PagedData;
import com.mongodb.test.model.SearchData;
import com.mongodb.test.repository.ListingsAndReviewsCrudRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.aggregation.AddFieldsOperation;
import org.springframework.data.mongodb.core.aggregation.Aggregation;
import org.springframework.data.mongodb.core.aggregation.AggregationOperation;
import org.springframework.data.mongodb.core.aggregation.ArithmeticOperators;
import org.springframework.data.mongodb.core.aggregation.CountOperation;
import org.springframework.data.mongodb.core.aggregation.FacetOperation;
import org.springframework.data.mongodb.core.aggregation.LimitOperation;
import org.springframework.data.mongodb.core.aggregation.ProjectionOperation;
import org.springframework.data.mongodb.core.aggregation.SkipOperation;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
public class ListingAndReviewsService {

    @Autowired
    ListingsAndReviewsCrudRepository listingsAndReviewsCrudRepository;

    @Autowired
    MongoTemplate mongoTemplate;

    private static List<SearchPath> getSearchPaths(List<String> paths) {
        List<SearchPath> wildCardPaths =
                paths.stream().filter(path -> path.contains("*"))
                        .map(SearchPath::wildcardPath)
                        .collect(Collectors.toList());
        List<SearchPath> fieldPaths =
                paths.stream()
                        .filter(path -> !path.contains("*"))
                        .map(SearchPath::fieldPath)
                        .collect(Collectors.toList());
        fieldPaths.addAll(wildCardPaths);
        return fieldPaths;
    }

    public ListingsAndReviews getById(String id) {
        return listingsAndReviewsCrudRepository.findById(id).get();
    }

    public ListingsAndReviews getByIdUsingTemplate(String id) {
        return mongoTemplate.findById(id, ListingsAndReviews.class);
    }

    public List<ListingsAndReviews> getByPriceLessThanAndAccommodatesGreaterThan(float price, int accommodates) {
        return mongoTemplate.find(Query.query(Criteria.where("price").lt(price)).addCriteria(Criteria.where(
                        "accommodates").gt(accommodates)),
                ListingsAndReviews.class);
    }

    public List<ListingsAndReviews> filterByPriceAndAccommodatesAndSortByField(float price, int accommodates,
                                                                               String field) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        aggregationOperations.add(Aggregation.match(Criteria.where("price").lt(price)));
        aggregationOperations.add(Aggregation.match(Criteria.where("accommodates").gt(accommodates)));
        aggregationOperations.add(Aggregation.sort(Sort.Direction.ASC, field));
        return mongoTemplate.aggregate(Aggregation.newAggregation(ListingsAndReviews.class, aggregationOperations),
                ListingsAndReviews.class).getMappedResults();
        //aggregationOperations.add(Aggregates.match(Filters.eq("price",price)));
    }

    public List<ListingsAndReviews> filterByPriceAndAccommodatesAndSortByFieldWithOnlyRequiredFields(float price,
                                                                                                     int accommodates, String field) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        //matching
        aggregationOperations.add(Aggregation.match(Criteria.where("price").lt(price)));
        aggregationOperations.add(Aggregation.match(Criteria.where("accommodates").gt(accommodates)));


        //adding a new field
        aggregationOperations.add(Aggregation.addFields().addFieldWithValue("totalRooms",
                ArithmeticOperators.valueOf("$bedrooms").add("$bathrooms")
        ).build());

        //projecting only the required fields
        ProjectionOperation projectionOperation = Aggregation.project()
                .andInclude("name", "price", "accommodates", "summary", "space", "description", "amenities", "totalRooms",
                        "bedrooms", "bathrooms");

        aggregationOperations.add(projectionOperation);

        //sorting
        aggregationOperations.add(Aggregation.sort(Sort.Direction.ASC, field));

        return mongoTemplate.aggregate(Aggregation.newAggregation(ListingsAndReviews.class, aggregationOperations),
                ListingsAndReviews.class).getMappedResults();

    }

    public List<PagedData> filterByPriceAndAccommodatesAndSortByFieldWithOnlyRequiredFieldsWithPaging(float price,
                                                                                                      int accommodates, String field,
                                                                                                      int pageSize, int pageNumber) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        //matching
        aggregationOperations.add(Aggregation.match(Criteria.where("price").lt(price)));
        aggregationOperations.add(Aggregation.match(Criteria.where("accommodates").gt(accommodates)));


        //adding a new field
        aggregationOperations.add(Aggregation.addFields().addFieldWithValue("totalRooms",
                ArithmeticOperators.valueOf("$bedrooms").add("$bathrooms")
        ).build());

        //projecting only the required fields
        ProjectionOperation projectionOperation = Aggregation.project()
                .andInclude("name", "price", "accommodates", "summary", "space", "description", "amenities", "totalRooms",
                        "bedrooms", "bathrooms");

        aggregationOperations.add(projectionOperation);

        //sorting
        aggregationOperations.add(Aggregation.sort(Sort.Direction.ASC, field));

        //paging
        CountOperation countOperation = Aggregation.count().as("total");
        AddFieldsOperation addPageNumberFieldOperation = Aggregation.addFields()
                .addFieldWithValue("pageNumber", pageNumber)
                .build();
        AddFieldsOperation addPageSizeFieldOperation = Aggregation.addFields()
                .addFieldWithValue("pageSize", pageSize)
                .build();
        SkipOperation skipOperation =
                Aggregation.skip((long) pageNumber * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);
        FacetOperation facetOperation = Aggregation
                .facet(countOperation, addPageNumberFieldOperation, addPageSizeFieldOperation).as("metadata")
                .and(skipOperation, limitOperation).as("data");
        aggregationOperations.add(facetOperation);

        return mongoTemplate.aggregate(Aggregation.newAggregation(ListingsAndReviews.class, aggregationOperations),
                PagedData.class).getMappedResults();
    }

    public List<PagedData> getSearchResults(List<SearchData> searchDatas, int pageSize, int pageNumber) {

        List<SearchOperator> searchOps = new ArrayList<>();
        //search stage. can include multiple searches using compound search
        searchDatas.forEach(searchData -> {
            List<SearchPath> searchPaths = getSearchPaths(searchData.getPaths());
            SearchOperator fullTextSearchOp = SearchOperator.text(searchPaths,
                    searchData.getSearchQuery());
            searchOps.add(fullTextSearchOp);
        });


        List<AggregationOperation> aggregationOperations = new ArrayList<>();
        aggregationOperations
                .add(Aggregation.stage(Aggregates.search(SearchOperator.compound().must(searchOps),
                        SearchOptions.searchOptions().index("default"))));

        //paging
        CountOperation countOperation = Aggregation.count().as("total");
        AddFieldsOperation addPageNumberFieldOperation = Aggregation.addFields()
                .addFieldWithValue("pageNumber", pageNumber)
                .build();
        AddFieldsOperation addPageSizeFieldOperation = Aggregation.addFields()
                .addFieldWithValue("pageSize", pageSize)
                .build();
        SkipOperation skipOperation =
                Aggregation.skip((long) pageNumber * pageSize);
        LimitOperation limitOperation = Aggregation.limit(pageSize);
        FacetOperation facetOperation = Aggregation
                .facet(countOperation, addPageNumberFieldOperation, addPageSizeFieldOperation).as("metadata")
                .and(skipOperation, limitOperation).as("data");
        aggregationOperations.add(facetOperation);

        return mongoTemplate.aggregate(Aggregation.newAggregation(ListingsAndReviews.class, aggregationOperations),
                PagedData.class).getMappedResults();

    }


    public List<String> autoComplete(String query, String path) {
        List<AggregationOperation> aggregationOperations = new ArrayList<>();

        FieldSearchPath searchPath = SearchPath.fieldPath(path);
        SearchOperator autocomplete = SearchOperator.autocomplete(searchPath,
                query);
        aggregationOperations
                .add(Aggregation.stage(Aggregates.search(autocomplete,
                        SearchOptions.searchOptions().index("autocomplete"))));

        List<ListingsAndReviews> out =  mongoTemplate.aggregate(Aggregation.newAggregation(ListingsAndReviews.class,
                        aggregationOperations),
                ListingsAndReviews.class).getMappedResults();

        return out.stream().map(listingsAndReviews -> listingsAndReviews.getProperty_type()).distinct().collect(Collectors.toList());

    }

}
