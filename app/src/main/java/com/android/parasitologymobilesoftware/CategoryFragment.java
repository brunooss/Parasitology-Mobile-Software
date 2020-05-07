package com.android.parasitologymobilesoftware;

import android.content.Context;
import android.os.Bundle;
import android.text.Html;
import android.util.Log;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;
import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.fragment.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import com.android.MyApplication;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.SetOptions;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.Map;

public class CategoryFragment extends Fragment {

    static InputStream inputStream;
    private WebView webViewText;

    private static Button buttonNext;
    private static Button buttonPrevious;

    private FirebaseFirestore dataBase;
    private FirebaseAuth firebaseAuth;

    private String email;

    private static String categoryParent = null;
    private static int nextCategoryId = 0, concludeProgress = 0;

    private static boolean categoryStatus;

    private int progressApp;
    private String databaseCategoryProgress;

    public static String category = "notNull";
    public static int categoryId;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable final ViewGroup container, @Nullable Bundle savedInstanceState) {

        dataBase = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();

        email = firebaseAuth.getCurrentUser().getEmail();

        View rootView = inflater.inflate(R.layout.fragment_category, container, false);
        TextView textViewTitle = rootView.findViewById(R.id.textViewCategoryTitle);
        webViewText = rootView.findViewById(R.id.webViewCategoryText);
        ImageView imageView = rootView.findViewById(R.id.imageViewCategoryImage);
        buttonNext = rootView.findViewById(R.id.buttonCategoryGroup).findViewById(R.id.buttonCategoryNext2);
        buttonPrevious = rootView.findViewById(R.id.buttonCategoryGroup).findViewById(R.id.buttonCategoryPrevious2);

        if (getArguments().getString("type") != null) {
            if(getArguments().getString("type").equals("textAndImage")) {
                textViewTitle.setText(Html.fromHtml(this.getArguments().getString("title"), Html.FROM_HTML_MODE_COMPACT));
                webViewText.loadData("<body><style>* { text-align: justify; }</style>" + this.getArguments().getString("text") + "</body>", "text/html", "UTF-8");
                webViewText.scrollTo(0, 0);
                imageView.setImageResource(getResources().getIdentifier(getArguments().getString("imageAddress"), "drawable", getContext().getPackageName()));

            } else if (getArguments().getString("type").equals("text")) {
                textViewTitle.setText(Html.fromHtml(this.getArguments().getString("title"), Html.FROM_HTML_MODE_COMPACT));
                webViewText.loadData("<body><style>* { text-align: justify; }</style>" + this.getArguments().getString("text") + "</body>", "text/html", "UTF-8");
                webViewText.scrollTo(0, 0);
                imageView.setVisibility(ImageView.INVISIBLE);
            }
        }

        if (getArguments().getInt("position") == 0) {
            buttonPrevious.setBackgroundResource(R.drawable.custom_button_category_gray);
        }
        if (getArguments().getInt("number") == (getArguments().getInt("position") + 1)) {
            buttonNext.setBackgroundResource(R.drawable.custom_button_category_yellow);
            buttonNext.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    nextCategoryId = getArguments().getInt("nextCategoryId");
                    categoryParent = getArguments().getString("parentCategory");
                    setConcludeProgress(getArguments().getInt("concludeProgress"));
//                    CategoryActivity categoryActivity = (CategoryActivity) getActivity();
//                    HomeActivity homeActivity = new HomeActivity();
//                    for(int i = 0; i < homeActivity.categoriesIds.length; i++) {
//                        if(homeActivity.categoriesIds[i] == categoryActivity.categoryId) {
//                            HomeFragment homeFragment = new HomeFragment();
//                            homeFragment.updateId(homeActivity.categoriesIds[i + 1]);
//                        }
//                    }

                    dataBase.collection("generalUserInfo").document(email).collection("specific info").document("progress categories")
                            .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                        @Override
                        public void onSuccess(DocumentSnapshot documentSnapshot) {
                            setCategoryStatus(documentSnapshot.getBoolean(category));

                            Log.i("CategoryFragment", "solicitou status da categoria do banco de dados");

                            Log.d("CategoryFragment", "Status da categoria: " + categoryStatus);

                            if (categoryStatus == false) {

                                dataBase.collection("generalUserInfo").document(email).collection("specific info").document("progress")
                                        .get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                                    @Override
                                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                                        Log.d("CategoryFragment", ""+getConcludeProgress());
                                        switch (categoryParent) {
                                            case "Introdução":
                                                //setConcludeProgress(documentSnapshot.getLong("progress introduction").intValue());
                                                databaseCategoryProgress = "progress introduction";
                                                progressApp = concludeProgress / 4;
                                                break;
                                            case "Protozoários":
                                                //setConcludeProgress(documentSnapshot.getLong("progress protozoarios").intValue());
                                                databaseCategoryProgress = "progress protozoarios";
                                                progressApp = 25 + concludeProgress / 4;
                                                break;
                                            case "Helmintos":
                                                //setConcludeProgress(documentSnapshot.getLong("progress helmintos").intValue());
                                                databaseCategoryProgress = "progress helmintos";
                                                progressApp = 50 + concludeProgress / 4;
                                                break;
                                            case "Artrópodes":
                                                //setConcludeProgress(documentSnapshot.getLong("progress status").intValue());
                                                databaseCategoryProgress = "progress artropodes";
                                                progressApp = 75 + concludeProgress / 4;
                                                break;
                                        }
                                        Log.d("CategoryFragment", "Progresso do app: "+progressApp);
                                        Map<String, Object> newProgressInfo = new HashMap<>();
                                        newProgressInfo.put(databaseCategoryProgress, concludeProgress);
                                        newProgressInfo.put("progress status", progressApp);
                                        dataBase.collection("generalUserInfo").document(email).collection("specific info").document("progress")
                                                .update(newProgressInfo);
                                    }
                                });
                                Log.d("CategoryFragment", "Category's status: " + categoryStatus);
                                Log.d("CategoryFragment", "Next Category's id received here: " + nextCategoryId);
                                Log.d("CategoryFragment", "Category's parent received here: " + categoryParent);
                                Log.d("CategoryFragment", "O progresso concluído na categoria é: " + concludeProgress);
                            } else {
                                Log.i("CategoryFragment", "Esta categoria já está feita");
                            }
                        }
                    });
                    getActivity().finish();
                }
            });
            buttonNext.setText("Completar");
        }
        return rootView;
    }


    public static CategoryFragment newInstance(int position) {
        Bundle args = new Bundle();
        HomeActivity activity = new HomeActivity();

        category = activity.getCategory();
        categoryId = activity.getCategoryId();


        try {
            inputStream = MyApplication.getMyApplicationContext().getAssets().open("fragments_settings.json");
            int size = inputStream.available();
            byte[] buffer = new byte[size];
            inputStream.read(buffer);
            inputStream.close();
            String JSON = new String(buffer, "UTF-8");
            JSONArray jsonArray = new JSONArray(JSON);


            for(int i = 0; i < jsonArray.length(); i++) {
                JSONObject jsonObject = jsonArray.getJSONObject(i);

                if(jsonObject.getString("categoryName").equals(category) && !jsonObject.get("numberOfTabs").equals(position)) {
                    JSONObject tab = jsonObject.getJSONArray("tabs").getJSONObject(position);
                    args.putString("title", tab.getString("title"));
                    args.putString("type", tab.getString("type"));
                    args.putString("text", tab.getString("text"));
                    args.putInt("number", jsonObject.getInt("numberOfTabs"));
                    args.putString("parentCategory", jsonObject.getString("parentCategory"));
                    args.putInt("nextCategoryId", jsonObject.getInt("nextCategoryId"));
                    args.putInt("concludeProgress", jsonObject.getInt("concludeProgress"));

                    try {
                        tab.get("imageAddress");
                        args.putString("imageAddress", tab.getString("imageAddress"));
                    } catch (JSONException ignored) { }

                }
            }


        } catch (IOException e) {
            e.printStackTrace();
            Toast.makeText(MyApplication.getMyApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();

        } catch (JSONException e) {
            e.printStackTrace();
            Toast.makeText(MyApplication.getMyApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
        }

        args.putInt("position", position);

        CategoryFragment fragment = new CategoryFragment();
        fragment.setArguments(args);
        return fragment;
    }

    public void updateWebViewSearch(String s) {
        this.webViewText.findAll(s);
    }
    public void updateWebViewSearchSelectWord(String s) {
        this.webViewText.findAll(s);
        this.webViewText.findNext(true);
    }
    public void updateWebViewClearMatches() {
        this.webViewText.clearMatches();
    }

    public void setCategoryStatus(boolean categoryStatus) {
        this.categoryStatus = categoryStatus;
    }

    public void setConcludeProgress(int concludeProgress) {
        this.concludeProgress = concludeProgress;
    }

    public String getCategoryParent() {
        return categoryParent;
    }

    public int getNextCategoryId() {
        return nextCategoryId;
    }

    public int getConcludeProgress() {
        return concludeProgress;
    }

    public boolean isCategoryStatus() {
        return categoryStatus;
    }

    public static String getCategory() {
        return category;
    }

    public int getProgressApp() {
        return progressApp;
    }

    public void setProgressApp(int progressApp) {
        this.progressApp = progressApp;
    }
}
