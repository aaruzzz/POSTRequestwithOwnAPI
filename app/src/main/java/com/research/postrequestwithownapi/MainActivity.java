package com.research.postrequestwithownapi;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.MediaStore;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import android.util.Patterns;

import com.google.android.material.bottomsheet.BottomSheetDialog;

import java.util.List;


import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

public class MainActivity extends AppCompatActivity {

    private TextView textViewResult;
    private APIInterface apiInterface;

    EditText email;
    EditText name;
    EditText password;
    Button CreateAccountBTN;
    Button RefreshBTN;
    Button UploadBTN;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        textViewResult = findViewById(R.id.result_list);

        Retrofit retrofit = new Retrofit.Builder().baseUrl("http://127.0.0.1:3000/").addConverterFactory(GsonConverterFactory.create()).build();
        apiInterface = retrofit.create(APIInterface.class);

        getUsers();

        CreateAccountBTN = findViewById(R.id.createaccountbtn);
        CreateAccountBTN.setOnClickListener(view -> postUsers());

        RefreshBTN = findViewById(R.id.refreshbtn);
        RefreshBTN.setOnClickListener(view -> getUsers());

        UploadBTN = findViewById(R.id.uploadbtn);
        UploadBTN.setOnClickListener(view -> {
            Intent intent = new Intent(Intent.ACTION_PICK, MediaStore.Images.Media.EXTERNAL_CONTENT_URI);
            startActivityForResult(intent, 3);
        });


    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK && data != null) {
            Uri selectedImage = data.getData();
            ImageView ProfilePicImage = findViewById(R.id.profile_pic);
            ImageView SmallPP = findViewById(R.id.userprofileimg);
            ProfilePicImage.setImageURI(selectedImage);
            SmallPP.setImageURI(selectedImage);
            TextView ProfilePicText = findViewById(R.id.profile_pic_text);
            ProfilePicText.setText(R.string.profilepic);
        }
    }

    private void getUsers() {
        Call<List<Users>> call_users = apiInterface.getUsers();

        call_users.enqueue(new Callback<List<Users>>() {
            @Override
            public void onResponse(@NonNull Call<List<Users>> call, @NonNull Response<List<Users>> response) {
                List<Users> users = response.body();
                textViewResult.setText(null);
                if (users != null) {
                    for (Users users1 : users) {
                        Integer id = users1.getUser_id();
                        String email = "\nEmail: " + users1.getEmail() + "\n";
                        String name = "Name: " + users1.getName() + "\n";
                        String password = "Password: " + users1.getPassword() + "\n";
                        textViewResult.append("ID: " + id + email + name + password + "\n");
                    }
                }
            }

            @Override
            public void onFailure(@NonNull Call<List<Users>> call, @NonNull Throwable t) {
                textViewResult.setText(t.getMessage());
            }
        });
    }

    private void postUsers() {
        if (emailChecker() && nameChecker() && passwordChecker()) {
            actualPost();
        } else Toast.makeText(this, "Encountered an error", Toast.LENGTH_SHORT).show();

    }


    private boolean emailChecker() {
        boolean emailFlag = false;
        email = findViewById(R.id.enteremail);
        String email_text = email.getText().toString();
        TextView email_error_text = findViewById(R.id.emailexistmsg);

        if (!email_text.isEmpty() && Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
            emailFlag = true;
        } else if (email_text.isEmpty()) {
            makeBackgroundRed(email);
            email_error_text.setText(R.string.email_missing);
            email_error_text.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "Email missing!", Toast.LENGTH_SHORT).show();
        } else if (!Patterns.EMAIL_ADDRESS.matcher(email_text).matches()) {
            makeBackgroundRed(email);
            email_error_text.setText(R.string.enter_valid_email);
            email_error_text.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "Enter valid email!", Toast.LENGTH_SHORT).show();
        } else {
            makeBackgroundRed(name);
            Toast.makeText(this, "EMAIL ERROR", Toast.LENGTH_SHORT).show();
        }

        email.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {

            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                makeBackgroundBlack(email);
                email_error_text.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return emailFlag;
    }

    private boolean nameChecker() {
        name = findViewById(R.id.enterfullname);
        TextView name_text_error = findViewById(R.id.nameexistmsg);
        String name_text = name.getText().toString();
        boolean nameFlag = false;

        if (name_text.trim().matches("^[A-Za-z]+(?:\\s[A-Za-z]+)?(?:\\s[A-Za-z]+)*$") && name_text.length() <= 32) {
            nameFlag = true;
        } else if (name_text.isEmpty()) {
            makeBackgroundRed(name);
            name_text_error.setText(R.string.name_missing);
            name_text_error.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "Name missing!", Toast.LENGTH_SHORT).show();
        } else if (!(name_text.trim().matches("^[A-Za-z]+(?:\\s[A-Za-z]+)?(?:\\s[A-Za-z]+)*$"))) {
            makeBackgroundRed(name);
            name_text_error.setText(R.string.please_enter_proper_name);
            name_text_error.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "Please enter proper name!", Toast.LENGTH_SHORT).show();
        } else if (name_text.length() > 32) {
            makeBackgroundRed(name);
            name_text_error.setText(R.string.input_is_too_long);
            name_text_error.setVisibility(View.VISIBLE);
//            Toast.makeText(this, "TOO LONG", Toast.LENGTH_SHORT).show();
        } else {
            makeBackgroundRed(name);
            Toast.makeText(this, "NAME ERROR", Toast.LENGTH_SHORT).show();
        }


        name.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                makeBackgroundBlack(name);
                name_text_error.setVisibility(View.GONE);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return nameFlag;
    }


    private boolean passwordChecker() {
        password = findViewById(R.id.enterpassword);
        String password_text = password.getText().toString();
        boolean passwordFlag = false;


        if (password_text.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^\\w\\s]).{8,}$")) {
            passwordFlag = true;
        } else {
            // Inflate the bottom sheet view
            View bottomSheetView = getLayoutInflater().inflate(R.layout.bottom_sheet, null);

            final BottomSheetDialog bottomSheetDialog = new BottomSheetDialog(this);
            bottomSheetDialog.setContentView(bottomSheetView);

            TextView eight_letters = bottomSheetView.findViewById(R.id.eight_letter_text);
            TextView capital_letter = bottomSheetView.findViewById(R.id.capital_letter_text);
            TextView small_letter = bottomSheetView.findViewById(R.id.small_letter_text);
            TextView one_number = bottomSheetView.findViewById(R.id.one_number_text);
            TextView special_character = bottomSheetView.findViewById(R.id.special_character_text);


            if (!password_text.matches("^.{8,}$")) {
                makeBackgroundRed(password);
                eight_letters.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_close_24_red, 0, 0, 0);
            } else
                eight_letters.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_done_24_green, 0, 0, 0);

            if (!password_text.matches("^(?=.*?[A-Z]).+$")) {
                makeBackgroundRed(password);
                capital_letter.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_close_24_red, 0, 0, 0);
            } else
                capital_letter.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_done_24_green, 0, 0, 0);

            if (!password_text.matches("^(?=.*?[a-z]).+$")) {
                makeBackgroundRed(password);
                small_letter.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_close_24_red, 0, 0, 0);
            } else
                small_letter.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_done_24_green, 0, 0, 0);

            if (!password_text.matches("^(?=.*?\\d).+$")) {
                makeBackgroundRed(password);
                one_number.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_close_24_red, 0, 0, 0);
            } else
                one_number.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_done_24_green, 0, 0, 0);

            if (!password_text.matches("^(?=.*?[^\\w\\s]).+$")) {
                makeBackgroundRed(password);
                special_character.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_close_24_red, 0, 0, 0);
            } else
                special_character.setCompoundDrawablesRelativeWithIntrinsicBounds(R.drawable.baseline_done_24_green, 0, 0, 0);


