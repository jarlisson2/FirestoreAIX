package com.jdl.Firestore.query;

import io.google.firebase.firestore.Query;

public interface QueryHandler {
    Query handle(Query query, Object value);
}
