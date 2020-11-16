package com.jdl.firestore.query;

import com.google.firebase.firestore.Query;

public class StartAfterQueryHandler implements QueryHandler {
    @Override
    public Query handle(Query query, Object startAfter) {

        if (JSONDateWrapper.isWrappedDate(startAfter)) {
            startAfter = JSONDateWrapper.unwrapDate(startAfter);
        } else if (JSONTimestampWrapper.isWrappedTimestamp(startAfter)) {
            startAfter = JSONTimestampWrapper.unwrapTimestamp(startAfter);
        } else if (JSONGeopointWrapper.isWrappedGeoPoint(startAfter)) {
            startAfter = JSONGeopointWrapper.unwrapGeoPoint(startAfter);
        }

        return query.startAfter(startAfter);
    }
}
