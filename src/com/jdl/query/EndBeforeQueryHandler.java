package com.jdl.firestore.query;

import com.google.firebase.firestore.Query;
public class EndBeforeQueryHandler implements QueryHandler {
    @Override
    public Query handle(Query query, Object endBefore) {

        if (JSONDateWrapper.isWrappedDate(endBefore)) {
            endBefore = JSONDateWrapper.unwrapDate(endBefore);
        } else if (JSONTimestampWrapper.isWrappedTimestamp(endBefore)) {
            endBefore = JSONTimestampWrapper.unwrapTimestamp(endBefore);
        } else if (JSONGeopointWrapper.isWrappedGeoPoint(endBefore)) {
            endBefore = JSONGeopointWrapper.unwrapGeoPoint(endBefore);
        }

        return query.endBefore(endBefore);
    }
}
