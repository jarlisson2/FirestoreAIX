package com.jdl.Firestore;

import android.app.Activity;
import android.content.Context;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.annotation.WorkerThread;
import android.util.Log;

import com.google.android.gms.common.internal.MetadataValueReader;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.gms.tasks.Tasks;
import com.google.firebase.FirebaseApp;
import com.google.firebase.FirebaseOptions;
import com.google.firebase.firestore.CollectionReference;
import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldPath;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.FirebaseFirestoreSettings;
import com.google.firebase.firestore.ListenerRegistration;
import com.google.firebase.firestore.Query;

import org.json.JSONArray;
import org.json.JSONException;

import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.google.firebase.firestore.SetOptions;
import com.google.firebase.firestore.WriteBatch;

import com.jdl.Firestore.query.QueryHelper;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.Callable;
import java.util.concurrent.Executor;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.annotations.UsesPermissions;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.util.YailDictionary;
import com.google.appinventor.components.runtime.util.YailList;
import com.google.gson.Gson;
import com.google.gson.reflect.TypeToken;

@DesignerComponent(version = 1, description = "Firebase Firestore <br> Developed by Jarlisson", category = ComponentCategory.EXTENSION, nonVisible = true, iconName = "aiwebres/firestore.png")
@UsesLibraries(libraries = "firestore.jar, gson-2.1.jar")
@UsesPermissions(permissionNames = "android.permission.INTERNET, android.permission.ACCESS_NETWORK_STATE")
@SimpleObject(external = true)

public class Firestore extends AndroidNonvisibleComponent {
    public Activity activity;
    public Context context;
    private static final String TAG = "FirestoreJDL";
    private FirebaseFirestore db;
    private Map<String, ListenerRegistration> listeners = new HashMap<>();
    private static final ThreadPoolExecutor EXECUTOR = new ThreadPoolExecutor(2, 4, 60, TimeUnit.SECONDS,
            new LinkedBlockingQueue<Runnable>());

    public Firestore(final ComponentContainer container) {
        super(container.$form());
        context = container.$context();
        MetadataValueReader.setValuesForTesting("0", 12451000);

    }

    @SimpleFunction(description = "Initializes the SDK.")
    public void InitializeFirebase(String projectId, String apiKey, String applicationId, boolean persistent) {
        if (FirebaseApp.getApps(context).isEmpty()) {
            FirebaseOptions options = new FirebaseOptions.Builder().setProjectId(projectId).setApiKey(apiKey)
                    .setApplicationId(applicationId).build();
            Log.d(TAG, context.getPackageName().toString());
            FirebaseApp secondApp = FirebaseApp.initializeApp(context, options, "second app");
            db = FirebaseFirestore.getInstance(secondApp);
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(persistent).build();
            db.setFirestoreSettings(settings);
        } else {
            FirebaseApp secondApp = FirebaseApp.getInstance("second app");
            db = FirebaseFirestore.getInstance(secondApp);
            FirebaseFirestoreSettings settings = new FirebaseFirestoreSettings.Builder()
                    .setPersistenceEnabled(persistent).build();
            db.setFirestoreSettings(settings);
        }
    }

