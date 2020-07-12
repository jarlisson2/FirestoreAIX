package com.jdl.Firestore.query;

import io.google.firebase.firestore.Query;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

public class OrderByQueryHandler implements QueryHandler {
    @Override
    public Query handle(Query query, Object orderByObject) {

        JSONObject order = (JSONObject) orderByObject;

        try {
            Query.Direction direction = Query.Direction.ASCENDING;

            if ("desc".equals(order.getString("direction"))) {
                direction = Query.Direction.DESCENDING;
            }

            query = query.orderBy(order.getString("field"), direction);

            FirestoreLog.d("FirestoreJDL", String.format("Order by %s (%s)", order.getString("field"), direction.toString()));

        } catch (JSONException e) {
            FirestoreLog.e("FirestoreJDL", "Error processing ordering", e);
        }

        return query;
    }
}
