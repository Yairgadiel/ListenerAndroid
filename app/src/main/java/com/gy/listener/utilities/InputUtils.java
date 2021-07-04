package com.gy.listener.utilities;

import android.text.Editable;
import android.text.TextWatcher;

import com.google.android.material.textfield.TextInputEditText;
import com.google.android.material.textfield.TextInputLayout;
import com.gy.listener.R;

public final class InputUtils {
    /**
     * This method adds a text validator for a given edit text and layout (by Material Design).
     * It validates an input was inserted
     */
    public static void addTextValidator(TextInputEditText inputEditText, TextInputLayout textInputLayout) {
        inputEditText.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {
                if (s.length() == 0) {
                    textInputLayout.setError(inputEditText.getContext().getString(R.string.empty_string_error));
                }
                else {
                    textInputLayout.setError(null);
                }
            }

            @Override
            public void afterTextChanged(Editable s) {}
        });
    }
}