    @SimpleFunction(description = "Gets the Document by returning a Dictionary in \"GotDocument\".")
    public void GetDocument(String documentReference) {
        DocumentReference docRef = db.document(documentReference);
        docRef.get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                if (task.isSuccessful()) {
                    DocumentSnapshot document = task.getResult();
                    GotDocument(YailDictionary.makeDictionary((Map) document.getData()));
                    if (document.exists()) {
                        OnSuccess("DocumentSnapshot data: " + document.getData());
                        Log.d(TAG, "DocumentSnapshot data: " + document.getData());
                    } else {
                        OnSuccess("No such document");
                        Log.d(TAG, "No such document");
                    }
                } else {
                    OnFailure("get failed with " + task.getException().getMessage());
                    Log.d(TAG, "get failed with ", task.getException());
                }
            }
        });
    }

    @SimpleFunction(description = "Get a list of documents according to the query, returning it in \"GotQuery\".")
    public void GetDocumentQuery(String collectionReference, YailList query) {
        try {
            Query queryColection = db.collection(collectionReference);
            queryColection = QueryHelper.processQueries(new JSONArray(query.toJSONString()), queryColection);
            queryColection.get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
                @Override
                public void onComplete(@NonNull Task<QuerySnapshot> task) {
                    if (task.isSuccessful()) {
                        List<YailDictionary> docs = new ArrayList<>();
                        for (QueryDocumentSnapshot document : task.getResult()) {
                            OnSuccess(document.getId() + " => " + document.getData());
                            Log.d(TAG, document.getId() + " => " + document.getData());
                            docs.add(YailDictionary.makeDictionary((Map) document.getData()));
                        }
                        GotQuery(YailList.makeList(docs));
                    } else {
                        Log.d(TAG, "Error getting documents query: ", task.getException());
                    }
                }
            });
        } catch (JSONException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
    }

    @SimpleFunction(description = "Set the documents.")
    public void SetDocument(String documentReference, YailDictionary document) {
        DocumentReference docRef = db.document(documentReference);
        docRef.set(toMap(document)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                OnSuccess("DocumentSnapshot successfully written!");
                Log.d(TAG, "DocumentSnapshot successfully written!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OnFailure("Error writing document " + e.getMessage());
                Log.w(TAG, "Error writing document", e);
            }
        });
    }

    @SimpleFunction(description = "Update one field, creating the document if it does not already exist.")
    public void SetFieldWithMerge(String documentReference, YailDictionary document) {
        DocumentReference docRef = db.document(documentReference);
        docRef.set(toMap(document), SetOptions.merge()).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                OnSuccess("DocumentSnapshot successfully set field!");
                Log.d(TAG, "DocumentSnapshot successfully set field!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OnFailure("Error set field document " + e.getMessage());
                Log.w(TAG, "Error set field document", e);
            }
        });
    }

    @SimpleFunction(description = "Add a new document with a generated id.")
    public void AddDocument(String collectionReference, YailDictionary document) {
        CollectionReference docRef = db.collection(collectionReference);
        docRef.add(toMap(document)).addOnSuccessListener(new OnSuccessListener<DocumentReference>() {
            @Override
            public void onSuccess(DocumentReference documentReference) {
                OnSuccess("DocumentSnapshot written with ID: " + documentReference.getId());
                Log.d(TAG, "DocumentSnapshot written with ID: " + documentReference.getId());
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OnFailure("Error adding document " + e.getMessage());
                Log.w(TAG, "Error adding document", e);
            }
        });

    }

    @SimpleFunction(description = "Create a new empty document with a specific name, if you want to create it by generating an id leave the documentName field empty.")
    public void NewDocument(String collectionReference, String documentName) {
        Map<String, Object> data = new HashMap<>();
        DocumentReference docRef;
        if (documentName == "")
            docRef = db.collection(collectionReference).document();
        else
            docRef = db.collection(collectionReference).document(documentName);
        docRef.set(data).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                OnSuccess("DocumentSnapshot successfully new!");
                Log.d(TAG, "DocumentSnapshot successfully new!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OnFailure("Error new document " + e.getMessage());
                Log.w(TAG, "Error new document", e);
            }
        });
    }

    @SimpleFunction(description = "Updates the document.")
    public void UpdateDocument(String documentReference, YailDictionary document) {
        DocumentReference docRef = db.document(documentReference);
        docRef.update(toMap(document)).addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                OnSuccess("DocumentSnapshot successfully updated!");
                Log.d(TAG, "DocumentSnapshot successfully updated!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OnFailure("Error updating document " + e.getMessage());
                Log.w(TAG, "Error updating document", e);
            }
        });
    }

    @SimpleFunction(description = "Gets the complete list of documents in the collection, returning to \"GotAllDocs.\"")
    public void GetAllDocs(String collectionReference) {
        db.collection(collectionReference).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                if (task.isSuccessful()) {
                    List<YailDictionary> docs = new ArrayList<>();
                    for (QueryDocumentSnapshot document : task.getResult()) {
                        OnSuccess(document.getId() + " => " + document.getData());
                        Log.d(TAG, document.getId() + " => " + document.getData());
                        docs.add(YailDictionary.makeDictionary((Map) document.getData()));
                    }
                    GotAllDocs(YailList.makeList(docs));
                } else {
                    OnFailure("Error getting documents: " + task.getException().getMessage());
                    Log.d(TAG, "Error getting documents: ", task.getException());
                }
            }
        });
    }

    @SimpleFunction(description = "Register a listener and if there are changes to the collection it will return to OnListenerAdded, OnListenerModified, OnListenerRemoved.")
    public void RegisterListener(String collectionReference) {
        Query queryColection = db.collection(collectionReference);
        ListenerRegistration registration = queryColection.addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(@Nullable QuerySnapshot snapshots, @Nullable FirebaseFirestoreException e) {
                if (e != null) {
                    Log.w(TAG, "listen:error", e);
                    OnFailure("listen:error: " + e.getMessage());
                    return;
                }

                for (DocumentChange dc : snapshots.getDocumentChanges()) {
                    switch (dc.getType()) {
                        case ADDED:
                            OnListenerAdded(YailDictionary.makeDictionary((Map)dc.getDocument().getData()));
                            break;
                        case MODIFIED:
                            OnListenerModified(YailDictionary.makeDictionary((Map)dc.getDocument().getData()));
                            break;
                        case REMOVED:
                            OnListenerRemoved(YailDictionary.makeDictionary((Map)dc.getDocument().getData()));
                            break;
                    }
                }

            }
        });
        listeners.put(collectionReference, registration);
    }

    @SimpleFunction(description = "Removes the listener.")
    public void RemoveListener(String collectionReference) {
        ListenerRegistration list = listeners.get(collectionReference);
        list.remove();
    }

    @SimpleFunction(description = "Deleted collection.")
    public void DeleteCollection(String collectionReference) {
        deleteCollection(collectionReference);
    }

    @SimpleFunction(description = "Deleted document.")
    public void DeleteDocument(String documentReference) {
        DocumentReference docRef = db.document(documentReference);
        docRef.delete().addOnSuccessListener(new OnSuccessListener<Void>() {
            @Override
            public void onSuccess(Void aVoid) {
                OnSuccess("DocumentSnapshot successfully deleted!");
                Log.d(TAG, "DocumentSnapshot successfully deleted!");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                OnFailure("Error deleting document " + e.getMessage());
                Log.w(TAG, "Error deleting document", e);
            }
        });
    }

    @SimpleEvent(description = "Returns the document in the dictionary type obtained from \"GotValue\".")
    public void GotDocument(YailDictionary document) {
        EventDispatcher.dispatchEvent(this, "GotDocument", document);
    }

    @SimpleEvent(description = "Event returns all documents.")
    public void GotAllDocs(YailList documents) {
        EventDispatcher.dispatchEvent(this, "GotAllDocs", documents);
    }

    @SimpleEvent(description = "Event returns a list with the selected data.")
    public void GotQuery(YailList documents) {
        EventDispatcher.dispatchEvent(this, "GotQuery", documents);
    }

    @SimpleEvent(description = "Returns the event if any method is successful.")
    public void OnSuccess(String message) {
        EventDispatcher.dispatchEvent(this, "OnSuccess", message);
    }

    @SimpleEvent(description = "Returns the error message.")
    public void OnFailure(String message) {
        EventDispatcher.dispatchEvent(this, "OnFailure", message);
    }

    @SimpleEvent(description = "When registering the listener, this event will be called when adding a field to the collection.")
    public void OnListenerAdded(YailDictionary document) {
        EventDispatcher.dispatchEvent(this, "OnListenerAdded", document);
    }

    @SimpleEvent(description = "When registering the listener, this event will be called when modifying a field in the collection.")
    public void OnListenerModified(YailDictionary document) {
        EventDispatcher.dispatchEvent(this, "OnListenerModified", document);
    }

    @SimpleEvent(description = "When registering the listener, this event will be called when removing a field from the collection.")
    public void OnListenerRemoved(YailDictionary document) {
        EventDispatcher.dispatchEvent(this, "OnListenerRemoved", document);
    }

    private Map<String, Object> toMap(YailDictionary dictionary) {
        return new Gson().fromJson(dictionary.toString(), new TypeToken<HashMap<String, Object>>() {
        }.getType());
    }

    private void deleteCollection(final String path) {
        deleteCollection(db.collection(path), 50, EXECUTOR);
    }

    private Task<Void> deleteCollection(final CollectionReference collection, final int batchSize, Executor executor) {
        return Tasks.call(executor, new Callable<Void>() {
            @Override
            public Void call() throws Exception {
                Query query = collection.orderBy(FieldPath.documentId()).limit(batchSize);
                List<DocumentSnapshot> deleted = deleteQueryBatch(query);
                while (deleted.size() >= batchSize) {
                    DocumentSnapshot last = deleted.get(deleted.size() - 1);
                    query = collection.orderBy(FieldPath.documentId()).startAfter(last.getId()).limit(batchSize);

                    deleted = deleteQueryBatch(query);
                }
                return null;
            }
        });

    }

    @WorkerThread
    private List<DocumentSnapshot> deleteQueryBatch(final Query query) throws Exception {
        QuerySnapshot querySnapshot = Tasks.await(query.get());

        WriteBatch batch = query.getFirestore().batch();
        for (QueryDocumentSnapshot snapshot : querySnapshot) {
            batch.delete(snapshot.getReference());
        }
        Tasks.await(batch.commit());

        return querySnapshot.getDocuments();
    }

}
