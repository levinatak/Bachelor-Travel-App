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
 * Use the {@link Events2Fragment#newInstance} factory method to
 * create an instance of this fragment.
 */
public class Events2Fragment extends Fragment {

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    private String mParam1;
    private String mParam2;

    private View view;
    private LinearLayout layoutList;
    private ImageButton btnAdd;
    private ArrayList<String[]> genEvtList = new ArrayList<>();

    public Events2Fragment() {
        // Required empty public constructor
    }

    public static Events2Fragment newInstance(String param1, String param2) {
        Events2Fragment fragment = new Events2Fragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        view = inflater.inflate(R.layout.fragment_events2, container, false);
        return view;
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        layoutList = view.findViewById(R.id.layout_list);
        btnAdd = view.findViewById(R.id.btnAdd);

        btnAdd.setOnClickListener(v -> {
            try {
                addView();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        });

        Button btnEvt3 = view.findViewById(R.id.btnEvt3);
        btnEvt3.setOnClickListener(v -> proceedToNextFragment());
    }

    private void proceedToNextFragment() {
        StringArrayViewModel modelArray = new ViewModelProvider(requireActivity()).get(StringArrayViewModel.class);
        modelArray.setList("evtList", genEvtList);

        Events3Fragment evt3Frag = new Events3Fragment();
        FragmentManager fragMan = getFragmentManager();
        if (fragMan != null) {
            fragMan.beginTransaction()
                    .replace(R.id.frame_layout, evt3Frag)
                    .commit();
        }
    }

    private void addView() throws IOException {
        Log.d("API LOG", "Start adding view");

        View evtView = getLayoutInflater().inflate(R.layout.row_add, null, false);
        EditText tfEvtElem = evtView.findViewById(R.id.title_elem);
        ImageView imgSearch = evtView.findViewById(R.id.img_search);
        ImageView imgRemove = evtView.findViewById(R.id.img_remove);

        evtView.setTag(genEvtList.size());
        tfEvtElem.setHint("Event");

        imgRemove.setOnClickListener(v -> removeView(evtView));
        imgSearch.setOnClickListener(v -> searchEvent(evtView, tfEvtElem, imgSearch));

        layoutList.addView(evtView);
        Log.d("API LOG", "View added to layoutList");
    }

    private void removeView(View v) {
        int position = (int) v.getTag();
        if (position < genEvtList.size()) {
            genEvtList.remove(position);
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

    private void searchEvent(View v, EditText tfEvtElem, ImageView imgSearch) {
        //EditText tfEvtElem = v.findViewById(R.id.title_elem);
        //ImageView imgSearch = v.findViewById(R.id.img_search);
        String evtString = tfEvtElem.getText().toString();

        String promptText = buildPromptText(evtString);
        GeminiViewModel gemViewModel = new ViewModelProvider(requireActivity()).get(GeminiViewModel.class);

        gemViewModel.clearResult();

        Observer<GeminiViewModel.LoadState> loadStateObserver = getLoadStateObserver(gemViewModel);
        Observer<String> resultObserver = getResultObserver(tfEvtElem, imgSearch, gemViewModel);

        gemViewModel.getLoadState().observe(getViewLifecycleOwner(), loadStateObserver);
        gemViewModel.getResult().observe(getViewLifecycleOwner(), resultObserver);

        Log.d("API LOG", "Starting fetchGeminiResponse");
        gemViewModel.fetchGeminiResponse(promptText);
    }

    private String buildPromptText(String evtString) {
        return "Du bist Veranstaltungsmanager, deine Aufgabe ist es zukuenftige Events auf Basis einer Suchanfrage auszugeben " +
                "[Feste (Festivals, Jahrmaerkte, Strassenfeste, etc.), Buehnenkunst (Konzerte, Theaterauffuehrungen, Opern, Musicals, Kabarett, Comedy, Lesungen, Zirkus, etc.), " +
                "Ausstellungen, Messen, Maerkte, Sportveranstaltungen, Ausflugsfahrten, Stadtrundgaenge, Kundgebungen, etc.].\n" +
                "Ich gebe dir eine Suchanfrage und du gibst mir passende Events dazu aus.\n" +
                "Hier ist eine Aufzaehlung an Bedingungen, die du dabei beachten sollst:\n" +
                "1. Beachte Events auf der ganzen Welt, aber gib Events in Deutschland und Umland eine hoehere Prioritaet.\n" +
                "2. Die Events MUESSEN in der Zukunft liegen. Such zusaetzlich zu dem Event Veranstaltungsort und -Zeitraum raus. " +
                "Aktuelles Datum, ab dem die Events stattfinden duerfen: " + getCurrentDate() + "\n" +
                "3. Falls nur vergangene Events gefunden werden, such weiter ob die gleichen oder aehnliche Events z.B. naechstes Jahr erneut stattfinden. Gib diese stattdessen aus!\n" +
                "4.a) Gib 1 Event aus, wenn die Anfrage eindeutig ist.\n" +
                "4.b) Gib bis zu 5 Events aus, wenn auch andere Events mit der Anfrage gemeint sein koennten.\n" +
                "5. Sortiere die Treffer nach der Reihenfolge der Prioritaet, Groesse des Events, und Genauigkeit der Suchanfrage.\n" +
                "6. Mache die Ausgaben nach diesem Schema:\n" +
                "a) Fuer 1 Ausgabe: 'Event-Titel / Ort / Datum'\n" +
                "b) Fuer mehrere Ausgaben: 'Event-Titel1 / Ort1 / Datum1; Event-Titel2 / Ort2 / Datum2; etc.'\n" +
                "7. Datum-Ausgabe in dem Format: a) 'DD.MM.YYYY' bzw. b) 'DD.MM.YYYY-DD.MM.YYYY'.\n" +
                "8. Falls gar nichts gefunden wird, antworte mit 'keine Ergebnisse'.\n" +
                "9. Frag nicht nach mehr Kontext!\n" +
                "Die Suchanfrage fuer die Ausgabe lautet: '" + evtString + "'";
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

    private Observer<String> getResultObserver(EditText tfEvtElem, ImageView imgSearch, GeminiViewModel gemViewModel) {
        return new Observer<String>() {
            @Override
            public void onChanged(String resultString) {
                if (resultString != null && layoutList.getChildCount() > 0) {
                    Log.d("API LOG", "Daten zur Anzeige bereit");
                    String[] options = resultString.split("; ");
                    showChoiceDialog(options, result -> {
                        Log.d("API LOG", "TF Result: " + result);
                        if (!result.equals("")) {
                            tfEvtElem.setText(result);
                            tfEvtElem.setFocusable(false);

                            imgSearch.setEnabled(false);
                            imgSearch.setAlpha(0.5f);
                        }
                        gemViewModel.getResult().removeObserver(this);
                    });
                }
            }
        };
    }

    private void showChoiceDialog(String[] options, ResultHandler resultHandler) {
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
                if (evtPlaceDate.length == 3) {
                    Log.d("API LOG", "Event: " + evtPlaceDate[0]);
                    Log.d("API LOG", "Ort: " + evtPlaceDate[1]);
                    Log.d("API LOG", "Datum: " + evtPlaceDate[2]);

                    for (int i = 0; i < evtPlaceDate.length; i++) {
                        evtPlaceDate[i] = " " + evtPlaceDate[i].trim() + " ";
                    }

                    resultHandler.handle(evtPlaceDate[0]);
                    genEvtList.add(evtPlaceDate);

                } else {
                    Log.e("API LOG", "Event: " + selectedResult);
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
