package com.example.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Login extends AppCompatActivity {

    private Button loginButton;
    private EditText emailEditText, passwordEditText;
    private FirebaseAuth auth;

    private String verificationId;
    private PhoneAuthProvider.ForceResendingToken mResendToken;

    // Callbacks for phone verification
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // Initialize Firebase Auth
        auth = FirebaseAuth.getInstance();

        loginButton = findViewById(R.id.login1);
        TextView signupTextView = findViewById(R.id.signup);
        emailEditText = findViewById(R.id.email_or_phone);
        emailEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        passwordEditText = findViewById(R.id.password);

        // Initialize verification callbacks
        initVerificationCallbacks();

        // Handle login button click
        loginButton.setOnClickListener(v -> {
            String emailOrPhone = emailEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (isValidEmail(emailOrPhone)) {
                auth.signInWithEmailAndPassword(emailOrPhone, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign in success, navigate to MainActivity
                                startActivity(new Intent(Login.this, MainActivity.class));
                                finish();  // Close LoginActivity
                            } else {
                                // If sign in fails, display a message to the uster.
                                Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                startPhoneNumberVerification(emailOrPhone);
            }
        });

        signupTextView.setOnClickListener(v -> {
            startActivity(new Intent(Login.this, Signup.class));
        });
    }

    // Method to initialize phone verification callbacks
    private void initVerificationCallbacks() {
        mCallbacks = new PhoneAuthProvider.OnVerificationStateChangedCallbacks() {
            @Override
            public void onVerificationCompleted(@NonNull PhoneAuthCredential credential) {
                // Sign in with the credential
                signInWithPhoneAuthCredential(credential);
            }

            @Override
            public void onVerificationFailed(@NonNull FirebaseException e) {
                Toast.makeText(Login.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID for later use
                Login.this.verificationId = verificationId;
                mResendToken = token; // Save token if needed for resending
                promptUserForVerificationCode();
            }
        };
    }

    // Method to start phone number verification
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth)
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // (optional) Activity for callback binding
                        // If no activity is passed, reCAPTCHA verification can not be used.
                        .setCallbacks(mCallbacks)          // OnVerificationStateChangedCallbacks
                        .build();
        PhoneAuthProvider.verifyPhoneNumber(options);
    }

    // Method to prompt user for the verification code
    private void promptUserForVerificationCode() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Enter Verification Code");

        // Create an EditText for inputting the verification code
        final EditText input = new EditText(this);
        input.setInputType(InputType.TYPE_CLASS_NUMBER); // Set input type to number
        builder.setView(input);

        // Set positive button for the dialog
        builder.setPositiveButton("Verify", (dialog, which) -> {
            String code = input.getText().toString().trim();
            if (!TextUtils.isEmpty(code)) {
                // Verify the code entered by the user
                verifyCode(code);
            } else {
                Toast.makeText(this, "Please enter the verification code", Toast.LENGTH_SHORT).show();
            }
        });

        // Set negative button for the dialog
        builder.setNegativeButton("Cancel", (dialog, which) -> dialog.cancel());

        // Show the dialog
        builder.show();
    }

    // Method to verify the code entered by the user
    private void verifyCode(String code) {
        PhoneAuthCredential credential = PhoneAuthProvider.getCredential(verificationId, code);
        signInWithPhoneAuthCredential(credential);
    }

    // Method to sign in with the phone auth credential
    private void signInWithPhoneAuthCredential(PhoneAuthCredential credential) {
        auth.signInWithCredential(credential)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        // Sign in success, navigate to MainActivity
                        startActivity(new Intent(Login.this, MainActivity.class));
                        finish();  // Close LoginActivity
                    } else {
                        // If sign in fails, display a message to the user.
                        Toast.makeText(Login.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Utility method to check if the input is a valid email
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
}
