package com.example.ttlockapp.card;

import android.os.Bundle;
import android.view.View;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;

import com.example.ttlockapp.MyApplication;
import com.example.ttlockapp.R;
import com.example.ttlockapp.databinding.ActivityCardManagementBinding;
import com.ttlock.bl.sdk.api.TTLockClient;
import com.ttlock.bl.sdk.callback.AddICCardCallback;
import com.ttlock.bl.sdk.callback.ClearAllICCardCallback;
import com.ttlock.bl.sdk.callback.DeleteICCardCallback;
import com.ttlock.bl.sdk.callback.GetAllICCardCallback;
import com.ttlock.bl.sdk.callback.ModifyICCardPeriodCallback;
import com.ttlock.bl.sdk.entity.ICCardInfo;
import com.ttlock.bl.sdk.entity.LockError;

import java.util.Calendar;
import java.util.List;

public class CardManagementActivity extends AppCompatActivity {
    private ActivityCardManagementBinding binding;
    private boolean isAddingCard = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DataBindingUtil.setContentView(this, R.layout.activity_card_management);

        if (MyApplication.getInstance().getCurrentLock() == null) {
            Toast.makeText(this, "No lock selected", Toast.LENGTH_SHORT).show();
            finish();
            return;
        }

        setupListeners();
        getAllCards();
    }

    private void setupListeners() {
        binding.btnAddCard.setOnClickListener(v -> {
            if (!isAddingCard) {
                startAddCard();
            } else {
                stopAddCard();
            }
        });

        binding.btnModifyCard.setOnClickListener(v -> modifyCardPeriod());
        binding.btnDeleteCard.setOnClickListener(v -> deleteCard());
        binding.btnClearAllCards.setOnClickListener(v -> clearAllCards());
        binding.btnRefreshCards.setOnClickListener(v -> getAllCards());
    }

    private void startAddCard() {
        binding.progressBar.setVisibility(View.VISIBLE);
        isAddingCard = true;
        binding.btnAddCard.setText("Stop Adding Card");
        binding.tvStatus.setText("Please tap card on the lock...");

        // Get start and end time
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.YEAR, 1); // Default validity: 1 year

        TTLockClient.getDefault().addICCard(startDate, endDate, new AddICCardCallback() {
            @Override
            public void onEnterAddMode() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(CardManagementActivity.this, 
                            "Ready to add card", Toast.LENGTH_SHORT).show();
                });
            }

            @Override
            public void onAddICCardSuccess(long cardNumber) {
                runOnUiThread(() -> {
                    binding.tvStatus.setText("Added card: " + cardNumber);
                    Toast.makeText(CardManagementActivity.this, 
                            "Card added successfully", Toast.LENGTH_SHORT).show();
                    stopAddCard();
                    getAllCards(); // Refresh the card list
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    Toast.makeText(CardManagementActivity.this, 
                            "Failed to add card: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                    stopAddCard();
                });
            }
        });
    }

    private void stopAddCard() {
        isAddingCard = false;
        binding.btnAddCard.setText("Add New Card");
        binding.progressBar.setVisibility(View.GONE);
        TTLockClient.getDefault().stopAddICCard();
    }

    private void modifyCardPeriod() {
        String cardNumber = binding.etCardNumber.getText().toString().trim();
        if (cardNumber.isEmpty()) {
            Toast.makeText(this, "Please enter card number", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        // Get new start and end time
        Calendar startDate = Calendar.getInstance();
        Calendar endDate = Calendar.getInstance();
        endDate.add(Calendar.MONTH, 1); // Extend by 1 month

        TTLockClient.getDefault().modifyICCardValidityPeriod(Long.parseLong(cardNumber), 
                startDate, endDate, new ModifyICCardPeriodCallback() {
            @Override
            public void onModifyICCardPeriodSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(CardManagementActivity.this, 
                            "Card period modified successfully", Toast.LENGTH_SHORT).show();
                    getAllCards(); // Refresh the card list
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(CardManagementActivity.this, 
                            "Failed to modify card: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void deleteCard() {
        String cardNumber = binding.etCardNumber.getText().toString().trim();
        if (cardNumber.isEmpty()) {
            Toast.makeText(this, "Please enter card number", Toast.LENGTH_SHORT).show();
            return;
        }

        binding.progressBar.setVisibility(View.VISIBLE);

        TTLockClient.getDefault().deleteICCard(Long.parseLong(cardNumber), 
                new DeleteICCardCallback() {
            @Override
            public void onDeleteICCardSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(CardManagementActivity.this, 
                            "Card deleted successfully", Toast.LENGTH_SHORT).show();
                    getAllCards(); // Refresh the card list
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(CardManagementActivity.this, 
                            "Failed to delete card: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void clearAllCards() {
        binding.progressBar.setVisibility(View.VISIBLE);

        TTLockClient.getDefault().clearAllICCard(new ClearAllICCardCallback() {
            @Override
            public void onClearAllICCardSuccess() {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(CardManagementActivity.this, 
                            "All cards cleared successfully", Toast.LENGTH_SHORT).show();
                    binding.tvCardList.setText("No cards");
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(CardManagementActivity.this, 
                            "Failed to clear cards: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    private void getAllCards() {
        binding.progressBar.setVisibility(View.VISIBLE);

        TTLockClient.getDefault().getAllICCardNumber(new GetAllICCardCallback() {
            @Override
            public void onGetAllICCardSuccess(List<ICCardInfo> cardList) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    if (cardList.isEmpty()) {
                        binding.tvCardList.setText("No cards");
                    } else {
                        StringBuilder sb = new StringBuilder();
                        for (ICCardInfo card : cardList) {
                            sb.append("Card: ").append(card.getCardNumber()).append("\n");
                        }
                        binding.tvCardList.setText(sb.toString());
                    }
                });
            }

            @Override
            public void onFail(LockError error) {
                runOnUiThread(() -> {
                    binding.progressBar.setVisibility(View.GONE);
                    Toast.makeText(CardManagementActivity.this, 
                            "Failed to get cards: " + error.getErrorMsg(), Toast.LENGTH_SHORT).show();
                });
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (isAddingCard) {
            stopAddCard();
        }
    }
}
