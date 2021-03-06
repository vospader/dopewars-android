package com.daverin.dopewars;

import java.util.Random;

import android.app.Activity;
import android.app.Dialog;
import android.content.Intent;
import android.content.res.Configuration;
import android.graphics.Color;
import android.os.Bundle;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.ScrollView;
import android.widget.SeekBar;
import android.widget.TextView;

public class DopeWars2Game extends Activity {

  // When the dialog is created, there's not much to do other than set the
  // view to the main game screen layout.  onCreate doesn't have to do
  // any game state setup because onResume does that, and onResume always
  // follows onCreate.
  @Override
  public void onCreate(Bundle savedInstanceState) {
    super.onCreate(savedInstanceState);
    setContentView(R.layout.main_game_screen);
    ((Button)findViewById(R.id.subway_button)).setOnClickListener(
        new SubwayClickListener());
    ((Button)findViewById(R.id.bank_deposit_button)).setOnClickListener(
        new BankDepositListener());
    ((Button)findViewById(R.id.bank_withdraw_button)).setOnClickListener(
        new BankWithdrawListener());
    ((Button)findViewById(R.id.loan_shark_button)).setOnClickListener(
        new PayLoanSharkClickListener());
    ((Button)findViewById(R.id.inventory_button)).setOnClickListener(
        new InventoryClickListener());
  }

  // onResume is the key method for restoring saved data.  This will go to
  // the local game database and retrieve the stored game data to use with
  // the game state, then refresh the display according to that game state.
  @Override
  public void onResume() {
    super.onResume();
    game_state_ = getGameState();
    refreshDisplay();
  }

  // onPause is the key method for saving game data against phone actions.
  // As soon as anything causes the process to pause (turning the phone
  // screen off, going to the home view, etc), the onPause method will
  // save out the current game state.
  //
  // TODO: with enough saving on data changes, is this even necessary?
  @Override
  public void onPause() {
    super.onPause();
    saveGameState();
  }

  // This is to handle view refresh when the phone's orientation is changed.
  //
  // TODO: this may not end up being necessary, depending on the final
  //       decisions about view
  @Override
  public void onConfigurationChanged(Configuration newConfig) {
    super.onConfigurationChanged(newConfig);
    refreshDisplay();
  }

  // The game relies a lot on dialogs to interact with the user, so the
  // handler to create dialogs is important.  Each dialog in the game has
  // its own initialization function.
  @Override
  protected Dialog onCreateDialog(int id) {
    switch(id) {
    case DIALOG_DRUG_BUY:       return GetDrugBuyDialog();
    case DIALOG_DRUG_SELL:      return GetDrugSellDialog();
    case DIALOG_SUBWAY:         return GetSubwayDialog();
    case DIALOG_PAY_LOAN_SHARK: return GetPayLoanSharkDialog();
    case DIALOG_BANK_DEPOSIT:   return GetBankDepositDialog();
    case DIALOG_BANK_WITHDRAW:  return GetBankWithdrawDialog();
    case DIALOG_HARDASS:        return GetHardassDialog();
    case DIALOG_END_GAME:       return GetEndGameDialog();
    case DIALOG_BUY_COAT:       return GetBuyCoatDialog();
    case DIALOG_BUY_GUN:        return GetBuyGunDialog();
    case DIALOG_INVENTORY:      return GetInventoryDialog();
    }

    return super.onCreateDialog(id);
  }

  // The game relies a lot on dialogs to interact with the user, so the
  // handler to prepare dialogs is important.  Each dialog has its own
  // preparation function.
  @Override
  protected void onPrepareDialog(int id, Dialog d) {
    switch(id) {
    case DIALOG_SUBWAY:         PrepareSubwayDialog(); break;
    case DIALOG_DRUG_BUY:       PrepareDrugBuyDialog(); break;
    case DIALOG_DRUG_SELL:      PrepareDrugSellDialog(); break;
    case DIALOG_PAY_LOAN_SHARK: PreparePayLoanSharkDialog(); break;
    case DIALOG_BANK_DEPOSIT:   PrepareBankDepositDialog(); break;
    case DIALOG_BANK_WITHDRAW:  PrepareBankWithdrawDialog(); break;
    case DIALOG_HARDASS:        PrepareHardassDialog(); break;
    case DIALOG_END_GAME:       PrepareEndGameDialog(); break;
    case DIALOG_BUY_COAT:       PrepareBuyCoatDialog(); break;
    case DIALOG_BUY_GUN:        PrepareBuyGunDialog(); break;
    case DIALOG_INVENTORY:      PrepareInventoryDialog(); break;
    }
  }

