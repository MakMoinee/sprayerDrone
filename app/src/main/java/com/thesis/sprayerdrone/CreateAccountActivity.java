package com.thesis.sprayerdrone;

import android.os.Bundle;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.github.MakMoinee.library.interfaces.DefaultBaseListener;
import com.github.MakMoinee.library.services.HashPass;
import com.thesis.sprayerdrone.databinding.ActivityCreateAccountBinding;
import com.thesis.sprayerdrone.models.Users;
import com.thesis.sprayerdrone.services.UserService;

public class CreateAccountActivity extends AppCompatActivity {

    ActivityCreateAccountBinding binding;
    HashPass hashPass = new HashPass();
    UserService userService;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCreateAccountBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        userService = new UserService(CreateAccountActivity.this);
        setListeners();
    }

    private void setListeners() {
        binding.btnCreateAccount.setOnClickListener(v -> {
            String username = binding.editUsername.getText().toString().trim();
            String password = binding.editPassword.getText().toString().trim();
            String confirmPass = binding.editConfirmPassword.getText().toString().trim();
            String firstName = binding.editFirstName.getText().toString().trim();
            String middleName = binding.editMiddleName.getText().toString().trim();
            String lastName = binding.editLastName.getText().toString().trim();

            if (username.equals("") || password.equals("") || confirmPass.equals("") || firstName.equals("") || lastName.equals("")) {
                Toast.makeText(CreateAccountActivity.this, "Please Don't Leave Empty Fields", Toast.LENGTH_SHORT).show();
            } else {
                if (!confirmPass.equals(password)) {
                    return;
                }
                Users userForCreate = new Users.UserBuilder()
                        .setFirstName(firstName)
                        .setMiddleName(middleName)
                        .setLastName(lastName)
                        .setUsername(username)
                        .setPassword(hashPass.makeHashPassword(password))
                        .build();
                userService.insertUniqueUser(userForCreate, new DefaultBaseListener() {
                    @Override
                    public <T> void onSuccess(T any) {
                        Toast.makeText(CreateAccountActivity.this, "Successfully Created Account", Toast.LENGTH_SHORT).show();
                        finish();
                    }

                    @Override
                    public void onError(Error error) {
                        Toast.makeText(CreateAccountActivity.this, "Failed to create account, Please try again later", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        });
    }
}
