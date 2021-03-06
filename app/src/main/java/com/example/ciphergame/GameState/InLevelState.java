package com.example.ciphergame.GameState;

import android.graphics.Color;
import android.text.Html;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import com.example.ciphergame.Views.*;
import com.example.ciphergame.Cipher;
import com.example.ciphergame.R;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;

public class InLevelState extends GameState implements View.OnClickListener {

    /*
        TODO add currencies, like lives and coins, which can be used for hints or answers
     */

    private static final String WHITE = "<font color=#FFFFFF>";
    private static final String RED = "<font color=#FF0000>";
    private static final String FONT = "</font>";

    private TextView text;
    private Button[] letters; // the bottom letters
    private Button[] cipherLetters; // the top letters
    private int selectedLetter = -1;

    private int level;
    private int textPack;
    private Cipher cipher;
    private String hintCipherText;
    private String cipherText;
    private String curCipherText;

    private boolean instructionsOpen;

    InLevelState(GameStateManager gsm) {
        super(gsm);
    }

    public void init() {
        setContentView(R.layout.in_level_state);

        AdView adView = getView(R.id.in_level_ad);
        AdRequest adRequest = new AdRequest.Builder().addTestDevice("F7C1A666D29DEF8F4F05EED1EAC2E8E0").build();
        adView.loadAd(adRequest);

        BackButton backButton = getView(R.id.back_button);
        backButton.init(gsm);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (instructionsOpen) openInstructions();
                else gsm.setState(gsm.getPrevState());
            }
        });
        ((VolumeButton) getView(R.id.volume_button)).init(gsm, ViewHelper.TOP_RIGHT);

        Button reset = getView(R.id.reset_answer);
        Button hint = getView(R.id.hint_button);
        Button lives = getView(R.id.lives_button);
        reset.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                resetAnswer();
            }
        });
        hint.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { gsm.setState(GameStateManager.HINTSTATE); }
        });
        lives.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) { gsm.setState(GameStateManager.CURRENCYSTATE); }
        });
        for (Button button : new Button[] { reset, hint, lives }) {
            button.setBackgroundColor(Color.TRANSPARENT);
            ViewHelper.setWidthAndHeightAsPercentOfScreen(button, 30, 5);
        }

        cipher = new Cipher(getApp().getApplicationContext());
        cipherText = withoutHtml(gsm.getData().getString("cipherText", cipher.getText(textPack, level)));
        gsm.getDataEditor().putString("cipherText", cipherText).apply();
        hintCipherText = gsm.getData().getString("hintCipherText", cipherText);
        curCipherText = gsm.getData().getString("curCipherText", cipherText);

        ViewHelper.setWidthAsPercentOfScreen(getView(R.id.scrollViewTop), 94);
        ViewHelper.setWidthAsPercentOfScreen(getView(R.id.scrollViewBottom), 94);

        // TODO make this more efficient
        letters = new Button[] { getView(R.id.a), getView(R.id.b),
                getView(R.id.c), getView(R.id.d), getView(R.id.e), getView(R.id.f),
                getView(R.id.g), getView(R.id.h), getView(R.id.i), getView(R.id.j),
                getView(R.id.k), getView(R.id.l), getView(R.id.m), getView(R.id.n),
                getView(R.id.o), getView(R.id.p), getView(R.id.q), getView(R.id.r),
                getView(R.id.s), getView(R.id.t), getView(R.id.u), getView(R.id.v),
                getView(R.id.w), getView(R.id.x), getView(R.id.y), getView(R.id.z) };
        cipherLetters = new Button[] { getView(R.id.a_text), getView(R.id.b_text),
                getView(R.id.c_text), getView(R.id.d_text), getView(R.id.e_text), getView(R.id.f_text),
                getView(R.id.g_text), getView(R.id.h_text), getView(R.id.i_text), getView(R.id.j_text),
                getView(R.id.k_text), getView(R.id.l_text), getView(R.id.m_text), getView(R.id.n_text),
                getView(R.id.o_text), getView(R.id.p_text), getView(R.id.q_text), getView(R.id.r_text),
                getView(R.id.s_text), getView(R.id.t_text), getView(R.id.u_text), getView(R.id.v_text),
                getView(R.id.w_text), getView(R.id.x_text), getView(R.id.y_text), getView(R.id.z_text) };
        for (Button button : letters) {
            ViewHelper.setPaddingBottomAsPercentOfScreen(button, 0.25);
            ViewHelper.setWidthAsPercentOfScreen(button, 10);
            ViewHelper.makeSquareWithWidth(button);
        }
        for (Button button : cipherLetters) {
            ViewHelper.setPaddingBottomAsPercentOfScreen(button, 0.25);
            ViewHelper.setWidthAsPercentOfScreen(button, 10);
            ViewHelper.makeSquareWithWidth(button);
        }
        resetText();

        text = getView(R.id.in_level_text);
        text.setText(Html.fromHtml(curCipherText, 1));
        text.setTextSize((cipherText.length() >= 250) ? (float) (20 - ((cipherText.length() - 250) / 50.0)) : 20);

        Button instructions = getView(R.id.instructions);
        instructions.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openInstructions();
            }
        });
        ViewHelper.setGetBiggerTouchListener(instructions);
        ViewHelper.setMarginTopAsPercentOfScreen(instructions, 1);

        ViewHelper.setPaddingTopAsPercentOfScreen(getView(R.id.instructions_text), 20);

        instructionsOpen = false;
    }

    private void openInstructions() {
        instructionsOpen = !instructionsOpen;
        getView(R.id.instructions_text).setVisibility((instructionsOpen) ? View.VISIBLE : View.INVISIBLE);
        getView(R.id.volume_button).setVisibility((instructionsOpen) ? View.INVISIBLE : View.VISIBLE);
        getView(R.id.instructions).setVisibility((instructionsOpen) ? View.INVISIBLE : View.VISIBLE);
    }

    // TODO fix this
    private void resetAnswer() {
        selectedLetter = -1;
        curCipherText = hintCipherText;
        gsm.getDataEditor().putString("curCipherText", curCipherText).apply();
        changeTextColor(-1);
        resetText();
        resetCipherLetters();
    }

    private void resetCipherLetters() {
        String regularText = cipher.getRegularText(textPack, level).toUpperCase();
        for (int i = 0; i < 26; i++) {
            cipherLetters[i].setText(regularText.contains("" + (cipher.getCipherAlphabet()[i])) ? gsm.getData().getString("cipherLetter" + i, "") : "-");
        }
    }

    // TODO fix this
    private void resetText() {
        String regularText = cipher.getRegularText(textPack, level).toUpperCase();
        int color = getApp().getApplicationContext().getColor(R.color.opaqueBlack);
        for (int i = 0; i < 26; i++) {
            letters[i].setOnClickListener(this);
            letters[i].setBackgroundResource(R.drawable.circle);
            letters[i].setText(cipherText.contains("" + (char) (i + Cipher.UPPER_CASE_START)) ? "" + (char) (i + Cipher.UPPER_CASE_START) : "-");
        }
        for (int i = 0; i < 26; i++) {
            cipherLetters[i].setOnClickListener(this);
            cipherLetters[i].setBackgroundResource(R.drawable.circle);
            cipherLetters[i].setTextColor(color);
            if (regularText.contains("" + cipher.getCipherAlphabet()[i]))
                for (int j = 0; j < cipherText.length(); j++)
                    if (withoutHtml(curCipherText).charAt(j) == (char) (i + Cipher.LOWER_CASE_START)) {
                        cipherLetters[i].setText("" + cipherText.charAt(j));
                        break;
                    }
        }
    }

    private void checkAnswer() {
        if (withoutHtml(curCipherText).toLowerCase().equals(cipher.getRegularText(textPack, level))) {
            text.setText("You won");
            currencies.levelComplete();
//            level++;
//            gsm.setState(GameStateManager.INLEVELSTATE);
        }
    }

    private void changeTextColor(int letter) {
        // changes the selected letters to red
        char realLetter = (char) (letter + Cipher.UPPER_CASE_START);
        char curLetter = realLetter;
        for (int i = 0; i < cipherText.length(); i++)
            if (cipherText.charAt(i) == realLetter) {
                curLetter = withoutHtml(curCipherText).charAt(i);
                break;
            }
        if (letter != -1)
            curCipherText = curCipherText.replace(WHITE + curLetter + FONT, "" + realLetter)
                    .replace(WHITE, "@").replace(FONT, "*")
                    .replace("" + realLetter, RED + realLetter + FONT)
                    .replace("@", WHITE).replace("*", FONT);
        else {
            String s = curCipherText;
            for (int i = 0; i < cipherLetters.length; i++)
                if (!cipherLetters[i].getText().equals("" + (char) (i + Cipher.LOWER_CASE_START)) && isRed(firstLetterOf(cipherLetters[i])))
                    curCipherText = curCipherText.replace(RED + cipherLetters[i].getText(), WHITE + (char) (i + Cipher.LOWER_CASE_START));
            if (s.equals(curCipherText))
                curCipherText = curCipherText.replace(RED, "");
        }
        text.setText(Html.fromHtml(curCipherText, 1));
    }

    private void changeText(int oldLetter, int newLetter) {
        curCipherText = curCipherText.replace(RED + (char) (oldLetter + Cipher.UPPER_CASE_START),
                WHITE + (char) (newLetter + Cipher.LOWER_CASE_START));
        text.setText(Html.fromHtml(curCipherText, 1));
        gsm.getDataEditor().putString("curCipherText", curCipherText).apply();
        checkAnswer();
    }

    private void removeLetter(int index) {
        // deselects a given cipherLetter and assumes the cipherLetter has already been selected
        curCipherText = curCipherText.replace(WHITE + (char) (index + Cipher.LOWER_CASE_START) + FONT, firstLetterOf(cipherLetters[index]) + "");
        gsm.getDataEditor().putString("curCipherText", curCipherText).apply();
        text.setText(curCipherText);
    }

    private void normalizeText(int index) {
        // makes text of the cipher letter of the index what it should be
        curCipherText = curCipherText.replace(WHITE + (char) (index + Cipher.LOWER_CASE_START) + FONT, cipherLetters[index].getText());
    }

    private char[] getCurAlphabet() {
        char[] alphabet = new char[26];
        for (int i = 0; i < cipherLetters.length; i++)
            alphabet[i] = Character.toLowerCase(firstLetterOf(cipherLetters[i]) == '!' ? (char) (i + Cipher.LOWER_CASE_START) : firstLetterOf(cipherLetters[i]));
        return alphabet;
    }
    private boolean lettersContain(int letter) {
        for (int i = 0; i < 26; i++)
            if (letters[i].getText().equals("" + (char) (letter + Cipher.UPPER_CASE_START)))
                return true;
        return false;
    }

    public void peek() {
        char[] cipherAlphabet = cipher.getCipherAlphabet();
        char[] curCipherAlphabet = getCurAlphabet();
        boolean[] canCorrect;
        int randChoice;

        // find which letters can be swapped
        canCorrect = new boolean[26];
        for (int i = 0; i < 26; i++)
            canCorrect[i] = cipherAlphabet[i] != curCipherAlphabet[i] && firstLetterOf(cipherLetters[i]) != '-' && lettersContain(i);

        // pick a random letter to switch
        do randChoice = (int) (Math.random() * 25);
        while (!canCorrect[randChoice]);

        System.out.println(randChoice);

        changeTextColor(cipherAlphabet[randChoice] - Cipher.UPPER_CASE_START);
        changeText(cipherAlphabet[randChoice] - Cipher.UPPER_CASE_START, randChoice);
        cipherLetters[randChoice].setText("" + cipherAlphabet[randChoice]);
        hintCipherText = hintCipherText.replace("" + cipherAlphabet[randChoice], WHITE + (char) (randChoice + Cipher.LOWER_CASE_START) + FONT);
        gsm.getDataEditor().putString("hintCipherText", hintCipherText).putString("cipherLetter" + randChoice, "" + cipherAlphabet[randChoice]).apply();
    }

    @Override
    public void onClick(View view) {
        for (int i = 0; i < letters.length; i++)
            if (view.getId() == letters[i].getId() && !letters[i].getText().equals("-")) {
                if (selectedLetter == i)
                    letters[selectedLetter].setBackgroundResource(R.drawable.circle);
                else {
                    if (letterSelected()) {
                        letters[selectedLetter].setBackgroundResource(R.drawable.circle);
                        changeTextColor(-1);
                    }
                    letters[i].setBackgroundResource(R.drawable.green_circle);
                }
                selectedLetter = (selectedLetter == i) ? -1 : i;
                changeTextColor(selectedLetter);
            }
        // TODO add removable letters
        for (int i = 0; i < cipherLetters.length; i++)
            if (view.getId() == cipherLetters[i].getId() && !cipherLetters[i].getText().equals("-")) {
                if (letterSelected() && !hintsContain(selectedLetter)) {
                    if (isDifferentThanNormal(i))
                        normalizeText(i);
                    changeText(selectedLetter, i);
                    letters[selectedLetter].setBackgroundResource(R.drawable.circle);
                    cipherLetters[i].setText("" + (char) (selectedLetter + Cipher.UPPER_CASE_START));
                    for (int j = 0; j < cipherLetters.length; j++)
                        if (cipherLetters[j].getText().equals("" + (char) (selectedLetter + Cipher.UPPER_CASE_START)) && j != i) {
                            cipherLetters[j].setText("");
                            break;
                        }
                    selectedLetter = -1;
                } else if (firstLetterOf(cipherLetters[i]) != '!' && gsm.getData().getString("cipherLetter" + i, "").equals("")) {
                    removeLetter(i);
                    cipherLetters[i].setText("");
                }
            }
    }

    private boolean letterSelected() { return selectedLetter != -1; }
    private boolean isDifferentThanNormal(int index) { return !cipherLetters[index].getText().equals("" + (char) (index + Cipher.LOWER_CASE_START)); }
    private char firstLetterOf(Button button) { return (button.getText().length() > 0) ? button.getText().charAt(0) : '!'; }

    private String withoutHtml(String s) { return s.replace(FONT, "").replace(RED, "").replace(WHITE, ""); }
    private boolean isRed(int letter) { return curCipherText.contains(RED + (char) letter); }
    private boolean hintsContain(int letter) {
        for (int i = 0; i < 26; i++)
            if (gsm.getData().getString("cipherLetter" + i, "").equals("" + (char) (letter + Cipher.UPPER_CASE_START)))
                return true;
        return false;
    }

    void setLevelNumber(int number) { level = number; }
    void setTextPack(int textPack) { this.textPack = textPack; }

}