//            if (password_text.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^\\w\\s])$")) {
//                makeBackgroundRed(password);
//                Toast.makeText(MainActivity.this, "Please enter minimum 8 letters", Toast.LENGTH_SHORT).show();
//            } else if (password_text.matches("^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^\\w\\s]).{8,}$")) {
//                makeBackgroundRed(password);
//                Toast.makeText(MainActivity.this, "Please enter at least one capital letter", Toast.LENGTH_SHORT).show();
//            } else if (password_text.matches("^(?=.*?[A-Z])(?=.*?[0-9])(?=.*?[^\\w\\s]).{8,}$")) {
//                makeBackgroundRed(password);
//                Toast.makeText(MainActivity.this, "Please enter at least one small letter", Toast.LENGTH_SHORT).show();
//            } else if (password_text.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[^\\w\\s]).{8,}$")) {
//                makeBackgroundRed(password);
//                Toast.makeText(MainActivity.this, "Please enter at least one number", Toast.LENGTH_SHORT).show();
//            } else if (password_text.matches("^(?=.*?[a-z])(?=.*?[0-9])(?=.*?[A-Z]).{8,}$")) {
//                makeBackgroundRed(password);
//                Toast.makeText(MainActivity.this, "Please enter at least one special character", Toast.LENGTH_SHORT).show();
//            } else if (!password_text.matches("^(?=.*?[A-Z])(?=.*?[a-z])(?=.*?[0-9])(?=.*?[^\\w\\s]).{8,}$")) {
//                makeBackgroundRed(password);
//                Toast.makeText(MainActivity.this, "Password must contain minimum 8 characters, atleast one capital letter, atleast one small letter, atleast one special character and atleast one number.", Toast.LENGTH_SHORT).show();
//            } else {
//                makeBackgroundRed(password);
//                Toast.makeText(MainActivity.this, "ERROR", Toast.LENGTH_SHORT).show();
//            }

            bottomSheetDialog.show();

        }

        password.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence charSequence, int i, int i1, int i2) {
            }

            @Override
            public void onTextChanged(CharSequence charSequence, int i, int i1, int i2) {
                makeBackgroundBlack(password);
            }

            @Override
            public void afterTextChanged(Editable editable) {
            }
        });

        return passwordFlag;
    }

    private void makeBackgroundRed(EditText Variable) {
        View background = new View(getApplicationContext());
        background.setBackgroundResource(R.drawable.textborderred);
        Variable.setBackground(background.getBackground());
        Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
        Variable.startAnimation(shake);
    }

    private void makeBackgroundBlack(EditText Variable) {
        View background = new View(getApplicationContext());
        background.setBackgroundResource(R.drawable.textborderblack);
        Variable.setBackground(background.getBackground());
    }


    private void actualPost() {
        Users users = new Users(null, email.getText().toString(), name.getText().toString(), password.getText().toString());
        Call<Users> usersCall = apiInterface.createUsers(users);

        usersCall.enqueue(new Callback<Users>() {
            @Override
            public void onResponse(@NonNull Call<Users> call, @NonNull Response<Users> response) {
                if (!response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Error" + response.code(), Toast.LENGTH_SHORT).show();
//                    textViewResult.setText("Code error: "+response.code());
                }
                if (response.isSuccessful()) {
                    Toast.makeText(MainActivity.this, "Successfully Created new user!!", Toast.LENGTH_SHORT).show();
                    email.setText(null);
                    name.setText(null);
                    password.setText(null);
                }
            }

            @Override
            public void onFailure(@NonNull Call<Users> call, @NonNull Throwable t) {
                textViewResult.setText(t.getMessage());
                Toast.makeText(MainActivity.this, t.getMessage(), Toast.LENGTH_SHORT).show();
            }
        });
    }

}