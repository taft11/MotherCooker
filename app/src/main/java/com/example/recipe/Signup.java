package com.example.recipe;

import android.content.Intent;
import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.google.firebase.FirebaseException;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.PhoneAuthCredential;
import com.google.firebase.auth.PhoneAuthOptions;
import com.google.firebase.auth.PhoneAuthProvider;

import java.util.concurrent.TimeUnit;

public class Signup extends AppCompatActivity {

    private EditText emailOrPhoneEditText;
    private EditText passwordEditText;
    private Button signupButton;
    private FirebaseAuth auth;

    private String verificationId;

    // Callbacks for phone verification
    private PhoneAuthProvider.OnVerificationStateChangedCallbacks mCallbacks;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_signup);

        auth = FirebaseAuth.getInstance();

        emailOrPhoneEditText = findViewById(R.id.email_or_phone);
        emailOrPhoneEditText.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_EMAIL_ADDRESS);
        passwordEditText = findViewById(R.id.password);
        signupButton = findViewById(R.id.signup);

        // Initialize verification callbacks
        initVerificationCallbacks();

        Toolbar toolbar = findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        signupButton.setOnClickListener(v -> {
            String emailOrPhone = emailOrPhoneEditText.getText().toString().trim();
            String password = passwordEditText.getText().toString().trim();

            if (TextUtils.isEmpty(emailOrPhone)) {
                Toast.makeText(Signup.this, "Email or Phone number cannot be empty", Toast.LENGTH_SHORT).show();
                return;
            }

            // Check if the input is an email or a phone number
            if (isValidEmail(emailOrPhone)) {
                // Sign up with email and password
                auth.createUserWithEmailAndPassword(emailOrPhone, password)
                        .addOnCompleteListener(this, task -> {
                            if (task.isSuccessful()) {
                                // Sign up success, navigate to MainActivity
                                startActivity(new Intent(Signup.this, MainActivity.class));
                                finish(); // Close SignupActivity
                            } else {
                                // If sign up fails, display a message to the user.
                                Toast.makeText(Signup.this, "Sign Up failed.", Toast.LENGTH_SHORT).show();
                            }
                        });
            } else {
                // Start phone number verification if the input is not a valid email
                startPhoneNumberVerification(emailOrPhone);
            }
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
                // Handle error
                Toast.makeText(Signup.this, "Verification failed: " + e.getMessage(), Toast.LENGTH_SHORT).show();
            }

            @Override
            public void onCodeSent(@NonNull String verificationId,
                                   @NonNull PhoneAuthProvider.ForceResendingToken token) {
                // Save verification ID for later use
                Signup.this.verificationId = verificationId;
                // Prompt user to enter the verification code
                promptUserForVerificationCode();
            }
        };
    }

    // Method to start phone number verification
    private void startPhoneNumberVerification(String phoneNumber) {
        PhoneAuthOptions options =
                PhoneAuthOptions.newBuilder(auth) // Use the Firebase Auth instance
                        .setPhoneNumber(phoneNumber)       // Phone number to verify
                        .setTimeout(60L, TimeUnit.SECONDS) // Timeout and unit
                        .setActivity(this)                 // Activity for callback binding
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
                        // Sign up success, navigate to MainActivity
                        startActivity(new Intent(Signup.this, MainActivity.class));
                        finish();  // Close SignupActivity
                    } else {
                        // If sign up fails, display a message to the user.
                        Toast.makeText(Signup.this, "Sign Up failed.", Toast.LENGTH_SHORT).show();
                    }
                });
    }

    // Utility method to check if the input is a valid email
    private boolean isValidEmail(String email) {
        return android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }
    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish(); // Close the activity and return to the previous one
            return true;
        }
        return super.onOptionsItemSelected(item);
    }
}