  // TODO: The drug buy dialog is under development.
  private Dialog GetDrugBuyDialog() {
    if (drug_buy_dialog_ == null) {
      drug_buy_dialog_ = new Dialog(this);
      drug_buy_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
      drug_buy_dialog_.setContentView(R.layout.drug_buy_layout);
      ((SeekBar)drug_buy_dialog_.findViewById(R.id.drug_quantity_slide)).
      setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
          ((TextView)drug_buy_dialog_.findViewById(R.id.drug_quantity)).setText(
              Integer.toString(progress));
        }
        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}
      });
    }
    return drug_buy_dialog_;
  }

  // TODO: The drug buy dialog is under development.
  private void PrepareDrugBuyDialog() {
    ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_name))).setText(
        "Buy " + game_state_.drugs_.elementAt(dialog_drug_index_).drug_name_);
    float drug_buy_price = game_state_.drug_price_.elementAt(dialog_drug_index_);
    ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_price))).setText(
        "$" + Float.toString(drug_buy_price));
    int max_num_drugs = (int)(game_state_.cash_ / drug_buy_price);

    max_num_drugs = Math.min(max_num_drugs, game_state_.max_space_);

    ((SeekBar)(drug_buy_dialog_.findViewById(R.id.drug_quantity_slide))).setMax(
        max_num_drugs);
    ((SeekBar)(drug_buy_dialog_.findViewById(R.id.drug_quantity_slide))).setProgress(
        max_num_drugs);

    ((TextView)(drug_buy_dialog_.findViewById(R.id.drug_quantity))).setText(
        Integer.toString(max_num_drugs));

    ((Button)(drug_buy_dialog_.findViewById(R.id.drug_buy_cancel))).setOnClickListener(
        new CancelDialogListener(DIALOG_DRUG_BUY));

    ((Button)(drug_buy_dialog_.findViewById(R.id.drug_buy_confirm)))
    .setOnClickListener(new BuyDrugsListener());
  }

  // TODO: The drug sell dialog is under development.
  private Dialog GetDrugSellDialog() {
    if (drug_sell_dialog_ == null) {
      drug_sell_dialog_ = new Dialog(this);
      drug_sell_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
      drug_sell_dialog_.setContentView(R.layout.drug_sell_layout);
      ((SeekBar)drug_sell_dialog_.findViewById(R.id.drug_quantity_slide)).
      setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
          ((TextView)drug_sell_dialog_.findViewById(R.id.drug_quantity)).setText(
              Integer.toString(progress));
        }
        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}

      });
    }
    return drug_sell_dialog_;
  }

  // TODO: The drug sell dialog is under development.
  private void PrepareDrugSellDialog() {
    ((TextView)(drug_sell_dialog_.findViewById(R.id.drug_name))).setText(
        "Sell " + game_state_.drugs_.elementAt(dialog_drug_index_).drug_name_);
    float drug_sell_price = game_state_.drug_price_.elementAt(dialog_drug_index_);
    ((TextView)(drug_sell_dialog_.findViewById(R.id.drug_price))).setText(
        "$" + Float.toString(drug_sell_price));
    int drug_quantity = game_state_.dealer_drugs_.elementAt(dialog_drug_index_);

    ((SeekBar)(drug_sell_dialog_.findViewById(R.id.drug_quantity_slide))).setMax(
        drug_quantity);
    ((SeekBar)(drug_sell_dialog_.findViewById(R.id.drug_quantity_slide))).setProgress(
        drug_quantity);

    ((TextView)(drug_sell_dialog_.findViewById(R.id.drug_quantity))).setText(
        Integer.toString(drug_quantity));

    ((Button)(drug_sell_dialog_.findViewById(R.id.drug_sell_cancel))).setOnClickListener(
        new CancelDialogListener(DIALOG_DRUG_SELL));

    ((Button)(drug_sell_dialog_.findViewById(R.id.drug_sell_confirm)))
    .setOnClickListener(new SellDrugsListener());
  }

  // TODO: The subway dialog is under development.
  private Dialog GetSubwayDialog() {
    if (subway_dialog_ == null) {
      subway_dialog_ = new Dialog(this);
      subway_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
      subway_dialog_.setContentView(R.layout.simple_subway_layout);
      subway_dialog_.findViewById(R.id.brooklyn).setOnClickListener(
          new ChangeLocationListener(0));
      subway_dialog_.findViewById(R.id.the_bronx).setOnClickListener(
          new ChangeLocationListener(1));
      subway_dialog_.findViewById(R.id.central_park).setOnClickListener(
          new ChangeLocationListener(2));
      subway_dialog_.findViewById(R.id.coney_island).setOnClickListener(
          new ChangeLocationListener(3));
      subway_dialog_.findViewById(R.id.the_ghetto).setOnClickListener(
          new ChangeLocationListener(4));
      subway_dialog_.findViewById(R.id.manhattan).setOnClickListener(
          new ChangeLocationListener(5));
      subway_dialog_.findViewById(R.id.queens).setOnClickListener(
          new ChangeLocationListener(6));
      subway_dialog_.findViewById(R.id.staten_island).setOnClickListener(
          new ChangeLocationListener(7));
    }
    return subway_dialog_;
  }

  // TODO: The subway dialog is under development.
  private void PrepareSubwayDialog() {
    subway_dialog_.findViewById(R.id.brooklyn).setVisibility(
        game_state_.location_ == 0 ? View.GONE : View.VISIBLE);
    subway_dialog_.findViewById(R.id.the_bronx).setVisibility(
        game_state_.location_ == 1 ? View.GONE : View.VISIBLE);
    subway_dialog_.findViewById(R.id.central_park).setVisibility(
        game_state_.location_ == 2 ? View.GONE : View.VISIBLE);
    subway_dialog_.findViewById(R.id.coney_island).setVisibility(
        game_state_.location_ == 3 ? View.GONE : View.VISIBLE);
    subway_dialog_.findViewById(R.id.the_ghetto).setVisibility(
        game_state_.location_ == 4 ? View.GONE : View.VISIBLE);
    subway_dialog_.findViewById(R.id.manhattan).setVisibility(
        game_state_.location_ == 5 ? View.GONE : View.VISIBLE);
    subway_dialog_.findViewById(R.id.queens).setVisibility(
        game_state_.location_ == 6 ? View.GONE : View.VISIBLE);
    subway_dialog_.findViewById(R.id.staten_island).setVisibility(
        game_state_.location_ == 7 ? View.GONE : View.VISIBLE);
  }

  // TODO: The subway dialog is under development
  public class ChangeLocationListener implements View.OnClickListener {
    public ChangeLocationListener(int new_location) {
      location_ = new_location;
    }
    public void onClick(View v) {
      game_state_.location_ = location_;
      // TODO: handle remaining time/end of game, or actually, that may
      // be something that gets handled in refreshDisplay(), because
      // really all that's going to happen here is not showing the
      // subway and instead showing an end game button, which will
      // have its own handling.
      game_state_.days_left_ -= 1;

      // Apply loan shark interest when moving locations.
      game_state_.loan_ +=
        game_state_.loan_interest_rate_ * game_state_.loan_;

      // Apply bank interest when moving locations.
      game_state_.bank_ += 
        game_state_.bank_interest_rate_ * game_state_.bank_;

      game_state_.SetupNewLocation();

      refreshDisplay();
      dismissDialog(DIALOG_SUBWAY);
    }
    int location_;
  }

  // TODO: The loan shark dialogs are under development.
  private Dialog GetPayLoanSharkDialog() {
    if (pay_loan_shark_dialog_ == null) {
      pay_loan_shark_dialog_ = new Dialog(this);
      pay_loan_shark_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
      pay_loan_shark_dialog_.setContentView(R.layout.loan_shark_pay_layout);
      ((SeekBar)pay_loan_shark_dialog_.findViewById(R.id.loan_amount_slide)).
      setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
          ((TextView)pay_loan_shark_dialog_.findViewById(R.id.loan_amount)).setText(
              Integer.toString(progress));
        }
        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}

      });
      ((Button)pay_loan_shark_dialog_.findViewById(R.id.loan_refresh)).setOnClickListener(
          new RefreshLoanListener());
    }
    return pay_loan_shark_dialog_;
  }

  // TODO: The loan shark dialogs are under development.
  private void PreparePayLoanSharkDialog() {
    int max_loan_payment = Math.min(game_state_.loan_, game_state_.cash_);
    ((SeekBar)(pay_loan_shark_dialog_.findViewById(R.id.loan_amount_slide))).setMax(
        max_loan_payment);
    ((SeekBar)(pay_loan_shark_dialog_.findViewById(R.id.loan_amount_slide))).setProgress(
        max_loan_payment);

    ((TextView)(pay_loan_shark_dialog_.findViewById(R.id.loan_amount))).setText(
        Integer.toString(max_loan_payment));

    ((Button)(pay_loan_shark_dialog_.findViewById(R.id.loan_pay_cancel))).setOnClickListener(
        new CancelDialogListener(DIALOG_PAY_LOAN_SHARK));

    ((Button)(pay_loan_shark_dialog_.findViewById(R.id.loan_pay_confirm)))
    .setOnClickListener(new PayLoanSharkListener());

    boolean refresh_button_visible = !game_state_.DealerHasDrugs()
    && game_state_.cash_ < 2000
    && game_state_.bank_ < 2000;
    pay_loan_shark_dialog_.findViewById(R.id.loan_refresh).setVisibility(
        refresh_button_visible ? View.VISIBLE : View.GONE);
  }

  // TODO: The banking dialogs are under development.
  private Dialog GetBankDepositDialog() {
    if (bank_deposit_dialog_ == null) {
      bank_deposit_dialog_ = new Dialog(this);
      bank_deposit_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
      bank_deposit_dialog_.setContentView(R.layout.bank_deposit_layout);
      ((SeekBar)bank_deposit_dialog_.findViewById(R.id.bank_amount_slide)).
      setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
          ((TextView)bank_deposit_dialog_.findViewById(R.id.bank_amount)).setText(
              Integer.toString(progress));
        }
        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}

      });
    }
    return bank_deposit_dialog_;
  }

  // TODO: The banking dialogs are under development.
  private void PrepareBankDepositDialog() {
    int max_bank_deposit = game_state_.cash_;
    ((SeekBar)(bank_deposit_dialog_.findViewById(R.id.bank_amount_slide))).setMax(
        max_bank_deposit);
    ((SeekBar)(bank_deposit_dialog_.findViewById(R.id.bank_amount_slide))).setProgress(
        max_bank_deposit);

    ((TextView)(bank_deposit_dialog_.findViewById(R.id.bank_amount))).setText(
        Integer.toString(max_bank_deposit));

    ((Button)(bank_deposit_dialog_.findViewById(R.id.bank_deposit_cancel))).setOnClickListener(
        new CancelDialogListener(DIALOG_BANK_DEPOSIT));

    ((Button)(bank_deposit_dialog_.findViewById(R.id.bank_deposit_confirm)))
    .setOnClickListener(new BankDepositConfirmListener());
  }

  // TODO: The banking dialogs are under development.
  private Dialog GetBankWithdrawDialog() {
    if (bank_withdraw_dialog_ == null) {
      bank_withdraw_dialog_ = new Dialog(this);
      bank_withdraw_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
      bank_withdraw_dialog_.setContentView(R.layout.bank_withdraw_layout);
      ((SeekBar)bank_withdraw_dialog_.findViewById(R.id.bank_amount_slide)).
      setOnSeekBarChangeListener(new SeekBar.OnSeekBarChangeListener() {
        public void onProgressChanged(SeekBar seekBar, int progress,
            boolean fromTouch) {
          ((TextView)bank_withdraw_dialog_.findViewById(R.id.bank_amount)).setText(
              Integer.toString(progress));
        }
        public void onStartTrackingTouch(SeekBar seekBar) {}
        public void onStopTrackingTouch(SeekBar seekBar) {}

      });
    }
    return bank_withdraw_dialog_;
  }

  // TODO: The banking dialogs are under development.
  private void PrepareBankWithdrawDialog() {
    int max_bank_deposit = game_state_.bank_;
    ((SeekBar)(bank_withdraw_dialog_.findViewById(R.id.bank_amount_slide))).setMax(
        max_bank_deposit);
    ((SeekBar)(bank_withdraw_dialog_.findViewById(R.id.bank_amount_slide))).setProgress(
        max_bank_deposit);

    ((TextView)(bank_withdraw_dialog_.findViewById(R.id.bank_amount))).setText(
        Integer.toString(max_bank_deposit));

    ((Button)(bank_withdraw_dialog_.findViewById(R.id.bank_withdraw_cancel))).setOnClickListener(
        new CancelDialogListener(DIALOG_BANK_WITHDRAW));

    ((Button)(bank_withdraw_dialog_.findViewById(R.id.bank_withdraw_confirm)))
    .setOnClickListener(new BankWithdrawConfirmListener());
  }

  // TODO: The hardass dialogs are under development.
  private Dialog GetHardassDialog() {
    if (hardass_dialog_ == null) {
      hardass_dialog_ = new Dialog(this);
      hardass_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
      hardass_dialog_.setContentView(R.layout.hardass_layout);
      ((Button)hardass_dialog_.findViewById(R.id.hardass_fight)).setOnClickListener(
          new FightHardassListener());
      ((Button)hardass_dialog_.findViewById(R.id.hardass_run)).setOnClickListener(
          new RunFromHardassListener());
      ((Button)hardass_dialog_.findViewById(R.id.hardass_do_nothing)).setOnClickListener(
          new DoNothingWithHardassListener());
    }
    return hardass_dialog_;
  }

  private class FightHardassListener implements View.OnClickListener {
    public void onClick(View v) {
      // TODO: fight logic
      game_state_.hardass_deputies_ = 0;
      game_state_.hardass_health_ = Math.max(game_state_.hardass_health_ - 5, 0);
      game_state_.health_ -= 1;
      dismissDialog(DIALOG_HARDASS);
      refreshDisplay();
    }
  }

  private class RunFromHardassListener implements View.OnClickListener {
    public void onClick(View v) {
      // TODO: run logic
      if (rand_gen_.nextFloat() < 0.5) {
        game_state_.hardass_deputies_ = 0;
        game_state_.hardass_health_ = 0;
      } else {
        game_state_.health_ -= 1;
      }
      dismissDialog(DIALOG_HARDASS);
      refreshDisplay();
    }
  }

  private class DoNothingWithHardassListener implements View.OnClickListener {
    public void onClick(View v) {
      // TODO: stand there logic
      game_state_.health_ -= 1;
      dismissDialog(DIALOG_HARDASS);
      refreshDisplay();
    }
  }

  // TODO: The hardass dialogs are under development.
  private void PrepareHardassDialog() {
    String warning_string = "Officer Hardass and " +
    Integer.toString(game_state_.hardass_deputies_) +
    " of his deputies are attacking!  Your health: " +
    Integer.toString(game_state_.health_);

    ((TextView)(hardass_dialog_.findViewById(R.id.hardass_message))).setText(
        warning_string);
    hardass_dialog_.findViewById(R.id.hardass_fight).setVisibility(
        game_state_.guns_ > 0 ? View.VISIBLE : View.GONE);
  }

  // TODO: The end game dialogs are under development.
  private Dialog GetEndGameDialog() {
    if (end_game_dialog_ == null) {
      end_game_dialog_ = new Dialog(this);
      end_game_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
      end_game_dialog_.setContentView(R.layout.end_of_game_layout);
      ((Button)end_game_dialog_.findViewById(R.id.main_menu_button)).setOnClickListener(
          new BackToMainMenuListener());
    }
    return end_game_dialog_;
  }

  // TODO: The end game dialogs are under development.
  private void PrepareEndGameDialog() {
    ((TextView)end_game_dialog_.findViewById(R.id.total_cash)).setText(
        "$" + Integer.toString(game_state_.cash_ + game_state_.bank_ - game_state_.loan_)); 
  }

  // TODO: The coat buying dialogs are under development.
  private Dialog GetBuyCoatDialog() {
    if (buy_coat_dialog_ == null) {
      buy_coat_dialog_ = new Dialog(this);
      buy_coat_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
      buy_coat_dialog_.setContentView(R.layout.buy_coat_layout);
      ((Button)buy_coat_dialog_.findViewById(R.id.buy_coat_button)).setOnClickListener(
          new BuyCoatListener());
      ((Button)buy_coat_dialog_.findViewById(R.id.cancel_button)).setOnClickListener(
          new CancelBuyCoatListener());
    }
    return buy_coat_dialog_;
  }

  // TODO: The coat buying dialogs are under development.
  private void PrepareBuyCoatDialog() {
    ((TextView)buy_coat_dialog_.findViewById(R.id.buy_coat_message)).setText(
        "Wanna buy a coat for $" + Integer.toString(game_state_.coat_price_)); 
  }

  // TODO: The coat buying dialogs are under development.
  private Dialog GetBuyGunDialog() {
    if (buy_gun_dialog_ == null) {
      buy_gun_dialog_ = new Dialog(this);
      buy_gun_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
      buy_gun_dialog_.setContentView(R.layout.buy_gun_layout);
      ((Button)buy_gun_dialog_.findViewById(R.id.buy_gun_button)).setOnClickListener(
          new BuyGunListener());
      ((Button)buy_gun_dialog_.findViewById(R.id.cancel_button)).setOnClickListener(
          new CancelBuyGunListener());
    }
    return buy_gun_dialog_;
  }

  // TODO: The coat buying dialogs are under development.
  private void PrepareBuyGunDialog() {
    ((TextView)buy_gun_dialog_.findViewById(R.id.buy_gun_message)).setText(
        "Wanna buy a gun for $" + Integer.toString(game_state_.gun_price_)); 
  }

  // TODO: The inventory dialogs are under development.
  private Dialog GetInventoryDialog() {
    if (inventory_dialog_ == null) {
      inventory_dialog_ = new Dialog(this);
      inventory_dialog_.requestWindowFeature(Window.FEATURE_NO_TITLE);
      inventory_dialog_.setContentView(R.layout.inventory_layout);
      ((Button)inventory_dialog_.findViewById(R.id.ok_button)).setOnClickListener(
          new CancelDialogListener(DIALOG_INVENTORY));
    }
    return inventory_dialog_;
  }

  // TODO: this is very incomplete, because we haven't worked out what the
  // target layout is going to be, so for now, simplicity rules.
  private LinearLayout ConstructInventoryDrugLayout(int drug_index) {
    LinearLayout drug_layout = new LinearLayout(this);
    drug_layout.setOrientation(LinearLayout.HORIZONTAL);
    drug_layout.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.FILL_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));

    TextView drug_name = new TextView(this);
    drug_name.setTextColor(Color.WHITE);
    drug_name.setTextSize(14);
    drug_name.setGravity(Gravity.LEFT);
    drug_name.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));
    drug_name.setText(game_state_.drugs_.elementAt(drug_index).drug_name_);

    TextView drug_quantity = new TextView(this);
    drug_quantity.setTextColor(Color.WHITE);
    drug_quantity.setTextSize(14);
    drug_quantity.setGravity(Gravity.LEFT);
    drug_quantity.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));
    drug_quantity.setText(Integer.toString(game_state_.dealer_drugs_.elementAt(drug_index)));

    drug_layout.addView(drug_name);
    drug_layout.addView(drug_quantity);

    return drug_layout;
  }

  // TODO: The coat buying dialogs are under development.
  private void PrepareInventoryDialog() {
    LinearLayout container = (LinearLayout)inventory_dialog_.findViewById(
        R.id.drug_list);
    container.removeAllViews();
    for (int i = 0; i < game_state_.dealer_drugs_.size(); ++i) {
      if (game_state_.dealer_drugs_.elementAt(i) > 0) {
        container.addView(ConstructInventoryDrugLayout(i));
      }
    }
  }

  // The dialogs available in the game include moving from place to place on the subway,
  // buying and selling drugs, looking at your inventory, the loan shark, and the bank.
  public static final int DIALOG_SUBWAY = 2002;
  public static final int DIALOG_DRUG_BUY = 2003;
  public static final int DIALOG_INVENTORY = 2004;
  public static final int DIALOG_DRUG_SELL = 2005;
  public static final int DIALOG_PAY_LOAN_SHARK = 2006;
  public static final int DIALOG_TAKE_LOAN_SHARK = 2007;
  public static final int DIALOG_BANK_DEPOSIT = 2008;
  public static final int DIALOG_BANK_WITHDRAW = 2009;
  public static final int DIALOG_END_GAME = 2010;
  public static final int DIALOG_HARDASS = 2011;
  public static final int DIALOG_BUY_COAT = 2012;
  public static final int DIALOG_BUY_GUN = 2013;

  private class CancelDialogListener implements View.OnClickListener {
    public CancelDialogListener(int dialog_id) {
      dialog_id_ = dialog_id;
    }
    public void onClick(View v) {
      dismissDialog(dialog_id_);
    }
    int dialog_id_;
  }

  private class BackToMainMenuListener implements View.OnClickListener {
    public void onClick(View v) {
      dismissDialog(DIALOG_END_GAME);
      Intent i = new Intent();
      setResult(RESULT_OK, i);
      finish();
    }
  }

  private class BuyCoatListener implements View.OnClickListener {
    public void onClick(View v) {
      game_state_.max_space_ += 10;
      game_state_.cash_ -= game_state_.coat_price_;
      game_state_.coat_price_ = 0;
      dismissDialog(DIALOG_BUY_COAT);
      refreshDisplay();
    }
  }

  // This is almost simple enough to be a CancelDialogListener but it needs
  // to zero out the coat cost or else the dialog will keep showing up every
  // time the display is refreshed.
  private class CancelBuyCoatListener implements View.OnClickListener {
    public void onClick(View v) {
      game_state_.coat_price_ = 0;
      dismissDialog(DIALOG_BUY_COAT);
      refreshDisplay();
    }
  }

  private class BuyGunListener implements View.OnClickListener {
    public void onClick(View v) {
      game_state_.max_space_ -= 8;
      game_state_.guns_ += 1;
      game_state_.cash_ -= game_state_.gun_price_;
      game_state_.gun_price_ = 0;
      dismissDialog(DIALOG_BUY_GUN);
      refreshDisplay();
    }
  }

  // This is almost simple enough to be a CancelDialogListener but it needs
  // to zero out the coat cost or else the dialog will keep showing up every
  // time the display is refreshed.
  private class CancelBuyGunListener implements View.OnClickListener {
    public void onClick(View v) {
      game_state_.gun_price_ = 0;
      dismissDialog(DIALOG_BUY_GUN);
      refreshDisplay();
    }
  }

  private class BuyDrugsClickListener implements View.OnClickListener {
    public BuyDrugsClickListener(int drug_index) {
      drug_index_ = drug_index;
    }
    public void onClick(View v) {
      dialog_drug_index_ = drug_index_;
      showDialog(DIALOG_DRUG_BUY);
    }
    int drug_index_;
  }

  private class SellDrugsClickListener implements View.OnClickListener {
    public SellDrugsClickListener(int drug_index) {
      drug_index_ = drug_index;
    }
    public void onClick(View v) {
      dialog_drug_index_ = drug_index_;
      showDialog(DIALOG_DRUG_SELL);
    }
    int drug_index_;
  }

  public class InventoryClickListener implements View.OnClickListener {
    public InventoryClickListener() {
    }
    public void onClick(View v) {
      showDialog(DIALOG_INVENTORY);
    }
  }

  public class SubwayClickListener implements View.OnClickListener {
    public SubwayClickListener() {
    }
    public void onClick(View v) {
      if (game_state_.days_left_ > 0) {
        showDialog(DIALOG_SUBWAY);
      } else {
        showDialog(DIALOG_END_GAME);
      }
    }
  }

  private class PayLoanSharkClickListener implements View.OnClickListener {
    public void onClick(View v) {
      showDialog(DIALOG_PAY_LOAN_SHARK);
    }
  }

  private class PayLoanSharkListener implements View.OnClickListener {
    public void onClick(View v) {
      int loan_payment_amount = Integer.parseInt(((TextView)pay_loan_shark_dialog_.findViewById(
          R.id.loan_amount)).getText().toString());
      game_state_.cash_ -= loan_payment_amount;
      game_state_.loan_ -= loan_payment_amount;
      refreshDisplay();
      dismissDialog(DIALOG_PAY_LOAN_SHARK);
    }
  }

  private class RefreshLoanListener implements View.OnClickListener {
    public void onClick(View v) {
      game_state_.cash_ += 2000;
      game_state_.loan_ += 5500;
      refreshDisplay();
      dismissDialog(DIALOG_PAY_LOAN_SHARK);
    }
  }

  private class BankDepositListener implements View.OnClickListener {
    public void onClick(View v) {
      showDialog(DIALOG_BANK_DEPOSIT);
    }
  }

  private class BankWithdrawListener implements View.OnClickListener {
    public void onClick(View v) {
      showDialog(DIALOG_BANK_WITHDRAW);
    }
  }

  private class BankDepositConfirmListener implements View.OnClickListener {
    public void onClick(View v) {
      int bank_deposit_amount = Integer.parseInt(((TextView)bank_deposit_dialog_.findViewById(
          R.id.bank_amount)).getText().toString());
      game_state_.cash_ -= bank_deposit_amount;
      game_state_.bank_ += bank_deposit_amount;
      refreshDisplay();
      dismissDialog(DIALOG_BANK_DEPOSIT);
    }
  }

  private class BankWithdrawConfirmListener implements View.OnClickListener {
    public void onClick(View v) {
      int bank_withdraw_amount = Integer.parseInt(((TextView)bank_withdraw_dialog_.findViewById(
          R.id.bank_amount)).getText().toString());
      game_state_.cash_ += bank_withdraw_amount;
      game_state_.bank_ -= bank_withdraw_amount;
      refreshDisplay();
      dismissDialog(DIALOG_BANK_WITHDRAW);
    }
  }

  private class BuyDrugsListener implements View.OnClickListener {
    public void onClick(View v) {
      int drug_price = game_state_.drug_price_.elementAt(dialog_drug_index_);
      int drug_quantity = Integer.parseInt(((TextView)drug_buy_dialog_.findViewById(
          R.id.drug_quantity)).getText().toString());
      game_state_.cash_ -= drug_quantity * drug_price;
      game_state_.max_space_ -= drug_quantity;
      game_state_.dealer_drugs_.setElementAt(
          game_state_.dealer_drugs_.elementAt(dialog_drug_index_) +
          drug_quantity, dialog_drug_index_);
      refreshDisplay();
      dismissDialog(DIALOG_DRUG_BUY);
    }
  }

  private class SellDrugsListener implements View.OnClickListener {
    public void onClick(View v) {
      int drug_price = game_state_.drug_price_.elementAt(dialog_drug_index_);
      int drug_quantity = Integer.parseInt(((TextView)drug_sell_dialog_.findViewById(
          R.id.drug_quantity)).getText().toString());
      game_state_.cash_ += drug_quantity * drug_price;
      game_state_.max_space_ += drug_quantity;
      game_state_.dealer_drugs_.setElementAt(
          game_state_.dealer_drugs_.elementAt(dialog_drug_index_) -
          drug_quantity, dialog_drug_index_);
      refreshDisplay();
      dismissDialog(DIALOG_DRUG_SELL);
    }
  }

  private GameState getGameState() {
    DealerDataAdapter dealer_data = new DealerDataAdapter(this);
    dealer_data.open();
    GameState game_state = new GameState(dealer_data.getGameString());
    dealer_data.close();
    return game_state;
  }

  private void saveGameState() {
    DealerDataAdapter dealer_data = new DealerDataAdapter(this);
    dealer_data.open();
    dealer_data.setGameString(game_state_.SerializeGame());
    dealer_data.close();
  }

  // TODO: this is very incomplete, because we haven't worked out what the
  // target layout is going to be, so for now, simplicity rules.
  private LinearLayout constructDrugLayout(int drug_index) {
    LinearLayout drug_layout = new LinearLayout(this);
    drug_layout.setOrientation(LinearLayout.HORIZONTAL);
    drug_layout.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.FILL_PARENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));

    TextView drug_name = new TextView(this);
    drug_name.setTextColor(Color.WHITE);
    drug_name.setTextSize(14);
    drug_name.setGravity(Gravity.LEFT);
    drug_name.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));
    drug_name.setText(game_state_.drugs_.elementAt(drug_index).drug_name_);

    TextView drug_price = new TextView(this);
    drug_price.setTextColor(Color.WHITE);
    drug_price.setTextSize(14);
    drug_price.setGravity(Gravity.LEFT);
    drug_price.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));
    drug_price.setText(Integer.toString(game_state_.drug_price_.elementAt(drug_index)));

    Button buy_button = new Button(this);
    buy_button.setTextSize(14);
    buy_button.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));
    buy_button.setText("Buy");
    buy_button.setOnClickListener(new BuyDrugsClickListener(drug_index));

    Button sell_button = new Button(this);
    sell_button.setTextSize(14);
    sell_button.setLayoutParams(new LinearLayout.LayoutParams(
        LinearLayout.LayoutParams.WRAP_CONTENT,
        LinearLayout.LayoutParams.WRAP_CONTENT));
    sell_button.setText("Sell");
    sell_button.setOnClickListener(new SellDrugsClickListener(drug_index));

    drug_layout.addView(drug_name);
    drug_layout.addView(drug_price);
    drug_layout.addView(buy_button);
    drug_layout.addView(sell_button);

    return drug_layout;
  }

  private void AddAllAvailableDrugs() {
    LinearLayout drug_layout = (LinearLayout)findViewById(R.id.drug_list);
    drug_layout.removeAllViews();
    for (int i = 0; i < game_state_.drugs_.size(); ++i) {
      if (game_state_.drug_price_.elementAt(i) > 0) {
        drug_layout.addView(constructDrugLayout(i));
      }
    }
  }

  private void refreshDisplay() {
    ((TextView)findViewById(R.id.location_name)).setText(
        game_state_.locations_.elementAt(game_state_.location_).
        location_name_);

    AddAllAvailableDrugs();

    ((TextView)findViewById(R.id.dealer_cash)).setText(
        "c: " + Integer.toString(game_state_.cash_));
    ((TextView)findViewById(R.id.dealer_bank)).setText(
        "b: " + Integer.toString(game_state_.bank_));
    ((TextView)findViewById(R.id.dealer_loan)).setText(
        "l: " + Integer.toString(game_state_.loan_));
    ((TextView)findViewById(R.id.dealer_health)).setText(
        "h: " + Integer.toString(game_state_.health_));
    ((TextView)findViewById(R.id.dealer_space)).setText(
        "s: " + Integer.toString(game_state_.max_space_));
    ((TextView)findViewById(R.id.dealer_guns)).setText(
        "g: " + Integer.toString(game_state_.guns_));
    ((TextView)findViewById(R.id.dealer_days_left)).setText(
        "d: " + Integer.toString(game_state_.days_left_));

    // Hide the bank and loan shark if not in Brooklyn.
    ((LinearLayout)findViewById(R.id.bank_row)).setVisibility(
        game_state_.location_ == 0 ? View.VISIBLE : View.GONE);
    ((Button)findViewById(R.id.loan_shark_button)).setVisibility(
        game_state_.location_ == 0 ? View.VISIBLE : View.GONE);

    // Check for the end of the game.
    if (game_state_.days_left_ <= 0) {
      ((Button)findViewById(R.id.subway_button)).setText("End Game");
      // TODO: change the listener to something that ends the game
    }

    // Check for officer hardass.  If he's present, then bring up the
    // fight or flight dialog.
    if (game_state_.hardass_health_ > 0) {
      showDialog(DIALOG_HARDASS);
    } else if (game_state_.coat_price_ > 0) {
      showDialog(DIALOG_BUY_COAT);
    } else if (game_state_.gun_price_ > 0) {
      showDialog(DIALOG_BUY_GUN);
    }
  }

  Dialog subway_dialog_;
  Dialog drug_buy_dialog_;
  Dialog drug_sell_dialog_;
  Dialog inventory_dialog_;
  Dialog loan_shark_dialog_;
  Dialog bank_deposit_dialog_;
  Dialog bank_withdraw_dialog_;
  Dialog end_game_dialog_;
  Dialog pay_loan_shark_dialog_;
  Dialog hardass_dialog_;
  Dialog buy_coat_dialog_;
  Dialog buy_gun_dialog_;

  String dialog_drug_name_;
  int dialog_drug_index_;

  GameState game_state_;

  int viewWidth_;
  int viewHeight_;
  LinearLayout outer_layout_;
  LinearLayout current_row_;
  int total_width_added_;

  int currently_selected_length_;
  int currently_selected_game_type_;

  // Random number generator for this activity.
  public static Random rand_gen_ = new Random();
}
