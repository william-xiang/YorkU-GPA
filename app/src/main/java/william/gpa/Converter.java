package william.gpa;

import android.Manifest;
import android.text.SpannableString;
import android.text.style.RelativeSizeSpan;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class Converter {
    SpannableString result;
    static List<List<String>> selected;

    public Converter() {
        selected = SecondActivity.selected;
        convert();
    }

    public void convert() {
        Map<String, Integer> conversionChart9 = new HashMap<>();
        Map<String, Double> conversionChart4 = new HashMap<>();

        conversionChart9.put("A+", 9);
        conversionChart9.put("A", 8);
        conversionChart9.put("B+", 7);
        conversionChart9.put("B", 6);
        conversionChart9.put("C+", 5);
        conversionChart9.put("C", 4);
        conversionChart9.put("D+", 3);
        conversionChart9.put("D", 2);
        conversionChart9.put("E", 1);
        conversionChart9.put("F", 0);
        conversionChart9.put("NC", 0);

        conversionChart4.put("A+", 4.0);
        conversionChart4.put("A", 3.8);
        conversionChart4.put("B+", 3.3);
        conversionChart4.put("B", 3.0);
        conversionChart4.put("C+", 2.3);
        conversionChart4.put("C", 2.0);
        conversionChart4.put("D+", 1.3);
        conversionChart4.put("D", 1.0);
        conversionChart4.put("E", 0.0);
        conversionChart4.put("F", 0.0);
        conversionChart4.put("NC", 0.0);

        double credit = 0;
        double credits = 0;
        double pointes9 = 0;
        double pointes4 = 0;

        for (List<String> selectedElement : selected) {
            String mark = selectedElement.get(3);
            credit = Double.parseDouble(selectedElement.get(2));
            credits += credit;
            pointes9 += conversionChart9.get(mark) * credit;
            pointes4 += conversionChart4.get(mark) * credit;
        }

        result = new SpannableString(String.format("%.2f", pointes9 / credits) + " (9.0 Scale)\n" + String.format("%.2f", pointes4 / credits) + " (4.0 Scale)");
        result.setSpan(new RelativeSizeSpan(1f), 0, result.length(), 0); // set size
    }

    public SpannableString getResult() {
        return this.result;
    }
}
