package com.jdl.Firestore;
/*
import com.google.appinventor.components.annotations.DesignerComponent;
import com.google.appinventor.components.annotations.SimpleEvent;
import com.google.appinventor.components.annotations.SimpleFunction;
import com.google.appinventor.components.annotations.SimpleObject;
import com.google.appinventor.components.annotations.UsesActivities;
import com.google.appinventor.components.annotations.UsesInfoMetaData;
import com.google.appinventor.components.annotations.UsesLibraries;
import com.google.appinventor.components.annotations.UsesPermissions;
import com.google.appinventor.components.annotations.UsesProvider;
import com.google.appinventor.components.annotations.UsesServices;
import com.google.appinventor.components.annotations.androidmanifest.ActivityElement;
import com.google.appinventor.components.annotations.androidmanifest.MetaDataElement;
import com.google.appinventor.components.annotations.androidmanifest.ProviderElement;
import com.google.appinventor.components.annotations.androidmanifest.ServiceElement;
import com.google.appinventor.components.common.ComponentCategory;
import com.google.appinventor.components.runtime.AndroidNonvisibleComponent;
import com.google.appinventor.components.runtime.ComponentContainer;
import com.google.appinventor.components.runtime.EventDispatcher;
import com.google.appinventor.components.runtime.ReplForm;
import com.google.appinventor.components.runtime.util.YailDictionary;

import android.app.Activity;
import android.content.Context;
import android.support.annotation.NonNull;
import android.util.Log;
import android.widget.Toast;

import io.google.android.gms.tasks.OnCompleteListener;
import io.google.android.gms.tasks.Task;
import io.google.firebase.FirebaseApp;
import io.google.firebase.FirebaseOptions;
import io.google.firebase.auth.AuthResult;
import io.google.firebase.auth.FirebaseAuth;

@DesignerComponent(version = 1, description = "Firebase Firestore <br> Developed by Jarlisson", category = ComponentCategory.EXTENSION, nonVisible = true, iconName = "aiwebres/firestore.png", helpUrl = "https://github.com/jarlisson2/PictureInPicture")
@UsesInfoMetaData(metaDataElements = { @MetaDataElement(name = "io.google.android.gms.version", value = "12451000") })

@ActivityElement(name = "io.google.firebase.auth.internal.FederatedSignInActivity", excludeFromRecents = "true", exported = "true", launchMode = "singleInstance", permission = "io.google.firebase.auth.api.gms.permission.LAUNCH_FEDERATED_SIGN_IN", theme = "@android:style/Theme.Translucent.NoTitleBar") })
@UsesServices(services = {        @ServiceElement(name = "io.google.firebase.components.ComponentDiscoveryService", metaDataElements = {                @MetaDataElement(name = "io.google.firebase.components:io.google.firebase.auth.FirebaseAuthRegistrar", value = "io.google.firebase.components.ComponentRegistrar") }) })
@UsesLibraries(libraries = "firestore.jar")
@UsesPermissions(permissionNames = "android.permission.INTERNET, android.permission.ACCESS_NETWORK_STATE")
@SimpleObject(external = true)

public class Auth extends AndroidNonvisibleComponent {
    public Context context;
    public Activity activity;
    private FirebaseAuth mAuth;
    private static final String TAG = "EmailPassword";

    public Auth(final ComponentContainer container) {
        super(container.$form());
        context = container.$context();
        activity = (Activity) context;
    }

    @SimpleFunction(description = ".")
    public void InitializeFirebase(String projectId, String apiKey, String applicationId, String databaseUrl,
            String storageBucket) {
        //FirebaseOptions options = new FirebaseOptions.Builder().setProjectId(projectId).setApiKey(apiKey)
        //        .setApplicationId(applicationId).setDatabaseUrl(databaseUrl).setStorageBucket(storageBucket).build();
        //Log.d(TAG, context.getPackageName().toString());
        //FirebaseApp secondApp = FirebaseApp.initializeApp(context, options, "second app");
        mAuth = FirebaseAuth.getInstance();
    }

    @SimpleFunction(description = ".")
    public void CreateAccount(String email, String password) {
        Log.d(TAG, "createAccount:" + email);

        mAuth.createUserWithEmailAndPassword(email, password).addOnCompleteListener(activity,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "createUserWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
        // [END create_user_with_email]
    }

    @SimpleFunction(description = ".")
    public void LoginWithEmail(String email, String password) {
        mAuth.signInWithEmailAndPassword(email, password).addOnCompleteListener(activity,
                new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());
                            Toast.makeText(context, "Authentication failed.", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
    }

    @SimpleFunction(description = ".")
    public void SignInWithCustomToken(String mCustomToken) {
        mAuth.signInWithCustomToken(mCustomToken)
        .addOnCompleteListener(activity, new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
                if (task.isSuccessful()) {
                    // Sign in success, update UI with the signed-in user's information
                    Log.d(TAG, "signInWithCustomToken:success");
                } else {
                    // If sign in fails, display a message to the user.
                    Log.w(TAG, "signInWithCustomToken:failure", task.getException());
                    Toast.makeText(context, "Authentication failed.",
                            Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

}*/