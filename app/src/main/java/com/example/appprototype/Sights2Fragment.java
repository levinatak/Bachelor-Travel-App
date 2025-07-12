package com.example.appprototype;

import android.app.AlertDialog;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.Observer;
import androidx.lifecycle.ViewModelProvider;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Locale;

/**
 * A simple {@link Fragment} subclass.
 * Use the {@link Sights2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Sights2Fragment extends Fragment
{
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;
    private LinearLayout layoutList;
    private ImageButton btnAdd;
    private ArrayList<String[]> genSightList = new ArrayList<>();

    public Sights2Fragment() {
        // Required empty public constructor
    }

    public static Sights2Fragment newInstance(String param1, String param2)
    {
        Sights2Fragment fragment = new Sights2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState)
    {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_sights2, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState)
    {
        layoutList = view.findViewById(R.id.layout_list);
        btnAdd = view.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> {
            try {
                addView();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button btnSights3 = view.findViewById(R.id.btnSights3);
        btnSights3.setOnClickListener(v -> proceedToNextFragment());
    }

    private void proceedToNextFragment() {
        StringArrayViewModel modelArray = new ViewModelProvider(requireActivity()).get(StringArrayViewModel.class);
        modelArray.setList("sgtList", genSightList);

        Sights3Fragment sgt3Frag = new Sights3Fragment();
        FragmentManager fragMan = getFragmentManager();
        if (fragMan != null) {
            fragMan.beginTransaction()
                    .replace(R.id.frame_layout, sgt3Frag)
                    .commit();
        }
    }

    private void addView() throws IOException {
        Log.d("API LOG", "Start adding view");

        View sightView = getLayoutInflater().inflate(R.layout.row_add, null, false);
        EditText tfSgtElem = sightView.findViewById(R.id.title_elem);
        ImageView imgSearch = sightView.findViewById(R.id.img_search);
        ImageView imgRemove = sightView.findViewById(R.id.img_remove);

        sightView.setTag(genSightList.size());
        tfSgtElem.setHint("Sehenswürdigkeit");

        imgRemove.setOnClickListener(v -> removeView(sightView));
        imgSearch.setOnClickListener(v -> searchEvent(sightView, tfSgtElem, imgSearch));

        layoutList.addView(sightView);
        Log.d("API LOG", "View added to layoutList");
    }

    private void removeView(View v) {
        int position = (int) v.getTag();
        if (position < genSightList.size()) {
            genSightList.remove(position);
        }
        layoutList.removeView(v);

        updateViewTags();
    }

    private void updateViewTags() {
        for (int i = 0; i < layoutList.getChildCount(); i++) {
            View childView = layoutList.getChildAt(i);
            childView.setTag(i);
        }
    }

    private void searchEvent(View v, EditText tfSgtElem, ImageView imgSearch) {
        //EditText tfSgtElem = v.findViewById(R.id.title_elem);
        //ImageView imgSearch = v.findViewById(R.id.img_search);
        String sgtString = tfSgtElem.getText().toString();

        String promptText = buildPromptText(sgtString);
        GeminiViewModel gemViewModel = new ViewModelProvider(requireActivity()).get(GeminiViewModel.class);

        gemViewModel.clearResult();

        Observer<GeminiViewModel.LoadState> loadStateObserver = getLoadStateObserver(gemViewModel);
        Observer<String> resultObserver = getResultObserver(tfSgtElem, imgSearch, gemViewModel);

        gemViewModel.getLoadState().observe(getViewLifecycleOwner(), loadStateObserver);
        gemViewModel.getResult().observe(getViewLifecycleOwner(), resultObserver);

        Log.d("API LOG", "Starting fetchGeminiResponse");
        gemViewModel.fetchGeminiResponse(promptText);
    }

    private String buildPromptText(String sgtString) {
        return "Du bist Reiseberater, deine Aufgabe ist es beliebte Sehenswuerdigkeiten auf Basis einer Suchanfrage auszugeben " +
                "[Natuerliche (Straende, Hoehlen, Felswaende, Fluesse, Seen, Waelder, etc), " +
                "Kulturelle (Kirchen, Anwesen, hisstorische Gebaeude, archaeologische Staetten, historische Gaerten, Denkmaeler, etc), " +
                "Unterhaltung (Clubanlagen, Heilbaeder, Museen & Galerien, Vergnuegungsparks, etc), etc].\n" +
                "Ich gebe dir eine Suchanfrage und du gibst mir passende Sehenswuerdigkeiten dazu aus.\n" +
                "Hier ist eine Aufzaehlung an Bedingungen, die du dabei beachten sollst:\n" +
                "1. Beachte Sehenswuerdigkeiten auf der ganzen Welt, aber gib Sehenswuerdigkeiten in Deutschland und Umland eine hoehere Prioritaet.\n" +
                "2.a) Gib 1 Sehenswuerdigkeit aus, wenn die Anfrage eindeutig ist.\n" +
                "2.b) Gib bis zu 5 Sehenswuerdigkeiten aus, wenn auch andere Sehenswuerdigkeiten mit der Anfrage gemeint sein koennten.\n" +
                "3. Sortiere die Treffer nach der Reihenfolge der Prioritaet, Beliebtheit der Sehenswuerdigkeit, und Genauigkeit der Suchanfrage.\n" +
                "4. Mache die Ausgaben nach diesem Schema:\n" +
                "a) Fuer 1 Ausgabe: 'Sehenswuerdigkeit-Titel / Ort'\n" +
                "b) Fuer mehrere Ausgaben: 'Sehenswuerdigkeit-Titel1 / Ort1; Sehenswuerdigkeit-Titel2 / Ort2; etc.'\n" +
                "5. Falls gar nichts gefunden wird, antworte mit 'keine Ergebnisse'.\n" +
                "6. Frag nicht nach mehr Kontext!\n" +
                "Die Suchanfrage fuer die Ausgabe lautet: '" + sgtString + "'";
    }

    private Observer<GeminiViewModel.LoadState> getLoadStateObserver(GeminiViewModel gemViewModel) {
        return new Observer<GeminiViewModel.LoadState>() {
            @Override
            public void onChanged(GeminiViewModel.LoadState state) {
                if (state == GeminiViewModel.LoadState.LOADED) {
                    Log.d("API LOG", "Daten erfolgreich geladen");
                    gemViewModel.getLoadState().removeObserver(this);
                } else if (state == GeminiViewModel.LoadState.FAILED) {
                    Log.d("API LOG", "Fehler beim Laden der Daten");
                    gemViewModel.getLoadState().removeObserver(this);
                }
            }
        };
    }

    private Observer<String> getResultObserver(EditText tfSgtElem, ImageView imgSearch, GeminiViewModel gemViewModel) {
        return new Observer<String>() {
            @Override
            public void onChanged(String resultString) {
                if (resultString != null && layoutList.getChildCount() > 0) {
                    Log.d("API LOG", "Daten zur Anzeige bereit");
                    String[] options = resultString.split("; ");
                    showChoiceDialog(options, result -> {
                        Log.d("API LOG", "TF Result: " + result);
                        if (!result.equals("")) {
                            tfSgtElem.setText(result);
                            tfSgtElem.setFocusable(false);

                            imgSearch.setEnabled(false);
                            imgSearch.setAlpha(0.5f);
                        }
                        gemViewModel.getResult().removeObserver(this);
                    });
                }
            }
        };
    }

    private void showChoiceDialog(String[] options, Sights2Fragment.ResultHandler resultHandler) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Wähle eine Option");

        final int[] chosenIndex = {-1};

        builder.setSingleChoiceItems(options, -1, (dialogInterface, which) -> {
            chosenIndex[0] = which;
            AlertDialog dialog = (AlertDialog) dialogInterface;
            Button okButton = dialog.getButton(AlertDialog.BUTTON_POSITIVE);
            if (okButton != null) {
                okButton.setEnabled(true);
            }
        });

        builder.setPositiveButton("OK", (dialogInterface, which) -> {
            if (chosenIndex[0] != -1) {
                String selectedResult = options[chosenIndex[0]];
                Log.d("API LOG", "Choice Result: " + selectedResult);
                String[] evtPlaceDate = selectedResult.split("/");

                if (evtPlaceDate.length == 2) {
                    Log.d("API LOG", "Sight: " + evtPlaceDate[0]);
                    Log.d("API LOG", "Ort: " + evtPlaceDate[1]);

                    for (int i = 0; i < evtPlaceDate.length; i++) {
                        evtPlaceDate[i] = " " + evtPlaceDate[i].trim() + " ";
                    }

                    resultHandler.handle(evtPlaceDate[0]);
                    genSightList.add(evtPlaceDate);

                } else {
                    Log.e("API LOG", "Sight: " + selectedResult);
                    resultHandler.handle("");
                }
                dialogInterface.dismiss();
            }
        });

        AlertDialog dialog = builder.create();
        dialog.show();
        dialog.getButton(AlertDialog.BUTTON_POSITIVE).setEnabled(false);
    }

    public interface ResultHandler {
        void handle(String result);
    }

    public String getCurrentDate() {
        SimpleDateFormat formatter = new SimpleDateFormat("dd.MM.yyyy", Locale.GERMANY);
        return formatter.format(new Date());
    }
}