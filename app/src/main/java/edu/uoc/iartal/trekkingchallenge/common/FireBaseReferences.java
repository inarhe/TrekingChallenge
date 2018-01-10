/* Copyright 2018 Ingrid Artal Hermoso

 Licensed under the Apache License, Version 2.0 (the "License");
 you may not use this file except in compliance with the License.
 You may obtain a copy of the License at

 http://www.apache.org/licenses/LICENSE-2.0

 Unless required by applicable law or agreed to in writing, software
 distributed under the License is distributed on an "AS IS" BASIS,
 WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 See the License for the specific language governing permissions and
 limitations under the License.
 */

package edu.uoc.iartal.trekkingchallenge.common;

/**
 * Firebase database references
 */
public class FireBaseReferences {

    // User references
    final public static String USER_REFERENCE = "user";
    final public static String USER_ID_REFERENCE = "id";
    final public static String USER_ALIAS_REFERENCE = "alias";
    final public static String USER_NAME_REFERENCE = "name";
    final public static String USER_MAIL_REFERENCE = "mail";
    final public static String USER_FINISHED_REFERENCE = "finished";
    final public static String USER_RESULT_REFERENCE = "challengeResults";
    final public static String USER_TRIPSDONE_REFERENCE = "tripsDone";
    final public static String USER_TRIPS_REFERENCE = "trips";
    final public static String USER_GROUPS_REFERENCE = "groups";
    final public static String USER_RATINGS_REFERENCE = "ratings";
    final public static String USER_CHALLENGES_REFERENCE = "challenges";
    final public static String USER_PASSWORD_REFERENCE = "password";

    // Group references
    final public static String GROUP_REFERENCE = "group";
    final public static String GROUP_NAME_REFERENCE = "name";
    final public static String GROUP_DESCRIPTION_REFERENCE = "description";

    // Route references
    final public static String ROUTE_REFERENCE = "route";
    final public static String ROUTE_FINISHED_REFERENCE = "finished";
    final public static String HEADERS_STORAGE = "headers/";
    final public static String TRACKS_STORAGE = "tracks/";
    final public static String PROFILES_STORAGE = "profiles/";
    final public static String ROUTE_RATINGS_REFERENCE = "ratings";
    final public static String ROUTE_SUM_RATINGS_REFERENCE = "sumRatings";
    final public static String ROUTE_NUM_RATINGS_REFERENCE = "numRatings";

    // Trip references
    final public static String TRIP_REFERENCE = "chTrip";
    final public static String TRIP_NAME_REFERENCE = "name";
    final public static String TRIP_DESCRIPTION_REFERENCE = "description";
    final public static String TRIP_DATE_REFERENCE = "date";
    final public static String TRIP_DONE_REFERENCE = "done";

    // Challenge references
    final public static String CHALLENGE_REFERENCE = "challenge";
    final public static String CHALLENGE_FINISHED_REFERENCE = "results";
    final public static String CHALLENGE_NAME_REFERENCE = "name";
    final public static String CHALLENGE_DESCRIPTION_REFERENCE = "description";
    final public static String CHALLENGE_DATE_REFERENCE = "limitDate";

    // Finished route references
    final public static String FINISHED_REFERENCE = "finished";

    // ChallengeResult references
    final public static String CHALLENGERESULT_REFERENCE = "challengeResult";
    final public static String CHALLENGERESULT_POSITION_REFERENCE = "position";

    // TripDone references
    final public static String TRIPSDONE_REFERENCE = "tripsDone";
    final public static String TRIPSDONE_TRIP_NAME = "tripName";

    // Rating references
    final public static String RATING_REFERENCE = "rating";

    // Message references
    final public static String MESSAGE_REFERENCE = "message";

    // History references
    final public static String HISTORY_REFERENCE = "history";
    final public static String HISTORY_ID_REFERENCE = "id";
    final public static String HISTORY_WINS_REFERENCE = "challengeWin";
    final public static String HISTORY_DISTANCE_REFERENCE = "totalDistance";
    final public static String HISTORY_SLOPE_REFERENCE = "totalSlope";
    final public static String HISTORY_HOUR_REFERENCE = "totalHour";
    final public static String HISTORY_MIN_REFERENCE = "totalMin";

    // Common references
    final public static String MEMBERS_REFERENCE = "members";
    final public static String NUMBER_OF_MEMBERS_REFERENCE = "numberOfMembers";
    final public static String MESSAGES_REFERENCE = "messages";
    final public static String USER_ADMIN_REFERENCE = "userAdmin";
    final public static String OBJECT_USER_REFERENCE = "user";
    final public static String NAME_REFERENCE = "name";
}
