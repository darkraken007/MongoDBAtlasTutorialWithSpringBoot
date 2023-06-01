package com.mongodb.test.model;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.time.LocalDateTime;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Document(value = "listingsAndReviews")
@JsonInclude(JsonInclude.Include.NON_EMPTY)
public class ListingsAndReviews {

        @Id
        public String id;
        public String listingUrl;
        public String name;
        public String summary;
        public String space;
        public String description;
        public String neighborhoodOverview;
        public String totalRooms;
        public String notes;
        public String transit;
        public String access;
        public String interaction;
        public String house_rules;
        public String property_type;
        public String room_type;
        public String bed_type;
        public String minimum_nights;
        public String maximum_nights;
        public String cancellation_policy;
        public LocalDateTime lastScraped;
        public LocalDateTime calendarLastScraped;
        public LocalDateTime firstReview;
        public LocalDateTime lastReview;
        public int accommodates;
        public int bedrooms;
        public int beds;
        public int numberOfReviews;
        public float bathrooms;
        public List<String> amenities;
        public float price;
        public float securityDeposit;
        public float cleaningFee;
        public float extraPeople;
        public float guestsIncluded;
        public Images images;
        public Host host;
        public Address address;
        public Availability availability;
        public ReviewScores reviewScores;
        public List<Review> reviews;

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        static class Images {

                public String thumbnailUrl;
                public String mediumUrl;
                public String pictureUrl;
                public String xlPictureUrl;

        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        static class Host {

                public String hostId;
                public String hostUrl;
                public String hostName;
                public String hostLocation;
                public String hostAbout;
                public String hostResponseTime;
                public String hostThumbnailUrl;
                public String hostPictureUrl;
                public String hostNeighbourhood;
                public int hostResponseRate;
                public Boolean hostIsSuperhost;
                public Boolean hostHasProfilePic;
                public Boolean hostIdentityVerified;
                public int hostListingsCount;
                public int hostTotalListingsCount;
                public List<String> hostVerifications;

        }
        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        static class Address {

                public String street;
                public String suburb;
                public String governmentArea;
                public String market;
                public String country;
                public String countryCode;
                public Location location;

        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        static class Location {

                public String type;
                public List<Float> coordinates;
                public Boolean isLocationExact;

        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        static class Availability {

                public int availability30;
                public int availability60;
                public int availability90;
                public int availability365;

        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        static class ReviewScores {

                public int reviewScoresAccuracy;
                public int reviewScoresCleanliness;
                public int reviewScoresCheckin;
                public int reviewScoresCommunication;
                public int reviewScoresLocation;
                public int reviewScoresValue;
                public int reviewScoresRating;

        }

        @Getter
        @Setter
        @NoArgsConstructor
        @AllArgsConstructor
        @Builder
        static class Review {

                public String id;
                public LocalDateTime date;
                public String listingId;
                public String reviewerId;
                public String reviewerName;
                public String comments;

        }


}

